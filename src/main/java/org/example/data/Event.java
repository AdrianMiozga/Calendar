package org.example.data;

import com.google.gson.annotations.SerializedName;


public record Event(@SerializedName("summary") String title, Time start, Time end) {

}
