package movil.palermo.com.py.controlstockregreso;


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
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import movil.palermo.com.py.controlstockregreso.modelo.Conductor;
import movil.palermo.com.py.controlstockregreso.modelo.Control;
import movil.palermo.com.py.controlstockregreso.modelo.ControlDetalle;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.Producto;
import movil.palermo.com.py.controlstockregreso.modelo.Sesion;
import movil.palermo.com.py.controlstockregreso.modelo.UnidadMedida;
import movil.palermo.com.py.controlstockregreso.modelo.Vehiculo;
import movil.palermo.com.py.controlstockregreso.modelo.Vendedor;
import movil.palermo.com.py.controlstockregreso.util.UtilJson;

/**
 * Created by Christian on 08/12/2014.
 */
public class MainCrearControlActivity extends ActionBarActivity implements View.OnClickListener {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static String TAG = MainCrearControlActivity.class.getSimpleName();

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
    private RuntimeExceptionDao<UnidadMedida, Integer> unidadMedidaDao;

    private RuntimeExceptionDao<Sesion, Integer> sesionDao;
    private RuntimeExceptionDao<Control, Integer> controlDao;
    private RuntimeExceptionDao<ControlDetalle, Integer> controlDetalleDao;


    private ImageView okImg;
    private Animation fadeOut;
    ProgressDialog pDialog;
    Intent intentMain;

