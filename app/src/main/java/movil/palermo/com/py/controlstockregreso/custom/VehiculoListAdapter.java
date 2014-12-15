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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.controlstockregreso.R;
import movil.palermo.com.py.controlstockregreso.modelo.Vehiculo;


public class VehiculoListAdapter extends BaseAdapter implements Filterable{
    private Activity activity;
    private LayoutInflater inflater;
    private List<Vehiculo> fullVehiculos;
    private List<Vehiculo> filteredVehiculos;
    private ItemFilter mFilter = new ItemFilter();

    public VehiculoListAdapter(Activity activity, List<Vehiculo> conductores) {
        this.activity = activity;
        this.fullVehiculos = conductores;
        this.filteredVehiculos =conductores;
    }

    @Override
    public int getCount() {
        return filteredVehiculos.size();
    }

    @Override
    public Object getItem(int location) {
        return filteredVehiculos.get(location);
    }

    @Override
    public long getItemId(int position) {
        return fullVehiculos.get(position) == null ? filteredVehiculos.get(position).getId() : position;
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

        Vehiculo v = filteredVehiculos.get(position);
        nro.setText("Nro: " + v.getNumero()); //falta el dato
        marca.setText("Marca: "+ v.getMarca());
        chapa.setText("Chapa: "+v.getChapa());

        return convertView;
    }
    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Vehiculo> list = fullVehiculos;

            int count = list.size();
            final ArrayList<Vehiculo> nlist = new ArrayList<Vehiculo>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                Vehiculo v = list.get(i);
                filterableString = v.getNumero().toString();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(v);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, android.widget.Filter.FilterResults results) {
            if(results!=null) {
                filteredVehiculos = (ArrayList<Vehiculo>) results.values;
                notifyDataSetChanged();
            }
        }

    }

}
