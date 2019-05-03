/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.extension.siddhi.gpl.execution.rlang;

import io.siddhi.core.event.ComplexEvent;
import io.siddhi.core.event.ComplexEventChunk;
import io.siddhi.core.event.stream.StreamEvent;
import io.siddhi.core.event.stream.StreamEventCloner;
import io.siddhi.core.event.stream.populater.ComplexEventPopulater;
import io.siddhi.core.exception.SiddhiAppCreationException;
import io.siddhi.core.exception.SiddhiAppRuntimeException;
import io.siddhi.core.executor.ExpressionExecutor;
import io.siddhi.core.query.processor.Processor;
import io.siddhi.core.query.processor.stream.StreamProcessor;
import io.siddhi.core.util.snapshot.state.State;
import io.siddhi.query.api.definition.Attribute;
import io.siddhi.query.api.definition.StreamDefinition;
import io.siddhi.query.compiler.SiddhiCompiler;
import io.siddhi.query.compiler.exception.SiddhiParserException;
import org.rosuda.REngine.JRI.JRIEngine;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class which is extended by RScriptStreamProcessor and RSourceStreamProcessor
 *
 * @param <S> the State parameter to hold the states that outlive the class that created it.
 */
public abstract class RStreamProcessor<S extends State> extends StreamProcessor<S> {

    List<Attribute> inputAttributes = new ArrayList<>();

    REXP outputs;
    REXP script;
    REXP env;

    REngine re;

    @Override
    protected void process(ComplexEventChunk<StreamEvent> complexEventChunk,
                           Processor processor,
                           StreamEventCloner streamEventCloner,
                           ComplexEventPopulater complexEventPopulater,
                           S state) {
        StreamEvent streamEvent;
        StreamEvent lastCurrentEvent = null;
        List<StreamEvent> eventList = new ArrayList<>();
        while (complexEventChunk.hasNext()) {
            streamEvent = complexEventChunk.next();
            if (streamEvent.getType() == ComplexEvent.Type.CURRENT) {
                eventList.add(streamEvent);
                lastCurrentEvent = streamEvent;
                complexEventChunk.remove();
            }
        }
        if (!eventList.isEmpty()) {
            complexEventPopulater.populateComplexEvent(lastCurrentEvent, process(eventList));
            complexEventChunk.add(lastCurrentEvent);
        }
        nextProcessor.process(complexEventChunk);
    }

    private Object[] process(List<StreamEvent> eventList) throws SiddhiAppRuntimeException {
        try {
            REXP eventData;
            ExpressionExecutor expressionExecutor;
            for (int j = 2; j < attributeExpressionLength; j++) {
                expressionExecutor = attributeExpressionExecutors[j];
                switch (expressionExecutor.getReturnType()) {
                    case DOUBLE:
                        eventData = doubleToREXP(eventList, expressionExecutor);
                        break;
                    case FLOAT:
                        eventData = floatToREXP(eventList, expressionExecutor);
                        break;
                    case INT:
                        eventData = intToREXP(eventList, expressionExecutor);
                        break;
                    case STRING:
                        eventData = stringToREXP(eventList, expressionExecutor);
                        break;
                    case LONG:
                        eventData = longToREXP(eventList, expressionExecutor);
                        break;
                    case BOOL:
                        eventData = boolToREXP(eventList, expressionExecutor);
                        break;
                    default:
                        continue;
                }
                re.assign(inputAttributes.get(j - 2).getName(), eventData, env);
            }
            re.eval(script, env, false);
        } catch (REngineException e) {
            throw new SiddhiAppRuntimeException("Unable to evaluate the script", e);
        } catch (REXPMismatchException e) {
            throw new SiddhiAppRuntimeException("Mismatch in returned output and expected output", e);
        }

        try {
            RList out = re.eval(outputs, env, true).asList();
            REXP result;
            Object[] data = new Object[out.size()];
            for (int i = 0; i < out.size(); i++) {
                result = ((REXP) out.get(i));
                switch (getReturnAttributes().get(i).getType()) {
                    case BOOL:
                        if (result.isLogical()) {
                            data[i] = (result.asInteger() == 1);
                        }
                        break;
                    case INT:
                        if (result.isNumeric()) {
                            data[i] = result.asInteger();
                        }
                        break;
                    case LONG:
                        if (result.isNumeric()) {
                            data[i] = (long) result.asDouble();
                        }
                        break;
                    case FLOAT:
                        if (result.isNumeric()) {
                            data[i] = ((Double) result.asDouble()).floatValue();
                        }
                        break;
                    case DOUBLE:
                        if (result.isNumeric()) {
                            data[i] = result.asDouble();
                        }
                        break;
                    case STRING:
                        if (result.isString()) {
                            data[i] = result.asString();
                        }
                        break;
                    default:
                        throw new SiddhiAppRuntimeException(
                                "Mismatch in returned and expected output. Expected: " + getReturnAttributes().get(i)
                                        .getType() + " Returned: " + result.asNativeJavaObject().getClass()
                                        .getCanonicalName());
                }
            }
            return data;
        } catch (REXPMismatchException e) {
            throw new SiddhiAppRuntimeException("Mismatch in returned output and expected output", e);
        } catch (REngineException e) {
            throw new SiddhiAppRuntimeException("Unable to evaluate the script", e);
        }
    }

