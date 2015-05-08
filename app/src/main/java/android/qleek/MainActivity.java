package android.qleek;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.qleek.MainActivityr.R;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
    public Context context;
    public static WifiManager mWifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_main);

        View mMainView  = findViewById(R.id.main_layout);

        hideSystemUI(mMainView);

        mWifiManager = (WifiManager) getSystemService(this.WIFI_SERVICE);
        mWifiManager.setWifiEnabled(true);
        Handler handler = new Handler();

        handler.postDelayed(qleekSetup, 0);
    }

    public Runnable qleekSetup = new Runnable() {
        public void run() {

//            resetAppSettings();

            SharedPreferences settings = getSharedPreferences(Constants.DEVICE_STATE, 0);
            boolean setup = settings.getBoolean(Constants.DEVICE_STATE_SETUP, true);
            boolean player = settings.getBoolean(Constants.DEVICE_STATE_PLAYER, false);
            boolean disconnected = settings.getBoolean(Constants.DEVICE_STATE_DISCONNECTED, false);

            if(setup) {
                // TODO Force Setup Mode
                // SETUP_STATE
                Log.d("QLEEK", "Entering SETUP_STATE");
                startServer();
            }
            else {
                if (disconnected) {
                    // No more connectivity : DISCONNECTED_STATE
                    handleDisconnection();
                }
                else {
                    if (isNetworkAvailable(context)) {
                        // Entering PLAYER_STATE
                        Log.d("QLEEK", "Entering PLAYER_STATE");
                        startPlayer();
                    }
                    else {
                        handleDisconnection();
                    }
                }
            }
        }
    };

    public void handleDisconnection () {
        SharedPreferences settings = getSharedPreferences(Constants.DEVICE_STATE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Constants.DEVICE_STATE_DISCONNECTED, true);
        editor.putBoolean(Constants.DEVICE_STATE_PLAYER, false);

        TextView txtv  = (TextView)findViewById(R.id.textView);
        txtv.setText("Internet Connection Lost");

        while (!isNetworkAvailable(this)) {

        }

        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    public void startServer() {
        Intent intent = new Intent(this, ApWebServerActivity.class);
        startActivity(intent);
    }

    public void startPlayer() {
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    public void resetAppSettings() {
        SharedPreferences settings = getSharedPreferences(Constants.DEVICE_STATE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Constants.DEVICE_STATE_SETUP, true);
        editor.commit();
    }

    @Override
    protected void onResume() {

        Log.i("MAINACTIVITY", "Resumed !!");
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    // This snippet hides the system bars.
    public static void hideSystemUI(View mDecorView) {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
    // except for the ones that make the content appear under the system bars.
    public static void showSystemUI(View mDecorView) {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
