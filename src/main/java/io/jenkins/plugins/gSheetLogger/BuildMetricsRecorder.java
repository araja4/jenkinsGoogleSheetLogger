package io.jenkins.plugins.gSheetLogger;

import hudson.EnvVars;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BuildMetricsRecorder extends Recorder implements SimpleBuildStep {
    private String credStr; //google service account credentials. The whole json object.
    private String spreadsheetId; //its the google sheet id that you wanna write to.

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

    //this method first makes a GET request to the wfapi which return the info of the run corresponding to the build_url
    //then this method converts the json text response into a JSONObject and returns that object.
    public JSONObject wfapiResponse(String build_url) throws IOException {
        URL yahoo = new URL(build_url+"wfapi/describe");
        URLConnection yc = yahoo.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String inputLine;
        String jsonTxt = "";
        while ((inputLine = in.readLine()) != null){
            jsonTxt += inputLine;
        }
        in.close();

        return new JSONObject(jsonTxt);
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener taskListener) throws InterruptedException, IOException {

        //accessing the BUILD_URL env to use for the wfapi
        final EnvVars env = run.getEnvironment(taskListener);
        String build_url = env.get("BUILD_URL");

        //using the JSONObject to get the run info
        JSONObject jsonResponse = wfapiResponse(build_url);
        JSONArray stages = jsonResponse.getJSONArray("stages"); //stages of the pipeline
        ArrayList<Object> gSheetData = new ArrayList<Object>();
        Date runDate = new Date((long)jsonResponse.get("startTimeMillis"));
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        gSheetData.add(formatter.format(runDate));
        gSheetData.add(jsonResponse.getString("id"));
        gSheetData.add(jsonResponse.getString("name"));

        //this method has no effect when the result is already set and worse than the proposed result
        //only setting this so getResult isn't null.
        run.setResult(Result.SUCCESS);
        gSheetData.add(run.getResult().toString());

        formatter = new SimpleDateFormat("HH:mm:ss");
        gSheetData.add(formatter.format(runDate));
        gSheetData.add(formatter.format(new Date((long)jsonResponse.get("endTimeMillis"))));
        for(int i=0; i<stages.length(); i++){
            JSONObject stage = stages.getJSONObject(i);
            String stageStatus = stage.getString("status");
            if (stageStatus.equals("FAILED"))
                stageStatus += " error message: " + stage.getJSONObject("error").getString("message");
            gSheetData.add(stageStatus);
            gSheetData.add(formatter.format(new Date((long)stage.get("startTimeMillis"))));

            //there is no stage level endTime in the wfapi so generating endTime using startTim+durationTime
            gSheetData.add(formatter.format(new Date((long)stage.get("startTimeMillis")+(int)stage.get("durationMillis"))));
        }

        GoogleSheetLogger sheet = new GoogleSheetLogger();
        try {
            String response = sheet.log(credStr, spreadsheetId, gSheetData.toArray());
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
        }
    }


}
