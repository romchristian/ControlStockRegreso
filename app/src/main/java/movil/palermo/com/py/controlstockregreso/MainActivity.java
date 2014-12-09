package movil.palermo.com.py.controlstockregreso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import movil.palermo.com.py.controlstockregreso.modelo.Conductor;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.Producto;
import movil.palermo.com.py.controlstockregreso.modelo.UnidadMedida;
import movil.palermo.com.py.controlstockregreso.modelo.Vehiculo;
import movil.palermo.com.py.controlstockregreso.modelo.Vendedor;
import movil.palermo.com.py.controlstockregreso.util.UtilJson;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private static String TAG = MainActivity.class.getSimpleName();

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Conductor, Integer> conductorDao;
    private RuntimeExceptionDao<Vendedor, Integer> vendedorDao;
    private RuntimeExceptionDao<Producto, Integer> productoDao;
    private RuntimeExceptionDao<Vehiculo, Integer> vehiculoDao;
    private RuntimeExceptionDao<UnidadMedida, Integer> unidadMedidaDao;
    ProgressDialog pDialog;
    private boolean actualizacionCorrecta;
    private int numeroSeccion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = "Nuevo Control";//getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        inicializarDaos();
    }


    private void inicializarDaos() {
        databaseHelper = new DatabaseHelper(this);
        productoDao = databaseHelper.getProductoDao();
        conductorDao = databaseHelper.getConductorDao();
        vendedorDao = databaseHelper.getVendedorDao();
        vehiculoDao = databaseHelper.getVehiculoDao();
        unidadMedidaDao = databaseHelper.getUnidadMedidaDao();
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        numeroSeccion = position;

        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, MainCrearControlFragment.newInstance(position))
                        .commit();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ListaControlFragment.newInstance(position))
                        .commit();
                break;
            default:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, MainCrearControlFragment.newInstance(position))
                        .commit();

        }


    }

    //
    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = "Nuevo Control";
                break;
            case 1:
                mTitle = "Listado Controles";
                break;
            case 2:
                mTitle = "Reposiciones";
                break;
        }

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setSubtitle("Resp: Christian Romero");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            if (numeroSeccion == 0) {
                getMenuInflater().inflate(R.menu.menu_main_crear_control, menu);
            }
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_actualizar:
                if (estaConectado()) {
                    cargaDatos();
                }
                break;
            case R.id.action_settings:
                return true;
            case R.id.action_extraer_bd:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    //region Metodos para el Progress Dialog
    private void showpDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
            pDialog.setProgress(0);
        }
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void updateProgress(int progress) {
        if (pDialog.isShowing()) {
            pDialog.setProgress(progress);
        }
    }

    //endregion

    //region Metodos para verificar conexion
    protected Boolean estaConectado() {
        if (conectadoWifi()) {
            return true;
        } else {
            /*okImg.setImageResource(R.drawable.no_wifi);
            okImg.setVisibility(View.VISIBLE);
            okImg.startAnimation(fadeOut);*/
            return false;
/*if(conectadoRedMovil()){
return true;
}else{
return false;
}*/
        }
    }

    protected Boolean conectadoWifi() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

    //endregion

    //region Datos de prueba
    private void cargaDatos() {
        unidadMedidadMock();
        productoMock();
        conductorMock();
        vendedorMock();
        vehiculoMock();
/*productosRequest();
conductorRequest();
vendedorRequest();
vehiculoRequest();*/
    }

    private void productoMock() {
        Producto p1 = new Producto(218, "Kentuky 10", 25, null);
        Producto p2 = new Producto(198, "Kentuky 20", 25, null);
        Producto p3 = new Producto(3, "Palermo Blue 10", 25, null);
        Producto p4 = new Producto(4, "Palermo Red 20", 25, null);
        productoDao.create(p1);
        productoDao.create(p2);
        productoDao.create(p3);
        productoDao.create(p4);
    }

    private void conductorMock() {
        Conductor c1 = new Conductor(1, "Conductor 1", 123456);
        Conductor c2 = new Conductor(2, "Conductor 2", 456123);
        conductorDao.create(c1);
        conductorDao.create(c2);
    }

    private void vendedorMock() {
        Vendedor v1 = new Vendedor(1, "Vendedor", 1, conductorDao.queryForId(1));
        Vendedor v2 = new Vendedor(2, "Vendedor", 1, conductorDao.queryForId(2));
        vendedorDao.create(v1);
        vendedorDao.create(v2);
    }

    private void vehiculoMock() {
        Vehiculo v1 = new Vehiculo(1, "Toyota", "ADV 456");
        Vehiculo v2 = new Vehiculo(2, "Daihasu", "ADV 400");
        vehiculoDao.create(v1);
        vehiculoDao.create(v2);
    }

    private void unidadMedidadMock() {
        unidadMedidaDao.executeRaw("delete from unidadmedida");
        UnidadMedida m1 = new UnidadMedida(1, "Cajas");
        UnidadMedida m2 = new UnidadMedida(2, "Gruesas");
        UnidadMedida m3 = new UnidadMedida(3, "Cajetillas");
        UnidadMedida m4 = new UnidadMedida(4, "Unidad");
        unidadMedidaDao.create(m1);
        unidadMedidaDao.create(m2);
        unidadMedidaDao.create(m3);
        unidadMedidaDao.create(m4);
    }

    //endregion

    //region Metodos json
    private void productosRequest() {
        showpDialog();
        JsonArrayRequest req = new JsonArrayRequest(UtilJson.PREF_URL + "/productos/123456",
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
                                    if (productoDao.create(new Producto(obj.getInt("id"), obj.getString("nombre"), obj.getInt("unidadMedidadEstandar"), obj.getString("img"))) == 1) {
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
                hidepDialog();
            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    private void conductorRequest() {
        JsonArrayRequest req = new JsonArrayRequest(UtilJson.PREF_URL + "/conductores/123456",
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
                hidepDialog();
            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    private void vendedorRequest() {
        JsonArrayRequest req = new JsonArrayRequest(UtilJson.PREF_URL + "/vendedores/123456",
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
                hidepDialog();
            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    private void vehiculoRequest() {
        JsonArrayRequest req = new JsonArrayRequest(UtilJson.PREF_URL + "/vehiculos/123456",
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
                                    if (vehiculoDao.create(new Vehiculo(obj.getInt("id"), obj.getString("marca"), obj.getString("chapa"))) == 1) {
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
                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });
// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
//endregion

}
