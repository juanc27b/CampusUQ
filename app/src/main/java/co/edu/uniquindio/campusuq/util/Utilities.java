package co.edu.uniquindio.campusuq.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Juan Camilo on 8/02/2018.
 */

public class Utilities {

    public final static String URL_SERVICIO =
            "http://10.0.2.2:3000/api/peliculas";

    public static final int LISTAR_PELICULAS = 1;
    public static final int AGREGAR_PELICULA= 2;
    public static final int MODIFICAR_PELICULA = 3;
    public static final int ELIMINAR_PELICULA = 4;

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


}
