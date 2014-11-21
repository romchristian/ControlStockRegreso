package movil.palermo.com.py.controlstockregreso;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class DetalleProducto extends ActionBarActivity implements View.OnClickListener{
    private TextView txtVwNombreProducto;
    private EditText editTxtCajasCantidad;
    private EditText editTxtGruesasCantidad;
    private EditText editTxtCajetillasCantidad;
    private Button bttnConfirmarDetalla;
    Intent resultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);
        txtVwNombreProducto = (TextView) findViewById(R.id.txtVwNombreProductoDetalle);
        editTxtCajasCantidad =(EditText) findViewById(R.id.editTxtCajasCantidad);
        editTxtGruesasCantidad = (EditText) findViewById(R.id.editTxtGruesasCantidad);
        editTxtCajetillasCantidad = (EditText) findViewById(R.id.editTxtCajetillasCantidad);
        bttnConfirmarDetalla = (Button) findViewById(R.id.bttnConfirmarDetalle);
        resultado = new Intent();
        Bundle datos = getIntent().getExtras();
        String producto = datos.getString("PRODUCTO");


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detalle_producto, menu);
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

    }
}
