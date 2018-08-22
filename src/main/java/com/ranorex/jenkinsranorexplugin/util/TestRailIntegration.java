package com.ranorex.jenkinsranorexplugin.util;

public class TestRailIntegration {
    private static Boolean useIntegration;
    private static String TestRailUserName;
    private static String TestRailPassword;
    private static String TestRailRunId;
    private static String TestRailRunName;


    public static Boolean getUseIntegration() {
        return useIntegration;
    }

    public static void setUseIntegration(Boolean useIntegration) {
        TestRailIntegration.useIntegration = useIntegration;
    }

    public static String getTestRailUserName() {
        return TestRailUserName;
    }

    public static void setTestRailUserName(String testRailUserName) {
        TestRailUserName = testRailUserName;
    }

    public static String getTestRailPassword() {
        return TestRailPassword;
    }

    public static void setTestRailPassword(String testRailPassword) {
        TestRailPassword = testRailPassword;
    }

    public static String getTestRailRunId() {
        return TestRailRunId;
    }

    public static void setTestRailRunId(String testRailRunId) {
        TestRailRunId = testRailRunId;
    }

    public static String getTestRailRunName() {
        return TestRailRunName;
    }

    public static void setTestRailRunName(String testRailRunName) {
        TestRailRunName = testRailRunName;
    }
}
