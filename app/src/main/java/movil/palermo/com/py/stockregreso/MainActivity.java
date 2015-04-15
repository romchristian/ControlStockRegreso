package movil.palermo.com.py.stockregreso;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.Calendar;
import java.util.Date;

import movil.palermo.com.py.stockregreso.modelo.Conductor;
import movil.palermo.com.py.stockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.stockregreso.modelo.Producto;
import movil.palermo.com.py.stockregreso.modelo.ReposicionDetalle;
import movil.palermo.com.py.stockregreso.modelo.UnidadMedida;
import movil.palermo.com.py.stockregreso.modelo.Vehiculo;
import movil.palermo.com.py.stockregreso.modelo.Vendedor;
import movil.palermo.com.py.stockregreso.util.UtilJson;


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
    private RuntimeExceptionDao<ReposicionDetalle, Integer> reposcionDetalleDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = "Stock de Regreso";//getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        SharedPreferences pref = getSharedPreferences(PREFERENCIAS,MODE_PRIVATE);
        logueado = pref.getBoolean("LOGUEADO",false);
        //logueado = true;
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
        reposcionDetalleDao = databaseHelper.getReposicionDetalleDao();
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        numeroSeccion = position;
        switch (position) {
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ListaControlFragment.newInstance(position), "LISTA_CONTROL_FRAGMENT")
                        .commit();
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#669900")));

                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ListaReposicionFragment.newInstance(position), "LISTA_REPOSICION_FRAGMENT")
                        .commit();

                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#AC58FA")));

                /*final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                LayoutInflater inflater=this.getLayoutInflater();
                //this is what I did to added the layout to the alert dialog
                View layout=inflater.inflate(R.layout.dialog_login,null);
                final EditText passwordInput=(EditText)layout.findViewById(R.id.txtPassword);
                alert.setView(layout);
                alert.setTitle("Acceso a configuraciones");
                alert.setMessage("Ingrese la contraseña:");
                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (passwordInput.getText().toString().equalsIgnoreCase("test")){
                            Intent settings = new Intent(alert.getContext(),SettingsActivity.class);
                            startActivity(settings);
                        }else
                        {
                            Toast.makeText(alert.getContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();*/



                break;
            case 3:
                logOut();
                break;

            default:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ListaControlFragment.newInstance(position), "LISTA_CONTROL_FRAGMENT")
                        .commit();
        }
    }

    //
    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = "Stock de Regreso";
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
        SharedPreferences sp = getSharedPreferences(LoginActivity.PREFERENCIAS,MODE_PRIVATE);
        actionBar.setSubtitle(sp.getString("NOMBRE",""));
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
                databaseHelper.extraerBD("hola");
                break;
            case R.id.action_enviar:
                UtilJson util = new UtilJson(this);
                util.enviarDatos();
                break;
            case R.id.action_nuevo:
                Intent i = new Intent(this, MainCrearControlActivity.class);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        logOut();
    }

    public void logOut(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Cerrar Sesión?");
        dialog.setPositiveButton("SI",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences pref = getSharedPreferences(LoginActivity.PREFERENCIAS,MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("LOGUEADO", false);
                editor.commit();
                finish();
            }
        });

        dialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog.show();
    }


    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


}
