package se.newbie.smartframe.receivers;

import se.newbie.smartframe.SmartFrameActivity;
import se.newbie.smartframe.model.IModel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStartReceiver extends BroadcastReceiver{
	private static final String TAG = "PowerReceiver";
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.v(TAG, "Broadcast received : " + intent.getAction());
    	
    	
    	if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

    	} else if (Intent.ACTION_DOCK_EVENT.equals(intent.getAction())) {
    		
    	} else if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {
        	/*Intent batteryStatus = context.getApplicationContext().registerReceiver(null,  new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        	
        	int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                status == BatteryManager.BATTERY_STATUS_FULL;    	
        	*/
            //if (isCharging) {
        		context.sendBroadcast(new Intent(IModel.ACTION_START));
            //}    		
    		
    	} else if (Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())) {
    		context.sendBroadcast(new Intent(IModel.ACTION_CLOSE));
    	} else if (IModel.ACTION_START.equals(intent.getAction())) {
        	Intent activity = new Intent(context, SmartFrameActivity.class);
        	activity.addFlags(Intent.FLAG_FROM_BACKGROUND 
        			| Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        			| Intent.FLAG_ACTIVITY_SINGLE_TOP 
        			| Intent.FLAG_ACTIVITY_NEW_TASK); 
        	context.startActivity(activity);    		
    	}
    }
}
