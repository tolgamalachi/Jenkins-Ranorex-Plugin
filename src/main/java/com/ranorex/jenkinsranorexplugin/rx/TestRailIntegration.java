package com.ranorex.jenkinsranorexplugin.rx;

import com.ranorex.jenkinsranorexplugin.util.StringUtil;

public class TestRailIntegration {

    private String UserName;
    private String Password;
    private String RunId;
    private String RunName;

    public TestRailIntegration(String UserName, String Password, String RunId, String RunName) {

        this.UserName = UserName;
        this.Password = Password;
        this.RunId = RunId;
        this.RunName = RunName;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("\tUsername:\t").append(this.UserName).append("\n");
        sb.append("\tPassword:\t").append("*****").append("\n");
        if (! StringUtil.isNullOrSpace(this.RunId)) {
            sb.append("\tRun Id:\t\t").append(this.RunId).append("\n");
        }
        if (! StringUtil.isNullOrSpace(this.RunName)) {
            sb.append("\tRun Name:\t").append(this.RunName).append("\n");
        }
        return sb.toString();
    }

    //Getter and Setter

    public String getUserName() {
        return UserName;
    }

    public String getPassword() {
        return Password;
    }

    public String getRunId() {
        return RunId;
    }

    public String getRunName() {
        return RunName;
    }
}
