package movil.palermo.com.py.controlstockregreso;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import movil.palermo.com.py.controlstockregreso.modelo.Conductor;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.Producto;
import movil.palermo.com.py.controlstockregreso.modelo.Vehiculo;
import movil.palermo.com.py.controlstockregreso.modelo.Vendedor;


public class ActualizaActivity extends OrmLiteBaseActivity<DatabaseHelper>{

    private static final String PREF_URL = "http://172.16.8.78:8080/ServicioStockRegreso/webresources/servicio";
    private static final String TAG = ActualizaActivity.class.getSimpleName();

    private RuntimeExceptionDao<Conductor, Integer> conductorDao;
    private RuntimeExceptionDao<Vendedor, Integer> vendedorDao;
    private RuntimeExceptionDao<Producto, Integer> productoDao;
    private RuntimeExceptionDao<Vehiculo, Integer> vehiculoDao;
    private DatabaseHelper databaseHelper;

    private ProgressBar progressBar;

    private boolean actualizacionCorrecta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualiza);

        conductorDao = getHelper().getConductorDao();
        vendedorDao = getHelper().getVendedorDao();
        productoDao = getHelper().getProductoDao();
        vehiculoDao = getHelper().getVehiculoDao();


        // Las llamadas estan encadenadas
        AppController.getInstance().addToRequestQueue(getJsonRequest(PREF_URL+"/conductores/"+getPhoneId()));

    }

    private int getPhoneId(){
        return 123456;
    }

    public JsonArrayRequest getJsonRequest(final String url){
        JsonArrayRequest R = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        cargaDB(response,url);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        return R;
    }
    public void cargaDB(JSONArray response, String url){
      if(url.contains("productos")){
          insertaProductos(response);
      } else if(url.contains("conductores")){
          insertaConductores(response);
      }else if(url.contains("vendedores")){
          insertaVendedores(response);
      }else if(url.contains("vehiculos")){
          insertaVehiculos(response);
      }
    }
    private void insertaConductores(JSONArray response) {
        if(response!= null && response.length() > 0) {
            // Limpio la tabla
            conductorDao.executeRaw("delete from conductor");

            // Ahora cargo la tabla

            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject obj = response.getJSONObject(i);
                    if(conductorDao.create(new Conductor(obj.getInt("id"), obj.getString("nombre"), obj.getInt("ci"))) == 1) {
                        actualizacionCorrecta = true;
                    }
                } catch (JSONException e) {
                    actualizacionCorrecta = false;
                    e.printStackTrace();
                }
            }

            if(actualizacionCorrecta){
                AppController.getInstance().addToRequestQueue(getJsonRequest(PREF_URL+"/vendedores/"+getPhoneId()));
            }

        }
    }
    private void insertaVendedores(JSONArray response) {
        if(response!= null && response.length() > 0) {
            // Limpio la tabla
            vendedorDao.executeRaw("delete from vendedor");

            // Ahora cargo la tabla
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject obj = response.getJSONObject(i);
                    if(vendedorDao.create(new Vendedor(obj.getInt("id"), obj.getString("nombre"), obj.getInt("depositoId"),conductorDao.queryForId(obj.getInt("conductorId")))) == 1) {
                        actualizacionCorrecta = true;
                    }
                } catch (JSONException e) {
                    actualizacionCorrecta = false;
                    e.printStackTrace();

                }
            }

            if(actualizacionCorrecta){
                AppController.getInstance().addToRequestQueue(getJsonRequest(PREF_URL+"/vehiculos/"+getPhoneId()));
            }
        }
    }
    private void insertaVehiculos(JSONArray response) {
        if(response!= null && response.length() > 0) {
            // Limpio la tabla
            vehiculoDao.executeRaw("delete from vehiculo");

            // Ahora cargo la tabla

            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject obj = response.getJSONObject(i);
                    if(vehiculoDao.create(new Vehiculo(obj.getInt("id"), obj.getString("marca"), obj.getString("chapa"))) == 1) {
                        actualizacionCorrecta = true;
                    }
                } catch (JSONException e) {
                    actualizacionCorrecta = false;
                    e.printStackTrace();

                }
            }

            if(actualizacionCorrecta){
                AppController.getInstance().addToRequestQueue(getJsonRequest(PREF_URL+"/productos/"+getPhoneId()));
            }
        }
    }
    private void insertaProductos(JSONArray response) {
        if(response!= null && response.length() > 0) {
            // Limpio la tabla
            productoDao.executeRaw("delete from producto");

            // Ahora cargo la tabla

            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject obj = response.getJSONObject(i);
                    if(productoDao.create(new Producto(obj.getInt("id"), obj.getString("nombre"), obj.getInt("unidadMedidadEstandar"), obj.getString("img"))) == 1) {
                        actualizacionCorrecta = true;
                    }
                } catch (JSONException e) {
                    actualizacionCorrecta = false;
                    e.printStackTrace();
                }
            }
        }
    }
}
