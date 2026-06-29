package com.aliffcorp.car2pool.model;

public class Ride {

    private String origin;
    private String destination;
    private String time;
    private String driver;

    public Ride(){}
    public Ride(String origin, String driver, String time, String destination) {
        this.origin = origin;
        this.driver = driver;
        this.time = time;
        this.destination = destination;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }
}
