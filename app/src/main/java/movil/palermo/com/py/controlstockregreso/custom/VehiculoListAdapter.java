package movil.palermo.com.py.controlstockregreso.custom;

/**
 * Created by cromero on 14/11/2014.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import movil.palermo.com.py.controlstockregreso.R;
import movil.palermo.com.py.controlstockregreso.modelo.Conductor;
import movil.palermo.com.py.controlstockregreso.modelo.Vehiculo;


public class VehiculoListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Vehiculo> vehiculos;

    public VehiculoListAdapter(Activity activity, List<Vehiculo> conductores) {
        this.activity = activity;
        this.vehiculos = conductores;
    }

    @Override
    public int getCount() {
        return vehiculos.size();
    }

    @Override
    public Object getItem(int location) {
        return vehiculos.get(location);
    }

    @Override
    public long getItemId(int position) {
        return vehiculos.get(position) == null ? vehiculos.get(position).getId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_vehiculo, null);

        TextView nro = (TextView) convertView.findViewById(R.id.nro);
        TextView marca = (TextView) convertView.findViewById(R.id.marca);
        TextView chapa = (TextView) convertView.findViewById(R.id.chapa);

        Vehiculo v = vehiculos.get(position);
        nro.setText("Nro: 123456"); //falta el dato
        marca.setText("Marca: "+ v.getMarca());
        chapa.setText("Chapa: "+v.getChapa());

        return convertView;
    }

}
