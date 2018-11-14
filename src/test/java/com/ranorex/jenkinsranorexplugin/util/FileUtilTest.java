package com.ranorex.jenkinsranorexplugin.util;

import hudson.FilePath;
import hudson.model.UpdateCenter;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilTest {
    private final String _testSuiteFileWithoutSpace = "TestSuite.rxtst";
    private final String _testSuiteFileWithSpace = "Test Suite.rxtst";
    private final String _testExeFileWithoutSpace = "TestSuite.exe";
    private final String _testExeFileWithSpace = "Test Suite.exe";
    private final String _jenkinsWorkSpaceWithoutSpace = "C:\\Users\\user\\.jenkins\\workspace\\";
    private final String _jenkinsWorkSpaceWithSpace = "C:\\Users\\us er\\.jenkins\\workspace\\";
    private final String _jenkinsJobNameWithoutSpace = "TestJobWithOutSpace";
    private final String _jenkinsJobNameWithSpace = "Test Job with Space";
    private final String _relativeDirectoryWithDotWithoutSpace = ".\\bin\\Debug\\";
    private final String _relativeDirectoryWithDotWithSpace = ".\\bin\\De b ug\\";
    private final String _relativeDirectoryWithouDotWithoutSpace = "\\bin\\Debug\\";
    private final String _relativeDirectoryWithoutDotWithSpace = "\\bin\\D e b u g\\";

    private final String _absoluteTestSuiteDirectoryOutsideJenkinsWithoutSpace = "C:\\Temp\\";
    private final String _absoluteTestSuiteDirectoryOutsideJenkinsWithSpace = "C:\\Temp Directory\\";

    /**
     * Combined Values
     **/
    private final String _absoluteJenkinsJobWithoutSpace = _jenkinsWorkSpaceWithoutSpace + _jenkinsJobNameWithoutSpace; // "C:\\Users\\user\\.jenkins\\workspace\\TestJobWithOutSpace"
    private final String _absoluteJenkinsJobWithSpace = _jenkinsWorkSpaceWithSpace + _jenkinsJobNameWithSpace; //"C:\\Users\\us er\\.jenkins\\workspace\\Test Job with Space"


    @DisplayName ("GetExecutableFromTestSuite should return correct .exe file")
    @ParameterizedTest (name = "#{index} Get Exe from [{0}]")
    @CsvSource ({"TestSuite.rxtst, TestSuite.exe",
            "Test Suite.rxtst, Test Suite.exe",
            "TestSuite.exe, TestSuite.exe",
            "Test Suite.exe,Test Suite.exe"})
    void GetExecutableFromTestSuite_ValidInput_CorrectExe(String input, String expectedOutput) {
        String actualResult = FileUtil.getExecutableFromTestSuite(input);
        assertEquals(expectedOutput, actualResult);
    }

    @DisplayName ("GetExecutableFromTestSuite should throw an exception if the input is not correct")
    @ParameterizedTest (name = "#{index} Get Exe from [{0}]")
    @ValueSource (strings = {"TestSuite.xyz", "Test Suite.xyz"})
    void GetExecutableFromTestSuite_InvalidInput_ThrowsException(String input) {
        String expectedResult = "Input '" + input + "' is not a valid Test Suite File";
        Throwable exception = assertThrows(InvalidParameterException.class, () -> FileUtil.getExecutableFromTestSuite(input));
        assertEquals(expectedResult, exception.getMessage());
    }

    @DisplayName ("GetRanorexWorkingDirectory should return correct directory")
    @ParameterizedTest (name = "#{index} Get Ranorex Working Directory from [{0}, {1}]")
    @CsvSource ({
            "C:\\Users\\user\\.jenkins\\workspace\\TestJobWithOutSpace, .\\bin\\Debug\\TestExe.exe,C:\\Users\\user\\.jenkins\\workspace\\TestJobWithOutSpace\\bin\\Debug\\",
            "C:\\Users\\user\\.jenkins\\workspace\\Test Job With Space, .\\bin\\De b ug\\Test Exe.exe,C:\\Users\\user\\.jenkins\\workspace\\Test Job With Space\\bin\\De b ug\\",
            "C:\\Users\\user\\.jenkins\\workspace\\TestJobWithOutSpace, C:\\AbsoluteWithoutSpace\\bin\\Debug\\TestExe.exe,C:\\AbsoluteWithoutSpace\\bin\\Debug\\",
            "C:\\Users\\user\\.jenkins\\workspace\\Test Job With Space, C:\\Absolute With Space\\bin\\De bug\\Test Exe.exe,C:\\Absolute With Space\\bin\\De bug\\",
    })
    void GetRanorexWorkingDirectory_RelativeTestSuitePathWithoutSpace_AbsoluteTestSuitePathWithoutSpace(String JenkinsJobDirectoryInput, String relativeTestExePath, String expectedTestSuitePath) {
        FilePath JenkinsJobDirectory = new FilePath(new File(JenkinsJobDirectoryInput));
        FilePath expectedResult = new FilePath(new File(expectedTestSuitePath));
        FilePath actualResult = FileUtil.getRanorexWorkingDirectory(JenkinsJobDirectory, relativeTestExePath);
        assertEquals(expectedResult, actualResult);
    }

    @DisplayName ("IsAbsolutePath should return true if Input is an Absolute Path")
    @ParameterizedTest (name = "#{index} [{0}] is an absolute path")
    @ValueSource (strings = {"C:\\Temp\\", "C:\\ T e m p\\"})
    void IsAbsolutePath_AbsolutePath_True(String input) {
        boolean actualResult = FileUtil.isAbsolutePath(input);
        assertTrue(actualResult);
    }

    @DisplayName ("IsAbsolutePath should return false if Input is not an Absolute Path")
    @ParameterizedTest (name = "#{index} [{0}] is not an absolute path")
    @ValueSource (strings = {"./Test/banana.exe", "", ".\\bin\\Debug\\", ".\\bin\\De b ug\\"})
    void isAbsolutePath_RelativePath_False(String input) {
        boolean result = FileUtil.isAbsolutePath(input);
        assertFalse(result);
    }

    @DisplayName ("CombinePath should return the correct combined path")
    @ParameterizedTest (name = "#{index} Combine [{0}] with [{1}]")
    @CsvSource ({
            "C:\\Temp, .\\bin\\Debug\\, C:\\Temp\\bin\\Debug\\",
            "C:\\Temp\\, \\bin\\Debug\\, C:\\Temp\\bin\\Debug\\",
            "C:\\Temp\\, .\\bin\\Debug\\, C:\\Temp\\bin\\Debug\\",
            "C:\\Temp Directory\\, bin\\De b ug\\, C:\\Temp Directory\\bin\\De b ug\\",
            "C:\\Temp Directory\\, .\\bin\\De b ug\\, C:\\Temp Directory\\bin\\De b ug\\,",
            "C:\\Temp Directory\\, \\bin\\D e b u g\\, C:\\Temp Directory\\bin\\D e b u g\\"

    })
    void CombinePath_ValidInput_AbsolutePath(String BasePath, String relativePath, String expectedCombinedPath) {
        String actualCombinedPath = FileUtil.combinePath(BasePath, relativePath);
        assertEquals(expectedCombinedPath, actualCombinedPath);
    }

    @DisplayName ("GetAbsoluteReportDirectory should return the correct Absolute Directory")
    @ParameterizedTest (name = "#{index} Combine [{0}] with [{1}]")
    @CsvSource ({
            "C:\\Temp\\, C:\\Temp\\, C:\\Temp\\",
            "C:\\Temp Directory\\, C:\\Temp Directory\\, C:\\Temp Directory\\",
            "C:\\Temp Directory, .\\Test\\Banana, C:\\Temp Directory\\Test\\Banana",
            "C:\\Temp Directory, .\\Te st\\Ban ana, C:\\Temp Directory\\Te st\\Ban ana",
            "\\bin\\D e b u g\\, C:\\Temp Directory\\,  C:\\Temp Directory\\",})
    void GetAbsoluteReportDirectory_ValidInput_AbsolutePath(String Basepath, String ReportDirectory, String expectedReportDirectory) {
        String actualReportDirectory = FileUtil.getAbsoluteReportDirectory(Basepath, ReportDirectory);
        assertEquals(expectedReportDirectory, actualReportDirectory);
    }

    @DisplayName ("IgnoreFileExtension should return the correct Filename without File extension")
    @ParameterizedTest (name = "#{index} Remove extension from [{0}]")
    @CsvSource ({
            "RanorexReport.rxzlog, RanorexReport",
            "Ranorex Rep ort.rxzlog,Ranorex Rep ort"})
    void IgnoreFileExtension_ValidFileNameWithoutSpaceWithZippedExtension_ValidFileNameWithoutSpaceWithoutExtension(String inputFileName, String expectedFileName) {
        String actualFileName = FileUtil.removeFileExtension(inputFileName);
        assertEquals(expectedFileName, actualFileName);
    }

    @DisplayName ("IgnoreFileExtension should throw an exception if the input is null or empty")
    @ParameterizedTest (name = "#{index} ignoreExtension from [{0}]")
    @ValueSource (strings = {"", "null", " "})
    void IgnoreFileExtension_NullInput_ThrowsException(String input) {
        Throwable except;
        if ("null".equals(input)) {
            except = assertThrows(InvalidParameterException.class, () -> FileUtil.removeFileExtension(null));
        } else {
            except = assertThrows(InvalidParameterException.class, () -> FileUtil.removeFileExtension(input));
        }
        Assert.assertEquals("Filename cannot be null or empty", except.getMessage());
    }

    @DisplayName ("IgnoreFileExtension should throw an exception if the file does not have an extension")
    @ParameterizedTest (name = "#{index} ignoreExtension from [{0}]")
    @ValueSource (strings = {"Report", "Re Po rt"})
    void IgnoreFileExtension_InvalidInput_ThrowsException(String input) {
        Throwable except = assertThrows(InvalidParameterException.class, () -> FileUtil.removeFileExtension(input));

        Assert.assertEquals("Filename does not have an extension", except.getMessage());
    }


    @DisplayName ("GetFile should return the correct filename")
    @ParameterizedTest (name = "#{index} Get filename from [{0}]")
    @CsvSource ({
            "C:\\Test\\Test.rxtst, Test.rxtst",
            "C:\\Te s t\\Te s t.rxtst, Te s t.rxtst",
            "Te s t.rxtst, Te s t.rxtst",
            "Test.rxtst, Test.rxtst",
            "C:\\Test\\Test, Test"})
    void getFile_fullPath_ValidFilename(String input, String expectedFileName) {
        String actualFileName = FileUtil.getFile(input);
        assertEquals(expectedFileName, actualFileName);
    }

    @DisplayName ("GetFile should throw an exception if the input is null or empty")
    @ParameterizedTest (name = "#{index} GetFile from [{0}]")
    @ValueSource (strings = {"", "null", " "})
    void getFile_null_ThrowsException(String input) {
        Throwable exception;
        if ("null".equals(input)) {
            exception = assertThrows(InvalidParameterException.class, () -> FileUtil.getFile(null));
        } else {
            exception = assertThrows(InvalidParameterException.class, () -> FileUtil.getFile(input));
        }
        assertEquals("Path is not valid", exception.getMessage());
    }
}
