package cs407.onthedot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class NewTripAddFriendsFragment extends ListFragment {

    private static final String ARG_FB_FRIENDS_LIST = "FB_FRIENDS_LIST";

    private ArrayList<Friend> facebookFriendsList;

    private Button cancel_button;
    private Button create_button;

    OnNewTripAddFriendsListener onNewTripAddFriendsListenerCallback;

    public interface OnNewTripAddFriendsListener {

        /**
         * Update the friends list associated with the trip
         *
         * @param facebookFriendsList The list of friends that are currently selected to
         *                            participate in the trip
         */
        public void onNewTripAddFriendsUpdated(ArrayList<Friend> facebookFriendsList);

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

    public static NewTripAddFriendsFragment newInstance(ArrayList<Friend> facebookFriendsList) {
        NewTripAddFriendsFragment fragment = new NewTripAddFriendsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_FB_FRIENDS_LIST, facebookFriendsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.facebookFriendsList = getArguments().getParcelableArrayList(ARG_FB_FRIENDS_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        //super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_new_trip_add_friends, container, false);

        setListAdapter(new FriendsListAdapter(getActivity(), facebookFriendsList));

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
                        .onNewTripAddFriendsUpdated(facebookFriendsList);

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
        //if the friend is being selected for the trip

        //if the friend is being deselected from the trip
        //onNewTripAddFriendsListenerCallback.onNewTripAddFriendsUpdated();
        Toast.makeText(getActivity(), "Item " + pos + " was clicked", Toast.LENGTH_SHORT).show();
    }

}
