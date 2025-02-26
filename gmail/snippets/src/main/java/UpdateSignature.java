// Copyright 2022 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


// [START gmail_update_signature]
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.SendAs;
import com.google.api.services.gmail.model.ListSendAsResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;

/* Class to demonstrate the use of Gmail Update Signature API */
public class UpdateSignature {
    /**
     * Update the gmail signature.
     *
     * @return the updated signature id , {@code null} otherwise.
     * @throws IOException - if service account credentials file not found.
     */
    public static String updateGmailSignature() throws IOException {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application. */
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(GmailScopes.GMAIL_SETTINGS_BASIC);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        // Create the gmail API client
        Gmail service = new Gmail.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Gmail samples")
                .build();

        try {
            SendAs primaryAlias = null;
            ListSendAsResponse aliases = service.users().settings().sendAs().list("me").execute();
            for (SendAs alias : aliases.getSendAs()) {
                if (alias.getIsPrimary()) {
                    primaryAlias = alias;
                    break;
                }
            }
            // Updating a new signature
            SendAs aliasSettings = new SendAs().setSignature("Automated Signature");
            SendAs result = service.users().settings().sendAs().patch(
                            "me",
                            primaryAlias.getSendAsEmail(),
                            aliasSettings)
                    .execute();
            //Prints the updated signature
            System.out.println("Updated signature - " + result.getSignature());
            return result.getSignature();
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Unable to update signature: " + e.getDetails());
            } else {
                throw e;
            }
        }
        return null;
    }
}
// [END gmail_update_signature]