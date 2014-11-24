package movil.palermo.com.py.controlstockregreso;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.Producto;
import movil.palermo.com.py.controlstockregreso.modelo.Vehiculo;


public class ActualizaActivity extends OrmLiteBaseActivity<DatabaseHelper> implements View.OnClickListener{

    private static final String url = "http://172.16.8.78:8080/ServicioStockRegreso/webresources/servicio/productos/12345";
    private static final String urlVehiculos = "http://172.16.8.78:8080/ServicioStockRegreso/webresources/servicio/vehiculos/12345";
    private static final String TAG = ActualizaActivity.class.getSimpleName();

    private Button button;
    private CheckBox checkBoxProductos;
    private CheckBox checkBoxVehiculos;

    private RuntimeExceptionDao<Producto, Integer> productoDao;
    private RuntimeExceptionDao<Vehiculo, Integer> vehiculoDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualiza);

        button = (Button) findViewById(R.id.button);
        checkBoxProductos = (CheckBox) findViewById(R.id.checkBoxProductos);
        checkBoxVehiculos = (CheckBox) findViewById(R.id.checkBoxVehiculos);

        productoDao = getHelper().getProductoRuntimeDao();
        vehiculoDao = getHelper().getVehiculoRuntimeDao();

        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        productoDao.executeRaw("delete from producto");
        productoDao.executeRaw("delete from vehiculo");

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(getRequestProductos());
        AppController.getInstance().addToRequestQueue(getRequestVehiculos());
    }

    public JsonArrayRequest getRequestProductos(){
        final RuntimeExceptionDao<Producto, Integer> productoDao = getHelper().getProductoRuntimeDao();
        JsonArrayRequest productosReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());


                        // Parsing json
                        int acumulado = 0;
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Producto producto = new Producto(obj.getInt("id"),obj.getString("nombre"),obj.getInt("unidadMedidadEstandar"),obj.getString("img"));


                                if(productoDao.create(producto)==1){
                                    acumulado +=1;

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(acumulado == response.length()){
                                checkBoxProductos.setChecked(true);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });


        return productosReq;
    }

    public JsonArrayRequest getRequestVehiculos(){

        JsonArrayRequest vehiculosReq = new JsonArrayRequest(urlVehiculos,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());


                        // Parsing json
                        int acumulado = 0;
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Vehiculo vehiculo = new Vehiculo(obj.getInt("id"),obj.getString("marca"),obj.getString("chapa"));

                                // adding movie to movies array
                                if(vehiculoDao.create(vehiculo)==1){
                                    acumulado +=1;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(acumulado == response.length()){
                                checkBoxVehiculos.setChecked(true);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });


        return vehiculosReq;
    }
}
