package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.model.History;
import com.google.api.services.gmail.model.HistoryMessageAdded;
import com.google.api.services.gmail.model.HistoryMessageDeleted;
import com.google.api.services.gmail.model.ListHistoryResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.vo.Email;

/**
 * Created by Juan Camilo on 20/03/2018.
 */
/**
 * Attempt to call the API, after verifying that all the preconditions are
 * satisfied. The preconditions are: Google Play Services installed, an
 * account was selected and the device currently has online access. If any
 * of the preconditions are not satisfied, the app will prompt the user as
 * appropriate.
 */
public class EmailsServiceController {

    public static ArrayList<Email> getEmails(Context context, BigInteger startHistoryID) {
        ArrayList<Email> emails = new ArrayList<>();
        com.google.api.services.gmail.Gmail mService = null;
        EmailsPresenter emailsPresenter = new EmailsPresenter(context);
        GoogleAccountCredential credential = emailsPresenter.getCredential();

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.gmail.Gmail.Builder(
                transport, jsonFactory, credential)
                .setApplicationName(context.getString(R.string.app_name))
                .build();

        try {
            String user = "me";
            List<Message> messages = new ArrayList<>();
            boolean reSync = true;

            if (startHistoryID != null) {
                List<History> histories = new ArrayList<History>();
                ListHistoryResponse response = mService.users().history().list(user).setStartHistoryId(startHistoryID).execute();
                reSync = response.getHistory() == null;
                while (response.getHistory() != null) {
                    histories.addAll(response.getHistory());
                    if (response.getNextPageToken() != null) {
                        String pageToken = response.getNextPageToken();
                        response = mService.users().history().list(user).setPageToken(pageToken)
                                .setStartHistoryId(startHistoryID).execute();
                    } else {
                        break;
                    }
                }

                for (History history : histories) {
                    List<HistoryMessageAdded> messagesAdded = history.getMessagesAdded();
                    if (messagesAdded != null) {
                        for (HistoryMessageAdded messageAdded : messagesAdded) {
                            messages.add(messageAdded.getMessage());
                        }
                    }
                    List<HistoryMessageDeleted> messagesDeleted = history.getMessagesDeleted();
                    if (messagesDeleted != null) {
                        EmailsSQLiteController dbController = new EmailsSQLiteController(context, 1);
                        ArrayList<String> ids = new ArrayList<>();
                        for (HistoryMessageDeleted messageDeleted : messagesDeleted) {
                            ids.add(messageDeleted.getMessage().getId());
                        }
                        dbController.delete(ids);
                        dbController.destroy();
                    }
                }
            }

            if (reSync) {
                ListMessagesResponse response = mService.users().messages().list(user).execute();
                if (response.getMessages() != null) {
                    messages = response.getMessages();
                }
                    /*
                    while (response.getMessages() != null) {
                        messages.addAll(response.getMessages());
                        if (response.getNextPageToken() != null) {
                            String pageToken = response.getNextPageToken();
                            response = mService.users().messages().list(user).setPageToken(pageToken).execute();
                        } else {
                            break;
                        }
                    }
                    */
            }

            for (Message message : messages) {
                String subject = "", from = "", to = "", date = "", content = "";
                List<MessagePartHeader> headers = message.getPayload().getHeaders();
                for (MessagePartHeader header : headers) {
                    String name = header.getName();
                    String value = header.getValue();
                    switch (name) {
                        case "Subject":
                            subject = value;
                            break;
                        case "From":
                            from = value;
                            break;
                        case "To":
                            to = value;
                            break;
                        case "Date":
                            date = value;
                            break;
                        default:
                            break;
                    }
                }
                String mimeType = message.getPayload().getMimeType();
                if (mimeType != null && mimeType.startsWith("text")) {
                    content += message.getPayload().getBody().getData();
                }
                content = addParts(content, message.getPayload().getParts());

                Email email = new Email(Integer.parseInt(message.getId()), subject, from, to, date, content, message.getHistoryId());
                emails.add(email);
            }
        } catch (Exception e) {
            Log.e(EmailsServiceController.class.getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
        return emails;
    }

    private static String addParts(String content, List<MessagePart> parts) {
        if (parts != null) {
            for (MessagePart part : parts) {
                String mimeType = part.getMimeType();
                if (mimeType != null && mimeType.startsWith("text")) {
                    content += part.getBody().getData();
                }
                content = addParts(content, part.getParts());
            }
        }
        return content;
    }

}
