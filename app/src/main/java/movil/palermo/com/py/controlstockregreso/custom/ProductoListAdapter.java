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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.controlstockregreso.AppController;
import movil.palermo.com.py.controlstockregreso.R;
import movil.palermo.com.py.controlstockregreso.modelo.ProductoResumen;


public class ProductoListAdapter extends BaseAdapter implements Filterable {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ProductoResumen> fullProductos;
    private List<ProductoResumen> filteredProductos;
    private ItemFilter mFilter = new ItemFilter();
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ProductoListAdapter(Activity activity, List<ProductoResumen> productos) {
        this.activity = activity;
        this.fullProductos = productos;
        this.filteredProductos=productos;
    }

    @Override
    public int getCount() {
        return filteredProductos.size();
    }

    @Override
    public Object getItem(int location) {
        return filteredProductos.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_producto, null);

        /*if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);*/

        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView nombre = (TextView) convertView.findViewById(R.id.nombre);
        TextView tituloCajas=(TextView) convertView.findViewById(R.id.tituloCajas);
        TextView cantCajas = (TextView) convertView.findViewById(R.id.cantCajas);
        TextView tituloGruesas=(TextView) convertView.findViewById(R.id.tituloGruesas);
        TextView cantGruesas = (TextView) convertView.findViewById(R.id.cantGruesas);
        TextView tituloCajetillas=(TextView) convertView.findViewById(R.id.tituloUnidades);
        TextView cantCajetillas = (TextView) convertView.findViewById(R.id.cantUnidades);

        // getting movie data for the row
        ProductoResumen p = filteredProductos.get(position);

        // thumbnail image
        /*thumbNail.setImageUrl(m.getImg(), imageLoader);*/

        int img = 0;
        if (p.getNombre().toLowerCase().contains("blue")) {
            img = R.drawable.palermo_blue;
        } else if (p.getNombre().toLowerCase().contains("green")) {
            img = R.drawable.palermo_green;
        }else if (p.getNombre().toLowerCase().contains("red")) {
            img = R.drawable.palermo_red;
        }else if (p.getNombre().toLowerCase().contains("plm 3")) {
            img = R.drawable.plm3;
        }else if (p.getId() == 218) {
            img = R.drawable.kentucky_10;
        }else if (p.getId() == 403) {
            img = R.drawable.sanmarino_20;
        }else if (p.getId() == 404) {
            img = R.drawable.sanmarino_10;
        }else if (p.getId() == 198) {
            img = R.drawable.kentucky_20;
        }else if (p.getId() == 204) {
            img = R.drawable.kentucky_soft;
        }else if (p.getId() == 411) {
            img = R.drawable.sanmarino20;
        }else if (p.getId() == 412) {
            img = R.drawable.sanmarino10;
        }else if (p.getId() == 409) {
            img = R.drawable.duo20;
        }else if (p.getId() == 410) {
            img = R.drawable.duo10;
        }else{
            img = R.drawable.kit;
        }

        thumbNail.setImageResource(img);
        nombre.setText(p.getNombre());

        if(p.getKit()>0){
            tituloCajas.setVisibility(TextView.GONE);
            cantCajas.setVisibility(TextView.GONE);
            tituloGruesas.setVisibility(TextView.GONE);
            cantGruesas.setVisibility(TextView.GONE);
            tituloCajetillas.setText("Unidades");
            cantCajetillas.setText(""+  p.getCantUnidad());
        }else{
            tituloCajas.setVisibility(TextView.VISIBLE);
            cantCajas.setVisibility(TextView.VISIBLE);
            tituloGruesas.setVisibility(TextView.VISIBLE);
            cantGruesas.setVisibility(TextView.VISIBLE);
            cantCajas.setText(""+ (p.getCantCajas()));
            cantGruesas.setText(""+(p.getCantGruesas()));
            tituloCajetillas.setText("Cajetillas");
            cantCajetillas.setText(""+ p.getCantCajetillas());
        }

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

            final List<ProductoResumen> list = fullProductos;

            int count = list.size();
            final ArrayList<ProductoResumen> nlist = new ArrayList<ProductoResumen>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                ProductoResumen p = list.get(i);
                filterableString = p.getNombre();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(p);
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
                filteredProductos = (ArrayList<ProductoResumen>) results.values;
                notifyDataSetChanged();
            }
        }


    }

}
