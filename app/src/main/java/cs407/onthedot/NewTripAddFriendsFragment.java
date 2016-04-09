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
        setListAdapter(new FriendsListAdapter(getActivity(), facebookFriendsIdList));
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
        Toast.makeText(getActivity(), "Item " + pos + " was clicked", Toast.LENGTH_SHORT).show();
    }

}
