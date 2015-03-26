package movil.palermo.com.py.controlstockregreso.util;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.controlstockregreso.AppController;
import movil.palermo.com.py.controlstockregreso.custom.GsonRequest;
import movil.palermo.com.py.controlstockregreso.modelo.Conductor;
import movil.palermo.com.py.controlstockregreso.modelo.Control;
import movil.palermo.com.py.controlstockregreso.modelo.ControlDetalle;
import movil.palermo.com.py.controlstockregreso.modelo.ControlSimple;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.Producto;
import movil.palermo.com.py.controlstockregreso.modelo.ProductoUM;
import movil.palermo.com.py.controlstockregreso.modelo.ReposicionDetalle;
import movil.palermo.com.py.controlstockregreso.modelo.ReposicionSimple;
import movil.palermo.com.py.controlstockregreso.modelo.ResponseControl;
import movil.palermo.com.py.controlstockregreso.modelo.ResponseReposicion;
import movil.palermo.com.py.controlstockregreso.modelo.Sesion;
import movil.palermo.com.py.controlstockregreso.modelo.SesionSimple;
import movil.palermo.com.py.controlstockregreso.modelo.UnidadMedida;
import movil.palermo.com.py.controlstockregreso.modelo.Vehiculo;
import movil.palermo.com.py.controlstockregreso.modelo.Vendedor;

/**
 * Created by cromero on 26/11/2014.
 */
public class UtilJson {

    public String prefUrl;
    public static final String TAG = UtilJson.class.getSimpleName();
    private Context context;
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Conductor, Integer> conductorDao;
    private RuntimeExceptionDao<Vendedor, Integer> vendedorDao;
    private RuntimeExceptionDao<Producto, Integer> productoDao;
    private RuntimeExceptionDao<Vehiculo, Integer> vehiculoDao;
    private RuntimeExceptionDao<UnidadMedida, Integer> unidadMedidaDao;

    private RuntimeExceptionDao<Sesion, Integer> sesionDao;
    private RuntimeExceptionDao<Control, Integer> controlDao;
    private RuntimeExceptionDao<ControlDetalle, Integer> controlDetalleDao;
    private RuntimeExceptionDao<ReposicionDetalle, Integer> reposcionDetalleDao;
    private RuntimeExceptionDao<ProductoUM, Integer> productoUMDao;



    public UtilJson(Context context) {
        this.context = context;
        inicializarDaos();


        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        prefUrl = sharedPrefs.getString("url_servidor","http://172.16.11.17:3080/ServicioStockRegreso/webresources/servicio");
    }


    public String getPrefUrl() {
        return prefUrl;
    }

    public void setPrefUrl(String prefUrl) {
        this.prefUrl = prefUrl;
    }

