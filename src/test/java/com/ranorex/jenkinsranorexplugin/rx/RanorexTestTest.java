package com.ranorex.jenkinsranorexplugin.rx;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RanorexTestTest {
    @Test
    public void Constructor_ValidInput_ValidObject() {
        try {
            RanorexTest t = new RanorexTest("", "C:\\Users\\mstoegerer\\Documents\\Ranorex\\RanorexStudio Projects\\MTestSuiteTest\\MTestSuiteTest\\bin\\Debug\\MTestSuiteTest.exe", "");
            assertTrue(true);
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }
    }

    @Test
    public void Constructor_InvalidTestExe_ThrowsFileNotFoundException() {
        try {
            RanorexTest t = new RanorexTest("", "ThisIsNotAValidFile", "");
        } catch (FileNotFoundException e) {
            assertEquals("File 'ThisIsNotAValidFile' does not exist", e.getMessage());
        }
    }

    @Test
    public void Constructor_InvalidWorkingDirectory_ThrowsInvalidParameterException() {
        try {
            RanorexTest t = new RanorexTest("ThisIsNotAValidWorkingDirectory", "C:\\Users\\mstoegerer\\Documents\\Ranorex\\RanorexStudio Projects\\MTestSuiteTest\\MTestSuiteTest\\bin\\Debug\\MTestSuiteTest1.exe", "");
        } catch (Exception e) {
            assertEquals("'ThisIsNotAValidWorkingDirectory' is not a valid directory path", e.getMessage());
        }
    }

    @Test
    public void PrintWorkingDirectory_ValidInput_ValidWorkingDirectory() {
        try {
            RanorexTest t = new RanorexTest("C:\\Temp", "C:\\Users\\mstoegerer\\Documents\\Ranorex\\RanorexStudio Projects\\MTestSuiteTest\\MTestSuiteTest\\bin\\Debug\\MTestSuiteTest.exe", "");
            assertEquals("C:\\Temp", t.getWorkingDirectoryString());
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }
    }
}