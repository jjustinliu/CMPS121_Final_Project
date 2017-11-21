package justin.travis.devin.finalproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts;
import android.telephony.*;
import android.view.View;
import android.widget.*;

public class AutoSMS extends Activity {

    private EditText SMSText = null;
    private Button startButton = null;
    private TextView instruction = null;

    private ContentResolver content = null;
    private ArrayList<String> nameList = null;
    private ArrayList<String> numberList = null;
    private TelephonyManager telephonyManager = null;
    private SmsManager smsManager = null;

    private String incomingNumber = null;
    private boolean isMonitoring = false;

    public void serviceControl(boolean isMonitoring){
        if(!isMonitoring){
            startService();
        }else{
            stopService();
        }
        this.isMonitoring = !this.isMonitoring;
    }

    public void startService(){

        content = getContentResolver();
        Cursor cursor = content.query(Contacts.People.CONTENT_URI, null, null, null, null);
        nameList = new ArrayList<String>(cursor.getCount());
        numberList = new ArrayList<String>(cursor.getCount());

        for(int i = 0; i< cursor.getCount() ; i++){
            cursor.moveToPosition(i);
            nameList.add(cursor.getString(cursor.getColumnIndex(Contacts.People.NAME)));
            numberList.add(cursor.getString(cursor.getColumnIndex(Contacts.People.NUMBER)));
        }


        PhoneStateListener phoneListener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state,String incoming){
                if(isMonitoring && state == TelephonyManager.CALL_STATE_RINGING){
                    incomingNumber = incoming;
                    sendSMS();
                }
            }
        };

        telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        smsManager = SmsManager.getDefault();
        instruction.setText("Auto Reply Started");
    }

    public void stopService(){
        instruction.setText("Auto Reply Stopped");
    }

    public void sendSMS(){
        /* SmsManager smsManager = SmsManager.getDefault();
         * String smsText = "Text Content";
         * String number = "Phone Number";
         * smsManager.sendTextMessage(incomingNumber, null, smsText, null, null);
         * */
        if(numberList.contains(incomingNumber));{
            String smsText = SMSText.getText().toString();
            smsManager.sendTextMessage(incomingNumber, null, smsText, null, null);
            instruction.setText("Text Sent");
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        this.SMSText = (EditText)findViewById(R.id.SMSText);
        this.startButton = (Button)findViewById(R.id.startButton);
        Button closeButton = (Button) findViewById(R.id.closeButton);
        this.instruction = (TextView)findViewById(R.id.instruction);


        startButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!isMonitoring){
                    startButton.setText("Stop Auto Reply");
                }else{
                    startButton.setText("Start Auto Reply");
                }
                serviceControl(isMonitoring);
            }
        });

    }
}
