package movil.palermo.com.py.controlstockregreso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//import py.com.palermo.movil.movilcontrolstockretorno.RequestCodes;

public class MainCrearControl extends ActionBarActivity implements View.OnClickListener {
    public static final int AGREGAR_VENDEDOR = 101;
    public static final int AGREGAR_CHOFER = 102;
    public static final int AGREGAR_MOVIL = 103;
    public static final int CARGAR_PRODUCTOS = 104;

    private Button bttnAgregarVendedor, bttnAgregarChofer, bttnAgregarMovil, bttnCargarProductos, bttnFinalizarControl;
    private TextView txtVwVendedor, txtVwChofer, txtVwMovil;
    Intent intentMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_crear_control);
        bttnAgregarVendedor = (Button) findViewById(R.id.bttnAgregarVendedor);
        bttnAgregarChofer = (Button) findViewById(R.id.bttnAgregarChofer);
        bttnAgregarMovil = (Button)findViewById(R.id.bttnAgregarMovil);
        bttnCargarProductos = (Button) findViewById(R.id.bttnCargarProductos);
        bttnFinalizarControl = (Button) findViewById(R.id.bttnFinalizarControl);
        txtVwVendedor = (TextView) findViewById(R.id.txtVwVendedor);
        txtVwChofer = (TextView) findViewById(R.id.txtVwChofer);
        txtVwMovil = (TextView) findViewById(R.id.txtVwMovil);
        bttnAgregarVendedor.setOnClickListener(this);
        bttnAgregarChofer.setOnClickListener(this);
        bttnAgregarMovil.setOnClickListener(this);
        bttnCargarProductos.setOnClickListener(this);
        bttnFinalizarControl.setOnClickListener(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_crear_control, menu);
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
        switch (view.getId()){
            case R.id.bttnAgregarVendedor:
                intentMain = new Intent(this,ListaVendedor.class);
                startActivityForResult(intentMain,AGREGAR_VENDEDOR);
                break;
            case R.id.bttnAgregarChofer:
                intentMain = new Intent(this,ListaChofer.class);
                startActivityForResult(intentMain,AGREGAR_CHOFER);
                break;
            case R.id.bttnAgregarMovil:
                intentMain = new Intent(this,ListaMovil.class);
                startActivityForResult(intentMain,AGREGAR_MOVIL);
                break;
            case R.id.bttnCargarProductos:
                intentMain = new Intent(this,ListaProductos.class);
                startActivityForResult(intentMain,CARGAR_PRODUCTOS);
                break;
            default:
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK ){
            Bundle resultado = data.getExtras();
            String dato = resultado.getString("RESULTADO");
            switch (requestCode) {
                case AGREGAR_VENDEDOR:
                    txtVwVendedor.setText("Vendedor: " + dato);
                    Toast.makeText(this, "Resultado: " + dato, Toast.LENGTH_SHORT).show();
                    break;
                case AGREGAR_CHOFER:
                    txtVwChofer.setText("Chofer: " + dato);
                    Toast.makeText(this, "Resultado: " + dato, Toast.LENGTH_SHORT).show();
                    break;
                case AGREGAR_MOVIL:
                    txtVwMovil.setText("Móvil: " + dato);
                    Toast.makeText(this, "Resultado: " + dato, Toast.LENGTH_SHORT).show();
                    break;
                case CARGAR_PRODUCTOS:
                    Toast.makeText(this, "Resultado: " + dato, Toast.LENGTH_SHORT).show();
                    break;
            }
        }else{
            if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "No se seleccionó un vendedor", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
