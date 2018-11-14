package com.ranorex.jenkinsranorexplugin.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.omg.CORBA.DynAnyPackage.Invalid;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.*;

class RanorexParameterTest {
    @DisplayName ("isValidFlag should return true if flag is in the white list")
    @ParameterizedTest (name = "#{index} [{0}] is a valid flag")
    @ValueSource (strings = {"param", "pa"})
    void isValid_ValidFlag_True(String flag) {
        boolean result = RanorexParameter.isValidFlag(flag);
        assertTrue(result);
    }

    @DisplayName ("isValidFlag should return false if flag is not in the white list")
    @ParameterizedTest (name = "#{index} [{0}] is not a valid flag")
    @ValueSource (strings = {
            "listconfigparams", "lcp",
            "reportfile", "rf",
            "zipreport", "zr",
            "junit", "ju",
            "listglobalparams", "lp",
            "listtestcaseparams", "ltcpa",
            "runconfig", "rc",
            "testrail", "truser", "trpass", "trrunid", "trrunname"})
    void isValid_IgnoredParam_False(String flag) {
        boolean result = RanorexParameter.isValidFlag(flag);
        assertFalse(result);
    }

    // Split param String
    @DisplayName ("trySplitArgument should throw an InvalidParameterException if the input is null or empty")
    @ParameterizedTest (name = "trySplitArgument [{0}]")
    @ValueSource (strings = {"null", "", " "})
    void splitParameterString_NullOrEmpty_ThrowsInvalidParameterException(String input) {
        Throwable exception;
        if ("null".equals(input)) {
            exception = assertThrows(InvalidParameterException.class, () -> RanorexParameter.trySplitArgument(null));
        } else {
            exception = assertThrows(InvalidParameterException.class, () -> RanorexParameter.trySplitArgument(input));
        }
        assertEquals("Cannot split empty string", exception.getMessage());
    }

    //FixMe: Method is not throwing an exception
    @Disabled
    @DisplayName ("trySplitArgument should throw an InvalidParameterException if the input is not valid")
    @ParameterizedTest (name = "trySplitArgument [{0}]")
    @ValueSource (strings = {"/banana:paramName=test", "/banana:pa=", "/pa:paramNametest", "paramNametest"})
    void trySplitParameterString_InvalidInput_ThrowsInvalidParameterException(String input) {
        Throwable exception = assertThrows(InvalidParameterException.class, () -> RanorexParameter.trySplitArgument(input));
        assertEquals("Parameter '" + input + "' is not valid", exception.getMessage());
    }

    @DisplayName ("trySplitArgument should return the correct split argument")
    @ParameterizedTest (name = "trySplitArgument [{0}]")
    @CsvSource ({"/pa:paramName=test, 3, pa, paramName, test",
            "/param:paramName=test, 3, param, paramName, test",
            "paramName=test, 3, pa, paramName, test"})
    void splitParameterString_ValidParameterString_ValidParameter(String input, int expectedAmountOfParts, String expectedFlag, String expectedName, String expectedValue) {
        String[] splitParam = RanorexParameter.trySplitArgument(input);
        assertAll("Correct Split",
                () -> assertEquals(expectedAmountOfParts, splitParam.length),
                () -> assertEquals(expectedFlag, splitParam[0]),
                () -> assertEquals(expectedName, splitParam[1]),
                () -> assertEquals(expectedValue, splitParam[2])
        );
    }


    // Parsing Input String Constructor
    @DisplayName ("Constructor should create a valid object if the input is correct")
    @ParameterizedTest (name = "Create Parameter object with [{0}]")
    @CsvSource ({
            "/pa:TestName=TestValue, pa, TestName, TestValue",
            "TestName=TestValue, pa, TestName, TestValue",
            "/param:TestName=TestValue, param, TestName, TestValue"})
    void Constructor_ValidInputString_ValidRanorexParameter(String input, String expectedFlag, String expectedName, String expectedValue) {
        RanorexParameter valid = new RanorexParameter(input);
        assertAll("Create Object", () -> assertEquals(valid.getFlag(), expectedFlag),
                () -> assertEquals(valid.getName(), expectedName),
                () -> assertEquals(valid.getValue(), expectedValue));
    }

    @DisplayName ("Constructor should throw an InvalidParemterException if the input is not valid")
    @ParameterizedTest (name = "Create Parameter object with [{0}]")
    @ValueSource (strings = {"/pa:TestNameTestValue",
            "/param:TestNameTestValue"})
    void Constructor_InvalidInput_ThrowsInvalidParameterException(String input) {
        Throwable exception = assertThrows(InvalidParameterException.class, () -> new RanorexParameter(input));
        assertEquals("'" + input + "' is not a valid Parameter", exception.getMessage());
    }


    @DisplayName ("tryExtractFlag should throw an InvalidParemterException if the input is not valid")
    @ParameterizedTest (name = "Try extract flag from [{0}]")
    @ValueSource (strings = {"/pa", "/param"})
    void extractFlag_InvalidInput_ThrowsInvalidParameterException(String input) {

        Throwable exception = assertThrows(InvalidParameterException.class, () -> RanorexParameter.tryExtractFlag(input));
        assertEquals("Parameter '" + input + "' does not contain a separator!", exception.getMessage());
    }

    @DisplayName ("tryExtractFlag should return the correct flag")
    @ParameterizedTest (name = "Try extract flag from [{0}]")
    @CsvSource ({
            "/param:Test=test, param",
            "param:Test=test, param",
            "/pa:Test=test, pa",
            "pa:Test=test, pa"})
    void extractFlag_ValidInput_CorrectFlag(String input, String expectedFlag) {
        String flag = RanorexParameter.tryExtractFlag(input);
        assertEquals(expectedFlag, flag);
    }

    @DisplayName ("isValid should return true if the input is valid")
    @ParameterizedTest (name = "[{0}] is valid")
    @ValueSource (strings = {"/param:Test name = value 1", "/pa:Test name = value 1", "Test name = value 1"})
    void isValid_ValidParam_True(String input) {
        assertTrue(RanorexParameter.isValid(input));
    }

    @DisplayName ("isValid should return false if the input is not valid")
    @ParameterizedTest (name = "[{0}] is not valid")
    @ValueSource (strings = {"/zr", "/param:test", "/param:test=", "/testArgument:TestName=TestValue"})
    void isValid_InvalidInput_False(String input) {
        assertFalse(RanorexParameter.isValid(input));
    }
}