package com.ranorex.jenkinsranorexplugin.rx;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RanorexReportTest {

    @Test
    @DisplayName ("Constructor should create a valid Ranorex Report object if the Input is valid ")
    void ConstructorTest() {
        try {
            RanorexReport rpo = new RanorexReport("C:\\temp\\", "This Report Dir", "This report Name", "rxlog", false, false, "", "");
            assertEquals("C:\\temp\\This Report Dir", rpo.getReportDirectory());
            assertEquals("This report Name", rpo.getReportName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.assertTrue(false);
        }

    }
}