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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.web.WebService;

/**
 * Utilidades.
 */
public class Utilities {

    public static final String CATEGORY = "CATEGORY";
    public static final String FEEDBACK = "FEEDBACK";
    public static final String SUBCATEGORY = "SUBCATEGORY";
    public static final String ITEMS = "ITEMS";
    public static final String URL = "URL";
    public static final String LINK = "LINK";
    public static final String SELECT_ALL = "SELECT_ALL";

    public static final String URL_SERVICIO = "https://campus-uq.000webhostapp.com";
    static final String NOMBRE_BD = "Campus_UQ";

    public static final String PREFERENCES = "preferences";
    public static final String PREFERENCE_LANGUAGE = "language_preferences";
    public static final String LANGUAGE_ES = "es";
    private static final String LANGUAGE_EN = "en";
    public static final String CALENDAR_NOTIFY = "calendar_notify";

    public static final int SUCCESS_STATE = 11;
    public static final int FAILURE_STATE = 12;

    public static final long UPLOAD_FILE_MAX_MB = 11;

    /**
     * Permite obtener el keyHash para la utilizacion de facebookSDK.
     * @param context Contexto utilizado para realizar la operacion.
     */
    public static void getKeyHash(Context context) {
        try {
            PackageInfo info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("KeyHash:", sign);
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determina si hay o no conexión a internet.
     * @param context Contexto con el cual realizar el proceso.
     * @return Verdadero si hay conexión a internet, falso en caso contrario.
     */
    public static boolean haveNetworkConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    /**
     * Obtiene un dialogo de progreso que puede ser vertical u horizontal.
     * @param context Contexto con el cual crear el dialogo.
     * @param vertical Valor booleano que indica si se quiere el dialogo vertical u horizontal.
     * @return Nuevo dialogo de progreso.
     */
    public static ProgressDialog getProgressDialog(Context context, boolean vertical) {
        ProgressDialog pDialog = new ProgressDialog(context);

        if (vertical) {
            pDialog.setTitle(context.getString(R.string.loading_content));
            pDialog.setMessage(context.getString(R.string.please_wait));
        } else {
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setTitle(R.string.downloading_data);
            pDialog.setMessage(context.getString(R.string.wait_to_informations));
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

    /**
     * Almacena un elemento multimedia en la subruta especificada descargandolo desde la direccion
     * url especificada.
     * @param spec Direccion url desde la cual descargar el elemento multimedia.
     * @param path Subruta donde almacenar el elemento multimedia.
     * @param context Contexto con el cual realizar el proceso.
     * @return Ruta en la cual se almacenó el elemento multimedia, o null en caso de error.
     */
    public static String saveMedia(String spec, String path, Context context) {
        if (spec != null) try {
            File dir = new File(context.getExternalFilesDir(null).getAbsolutePath() +
                    "/CampusUQ/.Media" + path);
            dir.mkdirs();
            File file = new File(dir, spec.substring(spec.lastIndexOf('/') + 1));

            copy(new URL(spec).openConnection().getInputStream(), new FileOutputStream(file));

            return file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Escribe el contenido del flujo de entrada en el flujo de salida, garantizando cerrar ambos
     * flujos al finalizar este metodo.
     * @param inputStream Flujo de entrada desde el cual leer.
     * @param outputStream Flujo de salida en el cual escribir.
     * @throws IOException Si hay un error al leer el flujo de entrada o al escribir en el flujo de
     * salida.
     */
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

    /**
     * Cambia el lenguaje almacenado en la preferencias compartidas intercalando entre ingles y
     * español.
     * @param context Contexto.
     */
    @SuppressLint("ApplySharedPref")
    public static void changeLanguage(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(PREFERENCE_LANGUAGE, LANGUAGE_ES.equals(sharedPreferences
                .getString(PREFERENCE_LANGUAGE, LANGUAGE_ES)) ? LANGUAGE_EN : LANGUAGE_ES).commit();
        getLanguage(context);
    }

    /**
     * Obtiene un nuevo contexto actualizado con el nuevo lenguaje obtenido de las preferencias
     * compartidas.
     * @param context Contexto actual a partir del cual obtener el nuevo contexto.
     * @return Nuevo contexto con el lenguaje actualizado.
     */
    public static Context getLanguage(Context context){
        Locale locale = new Locale(context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getString(PREFERENCE_LANGUAGE, LANGUAGE_ES), "CO");
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        return context.createConfigurationContext(configuration);
    }

    /**
     * Obtiene un mapa de bits redimencionado a un maximo horizontal o vertical de 500, conservando
     * la relacion de aspecto.
     * @param image Mapa de bits a redimencionar.
     * @return Mapa de bits redimencionado.
     */
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

    /**
     * Obtiene la ruta de un elemento multimedia a partir de su Uri.
     * @param context Contexto.
     * @param uri Uri del elemento multimedia.
     * @return Ruta del elemento multimedia.
     */
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
