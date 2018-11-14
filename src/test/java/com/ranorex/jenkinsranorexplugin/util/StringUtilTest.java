package com.ranorex.jenkinsranorexplugin.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.InvalidParameterException;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringUtilTest {
    private final String _textWithQuotes = "\"This is a sample text\"";
    private final String _textWithBackslash = "This is a sample text\\";


    @DisplayName ("AppendQuote should return a text with quotes")
    @ParameterizedTest (name = "#{index} Append Quotes to [{0}]")
    @CsvSource ({"This is a sample text, \"This is a sample text\"",
            "\"This is a sample text\", \"This is a sample text\""})
    public void AppendQuote_ValidInput_TextWithQuotes(String input, String expectedOutput) {
        String actualOutput = StringUtil.appendQuote(input);
        assertEquals(expectedOutput, actualOutput);
    }

    @DisplayName ("isNullOrSpace should return true if a string is null or space")
    @ParameterizedTest (name = "#{index} [{0}] is null or space ")
    @ValueSource (strings = {"", " ", "null"})
    public void IsNullOrSpace_NullOrSpace_True(String input) {
        if ("null".equals(input)) {
            assertTrue(StringUtil.isNullOrSpace(null));
        } else {
            assertTrue(StringUtil.isNullOrSpace(input));
        }
    }

    @DisplayName ("isNullOrSpace should return false if a string is not null or space")
    @ParameterizedTest (name = "#{index} [{0}] is not null or space ")
    @ValueSource (strings = {"Test", "Banana", "123"})
    public void IsNullOrSpace_Valid_false(String input) {
        assertFalse(StringUtil.isNullOrSpace(input));
    }

    @DisplayName ("AppendBackslash should return a text with Backslash")
    @ParameterizedTest (name = "#{index} Append Backslash to [{0}]")
    @CsvSource ({"This is a sample text, This is a sample text\\",
            "This is a sample text\\, This is a sample text\\"})
    public void AppendBackslash_Text_TextWithBackslash(String input, String expectedOutput) {
        String actualOutput = StringUtil.appendBackslash(input);
        assertEquals(expectedOutput, actualOutput);
    }

    @DisplayName ("Splitpath should return an array of directories")
    @ParameterizedTest (name = "#{index} Split [{0}]")
    @CsvSource ({
            "C:\\Temp\\Test Directory\\Banana\\, C:_Temp_Test Directory_Banana",
            "C:/Temp/Test Directory/Banana/, C:_Temp_Test Directory_Banana"})
    public void SplitPath_Path_ArrayOfDirectories(String input, String expectedOutput) {
        String[] splitPath = expectedOutput.split("_");
        String[] actualOutput = StringUtil.splitPath(input);
        assertArrayEquals(splitPath, actualOutput);
    }

    //Remove heading slash
    @DisplayName ("RemoveHeadingSlash should throw an InvalidParameterException if the input is null or empty")
    @ParameterizedTest (name = "#{index} [{0}] causes an InvalidParameterException")
    @ValueSource (strings = {"null", "", " "})
    public void RemoveHeadingSlash_NullOrEmpty_ThrowsInvalidParameterException(String input) {
        Throwable exception;
        if ("null".equals(input)) {
            exception = assertThrows(InvalidParameterException.class, () -> StringUtil.removeHeadingSlash(null));
        } else {
            exception = assertThrows(InvalidParameterException.class, () -> StringUtil.removeHeadingSlash(input));
        }
        assertEquals("Argument is empty", exception.getMessage());
    }

    @DisplayName ("RemoveHeadingSlash should remove the heading slash if there is one")
    @ParameterizedTest (name = "#{index} Remove heading slash from [{0}]")
    @CsvSource ({
            "/bananaRama, bananaRama",
            "bananaRama, bananaRama"})
    public void RemoveHeadingSlash_ValidInput_ValidOutputWithoutSlash(String input, String expectedOutput) {
        String actualResult = StringUtil.removeHeadingSlash(input);
        assertEquals(expectedOutput, actualResult);
    }
    @DisplayName ("SplitBy should return a list of split strings")
    @ParameterizedTest (name = "#{index} Split [{0}]")
    @CsvSource(value = "This,is,Sparta;Test\tBanana_This-is-Sparta-Test-Banana", delimiter = '_')
    public void splitBy_ValidInput_ListOfSplitItems(String input, String expectedOutput) {
        String[] expectedSplit = expectedOutput.split("-");
        List<String> actualSplit = StringUtil.splitBy(input, ",;\t");
        assertEquals(5, actualSplit.size());
        assertEquals(expectedSplit[0], actualSplit.get(0));
        assertEquals(expectedSplit[1], actualSplit.get(1));
        assertEquals(expectedSplit[2], actualSplit.get(2));
        assertEquals(expectedSplit[3], actualSplit.get(3));
        assertEquals(expectedSplit[4], actualSplit.get(4));
    }
}
