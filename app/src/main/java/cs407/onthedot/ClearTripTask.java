package cs407.onthedot;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;

import java.io.IOException;

/**
 * Created by connerhuff on 5/4/16.
 */
public class ClearTripTask extends AsyncTask<TripApi, Void, Void> {

    Context context;

    Long tripIDToDelete;

    public ClearTripTask(Context context, Long tripIDToDelete) {
        super();

        this.context = context;
        this.tripIDToDelete = tripIDToDelete;
    }

    protected Void doInBackground(TripApi... tripApiService) {

        try {
            new EndpointsPortal().tripApiService.clearTripsById(this.tripIDToDelete).execute();
        } catch (IOException e) {
            Log.e("ClearTripTask", "Error when trying to call clearTripsById", e);
        }

        return null;
    }

    protected void onPostExecute(Void v) {
        DBHelper.getInstance(context).deleteTripByTripID(tripIDToDelete);
    }

}
