package com.abdulahad.blooddonationapp.admin.model;

public class AdminItem {
    private String title;
    private int icon;

    public AdminItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }
}
