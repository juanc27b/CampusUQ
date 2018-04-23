package co.edu.uniquindio.campusuq.util;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.announcements.AnnouncementsPresenter;
import co.edu.uniquindio.campusuq.objects.ObjectsPresenter;
import co.edu.uniquindio.campusuq.web.WebService;

/**
 * Created by Juan Camilo on 8/02/2018.
 */

public class Utilities {

    public final static String URL_SERVICIO = "https://campus-uq.000webhostapp.com";
    public static final String NOMBRE_BD = "Campus_UQ";

    public final static String PREFERENCES = "preferences";
    private final static String PREFERENCE_LANGUAGE = "language_preferences";
    private final static String LANGUAGE_ES = "es";
    private final static String LANGUAGE_EN = "en";
    public final static String CALENDAR_NOTIFY = "calendar_notify";

    public static final int SUCCESS_STATE = 11;
    public static final int FAILURE_STATE = 12;

    public static final long UPLOAD_FILE_MAX_MB = 11;

    public static void getKeyHash(Context context) {
        try {
            PackageInfo info =
                    context.getPackageManager().getPackageInfo(context.getPackageName(),
                            PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(),
                        Base64.DEFAULT);
                Log.d("HASH KEY:", sign);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("Test", "1 KeyHash Error: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.d("Test", "2 KeyHash Error: " + e.getMessage());
        }
    }

    public static boolean haveNetworkConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static ProgressDialog getProgressDialog(Context context, boolean vertical) {
        ProgressDialog pDialog = new ProgressDialog(context);
        if (vertical) {
            pDialog.setTitle(context.getString(R.string.loading_content));
            pDialog.setMessage(context.getString(R.string.please_wait));
        } else {
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setTitle(context.getString(R.string.downloading_data));
            pDialog.setMessage(context.getString(R.string.wait_to));
            pDialog.setMax(12);
            pDialog.setProgress(0);
        }
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                WebService.PENDING_ACTION = WebService.ACTION_NONE;
            }
        });
        return pDialog;
    }

    public static String saveMedia(String spec, String path, Context context) {
        String imagePath = null;

        if (spec != null) try {
            File dir = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/CampusUQ/Media" + path);
            dir.mkdirs();
            File file = new File(dir, spec.substring(spec.lastIndexOf('/') + 1));

            copy(new URL(spec).openConnection().getInputStream(), new FileOutputStream(file));

            imagePath = file.getPath();
            MediaScannerConnection
                    .scanFile(context, new String[]{imagePath}, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imagePath;
    }

    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        try {
            for (byte[] buffer = new byte[4096]; ; ) {
                int bytesRead = inputStream.read(buffer);
                if (bytesRead == -1) break;
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            try {
                inputStream.close();
            } finally {
                outputStream.close();
            }
        }
    }

    public static void deleteHistory(Context context) {
        ObjectsPresenter.deleteHistory(context);
        AnnouncementsPresenter.deleteHistory(context);
    }

    @SuppressLint("ApplySharedPref")
    public static void changeLanguage(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(PREFERENCE_LANGUAGE, LANGUAGE_ES.equals(sharedPreferences
                .getString(PREFERENCE_LANGUAGE, LANGUAGE_ES)) ? LANGUAGE_EN : LANGUAGE_ES).commit();
        getLanguage(context);
    }

    public static Context getLanguage(Context context){
        Locale locale = new Locale(context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getString(PREFERENCE_LANGUAGE, LANGUAGE_ES), "CO");
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        return context.createConfigurationContext(configuration);
    }

    public static Bitmap getResizedBitmap(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = width / (float) height;

        if (bitmapRatio > 0) {
            width = 500;
            height = (int) (width / bitmapRatio);
        } else {
            height = 500;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static String getPath(Context context, Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null,
                null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow(projection[0]));
            }

            cursor.close();
        }

        return path;
    }

}
