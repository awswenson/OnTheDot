package cs407.onthedot;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class FacebookLoginFragment extends android.app.Fragment {
    com.facebook.CallbackManager callbackManager;
    private OnFragmentInteractionListener mListener;

    public FacebookLoginFragment() {
        // Required empty public constructor
    }

    public static FacebookLoginFragment newInstance(String param1, String param2) {
        FacebookLoginFragment fragment = new FacebookLoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //link up the facebook sdk
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
    }

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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    private void onFBLogin(){
        callbackManager = CallbackManager.Factory.create();

        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos", "public_profile"));

        //LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResults>(){}
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>(){
            @Override
            public void onSuccess(LoginResult loginResult){
                Toast.makeText(getActivity(),"Success!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(){
                Toast.makeText(getActivity(),"Cancel!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception){
                Toast.makeText(getActivity(),"Error!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}




/*LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>(){
            @Override
            public void onSuccess(LoginResult loginResult){

            }

            @Override
            public void onCancel(){

            }

            @Override
            public void onError(FacebookException exception){

            }
        });
        */

        /*
        //if the user is already logged in, then don't bother with facebook login fragment
        SharedPreferences prefs = getSharedPreferences( PREFS_NAME, 0);
        String token = prefs.getString("facebookToken", null);
        if (token == null){
            //if token is null, then we must start the login fragment
        }
        */
        /*
        //facebook login button being pressed triggers new function
        LoginButton loginButton = (LoginButton) this.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //call private method
                onFBLogin();
            }
        });
        */
