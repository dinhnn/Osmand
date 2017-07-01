package net.osmand.plus;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.osmand.plus.activities.MapActivity;

/**
 * Created by dinhnn on 6/25/17.
 */

public class OnPowerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if( !myKM.inKeyguardRestrictedInputMode()) {
            Intent i = new Intent(context, MapActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

    }
}