package movil.palermo.com.py.controlstockregreso;

import android.support.v7.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.controlstockregreso.custom.ControlDetalleListAdapter;
import movil.palermo.com.py.controlstockregreso.custom.ResizeAnimation;
import movil.palermo.com.py.controlstockregreso.custom.SlidingUpPaneLayout;
import movil.palermo.com.py.controlstockregreso.custom.UnidadMedidaSpinnerAdapter;
import movil.palermo.com.py.controlstockregreso.modelo.Control;
import movil.palermo.com.py.controlstockregreso.modelo.ControlDetalle;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.Producto;
import movil.palermo.com.py.controlstockregreso.modelo.UnidadMedida;


public class AgregarCantidadActivity extends ActionBarActivity implements View.OnClickListener {

    ImageView btnMas;
    EditText cantidad;
    ListView listaDetalle;
    List<ControlDetalle> detalles = new ArrayList<ControlDetalle>();
    ControlDetalleListAdapter adapter;
    UnidadMedidaSpinnerAdapter adapterUnidadMedida;
    Producto productoSeleccionado;
    private RuntimeExceptionDao<UnidadMedida, Integer> unidadMedidaDao;
    private RuntimeExceptionDao<Control, Integer> controlDao;
    private RuntimeExceptionDao<ControlDetalle, Integer> controlDetalleDao;
    RelativeLayout bottom;
    private Spinner unidadMedida;
    private List<UnidadMedida> listaUnidadMedida = new ArrayList<UnidadMedida>();


    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_agregar_cantidad);
        btnMas = (ImageView) findViewById(R.id.imgPlus);
        cantidad = (EditText) findViewById(R.id.editTextCantidad);
        listaDetalle = (ListView) findViewById(R.id.detalle);

        Object obj = getIntent().getSerializableExtra("PRODUCTO");
        if (obj != null && obj instanceof Producto) {
            productoSeleccionado = (Producto) obj;
        }

        cantidad.setRawInputType(Configuration.KEYBOARD_12KEY);
        adapter = new ControlDetalleListAdapter(this, detalles);
        listaDetalle.setAdapter(adapter);

        btnMas.setOnClickListener(this);

        final Context context = this;
        btnMas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pulse));
                        break;
                }

                return false;
            }
        });


        if (productoSeleccionado != null) {
            configuraActionBar();
        }

        databaseHelper = new DatabaseHelper(this);
        unidadMedidaDao = databaseHelper.getUnidadMedidaDao();
        controlDao = databaseHelper.getControlDao();
        controlDetalleDao = databaseHelper.getControlDetalleDao();

        final float density = getResources().getDisplayMetrics().density;

        SlidingUpPaneLayout slidingUpPaneLayout = (SlidingUpPaneLayout) findViewById(R.id.sliding_up_layout);
        slidingUpPaneLayout.setParallaxDistance((int) (200 * density));
        slidingUpPaneLayout.setShadowResourceTop(R.drawable.shadow_top);

        bottom = (RelativeLayout) findViewById(R.id.bottom_view);
        slidingUpPaneLayout.openPane(bottom, 0);


        addItemsOnSpinner2();
    }


    public void addItemsOnSpinner2() {

        unidadMedida = (Spinner) findViewById(R.id.spinner);
        /*List<String> list = new ArrayList<String>();
        list.add("list 1");
        list.add("list 2");
        list.add("list 3");*/
        listaUnidadMedida.addAll(unidadMedidaDao.queryForAll());
        adapterUnidadMedida = new UnidadMedidaSpinnerAdapter(this, listaUnidadMedida);
        /*ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);*/
        //adapterUnidadMedida.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unidadMedida.setAdapter(adapterUnidadMedida);
    }

    private void configuraActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(productoSeleccionado.getNombre());
        if (Build.VERSION.SDK_INT >= 14) {
            int img = 0;
            if (productoSeleccionado.getNombre().toLowerCase().contains("blue")) {
                img = R.drawable.palermo_blue;
            } else if (productoSeleccionado.getNombre().toLowerCase().contains("green")) {
                img = R.drawable.palermo_green;
            } else if (productoSeleccionado.getNombre().toLowerCase().contains("red")) {
                img = R.drawable.palermo_red;
            } else if (productoSeleccionado.getNombre().toLowerCase().contains("plm 3")) {
                img = R.drawable.plm3;
            } else if (productoSeleccionado.getNombre().toLowerCase().contains("duo")) {
                img = R.drawable.palermo_duo;
            } else if (productoSeleccionado.getId() == 218) {
                img = R.drawable.kentucky_10;
            } else if (productoSeleccionado.getId() == 403) {
                img = R.drawable.sanmarino_20;
            } else if (productoSeleccionado.getId() == 404) {
                img = R.drawable.sanmarino_10;
            } else if (productoSeleccionado.getId() == 198) {
                img = R.drawable.kentucky_20;
            } else if (productoSeleccionado.getId() == 204) {
                img = R.drawable.kentucky_soft;
            } else {
                img = R.drawable.kit;
            }

            actionBar.setIcon(img);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_agregar_cantidad, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int cantActual = 0;
        String texto = cantidad.getText() == null ? "0" : cantidad.getText().toString();

        switch (view.getId()) {
            case R.id.imgPlus:


                if (cantidad.getText() == null || cantidad.getText().toString().compareToIgnoreCase("0") == 0 || cantidad.getText().toString().compareToIgnoreCase("") == 0) {
                    Toast.makeText(this, "No hay cantidad", Toast.LENGTH_LONG).show();
                    break;
                }

                if (productoSeleccionado != null) {
                    ControlDetalle d = new ControlDetalle();
                    UnidadMedida um = null;

                    if (unidadMedida.getSelectedItem() instanceof UnidadMedida) {
                        um = (UnidadMedida) unidadMedida.getSelectedItem();
                    }

                    d.setUnidadMedida(um);
                    d.setCantidad(Integer.valueOf(cantidad.getText().toString()));
                    d.setProducto(productoSeleccionado);
                    detalles.add(d);
                    adapter.notifyDataSetChanged();
                    controlDetalleDao.create(d);
                }
                break;

        }

    }
}
