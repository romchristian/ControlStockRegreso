package movil.palermo.com.py.controlstockregreso;

import android.app.ActionBar;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.controlstockregreso.custom.ControlDetalleListAdapter;
import movil.palermo.com.py.controlstockregreso.modelo.Control;
import movil.palermo.com.py.controlstockregreso.modelo.ControlDetalle;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.Producto;
import movil.palermo.com.py.controlstockregreso.modelo.UnidadMedida;


public class AgregarCantidadActivity extends ActionBarActivity implements View.OnClickListener {

    ImageView btnMas, btnMinus;
    Button btnAgregar, btnConfirmar, btnCancelar;
    EditText cantidad;
    ListView listaDetalle;
    List<ControlDetalle> detalles = new ArrayList<ControlDetalle>();
    ControlDetalleListAdapter adapter;
    RadioGroup radioGroup;
    Producto productoSeleccionado;
    private RuntimeExceptionDao<UnidadMedida,Integer> unidadMedidaDao;
    private RuntimeExceptionDao<Control, Integer> controlDao;
    private RuntimeExceptionDao<ControlDetalle, Integer> controlDetalleDao;


    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_agregar_cantidad);
        btnMas = (ImageView) findViewById(R.id.imgPlus);
        btnMinus = (ImageView) findViewById(R.id.imgMinus);
        btnAgregar = (Button) findViewById(R.id.btnAgregar);
        btnConfirmar = (Button) findViewById(R.id.btnConfirmar);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        cantidad = (EditText) findViewById(R.id.editTextCantidad);
        listaDetalle = (ListView) findViewById(R.id.detalle);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        Object obj = getIntent().getSerializableExtra("PRODUCTO");
        if (obj != null && obj instanceof Producto) {
            productoSeleccionado = (Producto) obj;
        }

        cantidad.setRawInputType(Configuration.KEYBOARD_12KEY);
        adapter = new ControlDetalleListAdapter(this, detalles);
        listaDetalle.setAdapter(adapter);

        btnMas.setOnClickListener(this);
        btnMinus.setOnClickListener(this);
        btnAgregar.setOnClickListener(this);
        btnConfirmar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);

       final Context context = this;
        btnMas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale));
                return false;
            }
        });

        btnMinus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale));
                return false;
            }
        });


        if(productoSeleccionado!=null){
            configuraActionBar();
        }

        databaseHelper = new DatabaseHelper(this);
        unidadMedidaDao = databaseHelper.getUnidadMedidaDao();
        controlDao = databaseHelper.getControlDao();
        controlDetalleDao = databaseHelper.getControlDetalleDao();
    }


    private void  configuraActionBar(){
        final ActionBar actionBar = getActionBar();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                actionBar.setTitle(productoSeleccionado.getNombre());
                if(Build.VERSION.SDK_INT >=14) {
                    int img = 0;
                    if (productoSeleccionado.getNombre().toLowerCase().contains("blue")) {
                        img = R.drawable.palermo_blue;
                    } else if (productoSeleccionado.getNombre().toLowerCase().contains("green")) {
                        img = R.drawable.palermo_green;
                    }else if (productoSeleccionado.getNombre().toLowerCase().contains("red")) {
                        img = R.drawable.palermo_red;
                    }else if (productoSeleccionado.getNombre().toLowerCase().contains("plm 3")) {
                        img = R.drawable.plm3;
                    }else if (productoSeleccionado.getNombre().toLowerCase().contains("duo")) {
                        img = R.drawable.palermo_duo;
                    }else if (productoSeleccionado.getId() == 218) {
                        img = R.drawable.kentucky_10;
                    }else if (productoSeleccionado.getId() == 403) {
                        img = R.drawable.sanmarino_20;
                    }else if (productoSeleccionado.getId() == 404) {
                        img = R.drawable.sanmarino_10;
                    }else if (productoSeleccionado.getId() == 198) {
                        img = R.drawable.kentucky_20;
                    }else if (productoSeleccionado.getId() == 204) {
                        img = R.drawable.kentucky_soft;
                    }else{
                        img = R.drawable.kit;
                    }

                    actionBar.setIcon(img);
                }
            }
        });

        t.start();
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
        String texto = cantidad.getText()== null?"0":cantidad.getText().toString();

        switch (view.getId()) {
            case R.id.imgPlus:
                cantActual = Integer.valueOf(texto);
                cantidad.setText("" + (cantActual + 1));
                break;
            case R.id.imgMinus:
                cantActual = Integer.valueOf(texto);
                int cant = cantActual - 1;
                cantidad.setText("" + (cant < 0?0:cant));
                break;
            case R.id.btnAgregar:
                if(radioGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(this,"Seleccione una Unidad de Medida",Toast.LENGTH_LONG).show();
                    break;
                }

                if(cantidad.getText().toString().compareToIgnoreCase("0")==0){
                    Toast.makeText(this,"No hay cantidad",Toast.LENGTH_LONG).show();
                    break;
                }

                if (productoSeleccionado != null) {
                    ControlDetalle d = new ControlDetalle();
                    UnidadMedida um = null;

                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.radioCajas:
                            um = unidadMedidaDao.queryForId(1);
                            break;
                        case R.id.radioGruesas:
                            um = unidadMedidaDao.queryForId(2);
                            break;
                        case R.id.radioCajetillas:
                            um = unidadMedidaDao.queryForId(3);
                            break;
                    }

                    d.setUnidadMedida(um);
                    d.setCantidad(Integer.valueOf(cantidad.getText().toString()));
                    d.setProducto(productoSeleccionado);
                    detalles.add(d);
                    adapter.notifyDataSetChanged();
                    controlDetalleDao.create(d);
                }
                break;
            case R.id.btnConfirmar:
                break;
            case R.id.btnCancelar:
                finish();
                break;
        }

    }
}
