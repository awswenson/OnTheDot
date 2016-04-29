package cs407.onthedot;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.Toast;

import com.cs407.onthedot.onthedotbackend.myApi.MyApi;
import com.cs407.onthedot.onthedotbackend.myApi.model.TaskBean;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by connerhuff on 4/27/16.
 */
class EndpointsAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
    private static MyApi myApiService = null;
    private Context context;

    //DO NOT USE THIS FUNCTION
    @Override
    protected String doInBackground(Pair<Context, String>... params) {
        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://poised-team-129420.appspot.com/_ah/api/");
            // end options for devappserver

            myApiService = builder.build();
        }

        context = params[0].first;
        String name1 = params[0].second;
        TaskBean name = new TaskBean();

        try {
            //return myApiService.sayHi(name).execute().getData();
            //myApiService.getTasks().execute();//original
            myApiService.insertBean(name).execute();
            return null;
        } catch (IOException e) {
            return e.getMessage();
        }
    }



    //protected String doInBackgroundPUT(Pair<Context, TaskBean>... params) {
    protected String doInBackgroundPUT(Context context, TaskBean taskBean) {
        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://poised-team-129420.appspot.com/_ah/api/");
            // end options for devappserver

            myApiService = builder.build();
        }

        //context = params[0].first;
        //TaskBean task = params[0].second;

        try {
            //return myApiService.sayHi(name).execute().getData();
            //send off the task to the datastore
            //myApiService.storeTask(task).execute();
            myApiService.insertBean(taskBean).execute();
            return null;
        } catch (IOException e) {
            return e.getMessage();
        }
    }


    protected List<TaskBean> doInBackgroundGET(Pair<Context, TaskBean>... params) {
        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://poised-team-129420.appspot.com/_ah/api/");
            // end options for devappserver

            myApiService = builder.build();
        }

        context = params[0].first;
        TaskBean name = params[0].second;

        try {
            //return myApiService.sayHi(name).execute().getData();
            return myApiService.getTasks().execute().getItems();
        } catch (IOException e) {
            Log.d("ASYNC ERROR", "There was a problem in doInBackgroundGet");
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        //there is no result to display
        if (result != null){
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        }
    }

    protected void onPostExecute(List<TaskBean> result) {
        //there is no result to display
        if (result != null){

            Toast.makeText(context, result.get(0).getData(), Toast.LENGTH_LONG).show();
        }
    }
}
