package org.example.data.calendar;

import com.google.gson.annotations.SerializedName;


public record Event(@SerializedName("summary") String title, Time start, Time end) {

}
