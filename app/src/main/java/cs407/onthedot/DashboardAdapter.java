package cs407.onthedot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by AlexanderSwenson on 4/10/16.
 */

public class DashboardAdapter extends BaseAdapter {

    private ArrayList<Trip> tripsList;
    private Context context;

    private static class ViewHolder {
        ImageView destination_imageView;
        TextView destination_textView;
        TextView meetupTime_textView;
        TextView leaveIn_textView;
    }

    public DashboardAdapter(Context context, ArrayList<Trip> tripsList) {
        this.tripsList = tripsList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tripsList.size();
    }

    @Override
    public Trip getItem(int position) {
        return tripsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view

        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.dashboard_trip_cell, parent, false);

            viewHolder.destination_imageView = (ImageView) convertView.findViewById(R.id.destination_imageView);
            viewHolder.destination_textView = (TextView) convertView.findViewById(R.id.destination_textView);
            viewHolder.meetupTime_textView = (TextView) convertView.findViewById(R.id.meetupTime_textView);
            viewHolder.leaveIn_textView = (TextView) convertView.findViewById(R.id.leaveIn_textView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the data item for this position
        final Trip trip = getItem(position);

        // TODO Populate the data into the template view using the data object

        // Return the completed view to render on screen
        return convertView;
    }
}

