package movil.palermo.com.py.stockregreso.custom;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import movil.palermo.com.py.stockregreso.R;

import movil.palermo.com.py.stockregreso.modelo.ReposicionDetalle;

/**
 * Created by jcolman on 02/03/2015.
 */
public class ReposicionDetalleListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ReposicionDetalle> detalles;

    public ReposicionDetalleListAdapter(Activity activity, List<ReposicionDetalle> detalles) {
        this.activity = activity;
        this.detalles = detalles;
    }

    @Override
    public int getCount() {
        return detalles.size();
    }

    @Override
    public Object getItem(int location) {
        return detalles.get(location);
    }

    @Override
    public long getItemId(int position) {
        return detalles.get(position) == null ? detalles.get(position).getId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_control_detalle, null);

        TextView unidadMedida = (TextView) convertView.findViewById(R.id.unidadMedida);
        TextView cantidad = (TextView) convertView.findViewById(R.id.cantidad);
        TextView producto = (TextView) convertView.findViewById(R.id.producto);
        TextView fecha = (TextView) convertView.findViewById(R.id.fecha);

        ReposicionDetalle d = detalles.get(position);
        Log.d("ADAPTER", "Detalle: " + d);

        unidadMedida.setText(d.getUnidadMedida() != null ? d.getUnidadMedida().getNombre() : "No definido");

        cantidad.setText("" + d.getCantidad());
        producto.setText(d.getProducto() != null ? d.getProducto().getNombre() : "No definido");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        fecha.setText(sdf.format(new Date()));

        return convertView;
    }
}