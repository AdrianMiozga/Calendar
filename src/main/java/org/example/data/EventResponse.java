package org.example.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public record EventResponse(@SerializedName("items") List<Event> events) {

}
