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
        assertEquals("0h", getFormattedDuration(0L));
    }

    @Test
    void fiftyNineSecondsInput() {
        assertEquals("0h", getFormattedDuration(59L));
    }

    @Test
    void oneMinuteInput() {
        assertEquals("1m", getFormattedDuration(60L));
    }

    @Test
    void oneHourInput() {
        assertEquals("1h", getFormattedDuration(3600L));
    }
    
    @Test
    void ninetyNineInput() {
        assertEquals("99h", getFormattedDuration(356_400L));
    }

    @Test
    void maxInput() {
        assertEquals("2562047788015215h 30m", getFormattedDuration(Long.MAX_VALUE));
    }
}
