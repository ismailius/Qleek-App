package android.qleek;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.qleek.MainActivityr.R;
import android.util.Log;
import android.view.View;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import static android.qleek.MainActivity.hideSystemUI;

/**
 * Created by ismailsalhi on 4/28/15.
 */

public class PlayerActivity extends Activity implements
        PlayerNotificationCallback, ConnectionStateCallback {

    private static final String CLIENT_ID = "a25c3469a92648eab6ff4d41c4658562";
    private static final String REDIRECT_URI = "qleekprotocol://callback";
    private static final int REQUEST_CODE = 1337;

    private static final String DEBUG_TAG = "BACKEND_HTTP";

    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        View mMainView  = findViewById(R.id.player_layout);
        hideSystemUI(mMainView);

        SharedPreferences settings = this.getSharedPreferences(Constants.PLAYER_SETTINGS, 0);
        SharedPreferences.Editor editor = settings.edit();
//        editor.putString(Constants.PLAYER_SETTINGS, );

        getPlaybackParameters();

        /////// TEST
        String tok = "BQB6hN3OFCiKVruixmrOz22X9IBe2AdDqkNyVPXMDXbr8-GA8jd_jVC5iDp40weY3VacikMDhaecj3gSxgEFFNJ3_Ehc5RGXvro1k20ySUQtCK_j8evhEU6TmE0xMoS8I9Ls93vBlN5_C2h-k_URkI1mmDTHjuzb7A";
        Config playerConfig = new Config(this, tok, "930e1f7231cc42849d860780503d7e45");
        mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player player) {
                mPlayer.addConnectionStateCallback(PlayerActivity.this);
                mPlayer.addPlayerNotificationCallback(PlayerActivity.this);
                mPlayer.play("spotify:user:chamundi:playlist:7LiHLHMSqnPFa3hjZ1YH6E");
                mPlayer.setShuffle(true);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("PlayerActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    /** Get tokens and required information to start streaming */
    private void getPlaybackParameters( ){

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new HttpGetDeviceParams().execute(Constants.QLEEK_BACKEND_API);
        } else {
            // display error
        }
    }

    private class HttpGetDeviceParams extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            JSONArray ssidJsonArray = new JSONArray(Arrays.asList(result));
            Log.i(DEBUG_TAG, ""+ssidJsonArray.toString());
        }
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 1000 characters of the retrieved
        // data content.
        int len = 1000;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

//        // Check if result comes from the correct activity
//        if (requestCode == REQUEST_CODE) {
//            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
//            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
//
//                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
//                mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
//                    @Override
//                    public void onInitialized(Player player) {
//                        Log.i("SPOTIFY", "I'm gonna play something");
//                        mPlayer.addConnectionStateCallback(PlayerActivity.this);
//                        mPlayer.addPlayerNotificationCallback(PlayerActivity.this);
//                        mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        Log.e("PlayerActivity", "Could not initialize player: " + throwable.getMessage());
//                    }
//                });
//            }
//        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("PlayerActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("PlayerActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("PlayerActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("PlayerActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("PlayerActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("PlayerActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("PlayerActivity", "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
