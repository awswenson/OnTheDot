package cs407.onthedot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class NewTripAddFriendsFragment extends Fragment {

    private static final String ARG_FB_FRIENDS_ID_LIST = "FB_FRIENDS_ID_LIST";

    private ArrayList<String> facebookFriendsIdList;

    OnNewTripAddFriendsListener onNewTripAddFriendsListenerCallback;

    public interface OnNewTripAddFriendsListener {
        public void onNewTripAddFriendsUpdated(ArrayList<String> facebookFriendsIdList);
        public void onCreateTripButtonPressed();
    }

    public NewTripAddFriendsFragment() {
        // Required empty public constructor
    }

    public static NewTripAddFriendsFragment newInstance(ArrayList<String> facebookFriendsIdList) {
        NewTripAddFriendsFragment fragment = new NewTripAddFriendsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_FB_FRIENDS_ID_LIST, facebookFriendsIdList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            facebookFriendsIdList = getArguments().getStringArrayList(ARG_FB_FRIENDS_ID_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_trip_add_friends, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            try {
                onNewTripAddFriendsListenerCallback = (OnNewTripAddFriendsListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement OnNewTripAddFriendsListener");
            }
        }
    }

}
