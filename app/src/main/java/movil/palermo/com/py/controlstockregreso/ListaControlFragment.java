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

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

import info.vividcode.android.zxing.CaptureActivity;
import info.vividcode.android.zxing.CaptureActivityIntents;
import info.vividcode.android.zxing.CaptureResult;
import movil.palermo.com.py.controlstockregreso.custom.ControlListAdapter;
import movil.palermo.com.py.controlstockregreso.modelo.Control;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.EstadoControl;


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
        if (adapter.getCount()>0) {
            listaControl.setVisibility(View.VISIBLE);
            txtVwNuevoControl.setVisibility(View.GONE);
            imgVwflecha.setVisibility(View.GONE);
        }
        else {
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
        dialog.setMessage("Desea editar este control?");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent captureIntent = new Intent(rootView.getContext(),CaptureActivity.class);
                //captureIntent.putExtra("CONTROL", (Control) adapterView.getItemAtPosition(position));
                CaptureActivityIntents.setPromptMessage(captureIntent, "Escaneando código de autorización...");
                startActivityForResult(captureIntent, 1);



               /* Intent i = new Intent(rootView.getContext(),MainCrearControlActivity.class);
                i.putExtra("CONTROL", (Control) parent.getItemAtPosition(pos));
                startActivity(i);*/
            }
        });

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == CaptureActivity.RESULT_OK) {
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
                if(res.getContents().toString().equalsIgnoreCase("ventastp2014")){

                    Intent i = new Intent(rootView.getContext(),MainCrearControlActivity.class);
                    i.putExtra("CONTROL", controlSeleccionado);
                    startActivity(i);
                }else
                {
                    Toast.makeText(rootView.getContext(),"Código de verificación incorrecto", Toast.LENGTH_SHORT).show();
                }
            } else {

                // Process comes here when “back” button was clicked for instance.
            }
        }
    }
}
