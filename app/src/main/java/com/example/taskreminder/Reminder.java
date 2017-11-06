package com.example.taskreminder;

public class Reminder {

    public Double latitude;
    public Double longitude;
    public String title;
    public String description;
    public String locationName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String id;

    public Reminder(Double latitude, Double longitude, String locationName, String title, String description, String id){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.locationName = locationName;
        this.description = description;
    }
}
