package org.wentura.calendar.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.wentura.calendar.util.Util.queryToMap;

public class QueryToMapTest {

    @Test
    void nullInput() {
        assertNull(queryToMap(null));
    }

    @Test
    void emptyInput() {
        assertNull(queryToMap(""));
    }

    @Test
    void whitespaceInput() {
        assertNull(queryToMap("    "));
    }

    @Test
    void oneParameterInput() {
        var expectedMap =
                Map.ofEntries(
                        entry(
                                "code",
                                "4/0AcvDMrBxdwG40MsEowCJlmjdPs2WlwDNXSR-vuwnHP9QdkbO3Ptikqr2CMnVXt8VBfTdEg"));

        assertEquals(
                expectedMap,
                queryToMap(
                        "code=4/0AcvDMrBxdwG40MsEowCJlmjdPs2WlwDNXSR-vuwnHP9QdkbO3Ptikqr2CMnVXt8VBfTdEg"));
    }

    @Test
    void twoParameterInput() {
        var expectedMap =
                Map.ofEntries(
                        entry(
                                "code",
                                "4/0AcvDMrBxdwG40MsEowCJlmjdPs2WlwDNXSR-vuwnHP9QdkbO3Ptikqr2CMnVXt8VBfTdEg"),
                        entry(
                                "scope",
                                "https://www.googleapis.com/auth/calendar.events.readonly%20https://www.googleapis.com/auth/calendar.readonly"));

        assertEquals(
                expectedMap,
                queryToMap(
                        "code=4/0AcvDMrBxdwG40MsEowCJlmjdPs2WlwDNXSR-vuwnHP9QdkbO3Ptikqr2CMnVXt8VBfTdEg&scope=https://www.googleapis.com/auth/calendar.events.readonly%20https://www.googleapis.com/auth/calendar.readonly"));
    }
}
