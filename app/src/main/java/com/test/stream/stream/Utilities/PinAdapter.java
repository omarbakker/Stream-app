package com.test.stream.stream.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.test.stream.stream.Objects.Board.PinMessage;
import com.test.stream.stream.R;

import java.util.ArrayList;

/**
 * Created by kevinwong on 2016-10-30.
 */

public class PinAdapter extends ArrayAdapter<PinMessage> {

    public PinAdapter(Context context, ArrayList<PinMessage> pins){
        super(context, 0, pins);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        PinMessage pin = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_pin,parent, false);
        }

        TextView pinTitle = (TextView) convertView.findViewById(R.id.item_pin_title);
        TextView pinSubtitle = (TextView) convertView.findViewById(R.id.item_pin_subtitle);
        TextView pinDescription = (TextView) convertView.findViewById(R.id.item_pin_description);

        pinTitle.setText(pin.getTitle());
        pinSubtitle.setText(pin.getSubtitle());
        pinDescription.setText(pin.getDescription());

        return convertView;
    }
}
