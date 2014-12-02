package movil.palermo.com.py.controlstockregreso;

import android.app.Activity;
import android.app.ProgressDialog;
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

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.controlstockregreso.custom.ProductoListAdapter;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.Producto;


public class ListaProductos extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final int CONTAR_PRODUCTOS = 201;

    private Button bttnFinalizarConteo;
    private ListView lstVwProductos;
    Intent intentDetalleProducto;

    private List<Producto> productoList = new ArrayList<Producto>();
    private ProductoListAdapter adapter;
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Producto,Integer> productoDao;
    private ProgressDialog pDialog;


    private ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos);

        lstVwProductos = (ListView) findViewById(R.id.lstVwProductos);
        lstVwProductos.setOnItemClickListener(this);

        //Seteo el custom adapter
        adapter = new ProductoListAdapter(this, productoList);
        lstVwProductos.setAdapter(adapter);

        //instancio la BD y cargo mi lista

        databaseHelper = new DatabaseHelper(this.getApplicationContext());
        productoDao = databaseHelper.getProductoDao();
        recargaLista();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recargaLista();
    }

    private void recargaLista() {
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        productoList.clear();
        productoList.addAll(productoDao.queryForAll());
        adapter.notifyDataSetChanged();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
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
        /*if (view.getId()== R.id.bttnFinalizarConteo){
            intentDetalleProducto = new Intent();
            intentDetalleProducto.putExtra("RESULTADO","PRODUCTOS CARGADOS CORRECTAMENTE");
            setResult(RESULT_OK, intentDetalleProducto);
            finish();
        }*/

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this,AgregarCantidadActivity.class);
        intent.putExtra("PRODUCTO",(Producto)adapterView.getItemAtPosition(i));//le paso el id
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if (resultCode == Activity.RESULT_OK ){
            Bundle resultado = data.getExtras();
            String dato = resultado.getString("RESULTADO");
            Toast.makeText(this, "Conteo del producto finalizado: ", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Resultado: " + dato, Toast.LENGTH_SHORT).show();
        }
        else{
            if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "No se seleccionó un vendedor", Toast.LENGTH_SHORT).show();
            }
        }*/
    }
}
