package ch.limenet.android_speech.android_speech;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Locale;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements OnInitListener  {


    public static final String EXTRA_TEXT = "text";

    private String text;
    private TextView textView;
    private TextToSpeech textToSpeech;
    private int DATA_CHECKING = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent checkData = new Intent();
        checkData.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkData, DATA_CHECKING);

        textView = (TextView) findViewById(android.R.id.text1);

        text = getIntent().getStringExtra(EXTRA_TEXT);
        textView.setText(text);

    }

    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.US);
            speak();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void speak() {
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //do they have the data
        if (requestCode == DATA_CHECKING) {
            //yep - go ahead and instantiate
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
                textToSpeech = new TextToSpeech(this, this);
                //no data, prompt to install it
            else {
                Intent promptInstall = new Intent();
                promptInstall.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(promptInstall);
            }
        }
    }
}
