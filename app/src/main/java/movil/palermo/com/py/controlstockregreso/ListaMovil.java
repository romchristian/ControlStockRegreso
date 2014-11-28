package movil.palermo.com.py.controlstockregreso;

import android.app.ActionBar;
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

import movil.palermo.com.py.controlstockregreso.custom.ConductorListAdapter;
import movil.palermo.com.py.controlstockregreso.custom.VehiculoListAdapter;
import movil.palermo.com.py.controlstockregreso.modelo.Conductor;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.Vehiculo;


public class ListaMovil extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private ListView lstVwMovil;
    Intent datos;
    private List<Vehiculo> vehiculoList = new ArrayList<Vehiculo>();
    private VehiculoListAdapter adapter;
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Vehiculo,Integer> vehiculoDao;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_movil);

        lstVwMovil = (ListView)findViewById(R.id.lstVwMovil);


        adapter = new VehiculoListAdapter(this, vehiculoList);

        lstVwMovil.setAdapter(adapter);
        lstVwMovil.setOnItemClickListener(this);

        databaseHelper = new DatabaseHelper(this.getApplicationContext());
        vehiculoDao = databaseHelper.getVehiculoDao();

        recargaLista();
        configuraActionBar();
    }


    private void  configuraActionBar(){
        final ActionBar actionBar = getActionBar();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                actionBar.setTitle("Seleccione un móvil");
                if(Build.VERSION.SDK_INT >=14) {
                    actionBar.setIcon(R.drawable.camion);
                }
            }
        });

        t.start();
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
        vehiculoList.clear();
        vehiculoList.addAll(vehiculoDao.queryForAll());
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
        getMenuInflater().inflate(R.menu.menu_lista_movil, menu);
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
        Vehiculo item = vehiculoList.get(i);
        datos = new Intent();
        datos.putExtra("RESULTADO",item);
        setResult(RESULT_OK,datos);
        finish();
    }
}
