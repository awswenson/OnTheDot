package cs407.onthedot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by AlexanderSwenson on 5/4/16.
 */
public class BackendPollHandler extends BroadcastReceiver {

    private static final String INTENT_FACEBOOK_ID =
            "INTENT_FACEBOOK_ID";

    @Override
    public void onReceive(Context context, Intent intent) {

        String facebookID = intent.getStringExtra(INTENT_FACEBOOK_ID);

        BackendService.startGetTripsFromBackend(context, facebookID);
        Log.d("BackendPollHandler",
                "Called BackendService.startGetTripsFromBackend() from BackendPollHandler.onReceive");
    }
}
