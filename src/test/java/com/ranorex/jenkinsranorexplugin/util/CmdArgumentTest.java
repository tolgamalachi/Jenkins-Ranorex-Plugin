package com.ranorex.jenkinsranorexplugin.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.*;

class CmdArgumentTest {
    //Constuctor
    @DisplayName ("Constructor should throw an IllegalArgumentException if input is null, empty or space")
    @ParameterizedTest (name = "#{index} Create CmdArgument object with [{arguments}]")
    @ValueSource (strings = {"", "null", " "})
    void Constructor_InvalidInput_ShouldThrowException(String input) {
        try {
            if ("null".equals(input)) {
                CmdArgument dummy = new CmdArgument(null);
            } else {
                CmdArgument dummy = new CmdArgument(input);
            }
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertEquals("Argument must be not null or empty!", e.getMessage());
        }
    }

    @DisplayName ("Constructor should throw an InvalidParameterException if flags are on the ignore list")
    @ParameterizedTest (name = "#{index} Create CmdArgument object with [{0}]")
    @ValueSource (strings = {"/param:test", "param:test", "param", "/param", "/param:test=value", "param:test=value", "/pa", "pa"})
    void Constructor_IgnoredArgumentFlag_ThrowsException(String input) {
        try {
            CmdArgument cmdArg = new CmdArgument(input);
            assertTrue(false);
        } catch (InvalidParameterException e) {
            assertEquals("Argument '" + input + "' will be ignored", e.getMessage());
        }
    }

    //getFlag
    @DisplayName ("getFlag should return the correct Parameter flag")
    @ParameterizedTest (name = "#{index} getFlag on CmdArgument object with [{0}]")
    @CsvSource ({"banana, banana", "/apple, apple", "TestiMcTestTest, TestiMcTestTest"})
    void Constructor_ValidFlagWithoutName_CorrectFlag(String input, String output) {
        CmdArgument cmdArg = new CmdArgument(input);
        assertEquals(output, cmdArg.getFlag());
    }

    //getFlag & getName
    @DisplayName ("getFlag and getName should return the correct Parameter flag and name")
    @ParameterizedTest (name = "#{index} getFlag and getName on CmdArgument object with [{0}]")
    @CsvSource ({"banana:test, banana, test", "/apple:Value, apple, Value", "TestiMcTestTest:empty, TestiMcTestTest, empty"})
    void Constructor_ValidFlagAndName_CorrectFlagAndName(String input, String expectedFlag, String expectedName) {
        CmdArgument cmdArg = new CmdArgument(input);
        assertEquals(expectedFlag, cmdArg.getFlag());
        assertEquals(expectedName, cmdArg.getName());
    }


    @DisplayName ("getFlag and getName Should return the correct Parameter flag with name and value")
    @ParameterizedTest (name = "#{index} Create CmdArgument object with [{0}]")
    @CsvSource ({"/banana:test=value, banana, test, value",
            "banana:test=value, banana, test, value",
            "TestiMcTestTest:empty, TestiMcTestTest, empty, null"})
    void Constructor_ValidArgumentFlagWithNameAndValue_CorrectFlagAndName(String input, String expectedFlag, String expectedName, String expectedValue) {
        CmdArgument cmdArg = new CmdArgument(input);
        assertEquals(expectedFlag, cmdArg.getFlag());
        assertEquals(expectedName, cmdArg.getName());
        if ("null".equals(expectedValue)) {
            assertEquals(null, cmdArg.getValue());
        } else {
            assertEquals(expectedValue, cmdArg.getValue());
        }
    }


    ///isIgnored
    @DisplayName ("isIgnored should throw an IllegalArgumentException if Input is null, empty or space")
    @ParameterizedTest (name = "#{index} isIgnored with input: [{0}]")
    @ValueSource (strings = {"", "null", " "})
    void isIgnored_InvalidInput_ThrowsIllegalArgumentException(String input) {
        try {
            if ("null".equals(input)) {
                CmdArgument.isIgnored(null);
            } else {
                CmdArgument.isIgnored(input);
            }
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertEquals("Argument must be not null or empty!", e.getMessage());
        }
    }

    @DisplayName ("isIgnored should return true if flag is in ignore list")
    @ParameterizedTest (name = "#{index} isIgnored with input: [{0}]")
    @ValueSource (strings = {"param", "pa",
            "listconfigparams", "lcp",
            "reportfile", "rf",
            "zipreport", "zr",
            "junit", "ju",
            "listglobalparams", "lp",
            "listtestcaseparams", "ltcpa",
            "runconfig", "rc",
            "testrail", "truser", "trpass", "trrunid", "trrunname"
    })
    void isIgnored_IgnoredParam_True(String input) {
        boolean result = CmdArgument.isIgnored(input);
        assertTrue(result);
    }

