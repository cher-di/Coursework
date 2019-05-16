package com.dmitry.pickletax;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Classroom {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("campus_name")
    @Expose
    private String campus_name;

    @SerializedName("type")
    @Expose
    private String type;

    Classroom(String name, String campus_name, String type) {
        this.name = name;
        this.campus_name = campus_name;
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCampus_name(String campus_name) {
        this.campus_name = campus_name;
    }

    public String getCampus_name() {
        return campus_name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
