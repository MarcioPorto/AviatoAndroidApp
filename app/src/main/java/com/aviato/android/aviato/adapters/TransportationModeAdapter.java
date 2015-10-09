package com.aviato.android.aviato.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aviato.android.aviato.R;
import com.aviato.android.aviato.models.TransportationMode;

public class TransportationModeAdapter extends BaseAdapter {

    private Context mContext;
    private TransportationMode[] mTypesOfTransportation;

    public TransportationModeAdapter(Context context, TransportationMode[] typesOfTransportation) {
        mContext = context;
        mTypesOfTransportation = typesOfTransportation;
    }

    @Override
    public int getCount() {
        return mTypesOfTransportation.length;
    }

    @Override
    public Object getItem(int position) {
        return mTypesOfTransportation[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;   // We're not going to use this, but it needs to be here anyway.
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            // brand new
            convertView = LayoutInflater.from(mContext).inflate(R.layout.transportation_mode_item, null);
            holder = new ViewHolder();
            holder.transportIcon = (ImageView)convertView.findViewById(R.id.transportationModeIcon);
            holder.transportType = (TextView)convertView.findViewById(R.id.transportationType);
            holder.tripTime = (TextView)convertView.findViewById(R.id.tripTime);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        TransportationMode mode = mTypesOfTransportation[position];

        holder.transportIcon.setImageResource(mode.getIconId(mode.getTransportType()));
        holder.transportType.setText(mode.getTransportType());
        switch (mode.getTransportType()) {
            case "Car":
                holder.tripTime.setText(mode.getTripTime() + " min");
            case "Transit":
                // holder.tripTime.setText(mode.getTripTime() + " min");
                holder.tripTime.setText("1h 26min");
                break;
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView transportIcon;
        TextView transportType;
        TextView tripTime;
    }

}
