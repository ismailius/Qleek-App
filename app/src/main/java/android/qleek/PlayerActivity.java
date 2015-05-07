package android.qleek;

import android.app.Activity;
import android.content.Intent;
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

import static android.qleek.MainActivity.hideSystemUI;

/**
 * Created by ismailsalhi on 4/28/15.
 */

public class PlayerActivity extends Activity implements
        PlayerNotificationCallback, ConnectionStateCallback {

    private static final String CLIENT_ID = "a25c3469a92648eab6ff4d41c4658562";
    private static final String REDIRECT_URI = "qleekprotocol://callback";
    private static final int REQUEST_CODE = 1337;

    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        View mMainView  = findViewById(R.id.player_layout);
        hideSystemUI(mMainView);


        /////// TEST
        String tok = "BQCKDDL39i1BP1i99F_ndY3kmylyToFLLGh8Ez9PZYjduudWLKJHpwsA1O21J3IcmSzD5cO7WuutJD3tP1VtGGCFPQXCAZdvNCO4fdFJwFN4uIME9UYTqhsow3x6SEbOXIoLGvC7ij9RuDWCIA";
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

        /////////////////
//        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
//                AuthenticationResponse.Type.TOKEN,
//                REDIRECT_URI);
//        builder.setScopes(new String[]{"user-read-private", "streaming"});
//
//        AuthenticationRequest request = builder.build();
//
//        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
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
