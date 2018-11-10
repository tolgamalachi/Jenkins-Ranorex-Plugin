package com.ranorex.jenkinsranorexplugin;

import com.ranorex.jenkinsranorexplugin.util.CmdArgument;
import com.ranorex.jenkinsranorexplugin.util.FileUtil;
import com.ranorex.jenkinsranorexplugin.util.RanorexParameter;
import com.ranorex.jenkinsranorexplugin.util.StringUtil;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ArgumentListBuilder;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.io.PrintStream;

public class RanorexRunnerBuilder extends Builder {

    private static final String ZIPPED_REPORT_EXTENSION = ".rxzlog";
    private static final String ARGUMENT_SEPARATOR = "\t\r\n;";
    private static PrintStream LOGGER;
    /*
     * Builder GUI Fields
     */
    private final String rxTestSuiteFilePath;
    private final String rxRunConfiguration;
    private final String rxReportDirectory;
    private final String rxReportFile;
    private final String rxReportExtension;
    private final Boolean rxJUnitReport;
    private final Boolean rxZippedReport;
    private final String rxZippedReportDirectory;
    private final String rxZippedReportFile;
    private final String rxGlobalParameter;
    private final String cmdLineArgs;
    private final Boolean rxTestRail;
    private final String rxTestRailUser;
    private final String rxTestRailPassword;
    private final String rxTestRailRID;
    private final String rxTestRailRunName;

    /*
     * Other Variables
     */
    private String rxExecuteableFile;
    private String WorkSpace;
    private String usedRxReportDirectory;
    private String usedRxReportFile;
    private String usedRxZippedReportDirectory;
    private String usedRxZippedReportFile;
    private ArgumentListBuilder jArguments;

    /**
     * When this builder is created in the project configuration step, the
     * builder object will be created from the strings below
     *
     * @param rxTestSuiteFilePath     The name/location of the Ranorex Test Suite / Ranorex Test Exe File
     * @param rxRunConfiguration      The Ranorex Run configuration which will be executed
     * @param rxReportDirectory       The directory where the Ranorex Report should be saved
     * @param rxReportFile            The name of the Ranorex Report
     * @param rxReportExtension       The extension of your Ranorex Report
     * @param rxJUnitReport           If true, a JUnit compatible Report will be saved
     * @param rxZippedReport          If true, the report will also be saved as RXZLOG
     * @param rxZippedReportDirectory The directory where the Ranorex Zipped Report should be saved
     * @param rxZippedReportFile      The name of the zipped Ranorex Report
     * @param rxGlobalParameter       Global test suite parameters
     * @param cmdLineArgs             Additional CMD line arguments
     */
    @DataBoundConstructor

    public RanorexRunnerBuilder(String rxTestSuiteFilePath, String rxRunConfiguration, String rxReportDirectory, String rxReportFile, String rxReportExtension, Boolean rxJUnitReport, Boolean rxZippedReport, String rxZippedReportDirectory, String rxZippedReportFile, Boolean rxTestRail, String rxTestRailUser, String rxTestRailPassword, String rxTestRailRID, String rxTestRailRunName, String rxGlobalParameter, String cmdLineArgs) {
        this.rxTestSuiteFilePath = rxTestSuiteFilePath.trim();
        this.rxRunConfiguration = rxRunConfiguration.trim();
        this.rxReportDirectory = rxReportDirectory.trim();
        this.rxReportFile = rxReportFile.trim();
        this.rxReportExtension = rxReportExtension.trim();
        this.rxJUnitReport = rxJUnitReport;
        this.rxZippedReport = rxZippedReport;
        this.rxZippedReportDirectory = rxZippedReportDirectory.trim();
        this.rxZippedReportFile = rxZippedReportFile.trim();
        this.rxTestRail = rxTestRail;
        this.rxTestRailUser = rxTestRailUser.trim();
        this.rxTestRailPassword = rxTestRailPassword.trim();
        this.rxTestRailRID = rxTestRailRID.trim();
        this.rxTestRailRunName = rxTestRailRunName.trim();
        this.rxGlobalParameter = rxGlobalParameter.trim();
        this.cmdLineArgs = cmdLineArgs.trim();

    }

    public String getRxTestSuiteFilePath() {
        return this.rxTestSuiteFilePath;
    }

    public String getRxRunConfiguration() {
        return this.rxRunConfiguration;
    }

    public String getRxReportDirectory() {
        return this.rxReportDirectory;
    }

    public String getRxReportFile() {
        return this.rxReportFile;
    }

    public String getRxReportExtension() {
        return this.rxReportExtension;
    }

    public Boolean getrxJUnitReport() {
        return this.rxJUnitReport;
    }

