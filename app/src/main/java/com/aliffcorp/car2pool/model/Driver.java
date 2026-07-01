package com.aliffcorp.car2pool.model;

public class Driver {

    private int id;
    private String fullname;
    private String email;
    private String password;
    private String phone_number;
    private String vehicle_type;
    private String vehicle_model;
    private String vehicle_plate;
    private String license_number;
    private int available_seats;

    public Driver(){};
    public Driver(int id, String fullname, String email, String password, String phone_number, String vehicle_type, String vehicle_model, String vehicle_plate, String license_number, int available_seats) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.phone_number = phone_number;
        this.vehicle_type = vehicle_type;
        this.vehicle_model = vehicle_model;
        this.vehicle_plate = vehicle_plate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getVehicle_model() {
        return vehicle_model;
    }

    public void setVehicle_model(String vehicle_model) {
        this.vehicle_model = vehicle_model;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getVehicle_plate() {
        return vehicle_plate;
    }

    public void setVehicle_plate(String vehicle_plate) {
        this.vehicle_plate = vehicle_plate;
    }


    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", vehicle_type='" + vehicle_type + '\'' +
                ", vehicle_model='" + vehicle_model + '\'' +
                ", vehicle_plate='" + vehicle_plate + '\'' +
                '}';
    }
}
