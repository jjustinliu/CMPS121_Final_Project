package justin.travis.devin.finalproject;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Silent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silent);
        AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audio.setRingerMode(0);

    }
}
