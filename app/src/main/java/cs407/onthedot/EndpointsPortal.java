package cs407.onthedot;

import com.cs407.onthedot.onthedotbackend.tripApi.TripApi;
import com.cs407.onthedot.onthedotbackend.tripApi.model.TripBean;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Created by connerhuff on 5/3/16.
 */
public class EndpointsPortal  {

    final public TripApi tripApiService;


    public EndpointsPortal() {
        // Production testing
        //TripApi.Builder builder = new TripApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);

        // Local testing
        TripApi.Builder builder = new TripApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
        tripApiService = builder.build();
    }




    public synchronized void addTrip(TripBean trip) {
        new AddTripTask(trip).execute();
    }

    public synchronized void getTrips() {
        new GetTripsTask().execute();
    }

    public synchronized void clearTripById(Long id) {
        new ClearTripTask(id).execute();
    }


    //used for testing purposes to ensure backend is able to be communicated with
    public synchronized void sayHi() {

       // try {
            TripBean trip = new TripBean();
            trip.setId(new Long(1));
            //trip.setData("Test data");
            new PullRemoteTask().execute();
            //taskApiService.sayHi("Jason").execute();
            /*
        } catch (IOException e) {
            Log.e("Endpoint sayHi", "Error when loading tasks", e);
        }
        */
    }
}
