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
import org.wso2.siddhi.core.util.SiddhiTestHelper;

import java.util.concurrent.atomic.AtomicInteger;

public class RSourceTestCase {

    static final Logger LOG = Logger.getLogger(RSourceTestCase.class);

    private static SiddhiManager siddhiManager = new SiddhiManager();
    private AtomicInteger count = new AtomicInteger();
    private double value1;
    protected double value2;
    private boolean valueBool;
    private String valueString;
    private float valueFloat;
    private long valueLong;

    @BeforeMethod
    public void init() {
        count.set(0);
    }

    // get double values to the output stream
    @Test
    public void testRSource1() throws InterruptedException {
        LOG.info("r:evalSource test1");
        if (System.getenv("JRI_HOME") != null) {
            String defineStream = "@config(async = 'true') define stream weather (time long, temp double); ";

            String executionPlan = defineStream + " @info(name = 'query1') from weather#window.timeBatch(2 sec)" +
                    "#r:evalSource(\"src/test/resources/sample.R\", \"m double, c long\"," +
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
                            value1 = (Double) event.getData(2);
                            valueLong = (Long) event.getData(3);
                        }
                        count.incrementAndGet();
                    }
                }
            });

            siddhiAppRuntime.start();
            InputHandler inputHandler = siddhiAppRuntime.getInputHandler("weather");
            inputHandler.send(new Object[]{10L, 55.6d});
            inputHandler.send(new Object[]{20L, 65.6d});
            Thread.sleep(5000);
            inputHandler.send(new Object[]{30L, 75.6d});
            Thread.sleep(500);
            AssertJUnit.assertEquals("Only one event must arrive", 1, count.get());
            AssertJUnit.assertEquals("Value 1 returned", 121.2, value1, 1e-4);
            AssertJUnit.assertEquals("Value 2 returned", 30L, valueLong, 1e-4);
            siddhiAppRuntime.shutdown();
        }
    }

    // get integer, float values to the output stream
    @Test
    public void testRSource2() throws InterruptedException {
        LOG.info("r:evalSource test2");
        if (System.getenv("JRI_HOME") != null) {
            String defineStream = "@config(async = 'true') define stream weather (time long, temp double); ";

            String executionPlan = defineStream + " @info(name = 'query1') from weather#window.lengthBatch(2)" +
                    "#r:evalSource(\"src/test/resources/sample2.R\", \"m int, c float\", time, temp)" +
                    " select *" +
                    " insert into dataOut;";
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(executionPlan);

            siddhiAppRuntime.addCallback("query1", new QueryCallback() {
                @Override
                public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                    EventPrinter.print(timeStamp, inEvents, removeEvents);
                    if (inEvents != null) {

                        for (Event event : inEvents) {
                            value1 = (Integer) event.getData(2);
                            valueFloat = (Float) event.getData(3);
                        }
                        count.incrementAndGet();
                    }
                }
            });

            siddhiAppRuntime.start();
            InputHandler inputHandler = siddhiAppRuntime.getInputHandler("weather");
            inputHandler.send(new Object[]{10L, 55.6d});
            inputHandler.send(new Object[]{20L, 65.6d});
            inputHandler.send(new Object[]{30L, 75.6d});
            SiddhiTestHelper.waitForEvents(100, 1, count, 5000);
            AssertJUnit.assertEquals("Only one event must arrive", 1, count.get());
            AssertJUnit.assertEquals("Value 1 returned", 121, value1, 1e-4);
            AssertJUnit.assertEquals("Value 2 returned", 30f, valueFloat, 1e-4);
            siddhiAppRuntime.shutdown();
        }
    }

    // get string, bool to the output stream
    @Test
    public void testRSource3() throws InterruptedException {
        LOG.info("r:evalSource test3");
        if (System.getenv("JRI_HOME") != null) {
            String defineStream = "@config(async = 'true') define stream weather (time long, temp double); ";

            String executionPlan = defineStream + " @info(name = 'query1') from weather#window.lengthBatch(2)" +
                    "#r:evalSource(\"src/test/resources/sample3.R\", \"c string, m bool\"," +
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
                            valueString = (String) event.getData(2);
                            valueBool = (Boolean) event.getData(3);
                        }
                        count.incrementAndGet();
                    }
                }
            });

            siddhiAppRuntime.start();
            InputHandler inputHandler = siddhiAppRuntime.getInputHandler("weather");
            inputHandler.send(new Object[]{123L, 55.6d});
            inputHandler.send(new Object[]{101L, 72.3d});
            SiddhiTestHelper.waitForEvents(100, 1, count, 5000);
            AssertJUnit.assertEquals("Only one event must arrive", 1, count.get());
            AssertJUnit.assertEquals("Value 1 returned", "178.6", valueString);
            AssertJUnit.assertEquals("Value 2 returned", true, valueBool);
            siddhiAppRuntime.shutdown();
        }
    }

}
