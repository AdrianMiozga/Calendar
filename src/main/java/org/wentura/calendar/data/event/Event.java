package org.wentura.calendar.data.event;

import com.google.gson.annotations.SerializedName;

public record Event(@SerializedName("summary") String title, Time start, Time end) {}
