package org.example.data.calendar;

import com.google.gson.annotations.SerializedName;


public record Time(@SerializedName("dateTime") String offsetDateTime) {

}
