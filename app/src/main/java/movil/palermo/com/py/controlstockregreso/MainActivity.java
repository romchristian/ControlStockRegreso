package movil.palermo.com.py.controlstockregreso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
    final static String PREFERENCIAS = "PREF_LOGIN";
    private boolean logueado;

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

        SharedPreferences pref = getSharedPreferences(PREFERENCIAS,MODE_PRIVATE);
        logueado = pref.getBoolean("LOGUEADO",false);
        if (logueado){
            inicializarDaos();
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Actualizando...");
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                /*okImg.setImageResource(R.drawable.check);
                okImg.setVisibility(View.VISIBLE);
                okImg.startAnimation(fadeOut);*/
                }
            });
        }else{
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
            finish();
        }


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
                        .replace(R.id.container, ListaControlFragment.newInstance(position), "LISTA_CONTROL_FRAGMENT")
                        .commit();
                break;
            default:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ListaControlFragment.newInstance(position))
                        .commit();

        }
    }

    //
    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = "Listado Controles";
                break;
            case 1:
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
                getMenuInflater().inflate(R.menu.menu_listado_control, menu);
            } else if (numeroSeccion == 1) {
                getMenuInflater().inflate(R.menu.menu_listado_control, menu);
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
                break;
            case R.id.action_settings:
                return true;
            case R.id.action_extraer_bd:
                break;
            case R.id.action_nuevo:
                Intent i = new Intent(this, MainCrearControlActivity.class);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


}
