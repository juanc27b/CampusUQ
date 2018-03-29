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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import co.edu.uniquindio.campusuq.R;

/**
 * Created by Juan Camilo on 8/02/2018.
 */

public class Utilities {

    public final static String URL_SERVICIO = "https://campus-uq.000webhostapp.com";
    public static final String NOMBRE_BD = "Campus_UQ";

    public final static String PREFERENCES = "preferences";
    public final static String PREFERENCE_LANGUAGE = "language_preferences";
    public final static String LANGUAGE_ES = "es";
    public final static String LANGUAGE_EN = "en";


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
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        return isConnected;
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
            pDialog.setMax(120);
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

    public static String saveImage(String url, Context context) {

        String imagePath = null;
        InputStream is = null;

        try {
            URL ImageUrl = new URL(url);

            HttpURLConnection conn = (HttpURLConnection) ImageUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bmImg = BitmapFactory.decodeStream(is, null, options);

            String path = ImageUrl.getPath();
            String idStr = path.substring(path.lastIndexOf('/') + 1);
            File filepath = Environment.getExternalStorageDirectory();
            File dir = new File(filepath.getAbsolutePath() + "/CampusUQ/Media/Images");
            dir.mkdirs();

            File file = new File(dir, idStr);
            if (file.exists()) {
                file = new File(dir, "new_"+idStr);
            }
            FileOutputStream fos = new FileOutputStream(file);
            bmImg.compress(Bitmap.CompressFormat.JPEG, 75, fos);
            fos.flush();
            fos.close();

            imagePath = file.getPath();
            MediaScannerConnection.scanFile(context,
                    new String[]{imagePath},
                    new String[]{"image/jpeg"}, null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return imagePath;

    }

    public static void deleteHistory(Context context) {
        new ObjectsPresenter().deleteHistory(context);
        new AnnouncementsPresenter().deleteHistory(context);
    }

    public static void changeLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, context.MODE_PRIVATE);
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
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, context.MODE_PRIVATE);
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
