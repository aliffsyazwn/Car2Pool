package com.aliffcorp.car2pool.model;

public class Booking {

    private int booking_id;
    private int user_id;
    private int driver_id;
    private int ride_id;

    private Ride ride;

    public Ride getRide() { return ride; }
    public Booking() {
    }

    public Booking(int booking_id, int user_id, int driver_id, int ride_id) {
        this.booking_id = booking_id;
        this.user_id = user_id;
        this.driver_id = driver_id;
        this.ride_id = ride_id;
    }

    public int getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(int booking_id) {
        this.booking_id = booking_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public int getRide_id() {
        return ride_id;
    }

    public void setRide_id(int ride_id) {
        this.ride_id = ride_id;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "booking_id=" + booking_id +
                ", user_id=" + user_id +
                ", driver_id=" + driver_id +
                ", ride_id=" + ride_id +
                '}';
    }
}
