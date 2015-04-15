package movil.palermo.com.py.stockregreso.custom;

/**
 * Created by cromero on 14/11/2014.
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import movil.palermo.com.py.stockregreso.R;
import movil.palermo.com.py.stockregreso.modelo.Control;


public class ControlListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Control> controles;

    public ControlListAdapter(Activity activity, List<Control> controles) {
        this.activity = activity;
        this.controles = controles;
    }

    @Override
    public int getCount() {
        return controles.size();
    }

    @Override
    public Object getItem(int location) {
        return controles.get(location);
    }

    @Override
    public long getItemId(int position) {
        return controles.get(position) == null ? controles.get(position).getId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_control, null);

        TextView movil = (TextView) convertView.findViewById(R.id.movil);
        TextView chofer = (TextView) convertView.findViewById(R.id.chofer);
        TextView movilDetalle = (TextView) convertView.findViewById(R.id.movil_detalle);
        TextView fecha = (TextView) convertView.findViewById(R.id.fecha);

        Control c = controles.get(position);
        Log.d("ADAPTER", "Control: " + c);

        movil.setText(c.getVehiculo() != null ? "Móvil Nro: " + c.getVehiculo().getNumero() : "No definido");

        chofer.setText("Chof: " + c.getConductor() != null?c.getConductor().getNombre():"No definido");
        movilDetalle.setText(c.getVehiculo() != null ? c.getVehiculo().getMarca()+", Chapa: "+
                c.getVehiculo().getChapa()+", Km: "+c.getKm(): "No definido");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        fecha.setText(sdf.format(c.getFechaControl()));

        return convertView;
    }
}
