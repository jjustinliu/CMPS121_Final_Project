package justin.travis.devin.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {
    static int minutesSelected;
    static int hoursSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//------[Initialize Variables]-----------------------------------------------------------------

        List<Integer> hours = new ArrayList<>();
        List<Integer> minutes = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            hours.add(i);
        }
        for (int i = 0; i < 60; i++) {
            minutes.add(i);
        }
//------[Spotify Button]-----------------------------------------------------------------------
        Button spotifyButton = findViewById(R.id.button_spotify);
        spotifyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.spotify.music");
                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }
            }
        });

//-----[hours spinner stuff]-------------------------------------------------------------------

        // Spinner element
        Spinner hours_spinner_element = findViewById(R.id.spinner_hours);

        // Spinner click listener
        hours_spinner_element.setOnItemSelectedListener(this);

        // Creating adapter for spinner
        ArrayAdapter<Integer> hoursdataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);

        // Drop down layout style - list view with radio button
        hoursdataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        hours_spinner_element.setAdapter(hoursdataAdapter);

//-----[Minutes spinner stuff]-------------------------------------------------------------------

        // Spinner element
        Spinner minutes_spinner_element = findViewById(R.id.spinner_minutes);

        // Spinner click listener
        minutes_spinner_element.setOnItemSelectedListener(this);

        // Creating adapter for spinner
        ArrayAdapter<Integer> minutesdataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, minutes);

        // Drop down layout style - list view with radio button
        minutesdataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        minutes_spinner_element.setAdapter(minutesdataAdapter);

//-----[Start Button]---------------------------------------------------------------------------

        Button startButton = findViewById(R.id.button_start);
        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CountdownActivity.class);
                intent.putExtra("hours", hoursSelected);
                intent.putExtra("minutes", minutesSelected);
                startActivity(intent);

            }
        });

//----------------------------------------------------------------------------------------------

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner hours = (Spinner) parent;
        Spinner minutes = (Spinner) parent;

        if (hours.getId() == R.id.spinner_hours) {
            hoursSelected = Integer.parseInt(parent.getSelectedItem().toString());
            Toast.makeText(this, "Selected: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
        }
        if (minutes.getId() == R.id.spinner_minutes) {
            minutesSelected = Integer.parseInt(parent.getSelectedItem().toString());
            Toast.makeText(this, "Selected: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
        }

    }

    public void onNothingSelected(AdapterView<?> arg0) {

    }
}