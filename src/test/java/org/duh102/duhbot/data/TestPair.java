package org.duh102.duhbot.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPair {
    String aStr = "a";
    Integer one = 1;
    @Test
    public void testPair() {
        Pair<String, Integer> a = new Pair<>(aStr, one);
        assertEquals(aStr, a.getFirst());
        assertEquals(one, a.getSecond());
    }
}
