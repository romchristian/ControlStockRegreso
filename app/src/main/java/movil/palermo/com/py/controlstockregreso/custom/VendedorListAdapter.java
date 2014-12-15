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
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import android.widget.Filter;

import movil.palermo.com.py.controlstockregreso.R;
import movil.palermo.com.py.controlstockregreso.modelo.Vendedor;


public class VendedorListAdapter extends BaseAdapter implements Filterable {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Vendedor> vendedores;
    private List<Vendedor> filteredVededores = null;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    public VendedorListAdapter(Activity activity, List<Vendedor> vendedores) {
        this.activity = activity;
        this.vendedores = vendedores;
        filteredVededores = vendedores;
    }



    @Override
    public int getCount() {
        return filteredVededores.size();
    }

    @Override
    public Object getItem(int location) {
        return filteredVededores.get(location);
    }

    @Override
    public long getItemId(int position) {
        return filteredVededores.get(position) == null ? filteredVededores.get(position).getId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_vendedor, null);

        TextView nombre = (TextView) convertView.findViewById(R.id.nombre);
        TextView conductor = (TextView) convertView.findViewById(R.id.conductor);

        Vendedor v = filteredVededores.get(position);
        nombre.setText(v.getNombre());
        conductor.setText(v.getConductor() == null ? "Conductor: " + "No asignado" : "Conductor: " + v.getConductor().getNombre());

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

            final List<Vendedor> list = vendedores;

            int count = list.size();
            final ArrayList<Vendedor> nlist = new ArrayList<Vendedor>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                Vendedor v = list.get(i);
                filterableString = v.getNombre();
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
                filteredVededores = (ArrayList<Vendedor>) results.values;
                notifyDataSetChanged();
            }
        }


    }
}
