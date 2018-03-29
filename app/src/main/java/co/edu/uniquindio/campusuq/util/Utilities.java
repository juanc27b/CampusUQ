package co.edu.uniquindio.campusuq.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.api.client.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
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

    private final static String PREFERENCES = "preferences";
    private final static String PREFERENCE_LANGUAGE = "language_preferences";
    private final static String LANGUAGE_ES = "es";
    private final static String LANGUAGE_EN = "en";


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
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public static String saveImage(String spec, String path, Context context) {
        String imagePath = null;

        if (spec != null) try {
            URLConnection connection = new URL(spec).openConnection();
            connection.connect();

            File dir = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath()+"/CampusUQ/Media/Images"+path);
            dir.mkdirs();
            File file = new File(dir, spec.substring(spec.lastIndexOf('/')+1));
            FileOutputStream output = new FileOutputStream(file);

            IOUtils.copy(connection.getInputStream(), output);
            output.close();

            imagePath = file.getPath();
            MediaScannerConnection.scanFile(context, new String[]{imagePath}, null,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imagePath;
    }

    public static void deleteHistory(Context context) {
        ObjectsPresenter.deleteHistory(context);
        AnnouncementsPresenter.deleteHistory(context);
    }

    public static void changeLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        String language = prefs.getString(PREFERENCE_LANGUAGE, LANGUAGE_ES);
        if(language.equals(LANGUAGE_ES)) {
            language = LANGUAGE_EN;
        } else if(language.equals(LANGUAGE_EN)) {
            language = LANGUAGE_ES;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFERENCE_LANGUAGE, language);
        editor.commit();
        getLanguage(context);
    }

    public static void getLanguage(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        String language = prefs.getString(PREFERENCE_LANGUAGE, LANGUAGE_ES);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        configuration.setLocale(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            context.createConfigurationContext(configuration);
        } else {
            resources.updateConfiguration(configuration, displayMetrics);
        }
    }

}
