package com.sonymobile.lifelog.api.models;

import java.util.Date;

/**
 * Created by championswimmer on 11/5/16.
 */
public class Me {

    String username;
    Date lastModified;
    String birthday;
    String gender;
    Integer weight;
    Double height;
    Double bmr;
    Double defaultRunningStepLength;
    Double defaultStepLength;

    public String getUsername() {
        return username;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public Integer getWeight() {
        return weight;
    }

    public Double getHeight() {
        return height;
    }

    public Double getBmr() {
        return bmr;
    }

    public Double getDefaultRunningStepLength() {
        return defaultRunningStepLength;
    }

    public Double getDefaultStepLength() {
        return defaultStepLength;
    }


}
