package com.ranorex.jenkinsranorexplugin;

import com.ranorex.jenkinsranorexplugin.rx.RanorexReport;
import com.ranorex.jenkinsranorexplugin.rx.RanorexTest;
import com.ranorex.jenkinsranorexplugin.rx.TestRailIntegration;
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
    private static final String ARGUMENT_SEPARATOR = "\t\r\n;";
    private static PrintStream LOGGER;
    /*
     * Builder GUI Fields
     */
    private final String rxTestExecutablePath; //Path + File
    private final String rxTestSuite; //Just File
    private final String rxRunConfiguration;

    private final String rxReportDirectory; //Just Path
    private final String rxReportFile; // Just File
    private final String rxReportExtension;
    private final Boolean rxJUnitReport;

    private final Boolean rxCompressedReport;
    private final String rxCompressedReportDirectory; // Just Path
    private final String rxCompressedReportFile; //Just File

    private final String rxGlobalParameter;
    private final String cmdLineArgs;

    private final Boolean useRxTestRail;
    private final String rxTestRailUser;
    private final String rxTestRailPassword;
    private final String rxTestRailRID;
    private final String rxTestRailRunName;

    /*
     * Other Variables
     */
    private RanorexTest rxTest;
    private String rxExecuteableFileName;
    private String rxWorkingDirectory;
    private ArgumentListBuilder jArguments;

    /**
     * When this builder is created in the project configuration step, the
     * builder object will be created from the strings below
     *
     * @param rxTestSuite                 The name/location of the Ranorex Test Suite / Ranorex Test Exe File
     * @param rxRunConfiguration          The Ranorex Run configuration which will be executed
     * @param rxReportDirectory           The directory where the Ranorex Report should be saved
     * @param rxReportFile                The name of the Ranorex Report
     * @param rxReportExtension           The extension of your Ranorex Report
     * @param rxJUnitReport               If true, a JUnit compatible Report will be saved
     * @param rxCompressedReport          If true, the report will also be saved as RXZLOG
     * @param rxCompressedReportDirectory The directory where the Ranorex Zipped Report should be saved
     * @param rxCompressedReportFile      The name of the zipped Ranorex Report
     * @param rxGlobalParameter           Global test suite parameters
     * @param cmdLineArgs                 Additional CMD line arguments
     */
    @DataBoundConstructor

    public RanorexRunnerBuilder(String rxTestExecutablePath, String rxTestSuite, String rxRunConfiguration,
                                String rxReportDirectory, String rxReportFile, String rxReportExtension, Boolean rxJUnitReport,
                                Boolean rxCompressedReport, String rxCompressedReportDirectory, String rxCompressedReportFile,
                                Boolean useRxTestRail, String rxTestRailUser, String rxTestRailPassword, String rxTestRailRID, String rxTestRailRunName,
                                String rxGlobalParameter, String cmdLineArgs) {

        this.rxTestExecutablePath = rxTestExecutablePath.trim();
        this.rxTestSuite = rxTestSuite.trim();
        this.rxRunConfiguration = rxRunConfiguration.trim();
        this.rxReportDirectory = rxReportDirectory.trim();
        this.rxReportFile = rxReportFile.trim();
        this.rxReportExtension = rxReportExtension.trim();
        this.rxJUnitReport = rxJUnitReport;
        this.rxCompressedReport = rxCompressedReport;
        this.rxCompressedReportDirectory = rxCompressedReportDirectory.trim();
        this.rxCompressedReportFile = rxCompressedReportFile.trim();
        this.useRxTestRail = useRxTestRail;
        this.rxTestRailUser = rxTestRailUser.trim();
        this.rxTestRailPassword = rxTestRailPassword.trim();
        this.rxTestRailRID = rxTestRailRID.trim();
        this.rxTestRailRunName = rxTestRailRunName.trim();
        this.rxGlobalParameter = rxGlobalParameter.trim();
        this.cmdLineArgs = cmdLineArgs.trim();
    }

    public String getRxTestExecutablePath() {
        return this.rxTestExecutablePath;
    }

    public String getRxTestSuite() {
        return this.rxTestSuite;
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

    public Boolean getRxCompressedReport() {
        return this.rxCompressedReport;
    }

    public String getRxCompressedReportDirectory() {
        return this.rxCompressedReportDirectory;
    }

    public String getRxCompressedReportFile() {
        return this.rxCompressedReportFile;
    }

    public Boolean getUseRxTestRail() {
        return this.useRxTestRail;
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
        LOGGER = listener.getLogger();
        EnvVars env = build.getEnvironment(listener);
        boolean r = false;

        if (! StringUtil.isNullOrSpace(rxTestExecutablePath)) {
            try {
                LOGGER.println("[DEBUG]: rxTestExecutablePath: " + rxTestExecutablePath);
                LOGGER.println("[DEBUG]: jenkinsWorkspace: '" + build.getWorkspace() + "'");

                try {
                    rxWorkingDirectory = FileUtil.getRanorexWorkingDirectory(build.getWorkspace(), rxTestExecutablePath).getRemote();
                } catch (Exception ex) {
                    LOGGER.println("getRanorexWorkingDirectory threw an " + ex.getClass() + " exception");
                    LOGGER.println(ex.toString());
                }

                LOGGER.println("[DEBUG]: rxWorkingDirectory: " + rxWorkingDirectory);
                rxExecuteableFileName = FileUtil.getFile(rxTestExecutablePath);
                LOGGER.println("[DEBUG]: rxExecuteableFileName: " + rxExecuteableFileName);
                LOGGER.println("[DEBUG]: rxTestSuite: " + rxTestSuite);

                rxTest = new RanorexTest(rxWorkingDirectory, rxExecuteableFileName, rxTestSuite);

                if (! StringUtil.isNullOrSpace(rxRunConfiguration)) {
                    rxTest.setRanorexRunConfiguration(rxRunConfiguration);
                }
                LOGGER.println("[DEBUG]: rxRunConfiguration: " + rxRunConfiguration);
                RanorexReport rxReport = new RanorexReport(rxWorkingDirectory,
                        rxReportDirectory, rxReportFile, rxReportExtension, rxJUnitReport,
                        rxCompressedReport, rxCompressedReportDirectory, rxCompressedReportFile);

                rxTest.setRxReport(rxReport);

                if (useRxTestRail) {
                    TestRailIntegration rxTestRail = new TestRailIntegration(rxTestRailUser, rxTestRailPassword,
                            rxTestRailRID, rxTestRailRunName);
                    rxTest.setTestRail(rxTestRail);
                }
                LOGGER.println("[DEBUG]: useRxTestRail: " + useRxTestRail);
                if (! StringUtil.isNullOrSpace(rxGlobalParameter)) {
                    for (String param : StringUtil.splitBy(rxGlobalParameter, ARGUMENT_SEPARATOR)) {
                        try {
                            RanorexParameter rxParam = new RanorexParameter(param);
                            rxParam.trim();
                            rxTest.addGlobalParameter(rxParam);
                        } catch (Exception e) {
                            System.out.println("[INFO] [Ranorex] Parameter '" + param + "' will be ignored");
                            LOGGER.println("[INFO] [Ranorex] Parameter '" + param + "' will be ignored");
                        }
                    }
                }

                if (! StringUtil.isNullOrSpace(cmdLineArgs)) {
                    for (String argument : StringUtil.splitBy(cmdLineArgs, ARGUMENT_SEPARATOR)) {
                        try {
                            CmdArgument arg = new CmdArgument(argument);
                            rxTest.addCommandLineArgument(arg);
                        } catch (Exception e) {
                            System.out.println("[INFO] [Ranorex] Argument '" + argument + "' will be ignored ");
                            LOGGER.println("[INFO] [Ranorex] Argument '" + argument + "' will be ignored ");
                        }
                    }
                }
                // Summarize Output
                if (getDescriptor().isUseSummarize()) {
                    LOGGER.println("\n*************Start of Ranorex Summary*************");
                    LOGGER.println("Current Plugin version:\t" + getClass().getPackage().getImplementationVersion());
                    LOGGER.print(rxTest);
                    LOGGER.println("*************End of Ranorex Summary*************\n");
                }

                jArguments = rxTest.toExecutionArguments();
                r = exec(build, launcher, listener, env); // Start the given exe file with all arguments added before
            } catch (Exception e) {
                LOGGER.println("[ERROR]: " + e.toString());
                LOGGER.println("[Damn! ERROR]: " + e.getMessage());
                LOGGER.println("[ERROR]: " + e.toString());
            }
        } else {
            LOGGER.println("ERROR: Please specify a Ranorex Test Executable");
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
     */
    private boolean exec(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, EnvVars env) {
        FilePath currentWorkspace = FileUtil.getRanorexWorkingDirectory(build.getWorkspace(), rxTestExecutablePath);
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

        //Check Ranorex Test Executable
        public FormValidation doCheckRxTestExecutablePath(@QueryParameter String value) {
            if (StringUtil.isNullOrSpace(value)) {
                return FormValidation.error("Test executable is required");
            } else if (! value.contains(".exe")) {
                return FormValidation.error("'" + value + "' is not a valid executable");
            } else {
                return FormValidation.ok();
            }
        }

        public FormValidation doCheckRxTestSuite(@QueryParameter String value) {
            if (StringUtil.isNullOrSpace(value))
                return FormValidation.warning("Test Sequence will be executed. Please note that this will ignore the options below");
            else {
                return FormValidation.ok();
            }
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
        public FormValidation doCheckRxCompressedReportDirectory(@QueryParameter String value) {
            if (! StringUtil.isNullOrSpace(value)) {
                return FormValidation.ok();
            } else {
                return FormValidation.warning("Current Ranorex Working directory will be used");
            }
        }

        // Check Zipped Report Filename
        public FormValidation doCheckRxCompressedReportFile(@QueryParameter String value, @QueryParameter String rxReportFile) {
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
