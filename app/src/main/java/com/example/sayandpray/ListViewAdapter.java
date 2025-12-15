package com.example.sayandpray;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sayandpray.model.Product;

import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter<Product> {
    private Context context;
    private int resource;
    public ListViewAdapter(Context context, int resource, ArrayList<Product> items) {
        super(context, resource, items);
        this.context=context;
        this.resource=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = getItem(position);

        String namaDoa = getItem(position).getNamaDoa();
        String detailDoa = getItem(position).getDetailDoa();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource,parent,false);

        TextView txtNama = (TextView) convertView.findViewById(R.id.judulDoa);
        TextView txtDetail = (TextView) convertView.findViewById(R.id.isiDoa);
        TextView favLbl = (TextView) convertView.findViewById(R.id.favLbl);

        txtNama.setText(namaDoa);
        txtDetail.setText(detailDoa);

        if (product.getIsFavDoa()) {
            favLbl.setVisibility(View.VISIBLE);
        } else {
            favLbl.setVisibility(View.GONE);
        }

        return convertView;
    }
}
