package com.abdulahad.blooddonationapp.model;

public class BloodRequest {
    private int id;
    private int userId;
    private String name;
    private String phone;
    private String bloodGroup;
    private String district;
    private String address;
    private String problem;
    private String image;
    private String requestedAt;

    public BloodRequest(int id, int userId, String name, String phone, String bloodGroup,
                        String district, String address, String problem, String image, String requestedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.district = district;
        this.address = address;
        this.problem = problem;
        this.image = image;
        this.requestedAt = requestedAt;
    }

    // Getter Methods

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public String getDistrict() {
        return district;
    }

    public String getAddress() {
        return address;
    }

    public String getProblem() {
        return problem;
    }

    public String getImage() {
        return image;
    }

    public String getRequestedAt() {
        return requestedAt;
    }
}
