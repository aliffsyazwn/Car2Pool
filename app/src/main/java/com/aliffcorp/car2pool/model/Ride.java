package com.aliffcorp.car2pool.model;

public class Ride {

    private int ride_id;
    private int driver_id;
    private String origin;
    private String destination;
    private String departure_time;
    private int available_seats;

    public Ride(){}

    public Ride(int ride_id, int driver_id, String origin, String destination, String departure_time, int available_seats) {
        this.ride_id = ride_id;
        this.driver_id = driver_id;
        this.origin = origin;
        this.destination = destination;
        this.departure_time = departure_time;
        this.available_seats = available_seats;
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

    public int getAvailable_seats() {
        return available_seats;
    }

    public void setAvailable_seats(int available_seats) {
        this.available_seats = available_seats;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "ride_id=" + ride_id +
                ", driver_id=" + driver_id +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", departure_time='" + departure_time + '\'' +
                ", available_seats=" + available_seats +
                '}';
    }
}
