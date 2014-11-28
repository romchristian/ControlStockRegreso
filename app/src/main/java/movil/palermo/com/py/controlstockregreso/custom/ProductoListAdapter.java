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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import movil.palermo.com.py.controlstockregreso.AppController;
import movil.palermo.com.py.controlstockregreso.R;
import movil.palermo.com.py.controlstockregreso.modelo.Producto;


public class ProductoListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Producto> productos;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ProductoListAdapter(Activity activity, List<Producto> productos) {
        this.activity = activity;
        this.productos = productos;
    }

    @Override
    public int getCount() {
        return productos.size();
    }

    @Override
    public Object getItem(int location) {
        return productos.get(location);
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
            convertView = inflater.inflate(R.layout.list_row, null);

        /*if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);*/

        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView genre = (TextView) convertView.findViewById(R.id.genre);
        TextView year = (TextView) convertView.findViewById(R.id.releaseYear);

        // getting movie data for the row
        Producto m = productos.get(position);

        // thumbnail image
        /*thumbNail.setImageUrl(m.getImg(), imageLoader);*/

        int img = 0;
        if (m.getNombre().toLowerCase().contains("blue")) {
            img = R.drawable.palermo_blue;
        } else if (m.getNombre().toLowerCase().contains("green")) {
            img = R.drawable.palermo_green;
        }else if (m.getNombre().toLowerCase().contains("red")) {
            img = R.drawable.palermo_red;
        }else if (m.getNombre().toLowerCase().contains("plm 3")) {
            img = R.drawable.plm3;
        }else if (m.getNombre().toLowerCase().contains("duo")) {
            img = R.drawable.palermo_duo;
        }else if (m.getId() == 218) {
            img = R.drawable.kentucky_10;
        }else if (m.getId() == 403) {
            img = R.drawable.sanmarino_20;
        }else if (m.getId() == 404) {
            img = R.drawable.sanmarino_10;
        }else if (m.getId() == 198) {
            img = R.drawable.kentucky_20;
        }else if (m.getId() == 204) {
            img = R.drawable.kentucky_soft;
        }else{
            img = R.drawable.kit;
        }

        thumbNail.setImageResource(img);

        // title
        title.setText(m.getNombre());

        // rating
        rating.setText("Rating: " + String.valueOf(2));

        // genre

        genre.setText(m.getUnidadMedidadEstandar()+"");

        // release year
        year.setText(String.valueOf(2014));

        return convertView;
    }

}
