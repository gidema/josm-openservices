package org.openstreetmap.josm.plugins.ods;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ParameterSetTest {
    private static ParameterType<String> STRING_P = new ParameterType<>(String.class);
    private static ParameterType<Integer> INT_P = new ParameterType<>(Integer.class);

    @SuppressWarnings("static-method")
    @Test
    public void test() {
        ParameterSet parameters = new ParameterSet();
        parameters.put(STRING_P, "Blabla");
        parameters.put(INT_P, 17);

        assertEquals((Integer)17, parameters.get(INT_P));
        assertEquals("Blabla", parameters.get(STRING_P));

    }

}