    private void inicializarDaos() {
        databaseHelper = new DatabaseHelper(context);
        sesionDao = databaseHelper.getSesionDao();
        controlDao = databaseHelper.getControlDao();
        controlDetalleDao = databaseHelper.getControlDetalleDao();
        vehiculoDao = databaseHelper.getVehiculoDao();
        vendedorDao = databaseHelper.getVendedorDao();
        conductorDao = databaseHelper.getConductorDao();
        reposcionDetalleDao = databaseHelper.getReposicionDetalleDao();
        productoUMDao = databaseHelper.getProductoUMDao();
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

    public void enviarDatos() {
        if (estaConectado()) {
            probarServicio();
        }
    }

    public void recargaYLimpiaDatos() {
        if (estaConectado()) {
            probarServicioParaLimpiar();
        }
    }

    private void probarServicio() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, prefUrl + "/test",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if (response != null && response.compareTo("true") == 0) {
                            guardaDatos();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private void guardaDatos() {
        try {
            List<Control> lista = controlDao.queryBuilder().where().eq(Control.COL_ESTADO_DESCARGA, "N").query();

            if (lista != null) {

                for (Control c : lista) {
                    cargaDetalles(c);
                    insertaRequest(c);
                }

            }

            List<Control> lista2 = controlDao.queryForAll();

            if (lista2 != null) {

                for (Control c : lista2) {
                    insertaReposicion(c);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargaDetalles(Control c) {
        try {
            c.getDetalles().clear();
            List<ControlDetalle> lista = controlDetalleDao.queryBuilder()
                    .where().eq(ControlDetalle.COL_CONTROL_NOMBRE, c)
                    .query();
            if (lista != null) {
                c.getDetalles().addAll(lista);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void insertaRequest(final Control c) throws JSONException {

        SesionSimple s = new SesionSimple(c.getSesion());
        ControlSimple cs = new ControlSimple(c);
        s.setControlSimple(cs);
        final String body = new GsonBuilder().setPrettyPrinting().create().toJson(s);

        Response.Listener<ResponseControl> listenerExito = new Response.Listener<ResponseControl>() {
            @Override
            public void onResponse(ResponseControl response) {
                if (response.isExito()) {
                    Control ct = controlDao.queryForId(response.getControlId());
                    ct.setEstadoDescarga("S");
                    controlDao.update(ct);
                    Toast.makeText(context, "Se envió con exito", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GsonRequest<ResponseControl> req = new GsonRequest<ResponseControl>(Request.Method.POST, prefUrl + "/inserta", ResponseControl.class, body, listenerExito, context);

        int socketTimeout = 50000;//50 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);

        AppController.getInstance().addToRequestQueue(req);

    }


    private void insertaReposicion(final Control c) throws JSONException {

        List<ReposicionDetalle> lista = null;
        try {
            lista = reposcionDetalleDao.queryBuilder()
                    .where().eq(ReposicionDetalle.COL_CONTROL, c)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (lista != null && !lista.isEmpty()) {

            List<ReposicionSimple> lista2 = new ArrayList<>();
            for (ReposicionDetalle r : lista) {
                ReposicionSimple rs = new ReposicionSimple(r);
                lista2.add(rs);
            }

            final String body = new GsonBuilder().setPrettyPrinting().create().toJson(lista2);

            Response.Listener<ResponseReposicion> listenerExito = new Response.Listener<ResponseReposicion>() {
                @Override
                public void onResponse(ResponseReposicion response) {

                    if (response.isExito()) {
                        Toast.makeText(context, "Exito!", Toast.LENGTH_LONG).show();
                    }
                }
            };

            GsonRequest<ResponseReposicion> req = new GsonRequest<ResponseReposicion>(Request.Method.POST, prefUrl + "/insertaReposicion", ResponseReposicion.class, body, listenerExito, context);

            AppController.getInstance().addToRequestQueue(req);
        }

    }


    private void probarServicioParaLimpiar() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, prefUrl + "/test",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if (response != null && response.compareTo("true") == 0) {
                            cargaDatos();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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


        //Toast.makeText(this,"URL: " + prefurl,Toast.LENGTH_LONG).show();

        JsonArrayRequest req = new JsonArrayRequest(prefUrl + "/productos/123456",
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

//updateProgress(Double.valueOf((i * 100) / response.length()).intValue());
                                    }
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    Toast.makeText(context,
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
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
    private void conductorRequest() {
        JsonArrayRequest req = new JsonArrayRequest(prefUrl + "/conductores/123456",
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

//updateProgress(Double.valueOf((i * 100) / response.length()).intValue());
                                    }
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    Toast.makeText(context,
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
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
    private void vendedorRequest() {
        JsonArrayRequest req = new JsonArrayRequest(prefUrl + "/vendedores/123456",
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

//updateProgress(Double.valueOf((i * 100) / response.length()).intValue());
                                    }
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    Toast.makeText(context,
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
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
    private void vehiculoRequest() {
        JsonArrayRequest req = new JsonArrayRequest(prefUrl+ "/vehiculos/123456",
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

//updateProgress(Double.valueOf((i * 100) / response.length()).intValue());
                                    }
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    Toast.makeText(context,
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
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
    private void unidadMedidaRequest() {
        JsonArrayRequest req = new JsonArrayRequest(prefUrl + "/unidades/123456",
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

//updateProgress(Double.valueOf((i * 100) / response.length()).intValue());
                                    }
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    Toast.makeText(context,
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
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
    private void productoUMRequest() {
        JsonArrayRequest req = new JsonArrayRequest(prefUrl + "/productosum/123456",
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

//updateProgress(Double.valueOf((i * 100) / response.length()).intValue());
                                    }
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    Toast.makeText(context,
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
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
}
