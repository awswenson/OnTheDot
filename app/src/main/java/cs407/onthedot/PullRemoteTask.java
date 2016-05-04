package cs407.onthedot;

import android.os.AsyncTask;
import android.util.Log;

import com.cs407.onthedot.onthedotbackend.taskApi.TaskApi;
import com.cs407.onthedot.onthedotbackend.taskApi.model.TaskBean;

import java.io.IOException;

/**
 * Created by connerhuff on 5/4/16.
 */
public class PullRemoteTask extends AsyncTask<TaskApi, Void, TaskBean> {

    protected TaskBean doInBackground(TaskApi... taskApiService) {
        TaskBean task = new TaskBean();
        try{
            task = new EndpointsPortal().taskApiService.sayHi("Jason").execute();
        }
        catch (IOException e){
            Log.e("Async exception", "Error when loading tasks", e);
        }

        //taskApiService.sayHi("Jason").execute();
        return task;
    }

    /*
    protected void onProgressUpdate(Void v) {

    }
    */

    protected void onPostExecute(TaskBean taskBean) {
        Log.e("SayHiResults", taskBean.getData());
    }
}
