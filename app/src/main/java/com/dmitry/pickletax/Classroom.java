package com.dmitry.pickletax;

public class Classroom {
    private String name;
    private String campus;
    private String type;

    Classroom(String name, String campus, String type) {
        this.name = name;
        this.campus = campus;
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getCampus() {
        return campus;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
