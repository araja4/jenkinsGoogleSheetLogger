# Jenkins Google Sheet Logger
Is a Jenkins plugin that logs pipeline run info to a specified Google Sheet. Currently this plugin logs the following run info (in this order): run date, run id, run name, overall run status, run start time, run end time, 1st stage status, 1st stage start time, 1st stage end time, 2nd stage status, 2nd stage start time, 2nd stage end time, ... , last stage status, last stage start time, last stage end time, post stage status, post stage start time, post stage end time. If any stage fails during the run the error message gets appended to the stage status.

## Getting Started
These instructions will get you a copy of this plugin up and running on your Jenkins instance.

### Prerequisites
1. [Jenkins](https://jenkins.io/download/)
   - Install the pipeline plugin 
     - In Jenkins go to `Jenkins Home-> Manage Jenkins-> Manage Plugins-> Available`
     - Search for `pipeline` and check the box for `Pipeline`
     - Click the `Download now and install after restart` button
     - Restart Jenkins
2. [JDK](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
3. [Maven](https://maven.apache.org/)
4. [Create a Google Service Account](https://developers.google.com/identity/protocols/OAuth2ServiceAccount#creatinganaccount) (you can ignore everything relating to domain-wide authority). After creating your service account, a json file will be created and downloaded to your machine. You will need this during plugin setup.
5. Make sure `Jenkins URL` is set in `Jenkins Home-> Manage Jenkins-> Configure System -> Jenkins Location`.

### Installing
1. Download this repo
2. Using command line to `cd` into the downloaded repo
3. run `mvn package` this will generate a gSheetLogger.hpi file in the target directory (./target/gSheetLogger.hpi)
4. In Jenkins go to the `Jenkins Home-> Manage Jenkins-> Manage Plugins-> Advanced`
5. Under `Upload Plugin` browse for the `gSheetLogger.hpi` file from above and click `Upload`
6. Restart Jenkins

### Plugin setup
1. Create a Google Sheet with your Google account and give your Google Service Account edit permission to this sheet (you can use the email address of the service account to do this).
2. Go to a pipeline job or create a new pipeline job in your Jenkins instance.
3. Click `configure` (skip this step if you created a new pipeline job)
4. Select the `Pipeline` tab
5. Click on the `Pipeline Syntax` hyperlink at the bottom
6. Select `gSheetLogger: Log build info to a google sheet` from the `Sample Step` dropdown menu.
7. Copy the content of the json file from prerequisite-step-3 and past it in the `Google Service Account creds` field.
8. Insert the sheet id in the `Spreadsheet id` field. This ID is the value between the "/d/" and the "/edit" in the URL of the Google Sheet created in step 1.
9. Click the `Generate Pipeline Script` button
10. Copy the output and go back to your configure pipeline tab.
11. Past the output from above in the post step of your pipeline script.
12. Click the `Save` button
13. Next time this job is run its run info will be appended to the Google Sheet from step 1.
