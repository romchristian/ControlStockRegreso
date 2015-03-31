package movil.palermo.com.py.controlstockregreso;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import movil.palermo.com.py.controlstockregreso.modelo.Conductor;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.Producto;
import movil.palermo.com.py.controlstockregreso.modelo.ProductoUM;
import movil.palermo.com.py.controlstockregreso.modelo.UnidadMedida;
import movil.palermo.com.py.controlstockregreso.modelo.Vehiculo;
import movil.palermo.com.py.controlstockregreso.modelo.Vendedor;
import movil.palermo.com.py.controlstockregreso.util.UtilJson;


public class SplashActivity extends ActionBarActivity {
    ImageView logo;
    TextView mensaje;
    final static String TAG = "SPLASH";
    final static String PREFERENCIAS = "PREF_SPLASH";
    final static String PREF_LOGIN = "PREF_LOGIN";
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Conductor, Integer> conductorDao;
    private RuntimeExceptionDao<Vendedor, Integer> vendedorDao;
    private RuntimeExceptionDao<Producto, Integer> productoDao;
    private RuntimeExceptionDao<Vehiculo, Integer> vehiculoDao;
    private RuntimeExceptionDao<UnidadMedida, Integer> unidadMedidaDao;
    private RuntimeExceptionDao<ProductoUM, Integer> productoUMDao;
    private boolean  actualizacionCorrecta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        logo = (ImageView) findViewById(R.id.logo);
        mensaje = (TextView) findViewById(R.id.mensaje);
        logo.startAnimation(AnimationUtils.loadAnimation(this,R.anim.pulse_splash));
        SharedPreferences pref = getSharedPreferences(PREFERENCIAS,MODE_PRIVATE);

        String fecha = pref.getString("FECHA","");
        Log.d("FECHA_PREF","FECHA_PREF " + fecha );
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        // Para forzar que se actualice new Date(1000 * 60 * 60 * 24)
        String fechaActual = sdf.format(new Date());
        Log.d("FECHA_ACTUAL","FECHA_ACTUAL " + fechaActual);
        if (fecha.compareTo(fechaActual) !=0){
            SharedPreferences pref_login = getSharedPreferences(PREF_LOGIN,MODE_PRIVATE);
            SharedPreferences.Editor editor = pref_login.edit();
            editor.putBoolean("LOGUEADO",false);
            new UtilJson(this.getApplicationContext()).recargaYLimpiaDatos();
            //inicializarDaos();
            //probarServicio();
        }else{
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    finalizarSplash();
                }
            };

            // Simulate a long loading process on application startup.
            Timer timer = new Timer();
            timer.schedule(task, 50);
        }
    }

    private void finalizarSplash() {
        SharedPreferences pref = getSharedPreferences(PREFERENCIAS,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        editor.putString("FECHA",sdf.format(new Date()));
        editor.commit();
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }


    private void onErrorSplash() {
        mensaje.setText("Error al actualizar...");
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        };

        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, 2000);
    }
}
