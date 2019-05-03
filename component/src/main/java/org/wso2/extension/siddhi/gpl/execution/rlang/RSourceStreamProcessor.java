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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * This class runs the R script loaded from a file to each event and produces aggregated outputs based on the provided
 * input variable parameters and expected output attributes.
 */
@Extension(
        name = "evalSource",
        namespace = "r",
        description = "The R source Stream processor runs the R script loaded from a file for each event "
                + "and produces aggregated outputs based on the input variable parameters provided and the expected "
                + "output attributes.",
        parameters = {
                @Parameter(name = "file.path",
                           description = "The file path of the R script where this script is located uses the input " +
                                   "variable parameters and produces the expected output attributes.",
                           type = {DataType.STRING}),
                @Parameter(name = "output.attributes",
                           description = "The expected output attributes. This can be provided and a string of " +
                                   "comma-separated attribute names. Each attribute is denoted as " +
                                   "<name><space><type>. e.g., 'output1 string, output2 long'.",
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
        examples = @Example(
                syntax = "@info(name = 'query1')\n"
                        + "from weather#window.lengthBatch(2)#r:evalSource(\"src/test/resources/sample2.R\", \"m int, "
                        + "c float\", time, temp)\n"
                        + "select *\n"
                        + "insert into dataOut;",
                description = "This 'r' source function takes in a 'r' script file location and computes the output as "
                + "defined in the file."
        )
)
public class RSourceStreamProcessor extends RStreamProcessor<State> {

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
                    attributeExpressionLength
                    + "Usage: #R:evalSource(filePath:string, "
                    + "outputVariables:string, input1, ...)");
        }
        String scriptString;
        String filePath;
        String outputString;

        try {
            if (!(attributeExpressionExecutors[0] instanceof ConstantExpressionExecutor)) {
                throw new SiddhiAppCreationException("First parameter should be a constant");
            }
            filePath = (String) attributeExpressionExecutors[0].execute(null);
        } catch (ClassCastException e) {
            throw new SiddhiAppCreationException("First parameter should be of type string. Found " +
                    attributeExpressionExecutors[0].execute(null).getClass()
                            .getCanonicalName() + "\n" +
                    "Usage: #R:evalSource(filePath:string, "
                    + "outputVariables:string, input1, ...)");
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
                    "Usage: #R:evalSource(filePath:string, "
                    + "outputVariables:string, input1, ...)");
        }

        for (int i = 2; i < attributeExpressionLength; i++) {
            if (attributeExpressionExecutors[i] instanceof VariableExpressionExecutor) {
                inputAttributes.add(((VariableExpressionExecutor) attributeExpressionExecutors[i]).getAttribute());
            } else {
                throw new SiddhiAppCreationException("Parameter " + (i + 1) + " should be a variable");
            }
        }
        try {
            scriptString = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            throw new SiddhiAppCreationException("Error while reading R source file", e);
        } catch (SecurityException e) {
            throw new SiddhiAppCreationException("Access denied while reading R source file", e);
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
