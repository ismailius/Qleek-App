package android.qleek;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.qleek.MainActivityr.R;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import org.json.JSONException;
import org.json.JSONObject;

import static android.qleek.MainActivity.hideSystemUI;

/**
 * Created by ismailsalhi on 4/28/15.
 */

public class PlayerActivity extends Activity implements
        PlayerNotificationCallback, ConnectionStateCallback {

    private static final String DEBUG_TAG = "BACKEND_HTTP";

    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        View mMainView  = findViewById(R.id.player_layout);
        hideSystemUI(mMainView);

        startSpotifyPlayer();
    }

    /** Get tokens and required information to start streaming */
    private void getSpotifyToken(){
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        String url = Constants.QLEEK_SPOTIFY_TOKEN_URL;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    SharedPreferences settings = PlayerActivity.this.getSharedPreferences(Constants.PLAYER_SETTINGS, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(Constants.SPOTIFY_TOKEN, response.getString("spotify_token"));
                    editor.commit();

                    Config playerConfig = new Config(PlayerActivity.this,  settings.getString(Constants.SPOTIFY_TOKEN, "defaultValue"), Constants.SPOTIFY_CLIENT_ID);
                    mPlayer = Spotify.getPlayer(playerConfig, PlayerActivity.this, new Player.InitializationObserver() {
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

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("SPOTIFY_TOKEN_REQUEST", " Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(jsonObjReq);
    }

    public void startSpotifyPlayer() {
        getSpotifyToken();
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

    @Override
    protected void onStop () {
        super.onStop();
//        if (mRequestQueue != null) {
//            mRequestQueue.cancelAll(TAG);
//        }
    }
}
