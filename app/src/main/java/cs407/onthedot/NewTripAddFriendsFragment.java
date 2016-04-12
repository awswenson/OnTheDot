package cs407.onthedot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewTripAddFriendsFragment extends ListFragment {

    private static final String ARG_FB_ATTENDING_FRIENDS_LIST = "FB_ATTENDING_FRIENDS_LIST";

    private ArrayList<Friend> attendingFBFriendsList;
    private ArrayList<Friend> entireFBFriendsList;

    private Button cancel_button;
    private Button create_button;

    private FriendsListAdapter friendsListAdapter;

    OnNewTripAddFriendsListener onNewTripAddFriendsListenerCallback;

    public interface OnNewTripAddFriendsListener {

        /**
         * Update the friends list associated with the trip
         *
         * @param attendingFBFriendsList The list of friends that are currently selected to
         *                               participate in the trip
         */
        public void onNewTripAddFriendsUpdated(ArrayList<Friend> attendingFBFriendsList);

        /**
         * Determines what happens after the "Create" button is clicked.  Ideally,
         * the class that implements this should make sure the Trip details are up to date
         * and then add the Trip to the database somehow
         */
        public void onCreateTripButtonPressed();
    }


    public NewTripAddFriendsFragment() {
        // Required empty public constructor
    }

    public static NewTripAddFriendsFragment newInstance(ArrayList<Friend> attendingFBFriendsList) {
        NewTripAddFriendsFragment fragment = new NewTripAddFriendsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_FB_ATTENDING_FRIENDS_LIST, attendingFBFriendsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.attendingFBFriendsList = getArguments().getParcelableArrayList(ARG_FB_ATTENDING_FRIENDS_LIST);
        }

        this.entireFBFriendsList = new ArrayList<Friend>();

        // Get all the Friends that correspond to this users friends list
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {

                    @Override
                    public void onCompleted(JSONArray object, GraphResponse response) {
                        Log.d("TEST GRAPH API FRIENDS", "onCompleted: " + object.toString());

                        // Parse JSON here and deliver it to the new ArrayList of friend ID's
                        for (int i = 0; i < object.length(); i++) {
                            try {

                                JSONObject friend = object.getJSONObject(i);
                                String id = friend.getString("id");
                                String name = friend.getString("name");
                                entireFBFriendsList.add(new Friend(name, false, id));
                            } catch(JSONException j) {
                                // Error getting the JSON, so do not add the name
                                Log.d("NewTripAddFriendsFrag", "ERROR: Trouble parsing FB Friends JSON");
                            }
                        }

                        // Make sure that the friendsListAdapter is already setup before
                        // notifying data change.  If it's not setup, it will be later.  This
                        // just means that FB data came back before views where setup
                        if (friendsListAdapter != null) {
                            friendsListAdapter.notifyDataSetChanged();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        //super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_new_trip_add_friends, container, false);

        friendsListAdapter = new FriendsListAdapter(getActivity(), entireFBFriendsList);

        setListAdapter(friendsListAdapter);

        friendsListAdapter.notifyDataSetChanged();

        /*
          TODO use the attendingFBFriendsList to highlight/select already selected friends in the ListView.
          Might want to do this after the entireFBFriendsList is populated from the call in OnCreate.
          That could happen before this point in time, or it could happen after.
        */

        cancel_button = (Button) view.findViewById(R.id.cancel_button);
        create_button = (Button) view.findViewById(R.id.create_button);

        cancel_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        create_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onNewTripAddFriendsListenerCallback
                        .onNewTripAddFriendsUpdated(attendingFBFriendsList);

                onNewTripAddFriendsListenerCallback.onCreateTripButtonPressed();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This ensures that the activity that created this fragment has implemented
        // the embedded listener class.
        if (context instanceof Activity) {
            try {
                onNewTripAddFriendsListenerCallback = (OnNewTripAddFriendsListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement OnNewTripAddFriendsListener");
            }
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);

        /*
          TODO If the friend is being selected for the trip, add the friend to the
          attendingFBFriendsList.  If the friend is being deselected from the trip, then remove
          the friend from attendingFBFriendsList.  DO NOT call
          onNewTripAddFriendsListenerCallback.onNewTripAddFriendsUpdated(attendingFBFriendsList). We
          only want to do that when the user presses "Create" or the back button.
        */

        Toast.makeText(getActivity(), "Item " + pos + " was clicked", Toast.LENGTH_SHORT).show();
    }

}
