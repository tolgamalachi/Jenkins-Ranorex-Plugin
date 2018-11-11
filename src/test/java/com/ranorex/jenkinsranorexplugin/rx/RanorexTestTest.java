package com.ranorex.jenkinsranorexplugin.rx;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RanorexTestTest {
    @Test
    public void Constructor_ValidInput_ValidObject() {
        try {
            RanorexTest t = new RanorexTest("C:\\Temp", "MTestSuiteTest.exe", "");
            assertTrue(true);
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }
    }

    @Test
    public void Constructor_InvalidTestExe_ThrowsFileNotFoundException() {
        try {
            RanorexTest t = new RanorexTest("C:\\Temp", "ThisIsNotAValidFile", "");
        } catch (FileNotFoundException e) {
            assertEquals("File 'C:\\Temp\\ThisIsNotAValidFile' does not exist", e.getMessage());
        }
    }

    @Test
    public void Constructor_InvalidWorkingDirectory_ThrowsInvalidParameterException() {
        try {
            RanorexTest t = new RanorexTest("ThisIsNotAValidWorkingDirectory", "MTestSuiteTest1.exe", "");
        } catch (Exception e) {
            assertEquals("'ThisIsNotAValidWorkingDirectory' is not a valid directory path", e.getMessage());
        }
    }

    @Test
    public void PrintWorkingDirectory_ValidInput_ValidWorkingDirectory() {
        try {
            RanorexTest t = new RanorexTest("C:\\Temp", "MTestSuiteTest.exe", "");
            assertEquals("C:\\Temp", t.getWorkingDirectoryString());
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }
    }

    @Test
    public void ToString_ValidInputWithoutTestSuite_ValidOutput() {
        try {
            RanorexTest t = new RanorexTest("C:\\Temp", "MTestSuiteTest.exe", "");
            String output = "Working Directory:\tC:\\Temp\n" +
                    "Test Executable File:\tMTestSuiteTest.exe\n" +
                    "Test Suite File:\tNo Test Suite File selected!\n" +
                    "RunConfiguration:\tNo Runconfiguration entered\n" +
                    "Sync with TestRail:\tfalse\n" +
                    "\n" +
                    "Global Parameters:\n" +
                    "\tNo Parameters entered\n" +
                    "\n" +
                    "Command Line Arguments:\n" +
                    "\tNo Command Line Arguments entered\n";
            assertEquals(output, t.toString());
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }
    }

    @Test
    public void ToString_ValidInputWithTestSuite_ValidOutput() {
        try {
            RanorexTest t = new RanorexTest("C:\\Temp", "MTestSuiteTest.exe", "Test.rxtst");
            String output = "Working Directory:\tC:\\Temp\n" +
                    "Test Executable File:\tMTestSuiteTest.exe\n" +
                    "Test Suite File:\tTest.rxtst\n" +
                    "RunConfiguration:\tNo Runconfiguration entered\n" +
                    "Sync with TestRail:\tfalse\n" +
                    "\n" +
                    "Global Parameters:\n" +
                    "\tNo Parameters entered\n" +
                    "\n" +
                    "Command Line Arguments:\n" +
                    "\tNo Command Line Arguments entered\n";
            assertEquals(output, t.toString());
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }
    }
}