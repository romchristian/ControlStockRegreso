package movil.palermo.com.py.controlstockregreso;

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

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.controlstockregreso.custom.ProductoListAdapter;
import movil.palermo.com.py.controlstockregreso.modelo.Control;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.Producto;
import movil.palermo.com.py.controlstockregreso.modelo.ProductoResumen;


public class ListaProductos extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final int CONTAR_PRODUCTOS = 201;

    private Button bttnFinalizarConteo;
    private ListView lstVwProductos;
    Intent intentDetalleProducto;

    private List<ProductoResumen> productoList = new ArrayList<ProductoResumen>();
    private ProductoListAdapter adapter;
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Producto, Integer> productoDao;
    private ProgressDialog pDialog;


    private ArrayAdapter<String> adaptador;

    private Control controlActual;

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

        Object obj = getIntent().getSerializableExtra("CONTROL");
        if(obj != null && obj instanceof Control){
            controlActual = (Control)obj;
        }

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

        //Hago consulta nativa para sumar las cantidades


        GenericRawResults<String[]> rawResults =
                productoDao.queryRaw(
                        "select p.id, p.nombre , " +
                                "     sum(case when d.unidad_medida_id = 1 then d.cantidad else 0 end) as cajas, " +
                                "     sum(case when d.unidad_medida_id = 2 then d.cantidad else 0 end) as gruesas, " +
                                "     sum(case when d.unidad_medida_id = 3 then d.cantidad else 0 end) as cajetillas " +
                                " from producto p left join controldetalle d   on d.producto_id = p.id group by p.id,p.nombre order by p.nombre ");

        productoList.clear();

        try {
            List<String[]> results = rawResults.getResults();
            for (String[] row : results) {
                productoList.add(new ProductoResumen(row));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //productoList.addAll(productoDao.queryForAll());
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
        Intent intent = new Intent(this, AgregarCantidadActivity.class);
        ProductoResumen pr = (ProductoResumen) adapterView.getItemAtPosition(i);
        productoDao.queryForId(pr.getId());
        intent.putExtra("PRODUCTO", productoDao.queryForId(pr.getId()));
        intent.putExtra("CONTROL", controlActual);
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
