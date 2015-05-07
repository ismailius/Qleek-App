    package android.qleek;

    import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

    public class MyBroadcastReceiver extends BroadcastReceiver {
        public static boolean ANDROID_BOOTED = false;
        @Override
        public void onReceive(Context context, Intent intent) {

            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

                ANDROID_BOOTED = true;
            }

            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) && ANDROID_BOOTED) {
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                if (networkInfo.getDetailedState().toString().equals("CONNECTED")) {
                    SharedPreferences settings = context.getSharedPreferences(Constants.DEVICE_STATE, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(Constants.DEVICE_STATE_PLAYER, true);
                    editor.putBoolean(Constants.DEVICE_STATE_SETUP, false);
                    editor.putBoolean(Constants.DEVICE_STATE_DISCONNECTED, false);
                    editor.commit();

                    Intent i = new Intent(context, PlayerActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
                if (networkInfo.getDetailedState().toString().contains("DISCONNECTED")) {
                    SharedPreferences settings = context.getSharedPreferences(Constants.DEVICE_STATE, 0);

                    boolean setup = settings.getBoolean(Constants.DEVICE_STATE_SETUP, true);
                    boolean player = settings.getBoolean(Constants.DEVICE_STATE_PLAYER, false);

                    if(setup)
                    {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean(Constants.DEVICE_STATE_SETUP, true);
                        editor.putBoolean(Constants.DEVICE_STATE_DISCONNECTED, true);
                        editor.putBoolean(Constants.DEVICE_STATE_PLAYER, false);
                        editor.commit();
                    }
                    else {
                        if(player)
                        {
                            // Disonnection during player -> go to MainActivity
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean(Constants.DEVICE_STATE_SETUP, false);
                            editor.putBoolean(Constants.DEVICE_STATE_DISCONNECTED, true);
                            editor.putBoolean(Constants.DEVICE_STATE_PLAYER, false);
                            editor.commit();

                            Intent intent2 = new Intent(context, MainActivity.class);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent2);
                        }
                    }
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
