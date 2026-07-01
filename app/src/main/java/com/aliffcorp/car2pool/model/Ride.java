package com.aliffcorp.car2pool.model;

public class Ride {

    private int id;
    private String origin;
    private String destination;
    private String time;
    private int available_seats;
    private Driver driver;

    public Ride(){}

    public Ride(int id, String origin, String destination, int available_seats, String time, Driver driver) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.available_seats = available_seats;
        this.time = time;
        this.driver = driver;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getAvailable_seats() {
        return available_seats;
    }

    public void setAvailable_seats(int available_seats) {
        this.available_seats = available_seats;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
