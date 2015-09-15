package movil.palermo.com.py.stockregresomovil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.stockregresomovil.R;
import movil.palermo.com.py.stockregresomovil.custom.ReposicionProductoListAdapter;
import movil.palermo.com.py.stockregresomovil.modelo.Control;
import movil.palermo.com.py.stockregresomovil.modelo.DatabaseHelper;
import movil.palermo.com.py.stockregresomovil.modelo.Producto;
import movil.palermo.com.py.stockregresomovil.modelo.ProductoReposicion;
import movil.palermo.com.py.stockregresomovil.modelo.ProductoUM;

public class ListaReposicionProducto extends ActionBarActivity implements  AdapterView.OnItemClickListener {
    public static final int CONTAR_PRODUCTOS = 201;


    private ListView lstVwProductos;
    private GenericRawResults<String[]> cajetillasXCajaRaw;
    private String[] cajetillasXCajaRow;
    private String productoID;
    private List<ProductoReposicion> productoList = new ArrayList<ProductoReposicion>();
    private ReposicionProductoListAdapter adapter;
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Producto, Integer> reposicionDao;
    private RuntimeExceptionDao<ProductoUM, Integer> productoUMDao;
    private ProgressDialog pDialog;
    private Intent datos;

    private Control controlActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_reposicion_producto);



        lstVwProductos = (ListView) findViewById(R.id.lstVwProductos);
        lstVwProductos.setOnItemClickListener(this);

        //Seteo el custom adapter
        adapter = new ReposicionProductoListAdapter(this, productoList);
        lstVwProductos.setAdapter(adapter);

        //instancio la BD y cargo mi lista

        databaseHelper = new DatabaseHelper(this.getApplicationContext());
        reposicionDao = databaseHelper.getProductoDao();
        productoUMDao = databaseHelper.getProductoUMDao();

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Seleccione el producto");

        Object obj = getIntent().getSerializableExtra("CONTROL");
        if(obj != null && obj instanceof Control){
            controlActual = (Control)obj;
            ab.setSubtitle("Móvil Nro: " + controlActual.getVehiculo().getNumero().toString() + "       Reposición");
        }
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
        Log.d("TAG", "Antes RECARGA LISTA " + controlActual);

        //Hago consulta nativa para sumar las cantidades
        int idControl = controlActual.getId();
        Log.d("TAG", "Dentro RECARGA LISTA " + idControl);

        /*String consulta = "select p.id, \n" +
                "       p.nombre, \n" +
                "       p.kit, \n" +
                "       resumen.cajas,\n" +
                "       resumen.gruesas,\n" +
                "       resumen.cajetillas,\n" +
                "       resumen.unidad,\n" +
                "       resumen.gruesasRepo,\n" +
                "       resumen.unidadRepo\n" +
                "from  producto p \n" +
                "left outer join \n" +
                "(select z.cid, z.pid,\n" +
                "sum(case when z.uid = 16  then z.cant else 0 end) as cajas, \n" +
                "sum(case when z.uid = 15  then z.cant else 0 end) as gruesas, \n" +
                "sum(case when z.uid = 25  then z.cant else 0 end) as cajetillas, \n" +
                "sum(case when z.uid = 29  then z.cant else 0 end) as unidad, \n" +
                "sum(case when z.uid = 15  then z.cantrepo else 0 end) as gruesasRepo, \n" +
                "sum(case when z.uid = 29  then z.cantrepo else 0 end) as unidadRepo\n" +
                "from \n" +
                "(select cd.cid as cid, cd.pid as pid, cd.uid as uid, sum(cd.cant) as cant, sum(rd.cant) as cantrepo from (select control_id as cid,  producto_id as pid,unidad_medida_id as uid, sum(cantidad) as cant  from  controldetalle\n" +
                "where control_id = " + idControl +"\n"+
                "group by  control_id, producto_id,unidad_medida_id) cd\n" +
                "left join \n" +
                "(select control_id as cid, producto_id as pid,unidad_medida_id as uid, 2 as tipo, sum(cantidad) as cant from reposiciondetalle\n" +
                "where control_id = " + idControl +"\n"+
                "group by  control_id, producto_id,unidad_medida_id) rd\n" +
                "on cd.cid = rd.cid and cd.pid = rd.pid and cd.uid = rd.uid\n" +
                "where cd.cid = " + idControl +"\n"+
                "group by cd.cid, cd.pid, cd.uid) z\n" +
                "group by z.cid, z.pid) resumen \n" +
                "on p.id = resumen.pid \n" +
                "order by p.orden, p.nombre";*/
        String consulta =    "select p.id, p.nombre, p.kit, " +
                "     sum(case when d.unidad_medida_id = 16 and d.control_id = " + idControl + " then d.cantidad else 0 end) as cajas, " +
                "     sum(case when d.unidad_medida_id = 15 and d.control_id = " + idControl + " then d.cantidad else 0 end) as gruesas, " +
                "     sum(case when d.unidad_medida_id = 25 and d.control_id = " + idControl + " then d.cantidad else 0 end) as cajetillas, " +
                "     sum(case when d.unidad_medida_id = 29 and d.control_id = " + idControl + " then d.cantidad else 0 end) as unidad " +
                " from producto p left join reposiciondetalle d   on d.producto_id = p.id group by p.id,p.nombre order by p.orden";


        GenericRawResults<String[]> rawResults =
                reposicionDao.queryRaw(consulta);

        productoList.clear();

        try {
            List<String[]> results = rawResults.getResults();
            for (String[] row : results) {
                productoList.add(new ProductoReposicion(row));
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
        getMenuInflater().inflate(R.menu.menu_lista_reposicion_producto, menu);
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
        Intent intent = new Intent(this, AgregarReposicionActivity.class);
        ProductoReposicion rep = (ProductoReposicion) adapterView.getItemAtPosition(i);
        reposicionDao.queryForId(rep.getId());
        intent.putExtra("PRODUCTO", reposicionDao.queryForId(rep.getId()));
        intent.putExtra("CONTROL", controlActual);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
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

    @Override
    public void onBackPressed() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Finalizar Reposicion?");
        dialog.setPositiveButton("SI",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                datos = new Intent();
                datos.putExtra("RESULTADO", controlActual);
                setResult(RESULT_OK,datos);
                finish();
            }
        });

        dialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog.show();

    }

}