package movil.palermo.com.py.stockregresomovil;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONException;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import movil.palermo.com.py.stockregresomovil.R;
import movil.palermo.com.py.stockregresomovil.custom.GsonRequest;
import movil.palermo.com.py.stockregresomovil.modelo.Conductor;
import movil.palermo.com.py.stockregresomovil.modelo.Control;
import movil.palermo.com.py.stockregresomovil.modelo.ControlDetalle;
import movil.palermo.com.py.stockregresomovil.modelo.ControlSimple;
import movil.palermo.com.py.stockregresomovil.modelo.DatabaseHelper;
import movil.palermo.com.py.stockregresomovil.modelo.EstadoControl;
import movil.palermo.com.py.stockregresomovil.modelo.Producto;
import movil.palermo.com.py.stockregresomovil.modelo.ReposicionDetalle;
import movil.palermo.com.py.stockregresomovil.modelo.ResponseControl;
import movil.palermo.com.py.stockregresomovil.modelo.Sesion;
import movil.palermo.com.py.stockregresomovil.modelo.SesionSimple;
import movil.palermo.com.py.stockregresomovil.modelo.UnidadMedida;
import movil.palermo.com.py.stockregresomovil.modelo.Vehiculo;
import movil.palermo.com.py.stockregresomovil.modelo.Vendedor;
import movil.palermo.com.py.stockregresomovil.util.UtilJson;

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
    private TextView txtPrefTelefonoId;
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
    private RuntimeExceptionDao<ReposicionDetalle, Integer> reposcionDetalleDao;


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

    public String telefonoId;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //telefonoId =  Settings.Secure.getString(getApplicationContext().getContentResolver(),
                //Settings.Secure.ANDROID_ID);

        //txtPrefTelefonoId =(TextView) findViewById(R.id.prefTelefonoId);
        //txtPrefTelefonoId.setText(telefonoId);

        SharedPreferences sp = getSharedPreferences(LoginActivity.PREFERENCIAS, MODE_PRIVATE);
        telefonoId= sp.getString("TELEFONOID","");
        Log.d("TELEFONOID", this.telefonoId);

        configuraActionBar();
        setContentView(R.layout.activity_main_crear_control);
        inicializarViews();
        asignarListeners();
        inicializarDaos();
        inicializaSesion();
        configuraEfectos();
        bttnCargarProductos.setEnabled(false);
        bttnFinalizarControl.setEnabled(false);

        Object obj = getIntent().getSerializableExtra("CONTROL");
        if (obj != null && obj instanceof Control) {
            controlActual = (Control) obj;
            vendedorSeleccionado = controlActual.getVendedor();
            conductorSeleccionado = controlActual.getConductor();
            //conductorSeleccionado = vendedorSeleccionado.getConductor();
            vehiculoSeleccionado = controlActual.getVehiculo();
            txtVendedorValue.setText(vendedorSeleccionado.getNombre());
            recuadroVendedor.setBackgroundResource(R.drawable.recuadro_seleccionado);
            txtChoferValue.setText(conductorSeleccionado.getNombre());
            recuadroChofer.setBackgroundResource(R.drawable.recuadro_seleccionado);
            txtMovilValue.setText(vehiculoSeleccionado.getMarca() + " Chapa: " + vehiculoSeleccionado.getChapa());
            recuadroMovil.setBackgroundResource(R.drawable.recuadro_seleccionado);
            bttnCargarProductos.setEnabled(sePuedeCargarProductos());
            bttnFinalizarControl.setEnabled(sePuedeFinalizarControl(controlActual));
        } else {
            bttnFinalizarControl.setEnabled(false);
        }

    }

    private boolean sePuedeFinalizarControl(Control c) {
        try {
            List<ControlDetalle> lista = controlDetalleDao.queryBuilder()
                    .where().eq(ControlDetalle.COL_CONTROL_NOMBRE, c)
                    .query();
            return lista != null && !(lista.isEmpty());

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void configuraEfectos() {

        final Context context = this;
        View.OnTouchListener listenerMovil = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.pulse2);
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                intentMain = new Intent(context, ListaMovil.class);
                                startActivityForResult(intentMain, AGREGAR_MOVIL);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        view.startAnimation(anim);
                        break;
                }

                return false;
            }
        };

        View.OnTouchListener listenerChofer = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.pulse2);
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                intentMain = new Intent(context, ListaChofer.class);
                                startActivityForResult(intentMain, AGREGAR_CHOFER);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        view.startAnimation(anim);
                        break;
                }

                return false;
            }
        };


        View.OnTouchListener listenerVendedor = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.pulse2);
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                intentMain = new Intent(context, ListaVendedor.class);
                                startActivityForResult(intentMain, AGREGAR_VENDEDOR);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        view.startAnimation(anim);
                        break;
                }

                return false;
            }
        };

        recuadroMovil.setOnTouchListener(listenerMovil);
        recuadroChofer.setOnTouchListener(listenerChofer);
        recuadroVendedor.setOnTouchListener(listenerVendedor);
    }


    private void configuraActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Nueva Recepción");
        SharedPreferences sp = getSharedPreferences(LoginActivity.PREFERENCIAS, MODE_PRIVATE);
        actionBar.setSubtitle(sp.getString("NOMBRE", ""));
        //actionBar.hide();
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
        recuadroMovil.setOnClickListener(this);
        recuadroVendedor.setOnClickListener(this);
        recuadroChofer.setOnClickListener(this);
        bttnCargarProductos.setOnClickListener(this);
        bttnFinalizarControl.setOnClickListener(this);
    }

    private void inicializarDaos() {
        databaseHelper = new DatabaseHelper(this);
        sesionDao = databaseHelper.getSesionDao();
        controlDao = databaseHelper.getControlDao();
        controlDetalleDao = databaseHelper.getControlDetalleDao();
        vehiculoDao = databaseHelper.getVehiculoDao();
        vendedorDao = databaseHelper.getVendedorDao();
        conductorDao = databaseHelper.getConductorDao();
        reposcionDetalleDao = databaseHelper.getReposicionDetalleDao();
    }

    public void inicializaSesion() {
        SharedPreferences sp = getSharedPreferences(LoginActivity.PREFERENCIAS, MODE_PRIVATE);
        String responsable = sp.getString("USER", "");
        Sesion actual;
        try {
            actual = sesionDao.queryBuilder().where().eq(Sesion.COL_FECHA, MainActivity.removeTime(new Date())).and().eq(Sesion.COL_RESPONSABLE, responsable).queryForFirst();
        } catch (SQLException e) {
            actual = null;
            e.printStackTrace();
        }
        if (actual == null) {
            sesionActual = new Sesion();
            sesionActual.setFechaControl(MainActivity.removeTime(new Date()));
            sesionActual.setResponsable(responsable);
            sesionDao.create(sesionActual);
        } else {
            sesionActual = actual;
        }
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
    protected void onRestart() {
        super.onRestart();
        inicializarDaos();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.bttnCargarProductos:
                getControlActual().setFechaControl(MainActivity.removeTime(new Date()));
                getControlActual().setSesion(sesionActual);
                getControlActual().setKm(0);//falta el input
                controlDao.createOrUpdate(getControlActual());
                intentMain = new Intent(this, ListaProductos.class);
                intentMain.putExtra("CONTROL", getControlActual());
                startActivityForResult(intentMain, CARGAR_PRODUCTOS);
                break;
            case R.id.bttnFinalizarControl:

                Control c = getControlActual();

                if (c.getEstado() != null && c.getEstado().equalsIgnoreCase(EstadoControl.NUEVO.toString())) {
                    c.setEstado(EstadoControl.CONFIRMADO.toString());
                    Toast.makeText(this, "Estado del control:" + c.getEstado(), Toast.LENGTH_SHORT).show();
                } else if (c.getEstado() != null && c.getEstado().equalsIgnoreCase(EstadoControl.AUTORIZADO.toString())) {
                    c.setEstado(EstadoControl.MODIFICADO.toString());
                    Toast.makeText(this, "Estado del control:" + c.getEstado(), Toast.LENGTH_SHORT).show();
                }

                //Actualizo los estado a usados
                c.getVehiculo().setEstado("U");
                c.getVendedor().setEstado("U");
                c.getConductor().setEstado("U");

                controlDao.update(c);
                vehiculoDao.update(c.getVehiculo());
                vendedorDao.update(c.getVendedor());
                conductorDao.update(c.getConductor());
                enviarDatos();

                finish();
                break;
            default:
                break;
        }
    }

    private void enviarDatos() {
        if (estaConectado()) {
            probarServicio();
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
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void probarServicio() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, (new UtilJson(this).prefUrl) + "/test",
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

        Log.d("INSERTARID", this.telefonoId);
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
                    Toast.makeText(getApplicationContext(), "Se envió con exito", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GsonRequest<ResponseControl> req = new GsonRequest<ResponseControl>(Request.Method.POST, (new UtilJson(this).prefUrl) + "/inserta", ResponseControl.class, body, listenerExito, this);

        int socketTimeout = 50000;//50 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);

        AppController.getInstance().addToRequestQueue(req);

    }


    //region Metodos para verificar conexion

    protected Boolean estaConectado() {
        return conectadoWifi();
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
        bttnFinalizarControl.setEnabled(sePuedeFinalizarControl(controlActual));
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
                        txtChoferValue.setText(vendedorSeleccionado.getConductor() != null ? vendedorSeleccionado.getConductor().getNombre() : "Seleccione un conductor");
                        txtMovilValue.setText(vendedorSeleccionado.getConductor().getVehiculo() != null ? vendedorSeleccionado.getConductor().getVehiculo().getMarca() + ", Chapa: " + vendedorSeleccionado.getConductor().getVehiculo().getChapa() : "Seleccione un Móvil");
                        //Toast.makeText(this, "Conductor " + vendedorSeleccionado.getConductor(), Toast.LENGTH_LONG).show();
                        conductorSeleccionado = vendedorSeleccionado.getConductor();
                        vehiculoSeleccionado = vendedorSeleccionado.getConductor().getVehiculo();
                        getControlActual().setVendedor(vendedorSeleccionado);
                        getControlActual().setConductor(conductorSeleccionado);
                        getControlActual().setVehiculo(vehiculoSeleccionado);
                        recuadroVendedor.setBackgroundResource(R.drawable.recuadro_seleccionado);
                        recuadroChofer.setBackgroundResource(R.drawable.recuadro_seleccionado);
                        recuadroMovil.setBackgroundResource(R.drawable.recuadro_seleccionado);
                        bttnCargarProductos.setEnabled(sePuedeCargarProductos());
                    }
                    break;
                case AGREGAR_CHOFER:
                    if (obj instanceof Conductor) {
                        conductorSeleccionado = (Conductor) obj;
                        txtChoferValue.setText(conductorSeleccionado != null ? conductorSeleccionado.getNombre() : "Seleccione un conductor");
                        txtMovilValue.setText(conductorSeleccionado.getVehiculo() != null ? conductorSeleccionado.getVehiculo().getMarca() + ", Chapa: " + conductorSeleccionado.getVehiculo().getChapa() : "Seleccione un Móvil");
                        vehiculoSeleccionado = conductorSeleccionado.getVehiculo();
                        getControlActual().setConductor(conductorSeleccionado);
                        getControlActual().setVehiculo(vehiculoSeleccionado);
                        recuadroChofer.setBackgroundResource(R.drawable.recuadro_seleccionado);
                        recuadroMovil.setBackgroundResource(R.drawable.recuadro_seleccionado);
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
                    bttnFinalizarControl.setEnabled(sePuedeFinalizarControl(controlActual));
                    break;
            }
        } else {
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "No se seleccionó un vendedor", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (sePuedeFinalizarControl(controlActual)) {
            Toast.makeText(this, "Debe de terminar de cargar el control", Toast.LENGTH_SHORT).show();
        } else {
            cancelar();

        }
    }

    public void cancelar() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Desea cancelar la recepción?");
        dialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                controlDao.delete(controlActual);
                finish();
            }
        });

        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog.show();
    }

}
