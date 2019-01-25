# jenkinsGoogleSheetLogger
Jenkins plugin which logs pipeline run info to a specified google sheet. Currently this plugin logs the following run info (in this order): run date, run id, run name, overall run status, run start time, run end time, 1st stage status, 1st stage start time, 1st stage end time, 2nd stage status, 2nd stage start time, 2nd stage end time, ... , last stage status, last stage start time, last stage end time, post stage status, post stage start time, post stage end time. If any stage failed during the run the error message gets appeneded to the stage status.

##To install the plugin
If you do not have JDK and/or Maven already download and install them [JDK] (https://www.oracle.com/technetwork/java/javase/downloads/index.html) [Maven] (https://maven.apache.org/)
Download this repo
Using commandline cd into the dowloaded repo
run mvn hpi:hpi this will generate a gSheetLogger.hpi file in the target directory (./target/gSheetLogger.hpi)
In your Jenkins instance go to 
