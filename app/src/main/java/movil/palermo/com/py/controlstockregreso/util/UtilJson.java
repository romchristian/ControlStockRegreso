package movil.palermo.com.py.controlstockregreso.util;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import movil.palermo.com.py.controlstockregreso.AppController;
import movil.palermo.com.py.controlstockregreso.modelo.Conductor;
import movil.palermo.com.py.controlstockregreso.modelo.Producto;
import movil.palermo.com.py.controlstockregreso.modelo.UnidadMedida;
import movil.palermo.com.py.controlstockregreso.modelo.Vehiculo;
import movil.palermo.com.py.controlstockregreso.modelo.Vendedor;

/**
 * Created by cromero on 26/11/2014.
 */
public class UtilJson {

    private static final String PREF_URL = "http://172.16.8.78:8080/ServicioStockRegreso/webresources/servicio";
    private static final String TAG = UtilJson.class.getSimpleName();

    private RuntimeExceptionDao<Conductor, Integer> conductorDao;
    private RuntimeExceptionDao<Vendedor, Integer> vendedorDao;
    private RuntimeExceptionDao<Producto, Integer> productoDao;
    private RuntimeExceptionDao<Vehiculo, Integer> vehiculoDao;
    private boolean actualizacionCorrecta = false;
    private ProgressDialog p;

    public UtilJson(RuntimeExceptionDao<Conductor, Integer> conductorDao, RuntimeExceptionDao<Vendedor, Integer> vendedorDao, RuntimeExceptionDao<Producto, Integer> productoDao, RuntimeExceptionDao<Vehiculo, Integer> vehiculoDao,ProgressDialog p) {
        this.conductorDao = conductorDao;
        this.vendedorDao = vendedorDao;
        this.productoDao = productoDao;
        this.vehiculoDao = vehiculoDao;
        this.p = p;
    }


    public void empiezaServicio(){
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
                        p.dismiss();
                        Log.d("UtilJson","Response: "+response.length());
                        cargaDB(response,url);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d("UtilJson","Error Json");
            }
        });
        return R;
    }

    public void cargaDB(JSONArray response, String url){
        if(url.contains("productos")){
            insertaProductos(response);
        } else if(url.contains("conductores")){
            Log.d("UtilJson","En el if");
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
            Log.d("UtilJson","Entro en insertar");
            conductorDao.executeRaw("delete from conductor");

            // Ahora cargo la tabla

            for (int i = 0; i < response.length(); i++) {
                try {

                    JSONObject obj = response.getJSONObject(i);
                    if(conductorDao.create(new Conductor(obj.getInt("id"), obj.getString("nombre"), obj.getInt("ci"))) == 1) {
                        Log.d("UtilJson","C"+i);
                        actualizacionCorrecta = true;
                    }
                } catch (JSONException e) {
                    Log.d("UtilJson","Errror");
                    actualizacionCorrecta = false;
                   // p.dismiss();
                    e.printStackTrace();
                }
            }

            if(actualizacionCorrecta){
                Log.d("UtilJson","Termino");
                //p.dismiss();
             //   AppController.getInstance().addToRequestQueue(getJsonRequest(PREF_URL+"/vendedores/"+getPhoneId()));
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
                    p.dismiss();
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
                    p.dismiss();
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
                    p.dismiss();
                    e.printStackTrace();
                }
            }

            if (actualizacionCorrecta ){
                p.dismiss();
            }
        }
    }
}
