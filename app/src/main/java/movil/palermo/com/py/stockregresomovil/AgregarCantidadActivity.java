package movil.palermo.com.py.stockregresomovil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.stockregresomovil.R;
import movil.palermo.com.py.stockregresomovil.custom.ControlDetalleListAdapter;
import movil.palermo.com.py.stockregresomovil.custom.SegmentedRadioGroup;
import movil.palermo.com.py.stockregresomovil.custom.SlidingUpPaneLayout;
import movil.palermo.com.py.stockregresomovil.custom.UnidadMedidaSpinnerAdapter;
import movil.palermo.com.py.stockregresomovil.modelo.Control;
import movil.palermo.com.py.stockregresomovil.modelo.ControlDetalle;
import movil.palermo.com.py.stockregresomovil.modelo.DatabaseHelper;
import movil.palermo.com.py.stockregresomovil.modelo.Producto;
import movil.palermo.com.py.stockregresomovil.modelo.UnidadMedida;


public class AgregarCantidadActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    //region Variables
    private ImageView btnMas;
    private EditText cantidad;
    private ListView listaDetalle;
    private List<ControlDetalle> detalles = new ArrayList<ControlDetalle>();
    private ControlDetalleListAdapter adapter;
    private UnidadMedidaSpinnerAdapter adapterUnidadMedida;
    private Producto productoSeleccionado;
    private RuntimeExceptionDao<UnidadMedida, Integer> unidadMedidaDao;
    private RuntimeExceptionDao<Control, Integer> controlDao;
    private RuntimeExceptionDao<ControlDetalle, Integer> controlDetalleDao;
    private RelativeLayout bottom;
    private Spinner unidadMedida;
    private List<UnidadMedida> listaUnidadMedida = new ArrayList<UnidadMedida>();
    private Animation fadeOut;
    private ImageView okImg;
    private Control controlActual;
    private DatabaseHelper databaseHelper;
    private Integer esDevolucion;
    SegmentedRadioGroup segmentUnidadMedida;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_cantidad);

        btnMas = (ImageView) findViewById(R.id.imgPlus);
        cantidad = (EditText) findViewById(R.id.editTextCantidad);
        listaDetalle = (ListView) findViewById(R.id.detalle);
        okImg = (ImageView) findViewById(R.id.ok_img);
        bottom = (RelativeLayout) findViewById(R.id.bottom_view);

        databaseHelper = new DatabaseHelper(this);
        unidadMedidaDao = databaseHelper.getUnidadMedidaDao();
        controlDao = databaseHelper.getControlDao();
        controlDetalleDao = databaseHelper.getControlDetalleDao();

        adapter = new ControlDetalleListAdapter(this, detalles);
        listaDetalle.setAdapter(adapter);
        listaDetalle.setOnItemLongClickListener(this);

        btnMas.setOnClickListener(this);


        cargaExtras();
        configuraPanelSlideUp();
        cargaSegmentUnidadMedida();
        cargaSpinnerUnidadMedida();
        configuraEfectos();
        configuraTecladoNumerico();

        //Object param2 = getIntent().getSerializableExtra("DEVOLUCION");

