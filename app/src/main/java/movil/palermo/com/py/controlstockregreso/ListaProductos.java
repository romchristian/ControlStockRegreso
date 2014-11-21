package movil.palermo.com.py.controlstockregreso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


public class ListaProductos extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final int CONTAR_PRODUCTOS = 201;
    private String productos[] = { "PLM3", "Palermo Premium Red", "Palermo Premium Blue", "Palermo Premium Green",
            "Kentucky", "San Marino", "Ei8ht"};
    private Button bttnFinalizarConteo;
    private ListView lstVwProductos;
    Intent intentDetalleProducto;
    private ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos);
        bttnFinalizarConteo = (Button) findViewById(R.id.bttnFinalizarConteo);
        lstVwProductos = (ListView) findViewById(R.id.lstVwProductos);
        lstVwProductos.setOnItemClickListener(this);
        adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,productos);
        lstVwProductos.setAdapter(adaptador);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_productos, menu);
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
        if (view.getId()== R.id.bttnFinalizarConteo){
            intentDetalleProducto = new Intent();
            intentDetalleProducto.putExtra("RESULTADO","PRODUCTOS CARGADOS CORRECTAMENTE");
            setResult(RESULT_OK, intentDetalleProducto);
            finish();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        intentDetalleProducto = new Intent(this,DetalleProducto.class);
        String producto;
        producto = adapterView.getSelectedItem().toString();
        intentDetalleProducto.putExtra("PRODUCTO",producto);
        startActivityForResult(intentDetalleProducto,CONTAR_PRODUCTOS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK ){
            Bundle resultado = data.getExtras();
            String dato = resultado.getString("RESULTADO");
            Toast.makeText(this, "Conteo del producto finalizado: ", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Resultado: " + dato, Toast.LENGTH_SHORT).show();
        }
        else{
            if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "No se seleccionó un vendedor", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
