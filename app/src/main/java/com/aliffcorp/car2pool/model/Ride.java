package com.aliffcorp.car2pool.model;

public class Ride {

    private int ride_id;
    private int driver_id;
    private String origin;
    private String destination;
    private String departure_time;
    private Boolean fSeat;
    private Boolean rSeat;
    private Boolean mSeat;
    private Boolean lSeat;

    private User driver;

    public User getDriver() { return driver; }

    public Ride(){}

    public Ride(int ride_id, int driver_id, String origin, String destination, String departure_time, Boolean fSeat, Boolean mSeat, Boolean rSeat, Boolean lSeat, User driver) {
        this.ride_id = ride_id;
        this.driver_id = driver_id;
        this.origin = origin;
        this.destination = destination;
        this.departure_time = departure_time;
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

    public Boolean getfSeat() {
        return fSeat;
    }

    public void setfSeat(Boolean fSeat) {
        this.fSeat = fSeat;
    }

    public Boolean getrSeat() {
        return rSeat;
    }

    public void setrSeat(Boolean rSeat) {
        this.rSeat = rSeat;
    }

    public Boolean getmSeat() {
        return mSeat;
    }

    public void setmSeat(Boolean mSeat) {
        this.mSeat = mSeat;
    }

    public Boolean getlSeat() {
        return lSeat;
    }

    public void setlSeat(Boolean lSeat) {
        this.lSeat = lSeat;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "ride_id=" + ride_id +
                ", driver_id=" + driver_id +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", departure_time='" + departure_time + '\'' +
                ", fSeat=" + fSeat +
                ", rSeat=" + rSeat +
                ", mSeat=" + mSeat +
                ", lSeat=" + lSeat +
                ", driver=" + driver +
                '}';
    }
}
