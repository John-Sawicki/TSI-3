package com.john.android.tsi.Room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
@Entity(tableName = "task")
public class TaskEntryRm {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;
    private String system;
    private String summary;
    private String location;
    private int status;
    @Ignore
    public TaskEntryRm(String date, String system, String summary,
                       String location, int status){
        this.date = date;
        this.system = system;
        this.summary = summary;
        this.location = location;
        this.status = status;
    }
    public TaskEntryRm(int id, String date, String system, String summary,
                       String location, int status){
        this.id = id;
        this.date = date;
        this.system = system;
        this.summary = summary;
        this.location = location;
        this.status = status;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}