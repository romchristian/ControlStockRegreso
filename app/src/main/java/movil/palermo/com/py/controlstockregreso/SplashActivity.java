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
            inicializarDaos();
            probarServicio();
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

    private void inicializarDaos() {
        databaseHelper = new DatabaseHelper(this);
        productoDao = databaseHelper.getProductoDao();
        conductorDao = databaseHelper.getConductorDao();
        vendedorDao = databaseHelper.getVendedorDao();
        vehiculoDao = databaseHelper.getVehiculoDao();
        unidadMedidaDao = databaseHelper.getUnidadMedidaDao();
        productoUMDao = databaseHelper.getProductoUMDao();
    }

    private void probarServicio() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,(new UtilJson(this).prefUrl) + "/test" ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if(response!= null && response.compareTo("true") ==0){
                            cargaDatos();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                vehiculoDao.executeRaw("DELETE FROM controldetalle");
                vehiculoDao.executeRaw("DELETE FROM control");

                try {
                    UpdateBuilder<Vehiculo,Integer> ubVehiculo = vehiculoDao.updateBuilder();
                    ubVehiculo.updateColumnValue("estado", "N");
                    ubVehiculo.where().eq("estado","U");
                    ubVehiculo.update();


                    UpdateBuilder<Vendedor,Integer> ubVendedor = vendedorDao.updateBuilder();
                    ubVendedor.updateColumnValue("estado", "N");
                    ubVendedor.where().eq("estado","U");
                    ubVendedor.update();

                    UpdateBuilder<Conductor,Integer> ubConductor = conductorDao.updateBuilder();
                    ubConductor.updateColumnValue("estado", "N");
                    ubConductor.where().eq("estado","U");
                    ubConductor.update();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void cargaDatos() {

        vehiculoDao.executeRaw("DELETE FROM controldetalle");
        vehiculoDao.executeRaw("DELETE FROM control");

        productosRequest();
        conductorRequest();
        vendedorRequest();
        vehiculoRequest();
        unidadMedidaRequest();
        productoUMRequest();
    }
    private void productosRequest() {
        String prefurl = new UtilJson(this).prefUrl;

        //Toast.makeText(this,"URL: " + prefurl,Toast.LENGTH_LONG).show();

        JsonArrayRequest req = new JsonArrayRequest(prefurl + "/productos/123456",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        if (response != null && response.length() > 0) {
// Limpio la tabla
                            productoDao.executeRaw("delete from producto");
// Ahora cargo la tabla
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    if (productoDao.create(new Producto(obj.getInt("id"), obj.getString("nombre"), obj.getInt("unidadMedidadEstandar"), obj.getString("img"), obj.getInt("productoKit"),obj.getInt("orden"))) == 1) {
                                        actualizacionCorrecta = true;
//updateProgress(Double.valueOf((i * 100) / response.length()).intValue());
                                    }
                                } catch (JSONException e) {
                                    actualizacionCorrecta = false;
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),
                                            "Error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                onErrorSplash();
            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
    private void conductorRequest() {
        JsonArrayRequest req = new JsonArrayRequest((new UtilJson(this).prefUrl) + "/conductores/123456",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        if (response != null && response.length() > 0) {
// Limpio la tabla
                            conductorDao.executeRaw("delete from conductor");
// Ahora cargo la tabla
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    if (conductorDao.create(new Conductor(obj.getInt("id"), obj.getString("nombre"), obj.getInt("ci"))) == 1) {
                                        actualizacionCorrecta = true;
//updateProgress(Double.valueOf((i * 100) / response.length()).intValue());
                                    }
                                } catch (JSONException e) {
                                    actualizacionCorrecta = false;
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),
                                            "Error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                onErrorSplash();
            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
    private void vendedorRequest() {
        JsonArrayRequest req = new JsonArrayRequest((new UtilJson(this).prefUrl) + "/vendedores/123456",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        if (response != null && response.length() > 0) {
// Limpio la tabla
                            vendedorDao.executeRaw("delete from vendedor");
// Ahora cargo la tabla
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    if (vendedorDao.create(new Vendedor(obj.getInt("id"), obj.getString("nombre"), obj.getInt("depositoId"), conductorDao.queryForId(obj.getInt("conductorId")))) == 1) {
                                        actualizacionCorrecta = true;
//updateProgress(Double.valueOf((i * 100) / response.length()).intValue());
                                    }
                                } catch (JSONException e) {
                                    actualizacionCorrecta = false;
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),
                                            "Error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                onErrorSplash();
            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
    private void vehiculoRequest() {
        JsonArrayRequest req = new JsonArrayRequest((new UtilJson(this).prefUrl)+ "/vehiculos/123456",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        if (response != null && response.length() > 0) {
// Limpio la tabla
                            vehiculoDao.executeRaw("delete from vehiculo");
// Ahora cargo la tabla
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    if (vehiculoDao.create(new Vehiculo(obj.getInt("id"), obj.getString("marca"), obj.getString("chapa"),obj.getInt("nro"),obj.getInt("idMarca"))) == 1) {
                                        actualizacionCorrecta = true;
//updateProgress(Double.valueOf((i * 100) / response.length()).intValue());
                                    }
                                } catch (JSONException e) {
                                    actualizacionCorrecta = false;
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),
                                            "Error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                onErrorSplash();
            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
    private void unidadMedidaRequest() {
        JsonArrayRequest req = new JsonArrayRequest((new UtilJson(this).prefUrl) + "/unidades/123456",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        if (response != null && response.length() > 0) {
// Limpio la tabla
                            unidadMedidaDao.executeRaw("delete from unidadmedida");
// Ahora cargo la tabla
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    if (unidadMedidaDao.create(new UnidadMedida(obj.getInt("id"), obj.getString("nombre"))) == 1) {
                                        actualizacionCorrecta = true;
//updateProgress(Double.valueOf((i * 100) / response.length()).intValue());
                                    }
                                } catch (JSONException e) {
                                    actualizacionCorrecta = false;
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),
                                            "Error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            finalizarSplash();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                onErrorSplash();
            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
    private void productoUMRequest() {
        JsonArrayRequest req = new JsonArrayRequest((new UtilJson(this).prefUrl) + "/productosum/123456",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        if (response != null && response.length() > 0) {
// Limpio la tabla
                            productoUMDao.executeRaw("delete from productoum");
// Ahora cargo la tabla
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    if (productoUMDao.create(new ProductoUM(obj.getInt("productoId"), obj.getInt("unidadMedidaId"),obj.getInt("cantidad"))) == 1) {
                                        actualizacionCorrecta = true;
//updateProgress(Double.valueOf((i * 100) / response.length()).intValue());
                                    }
                                } catch (JSONException e) {
                                    actualizacionCorrecta = false;
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),
                                            "Error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            finalizarSplash();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                onErrorSplash();
            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }


}
