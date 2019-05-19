package com.dmitry.pickletax;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceValues {
    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("city")
    @Expose
    private String city;

    private int max_lesson_number;
    private String youngest_update;
    private String oldest_update;

    public ServiceValues() {
        this.email = "example@mail.ru";
        this.city = "ExampleCity";
        this.max_lesson_number = 0;
        this.oldest_update = null;
        this.youngest_update = null;
    }

    public ServiceValues(String email, String city) {
        this.email = email;
        this.city = city;
        this.max_lesson_number = 0;
        this.oldest_update = null;
        this.youngest_update = null;
    }

    public ServiceValues(String email, String city, int max_lesson_number, String youngest_update, String oldest_update) {
        this.email = email;
        this.city = city;
        this.max_lesson_number = max_lesson_number;
        this.youngest_update = youngest_update;
        this.oldest_update = oldest_update;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getMax_lesson_number() {
        return max_lesson_number;
    }

    public void setMax_lesson_number(int max_lesson_number) {
        this.max_lesson_number = max_lesson_number;
    }

    public String getYoungest_update() {
        return youngest_update;
    }

    public void setYoungest_update(String youngest_update) {
        this.youngest_update = youngest_update;
    }

    public String getOldest_update() {
        return oldest_update;
    }

    public void setOldest_update(String oldest_update) {
        this.oldest_update = oldest_update;
    }
}
