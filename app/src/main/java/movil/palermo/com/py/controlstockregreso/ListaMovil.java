package movil.palermo.com.py.controlstockregreso;

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


public class ListaMovil extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private String moviles[] = { "Volkswagen", "Tronco-movil", "Canyonero", "Planet Express",
            "Delivery bike", "AMC Pacer", "Batimovil"};
    private ListView lstVwMovil;
    private ArrayAdapter<String> adaptador;
    Intent datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_movil);
        lstVwMovil = (ListView)findViewById(R.id.lstVwMovil);
        adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,moviles);
        lstVwMovil.setAdapter(adaptador);
        lstVwMovil.setOnItemClickListener(this);
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
        String item = adaptador.getItem(i).toString();
        datos = new Intent();
        datos.putExtra("RESULTADO",item);
        setResult(RESULT_OK,datos);
        Toast.makeText(this, "Móvil seleccionado: " + item, Toast.LENGTH_SHORT).show();
        finish();
    }
}
