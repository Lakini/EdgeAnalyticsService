/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.edgeanalyticsservice;

import android.util.Log;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.core.util.EventPrinter;
import java.util.List;

/**
 * This class create from the build in sensors and inject
 * to the EdgeAnalytics Service for processing on a query passed.
 */
public class CEP {

    private static CEP instance = null;
    private SiddhiManager mSiddhiManager;
    private IEdgeAnalyticServiceCallback mCallback = null;

    //to use a single siddhi manager instance
    public static CEP getInstance() {
        if(instance == null) {
            instance = new CEP();
        }
        return instance;
    }

    /** Initialize the SiddhiManager Instance and hard code the streams for type 2 clients*/
    public CEP() {

        mSiddhiManager = new SiddhiManager();

        //This is for the location service
        mSiddhiManager.defineStream("define stream LocationStream (latitude double,longitude double); ");

        //This is for the humidity Service
        mSiddhiManager.defineStream("define stream HumidityStream (humidity float); ");

        //This is for  temperature service
        mSiddhiManager.defineStream("define stream TemperatureStream (tempValue float); ");

        //This is for light Intensity service
        mSiddhiManager.defineStream("define stream LightIntensityStream (lightValue double); ");
    }

    /**
     * Analyse the data according to the queries
     * and send the notification back to client through callback
     * @param value:String
     * @param stream:String
     */
    public void analyseTheData(String value, String stream) {

        String[] dataCollection=value.split(",");
        String[] value_type=null;
        Object[] x =new Object[dataCollection.length];

        /** Identify the data types which the client sends. */
        for(int i=0;i<dataCollection.length ;i++)
        {
            value_type=dataCollection[i].split("-");
            x[i]=0;
            switch (value_type[1]) {
                case "double":
                    x[i]=Double.parseDouble(value_type[0]);
                    break;
                case "float":
                    x[i]=Float.parseFloat(value_type[0]);
                    break;
                case "int":
                    x[i]=Integer.parseInt(value_type[0]);
                    break;
                case "string":
                    x[i]=value_type[0];
                    break;
                default:
                    break;
               }
        }

        InputHandler inputHandler = mSiddhiManager.getInputHandler(stream);
           try
            {
               Log.d("Service","got values!!"+x[0]);
               inputHandler.send(x);
            }
           catch (InterruptedException e) {
               e.printStackTrace();
            }
    }

    /**
     * Add  details to the Siddhi manager object
     * @param streamDefinition:String
     * @param query:List<String>
     * @param callbackFunction:List<String>
     * @param edgeCallback:IEdgeAnalyticServiceCallback
     */
    public void cepAddDetails(String streamDefinition, final List<String> query,final List<String> callbackFunction,IEdgeAnalyticServiceCallback edgeCallback) {

        mCallback = edgeCallback;

        if (streamDefinition !=null) {
            /** Define the stream to the Siddhi Manager  */
            mSiddhiManager.defineStream(streamDefinition);
        }

        /**
         * get the  queries and add them to the Siddhi Manager
         */
        for (int i = 0; i < query.size(); i++) {
            mSiddhiManager.addQuery(query.get(i));
        }

        /**
         * get the callbacks and add them to the Siddhi Manager
        */
        for(int j=0;j<callbackFunction.size();j++) {
            final int mFinalJ = j;

            mSiddhiManager.addCallback(callbackFunction.get(j), new StreamCallback() {
                public void receive(Event[] events) {
                    EventPrinter.print(events);
                    try {
                        mCallback.addCallBack("callback"+callbackFunction.get(mFinalJ));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
