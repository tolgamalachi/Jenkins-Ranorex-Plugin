/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ranorex.jenkinsranorexplugin.util;

import hudson.FilePath;

import java.io.File;
import java.security.InvalidParameterException;

/**
 * @author mstoegerer
 */
public abstract class FileUtil {

    /**
     * Translate the TestSuitefilename to the TestExecutionfilename
     *
     * @param TestSuiteFile The filepath for the Ranorex Test Suite file
     * @return The filepath for the Ranorex Test Exe file
     * @Throws InvalidParameterException if the Input is not valid
     */
    public static String getExecutableFromTestSuite(String TestSuiteFile) {
        String ExecutableFile;
        if (TestSuiteFile.contains(".rxtst")) {
            ExecutableFile = TestSuiteFile.replace(".rxtst", ".exe");
        } else if (TestSuiteFile.contains(".exe")) {
            ExecutableFile = TestSuiteFile;
        } else {
            throw new InvalidParameterException("Input '" + TestSuiteFile + "' is not a valid Test Suite File");
        }

        String[] splitPath = StringUtil.splitPath(ExecutableFile);
        return splitPath[splitPath.length - 1];
    }

    /**
     * Get the absolute path to the Ranorex Test Exe file
     *
     * @param jenkinsWorkspace     The current workspace for the Jenkins Job
     * @param rxTestExecutablePath The path to the Ranorex Test Exe
     * @return The directory in which the Ranorex Test Exe is located
     */
    public static FilePath getRanorexWorkingDirectory(FilePath jenkinsWorkspace, String rxTestExecutablePath) {
        File rxTestExe = new File(rxTestExecutablePath);
        //If the Test Suite Path is relative, append it to the Jenkins Workspace
        if (isAbsolutePath(rxTestExecutablePath)) {
            return new FilePath(new File(rxTestExe.getParent()));
        } else {
            return new FilePath(new File(jenkinsWorkspace.getRemote(), rxTestExe.getParent()));
        }
    }

    /**
     * Tests whether this abstract pathname is absolute.
     *
     * @param value Input path
     * @return true if and only if the file denoted by this abstract pathname
     * exists and is a directory; false otherwise
     */
    public static boolean isAbsolutePath(String value) {
        if (! StringUtil.isNullOrSpace(value)) {
            char[] chars = value.toCharArray();
            /*
             * we use this instead of file.isAbsolute() because it will provide false
             * negative return values if the master node is a unix based system. Since the
             * execution node must be a Windows system, this check should be ok.
             */

            return (chars[1] == ':' || value.startsWith("\\\\"));
        } else {
            return false;
        }
    }

    /**
     * Combines the current workspace with a relative path
     *
     * @param WorkSpace The current workspace
     * @param relPath   A relative path
     * @return Absolute path
     */
    public static String combinePath(String WorkSpace, String relPath) {
        //Remove '.' from the beginning at relPath
        if (relPath.charAt(0) == '.') {
            relPath = removeFirstCharacterOfString(relPath);
        }
        //Remove '\' from the beginning at relPath
        if (relPath.charAt(0) == '\\' && WorkSpace.charAt(WorkSpace.length() - 1) == '\\') {
            relPath = removeFirstCharacterOfString(relPath);
        }
        return (WorkSpace + (relPath.replace("/", File.separator)));
    }

    private static String removeFirstCharacterOfString(String value) {
        return value.substring(1);
    }

    /**
     * Tests if the given reportDirectory is Absolute,
     *
     * @param workSpace       The current Jenkins Job workspace
     * @param reportDirectory The Ranorex Report directory
     * @return Absolute Path to the ReportDirectory
     */
    public static String getAbsoluteReportDirectory(String workSpace, String reportDirectory) {
        String usedDirectory;

        if (! FileUtil.isAbsolutePath(reportDirectory)) {
            usedDirectory = FileUtil.combinePath(workSpace, reportDirectory);
        } else {
            usedDirectory = reportDirectory;
        }
        return usedDirectory;
    }


    /**
     * @param fileName The filename including the extension
     * @return The filename without extension
     */
    public static String removeFileExtension(String fileName) {
        if (StringUtil.isNullOrSpace(fileName)) {
            throw new InvalidParameterException("Filename cannot be null or empty");
        } else {
            String fileNameWithoutExtension;
            int position = fileName.lastIndexOf(".");
            if (position > 0) {
                fileNameWithoutExtension = fileName.substring(0, position);
                return fileNameWithoutExtension;
            }
            throw new InvalidParameterException("Filename does not have an extension");
        }
    }

    /**
     * Gets only the filename from a path.
     *
     * @param fullPath Path to File
     * @return Filename
     */
    public static String getFile(String fullPath) {
        if (! StringUtil.isNullOrSpace(fullPath)) {
            File f = new File(fullPath);
            return f.getName();
        }
        throw new InvalidParameterException("Path is not valid");
    }
}
