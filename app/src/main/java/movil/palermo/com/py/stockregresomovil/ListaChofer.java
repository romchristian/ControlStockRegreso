package movil.palermo.com.py.stockregresomovil;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.stockregresomovil.R;
import movil.palermo.com.py.stockregresomovil.custom.ConductorListAdapter;
import movil.palermo.com.py.stockregresomovil.modelo.Conductor;
import movil.palermo.com.py.stockregresomovil.modelo.DatabaseHelper;


public class ListaChofer extends ActionBarActivity implements AdapterView.OnItemClickListener {

    Intent datos;
    private ListView lstVwChofer;
    private List<Conductor> conductorList = new ArrayList<Conductor>();
    private ConductorListAdapter adapter;
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Conductor,Integer> conductorDao;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_lista_chofer);
        lstVwChofer = (ListView)findViewById(R.id.lstVwChofer);

        adapter = new ConductorListAdapter(this, conductorList);

        lstVwChofer.setAdapter(adapter);
        lstVwChofer.setOnItemClickListener(this);

        databaseHelper = new DatabaseHelper(this.getApplicationContext());
        conductorDao = databaseHelper.getConductorDao();
        recargaLista();
        configuraActionBar();
    }


    private void  configuraActionBar(){
        final ActionBar actionBar = getSupportActionBar();

                actionBar.setTitle("Seleccione un conductor");
                if(Build.VERSION.SDK_INT >=14) {
                    actionBar.setIcon(R.drawable.default_user_orange);
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
        conductorList.clear();
        try {
            List<Conductor> lista = conductorDao.queryBuilder()
                    .orderBy("nombre",true)
                    .where().eq("estado","N")
                    .query();
            conductorList.addAll(lista);
            adapter.notifyDataSetChanged();
            hidePDialog();
        } catch (SQLException e) {
            hidePDialog();
            e.printStackTrace();
        }
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
        getMenuInflater().inflate(R.menu.menu_lista_chofer, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Buscar");


        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                // this is your adapter that will be filtered
                adapter.getFilter().filter(newText);
                System.out.println("on text change text: "+newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
                adapter.getFilter().filter(query);
                System.out.println("on query submit: "+query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return super.onCreateOptionsMenu(menu);
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
        Conductor item = (Conductor) adapterView.getItemAtPosition(i);
        datos = new Intent();
        datos.putExtra("RESULTADO",item);
        setResult(RESULT_OK,datos);
        finish();
    }
}
