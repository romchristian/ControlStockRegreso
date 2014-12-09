package movil.palermo.com.py.controlstockregreso;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import java.util.Date;

import movil.palermo.com.py.controlstockregreso.modelo.Conductor;
import movil.palermo.com.py.controlstockregreso.modelo.Control;
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
public class MainCrearControlFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static String TAG = MainCrearControlFragment.class.getSimpleName();

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

    View rootView;

    public static MainCrearControlFragment newInstance(int sectionNumber) {
        MainCrearControlFragment fragment = new MainCrearControlFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MainCrearControlFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_main_crear_control, container, false);

        //configurarActionBar();
        inicializarViews();
        asignarListeners();
        inicializarDaos();
        inicializaSesion();
        esconderBotonFinalzar();
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }


    private void inicializarViews() {
        recuadroVendedor = (FrameLayout) rootView.findViewById(R.id.recuadroVendedor);
        recuadroChofer = (FrameLayout) rootView.findViewById(R.id.recuadroChofer);
        recuadroMovil = (FrameLayout) rootView.findViewById(R.id.recuadroMovil);
        txtVendedorValue = (TextView) rootView.findViewById(R.id.txtVwVendedorValue);
        txtChoferValue = (TextView) rootView.findViewById(R.id.txtVwChoferValue);
        txtMovilValue = (TextView) rootView.findViewById(R.id.txtVwMovilValue);


        searchVendedor = (ImageView) rootView.findViewById(R.id.search_vendedor);
        searchChofer = (ImageView) rootView.findViewById(R.id.search_chofer);
        searchMovil = (ImageView) rootView.findViewById(R.id.search_movil);
        bttnCargarProductos = (Button) rootView.findViewById(R.id.bttnCargarProductos);
        bttnFinalizarControl = (Button) rootView.findViewById(R.id.bttnFinalizarControl);
        okImg = (ImageView) rootView.findViewById(R.id.ok_img);

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

        pDialog = new ProgressDialog(rootView.getContext());
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
        databaseHelper = new DatabaseHelper(rootView.getContext());
        productoDao = databaseHelper.getProductoDao();
        conductorDao = databaseHelper.getConductorDao();
        vendedorDao = databaseHelper.getVendedorDao();
        vehiculoDao = databaseHelper.getVehiculoDao();
        sesionDao = databaseHelper.getSesionDao();
        controlDao = databaseHelper.getControlDao();
        unidadMedidaDao = databaseHelper.getUnidadMedidaDao();
    }

    public void inicializaSesion() {
        sesionActual = new Sesion();
        sesionActual.setFechaControl(new Date());
        sesionActual.setResponsable("Christian Romero");
        sesionDao.create(sesionActual);
    }

    private void esconderBotonFinalzar() {
        Animation a = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.shake);
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
                intentMain = new Intent(rootView.getContext(), ListaVendedor.class);
                startActivityForResult(intentMain, AGREGAR_VENDEDOR);
                break;
            case R.id.search_chofer:
                intentMain = new Intent(rootView.getContext(), ListaChofer.class);
                startActivityForResult(intentMain, AGREGAR_CHOFER);
                break;
            case R.id.search_movil:
                intentMain = new Intent(rootView.getContext(), ListaMovil.class);
                startActivityForResult(intentMain, AGREGAR_MOVIL);
                break;
            case R.id.bttnCargarProductos:
                controlActual = new Control();
                controlActual.setConductor(conductorSeleccionado);
                controlActual.setFechaControl(new Date());
                controlActual.setSesion(sesionActual);
                controlActual.setVehiculo(vehiculoSeleccionado);
                controlActual.setVehiculoChapa(vehiculoSeleccionado != null ? vehiculoSeleccionado.getChapa() : "no asignado");
                controlActual.setVendedor(vendedorSeleccionado);
                controlActual.setKm(0);//falta el input

                controlDao.create(controlActual);
                intentMain = new Intent(rootView.getContext(), ListaProductos.class);
                intentMain.putExtra("CONTROL", controlActual);
                startActivityForResult(intentMain, CARGAR_PRODUCTOS);
                break;
            default:
                break;
        }
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
                        recuadroVendedor.setBackgroundResource(R.drawable.recuadro_seleccionado);
                        bttnCargarProductos.setEnabled(sePuedeCargarProductos());
                    }
                    break;
                case AGREGAR_CHOFER:
                    if (obj instanceof Conductor) {
                        conductorSeleccionado = (Conductor) obj;
                        txtChoferValue.setText(conductorSeleccionado != null ? conductorSeleccionado.getNombre() : "Seleccione un conductor");
                        recuadroChofer.setBackgroundResource(R.drawable.recuadro_seleccionado);
                        bttnCargarProductos.setEnabled(sePuedeCargarProductos());
                    }
                    break;
                case AGREGAR_MOVIL:
                    if (obj instanceof Vehiculo) {
                        vehiculoSeleccionado = (Vehiculo) obj;
                        txtMovilValue.setText(vehiculoSeleccionado != null ? vehiculoSeleccionado.getMarca() + ", Chapa: " + vehiculoSeleccionado.getChapa() : "Seleccione un Móvil");
                        recuadroMovil.setBackgroundResource(R.drawable.recuadro_seleccionado);
                        bttnCargarProductos.setEnabled(sePuedeCargarProductos());
                    }
                    break;
                case CARGAR_PRODUCTOS:
                    //Toast.makeText(this, "Resultado: " + dato, Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(rootView.getContext(), "No se seleccionó un vendedor", Toast.LENGTH_SHORT).show();
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
