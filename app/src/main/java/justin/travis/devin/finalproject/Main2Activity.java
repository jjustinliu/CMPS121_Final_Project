package justin.travis.devin.finalproject;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //initialize the ui component
        textView = findViewById(R.id.textView_countdown);

        //get the text from intent
        int hoursTimer = getIntent().getIntExtra("hours", 0);
        int minutesTimer = getIntent().getIntExtra("minutes", 0);
        Toast.makeText(this, hoursTimer + " hours\n" + minutesTimer + " minutes", Toast.LENGTH_SHORT).show();

        //convert the string into integer
        int timeSeconds = (minutesTimer + (hoursTimer * 60)) * 60;
//        int timeSeconds = 1;

        //Initialize a CountDownTimer class with the time data from previous activity
        //which will set the text view with countDown time

        new CountDownTimer(timeSeconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                //set the remaining time in the textView
                String temp = (millisUntilFinished / 1000) / 3600 % 24 + ":" + (millisUntilFinished / 1000) / 60 % 60 + ":" + (millisUntilFinished / 1000) % 60;
                textView.setText(temp);
            }

            public void onFinish() {
                textView.setText("done!");
                MediaPlayer ring = MediaPlayer.create(Main2Activity.this, R.raw.ring);
                ring.start();
                finish();
            }
        }.start();
    }
}
