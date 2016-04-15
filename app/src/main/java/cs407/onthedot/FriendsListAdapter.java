package cs407.onthedot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by connerhuff on 4/7/16.
 */
public class FriendsListAdapter extends ArrayAdapter<Friend> {


    Context context;

    public FriendsListAdapter(Context context, List<Friend> items) {
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
            viewHolder.toggleButton = (CheckBox) convertView.findViewById(R.id.toggleButton);
            viewHolder.entireRowCell = (LinearLayout) convertView.findViewById(R.id.entireRowCell);

            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        Friend friend = getItem(position);
        if (friend != null){
            viewHolder.usersName.setText(friend.getName());
            viewHolder.toggleButton.setChecked(friend.isAttending());
            //download the profile picture from facebook
            DownloadImage di = new DownloadImage((ImageView) convertView.findViewById(R.id.profilePicture));
            di.execute("http://graph.facebook.com/" + friend.getId() + "/picture?type=large");
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
        LinearLayout entireRowCell;
        ImageView profPic;
        TextView usersName;
        CheckBox toggleButton;
    }
}
