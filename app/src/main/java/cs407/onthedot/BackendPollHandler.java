package cs407.onthedot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by AlexanderSwenson on 5/4/16.
 */
public class BackendPollHandler extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        BackendPollService.startSynchronizeLocalFromBackend(context);
        Log.d("BackendPollHandler",
                "Called BackendPollService.startActionSynchronizeDataFromBackend() from BackendPollHandler.onReceive");
    }
}
