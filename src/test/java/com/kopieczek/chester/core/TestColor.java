package com.kopieczek.chester.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestColor {
    @Test
    public void test_inverse_of_white_is_black() {
        assertEquals(Color.BLACK, Color.WHITE.inverse());
    }

    @Test
    public void test_inverse_of_black_is_white() {
        assertEquals(Color.WHITE, Color.BLACK.inverse());
    }
}