//        if (param2 != null && param2 instanceof Integer) {
       //     esDevolucion = (Integer) param2;
  //      }

    }

    //region Metodos privados
    private void configuraTecladoNumerico() {
        /*cantidad.setRawInputType(Configuration.KEYBOARD_12KEY);

        cantidad.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    agregaDetalle();
                    handled = true;
                }
                return handled;
            }
        });*/
    }

    private void configuraPanelSlideUp() {
        final float density = getResources().getDisplayMetrics().density;
        SlidingUpPaneLayout slidingUpPaneLayout = (SlidingUpPaneLayout) findViewById(R.id.sliding_up_layout);
        slidingUpPaneLayout.setParallaxDistance((int) (200 * density));
        slidingUpPaneLayout.setShadowResourceTop(R.drawable.shadow_top);
        slidingUpPaneLayout.openPane(bottom, 0);

    }

    private void configuraEfectos() {

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

        fadeOut = AnimationUtils.loadAnimation(context, R.anim.move_fade_out);
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
    }

    private void cargaExtras() {
        Object obj = getIntent().getSerializableExtra("PRODUCTO");
        if (obj != null && obj instanceof Producto) {
            productoSeleccionado = (Producto) obj;
        }

        if (productoSeleccionado != null) {
            configuraActionBar();
        }

        Object param2 = getIntent().getSerializableExtra("DEVOLUCION");

        if (param2 != null && param2 instanceof Integer) {
            esDevolucion = (Integer) param2;
        }

        Object obj2 = getIntent().getSerializableExtra("CONTROL");
        if (obj2 != null && obj2 instanceof Control) {
            controlActual = (Control) obj2;
            ActionBar ab = getSupportActionBar();
            ab.setSubtitle("Móvil Nro: " + controlActual.getVehiculo().getNumero().toString() + "        Stock Regreso");
            cargaDetalles();
        }


    }

    private void cargaDetalles() {
        try {
            detalles.clear();
            List<ControlDetalle> lista = controlDetalleDao.queryBuilder()
                    .where().eq(ControlDetalle.COL_CONTROL_NOMBRE, controlActual)
                    .and().eq(ControlDetalle.COL_PRODUCTO_NOMBRE, productoSeleccionado)
                    .and().eq(ControlDetalle.COL_CONTROL_DEVOLUCION, esDevolucion)
                    .query();
            if (lista != null) {
                detalles.addAll(lista);
                adapter.notifyDataSetChanged();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void cargaSegmentUnidadMedida() {
        segmentUnidadMedida = (SegmentedRadioGroup) findViewById(R.id.segment_unidamedida);
        RadioButton rd1 = (RadioButton) findViewById(R.id.radio_cajas);
        RadioButton rd2 = (RadioButton) findViewById(R.id.radio_gruesas);
        RadioButton rd3 = (RadioButton) findViewById(R.id.radio_cajetillas);
        RadioButton rd4 = (RadioButton) findViewById(R.id.radio_unidades);

        if (productoSeleccionado.getKit() > 0) {
            rd1.setVisibility(View.GONE);
            rd2.setVisibility(View.GONE);
            rd3.setVisibility(View.GONE);
            rd4.setVisibility(View.VISIBLE);
            segmentUnidadMedida.check(R.id.radio_unidades);
        } else {
            segmentUnidadMedida.check(R.id.radio_gruesas);
            rd1.setVisibility(View.VISIBLE);
            rd2.setVisibility(View.VISIBLE);
            rd3.setVisibility(View.VISIBLE);
            rd4.setVisibility(View.GONE);
        }

    }

    public void cargaSpinnerUnidadMedida() {
        unidadMedida = (Spinner) findViewById(R.id.spinner);
        listaUnidadMedida.addAll(unidadMedidaDao.queryForAll());
        if (productoSeleccionado.getKit() > 0) {
            listaUnidadMedida.remove(0);
            listaUnidadMedida.remove(0);
            listaUnidadMedida.remove(0);
        } else {
            listaUnidadMedida.remove(3);
        }
        adapterUnidadMedida = new UnidadMedidaSpinnerAdapter(this, listaUnidadMedida);
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
    //endregion

    //region Metodos sobreescritos
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_agregar_cantidad, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
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
        // int duration = Toast.LENGTH_SHORT;
        // Toast toast = Toast.makeText(this, "Devolucion " + esDevolucion, duration);
        // toast.show();
        switch (view.getId()) {
            case R.id.imgPlus:
                //agregaDetalle();
                int duration = Toast.LENGTH_SHORT;
                Log.d("DEV", "Devolucion " + esDevolucion);

                agregaDetalle2();
                break;
        }
    }

    private int agregaDetalle2() {


        int cantActual = 0;
        String texto = cantidad.getText() == null ? "0" : cantidad.getText().toString();

        if (cantidad.getText() == null || cantidad.getText().toString().compareToIgnoreCase("0") == 0 || cantidad.getText().toString().compareToIgnoreCase("") == 0) {
            Toast.makeText(this, "No hay cantidad", Toast.LENGTH_LONG).show();
            return 0;
        }

        if (productoSeleccionado != null) {
            ControlDetalle d = new ControlDetalle();
            UnidadMedida um = null;

            switch (segmentUnidadMedida.getCheckedRadioButtonId()) {
                case R.id.radio_cajas:
                    um = unidadMedidaDao.queryForId(16);
                    break;
                case R.id.radio_gruesas:
                    um = unidadMedidaDao.queryForId(15);
                    break;
                case R.id.radio_cajetillas:
                    um = unidadMedidaDao.queryForId(25);
                    break;
                case R.id.radio_unidades:
                    um = unidadMedidaDao.queryForId(29);
                    break;
            }

            if (unidadMedida != null) {

                d.setControl(controlActual);
                d.setUnidadMedida(um);
                d.setCantidad(Integer.valueOf(cantidad.getText().toString()));
                d.setProducto(productoSeleccionado);
                d.setEsDevolucion(esDevolucion);

                detalles.add(d);
                adapter.notifyDataSetChanged();
                controlDetalleDao.create(d);


                cantidad.setText("");

                okImg.setVisibility(View.VISIBLE);
                okImg.startAnimation(fadeOut);
            }
        }

        return 1;
    }

    private int agregaDetalle() {

        int cantActual = 0;
        String texto = cantidad.getText() == null ? "0" : cantidad.getText().toString();

        if (cantidad.getText() == null || cantidad.getText().toString().compareToIgnoreCase("0") == 0 || cantidad.getText().toString().compareToIgnoreCase("") == 0) {
            Toast.makeText(this, "No hay cantidad", Toast.LENGTH_LONG).show();
            return 0;
        }

        if (productoSeleccionado != null) {
            ControlDetalle d = new ControlDetalle();
            UnidadMedida um = null;

            if (unidadMedida.getSelectedItem() instanceof UnidadMedida) {
                um = (UnidadMedida) unidadMedida.getSelectedItem();
            }

            d.setControl(controlActual);
            d.setUnidadMedida(um);
            d.setCantidad(Integer.valueOf(cantidad.getText().toString()));
            d.setProducto(productoSeleccionado);
            detalles.add(d);
            adapter.notifyDataSetChanged();
            controlDetalleDao.create(d);


            cantidad.setText("");

            okImg.setVisibility(View.VISIBLE);
            okImg.startAnimation(fadeOut);

        }

        return 1;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        //controlDetalleDao.deleteById(Long.valueOf(id).intValue());
        final int pos = position;
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Advertencia!");
        dialog.setMessage("Desea eliminar este control?");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                controlDetalleDao.delete(detalles.get(pos));
                detalles.remove(pos);
                adapter.notifyDataSetChanged();
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
        return true;

    }
    //endregion
}
