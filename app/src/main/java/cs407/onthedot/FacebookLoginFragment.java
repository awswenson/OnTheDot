package cs407.onthedot;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class FacebookLoginFragment extends android.app.Fragment {
    private OnFragmentInteractionListener mListener;

    public FacebookLoginFragment() {
        // Required empty public constructor
    }

    //not used for now
    public static FacebookLoginFragment newInstance(String param1, String param2) {
        FacebookLoginFragment fragment = new FacebookLoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        //this comment is here just to text a new branch actually being made
        int i = 0;
        i++;
        if (i < 2){
            i--;
        }
        return fragment;
    }

    //returns the view that will be inflated, i.e. the login button itself, as well as setting
    //a click listener on the login button. When it is clicked, we get sent to the login function
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_facebook_login, container, false);
        //facebook login button being pressed triggers new function
        LoginButton loginButton = (LoginButton) v.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //call private method
                onFBLogin();
            }
        });
        // Inflate the layout for this fragment
        return v;
    }

    //not positive if needed, but online people were saying it was needed
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //not positive if needed, but online people were saying it was needed
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //function that handles the permissions (i.e. friends list etc) as well as the actual action of
    //loggin in
    private void onFBLogin(){
        com.facebook.CallbackManager callbackManager = CallbackManager.Factory.create();

        // Set permissions and attempt the login
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("email", "user_photos", "public_profile", "user_friends"));

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
