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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.List;

/**
 * This class implements IEdgeAnalyticService.aidl interface
 * and this use to make connection between the client and the Edge Analytics Service
 */
public class EdgeAnalyticsService extends Service {

    private CEP mCep;
    private TaskManager mTaskManager=null;


    /**
     * get one instance of the Siddhi manager by calling CEP class
     */
    public EdgeAnalyticsService() {
        mCep = CEP.getInstance();
    }

    @Override
    public void onCreate() {
    }

    /** Returns the IBinder object for the new connection
     *@param  intent Intent
     *@return  IBinder
     */
    @Override
    public IBinder onBind(Intent intent) {
        mTaskManager=null;
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    /** unbind the client form the client list of the Service
     * this returns true if you would like to have the service's onRebind(Intent) method later called when new clients bind to it.
     *@param  intent Intent
     */
    @Override
    public boolean onUnbind (Intent intent){
        return false;
    }

    /**
     *When destroying the activity stop the service by it self.
     */
    @Override
    public void onDestroy() {
        stopSelf();
    }

    /**
     * Implementation of methods in the IEdgeAnalyticsService interface
     */
    public final IEdgeAnalyticsService.Stub mBinder = new IEdgeAnalyticsService.Stub() {

        IEdgeAnalyticServiceCallback mIEdgeAnalyticServiceCallback=null;
        Context mContext=getApplicationContext();
        String mStream;

        @Override
        public void getService(String streamDefinition,String stream, List<String> query, List<String> callbackFunction,IEdgeAnalyticServiceCallback edgeCallback){
            mIEdgeAnalyticServiceCallback = edgeCallback;

            switch(stream)
            {
                case "1":
                    mStream="LOCATION_SERVICE";
                    break;
                case "2":
                    mStream="HUMIDITY_SERVICE";
                    break;
                case "3":
                    mStream="TEMPERATURE_SERVICE";
                    break;
                case "4":
                    mStream="INTENSITY_SERVICE";
                    break;
                default:
            }

            /** Add the details to CEP if the Client is Type1 */
            if (streamDefinition!=null) {
                mCep.cepAddDetails(streamDefinition, query, callbackFunction, edgeCallback);
            }

            else
            {
                TaskManager mTaskManager=new TaskManager(mContext);

                /** Add the details to CEP if the Client is Type2-LOCATION_SERVICE */
                if (mStream.equalsIgnoreCase("LOCATION_SERVICE")) {
                    mCep.cepAddDetails(null, query,callbackFunction, edgeCallback);
                    mTaskManager.getData("LOCATION_SERVICE");
                 }

                /** Add the details to CEP if the Client is Type2-HUMIDITY_SERVICE */
                else if (mStream.equalsIgnoreCase("HUMIDITY_SERVICE")) {
                    mCep.cepAddDetails(null, query,callbackFunction, edgeCallback);
                    mTaskManager.getData("HUMIDITY_SERVICE");
                }

                /** Add the details to CEP if the Client is Type2-TEMPERATURE_SERVICE */
                else if (mStream.equalsIgnoreCase("TEMPERATURE_SERVICE")) {
                    mCep.cepAddDetails(null, query,callbackFunction, edgeCallback);
                    mTaskManager.getData("TEMPERATURE_SERVICE");
                }

                /** Add the details to CEP if the Client is Type2-INTENSITY_SERVICE*/
                else if (mStream.equalsIgnoreCase("INTENSITY_SERVICE")) {
                    mCep.cepAddDetails(null, query,callbackFunction, edgeCallback);
                    mTaskManager.getData("INTENSITY_SERVICE");
                }
            }
        }

        /**
         *Passes values to the streams to do the analytics based on the queries
         * @param value:String
         * @param stream:String
         */
        @Override
        public void sendData(String value, String stream){
            mCep.analyseTheData(value, stream);
        }

        /**
         *stop the device in build sensors
         */
        @Override
        public void stopService() {
           mTaskManager.stopSensors();
        }
    };
}
