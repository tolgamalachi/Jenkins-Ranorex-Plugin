package com.ranorex.jenkinsranorexplugin.rx;

import hudson.FilePath;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;

import static junit.framework.TestCase.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RanorexTestTest {
    static File tempDirWithoutSpace = new File("C:\\JenkinsRxPluginTemp\\");
    static File tempDirWithSpace = new File("C:\\Jenkins Rx Plugin Temp\\");
    static File absoluteTestExeWithNoSpace = new File(tempDirWithoutSpace, "TestExe.exe");
    static File absoluteTestSuitewithNoSpace = new File(tempDirWithoutSpace, "TestSuite.rxtst");
    static File absoluteTestExeWithSpace = new File(tempDirWithSpace, "Test exe.exe");
    static File absoluteTestSuitewithSpace = new File(tempDirWithSpace, "Test Suite.rxtst");

    @BeforeAll
    static void createDummyFiles() {
        try {
            tempDirWithoutSpace.mkdir();
            tempDirWithSpace.mkdir();
            absoluteTestExeWithNoSpace.createNewFile();
            absoluteTestSuitewithNoSpace.createNewFile();
            absoluteTestExeWithSpace.createNewFile();
            absoluteTestSuitewithSpace.createNewFile();
        } catch (Exception e) {
            fail("Cannot create dummy files '" + e.getMessage() + "'");
        }
    }

    @AfterAll
    static void deleteDummyFiles() {
        try {

            absoluteTestExeWithNoSpace.delete();
            absoluteTestSuitewithNoSpace.delete();
            absoluteTestExeWithSpace.delete();
            absoluteTestSuitewithSpace.delete();
            tempDirWithoutSpace.delete();
            tempDirWithSpace.delete();
        } catch (Exception e) {
            fail("Cannot delete dummy files '" + e.getMessage() + "'");
        }
    }

    @DisplayName ("Constructor should create a valid Ranorex Test Object")
    @ParameterizedTest (name = "#{index} Create Ranorex Test object with [{0}] - [{1}] - [{2}]")
    @CsvSource ({
            "C:\\JenkinsRxPluginTemp, TestExe.exe, TestSuite.rxtst",
            "C:\\Jenkins Rx Plugin Temp, Test Exe.exe, Test Suite.rxtst"})
    public void Constructor_ValidInput_ValidObject(String WorkingDire, String TestExe, String TestSuite) {
        try {
            RanorexTest dummy = new RanorexTest(WorkingDire, TestExe, TestSuite);
            assertTrue(true);
        } catch (Exception e) {
            fail("An exception was thrown '" + e.getMessage() + "'");
        }
    }

    @DisplayName ("Constructor should throw an FileNotFoundException if any of the files does not exist")
    @ParameterizedTest (name = "#{index} Create Ranorex Test object with [{0}] - [{1}] - [{2}]")
    @CsvSource ({
            "C:\\JenkinsRxPluginTemp, NotExistingTestExe.exe, TestSuite.rxtst",
            "C:\\JenkinsRxPluginTemp, TestExe.exe, NotExistingTestSuite.rxtst",
            "C:\\Jenkins Rx Plugin Temp, NotExisting Test Exe.exe, Test Suite.rxtst",
            "C:\\Jenkins Rx Plugin Temp, Test Exe.exe, NotExisting Test Suite.rxtst",
            "C:\\NotExistingDirectory, Test Exe.exe, Test Suite.rxtst",
            "C:\\NotExisting Jenkins Rx Plugin Temp, Test Exe.exe, Test Suite.rxtst"})
    public void Constructor_NotExistingFiles_ThrowsFileNotFoundException(String WorkingDir, String TestExe, String TestSuite) {
        String notExistingFile = "";
        if (TestExe.contains("NotExisting")) {
            notExistingFile = WorkingDir + "\\" + TestExe;
        } else if (TestSuite.contains("NotExisting")) {
            notExistingFile = WorkingDir + "\\" + TestSuite;
        } else if (WorkingDir.contains("NotExisting")) {
            notExistingFile = WorkingDir;
        }
        Throwable thrownException = assertThrows(FileNotFoundException.class, () -> new RanorexTest(WorkingDir, TestExe, TestSuite));
        assertEquals(String.format("File or directory '%s' does not exist", notExistingFile), thrownException.getMessage());
    }

    @DisplayName ("Constructor should throw an InvalidParameterException if the Workingdirectory is not valid")
    @ParameterizedTest (name = "#{index} Create Ranorex Test object with [{0}]")
    @ValueSource (strings = {"C:\\Jenkins Rx Plugin Temp\\Test Exe.exe", "C:\\JenkinsRxPluginTemp\\TestExe.exe"})
    public void Constructor_InvalidWorkingDirectory_ThrowsInvalidParameterException(String input) {
        Throwable thrownException = assertThrows(InvalidParameterException.class, () -> new RanorexTest(input, "", ""));
        assertEquals("'" + input + "' is not a valid directory path", thrownException.getMessage());
    }

    @DisplayName ("GetWorkingDirectoryString should return the correct WorkingDirectory")
    @ParameterizedTest (name = "#{index} Create Ranorex Test object with [{0}]")
    @CsvSource ({
            "C:\\Jenkins Rx Plugin Temp, Test Exe.exe",
            "C:\\JenkinsRxPluginTemp, TestExe.exe"})
    public void GetWorkingDirectory_ValidInput_ValidWorkingDirectory(String WorkingDir, String TestExe) {
        try {
            RanorexTest t = new RanorexTest(WorkingDir, TestExe, "");
            assertEquals(WorkingDir, t.getWorkingDirectoryString());
        } catch (Exception e) {
           fail("An exception was thrown \r\n"+e.getMessage());
        }
    }

    //TODO Create Tests for ToString
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