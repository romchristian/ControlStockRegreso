package movil.palermo.com.py.stockregreso;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import movil.palermo.com.py.stockregreso.modelo.SupervisorSimple;
import movil.palermo.com.py.stockregreso.util.UtilJson;

public class RegistrarActivity extends ActionBarActivity implements View.OnClickListener {

    private EditText editEmpresa;
    private EditText editSucursal;
    private EditText editNombre;
    private EditText editUser;
    private EditText editPassword;
    private Button btnAceptar;
    private Button btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        editEmpresa = (EditText) findViewById(R.id.edit_empresa);
        editSucursal = (EditText) findViewById(R.id.edit_sucursal);
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
            Integer empresaid =null;
            Integer sucursalid =null;
            try {
                empresaid = Integer.parseInt(editEmpresa.getText().toString());
                sucursalid = Integer.parseInt(editSucursal.getText().toString());
                s.setEmpresaId(empresaid);
                s.setSucursalId(sucursalid);
                s.setNombre(editNombre.getText().toString());
                s.setUserid(editUser.getText().toString());
                s.setPassword(editPassword.getText().toString());
                s.setTelefonoId(utilJson.getTelefonoId());
                utilJson.enviarRegistro(s);

                finish();
            }catch (Exception ex){
                Toast.makeText(this,"Hay campor invalidos",Toast.LENGTH_LONG).show();
            }


        }
    }

    private boolean validar() {
        return true;
    }

    private void salir() {
        finish();
    }
}
