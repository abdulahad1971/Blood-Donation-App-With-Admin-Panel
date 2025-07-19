package com.abdulahad.blooddonationapp.admin.model;

public class UserModel {
    private String id, name, phone, blood_group, district, address, profile_image,is_donor;

    public UserModel(String id, String name, String phone, String blood_group, String district, String address, String profile_image,String is_donor) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.blood_group = blood_group;
        this.district = district;
        this.address = address;
        this.profile_image = profile_image;
        this.is_donor = is_donor;
    }

    public String getIs_donor() {
        return is_donor;
    }



    public void setIs_donor(String is_donor) {
        this.is_donor = is_donor;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getBlood_group() { return blood_group; }
    public String getDistrict() { return district; }
    public String getAddress() { return address; }
    public String getProfile_image() { return profile_image; }


}

