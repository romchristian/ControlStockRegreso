package movil.palermo.com.py.controlstockregreso;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener{

    Button button;
    EditText usuario;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        button = (Button) findViewById(R.id.button_login);
        usuario = (EditText)findViewById(R.id.editText_usuario);
        password = (EditText)findViewById(R.id.editText_pass);

        button.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
            case R.id.button_login:

                if(usuario.getText() != null && usuario.getText().toString().compareToIgnoreCase("admin") == 0
                        && password.getText()!= null && password.getText().toString().compareToIgnoreCase("admin") == 0) {

                    //Mock.LOGEADO = true;
                    Intent intent = new Intent(this, MainCrearControlFragment.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(this,"Acceso Denegado",Toast.LENGTH_LONG);
                }
                break;
        }
    }
}
