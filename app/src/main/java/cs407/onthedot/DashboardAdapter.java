package cs407.onthedot;

import android.content.Context;
import android.location.Address;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AlexanderSwenson on 4/10/16.
 */

public class DashboardAdapter extends BaseAdapter {

    private ArrayList<Trip> tripsList;
    private Context context;

    private static class ViewHolder {
        ImageView destination_imageView;
        TextView destination_textView;
        TextView additionalInfo_textView;
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

        final ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.dashboard_trip_cell, parent, false);

            viewHolder.destination_imageView = (ImageView) convertView.findViewById(R.id.destination_imageView);
            viewHolder.destination_textView = (TextView) convertView.findViewById(R.id.destination_textView);
            viewHolder.additionalInfo_textView = (TextView) convertView.findViewById(R.id.additionalInfo_textView);
            viewHolder.meetupTime_textView = (TextView) convertView.findViewById(R.id.meetupTime_textView);
            viewHolder.leaveIn_textView = (TextView) convertView.findViewById(R.id.leaveIn_textView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the data item for this position
        final Trip trip = getItem(position);

        DownloadImage di = new DownloadImage(viewHolder.destination_imageView);
        di.execute("http://maps.google.com/maps/api/staticmap?zoom=15&size=200x200&sensor=false&maptype=roadmap&markers=size:med|" +
                trip.getDestinationLatitude() + "," +
                trip.getDestinationLongitude());

        Address address = trip.getAddressInformation(context);

        if (address == null) {
            String destinationString = trip.getDestinationLatitude() + " " +
                    trip.getDestinationLongitude();

            viewHolder.destination_textView.setText(destinationString);
            viewHolder.additionalInfo_textView.setText("");
        }
        else {
            String additionalInfoString = address.getLocality() + ", " +
                    address.getAdminArea() + " " +
                    address.getPostalCode();

            viewHolder.destination_textView.setText(address.getAddressLine(0));
            viewHolder.additionalInfo_textView.setText(additionalInfoString);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String meetUpString = "Meet-up at " +
                timeFormat.format(trip.getMeetupTime()) + " on " +
                dateFormat.format(trip.getMeetupTime());

        viewHolder.meetupTime_textView.setText(meetUpString);

        if (trip.isTripComplete()) { // Trip is complete so do not display anything
            viewHolder.leaveIn_textView.setText("");
        }
        else { // Display when the user should leave

            String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + "Madison,+WI+53715&" + // TODO change to use starting location
                    "destination=" + trip.getDestinationLatitude() + "," + trip.getDestinationLongitude() +
                    "&departure_time=" + trip.getMeetupTime().getTime() +
                    "&traffic_model=best_guess&mode=walking";

            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                // routesArray contains ALL routes to get to the destination
                                JSONArray routesArray = response.getJSONArray("routes");

                                // Grab the first route
                                JSONObject route = routesArray.getJSONObject(0);

                                // Take all legs from the route (typically there should only be one
                                JSONArray legs = route.getJSONArray("legs");

                                // Grab first leg
                                JSONObject leg = legs.getJSONObject(0);

                                int duration = leg.getJSONObject("duration").getInt("value");

                                Calendar leaveAtCalendar = Calendar.getInstance();
                                leaveAtCalendar.setTime(trip.getMeetupTime());

                                leaveAtCalendar.add(Calendar.SECOND, -duration);

                                if ((new Date()).after(leaveAtCalendar.getTime())) {
                                    String leaveText = "Leave now";

                                    viewHolder.leaveIn_textView.setText(leaveText);
                                }
                                else {

                                    SimpleDateFormat timeFormat =
                                            new SimpleDateFormat("hh:mm a", Locale.getDefault());

                                    String leaveText = "Leave at " +
                                            timeFormat.format(leaveAtCalendar.getTime());

                                    viewHolder.leaveIn_textView.setText(leaveText);
                                }
                            } catch (JSONException e) {
                                viewHolder.leaveIn_textView.setText("");
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            viewHolder.leaveIn_textView.setText("");
                        }
                    });

            queue.add(jsObjRequest);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}

