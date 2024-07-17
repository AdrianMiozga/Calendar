package org.example.data.event;

import com.google.gson.annotations.SerializedName;

public record Time(@SerializedName("dateTime") String offsetDateTime) {}
