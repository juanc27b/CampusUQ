package co.edu.uniquindio.campusuq.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.google.api.client.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import co.edu.uniquindio.campusuq.R;

/**
 * Created by Juan Camilo on 8/02/2018.
 */

public class Utilities {

    public final static String URL_SERVICIO = "https://campus-uq.000webhostapp.com";
    public static final String NOMBRE_BD = "Campus_UQ";

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

    public static ProgressDialog getProgressDialog(Context context, boolean cancelable) {
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setTitle(context.getString(R.string.loading_content));
        pDialog.setMessage(context.getString(R.string.please_wait));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(cancelable);
        pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                WebService.PENDING_ACTION = WebService.ACTION_NONE;
            }
        });
        return pDialog;
    }

    static String saveImage(String spec, String path, Context context) {
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

}
