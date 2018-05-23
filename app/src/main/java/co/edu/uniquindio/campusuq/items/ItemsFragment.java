package co.edu.uniquindio.campusuq.items;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.edu.uniquindio.campusuq.R;

public class ItemsFragment extends DialogFragment implements View.OnClickListener {

    private static final String ITEM = "item";

    private ItemsActivity itemsActivity;
    private Item item;
    private RadioButton addContact;
    private RadioButton call;

    public static ItemsFragment newInstance(Item item) {
        Bundle args = new Bundle();
        args.putParcelable(ITEM, item);

        ItemsFragment fragment = new ItemsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        itemsActivity = (ItemsActivity) getActivity();
        assert itemsActivity != null;

        @SuppressLint("InflateParams") View view = itemsActivity.getLayoutInflater()
                .inflate(R.layout.fragment_dialog, null);

        Bundle args = getArguments();
        assert args != null;

        item = args.getParcelable(ITEM);
        addContact = view.findViewById(R.id.dialog_modify);
        call = view.findViewById(R.id.dialog_delete);

        ((TextView) view.findViewById(R.id.dialog_name)).setText(item.getTitle());
        addContact.setText(R.string.add_contact);
        call.setText(R.string.call);
        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_ok).setOnClickListener(this);

        return new AlertDialog.Builder(itemsActivity).setView(view).create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_ok:
                Matcher matcher = Pattern.compile(
                        "<b id='2'>.+?</b> (.+?)\\s*(?:Ext.*?)?<br/><b id='3'>",
                        Pattern.CASE_INSENSITIVE).matcher(item.getDescription());

                if (addContact.isChecked()) {
                    ArrayList<ContentProviderOperation> operationList = new ArrayList<>();
                    operationList.add(ContentProviderOperation
                            .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                            .build());
                    operationList.add(ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                                    0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract
                                    .CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                    item.getTitle())
                            .build());

                    if (matcher.find()) {
                        operationList.add(ContentProviderOperation
                                .newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                                        0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                                        matcher.group(1))
                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                                .build());
                    }

                    matcher = Pattern.compile("<b id='3'>.+?</b> (.+?)<br/><b id='4'>")
                            .matcher(item.getDescription());

                    if (matcher.find()) {
                        operationList.add(ContentProviderOperation
                                .newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                                        0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS,
                                        matcher.group(1))
                                .withValue(ContactsContract.CommonDataKinds.Email.TYPE,
                                        ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                                .build());
                    }

                    ContentProviderOperation.Builder builder = ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data
                                    .RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract
                                    .CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Organization
                                    .COMPANY, "Universidad del Quindío")
                            .withValue(ContactsContract.CommonDataKinds.Organization.TYPE,
                                    ContactsContract.CommonDataKinds.Organization.TYPE_WORK);

                    matcher = Pattern.compile("<b id='4'>.+?</b> (.+?)<br/><b id='5'>")
                            .matcher(item.getDescription());

                    if (matcher.find()) {
                        builder.withValue(ContactsContract.CommonDataKinds.Organization
                                .TITLE, matcher.group(1));
                    }

                    matcher = Pattern.compile("<b id='1'>.+?</b> (.+?)<br/><b id='2'>")
                            .matcher(item.getDescription());

                    if (matcher.find()) {
                        builder.withValue(ContactsContract.CommonDataKinds.Organization
                                .DEPARTMENT, matcher.group(1));
                    }

                    operationList.add(builder.build());

                    matcher = Pattern.compile("<b id='5'>.+?</b> (.+)")
                            .matcher(item.getDescription());

                    if (matcher.find()) {
                        operationList.add(ContentProviderOperation
                                .newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                                        0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Note.NOTE,
                                        matcher.group(1))
                                .build());
                    }

                    try {
                        itemsActivity.getContentResolver()
                                .applyBatch(ContactsContract.AUTHORITY, operationList);
                        Toast.makeText(itemsActivity, R.string.contact_added,
                                Toast.LENGTH_SHORT).show();
                    } catch (RemoteException | OperationApplicationException e) {
                        e.printStackTrace();
                        Toast.makeText(itemsActivity,
                                getString(R.string.contact_error, e.getLocalizedMessage()),
                                Toast.LENGTH_SHORT).show();
                    }
                } else if (call.isChecked() && ActivityCompat.checkSelfPermission(itemsActivity,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    if (matcher.find()) {
                        itemsActivity.startActivity(new Intent(Intent.ACTION_CALL,
                                Uri.parse("tel:"+matcher.group(1))));
                    } else {
                        Toast.makeText(itemsActivity, R.string.invalid_phone,
                                Toast.LENGTH_SHORT).show();
                    }
                }
                // Tanto ok como cancel cierran el dialogo, por eso aqui no hay break
            case R.id.dialog_cancel:
                dismiss();
                break;
        }
    }

}
