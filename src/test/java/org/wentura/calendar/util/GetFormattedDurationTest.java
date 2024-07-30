package org.wentura.calendar.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.wentura.calendar.util.TimeUtils.getFormattedDuration;

import org.junit.jupiter.api.Test;

public class GetFormattedDurationTest {

    @Test
    void negativeInput() {
        assertThrows(IllegalArgumentException.class, () -> getFormattedDuration(-60L));
    }

    @Test
    void zeroSecondsInput() {
        assertEquals("0h 0m", getFormattedDuration(0L));
    }

    @Test
    void fiftyNineSecondsInput() {
        assertEquals("0h 0m", getFormattedDuration(59L));
    }

    @Test
    void oneMinuteInput() {
        assertEquals("0h 1m", getFormattedDuration(60L));
    }

    @Test
    void oneHourInput() {
        assertEquals("1h 0m", getFormattedDuration(3600L));
    }
    
    @Test
    void ninetyNineInput() {
        assertEquals("99h 0m", getFormattedDuration(356_400L));
    }

    @Test
    void maxInput() {
        assertEquals("2562047788015215h 30m", getFormattedDuration(Long.MAX_VALUE));
    }
}
