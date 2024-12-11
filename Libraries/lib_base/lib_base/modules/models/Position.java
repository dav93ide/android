package com.bodhitech.it.lib_base.lib_base.modules.models;

import com.bodhitech.it.lib_base.lib_base.modules.utils.PositionUtils;

import java.util.Comparator;

public class Position implements Comparator<Position> {

    private Double mLatitude;
    private Double mLongitude;

    public Position(){ }

    public Position(Double latitude, Double longitude){
        mLatitude = latitude;
        mLongitude = longitude;
    }

    /** Override Object Methods **/
    @Override
    public int compare(Position o1, Position o2) {
        double dist1 = o1.getDistanceFromUser();
        double dist2 = o2.getDistanceFromUser();
        return dist1 < dist2 ? -1 : dist1 == dist2 ? 0x0 : 0x1;
    }

    public int compare(Position o2) {
        double dist1 = this.getDistanceFromUser();
        double dist2 = o2.getDistanceFromUser();
        return dist1 < dist2 ? -1 : dist1 == dist2 ? 0x0 : 0x1;
    }

    /** Getter & Setter Methods **/
    public Double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    /** Private Methods **/
    private double getDistanceFromUser(){
        return getDistanceFromPosition(PositionUtils.getLastUserPosition());
    }

    private double getDistanceFromPosition(Position pos){
        if(pos == null) {
            return 0x0;
        }
        double theta = mLongitude - pos.getLongitude();
        double dist = Math.sin(deg2rad(mLatitude)) * Math.sin(deg2rad(pos.getLatitude())) + Math.cos(deg2rad(mLatitude)) * Math.cos(deg2rad(pos.getLatitude())) * Math.cos(deg2rad(theta));
        return rad2deg(Math.acos(dist)) * 60 * 1.1515 * 1.609344;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }


    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    @Override
    public String toString() {
        return "Position{" +
                ", mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                '}';
    }

}