    protected List<Attribute> initialize(String scriptString, String outputString) {
        try {
            // Get the JRIEngine or create one
            re = JRIEngine.createEngine();
            // Create a new R environment
            env = re.newEnvironment(null, true);
        } catch (Exception e) {
            throw new SiddhiAppCreationException("Unable to create a new session in R", e);
        }
        StreamDefinition streamDefinition;
        try {
            streamDefinition =
                    SiddhiCompiler.parseStreamDefinition("define stream ROutputStream(" + outputString + ")");

        } catch (SiddhiParserException e) {
            throw new SiddhiAppCreationException(
                    "Could not parse the output variables string. Usage: \"a string, b int\"" +
                            ". Found: \"" + outputString + "\"", e);
        }

        List<Attribute> outputAttributes = streamDefinition.getAttributeList();

        StringBuilder sb = new StringBuilder("list(");
        for (int i = 0; i < outputAttributes.size(); i++) {
            sb.append(outputAttributes.get(i).getName());
            if (i != outputAttributes.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");

        try {
            // Parse the output list expression
            outputs = re.parse(sb.toString(), false);
            // Parse the script
            script = re.parse(scriptString, false);
        } catch (REngineException e) {
            throw new SiddhiAppCreationException("Unable to parse the script: " + scriptString, e);
        }
        return outputAttributes;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    private REXP doubleToREXP(List<StreamEvent> list, ExpressionExecutor expressionExecutor) {
        double[] arr = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = (Double) expressionExecutor.execute(list.get(i));
        }
        return new REXPDouble(arr);
    }

    private REXP floatToREXP(List<StreamEvent> list, ExpressionExecutor expressionExecutor) {
        double[] arr = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = (Float) expressionExecutor.execute(list.get(i));
        }
        return new REXPDouble(arr);
    }

    private REXP intToREXP(List<StreamEvent> list, ExpressionExecutor expressionExecutor) {
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = (Integer) expressionExecutor.execute(list.get(i));
        }
        return new REXPInteger(arr);
    }

    private REXP longToREXP(List<StreamEvent> list, ExpressionExecutor expressionExecutor) {
        double[] arr = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = (Long) expressionExecutor.execute(list.get(i));
        }
        return new REXPDouble(arr);
    }

    private REXP stringToREXP(List<StreamEvent> list, ExpressionExecutor expressionExecutor) {
        String[] arr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = (String) expressionExecutor.execute(list.get(i));
        }
        return new REXPString(arr);
    }

    private REXP boolToREXP(List<StreamEvent> list, ExpressionExecutor expressionExecutor) {
        boolean[] arr = new boolean[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = (Boolean) expressionExecutor.execute(list.get(i));
        }
        return new REXPLogical(arr);
    }
}
