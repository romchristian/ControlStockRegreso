package movil.palermo.com.py.controlstockregreso;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import info.vividcode.android.zxing.CaptureActivity;
import info.vividcode.android.zxing.CaptureActivityIntents;
import info.vividcode.android.zxing.CaptureResult;
import movil.palermo.com.py.controlstockregreso.custom.ControlListAdapter;
import movil.palermo.com.py.controlstockregreso.custom.GsonRequest;
import movil.palermo.com.py.controlstockregreso.modelo.Control;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.EstadoControl;
import movil.palermo.com.py.controlstockregreso.modelo.ReposicionDetalle;
import movil.palermo.com.py.controlstockregreso.modelo.ReposicionSimple;
import movil.palermo.com.py.controlstockregreso.modelo.ResponseControl;
import movil.palermo.com.py.controlstockregreso.modelo.ResponseReposicion;
import movil.palermo.com.py.controlstockregreso.util.UtilJson;


public class ListaControlFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener {


    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView listaControl;
    Intent datos;
    private List<Control> controlList = new ArrayList<Control>();
    private ControlListAdapter adapter;
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Control, Integer> controlDao;
    private ProgressDialog pDialog;
    private TextView txtVwNuevoControl;
    private ImageView imgVwflecha;
    private Control controlSeleccionado;
    public static final int REPOSICION = 101;
    private RuntimeExceptionDao<ReposicionDetalle, Integer> reposcionDetalleDao;


    View rootView;

    public static ListaControlFragment newInstance(int sectionNumber) {
        ListaControlFragment fragment = new ListaControlFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ListaControlFragment() {

    }

    @Override
    public void onResume() {
        recargaLista();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_lista_control, container, false);
        listaControl = (ListView) rootView.findViewById(R.id.listaControl);
        txtVwNuevoControl = (TextView) rootView.findViewById(R.id.txtVwNuevoControl);
        imgVwflecha = (ImageView) rootView.findViewById(R.id.imgFlecha);

        adapter = new ControlListAdapter(getActivity(), controlList);
        listaControl.setOnItemClickListener(this);

        listaControl.setAdapter(adapter);


        databaseHelper = new DatabaseHelper(getActivity());
        controlDao = databaseHelper.getControlDao();
        reposcionDetalleDao = databaseHelper.getReposicionDetalleDao();
        recargaLista();

        return rootView;
    }

    public void recargaLista() {
        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        controlList.clear();
        controlList.addAll(controlDao.queryForAll());
        adapter.notifyDataSetChanged();
        if (adapter.getCount() > 0) {
            listaControl.setVisibility(View.VISIBLE);
            txtVwNuevoControl.setVisibility(View.GONE);
            imgVwflecha.setVisibility(View.GONE);
        } else {
            listaControl.setVisibility(View.GONE);
            txtVwNuevoControl.setVisibility(View.VISIBLE);
            imgVwflecha.setVisibility(View.VISIBLE);
        }
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onItemClick(final AdapterView<?> adapterView, View view, final int position, long l) {

        controlSeleccionado = (Control) adapterView.getItemAtPosition(position);

        AlertDialog dialog = new AlertDialog.Builder(view.getContext()).create();
        dialog.setTitle("Advertencia!");
        dialog.setMessage("Seleccione la acción a realizar?");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Editar Control", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent captureIntent = new Intent(rootView.getContext(), CaptureActivity.class);
                //captureIntent.putExtra("CONTROL", (Control) adapterView.getItemAtPosition(position));
                CaptureActivityIntents.setPromptMessage(captureIntent, "Escaneando código de autorización...");
                startActivityForResult(captureIntent, 1);



               /* Intent i = new Intent(rootView.getContext(),MainCrearControlActivity.class);
                i.putExtra("CONTROL", (Control) parent.getItemAtPosition(pos));
                startActivity(i);*/
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Reposición", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(rootView.getContext(), ListaReposicionProducto.class);
                i.putExtra("CONTROL", (Control) controlSeleccionado);
                startActivityForResult(i, REPOSICION);
            }
        });

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == CaptureActivity.RESULT_OK) {

            switch (requestCode) {
                case REPOSICION:
                    Object obj = data.getSerializableExtra("RESULTADO");
                    Toast.makeText(rootView.getContext(), "Envio datos 1", Toast.LENGTH_SHORT).show();
                    if(obj != null && obj instanceof Control){
                        Toast.makeText(rootView.getContext(), "Envio datos 2", Toast.LENGTH_SHORT).show();
                        //envio reposiciones
                        Control c = (Control) obj;
                        enviarDatos(c);
                    }
                    break;
                case 1:
                    CaptureResult res = CaptureResult.parseResultIntent(data);
                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(rootView.getContext(), notification);
                        r.play();
                        Vibrator v = (Vibrator) rootView.getContext().getSystemService(Context.VIBRATOR_SERVICE);

                        v.vibrate(1000);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(rootView.getContext(), res.getContents() + " (" + res.getFormatName() + ")", Toast.LENGTH_LONG).show();
                    if (res.getContents().toString().equalsIgnoreCase("ventastp2014")) {

                        Intent i = new Intent(rootView.getContext(), MainCrearControlActivity.class);
                        i.putExtra("CONTROL", controlSeleccionado);
                        controlSeleccionado.setEstadoDescarga("N");
                        controlSeleccionado.setEstado(EstadoControl.MODIFICADO.toString());
                        startActivity(i);
                    } else {
                        Toast.makeText(rootView.getContext(), "Código de verificación incorrecto", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

        }

    }


    private void enviarDatos(Control c) {
        UtilJson util = new UtilJson((Activity)rootView.getContext());
        if (util.estaConectado()) {
            probarServicio(c);
        }
    }

    private void probarServicio(final Control c) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, UtilJson.PREF_URL + "/test",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if (response != null && response.compareTo("true") == 0) {
                            try{
                                insertaReposicion(c);
                            }catch (JSONException e){
                                e.printStackTrace();
                            }

                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private void insertaReposicion(final Control c) throws JSONException {

        List<ReposicionDetalle> lista = null;
        try {
            lista = reposcionDetalleDao.queryBuilder()
                    .where().eq(ReposicionDetalle.COL_CONTROL, c)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (lista != null && !lista.isEmpty()) {

            List<ReposicionSimple> lista2 = new ArrayList<>();
            for(ReposicionDetalle r : lista){
                ReposicionSimple rs = new ReposicionSimple(r);
                lista2.add(rs);
            }

            final String body = new GsonBuilder().setPrettyPrinting().create().toJson(lista2);

            Response.Listener<ResponseReposicion> listenerExito = new Response.Listener<ResponseReposicion>() {
                @Override
                public void onResponse(ResponseReposicion response) {

                    if (response.isExito()) {
                        Toast.makeText(rootView.getContext().getApplicationContext(), "Exito!", Toast.LENGTH_LONG).show();
                    }
                }
            };

            GsonRequest<ResponseReposicion> req = new GsonRequest<ResponseReposicion>(Request.Method.POST, UtilJson.PREF_URL + "/insertaReposicion", ResponseReposicion.class, body, listenerExito, rootView.getContext());

            AppController.getInstance().addToRequestQueue(req);
        }

    }
}
