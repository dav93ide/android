package com.bodhitech.it.lib_base.lib_base.modules.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.bodhitech.it.lib_base.lib_base.modules.models.Position;

import java.io.IOException;
import java.util.List;

public class PositionUtils {

    private static String TAG = PositionUtils.class.getSimpleName();
    // Distance Values
    public static final double RET_ERROR = -0x1;

    private static Geocoder mGeocoder;
    private static Position mLastUserPosition;

    public static void initGeocoder(Context context){
        if(mGeocoder == null){
            mGeocoder = new Geocoder(context);
        }
    }

    /** Getter & Setter Methods **/
    public static Geocoder getGeocoder() {
        return mGeocoder;
    }

    public static void setGeocoder(Geocoder geocoder) {
        mGeocoder = geocoder;
    }

    public static Position getLastUserPosition() {
        return mLastUserPosition;
    }

    public static void setLastUserPosition(Position lastUserPosition) {
        mLastUserPosition = lastUserPosition;
    }

    /** Public Methods **/
    public static Position getPositionFromAddress(String address){
        Position pos = null;
        try{
            List<Address> addresses = mGeocoder.getFromLocationName(address, 0x1);
            if(addresses.size() > 0){
                Address add = addresses.get(0);
                if(add != null){
                    pos = new Position(add.getLatitude(), add.getLongitude());
                }
            }
        } catch (IOException ioE){
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        }
        return pos;
    }

    public static double distanceBetween(double lat1, double lon1, double lat2, double lon2) {
        float result[] = new float[0x1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, result);
        return result[0x0];
    }

    public static double getDistanceFromUser(Double lat, Double lng) {
        Position userPosition = getLastUserPosition();
        if (userPosition == null) {
            return RET_ERROR;
        }
        if (lat == null || lng == null) {
            return RET_ERROR;
        }
        return distanceBetween(lat, lng, userPosition.getLatitude(), userPosition.getLongitude());
    }

}
