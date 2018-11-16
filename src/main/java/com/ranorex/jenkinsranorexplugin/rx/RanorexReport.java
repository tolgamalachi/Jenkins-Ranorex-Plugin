package com.ranorex.jenkinsranorexplugin.rx;

import com.ranorex.jenkinsranorexplugin.util.FileUtil;
import com.ranorex.jenkinsranorexplugin.util.StringUtil;

import java.io.File;
import java.security.InvalidParameterException;

public class RanorexReport {
    private static final String ZIPPED_REPORT_EXTENSION = "rxzlog";
    private File ReportDirectory;
    private String ReportName;
    private String ReportExtension;
    private Boolean JunitReport;
    private Boolean CompressedReport;
    private File CompressedReportDirectory;
    private String CompressedReportName;

    public RanorexReport(String RanorexWorkingDirectory, String ReportDirectory, String ReportName, String ReportExtension, Boolean Junit, Boolean CompressedReport, String CompressedReportDir, String CompressedReportName) {

        if (! StringUtil.isNullOrSpace(ReportDirectory)) {
            this.ReportDirectory = new File(FileUtil.getAbsoluteReportDirectory(RanorexWorkingDirectory, ReportDirectory));
        } else {
            this.ReportDirectory = new File(RanorexWorkingDirectory);
        }
        //this.ReportDirectory = StringUtil.appendBackslash(this.ReportDirectory);

        if (! StringUtil.isNullOrSpace(ReportName)) {
            try {
                this.ReportName = FileUtil.removeFileExtension(ReportName);
            } catch (InvalidParameterException e) {
                this.ReportName = ReportName;
                System.out.println("Nothing to remove here");
            }
        } else {
            this.ReportName = "%S_%Y_%M%D_%T";
        }
        this.ReportExtension = ReportExtension;
        this.JunitReport = Junit;
        this.CompressedReport = CompressedReport;
        if (this.CompressedReport) {
            if (! StringUtil.isNullOrSpace(CompressedReportDir)) {
                this.CompressedReportDirectory = new File(FileUtil.getAbsoluteReportDirectory(RanorexWorkingDirectory, CompressedReportDir));
            } else {
                this.CompressedReportDirectory = new File(RanorexWorkingDirectory);
            }

            //this.CompressedReportDirectory = StringUtil.appendBackslash(this.CompressedReportDirectory);
            if (! StringUtil.isNullOrSpace(CompressedReportName)) {
                try {
                    this.CompressedReportName = FileUtil.removeFileExtension(CompressedReportName);
                } catch (InvalidParameterException e) {
                    this.CompressedReportName = CompressedReportName;
                    System.out.println("Nothing to remove here");
                }
            } else {
                this.CompressedReportName = this.ReportName;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nRegular Ranorex Report\n");
        sb.append("\tDirectory:\t").append(this.ReportDirectory).append("\n");
        sb.append("\tName:\t\t").append(this.ReportName).append("\n");
        sb.append("\tExtension:\t").append(this.ReportExtension).append("\n");

        sb.append("\nCompressed Report:\t").append(this.CompressedReport).append("\n");
        if (this.CompressedReport) {
            sb.append("\tDirectory:\t").append(this.CompressedReportDirectory).append("\n");
            sb.append("\tName:\t\t").append(this.CompressedReportName).append("\n");
            sb.append("\tExtension:\t" + ZIPPED_REPORT_EXTENSION + "\n");
        }
        sb.append("\nJunit Report enabled: \t").append(this.JunitReport).append("\n\n");
        return sb.toString();
    }


    //Getter And Setter

    public String getFullReportArgument() {
        return this.ReportDirectory.getPath() + "\\" + this.ReportName + "." + this.ReportExtension;
    }

    public String getFullCompressedReportArgument() {
        return this.CompressedReportDirectory.getPath() + "\\" + this.CompressedReportName + "." + ZIPPED_REPORT_EXTENSION;
    }

    public Boolean getJunitReport() {
        return JunitReport;
    }

    public Boolean getCompressedReport() {
        return CompressedReport;
    }

    public String getReportDirectory() {
        return ReportDirectory.getPath();
    }

    public String getReportName() {
        return ReportName;
    }
}
