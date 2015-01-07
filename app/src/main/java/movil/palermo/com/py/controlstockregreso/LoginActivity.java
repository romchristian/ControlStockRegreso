package movil.palermo.com.py.controlstockregreso;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.GsonBuilder;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

import movil.palermo.com.py.controlstockregreso.modelo.Control;
import movil.palermo.com.py.controlstockregreso.modelo.Login;
import movil.palermo.com.py.controlstockregreso.modelo.ResponseLogin;
import movil.palermo.com.py.controlstockregreso.util.UtilJson;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener{

    Button button;
    EditText usuario;
    EditText password;
    public final static String PREFERENCIAS = "PREF_LOGIN";
    private Login login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        button = (Button) findViewById(R.id.button_login);
        usuario = (EditText)findViewById(R.id.editText_usuario);
        password = (EditText)findViewById(R.id.editText_pass);

        button.setOnClickListener(this);
        SharedPreferences pref = getSharedPreferences(PREFERENCIAS,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("LOGUEADO", false);
        editor.commit();
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
                if(!(usuario.getText().toString().isEmpty()) && !(password.getText().toString().isEmpty())){
                    login = new Login();
                    login.setUsuario(usuario.getText().toString());
                    login.setPassword(password.getText().toString());
                    try {
                        loginRequest(login);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(this,"Debe ingresar Usuario y Contraseña",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void loginRequest(Login login) throws JSONException {

        final String body = new GsonBuilder().setPrettyPrinting().create().toJson(login);

        Request req = new JsonRequest<ResponseLogin>(Request.Method.POST, UtilJson.PREF_URL + "/login", body,
                new Response.Listener<ResponseLogin>() {
                    @Override
                    public void onResponse(ResponseLogin response) {
                        if (response.isExito()){
                            Toast.makeText(getApplicationContext(),"Login exitoso!",Toast.LENGTH_LONG).show();
                            SharedPreferences pref = getSharedPreferences(PREFERENCIAS,MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("LOGUEADO", true);
                            editor.putString("USER", usuario.getText().toString());
                            editor.putString("NOMBRE", response.getNombre());
                            editor.commit();
                            Intent i;
                            i = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(i);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),"Usuario y/o contraseña incorrectos",Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"ERROR DE LOGIN: " + error.getMessage(),Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Response<ResponseLogin> parseNetworkResponse(NetworkResponse response) {
                String jsonString = null;
                try {
                    jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                ResponseLogin resp = new GsonBuilder().create().fromJson(jsonString, ResponseLogin.class);
                Response<ResponseLogin> result = Response.success(resp, HttpHeaderParser.parseCacheHeaders(response));
                return result;
            }
        };
        AppController.getInstance().addToRequestQueue(req);
    }
}
