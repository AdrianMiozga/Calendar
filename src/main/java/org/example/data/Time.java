package org.example.data;

import com.google.gson.annotations.SerializedName;


public record Time(@SerializedName("dateTime") String offsetDateTime) {

}
