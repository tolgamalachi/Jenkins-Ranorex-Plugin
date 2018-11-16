package com.ranorex.jenkinsranorexplugin.rx;

import com.ranorex.jenkinsranorexplugin.util.CmdArgument;
import com.ranorex.jenkinsranorexplugin.util.RanorexParameter;
import com.ranorex.jenkinsranorexplugin.util.StringUtil;
import hudson.util.ArgumentListBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class RanorexTest {
    private File _WorkingDirectory;
    private File _TestExecutable;
    private File _TestSuiteFile;
    private File _TestSequence;
    private String _RanorexRunConfiguration;
    private RanorexReport _RxReport;
    private Boolean _UseTestRail;
    private TestRailIntegration _TestRail;
    private List<RanorexParameter> _RanorexParameters;
    private List<CmdArgument> _CommandLineArguments;


    public RanorexTest(String WorkingDirectory, String TestExecutable, String TestSuite)
            throws FileNotFoundException, InvalidParameterException {
        this._WorkingDirectory = new File(WorkingDirectory);
        if(!this._WorkingDirectory.exists())
            throw new FileNotFoundException("File or directory '" + this._WorkingDirectory.getPath() + "' does not exist");
        if (! this._WorkingDirectory.isDirectory()) {
            throw new InvalidParameterException("'" + this._WorkingDirectory.getPath() + "' is not a valid directory path");
        }


        if (StringUtil.isNullOrSpace(TestExecutable)) {
            throw new InvalidParameterException("Test executable must not be empty");
        }

        this._TestExecutable = new File(this._WorkingDirectory, TestExecutable);
        if (! this._TestExecutable.exists()) {
            throw new FileNotFoundException("File or directory '" + this._TestExecutable.getPath() + "' does not exist");
        }

        //If no Test Suite is specified, Execute Test using the following prio order
        // 1. Run Ranorex Test Sequence file if exists
            //a. If multiple Ranorex Test Sequence file exist, use the one with the same name as the Ranorex Test exe
        // 2. Run Ranorex Test Suite with the Same name as the Ranorex Test exe
        // 3. Run Ranorex Test Suite with any name
        if (StringUtil.isNullOrSpace(TestSuite)) {
            //Check if TestSequence does exist
            //TODO: Extract into separate method
            Collection<File> files = FileUtils.listFiles(_WorkingDirectory, new WildcardFileFilter("*.rxsqc"), null);
            if (files.size() > 1) {
                System.out.println("Multiple sequence files found. Using the default");
            } else if (files.size() == 0) {
                System.out.println("No Test Sequence files found. Using Default Test Suite instead");
            } else if (files.size() == 1) {
                System.out.println("Single sequence found: '" + files.toArray()[0].toString() + "'");
            }
        } else {
            this._TestSuiteFile = new File(this._TestExecutable.getParent(), TestSuite);
            if (! this._TestSuiteFile.exists()) {
                throw new FileNotFoundException("File or directory '" + this._TestSuiteFile.getPath() + "' does not exist");
            }
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
        sb.append("Test Executable File:\t").append(this._TestExecutable.getName()).append("\n");

        sb.append("Test Suite File:\t");
        if (this._TestSuiteFile != null) {
            sb.append(this._TestSuiteFile.getName()).append("\n");
        } else {
            sb.append("No Test Suite File selected!\n");
        }

        sb.append("RunConfiguration:\t");
        if (! StringUtil.isNullOrSpace(this._RanorexRunConfiguration)) {
            sb.append(this._RanorexRunConfiguration).append("\n");
        } else {
            sb.append("No Runconfiguration entered\n");
        }
        if (this._RxReport != null) {
            sb.append(_RxReport);
        }
        sb.append("Sync with TestRail:\t").append(this._UseTestRail).append("\n");
        if (this._UseTestRail && this._TestRail != null) {
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
        if (this._TestExecutable != null && this._TestExecutable.exists())
            alb.add(this._TestExecutable.toString());
        //Test Suite
        if (this._TestSuiteFile != null && this._TestSuiteFile.exists())
            alb.add("/ts:" + this._TestSuiteFile.toString());
        //Run Configuration
        if (! StringUtil.isNullOrSpace(this._RanorexRunConfiguration))
            alb.add("/runconfig:" + this._RanorexRunConfiguration);
        //Report
        if (this._RxReport != null) {
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
        this._RanorexRunConfiguration = ranorexRunConfiguration;
    }

    public void setRxReport(RanorexReport rxReport) {
        this._RxReport = rxReport;
    }

    public void setTestRail(TestRailIntegration testRail) {
        this._UseTestRail = true;
        this._TestRail = testRail;
    }
}