package com.ranorex.jenkinsranorexplugin.rx;

import com.ranorex.jenkinsranorexplugin.util.CmdArgument;
import com.ranorex.jenkinsranorexplugin.util.RanorexParameter;
import com.ranorex.jenkinsranorexplugin.util.StringUtil;
import hudson.util.ArgumentListBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class RanorexTest {

    private String WorkingDirectory;
    private File WorkingDir;
    private String TestExecutable;
    private File TestExe;
    private String TestSuite;
    private File TestSuiteFile;
    private String RanorexRunConfiguration;
    private RanorexReport RxReport;
    private Boolean UseTestRail;
    private TestRailIntegration TestRail;
    private List<RanorexParameter> RanorexParameters;
    private List<CmdArgument> CommandLineArguments;

    public RanorexTest(String WorkingDirectory, String TestExecutable, String TestSuite) throws FileNotFoundException {
        this.WorkingDirectory = WorkingDirectory;

        this.WorkingDir = new File(WorkingDirectory);

        if (! WorkingDir.isDirectory() && ! StringUtil.isNullOrSpace(WorkingDir.getPath())) {
            throw new InvalidParameterException("'" + WorkingDir.getPath() + "' is not a valid directory path");
        }
        this.TestExecutable = TestExecutable;

        this.TestExe = new File(TestExecutable);
        if (! TestExe.exists()) {
            throw new FileNotFoundException("File '" + TestExe.getName() + "' does not exist");
        }

        this.TestSuite = TestSuite;
        this.TestSuiteFile = new File(TestExe.getPath(), TestSuite);
        if (! this.TestSuiteFile.exists()) {
            throw new FileNotFoundException("File '" + TestSuiteFile.getName() + "' does not exist");
        }
        this.UseTestRail = false;

        this.RanorexParameters = new ArrayList<>();
        this.CommandLineArguments = new ArrayList<>();
    }


    public void addGlobalParameter(RanorexParameter param) {
        this.RanorexParameters.add(param);
    }

    public void addCommandLineArgument(CmdArgument arg) {
        this.CommandLineArguments.add(arg);
    }


    public String getWorkingDirectoryString() {
        return this.WorkingDirectory;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Working Directory:\t").append(this.WorkingDirectory).append("\n");
        sb.append("Test Executable File:\t").append(this.TestExecutable).append("\n");

        sb.append("Test Suite File:\t");
        if (! StringUtil.isNullOrSpace(this.TestSuite)) {
            sb.append(this.TestSuite).append("\n");
        } else {
            sb.append("No Test Suite File selected!\n");
        }

        sb.append("RunConfiguration:\t");
        if (! StringUtil.isNullOrSpace(this.RanorexRunConfiguration)) {
            sb.append(this.RanorexRunConfiguration).append("\n");
        } else {
            sb.append("No Runconfiguration entered\n");
        }

        sb.append(RxReport);

        sb.append("Sync with TestRail:\t").append(this.UseTestRail).append("\n");
        if (this.UseTestRail) {
            sb.append(TestRail);
        }
        sb.append("\nGlobal Parameters:\n");
        if (! this.RanorexParameters.isEmpty()) {
            for (RanorexParameter p : this.RanorexParameters) {
                sb.append("\t").append(p.toString()).append("\n");
            }
        } else {
            sb.append("\tNo Parameters entered\n");
        }

        sb.append("\nCommand Line Arguments:\n");
        if (! this.CommandLineArguments.isEmpty()) {
            for (CmdArgument a : this.CommandLineArguments) {
                sb.append("\t").append(a.toString()).append("\n");
            }
        } else {
            sb.append("\tNo Command Line Arguments entered\n");
        }
        return sb.toString();
    }

    public ArgumentListBuilder toExecutionArguments() {
        ArgumentListBuilder alb = new ArgumentListBuilder("cmd.exe", "/C");

        //Test Exe
        if (! StringUtil.isNullOrSpace(this.TestExecutable))
            alb.add(this.TestExecutable);
        //Test Suite
        if (! StringUtil.isNullOrSpace(this.TestSuite))
            alb.add("/ts:" + this.TestSuite);
        //Run Configuration
        if (! StringUtil.isNullOrSpace(this.RanorexRunConfiguration))
            alb.add("/runconfig:" + this.RanorexRunConfiguration);
        //Report
        alb.add("/reportfile:" + this.RxReport.getFullReportArgument());
        //Compressed Report
        if (this.RxReport.getCompressedReport()) {
            alb.add("/zipreport");
            alb.add("/zipreportfile:" + this.RxReport.getFullCompressedReportArgument());
        }
        //JUnit Report
        if (this.RxReport.getJunitReport()) {
            alb.add("/junit");
        }
        //Test Rail
        if (this.UseTestRail) {
            alb.add("/testrail");
            alb.addMasked("/truser=" + this.TestRail.getUserName());
            alb.addMasked("/trpass=" + this.TestRail.getPassword());
            if (! StringUtil.isNullOrSpace(this.TestRail.getRunId())) {
                alb.add("/trrunid=" + this.TestRail.getRunId());
            }
            if (! StringUtil.isNullOrSpace(this.TestRail.getRunName())) {
                alb.add("/trrunaname=" + this.TestRail.getRunName());
            }
        }

        //Parameter
        if (! this.RanorexParameters.isEmpty()) {
            for (RanorexParameter p : this.RanorexParameters) {
                alb.add(p.toString());
            }
        }
        //Cmd Args
        if (! this.CommandLineArguments.isEmpty()) {
            for (CmdArgument a : this.CommandLineArguments) {
                alb.add(a.toString());
            }
        }
        return alb;
    }


    //Getter and Setter
    public void setTestSuite(String testSuite) {
        TestSuite = testSuite;
    }

    public void setRanorexRunConfiguration(String ranorexRunConfiguration) {
        RanorexRunConfiguration = ranorexRunConfiguration;
    }

    public void setRxReport(RanorexReport rxReport) {
        RxReport = rxReport;
    }

    public void setTestRail(TestRailIntegration testRail) {
        TestRail = testRail;
    }

    public void setUseTestRail(Boolean useTestRail) {
        UseTestRail = useTestRail;
    }
}
