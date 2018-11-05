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
    private File _WorkingDirectory;
    private File _TestExecutable;
    private File _TestSuiteFile;
    private String _RanorexRunConfiguration;
    private RanorexReport _RxReport;
    private Boolean _UseTestRail;
    private TestRailIntegration _TestRail;
    private List<RanorexParameter> _RanorexParameters;
    private List<CmdArgument> _CommandLineArguments;


    public RanorexTest(String WorkingDirectory, String TestExecutable, String TestSuite) throws FileNotFoundException {
        this._WorkingDirectory = new File(WorkingDirectory);
        if (! _WorkingDirectory.isDirectory()) {
            throw new InvalidParameterException("'" + _WorkingDirectory.getPath() + "' is not a valid directory path");
        }
        this._TestExecutable = new File(TestExecutable);
        if (! _TestExecutable.exists()) {
            throw new FileNotFoundException("File '" + _TestExecutable.getName() + "' does not exist");
        }
        this._TestSuiteFile = new File(_TestExecutable.getPath(), TestSuite);
        if (! _TestSuiteFile.exists()) {
            throw new FileNotFoundException("File '" + _TestSuiteFile.getName() + "' does not exist");
        }
        this._UseTestRail = false;
        this._RanorexParameters = new ArrayList<>();
        this._CommandLineArguments = new ArrayList<>();
    }


    public void addGlobalParameter(RanorexParameter param) {
        this._RanorexParameters.add(param);
    }

    public void addCommandLineArgument(CmdArgument arg) {
        this._CommandLineArguments.add(arg);
    }


    public String getWorkingDirectoryString() {
        return this._WorkingDirectory.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Working Directory:\t").append(this.getWorkingDirectoryString()).append("\n");
        sb.append("Test Executable File:\t").append(this._TestExecutable.toString()).append("\n");

        sb.append("Test Suite File:\t");
        if (! StringUtil.isNullOrSpace(this._TestSuiteFile.toString())) {
            sb.append(this._TestSuiteFile.toString()).append("\n");
        } else {
            sb.append("No Test Suite File selected!\n");
        }

        sb.append("RunConfiguration:\t");
        if (! StringUtil.isNullOrSpace(this._RanorexRunConfiguration)) {
            sb.append(this._RanorexRunConfiguration).append("\n");
        } else {
            sb.append("No Runconfiguration entered\n");
        }

        sb.append(_RxReport);

        sb.append("Sync with TestRail:\t").append(this._UseTestRail).append("\n");
        if (this._UseTestRail) {
            sb.append(_TestRail);
        }
        sb.append("\nGlobal Parameters:\n");
        if (! this._RanorexParameters.isEmpty()) {
            for (RanorexParameter p : this._RanorexParameters) {
                sb.append("\t").append(p.toString()).append("\n");
            }
        } else {
            sb.append("\tNo Parameters entered\n");
        }

        sb.append("\nCommand Line Arguments:\n");
        if (! this._CommandLineArguments.isEmpty()) {
            for (CmdArgument a : this._CommandLineArguments) {
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
        if (! StringUtil.isNullOrSpace(this._TestExecutable.toString()))
            alb.add(this._TestExecutable.toString());
        //Test Suite
        if (! StringUtil.isNullOrSpace(this._TestSuiteFile.toString()))
            alb.add("/ts:" + this._TestSuiteFile.toString());
        //Run Configuration
        if (! StringUtil.isNullOrSpace(this._RanorexRunConfiguration))
            alb.add("/runconfig:" + this._RanorexRunConfiguration);
        //Report
        alb.add("/reportfile:" + this._RxReport.getFullReportArgument());
        //Compressed Report
        if (this._RxReport.getCompressedReport()) {
            alb.add("/zipreport");
            alb.add("/zipreportfile:" + this._RxReport.getFullCompressedReportArgument());
        }
        //JUnit Report
        if (this._RxReport.getJunitReport()) {
            alb.add("/junit");
        }
        //Test Rail
        if (this._UseTestRail) {
            alb.add("/testrail");
            alb.addMasked("/truser=" + this._TestRail.getUserName());
            alb.addMasked("/trpass=" + this._TestRail.getPassword());
            if (! StringUtil.isNullOrSpace(this._TestRail.getRunId())) {
                alb.add("/trrunid=" + this._TestRail.getRunId());
            }
            if (! StringUtil.isNullOrSpace(this._TestRail.getRunName())) {
                alb.add("/trrunaname=" + this._TestRail.getRunName());
            }
        }

        //Parameter
        if (! this._RanorexParameters.isEmpty()) {
            for (RanorexParameter p : this._RanorexParameters) {
                alb.add(p.toString());
            }
        }
        //Cmd Args
        if (! this._CommandLineArguments.isEmpty()) {
            for (CmdArgument a : this._CommandLineArguments) {
                alb.add(a.toString());
            }
        }
        return alb;
    }


    //Getter and Setter
    /*public void setTestSuite(String testSuite) {
        _TestSuiteFile = testSuite;
    }*/

    public void setRanorexRunConfiguration(String ranorexRunConfiguration) {
        _RanorexRunConfiguration = ranorexRunConfiguration;
    }

    public void setRxReport(RanorexReport rxReport) {
        _RxReport = rxReport;
    }

    public void setTestRail(TestRailIntegration testRail) {
        _TestRail = testRail;
    }

    public void setUseTestRail(Boolean useTestRail) {
        _UseTestRail = useTestRail;
    }
}
