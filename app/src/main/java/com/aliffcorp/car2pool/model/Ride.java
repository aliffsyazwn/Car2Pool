package com.aliffcorp.car2pool.model;

import com.google.gson.annotations.SerializedName;

public class Ride {

    private int ride_id;
    private int driver_id;
    private String origin;
    private String destination;
    private String departure_time;
    private double price;

    @SerializedName("fSeat")
    private int fSeat;
    @SerializedName("rSeat")
    private int rSeat;
    @SerializedName("mSeat")
    private int mSeat;
    @SerializedName("lSeat")
    private int lSeat;

    private User driver;

    public User getDriver() { return driver; }

    public Ride(){}

    // NEW: Added double price to the constructor
    public Ride(int ride_id, int driver_id, String origin, String destination, String departure_time, double price, int fSeat, int mSeat, int rSeat, int lSeat, User driver) {
        this.ride_id = ride_id;
        this.driver_id = driver_id;
        this.origin = origin;
        this.destination = destination;
        this.departure_time = departure_time;
        this.price = price;
        this.fSeat = fSeat;
        this.mSeat = mSeat;
        this.rSeat = rSeat;
        this.lSeat = lSeat;
        this.driver = driver;
    }

    public int getRide_id() {
        return ride_id;
    }

    public void setRide_id(int ride_id) {
        this.ride_id = ride_id;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(String departure_time) {
        this.departure_time = departure_time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean getfSeat() {
        return fSeat != 0;
    }

    public void setfSeat(int fSeat) {
        this.fSeat = fSeat;
    }

    public boolean getrSeat() {
        return rSeat != 0;
    }

    public void setrSeat(int rSeat) {
        this.rSeat = rSeat;
    }

    public boolean getmSeat() {
        return mSeat != 0;
    }

    public void setmSeat(int mSeat) { this.mSeat = mSeat; }

    public boolean getlSeat() {
        return lSeat != 0;
    }

    public void setlSeat(int lSeat) {
        this.lSeat = lSeat;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public int getfSeatStatus() {
        return fSeat;
    }

    public int getrSeatStatus() {
        return rSeat;
    }

    public int getmSeatStatus() {
        return mSeat;
    }

    public int getlSeatStatus() {
        return lSeat;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "ride_id=" + ride_id +
                ", driver_id=" + driver_id +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", departure_time='" + departure_time + '\'' +
                ", price=" + price +
                ", fSeat=" + fSeat +
                ", rSeat=" + rSeat +
                ", mSeat=" + mSeat +
                ", lSeat=" + lSeat +
                ", driver=" + driver +
                '}';
    }
}