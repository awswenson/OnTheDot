package cs407.onthedot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class NewTripAddFriendsFragment extends ListFragment {

    private static final String ARG_FB_FRIENDS_ID_LIST = "FB_FRIENDS_ID_LIST";
    private static final String ARG_FB_FRIENDS_ATTENDING_LIST = "FB_FRIENDS_ATTENDING_LIST";
    private static final String ARG_FB_FRIENDS_NAME_LIST = "FB_FRIENDS_NAME_LIST";

    private ArrayList<Friend> facebookFriendsList;


    OnNewTripAddFriendsListener onNewTripAddFriendsListenerCallback;

    public interface OnNewTripAddFriendsListener {
        public void onNewTripAddFriendsUpdated(ArrayList<Friend> facebookFriendsList);
        public void onCreateTripButtonPressed();
    }


    public NewTripAddFriendsFragment() {
        // Required empty public constructor
    }

    public static NewTripAddFriendsFragment newInstance(ArrayList<Friend> facebookFriendsList) {
        NewTripAddFriendsFragment fragment = new NewTripAddFriendsFragment();
        Bundle args = new Bundle();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> attendingStatuses = new ArrayList<String>();
        ArrayList<String> ids = new ArrayList<String>();
        //save list of friends in bundle
        for (int i = 0; i<facebookFriendsList.size(); i++){
            names.add(facebookFriendsList.get(i).getName());
            String attendingValue = facebookFriendsList.get(i).isAttending()? "true": "false";
            attendingStatuses.add(attendingValue);
            ids.add(facebookFriendsList.get(i).getId());
        }

        args.putStringArrayList(ARG_FB_FRIENDS_NAME_LIST, names);
        args.putStringArrayList(ARG_FB_FRIENDS_ATTENDING_LIST, attendingStatuses);
        args.putStringArrayList(ARG_FB_FRIENDS_ID_LIST, ids);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList<String> names = getArguments().getStringArrayList(ARG_FB_FRIENDS_NAME_LIST);
            ArrayList<String> attending = getArguments().getStringArrayList(ARG_FB_FRIENDS_ATTENDING_LIST);
            ArrayList<String> ids = getArguments().getStringArrayList(ARG_FB_FRIENDS_ID_LIST);
            //reform the friend objects so that we can set the instance variable
            facebookFriendsList = new ArrayList<Friend>();
            for (int i = 0; i < getArguments().getStringArrayList(ARG_FB_FRIENDS_ID_LIST).size(); i++){
                Friend newFriend = new Friend(names.get(i),
                        (attending.get(i).equals("true"))? true: false, ids.get(i));
                facebookFriendsList.add(newFriend);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setListAdapter(new FriendsListAdapter(getActivity(), facebookFriendsList));
        // Inflate the layout for this fragment
        View v = super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("DEBUG: ", "onAttach");
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
        //if the friend is being selected for the trip

        //if the friend is being deselected from the trip
        //onNewTripAddFriendsListenerCallback.onNewTripAddFriendsUpdated();
        Toast.makeText(getActivity(), "Item " + pos + " was clicked", Toast.LENGTH_SHORT).show();
    }

}
