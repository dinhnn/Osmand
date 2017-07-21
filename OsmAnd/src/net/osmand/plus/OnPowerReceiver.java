package net.osmand.plus;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import net.osmand.plus.activities.MapActivity;

/**
 * Created by dinhnn on 6/25/17.
 */

public class OnPowerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        final SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if( !myKM.inKeyguardRestrictedInputMode() && sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            final SensorEventListener sensorEventListener = new SensorEventListener() {
                public void onSensorChanged(SensorEvent event) {
                    sensorManager.unregisterListener(this);
                    if (Math.abs(event.values[0]) > Math.abs(event.values[1]) && Math.abs(event.values[0]) > Math.abs(event.values[2])){
                        Intent i = new Intent(context, MapActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                }
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        }

    }
}