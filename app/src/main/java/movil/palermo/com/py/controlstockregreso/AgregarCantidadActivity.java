package movil.palermo.com.py.controlstockregreso;

import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.controlstockregreso.custom.ControlDetalleListAdapter;
import movil.palermo.com.py.controlstockregreso.modelo.ControlDetalle;
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
//        Object obj = getIntent().getSerializableExtra("PRODUCTO");
//        if (obj != null && obj instanceof Producto) {
//            productoSeleccionado = (Producto) obj;
//        }

        cantidad.setRawInputType(Configuration.KEYBOARD_12KEY);
        adapter = new ControlDetalleListAdapter(this, detalles);
        listaDetalle.setAdapter(adapter);

        btnMas.setOnClickListener(this);
        btnMinus.setOnClickListener(this);
        btnAgregar.setOnClickListener(this);
        btnConfirmar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);

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
        switch (view.getId()) {
            case R.id.imgPlus:
                cantActual = Integer.valueOf(cantidad.getText().toString());
                cantidad.setText("" + (cantActual + 1));
                break;
            case R.id.imgMinus:
                cantActual = Integer.valueOf(cantidad.getText().toString());
                cantidad.setText("" + (cantActual - 1));
                break;
            case R.id.btnAgregar:

                if(radioGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(this,"Seleccione una Unidad de Medida",Toast.LENGTH_LONG).show();
                    break;
                }

                if (productoSeleccionado != null) {
                    ControlDetalle d = new ControlDetalle();
                    UnidadMedida um = new UnidadMedida();
                    String unidadMedidad = "";

                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.radioCajas:
                            unidadMedidad = "Cajas";
                            break;
                        case R.id.radioGruesas:
                            unidadMedidad = "Gruesas";
                            break;
                        case R.id.radioCajetillas:
                            unidadMedidad = "Cajetillas";
                            break;
                    }

                    um.setNombre(unidadMedidad);
                    d.setUnidadMedida(um);
                    d.setCantidad(Integer.valueOf(cantidad.getText().toString()));
                    d.setProducto(productoSeleccionado);
                    detalles.add(d);
                    adapter.notifyDataSetChanged();

                }
                break;
            case R.id.btnConfirmar:
                break;
            case R.id.btnCancelar:
                break;
        }

    }
}
