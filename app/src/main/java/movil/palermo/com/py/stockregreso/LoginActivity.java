package movil.palermo.com.py.stockregreso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.GsonBuilder;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

import movil.palermo.com.py.stockregreso.modelo.Login;
import movil.palermo.com.py.stockregreso.modelo.ResponseLogin;
import movil.palermo.com.py.stockregreso.util.UtilJson;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener{

    Button button;
    EditText usuario;
    EditText password;
    TextView version;
    public final static String PREFERENCIAS = "PREF_LOGIN";
    private Login login;
    public String telefonoId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        telefonoId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        button = (Button) findViewById(R.id.button_login);
        usuario = (EditText)findViewById(R.id.editText_usuario);
        password = (EditText)findViewById(R.id.editText_pass);
        version = (TextView)findViewById(R.id.txtVersion);
        button.setOnClickListener(this);
        version.setText("Version: " + obtenerVersion(this));
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
            Intent i = new Intent(this,RegistrarActivity.class);
            startActivity(i);
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
                    login.setTelefonoId(telefonoId);
                    enviarDatos(login);
                }else{
                    Toast.makeText(this,"Debe ingresar Usuario y Contraseña",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void enviarDatos(Login login) {
        if (estaConectado()) {
            probarServicio(login);
        }
    }
    //region Metodos para verificar conexion
    protected Boolean estaConectado() {
        return conectadoWifi();
    }

    protected Boolean conectadoWifi() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return true;
    }
    private void probarServicio(final Login login) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,(new UtilJson(this).prefUrl) + "/test" ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if(response!= null && response.compareTo("true") ==0){
                            try {
                                loginRequest(login);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SharedPreferences pref = getSharedPreferences(PREFERENCIAS,MODE_PRIVATE);
                String user_nombre = pref.getString("NOMBRE_"+login.getUsuario(),"");
                String user = pref.getString("USER_"+login.getUsuario(),"");
                String pass = pref.getString("PASS_"+login.getUsuario(),"");
                if(user_nombre.length() > 0 && user.length() > 0 && pass.length() >0
                        && user.compareTo(login.getUsuario()) == 0 && pass.compareTo(login.getPassword())==0){
                    logea(login,user_nombre);
                }else {
                    Toast.makeText(getApplicationContext(),"Usuario y/o contraseña incorrectos",Toast.LENGTH_LONG).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void logea(Login login, String nombre){
        SharedPreferences pref = getSharedPreferences(PREFERENCIAS,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("LOGUEADO", true);
        editor.putString("USER", login.getUsuario());
        editor.putString("USER_"+login.getUsuario(), login.getUsuario());
        editor.putString("PASS_"+login.getUsuario(), login.getPassword());
        editor.putString("NOMBRE_"+login.getUsuario(), nombre);
        editor.putString("NOMBRE", nombre);
        editor.putString("TELEFONOID",telefonoId);
        Log.d("TELEFONOID", telefonoId);
        editor.commit();
        Intent i;
        i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        finish();
    }
    private void loginRequest(final Login login) throws JSONException {

        final String body = new GsonBuilder().setPrettyPrinting().create().toJson(login);

        Request req = new JsonRequest<ResponseLogin>(Request.Method.POST, (new UtilJson(this).prefUrl) + "/login", body,
                new Response.Listener<ResponseLogin>() {
                    @Override
                    public void onResponse(ResponseLogin response) {
                        if (response.isExito()){
                            logea(login,response.getNombre());
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
    private String obtenerVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = info.versionName;
        //Toast.makeText(context, "Version: " + version, Toast.LENGTH_LONG).show();
        return version;
    }
}
