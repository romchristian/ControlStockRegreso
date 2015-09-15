package movil.palermo.com.py.stockregresomovil.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.stockregresomovil.AppController;
import movil.palermo.com.py.stockregresomovil.SplashActivity;
import movil.palermo.com.py.stockregresomovil.custom.ControlListAdapter;
import movil.palermo.com.py.stockregresomovil.custom.GsonRequest;
import movil.palermo.com.py.stockregresomovil.modelo.Conductor;
import movil.palermo.com.py.stockregresomovil.modelo.Control;
import movil.palermo.com.py.stockregresomovil.modelo.ControlDetalle;
import movil.palermo.com.py.stockregresomovil.modelo.ControlSimple;
import movil.palermo.com.py.stockregresomovil.modelo.DatabaseHelper;
import movil.palermo.com.py.stockregresomovil.modelo.Producto;
import movil.palermo.com.py.stockregresomovil.modelo.ProductoUM;
import movil.palermo.com.py.stockregresomovil.modelo.ReposicionDetalle;
import movil.palermo.com.py.stockregresomovil.modelo.ReposicionLista;
import movil.palermo.com.py.stockregresomovil.modelo.ReposicionSimple;
import movil.palermo.com.py.stockregresomovil.modelo.ResponseControl;
import movil.palermo.com.py.stockregresomovil.modelo.ResponseRegistro;
import movil.palermo.com.py.stockregresomovil.modelo.ResponseReposicion;
import movil.palermo.com.py.stockregresomovil.modelo.Sesion;
import movil.palermo.com.py.stockregresomovil.modelo.SesionSimple;
import movil.palermo.com.py.stockregresomovil.modelo.SupervisorSimple;
import movil.palermo.com.py.stockregresomovil.modelo.UnidadMedida;
import movil.palermo.com.py.stockregresomovil.modelo.Vehiculo;
import movil.palermo.com.py.stockregresomovil.modelo.Vendedor;

/**
 * Created by cromero on 26/11/2014.
 */
public class UtilJson {

    final public static String OPERACION_GUARDA_DATOS = "guarda_datos";
    final public static String OPERACION_CARGA_DATOS = "carga_datos";
    final public static String OPERACION_CARGA_CONTROLES = "carga_controles";

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

    private List<Control> controles;
    private ControlListAdapter controlesAdapter;
    public String telefonoId;


    public UtilJson(Context context) {
        this.context = context;
        inicializarDaos();


        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        prefUrl = sharedPrefs.getString("url_servidor", "http://spkt.palermo.com.py:41180/ServicioStockRegreso/webresources/servicio");
        //prefUrl = sharedPrefs.getString("url_servidor", "http://172.16.8.78:8080/ServicioStockRegreso/webresources/servicio");
        controles = new ArrayList<>();
        telefonoId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d("POCKETID", this.getTelefonoId());


    }


    public String getTelefonoId() {
        return telefonoId;
    }

    public void setTelefonoId(String telefonoId) {
        this.telefonoId = telefonoId;
    }

    public String getPrefUrl() {
        return prefUrl;
    }

    public void setPrefUrl(String prefUrl) {
        this.prefUrl = prefUrl;
    }


    public List<Control> getControles() {
        return controles;
    }

    public void setControles(List<Control> controles) {
        this.controles = controles;
    }

    public ControlListAdapter getControlesAdapter() {
        return controlesAdapter;
    }

    public void setControlesAdapter(ControlListAdapter controlesAdapter) {
        this.controlesAdapter = controlesAdapter;
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
        productoDao = databaseHelper.getProductoDao();
        productoUMDao = databaseHelper.getProductoUMDao();
        unidadMedidaDao = databaseHelper.getUnidadMedidaDao();
    }


