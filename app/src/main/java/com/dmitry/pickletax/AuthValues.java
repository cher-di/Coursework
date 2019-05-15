package com.dmitry.pickletax;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthValues {
    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("city")
    @Expose
    public String city;
}
