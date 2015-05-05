package android.qleek;

import android.app.Activity;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.qleek.MainActivityr.R;
import android.view.View;

import static android.qleek.MainActivity.hideSystemUI;

/**
 * Created by ismailsalhi on 4/28/15.
 */
public class PlayerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        View mMainView  = findViewById(R.id.player_layout);
        hideSystemUI(mMainView);

        ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
        tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
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
