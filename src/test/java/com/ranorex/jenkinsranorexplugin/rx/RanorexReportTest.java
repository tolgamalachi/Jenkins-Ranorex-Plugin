package com.ranorex.jenkinsranorexplugin.rx;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RanorexReportTest {

    @Test
   void ConstructorTest()
    {
        try {
            RanorexReport rpo = new RanorexReport("C:\\temp\\", "This Report Dir", "This report Name", "rxlog", false, false, "", "");
        }catch(Exception e)
        {
            Assert.assertTrue(false);
        }

    }
}