    public Boolean getRxZippedReport() {
        return this.rxZippedReport;
    }

    public String getRxZippedReportDirectory() {
        return this.rxZippedReportDirectory;
    }

    public String getRxZippedReportFile() {
        return this.rxZippedReportFile;
    }

    public Boolean getRxTestRail() {
        return this.rxTestRail;
    }

    public String getRxTestRailUser() {
        return this.rxTestRailUser;
    }

    public String getRxTestRailPassword() {
        return this.rxTestRailPassword;
    }

    public String getRxTestRailRID() {
        return this.rxTestRailRID;
    }

    public String getRxTestRailRunName() {
        return this.rxTestRailRunName;
    }

    public String getRxGlobalParameter() {
        return this.rxGlobalParameter;
    }

    public String getCmdLineArgs() {
        return this.cmdLineArgs;
    }

    // public String getRxExecuteableFile()
    // {
    // return this.rxExecuteableFile;
    // }


    /**
     * Runs the step over the given build and reports the progress to the
     * listener
     *
     * @param build
     * @param launcher Starts a process
     * @param listener Receives events that happen during a build
     * @return Receives events that happen during a build
     * @throws IOException          - If the build is interrupted by the user (in an
     *                              attempt to abort the build.) Normally the BuildStep implementations may
     *                              simply forward the exception it got from its lower-level functions.
     * @throws InterruptedException - If the implementation wants to abort the
     *                              processing when an IOException happens, it can simply propagate the
     *                              exception to the caller. This will cause the build to fail, with the
     *                              default error message. Implementations are encouraged to catch
     *                              IOException on its own to provide a better error message, if it can do
     *                              so, so that users have better understanding on why it failed.
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        jArguments = new ArgumentListBuilder("cmd.exe", "/C");
        WorkSpace = FileUtil.getRanorexWorkingDirectory(build.getWorkspace(), rxTestSuiteFilePath).getRemote();
        WorkSpace = StringUtil.appendBackslash(WorkSpace);
        LOGGER = listener.getLogger();
        EnvVars env = build.getEnvironment(listener);
        boolean r = false;

        if (! StringUtil.isNullOrSpace(rxTestSuiteFilePath)) {
            rxExecuteableFile = FileUtil.getExecutableFromTestSuite(rxTestSuiteFilePath);
            jArguments.add(rxExecuteableFile);
            // Ranorex Run Configuration
            if (! StringUtil.isNullOrSpace(rxRunConfiguration)) {
                jArguments.add("/runconfig:" + rxRunConfiguration);
            }

            // Ranorex Reportdirectory
            if (! StringUtil.isNullOrSpace(rxReportDirectory)) {
                LOGGER.println("Reportpath to merge. Base: " + WorkSpace + " Relative: " + rxReportDirectory);
                usedRxReportDirectory = FileUtil.getAbsoluteReportDirectory(WorkSpace, rxReportDirectory);
                LOGGER.println("Merged path: " + usedRxReportDirectory);
            } else {
                usedRxReportDirectory = WorkSpace;
            }
            usedRxReportDirectory = StringUtil.appendBackslash(usedRxReportDirectory);

            // ReportFilename
            if (! StringUtil.isNullOrSpace(rxReportFile)) {
                if (! FileUtil.isAbsolutePath(rxReportFile)) {
                    usedRxReportFile = FileUtil.removeFileExtension(rxReportFile);
                } else {
                    LOGGER.println("'" + rxReportFile + "' is not a valid Ranorex Report filename");
                    return false;
                }
            } else {
                usedRxReportFile = "%S_%Y%M%D_%T";
            }
            jArguments.add("/reportfile:" + usedRxReportDirectory + usedRxReportFile + "." + rxReportExtension);

            // JUnit compatible Report
            if (rxJUnitReport) {
                jArguments.add("/junit");
            }

            // Compressed copy of Ranorex report
            if (rxZippedReport) {
                jArguments.add("/zipreport");
                // Zipped Ranorex Reportdirectory
                if (! StringUtil.isNullOrSpace(rxZippedReportDirectory)) {
                    usedRxZippedReportDirectory = FileUtil.getAbsoluteReportDirectory(WorkSpace, rxZippedReportDirectory);
                } else {
                    usedRxZippedReportDirectory = WorkSpace;
                }
                usedRxZippedReportDirectory = StringUtil.appendBackslash(usedRxZippedReportDirectory);

                // Zipped Report File Name
                if (! StringUtil.isNullOrSpace(rxZippedReportFile)) {
                    if (! FileUtil.isAbsolutePath(rxZippedReportFile)) {
                        usedRxZippedReportFile = FileUtil.removeFileExtension(rxZippedReportFile);
                    } else {
                        LOGGER.println("'" + rxZippedReportFile + "' is not a valid Ranorex Report filename");
                        return false;
                    }
                } else {
                    usedRxZippedReportFile = usedRxReportFile;
                }

                jArguments.add("/zipreportfile:" + usedRxZippedReportDirectory + usedRxZippedReportFile + ZIPPED_REPORT_EXTENSION);
            }

            //Test Rail
            if (rxTestRail) {
                jArguments.add("/testrail");
                if (! StringUtil.isNullOrSpace(rxTestRailUser) && ! StringUtil.isNullOrSpace(rxTestRailPassword)) {
                    jArguments.addMasked("/truser=" + rxTestRailUser);
                    jArguments.addMasked("/trpass=" + rxTestRailPassword);
                } else {
                    LOGGER.println("Testrail username and password are required");
                    return false;
                }
                if (! StringUtil.isNullOrSpace(rxTestRailRID)) {
                    jArguments.add("/trrunid=" + rxTestRailRID);
                }
                if (! StringUtil.isNullOrSpace(rxTestRailRunName)) {
                    jArguments.add("/trrunname=" + rxTestRailRunName);
                }
            }

            // Parse Global Parameters
            if (! StringUtil.isNullOrSpace(rxGlobalParameter)) {
                for (String param : StringUtil.splitBy(rxGlobalParameter, ARGUMENT_SEPARATOR)) {
                    try {
                        RanorexParameter rxParam = new RanorexParameter(param);
                        rxParam.trim();
                        jArguments.add(rxParam.toString());
                    } catch (Exception e) {
                        System.out.println("[INFO] [RanorexRunnerBuilder] Parameter '" + param + "' will be ignored");
                    }
                }
            }

            // Additional cmd arguments
            if (! StringUtil.isNullOrSpace(cmdLineArgs)) {
                for (String argument : StringUtil.splitBy(cmdLineArgs, ARGUMENT_SEPARATOR)) {
                    try {
                        CmdArgument arg = new CmdArgument(argument);
                        jArguments.add(arg.toString());
                    } catch (Exception e) {
                        System.out.println("[INFO] [RanorexRunnerBuilder] Argument '" + argument + "' will be ignored ");
                    }
                }
            }
            // Summarize Output
            if (getDescriptor().isUseSummarize()) {
                LOGGER.println("\n*************Start of Ranorex Summary*************");
                LOGGER.println("Current Plugin version:\t\t" + getClass().getPackage().getImplementationVersion());
                LOGGER.println("Ranorex Working Directory:\t" + WorkSpace);
                LOGGER.println("Ranorex test suite file:\t" + rxTestSuiteFilePath);
                LOGGER.println("Ranorex test exe file:\t\t" + rxExecuteableFile);
                LOGGER.println("Ranorex run configuration:\t" + rxRunConfiguration);
                LOGGER.println("Ranorex report directory:\t" + usedRxReportDirectory);
                LOGGER.println("Ranorex report filename:\t" + usedRxReportFile);
                LOGGER.println("Ranorex report extension:\t" + rxReportExtension);
                LOGGER.println("Junit-compatible report:\t" + rxJUnitReport);
                LOGGER.println("Ranorex report compression:\t" + rxZippedReport);
                if (rxZippedReport) {
                    LOGGER.println("\tRanorex zipped report dir:\t" + usedRxZippedReportDirectory);
                    LOGGER.println("\tRanorex zipped report file:\t" + usedRxZippedReportFile);
                }
                LOGGER.println("Ranorex Test Rail Integration:\t" + rxTestRail);
                if (rxTestRail) {
                    LOGGER.println("\tRanorex Test Rail User:\t\t" + rxTestRailUser);
                    LOGGER.println("\tRanorex Test Rail Password:\t" + "*****************");
                    LOGGER.println("\tRanorex Test Rail Run ID:\t" + rxTestRailRID);
                    LOGGER.println("\tRanorex Test Rail Run Name:\t" + rxTestRailRunName);
                }
                LOGGER.println("Ranorex global parameters:");
                if (! StringUtil.isNullOrSpace(rxGlobalParameter)) {
                    for (String param : StringUtil.splitBy(rxGlobalParameter, ARGUMENT_SEPARATOR)) {
                        try {
                            RanorexParameter rxParam = new RanorexParameter(param);
                            rxParam.trim();
                            LOGGER.println("\t*" + rxParam.toString());
                        } catch (Exception e) {
                            LOGGER.println("\t!" + param + " will be ignored");
                        }
                    }
                } else {
                    LOGGER.println("\t*No global parameters entered");
                }
                LOGGER.println("Command line arguments:");
                if (! StringUtil.isNullOrSpace(cmdLineArgs)) {
                    for (String argument : StringUtil.splitBy(cmdLineArgs, ARGUMENT_SEPARATOR)) {
                        try {
                            CmdArgument arg = new CmdArgument(argument);
                            arg.trim();
                            LOGGER.println("\t*" + arg.toString());
                        } catch (Exception e) {
                            LOGGER.println("\t!" + argument + " will be ignored ");
                        }
                    }
                } else {
                    LOGGER.println("\t*No command line arguments entered");
                }
                LOGGER.println("*************End of Ranorex Summary*************\n");
            }
            r = exec(build, launcher, listener, env); // Start the given exe file with all arguments added before
        } else {
            LOGGER.println("No TestSuite file given");
        }
        return r;
    }


    /**
     * Starts the given executeable file with all arguments and parameters
     *
     * @param build
     * @param launcher Starts a process
     * @param listener Receives events that happen during a build
     * @param env      Environmental variables to be used for launching processes for this build.
     * @return true if execution was succesfull; otherwise false
     * @throws InterruptedException
     * @throws IOException
     */
    private boolean exec(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, EnvVars env) {
        FilePath currentWorkspace = FileUtil.getRanorexWorkingDirectory(build.getWorkspace(), rxTestSuiteFilePath);
        LOGGER.println("Executing : " + jArguments.toString());
        try {
            int r = launcher.launch().cmds(jArguments).envs(env).stdout(listener).pwd(currentWorkspace).join();

            if (r != 0) {
                build.setResult(Result.FAILURE);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace(listener.fatalError("execution failed"));
            return false;
        }
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();

    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        /*
         * Configure Variables
         */
        private boolean useSummarize;

        /**
         * In order to load the persisted global configuration, you have to call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        //Check Report Directory
        public FormValidation doCheckRxReportDirectory(@QueryParameter String value) {
            if (! StringUtil.isNullOrSpace(value)) {
                return FormValidation.ok();
            } else {
                return FormValidation.warning("Current Ranorex Working directory will be used");
            }
        }

        // Check Report Filename
        public FormValidation doCheckRxReportFile(@QueryParameter String value) {
            if (! StringUtil.isNullOrSpace(value) && ! FileUtil.isAbsolutePath(value)) {
                return FormValidation.ok();
            } else if (FileUtil.isAbsolutePath(value)) {
                return FormValidation.error("'" + value + "' is not a valid Ranorex Report filename");
            } else {
                return FormValidation.warning("'%S_%Y%M%D_%T' will be used");
            }
        }

        // Check Zipped Report Directory
        public FormValidation doCheckRxZippedReportDirectory(@QueryParameter String value) {
            if (! StringUtil.isNullOrSpace(value)) {
                return FormValidation.ok();
            } else {
                return FormValidation.warning("Current Ranorex Working directory will be used");
            }
        }

        // Check Zipped Report Filename
        public FormValidation doCheckRxZippedReportFile(@QueryParameter String value, @QueryParameter String rxReportFile) {
            if (! StringUtil.isNullOrSpace(value) && ! FileUtil.isAbsolutePath(value)) {
                return FormValidation.ok();
            } else if (FileUtil.isAbsolutePath(value)) {
                return FormValidation.error("'" + value + "' is not a valid Ranorex Report filename");
            } else if ((StringUtil.isNullOrSpace(value) && StringUtil.isNullOrSpace(rxReportFile)) || (StringUtil.isNullOrSpace(value) && FileUtil.isAbsolutePath(rxReportFile))) {
                return FormValidation.warning("'%S_%Y%M%D_%T' will be used");
            } else {
                return FormValidation.warning("'" + rxReportFile + "' will be used");
            }
        }

        // Check Test Rail Username
        public FormValidation doCheckRxTestRailUser(@QueryParameter String value) {
            if (! StringUtil.isNullOrSpace(value)) {
                return FormValidation.ok();
            }
            return FormValidation.error("Username is required");

        }

        // Check Test Rail Password
        public FormValidation doCheckRxTestRailPassword(@QueryParameter String value) {
            if (! StringUtil.isNullOrSpace(value)) {
                return FormValidation.ok();
            }
            return FormValidation.error("Password is required");
        }

        @SuppressWarnings ("rawtypes")
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         *
         * @return a human readable string that is shown in the DropDown Menu
         */
        @Override
        public String getDisplayName() {
            return "Run a Ranorex Test Suite";
        }


        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            useSummarize = formData.getBoolean("useSummarize");
            save();
            return super.configure(req, formData); // To change body of generated methods, choose Tools | Templates.
        }

        public boolean isUseSummarize() {
            return useSummarize;
        }
    }
}
