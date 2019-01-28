# jenkinsGoogleSheetLogger
Is a Jenkins plugin that logs pipeline run info to a specified Google Sheet. Currently this plugin logs the following run info (in this order): run date, run id, run name, overall run status, run start time, run end time, 1st stage status, 1st stage start time, 1st stage end time, 2nd stage status, 2nd stage start time, 2nd stage end time, ... , last stage status, last stage start time, last stage end time, post stage status, post stage start time, post stage end time. If any stage fails during the run the error message gets appeneded to the stage status.

## How to install this plugin
0. If you do not already have the Java Development Kit (JDK) and/or Maven. Download and install them here: [JDK](https://www.oracle.com/technetwork/java/javase/downloads/index.html), [Maven](https://maven.apache.org/)
1. Download this repo
2. Using commandline `cd` into the dowloaded repo
3. run `mvn hpi:hpi` this will generate a gSheetLogger.hpi file in the target directory (./target/gSheetLogger.hpi)
4. In your Jenkins instance go to the _Advanced_ tab of the _Plugin Manager_ (Jenkins Home->Manage Jenkins->Manage Plugins->Advanced)
5. Under _Upload Plugin_ browse for the gSheetLogger.hpi file from above and click _Upload_
6. Restart your Jenkins instance.

## How to use this plugin
1. [Creat a Google Service Account](https://developers.google.com/identity/protocols/OAuth2ServiceAccount#creatinganaccount) (you can ignore everything relating to domain-wide authority). After creating your service account a json file will be created and downloaded to your machine. You will need this later.
2. Creat a Google Sheet with your Google account and give your Google Service Account edit permission to this sheet (you can use the email address of the service account to do this).
3. Go to a pipeline job or creat a new pipeline job in your Jenkins instance.
4. Click _configure_ (skip this step if you created a new pipeline job).
5. Select the _Pipeline_ tab.
6. Click on the _Pipeline Syntax_ hyperlink at the bottom.
7. Select _gSheetLogger: Log build info to a google sheet_ from the dropdown menu for Sample Step.
6. Copy the content of the json file from step 1 and past it in the _Google Service Account creds_ field.
7. Insert the sheet id in the _Spreadsheet id_ field. This ID is the value between the "/d/" and the "/edit" in the URL of the Google Sheet created in step 2.
8. Click the _Generate Pipeline Script_ button.
9. Copy the output and go back to your configure pipeline tab.
10. Past the output from above in the post step of your pipeline script.
11. Click the _Save_ button.
12. Next time this job is run its run info will be logged to the Google Sheet from step 2.


