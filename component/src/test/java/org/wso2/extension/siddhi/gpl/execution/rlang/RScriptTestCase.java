/*
 * Copyright (c)  2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.log4j.Logger;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;

import static org.junit.Assume.assumeTrue;

public class RScriptTestCase {

    static final Logger LOG = Logger.getLogger(RScriptTestCase.class);

    private static SiddhiManager siddhiManager;
    private int count;
    private double doubleValue;
    private long longValue;
    private int intValue;
    private boolean boolValue;

    @BeforeMethod
    public void init() {
        count = 0;
        siddhiManager = new SiddhiManager();
    }

    @Test
    public void testRScript1() throws InterruptedException {
        LOG.info("r:eval test1");
        assumeTrue(System.getenv("JRI_HOME") != null);

        String defineStream = "@config(async = 'true') define stream weather (time long, temp double); ";

        String executionPlan = defineStream + " @info(name = 'query1') from weather#window.lengthBatch(2)" +
                "#r:eval(\"c <- sum(time); m <- sum(temp); \", \"c long, m double\"," +
                " time, temp)" +
                " select *" +
                " insert into dataOut;";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(executionPlan);
        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {

                    for (Event event : inEvents) {
                        longValue = (Long) event.getData(2);
                        doubleValue = (Double) event.getData(3);
                    }
                    count++;
                }
            }
        });
        siddhiAppRuntime.start();
        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("weather");
        inputHandler.send(new Object[]{10L, 55.6d});
        inputHandler.send(new Object[]{20L, 65.6d});
        inputHandler.send(new Object[]{30L, 75.6d});
        Thread.sleep(1000);
        AssertJUnit.assertEquals("Only one event must arrive", 1, count);
        AssertJUnit.assertEquals("Value 1 returned", 10 + 20, longValue);
        AssertJUnit.assertEquals("Value 2 returned", (55.6 + 65.6), doubleValue, 1e-4);
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void testRScript2() throws InterruptedException {
        LOG.info("r:eval test2");
        assumeTrue(System.getenv("JRI_HOME") != null);
        String defineStream = "@config(async = 'true') define stream weather (time int, temp double); ";

        String executionPlan = defineStream + " @info(name = 'query1') from weather#window.timeBatch(2 sec)" +
                "#r:eval('c <- sum(time); m <- sum(temp); ', 'c int, m double', time, temp)" +
                " select *" +
                " insert into dataOut;";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(executionPlan);

        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {

                    for (Event event : inEvents) {
                        intValue = (Integer) event.getData(2);
                        doubleValue = (Double) event.getData(3);
                    }
                    count++;
                }
            }
        });

        siddhiAppRuntime.start();
        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("weather");
        inputHandler.send(new Object[]{10, 55.6});
        inputHandler.send(new Object[]{20, 65.6});
        Thread.sleep(2500);
        inputHandler.send(new Object[]{30, 75.6});
        Thread.sleep(1000);
        AssertJUnit.assertEquals("Only one event must arrive", 1, count);
        AssertJUnit.assertEquals("Value 1 returned", 30, intValue);
        AssertJUnit.assertEquals("Value 2 returned", (55.6 + 65.6), doubleValue, 1e-4);
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void testRScript3() throws InterruptedException {
        LOG.info("r:eval test3");
        assumeTrue(System.getenv("JRI_HOME") != null);

        String defineStream = "@config(async = 'true') define stream weather (time int, temp bool); ";

        String executionPlan = defineStream + " @info(name = 'query1') from weather#window.lengthBatch(3)" +
                "#r:eval(\"c <- sum(time); m <- any(temp); \", \"c double, m bool\"," +
                " time, temp)" +
                " select *" +
                " insert into dataOut;";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(executionPlan);
        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {

                    for (Event event : inEvents) {
                        doubleValue = (Double) event.getData(2);
                        boolValue = (Boolean) event.getData(3);
                    }
                    count++;
                }
            }
        });

        siddhiAppRuntime.start();
        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("weather");
        inputHandler.send(new Object[]{10, true});
        inputHandler.send(new Object[]{20, false});
        inputHandler.send(new Object[]{30, true});
        Thread.sleep(1000);
        inputHandler.send(new Object[]{40, false});

        AssertJUnit.assertEquals("Only one event must arrive", 1, count);
        AssertJUnit.assertEquals("Value 1 returned", (10 + 20 + 30) + 0.0, doubleValue, 1e-4);
        AssertJUnit.assertEquals("Value 2 returned", true, boolValue);
        siddhiAppRuntime.shutdown();
    }

}
