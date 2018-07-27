package io.jenkins.plugins.gSheetLogger;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BuildMetricsRecorder extends Recorder implements SimpleBuildStep {
    private String credStr;
    private String spreadsheetId;

    @DataBoundConstructor
    public BuildMetricsRecorder(String spreadsheetId, String credStr){
        this.credStr = credStr;
        this.spreadsheetId = spreadsheetId;
    }

    public String getCredStr() {
        return credStr;
    }

    public void setCredStr(String credStr) {
        this.credStr = credStr;
    }

    public String getSpreadsheetId() {
        return spreadsheetId;
    }

    public void setSpreadsheetId(String spreadsheetId) {
        this.spreadsheetId = spreadsheetId;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener taskListener) throws InterruptedException, IOException {


        Date runDate = new Date(run.getStartTimeInMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String runDateStr = formatter.format(runDate);

        int runNum = run.number;
        String runDuration = run.getTimestampString();

        run.setResult(Result.SUCCESS); //explain why this doesn't affect the build result
        String result = run.getResult().toString();

        GoogleSheetLogger sheet = new GoogleSheetLogger();
        try {
            String response = sheet.log(credStr, spreadsheetId, new Object[] {runDateStr, runNum, runDuration, result});
            taskListener.getLogger().println("Google Sheet Logger response: "+response);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Symbol("gSheetLogger")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }
        @Override
        public String getDisplayName() {
            return "Log build info to a google sheet";
        } //take care of this later
    }


}
