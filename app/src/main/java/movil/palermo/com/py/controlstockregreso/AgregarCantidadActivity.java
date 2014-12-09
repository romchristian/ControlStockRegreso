package movil.palermo.com.py.controlstockregreso;

import android.support.v7.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.controlstockregreso.custom.ControlDetalleListAdapter;
import movil.palermo.com.py.controlstockregreso.custom.SlidingUpPaneLayout;
import movil.palermo.com.py.controlstockregreso.custom.UnidadMedidaSpinnerAdapter;
import movil.palermo.com.py.controlstockregreso.modelo.Control;
import movil.palermo.com.py.controlstockregreso.modelo.ControlDetalle;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.Producto;
import movil.palermo.com.py.controlstockregreso.modelo.UnidadMedida;


public class AgregarCantidadActivity extends ActionBarActivity implements View.OnClickListener {

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

        btnMas.setOnClickListener(this);


        cargaExtras();
        configuraPanelSlideUp();
        cargaSpinnerUnidadMedida();
        configuraEfectos();
        configuraTecladoNumerico();

    }

    //region Metodos privados
    private void configuraTecladoNumerico(){
        cantidad.setRawInputType(Configuration.KEYBOARD_12KEY);
    }
    private void configuraPanelSlideUp(){
        final float density = getResources().getDisplayMetrics().density;
        SlidingUpPaneLayout slidingUpPaneLayout = (SlidingUpPaneLayout) findViewById(R.id.sliding_up_layout);
        slidingUpPaneLayout.setParallaxDistance((int) (200 * density));
        slidingUpPaneLayout.setShadowResourceTop(R.drawable.shadow_top);
        slidingUpPaneLayout.openPane(bottom, 0);

    }
    private void configuraEfectos(){

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

        fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);

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
    private  void cargaExtras(){
        Object obj = getIntent().getSerializableExtra("PRODUCTO");
        if (obj != null && obj instanceof Producto) {
            productoSeleccionado = (Producto) obj;
        }

        if (productoSeleccionado != null) {
            configuraActionBar();
        }


        Object obj2 = getIntent().getSerializableExtra("CONTROL");
        if (obj2 != null && obj2 instanceof Control) {
            controlActual = (Control) obj2;
            cargaDetalles();
        }
    }
    private void cargaDetalles(){
        try {
            detalles.clear();
            List<ControlDetalle> lista =controlDetalleDao.queryBuilder()
                    .where().eq(ControlDetalle.COL_CONTROL_NOMBRE,controlActual)
                    .and().eq(ControlDetalle.COL_PRODUCTO_NOMBRE,productoSeleccionado)
                    .query();
            if(lista != null) {
                detalles.addAll(lista);
                adapter.notifyDataSetChanged();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void cargaSpinnerUnidadMedida() {
        unidadMedida = (Spinner) findViewById(R.id.spinner);
        listaUnidadMedida.addAll(unidadMedidaDao.queryForAll());
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

                    d.setControl(controlActual);
                    d.setUnidadMedida(um);
                    d.setCantidad(Integer.valueOf(cantidad.getText().toString()));
                    d.setProducto(productoSeleccionado);
                    detalles.add(d);
                    adapter.notifyDataSetChanged();
                    controlDetalleDao.create(d);


                    cantidad.setText("");
                    okImg.setImageResource(R.drawable.check);
                    okImg.setVisibility(View.VISIBLE);
                    okImg.startAnimation(fadeOut);

                }
                break;

        }

    }
    //endregion
}
