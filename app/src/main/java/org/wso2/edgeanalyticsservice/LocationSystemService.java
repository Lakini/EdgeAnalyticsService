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
*//*

package org.wso2.edgeanalyticsservice1;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

public class LocationSystemService {

    public Context mContext;
    double [] mMeasurments=null;
    double mLongitude = 0.0;
    double mLatitude = 0.0;
    double MINIMUM_DISTANCE_FOR_UPDATES = 0.25;//in meters
    long   MINIMUM_TIME_BETWEEN_UPDATES = 1000;//milliseconds
    Location mlocation=null;
    LocationManager mLocationManager;
    IEdgeAnalyticServiceCallback mIEdgeAnalyticServiceCallback;
    MyLocationListner mLocationListner;
    CEP mCEP;

    public LocationSystemService(Context context,IEdgeAnalyticServiceCallback iEdgeAnalyticServiceCallback)
    {
        mContext=context;
        mIEdgeAnalyticServiceCallback=iEdgeAnalyticServiceCallback;
    }

    */
/** Initialize the location system Service and set the minimal update distance and time  *//*

    public void initializeLocationService()
    {
        mLocationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, (float) MINIMUM_DISTANCE_FOR_UPDATES, new MyLocationListner(), Looper.getMainLooper());
        mlocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLocationListner=new MyLocationListner();

        if(mlocation!=null)
        {
            mLocationListner.onLocationChanged(mlocation);
        }

        else
        {
            //do something
        }
    }

    */
/** Implementation of the LocationListner and onLocationChanged method will call
     * the cep analysing method to analyse the data*//*

    private class MyLocationListner implements LocationListener {
        public void onLocationChanged(Location location) {

           mCEP=new CEP();
            String locationVal;
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            locationVal=String.valueOf(mLatitude)+"-double,"+String.valueOf(mLongitude)+"-double";
            mCEP.analyseTheData(locationVal,"LocationStream",mIEdgeAnalyticServiceCallback);

        }

        public void onStatusChanged(String s, int i, Bundle b) {

        }

        public void onProviderDisabled(String s) {

        }

        public void onProviderEnabled(String s) {

        }
    }

    public float

}
*/

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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

/**
 * This class reads the location details of the device using GPS
 */
public class LocationSystemService {

    public Context mContext;
    double [] mMeasurments=null;
    double mLongitude = 0.0;
    double mLatitude = 0.0;
    double MINIMUM_DISTANCE_FOR_UPDATES = 0.25;//in meters
    long   MINIMUM_TIME_BETWEEN_UPDATES = 1000;//milliseconds
    Location mlocation=null;
    LocationManager mLocationManager;
    MyLocationListner mLocationListner;


    private static LocationSystemService mLocationSystemServiceInstance = null;


    //to use a single siddhi manager instance
    public static LocationSystemService getInstance(Context context) {
        if(mLocationSystemServiceInstance == null) {
            mLocationSystemServiceInstance = new LocationSystemService(context);
        }
        return mLocationSystemServiceInstance;
    }

    LocationSystemService(Context context) {
        startLocationService(context);
    }

    /** Initialize the location system Service and set the minimal update distance and time  */
    public void startLocationService(Context context)
    {
        mContext=context;
        mLocationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, (float) MINIMUM_DISTANCE_FOR_UPDATES, new MyLocationListner(), Looper.getMainLooper());
        mlocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLocationListner=new MyLocationListner();
    }

    /** Implementation of the LocationListner and onLocationChanged method will call
     * the cep analysing method to analyse the data*/
    private class MyLocationListner implements LocationListener {
        public void onLocationChanged(Location location) {

            //mCEP=new CEP();
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            mMeasurments=new double[]{mLatitude, mLongitude};

        }

        public void onStatusChanged(String s, int i, Bundle b) {

        }

        public void onProviderDisabled(String s) {

        }

        public void onProviderEnabled(String s) {

        }
    }

    public double[] getServicedata()
    {
        return mMeasurments;
    }

    public void stopLocationService()
    {
        mLocationManager.removeUpdates(mLocationListner);
        mLatitude=-99;
        mLongitude=-99;
    }
}
