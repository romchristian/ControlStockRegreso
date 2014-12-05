package movil.palermo.com.py.controlstockregreso;

import android.support.v7.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
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
        setContentView(R.layout.activity_lista_vendedor);
        lstVwVendedor =(ListView) findViewById(R.id.lstVwVendedor);
        adapter = new VendedorListAdapter(this, vendedorList);
        lstVwVendedor.setAdapter(adapter);
        lstVwVendedor.setOnItemClickListener(this);
        databaseHelper = new DatabaseHelper(this.getApplicationContext());
        vendedorDao = databaseHelper.getVendedorDao();
        recargaLista();
        configuraActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void  configuraActionBar(){
        final ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Seleccione un vendedor");
        if(Build.VERSION.SDK_INT >=14) {
            actionBar.setIcon(R.drawable.default_user);
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        recargaLista();
    }

    private void recargaLista() {

        vendedorList.clear();
        vendedorList.addAll(vendedorDao.queryForAll());
        adapter.notifyDataSetChanged();

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
        Vendedor item = vendedorList.get(i);
        datos = new Intent();
        datos.putExtra("RESULTADO",item);
        setResult(RESULT_OK,datos);
        finish();
    }
}
