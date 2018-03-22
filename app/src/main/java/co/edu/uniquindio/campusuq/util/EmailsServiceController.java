package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.History;
import com.google.api.services.gmail.model.HistoryMessageAdded;
import com.google.api.services.gmail.model.HistoryMessageDeleted;
import com.google.api.services.gmail.model.ListHistoryResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

import org.apache.commons.text.StringEscapeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
        Gmail mService = null;
        EmailsPresenter emailsPresenter = new EmailsPresenter(context);
        GoogleAccountCredential credential = emailsPresenter.getCredential();

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new Gmail.Builder(
                transport, jsonFactory, credential)
                .setApplicationName(context.getString(R.string.app_name))
                .build();

        try {
            String user = credential.getSelectedAccountName();
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
                    for (Message message : response.getMessages()) {
                        messages.add(getMessage(mService, user, message.getId()));
                    }
                }
            }

            for (Message message : messages) {
                if (message.getPayload() != null) {
                    String subject = "", from = "", to = "", date = "", content = "";
                    List<MessagePartHeader> headers = message.getPayload().getHeaders();
                    for (MessagePartHeader header : headers) {
                        String name = header.getName();
                        String value = StringEscapeUtils.unescapeHtml4(header.getValue());
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
                        byte[] emailBytes = Base64.decodeBase64(message.getPayload().getBody().getData());
                        content += StringEscapeUtils.unescapeHtml4(new String(emailBytes, "UTF-8"));
                    }
                    content = addParts(content, message.getPayload().getParts());

                    Email email = new Email(message.getId(), subject, from, to, date, content, message.getHistoryId());
                    emails.add(email);
                }
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(EmailsServiceController.class.getSimpleName(), e.getMessage());
            } else {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }
        return emails;
    }

    /**
     * Get Message with given ID.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param messageId ID of Message to retrieve.
     * @return Message Retrieved Message.
     * @throws IOException
     */
    public static Message getMessage(Gmail service, String userId, String messageId)
            throws IOException {
        Message message = service.users().messages().get(userId, messageId).setFormat("full").execute();

        return message;
    }

    private static String addParts(String content, List<MessagePart> parts)
            throws UnsupportedEncodingException {
        if (parts != null) {
            for (MessagePart part : parts) {
                String mimeType = part.getMimeType();
                if (mimeType != null && mimeType.startsWith("text")) {
                    byte[] emailBytes = Base64.decodeBase64(part.getBody().getData());
                    content += StringEscapeUtils.unescapeHtml4(new String(emailBytes, "UTF-8"));
                }
                content = addParts(content, part.getParts());
            }
        }
        return content;
    }

    public static boolean sendEmail(Context context, Email email) {
        boolean success = true;

        Gmail mService = null;
        EmailsPresenter emailsPresenter = new EmailsPresenter(context);
        GoogleAccountCredential credential = emailsPresenter.getCredential();

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new Gmail.Builder(
                transport, jsonFactory, credential)
                .setApplicationName(context.getString(R.string.app_name))
                .build();

        try {
            String user = credential.getSelectedAccountName();
            MimeMessage mimeMessage = createEmail(email.getTo(), email.getFrom(), email.getName(), email.getContent());
            Message message = sendMessage(mService, user, mimeMessage);
            success = message != null;
        } catch (Exception e) {
            success = false;
            if (e.getMessage() != null) {
                Log.e(EmailsServiceController.class.getSimpleName(), e.getMessage());
            } else {
                e.printStackTrace();
            }
        }

        return success;
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to email address of the receiver
     * @param from email address of the sender, the mailbox account
     * @param subject subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     */
    public static MimeMessage createEmail(String to, String from, String subject, String bodyText)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    public static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param emailContent Email to be sent.
     * @return The sent message
     * @throws MessagingException
     * @throws IOException
     */
    public static Message sendMessage(Gmail service, String userId, MimeMessage emailContent)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();
        return message;
    }

}