    private Vendedor vendedorSeleccionado;
    private Conductor conductorSeleccionado;
    private Vehiculo vehiculoSeleccionado;
    private Integer kmVehiculoSeleccionado;
    private Sesion sesionActual;
    private Control controlActual;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_crear_control);
        inicializarViews();
        asignarListeners();
        inicializarDaos();
        inicializaSesion();

        Object obj = getIntent().getSerializableExtra("CONTROL");
        Log.d("log1", "Antes del if" + obj);
        if (obj != null && obj instanceof Control) {
            Log.d("log2", "despues del if" + obj);
            controlActual = (Control) obj;
            vendedorSeleccionado = controlActual.getVendedor();
            conductorSeleccionado = controlActual.getConductor();
            vehiculoSeleccionado = controlActual.getVehiculo();
            txtVendedorValue.setText(vendedorSeleccionado.getNombre());
            recuadroVendedor.setBackgroundResource(R.drawable.recuadro_seleccionado);
            txtChoferValue.setText(conductorSeleccionado.getNombre());
            recuadroChofer.setBackgroundResource(R.drawable.recuadro_seleccionado);
            txtMovilValue.setText(vehiculoSeleccionado.getMarca() + " Chapa: " + vehiculoSeleccionado.getChapa());
            recuadroMovil.setBackgroundResource(R.drawable.recuadro_seleccionado);
            bttnCargarProductos.setEnabled(sePuedeCargarProductos());
            mostrarBotonFinalzar();
        } else {
            esconderBotonFinalzar();
        }


    }

    public Control getControlActual() {
        if (controlActual == null) {
            controlActual = new Control();
        }
        return controlActual;
    }

    private void inicializarViews() {
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
    }

    private void asignarListeners() {
        searchVendedor.setOnClickListener(this);
        searchChofer.setOnClickListener(this);
        searchMovil.setOnClickListener(this);
        bttnCargarProductos.setOnClickListener(this);
        bttnFinalizarControl.setOnClickListener(this);
    }

    private void inicializarDaos() {
        databaseHelper = new DatabaseHelper(this);
        sesionDao = databaseHelper.getSesionDao();
        controlDao = databaseHelper.getControlDao();
        controlDetalleDao = databaseHelper.getControlDetalleDao();
    }

    public void inicializaSesion() {
        sesionActual = new Sesion();
        sesionActual.setFechaControl(new Date());
        sesionActual.setResponsable("Christian Romero");
        sesionDao.create(sesionActual);
    }

    private void esconderBotonFinalzar() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.hide);
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

    private void mostrarBotonFinalzar() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.show);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bttnFinalizarControl.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        bttnFinalizarControl.startAnimation(a);
    }

    private boolean sePuedeCargarProductos() {
        if (vendedorSeleccionado != null && conductorSeleccionado != null && vehiculoSeleccionado != null) {
            bttnCargarProductos.requestFocus();
            return true;
        } else {
            return false;
        }
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
                getControlActual().setFechaControl(new Date());
                getControlActual().setSesion(sesionActual);
                getControlActual().setKm(0);//falta el input
                controlDao.createOrUpdate(getControlActual());
                intentMain = new Intent(this, ListaProductos.class);
                intentMain.putExtra("CONTROL", getControlActual());
                startActivityForResult(intentMain, CARGAR_PRODUCTOS);
                break;
            case R.id.bttnFinalizarControl:
                controlDao.update(getControlActual());
                enviarDatos();

                finish();
                break;
            default:
                break;
        }
    }

    private void enviarDatos() {
        if (estaConectado()) {
            try {

                cargaDetalles();
                insertaRequest();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void cargaDetalles(){
        try {
            getControlActual().getDetalles().clear();
            List<ControlDetalle> lista =controlDetalleDao.queryBuilder()
                    .where().eq(ControlDetalle.COL_CONTROL_NOMBRE,controlActual)
                    .query();
            if(lista != null) {
                getControlActual().getDetalles().addAll(lista);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private void insertaRequest() throws JSONException {

        final String body = new GsonBuilder().setPrettyPrinting().create().toJson(getControlActual());

        Request req = new JsonRequest<Control>(Request.Method.POST, UtilJson.PREF_URL + "/inserta", body,
                new Response.Listener<Control>() {
                    @Override
                    public void onResponse(Control response) {
                        Toast.makeText(getApplicationContext(), "Control" + response.getId(), Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Response<Control> parseNetworkResponse(NetworkResponse response) {
                String jsonString = null;
                Log.d("Respuesta1 ", "Respuesta1: " + jsonString);
                try {
                    jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    Log.d("Respuesta2 ","Respuesta2: " + jsonString);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d("Respuesta3 ","Respuesta3: " + jsonString);
                Control control = new GsonBuilder().create().fromJson(jsonString, Control.class);
                Response<Control> result = Response.success(control, HttpHeaderParser.parseCacheHeaders(response));
                return result;
            }
        };
        AppController.getInstance().addToRequestQueue(req);
    }


    //region Metodos para verificar conexion
    protected Boolean estaConectado() {
        if (conectadoWifi()) {
            return true;
        } else {

            return false;
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

    private void limpiarPantalla() {
        controlActual = null;
        vendedorSeleccionado = null;
        conductorSeleccionado = null;
        vehiculoSeleccionado = null;
        txtVendedorValue.setText("");
        recuadroVendedor.setBackgroundResource(R.drawable.recuadro);
        txtChoferValue.setText("");
        recuadroChofer.setBackgroundResource(R.drawable.recuadro);
        txtMovilValue.setText("");
        recuadroMovil.setBackgroundResource(R.drawable.recuadro);
        bttnCargarProductos.setEnabled(sePuedeCargarProductos());
        esconderBotonFinalzar();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Object obj = data.getSerializableExtra("RESULTADO");
            switch (requestCode) {
                case AGREGAR_VENDEDOR:
                    if (obj instanceof Vendedor) {
                        vendedorSeleccionado = (Vendedor) obj;
                        txtVendedorValue.setText(vendedorSeleccionado != null ? vendedorSeleccionado.getNombre() : "Seleccione un vendedor");
                        getControlActual().setVendedor(vendedorSeleccionado);
                        recuadroVendedor.setBackgroundResource(R.drawable.recuadro_seleccionado);
                        bttnCargarProductos.setEnabled(sePuedeCargarProductos());
                    }
                    break;
                case AGREGAR_CHOFER:
                    if (obj instanceof Conductor) {
                        conductorSeleccionado = (Conductor) obj;
                        txtChoferValue.setText(conductorSeleccionado != null ? conductorSeleccionado.getNombre() : "Seleccione un conductor");
                        getControlActual().setConductor(conductorSeleccionado);
                        recuadroChofer.setBackgroundResource(R.drawable.recuadro_seleccionado);
                        bttnCargarProductos.setEnabled(sePuedeCargarProductos());
                    }
                    break;
                case AGREGAR_MOVIL:
                    if (obj instanceof Vehiculo) {
                        vehiculoSeleccionado = (Vehiculo) obj;
                        txtMovilValue.setText(vehiculoSeleccionado != null ? vehiculoSeleccionado.getMarca() + ", Chapa: " + vehiculoSeleccionado.getChapa() : "Seleccione un Móvil");
                        getControlActual().setVehiculo(vehiculoSeleccionado);
                        getControlActual().setVehiculoChapa(vehiculoSeleccionado.getChapa());
                        recuadroMovil.setBackgroundResource(R.drawable.recuadro_seleccionado);
                        bttnCargarProductos.setEnabled(sePuedeCargarProductos());
                    }
                    break;
                case CARGAR_PRODUCTOS:
                    //Toast.makeText(this, "Resultado: " + dato, Toast.LENGTH_SHORT).show();
                    mostrarBotonFinalzar();
                    break;
            }
        } else {
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "No se seleccionó un vendedor", Toast.LENGTH_SHORT).show();
            }
        }

    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
               *//* databaseHelper.extraerBD(getPackageName());
                okImg.setImageResource(R.drawable.check);
                okImg.setVisibility(View.VISIBLE);
                okImg.startAnimation(fadeOut);*//*
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }*/


}
