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

import io.siddhi.annotation.Example;
import io.siddhi.annotation.Extension;
import io.siddhi.annotation.Parameter;
import io.siddhi.annotation.ReturnAttribute;
import io.siddhi.annotation.util.DataType;
import io.siddhi.core.config.SiddhiQueryContext;
import io.siddhi.core.event.stream.MetaStreamEvent;
import io.siddhi.core.event.stream.holder.StreamEventClonerHolder;
import io.siddhi.core.exception.SiddhiAppCreationException;
import io.siddhi.core.executor.ConstantExpressionExecutor;
import io.siddhi.core.executor.ExpressionExecutor;
import io.siddhi.core.executor.VariableExpressionExecutor;
import io.siddhi.core.query.processor.ProcessingMode;
import io.siddhi.core.util.config.ConfigReader;
import io.siddhi.core.util.snapshot.state.State;
import io.siddhi.core.util.snapshot.state.StateFactory;
import io.siddhi.query.api.definition.AbstractDefinition;
import io.siddhi.query.api.definition.Attribute;

import java.util.List;

/**
 * This class runs R script for each event and produces aggregated outputs based on the provided input variable
 * parameters and expected output attributes.
 */
@Extension(
        name = "eval",
        namespace = "r",
        description = "The R Script Stream Processor runs the R script defined within the Siddhi application " +
                "to each event and produces aggregated outputs based on the input variable parameters provided and " +
                "the expected output attributes.",
        parameters = {
                @Parameter(name = "script",
                           description = "The R script as a string which  produces aggregated outputs based on the " +
                                   "provided input variable parameters and the expected output attributes",
                           type = {DataType.STRING}),
                @Parameter(name = "output.attributes",
                           description = "The  expected set of output attributes. These can be provided as a " +
                                   "comma-separated list. Each attribute is denoted as '<name><space><type>'." +
                                   " e.g., 'output1 string, output2 long'.",
                           type = {DataType.INT, DataType.LONG, DataType.FLOAT, DataType.DOUBLE, DataType.STRING,
                                   DataType.STRING}),
                @Parameter(name = "input.attributes",
                           description = "A set of input attributes to be considered when generating the expected " +
                                   "output. This can be provided as a comma-separated list after output attributes. "
                                   + "e.g., 'att1, att2'.",
                           type = {DataType.INT, DataType.LONG, DataType.FLOAT, DataType.DOUBLE, DataType.STRING,
                                   DataType.STRING})
        },
        returnAttributes = @ReturnAttribute(
                name = "outputParameters",
                description = "The output parameters returned once the R script is run for each event.",
                type = {DataType.INT, DataType.LONG, DataType.FLOAT, DataType.DOUBLE, DataType.STRING,
                        DataType.STRING}),
        examples = @Example(syntax = "@info(name = 'query1')\n"
                        + "from weather#window.lengthBatch(2)#r:eval("
                        + "\"c <- sum(time); m <- sum(temp); \", \"c long, m double\", time, temp) \n"
                        + "select * \n"
                        + "insert into dataOut;",
                description = "This query runs the R script 'c <- sum(time); m <- sum(temp);' for every two events in" +
                        " a tumbling manner. Values are derived for two output parameters named 'c' and 'm' by " +
                        "considering the values of two other parameters named 'time' and 'temp' as the input. ")
)
public class RScriptStreamProcessor extends RStreamProcessor<State> {

    private List<Attribute> attributes;

    @Override
    protected StateFactory<State> init(MetaStreamEvent metaStreamEvent,
                                       AbstractDefinition abstractDefinition,
                                       ExpressionExecutor[] attributeExpressionExecutors,
                                       ConfigReader configReader,
                                       StreamEventClonerHolder streamEventClonerHolder,
                                       boolean b,
                                       boolean b1,
                                       SiddhiQueryContext siddhiQueryContext) {
        if (attributeExpressionExecutors.length < 2) {
            throw new SiddhiAppCreationException("Wrong number of attributes given. Expected 2 or more, found " +
                    attributeExpressionLength + "\n" +
                    "Usage: #R:eval(script:string, outputVariables:string, "
                    + "input1, ...)");
        }
        String scriptString;
        String outputString;

        try {
            if (!(attributeExpressionExecutors[0] instanceof ConstantExpressionExecutor)) {
                throw new SiddhiAppCreationException("First parameter should be a constant");
            }
            scriptString = (String) attributeExpressionExecutors[0].execute(null);
        } catch (ClassCastException e) {
            throw new SiddhiAppCreationException("First parameter should be of type string. Found " +
                    attributeExpressionExecutors[0].execute(null).getClass()
                            .getCanonicalName() + "\n" +
                    "Usage: #R:eval(script:string, outputVariables:string, "
                    + "input1, ...)");
        }
        try {
            if (!(attributeExpressionExecutors[1] instanceof ConstantExpressionExecutor)) {
                throw new SiddhiAppCreationException("Second parameter should be a constant");
            }
            outputString = (String) attributeExpressionExecutors[1].execute(null);
        } catch (ClassCastException e) {
            throw new SiddhiAppCreationException("Second parameter should be of type string. Found " +
                    attributeExpressionExecutors[1].execute(null).getClass()
                            .getCanonicalName() + "\n" +
                    "Usage: #R:eval(script:string, outputVariables:string, "
                    + "input1, ...)");
        }

        for (int i = 2; i < attributeExpressionLength; i++) {
            if (attributeExpressionExecutors[i] instanceof VariableExpressionExecutor) {
                inputAttributes.add(((VariableExpressionExecutor) attributeExpressionExecutors[i]).getAttribute());
            } else {
                throw new SiddhiAppCreationException("Parameter " + (i + 1) + " should be a variable");
            }
        }
        attributes = initialize(scriptString, outputString);
        return null;
    }

    @Override
    public List<Attribute> getReturnAttributes() {
        return attributes;
    }

    @Override
    public ProcessingMode getProcessingMode() {
        return ProcessingMode.BATCH;
    }
}
