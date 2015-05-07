    package android.qleek;

    import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
//                Intent i = new Intent(context, MainActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                if (isNetworkAvailable(context))
//                {
//                    SharedPreferences settings = context.getSharedPreferences(Constants.DEVICE_STATE, 0);
//                    SharedPreferences.Editor editor = settings.edit();
//                    editor.putBoolean("setupMode", false);
//
//                    editor.commit();
//                    context.startActivity(i);
//                }
//            }
//            else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
//                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//
//                if(networkInfo.getDetailedState().toString().equals("CONNECTED"))
//                {
//                    SharedPreferences settings = context.getSharedPreferences(Constants.DEVICE_STATE, 0);
//                    SharedPreferences.Editor editor = settings.edit();
//                    editor.putBoolean("setupMode", false);
//
//                    editor.commit();
//                }
//                if(networkInfo.getDetailedState().toString().contains("DISCONNECTED")) {
////                    SharedPreferences settings = context.getSharedPreferences(Constants.DEVICE_STATE, 0);
////                    SharedPreferences.Editor editor = settings.edit();
////                    editor.putBoolean("setupMode", true);
////                    editor.commit();
//
//                    Log.i("BROADCAST RECEIVER", "Wifi disconnected");
//                }
//            }

            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo.getDetailedState().toString().equals("CONNECTED")) {
                    SharedPreferences settings = context.getSharedPreferences(Constants.DEVICE_STATE, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("playerMode", true);

                    editor.commit();
                }
                if (networkInfo.getDetailedState().toString().contains("DISCONNECTED")) {
                    SharedPreferences settings = context.getSharedPreferences(Constants.DEVICE_STATE, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("disconnectedMode", true);
                    editor.putBoolean("playerMode", false);
                    editor.commit();
                    // TODO manage disconnections
                }
            }
        }

        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

    }
