package co.edu.uniquindio.campusuq.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.twitter.sdk.android.tweetcomposer.TweetUploadService;

/**
 * Receptor de twitter.
 */
public class TwitterReceiver extends BroadcastReceiver {

    /**
     * Funcion que responde a las recepciones.
     * @param context Contecto utilizado para realizar la operacion.
     * @param intent Intento.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (TweetUploadService.UPLOAD_SUCCESS.equals(intent.getAction())) {
            // success
            Toast.makeText(context, "Operación correcta", Toast.LENGTH_SHORT).show();
        } else if (TweetUploadService.UPLOAD_FAILURE.equals(intent.getAction())) {
            // failure
            Toast.makeText(context, "Error en la operación", Toast.LENGTH_SHORT).show();
        } else if (TweetUploadService.TWEET_COMPOSE_CANCEL.equals(intent.getAction())) {
            // cancel
        }
    }

}
