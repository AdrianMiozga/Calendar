package org.wentura.calendar;

import dagger.Component;

import org.wentura.calendar.data.event.EventRepository;
import org.wentura.calendar.di.NetworkModule;
import org.wentura.calendar.di.RepositoryModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {NetworkModule.class, RepositoryModule.class})
public interface AppComponent {

    EventRepository createEventRepository();
}
