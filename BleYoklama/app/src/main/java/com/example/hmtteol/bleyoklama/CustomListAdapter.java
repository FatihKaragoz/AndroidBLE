package com.example.hmtteol.bleyoklama;

/**
 * Created by hmtteol on 26.12.2017.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hmtteol.BleYoklama.R;

import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity Context;
    private final List<String> ListItemsName;

    public CustomListAdapter(Activity context, List<String> deviceName) {

        super(context, R.layout.custom_list_layout, deviceName);
        // TODO Auto-generated constructor stub
        this.Context = context;
        this.ListItemsName = deviceName;;
    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = Context.getLayoutInflater();
        View ListViewSingle = inflater.inflate(R.layout.custom_list_layout, null, true);

        TextView ListViewItems = (TextView) ListViewSingle.findViewById(R.id.tv);
        ImageView ListViewImage = (ImageView) ListViewSingle.findViewById(R.id.ikon);
        ListViewItems.setText(ListItemsName.get(ListItemsName.size()-1));
        ListViewImage.setImageResource(R.drawable.join);
        return ListViewSingle;

    };

}