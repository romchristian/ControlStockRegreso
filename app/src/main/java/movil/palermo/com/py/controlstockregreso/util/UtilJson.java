package movil.palermo.com.py.controlstockregreso.util;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by cromero on 26/11/2014.
 */
public class UtilJson {

    public static final String PREF_URL = "http://172.16.11.17:3080/ServicioStockRegreso/webresources/servicio";
    public static final String TAG = UtilJson.class.getSimpleName();
    private Activity context;


    public UtilJson(Activity context) {
        this.context = context;
    }

    public Boolean estaConectado() {
        if (conectadoWifi()) {
            return true;
        } else {

            return false;
        }
    }


    protected Boolean conectadoWifi() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return true;
    }

    protected Boolean conectadoRedMovil() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
}
