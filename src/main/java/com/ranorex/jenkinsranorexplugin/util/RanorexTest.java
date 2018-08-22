package com.ranorex.jenkinsranorexplugin.util;

public class RanorexTest {
    public static String getWorkingDirectory() {
        return WorkingDirectory;
    }

    public static void setWorkingDirectory(String workingDirectory) {
        WorkingDirectory = workingDirectory;
    }

    public static String getRanorexTestExecutable() {
        return RanorexTestExecutable;
    }

    public static void setRanorexTestExecutable(String ranorexTestExecutable) {
        RanorexTestExecutable = ranorexTestExecutable;
    }

    public static String getRanorexTestSuite() {
        return RanorexTestSuite;
    }

    public static void setRanorexTestSuite(String ranorexTestSuite) {
        RanorexTestSuite = ranorexTestSuite;
    }

    public static String getRanorexRunConfiguration() {
        return RanorexRunConfiguration;
    }

    public static void setRanorexRunConfiguration(String ranorexRunConfiguration) {
        RanorexRunConfiguration = ranorexRunConfiguration;
    }

    public static RanorexReport getRxReport() {
        return RxReport;
    }

    public static void setRxReport(RanorexReport rxReport) {
        RxReport = rxReport;
    }

    public static TestRailIntegration getTestRail() {
        return TestRail;
    }

    public static void setTestRail(TestRailIntegration testRail) {
        TestRail = testRail;
    }

    private static String WorkingDirectory;
    private static String RanorexTestExecutable;
    private static String RanorexTestSuite;
    private static String RanorexRunConfiguration;
    private static RanorexReport RxReport;
    private static TestRailIntegration TestRail;


}
