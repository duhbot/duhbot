package org.duh102.duhbot.functions;

import org.duh102.duhbot.exception.MismatchedServiceResponseClass;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestServiceResponse {
    @Test
    public void testGoodResponse() throws Exception {
        String test = "a";
        ServiceResponse<String> response =
                new ServiceResponse<>(String.class,
                test);
        String resp = response.getResponse();
        assertEquals(test, resp);
    }

    @Test
    public void testMismatchClassResponse() {
        String test = "a";
        assertThrows(MismatchedServiceResponseClass.class, () -> {
            new ServiceResponse<>(Integer.class, test);
        });
    }
}
