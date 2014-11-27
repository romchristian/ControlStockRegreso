package movil.palermo.com.py.controlstockregreso;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.controlstockregreso.custom.ProductoListAdapter;
import movil.palermo.com.py.controlstockregreso.custom.VendedorListAdapter;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.Producto;
import movil.palermo.com.py.controlstockregreso.modelo.Vendedor;


public class ListaVendedor extends ActionBarActivity implements AdapterView.OnItemClickListener {

    Intent datos;
    private ListView lstVwVendedor;

    private List<Vendedor> vendedorList = new ArrayList<Vendedor>();
    private VendedorListAdapter adapter;
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Vendedor,Integer> vendedorDao;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Seleccione un vendedor");

        setContentView(R.layout.activity_lista_vendedor);

        lstVwVendedor =(ListView) findViewById(R.id.lstVwVendedor);

        adapter = new VendedorListAdapter(this, vendedorList);

        lstVwVendedor.setAdapter(adapter);
        lstVwVendedor.setOnItemClickListener(this);
        databaseHelper = new DatabaseHelper(this.getApplicationContext());
        vendedorDao = databaseHelper.getVendedorDao();
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
        vendedorList.clear();
        vendedorList.addAll(vendedorDao.queryForAll());
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
        getMenuInflater().inflate(R.menu.menu_lista_vendedor, menu);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //String item = adaptador.getItem(i).toString();
        Vendedor item = vendedorList.get(i);
        datos = new Intent();
        datos.putExtra("RESULTADO",item);
        setResult(RESULT_OK,datos);
        Toast.makeText(this, "Vendedor seleccionado: " + item, Toast.LENGTH_SHORT).show();
        finish();
    }
}
