package org.example;

import org.example.data.CalendarRepository;
import org.example.data.EventResponse;
import retrofit2.Response;


public class Main {

    public static void main(String[] args) {
        CalendarRepository calendarRepository = new CalendarRepository();

        Response<EventResponse> response = calendarRepository.getEvents();
        System.out.println(response.code());
        System.out.println(response.body());
    }
}
