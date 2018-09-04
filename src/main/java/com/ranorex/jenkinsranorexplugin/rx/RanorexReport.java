package com.ranorex.jenkinsranorexplugin.rx;

import com.ranorex.jenkinsranorexplugin.util.FileUtil;
import com.ranorex.jenkinsranorexplugin.util.StringUtil;

public class RanorexReport {
    private static final String ZIPPED_REPORT_EXTENSION = "rxzlog";
    private String ReportDirectory;
    private String ReportName;
    private String ReportExtension;
    private Boolean JunitReport;
    private Boolean CompressedReport;
    private String CompressedReportDirectory;
    private String CompressedReportName;

    public RanorexReport(String RanorexWorkingDirectory, String ReportDirectory, String ReportName, String ReportExtension, Boolean Junit, Boolean CompressedReport, String CompressedReportDir, String CompressedReportName) {

        if (! StringUtil.isNullOrSpace(ReportDirectory)) {
            this.ReportDirectory = FileUtil.getAbsoluteReportDirectory(RanorexWorkingDirectory, ReportDirectory);
        } else {
            this.ReportDirectory = RanorexWorkingDirectory;
        }

        this.ReportDirectory = StringUtil.appendBackslash(this.ReportDirectory);

        if (! StringUtil.isNullOrSpace(ReportName)) {
            this.ReportName = FileUtil.removeFileExtension(ReportName);
        } else {
            this.ReportName = "%S_%Y_%M%D_%T";
        }
        this.ReportExtension = ReportExtension;
        this.JunitReport = Junit;
        this.CompressedReport = CompressedReport;
        if (this.CompressedReport) {
            if (! StringUtil.isNullOrSpace(CompressedReportDir)) {
                this.CompressedReportDirectory = FileUtil.getAbsoluteReportDirectory(RanorexWorkingDirectory, CompressedReportDir);
            } else {
                this.CompressedReportDirectory = RanorexWorkingDirectory;
            }
            this.CompressedReportDirectory = StringUtil.appendBackslash(this.CompressedReportDirectory);
            if (! StringUtil.isNullOrSpace(CompressedReportName)) {
                this.CompressedReportName = FileUtil.removeFileExtension(CompressedReportName);
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
        return this.ReportDirectory + this.ReportName + "." + this.ReportExtension;
    }

    public String getFullCompressedReportArgument() {
        return this.CompressedReportDirectory + this.CompressedReportName + "." + ZIPPED_REPORT_EXTENSION;
    }

    public Boolean getJunitReport() {
        return JunitReport;
    }

    public Boolean getCompressedReport() {
        return CompressedReport;
    }
}