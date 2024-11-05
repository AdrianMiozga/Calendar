package org.wentura.calendar.data.eventtime;

public record EventTime(String eventName, Long durationInSeconds) implements Comparable<EventTime> {

    @Override
    public int compareTo(EventTime other) {
        return other.durationInSeconds.compareTo(this.durationInSeconds);
    }
}
