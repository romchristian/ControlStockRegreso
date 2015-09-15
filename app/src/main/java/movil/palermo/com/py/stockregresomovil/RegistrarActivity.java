package movil.palermo.com.py.stockregresomovil;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import movil.palermo.com.py.stockregresomovil.R;
import movil.palermo.com.py.stockregresomovil.modelo.SupervisorSimple;
import movil.palermo.com.py.stockregresomovil.util.UtilJson;

public class RegistrarActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    //private EditText editEmpresa;
    private Spinner spinnerSucursal;
    private EditText editNombre;
    private EditText editUser;
    private EditText editPassword;
    private Button btnAceptar;
    private Button btnCancelar;
    private Integer empresaid =null;
    private Integer sucursalid =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configuraActionBar();
        setContentView(R.layout.activity_registrar);
        //editEmpresa = (EditText) findViewById(R.id.edit_empresa);
        spinnerSucursal = (Spinner) findViewById(R.id.spinnerSucursal);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sucursales_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerSucursal.setAdapter(adapter);
        spinnerSucursal.setOnItemSelectedListener(this);

        editNombre = (EditText) findViewById(R.id.edit_nombre);
        editUser = (EditText) findViewById(R.id.edit_user);
        editPassword = (EditText) findViewById(R.id.edit_password);
        btnAceptar = (Button) findViewById(R.id.button_aceptar);
        btnCancelar = (Button) findViewById(R.id.button_cancelar);

        btnAceptar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registrar, menu);
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
        switch (view.getId()) {
            case R.id.button_aceptar:
                enviaRegistro();
                break;
            case R.id.button_cancelar:
                salir();
                break;
        }
    }

    private void enviaRegistro() {
        UtilJson utilJson = new UtilJson(this);
        if(validar()) {
            SupervisorSimple s = new SupervisorSimple();
            //Integer empresaid =null;
            //Integer sucursalid =null;
            try {
                empresaid = 1;
                s.setEmpresaId(empresaid);
                s.setSucursalId(sucursalid);
                s.setNombre(editNombre.getText().toString());
                s.setUserid(editUser.getText().toString());
                s.setPassword(editPassword.getText().toString());
                s.setTelefonoId(utilJson.getTelefonoId());
                utilJson.enviarRegistro(s);

                finish();
            }catch (Exception ex){
                Toast.makeText(this,"Hay campos no validos",Toast.LENGTH_LONG).show();
            }


        }
    }
    private void configuraActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Registrar nuevo usuario");
        //actionBar.hide();
    }

    private boolean validar() {
        return true;
    }

    private void salir() {
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String sucursal = parent.getItemAtPosition(pos).toString();
        switch (sucursal){
            case "ASU":
                sucursalid = 1;
                //Toast.makeText(this,"Sucursal: " + sucursal + " " + sucursalid ,Toast.LENGTH_SHORT).show();
                break;
            case "CDE":
                sucursalid = 2;
                //Toast.makeText(this,"Sucursal: " + sucursal + " " + sucursalid ,Toast.LENGTH_SHORT).show();
                break;
            case "PJC":
                sucursalid = 4;
                //Toast.makeText(this,"Sucursal: " + sucursal + " " + sucursalid ,Toast.LENGTH_SHORT).show();
                break;
            case "ENC":
                sucursalid = 111;
                //Toast.makeText(this,"Sucursal: " + sucursal + " " + sucursalid ,Toast.LENGTH_SHORT).show();
                break;
            default:
                sucursalid = 1;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
