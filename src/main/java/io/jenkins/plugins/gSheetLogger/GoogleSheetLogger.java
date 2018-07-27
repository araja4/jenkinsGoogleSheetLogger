package io.jenkins.plugins.gSheetLogger;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GoogleSheetLogger {
    private static final String APPLICATION_NAME = "Jenkins gSheetLogger"; //maybe update this later too
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved credentials/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);


    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     */
    public String log(String credStr, String spreadsheetId, Object [] value) throws GeneralSecurityException, IOException {

        GoogleCredential credential = GoogleCredential.fromStream(new ByteArrayInputStream(credStr.getBytes(StandardCharsets.UTF_8))).createScoped(SCOPES);
        String range = "A:B";
        final String valueInputOption = "USER_ENTERED";
        // How the input data should be inserted.
        final String insertDataOption = "INSERT_ROWS";

        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        value
                )
                // Additional rows ...
        );
        ValueRange requestBody = new ValueRange().setValues(values);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets sheetsService = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        Sheets.Spreadsheets.Values.Append request = sheetsService.spreadsheets().values().append(spreadsheetId, range, requestBody);
        request.setValueInputOption(valueInputOption);
        request.setInsertDataOption(insertDataOption);

        AppendValuesResponse response = request.execute();

        return response.toString();



    }
}
