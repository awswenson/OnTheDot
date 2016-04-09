package cs407.onthedot;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by connerhuff on 4/7/16.
 */
public class FriendsListAdapter extends ArrayAdapter<String> {


    Context context;

    public FriendsListAdapter(Context context, List<String> items) {
        super(context, R.layout.fragment_new_trip_add_friends, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //this is the first time we have used this view holder. Must initialize
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.friend_list_cell, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.profPic = (ImageView) convertView.findViewById(R.id.profilePicture);
            viewHolder.usersName = (TextView) convertView.findViewById(R.id.usersName);
            viewHolder.toggleButton = (RadioButton) convertView.findViewById(R.id.toggleButton);
            viewHolder.entireRowCell = (RelativeLayout) convertView.findViewById(R.id.entireRowCell);

            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        String id = getItem(position);//TODO make friend object?
        if (id != null){
            viewHolder.usersName.setText("TestName");
            //download the profile picture from facebook
            DownloadImage di = new DownloadImage((ImageView) convertView.findViewById(R.id.profilePicture));
            di.execute("http://graph.facebook.com/" + id + "/picture?type=large");
        }

        return convertView;
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     *
     * @see http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
     */
    private static class ViewHolder {
        RelativeLayout entireRowCell;
        ImageView profPic;
        TextView usersName;
        RadioButton toggleButton;
    }
}
