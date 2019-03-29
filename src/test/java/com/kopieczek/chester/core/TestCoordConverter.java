package com.kopieczek.chester.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCoordConverter {
    @Test
    public void test_0_is_a1() {
        assertEquals("a1", CoordConverter.convert(0));
    }

    @Test
    public void test_1_is_b1() {
        assertEquals("b1", CoordConverter.convert(1));
    }

    @Test
    public void test_7_is_h1() {
        assertEquals("h1", CoordConverter.convert(7));
    }

    @Test
    public void test_8_is_a2() {
        assertEquals("a2", CoordConverter.convert(8));
    }

    @Test
    public void test_39_is_h5() {
        assertEquals("h5", CoordConverter.convert(39));
    }

    @Test
    public void test_63_is_h8() {
        assertEquals("h8", CoordConverter.convert(63));
    }

    @Test
    public void test_a1_is_0() {
        assertEquals(0, CoordConverter.convert("a1"));
    }

    @Test
    public void test_b1_is_1() {
        assertEquals(1, CoordConverter.convert("b1"));
    }

    @Test
    public void test_h1_is_7() {
        assertEquals(7, CoordConverter.convert("h1"));
    }

    @Test
    public void test_a2_is_8() {
        assertEquals(8, CoordConverter.convert("a2"));
    }

    @Test
    public void test_h5_is_39() {
        assertEquals(39, CoordConverter.convert("h5"));
    }

    @Test
    public void test_h8_is_63() {
        assertEquals(63, CoordConverter.convert("h8"));
    }
}
