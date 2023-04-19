package com.company;

import org.junit.Test;

import static org.junit.Assert.*;


public class SplitTest {

    @Test
    public void classic() {
        Split split = new Split("\\iHateProgramming so much soo soo much 3");
        assertEquals("\\iHateProgramming", split.getCommand());
        assertEquals("so much soo soo much 3", split.getMessage());
    }

    @Test
    public void classic1() {
        Split split = new Split("\\iHateProgramming");
        assertEquals("\\iHateProgramming", split.getCommand());
        assertEquals("", split.getMessage());
    }

    @Test
    public void classic2() {
        Split split = new Split("\\");
        assertEquals("\\", split.getCommand());
        assertEquals("", split.getMessage());
    }
}