    public Boolean estaConectado() {
        return conectadoWifi();
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
            probarServicio(OPERACION_GUARDA_DATOS);
        }
    }

    public void enviarRegistro(SupervisorSimple s) {
        if (estaConectado()) {
            probarServicioRegistro(s);
        }
    }

    private void probarServicioRegistro(final SupervisorSimple s) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, prefUrl + "/test",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if (response != null && response.compareTo("true") == 0) {
                            registra(s);

                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void recargaControles() {
        if (estaConectado()) {
            probarServicio(OPERACION_CARGA_CONTROLES);
        }
    }

    public void recargaYLimpiaDatos() {
        if (estaConectado()) {
            Toast.makeText(this.context, "Base de Datos Actualizada", Toast.LENGTH_SHORT).show();
            probarServicio(OPERACION_CARGA_DATOS);
        }
    }

    private void probarServicio(final String operacion) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, prefUrl + "/test",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if (response != null && response.compareTo("true") == 0) {
                            switch (operacion) {
                                case OPERACION_GUARDA_DATOS:
                                    guardaDatos();
                                    break;
                                case OPERACION_CARGA_DATOS:
                                    cargaDatos();
                                    break;
                                case OPERACION_CARGA_CONTROLES:
                                    cargaControles();
                                    break;
                            }

                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void registra(SupervisorSimple s) {
        try {
            registroRequest(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        SesionSimple s = new SesionSimple(c.getSesion(), telefonoId);
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
            ReposicionLista reposiciones = new ReposicionLista(lista2, telefonoId);

            //final String body = new GsonBuilder().setPrettyPrinting().create().toJson(lista2);
            final String body = new GsonBuilder().setPrettyPrinting().create().toJson(reposiciones);

            Response.Listener<ResponseReposicion> listenerExito = new Response.Listener<ResponseReposicion>() {
                @Override
                public void onResponse(ResponseReposicion response) {

                    if (response.isExito()) {
                        Toast.makeText(context, "Exito!", Toast.LENGTH_LONG).show();
                    }
                }
            };

            GsonRequest<ResponseReposicion> req = new GsonRequest<ResponseReposicion>(Request.Method.POST, prefUrl + "/insertaReposicion", ResponseReposicion.class, body, listenerExito, context);

            int socketTimeout = 50000;//50 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req.setRetryPolicy(policy);


            AppController.getInstance().addToRequestQueue(req);
        }

    }


    private void cargaDatos() {

        vehiculoDao.executeRaw("DELETE FROM controldetalle");
        vehiculoDao.executeRaw("DELETE FROM control");

        productosRequest();
        //conductorRequest();
        //vendedorRequest();
        vehiculoRequest();
        unidadMedidaRequest();
        productoUMRequest();
    }

    private void cargaControles() {
        controlesRequest();
    }

    private void controlesRequest() {

        JsonArrayRequest req = new JsonArrayRequest(prefUrl + "/controlesautorizados/" + this.telefonoId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        if (response != null && response.length() > 0) {

                            if (controles != null) {
                                controles.clear();
                            }

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    String uuid = obj.getString("uuid");
                                    Log.d(TAG, "Response: " + response);

                                    List<Control> lista = null;
                                    try {
                                        lista = controlDao.queryBuilder()
                                                .where().eq(Control.COL_UUID, uuid)
                                                .query();

                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    Log.d(TAG, "Lista: " + lista);

                                    if (lista == null || lista.size() == 0) {
                                        Log.d(TAG, "Lista 2: " + lista);

                                        Sesion sesionActual = sesionDao.queryForId(1);
                                        Control c = new Control();
                                        c.setEstado("L");
                                        c.setSesion(sesionActual);
                                        c.setFechaControl(sesionActual.getFechaControl());
                                        c.setUuid(uuid);
                                        Conductor con = conductorDao.queryForId(obj.getInt("conductorId"));
                                        c.setConductor(con);
                                        Vendedor v = vendedorDao.queryForId(obj.getInt("vendedorId"));
                                        Vehiculo vh = vehiculoDao.queryForId(obj.getInt("vehiculoId"));
                                        c.setVendedor(v);
                                        c.setVehiculo(vh);
                                        c.setVehiculoChapa(vh.getChapa());
                                        c.setEstadoDescarga("S");
                                        controlDao.create(c);

                                    }

                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    Toast.makeText(context,
                                            "Error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }

                            listener();
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

        int socketTimeout = 50000;//50 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);

        AppController.getInstance().addToRequestQueue(req);
    }


    public void listener() {
        try {

            Log.d(TAG, "LISTENER...");
            controles.clear();
            controles.addAll(controlDao.queryBuilder().where().eq(Control.COL_ESTADO, "L").query());
            Log.d(TAG, "CONTROLES... " + controles);

            controlesAdapter.notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void productosRequest() {


        //Toast.makeText(this,"URL: " + prefurl,Toast.LENGTH_LONG).show();

        JsonArrayRequest req = new JsonArrayRequest(prefUrl + "/productos/" + this.telefonoId,
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
                                    if (productoDao.create(new Producto(obj.getInt("id"), obj.getString("nombre"), obj.getInt("unidadMedidadEstandar"), obj.getString("img"), obj.getInt("productoKit"), obj.getInt("orden"))) == 1) {

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
        int socketTimeout = 50000;//50 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(req);
    }

    private void conductorRequest() {
        JsonArrayRequest req = new JsonArrayRequest(prefUrl + "/conductores/" + this.telefonoId,
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
                                    //return y == null ? null : y.intValue();
                                    if (conductorDao.create(new Conductor(obj.getInt("id"), obj.getString("nombre"), obj.getInt("ci"), vehiculoDao.queryForId(obj.isNull("vehiculoId") ? 0 : obj.getInt("vehiculoId")))) == 1) {

//updateProgress(Double.valueOf((i * 100) / response.length()).intValue());
                                    }
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    Toast.makeText(context,
                                            "Error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            vendedorRequest();
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
        int socketTimeout = 50000;//50 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(req);
    }

    private void vendedorRequest() {
        JsonArrayRequest req = new JsonArrayRequest(prefUrl + "/vendedores/" + this.telefonoId,
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
        int socketTimeout = 50000;//50 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(req);
    }

    private void vehiculoRequest() {

        JsonArrayRequest req = new JsonArrayRequest(prefUrl + "/vehiculos/" + this.telefonoId,
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
                                    if (vehiculoDao.create(new Vehiculo(obj.getInt("id"), obj.getString("marca"), obj.getString("chapa"), obj.getInt("nro"), obj.getInt("idMarca"))) == 1) {

//updateProgress(Double.valueOf((i * 100) / response.length()).intValue());
                                    }
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    Toast.makeText(context,
                                            "Error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            conductorRequest();
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
        int socketTimeout = 50000;//50 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(req);
    }

    private void unidadMedidaRequest() {
        JsonArrayRequest req = new JsonArrayRequest(prefUrl + "/unidades/" + this.telefonoId,
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
        int socketTimeout = 50000;//50 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(req);
    }

    private void productoUMRequest() {
        JsonArrayRequest req = new JsonArrayRequest(prefUrl + "/productosum/" + this.telefonoId,
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
                                    if (productoUMDao.create(new ProductoUM(obj.getInt("productoId"), obj.getInt("unidadMedidaId"), obj.getInt("cantidad"))) == 1) {

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

                        if (context instanceof SplashActivity) {
                            SplashActivity s = (SplashActivity) context;
                            s.finalizarSplash();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                if (context instanceof SplashActivity) {
                    SplashActivity s = (SplashActivity) context;
                    s.finalizarSplash();
                }

            }
        });
// Adding request to request queue
        int socketTimeout = 50000;//50 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(req);
    }


    private void registroRequest(final SupervisorSimple su) throws JSONException {

        final String body = new GsonBuilder().setPrettyPrinting().create().toJson(su);

        Request req = new JsonRequest<ResponseRegistro>(Request.Method.POST, prefUrl + "/registra", body,
                new Response.Listener<ResponseRegistro>() {
                    @Override
                    public void onResponse(ResponseRegistro response) {
                        if (response.isExito()) {
                            Toast.makeText(context, "Se Registro con exito!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "No se puede registrar, ya existe!", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "ERROR AL REGISTRAR: " + error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Response<ResponseRegistro> parseNetworkResponse(NetworkResponse response) {
                String jsonString = null;
                try {
                    jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                ResponseRegistro resp = new GsonBuilder().create().fromJson(jsonString, ResponseRegistro.class);
                Response<ResponseRegistro> result = Response.success(resp, HttpHeaderParser.parseCacheHeaders(response));
                return result;
            }
        };
        int socketTimeout = 20000;//50 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(req);
    }
}
