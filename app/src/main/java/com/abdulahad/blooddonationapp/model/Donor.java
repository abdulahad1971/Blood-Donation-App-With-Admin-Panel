package com.abdulahad.blooddonationapp.model;

public class Donor {
    String name, phone, blood_group, district, address, profile_image;

    public Donor(String name, String phone, String blood_group, String district, String address, String profile_image) {
        this.name = name;
        this.phone = phone;
        this.blood_group = blood_group;
        this.district = district;
        this.address = address;
        this.profile_image = profile_image;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getBloodGroup() { return blood_group; }
    public String getDistrict() { return district; }
    public String getAddress() { return address; }
    public String getProfileImage() { return profile_image; }
}

