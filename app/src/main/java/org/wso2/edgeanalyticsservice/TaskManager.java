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

import android.content.Context;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class take data from the Device sensors and Android Services when the type is Type 2
 */
public class TaskManager {

    private LightIntensitySensorService mLightIntensitySensorService;
    private LocationSystemService mLocationSystemService;
    private HumiditySensorService mHumiditySensorService;
    private TemperatureSensorService mTemperatureSensorService;
    private String mValue;
    private CEP mCep;
    private Context mContext;
    final Timer mTimer=new Timer();
    private  String mStream;

    /** Initialize the SiddhiManager Instance and hard code the streams for type 2 clients*/
    public TaskManager(Context context) {
        mContext=context;
        mCep=CEP.getInstance();
    }

    public void getData(String serviceType)
    {
        mStream=serviceType;

        if (serviceType.equalsIgnoreCase("LOCATION_SERVICE")) {
            getLocationService();
        }

        else if(serviceType.equalsIgnoreCase("HUMIDITY_SERVICE"))
        {
            getHumiditySensorDetails();
        }

        else if(serviceType.equalsIgnoreCase("TEMPERATURE_SERVICE"))
        {
            getTemperatureSensorDetails();
        }

        else if(serviceType.equalsIgnoreCase("INTENSITY_SERVICE"))
        {
            getLightIntensitySensorDetails();
        }
    }

    /** get the values from a System Service-Location system Service */
    public void getLocationService() {
        mLocationSystemService=LocationSystemService.getInstance(mContext);
        sendDataToService("LOCATION_SERVICE");
    }

    /** get the values from a Sensor Service-RELATIVE_HUMIDITY */
    public void getHumiditySensorDetails()
    {
      mHumiditySensorService=HumiditySensorService.getInstance(mContext);
      sendDataToService("HUMIDITY_SERVICE");
    }

    /** get the values from a Sensor Service-RELATIVE_TEMPERATURE */
    public void getTemperatureSensorDetails()
    {
        mTemperatureSensorService=TemperatureSensorService.getInstance(mContext);
        sendDataToService("TEMPERATURE_SERVICE");
    }

    /** get the values
     *  from a Sensor Service-TYPE_LIGHT */
    public void getLightIntensitySensorDetails()
    {
        mLightIntensitySensorService=LightIntensitySensorService.getInstance(mContext);
        sendDataToService("INTENSITY_SERVICE");
    }

    /** send data to the
     *  Stream by using the system services and sensor data */
    public void sendDataToService(final String service_type) {
        final double[] actualVal = {0};

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (service_type.equalsIgnoreCase("LOCATION_SERVICE")) {
                    actualVal[0] =mLocationSystemService.getServicedata()[0];
                    mValue = String.valueOf(mLocationSystemService.getServicedata()[0]) + "-double,"+String.valueOf(mLocationSystemService.getServicedata()[1]) + "-double,";
                }

                else if(service_type.equalsIgnoreCase("HUMIDITY_SERVICE"))
                {
                    actualVal[0] =mHumiditySensorService.getServicedata();
                    mValue = String.valueOf(mHumiditySensorService.getServicedata()) + "-double";
                }

                else if(service_type.equalsIgnoreCase("TEMPERATURE_SERVICE"))
                {
                    actualVal[0] =mTemperatureSensorService.getServicedata();
                    mValue = String.valueOf(mTemperatureSensorService.getServicedata()) + "-double";
                }

                else if(service_type.equalsIgnoreCase("INTENSITY_SERVICE"))
                {
                    actualVal[0] =mLightIntensitySensorService.getServicedata();
                    mValue = String.valueOf(mLightIntensitySensorService.getServicedata()) + "-double";
                }

                //use to check whether the Sensor is not listening
                if(actualVal[0]==-99)
                {
                    mTimer.cancel();
                }

                else
                {
                    mCep.analyseTheData(mValue, service_type);
                }
            }
        }, 10, (long) 937.5);
    }

    /**
     * Stop the sensor by unregister them.
     */
    public void stopSensors()
    {

        if (mStream.equalsIgnoreCase("LOCATION_SERVICE")) {
            mLocationSystemService.stopLocationService();
        }

        else if(mStream.equalsIgnoreCase("HUMIDITY_SERVICE"))
        {
            mHumiditySensorService.stopHumiditySensorService();
        }

        else if(mStream.equalsIgnoreCase("TEMPERATURE_SERVICE"))
        {
            mTemperatureSensorService.stopTemperatureSensorService();
        }

        else if(mStream.equalsIgnoreCase("INTENSITY_SERVICE"))
        {
           mLightIntensitySensorService.stopLightIntensitySensorService();
        }
    }
}



