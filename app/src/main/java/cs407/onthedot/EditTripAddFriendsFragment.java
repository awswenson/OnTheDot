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
import android.widget.CheckBox;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class EditTripAddFriendsFragment extends ListFragment {

    private static final String ARG_FB_ATTENDING_FRIENDS_LIST = "FB_ATTENDING_FRIENDS_LIST";

    private ArrayList<Friend> attendingFBFriendsList;
    private ArrayList<Friend> entireFBFriendsList;

    private Button cancel_button;
    private Button finish_button;

    private FriendsListAdapter friendsListAdapter;

    OnTripAddFriendsListener onTripAddFriendsListenerCallback;

    public interface OnTripAddFriendsListener {

        /**
         * Update the friends list associated with the trip
         *
         * @param attendingFBFriendsList The list of friends that are currently selected to
         *                               participate in the trip
         */
        public void onTripAddFriendsUpdated(ArrayList<Friend> attendingFBFriendsList);

        /**
         * Determines what happens after the "Finish" button is clicked.  Ideally,
         * the class that implements this should make sure the Trip details are up to date
         * and then add the Trip to the database somehow
         */
        public void onFinishTripButtonPressed();
    }

    public EditTripAddFriendsFragment() {
        // Required empty public constructor
    }

    public static EditTripAddFriendsFragment newInstance(ArrayList<Friend> attendingFBFriendsList) {
        EditTripAddFriendsFragment fragment = new EditTripAddFriendsFragment();
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
                            } catch (JSONException j) {
                                // Error getting the JSON, so do not add the name
                                Log.d("NewTripAddFriendsFrag", "ERROR: Trouble parsing FB Friends JSON");
                            }
                        }

                        // Populate the HashMap that will house the id's of the people
                        // who are invited currently
                        HashSet<String> attendingFBFriendsHashMap = new HashSet<>();
                        for (Friend attendingFriend : attendingFBFriendsList) {

                            attendingFBFriendsHashMap.add(attendingFriend.getId());
                        }

                        // Go through entire friends list and set attending for the ones who
                        // are in the HashMap
                        for (Friend friend : entireFBFriendsList) {

                            // If this is true, this person is in the attending list.  We should
                            // change their attending value to true.
                            if (attendingFBFriendsHashMap.contains(friend.getId())) {
                                friend.setAttending(true);
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
        View view = inflater.inflate(R.layout.fragment_edit_trip_add_friends, container, false);



        friendsListAdapter = new FriendsListAdapter(getActivity(), entireFBFriendsList);
 
        setListAdapter(friendsListAdapter);

        friendsListAdapter.notifyDataSetChanged();

        cancel_button = (Button) view.findViewById(R.id.cancel_button);
        finish_button = (Button) view.findViewById(R.id.finish_button);

        cancel_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        finish_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onTripAddFriendsListenerCallback
                        .onTripAddFriendsUpdated(attendingFBFriendsList);

                onTripAddFriendsListenerCallback.onFinishTripButtonPressed();
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
                onTripAddFriendsListenerCallback = (OnTripAddFriendsListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement OnNewTripAddFriendsListener");
            }
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);

        Friend selectedFriend = entireFBFriendsList.get(pos);

        CheckBox checkBox = (CheckBox) v.findViewById(R.id.toggleButton);

        // Check if the selectedFriend was already invited to the Trip. If so, then the
        // user is uninviting them on the trip.
        if (checkIfFriendIsSelectedAlready(selectedFriend)) { // The friend is being uninvited
            attendingFBFriendsList.remove(selectedFriend);
            checkBox.setChecked(false);
        }
        else { // The friend is being invited
            attendingFBFriendsList.add(selectedFriend);
            checkBox.setChecked(true);
        }
    }

    /**
     * Checks if the given friend is already invited.
     *
     * @param friend The friend to check if they are already attending
     * @return True if the friend was already in the attendingFBFriendsList; false otherwise.
     */
    private boolean checkIfFriendIsSelectedAlready(Friend friend){

        if (friend == null) {
            return false;
        }

        for (Friend friendInList : attendingFBFriendsList) {
            if (friendInList.equals(friend)) {
                return true;
            }
        }

        return false;
    }
}
