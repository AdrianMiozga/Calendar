package org.wentura.calendar.data.config;

public record Config(
        String port,
        String clientId,
        String clientSecret,
        String redirectURI,
        String redirectPath) {}