    @DisplayName ("isIgnored should return false if flag is not in ignore list")
    @ParameterizedTest (name = "#{index} isIgnored with input: [{0}]")
    @ValueSource (strings = {"banana", "/banana:", "/banana:name", "/banana:name=", "/banana:Name=Value",
            "ep", "endpointconfig",
            "epc", "endpointconfigfilepath", "epcfp",
            "reportlevel", "rl",
            "testsuite", "ts",
            "module", "mo",
            "testcaseparam", "testcontainerparam", "tcpa",
            "runlabel",
            "rul", "testcasedatarange",
            "testcontainerdatarange", "tcdr"})
    void isIgnored_ValidParams_False(String input) {
        boolean result = CmdArgument.isIgnored(input);
        assertFalse(result);
    }

    ///////////trySplitArgument
    @DisplayName ("trySplitArgument should throw an IllegalArgumentException if input is null, empty or space")
    @ParameterizedTest (name = "#{index} trySplitArgument with input [{arguments}]")
    @ValueSource (strings = {"", "null", " "})
    void trySplitArgument_InvalidInput_ShouldThrowException(String input) {
        try {
            if ("null".equals(input)) {
                CmdArgument.trySplitArgument(null);
            } else {
                CmdArgument.trySplitArgument(input);
            }
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot split empty string", e.getMessage());
        }
    }

    @DisplayName ("trySplitArgument should split argument successfully")
    @ParameterizedTest (name = "#{index} trySplitArgument with input [{0}]")
    @CsvSource ({"/rul:MyRunLabel, 2, rul, MyRunLabel, ",
            "/testcaseparam:MyParam=MyValue, 3, testcaseparam, MyParam, MyValue"})
    void splitArgumentString_CorrectFlagWithName_SplitArguments(String input, int expectedLength, String expectedFlag, String expectedName, String expectedValue) {
        String[] splitArgs = CmdArgument.trySplitArgument(input);
        assertEquals(expectedLength, splitArgs.length);
        assertEquals(expectedFlag, splitArgs[0]);
        assertEquals(expectedName, splitArgs[1]);
        if (expectedLength == 3) {
            assertEquals(expectedValue, splitArgs[2]);
        }
    }

    @DisplayName ("trySplitargument should throw an InvalidParameterException if the input is not valid")
    @ParameterizedTest (name = "#{index} trySplitArgument with input [{0}]")
    @ValueSource (strings = {"/testcaseparam:MyParam=", "/testcaseparam:"})
    void splitArgumentString_InvalidInput_ThrowsInvalidParameterException(String input) {
        try {
            CmdArgument.trySplitArgument(input);
            assertTrue(false);
        } catch (InvalidParameterException e) {
            assertEquals("Name or Value must not be null or empty", e.getMessage());
        }
    }

    ///////////trim
    @DisplayName ("trim should trim the parts of an argument correctly")
    @ParameterizedTest (name = "#{index} trim with input [{0}]")
    @CsvSource ({"   /tcdr : testcase = 25    , tcdr, testcase, 25"})
    //FixMe: @CsvSource automatically removes heading and trailing spaces, but since there are also spaces between : and =, the test should be OK
    void trim_ValidArgument_TrimmedArgument(String input, String expectedFlag, String expectedName, String expectedValue) {
        System.out.println(input);
        CmdArgument cmdarg = new CmdArgument(input);
        cmdarg.trim();
        assertEquals(expectedFlag, cmdarg.getFlag());
        assertEquals(expectedName, cmdarg.getName());
        assertEquals(expectedValue, cmdarg.getValue());

    }

    ////TryExtractFlag
    @DisplayName ("tryExctractFlag should throw an IllegalArgumentException if input is null, empty or space")
    @ParameterizedTest (name = "#{index} tryExctractFlag with input [{arguments}]")
    @ValueSource (strings = {"", "null", " "})
    void tryExtractFlag_InvalidInput_ThrowsIllegalArgumentException(String input) {
        try {
            if ("null".equals(input)) {
                CmdArgument.tryExtractFlag(null);
            } else {
                CmdArgument.tryExtractFlag(input);
            }
            assertTrue(false);

        } catch (IllegalArgumentException e) {
            assertEquals("Argument must not be null or empty", e.getMessage());
        }
    }

    @DisplayName ("tryExctractFlag should extract flag successfully")
    @ParameterizedTest (name = "#{index} tryExctractFlag with input [{0}] expected output: [{1}]")
    @CsvSource ({"/ValidFlag, ValidFlag",
            "ValidFlag, ValidFlag",
            "/ValidFlag:, ValidFlag",
            "ValidFlag:, ValidFlag",
            "/ValidFlag:Test, ValidFlag",
            "/ValidFlag:Test=, ValidFlag",
            "/ValidFlag:Test=Value, ValidFlag"})
    void tryExtractFlag_ValidInput_CorrectExtractedFlag(String input, String expectedOutput) {
        String actualOutput = CmdArgument.tryExtractFlag(input);
        assertEquals(expectedOutput, actualOutput);
    }

    ///toString
    @Test
    void toString_ValidInput_ValidString() {
        CmdArgument cmdArg = new CmdArgument("/flag : name = value ");
        assertEquals("/flag : name = value ", cmdArg.toString());
    }
}