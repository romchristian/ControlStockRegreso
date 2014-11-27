package movil.palermo.com.py.controlstockregreso;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
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
import movil.palermo.com.py.controlstockregreso.modelo.Vehiculo;
import movil.palermo.com.py.controlstockregreso.modelo.Vendedor;
import movil.palermo.com.py.controlstockregreso.util.UtilJson;


//import py.com.palermo.movil.movilcontrolstockretorno.RequestCodes;

public class MainCrearControl extends ActionBarActivity implements View.OnClickListener {

    private static String TAG = MainCrearControl.class.getSimpleName();

    public static final int AGREGAR_VENDEDOR = 101;
    public static final int AGREGAR_CHOFER = 102;
    public static final int AGREGAR_MOVIL = 103;
    public static final int CARGAR_PRODUCTOS = 104;

    private Button bttnCargarProductos, bttnFinalizarControl;
    private TextView txtVendedorValue, txtChoferValue, txtMovilValue;
    private ImageView searchVendedor, searchChofer, searchMovil;
    private FrameLayout recuadroVendedor, recuadroChofer, recuadroMovil;
    private boolean actualizacionCorrecta;

    //Para conectarse con la BD
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Conductor, Integer> conductorDao;
    private RuntimeExceptionDao<Vendedor, Integer> vendedorDao;
    private RuntimeExceptionDao<Producto, Integer> productoDao;
    private RuntimeExceptionDao<Vehiculo, Integer> vehiculoDao;
    private ImageView okImg;
    private Animation fadeOut;

    ProgressDialog pDialog;

    Intent intentMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Chofer: Jose Colman");
        actionBar.setSubtitle("Resp: Christian Romero");

        setContentView(R.layout.activity_main_crear_control);

        // okImg = (ImageView) findViewById(R.id.ok_img);


        recuadroVendedor = (FrameLayout) findViewById(R.id.recuadroVendedor);
        recuadroChofer = (FrameLayout) findViewById(R.id.recuadroChofer);
        recuadroMovil = (FrameLayout) findViewById(R.id.recuadroMovil);
        txtVendedorValue = (TextView) findViewById(R.id.txtVwVendedorValue);
        txtChoferValue = (TextView) findViewById(R.id.txtVwChoferValue);
        txtMovilValue = (TextView) findViewById(R.id.txtVwMovilValue);


        searchVendedor = (ImageView) findViewById(R.id.search_vendedor);
        searchChofer = (ImageView) findViewById(R.id.search_chofer);
        searchMovil = (ImageView) findViewById(R.id.search_movil);
        bttnCargarProductos = (Button) findViewById(R.id.bttnCargarProductos);
        bttnFinalizarControl = (Button) findViewById(R.id.bttnFinalizarControl);





        searchVendedor.setOnClickListener(this);
        searchChofer.setOnClickListener(this);
        searchMovil.setOnClickListener(this);
        bttnCargarProductos.setOnClickListener(this);
        bttnFinalizarControl.setOnClickListener(this);


        databaseHelper = new DatabaseHelper(this.getApplicationContext());
        productoDao = databaseHelper.getProductoDao();
        conductorDao = databaseHelper.getConductorDao();
        vendedorDao = databaseHelper.getVendedorDao();
        vehiculoDao = databaseHelper.getVehiculoDao();


        okImg = (ImageView) findViewById(R.id.ok_img);


        fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(2000);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                okImg.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Actualizando...");
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                okImg.setImageResource(R.drawable.check);
                okImg.setVisibility(View.VISIBLE);
                okImg.startAnimation(fadeOut);
            }
        });



        Animation a = AnimationUtils.loadAnimation(this, R.anim.shake);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bttnFinalizarControl.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        bttnFinalizarControl.startAnimation(a);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_crear_control, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_actualizar:
                if(estaConectado()){
                    cargaDatos();
                }
                break;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_vendedor:
                intentMain = new Intent(this, ListaVendedor.class);
                startActivityForResult(intentMain, AGREGAR_VENDEDOR);
                break;
            case R.id.search_chofer:
                intentMain = new Intent(this, ListaChofer.class);
                startActivityForResult(intentMain, AGREGAR_CHOFER);
                break;
            case R.id.search_movil:
                intentMain = new Intent(this, ListaMovil.class);
                startActivityForResult(intentMain, AGREGAR_MOVIL);
                break;
            case R.id.bttnCargarProductos:
                intentMain = new Intent(this, ListaProductos.class);
                startActivityForResult(intentMain, CARGAR_PRODUCTOS);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Object obj = data.getSerializableExtra("RESULTADO");
            switch (requestCode) {
                case AGREGAR_VENDEDOR:
                    if(obj instanceof  Vendedor) {
                        Vendedor v = (Vendedor) obj;
                        txtVendedorValue.setText(v != null ? v.getNombre() : "Seleccione un vendedor*");
                        recuadroVendedor.setBackgroundResource(R.drawable.recuadro_seleccionado);
                    }
                    break;
                case AGREGAR_CHOFER:
                    //txtChoferValue.setText(dato);
                    recuadroChofer.setBackgroundResource(R.drawable.recuadro_seleccionado);
                    //txtVwChofer.setText("Chofer: " + dato);
                    //Toast.makeText(this, "Resultado: " + dato, Toast.LENGTH_SHORT).show();
                    break;
                case AGREGAR_MOVIL:
                    //txtMovilValue.setText(dato);
                    recuadroMovil.setBackgroundResource(R.drawable.recuadro_seleccionado);
                    //txtVwMovil.setText("Móvil: " + dato);
                    //Toast.makeText(this, "Resultado: " + dato, Toast.LENGTH_SHORT).show();
                    break;
                case CARGAR_PRODUCTOS:
                    //Toast.makeText(this, "Resultado: " + dato, Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "No se seleccionó un vendedor", Toast.LENGTH_SHORT).show();
            }
        }
    }

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

    protected Boolean estaConectado(){
        if(conectadoWifi()){
            return true;
        }else{
            okImg.setImageResource(R.drawable.no_wifi);
            okImg.setVisibility(View.VISIBLE);
            okImg.startAnimation(fadeOut);
            return false;
            /*if(conectadoRedMovil()){
                return true;
            }else{

                return false;
            }*/
        }
    }




    protected Boolean conectadoWifi(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }


    protected Boolean conectadoRedMovil(){
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


    public void cargaDatos() {
        productosRequest();
        conductorRequest();
        vendedorRequest();
        vehiculoRequest();
    }

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


                                    if (vendedorDao.create(new Vendedor(obj.getInt("id"), obj.getString("nombre"), obj.getInt("depositoId"),conductorDao.queryForId(obj.getInt("conductorId")))) == 1) {
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


}
