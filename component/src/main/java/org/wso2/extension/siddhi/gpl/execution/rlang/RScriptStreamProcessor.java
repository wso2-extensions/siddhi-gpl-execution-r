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

import org.wso2.siddhi.annotation.Example;
import org.wso2.siddhi.annotation.Extension;
import org.wso2.siddhi.annotation.Parameter;
import org.wso2.siddhi.annotation.ReturnAttribute;
import org.wso2.siddhi.annotation.util.DataType;
import org.wso2.siddhi.core.config.SiddhiAppContext;
import org.wso2.siddhi.core.exception.SiddhiAppCreationException;
import org.wso2.siddhi.core.executor.ConstantExpressionExecutor;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.executor.VariableExpressionExecutor;
import org.wso2.siddhi.core.util.config.ConfigReader;
import org.wso2.siddhi.query.api.definition.AbstractDefinition;
import org.wso2.siddhi.query.api.definition.Attribute;

import java.util.List;
import java.util.Map;

/**
 * This class runs R script for each event and produces aggregated outputs based on the provided input variable
 * parameters and expected output attributes.
 */
@Extension(
        name = "eval",
        namespace = "r",
        description = "R script Stream processor. This extension runs the R script loaded from a file to each event "
                + "and produces aggregated outputs based on the provided input variable parameters and expected "
                + "output attributes.",
        parameters = {
                @Parameter(name = "script",
                           description = "R script as a string produces aggregated outputs based on the provided input "
                                   + "variable parameters and expected output attributes",
                           type = {DataType.STRING}),
                @Parameter(name = "output.attributes",
                           description = "A set of output attributes separated by commas as string. Each attribute is "
                                   + "denoted as <name><space><type>. e.g., 'output1 string, output2 long'",
                           type = {DataType.INT, DataType.LONG, DataType.FLOAT, DataType.DOUBLE, DataType.STRING,
                                   DataType.STRING}),
                @Parameter(name = "input.attributes",
                           description = "A set of input attributes separated by commas after output attributes. "
                                   + "e.g., 'att1, att2'",
                           type = {DataType.INT, DataType.LONG, DataType.FLOAT, DataType.DOUBLE, DataType.STRING,
                                   DataType.STRING})
        },
        returnAttributes = @ReturnAttribute(
                name = "outputParameters",
                description = "This runs the R script for each event and produces  aggregated outputs based on the "
                        + "provided input variable parameters and expected output attributes.",
                type = {DataType.INT, DataType.LONG, DataType.FLOAT, DataType.DOUBLE, DataType.STRING,
                        DataType.STRING}),
        examples = @Example(
                description = "TBD",
                syntax = "@info(name = 'query1')\n"
                        + "from weather#window.lengthBatch(2)#r:eval("
                        + "\"c <- sum(time); m <- sum(temp); \", \"c long, m double\", time, temp) \n"
                        + "select * \n"
                        + "insert into dataOut;")
)
public class RScriptStreamProcessor extends RStreamProcessor {

    @Override
    protected List<Attribute> init(AbstractDefinition abstractDefinition, ExpressionExecutor[] expressionExecutors,
                                   ConfigReader configReader, SiddhiAppContext siddhiAppContext) {
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
        return initialize(scriptString, outputString);
    }

    @Override public Map<String, Object> currentState() {
        return null;
    }

    @Override public void restoreState(Map<String, Object> map) {

    }
}
