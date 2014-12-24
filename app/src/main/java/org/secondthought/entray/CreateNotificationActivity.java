package org.secondthought.entray;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


public class CreateNotificationActivity extends ActionBarActivity implements View.OnClickListener {
    protected static final int REQUEST_OK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification);

        findViewById(R.id.voice_button).setOnClickListener(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (type != null &&
                (Intent.ACTION_SEND.equals(action) ||
                 "com.google.android.gm.action.AUTO_SEND".equals(action))) {
            if ("text/plain".equals(type)) {
                String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (null != text) {
                    createNotification(text);
                }
            }
            this.finish();
        }
    }

    /**
     * Create a notification with the user's text.
     * @param view The containing view.
     */
    public void createNotification(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        createNotification(editText.getText().toString());
        editText.setText("");
    }

    public void createNotification(String text) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_TEXT, text);
        sendBroadcast(notificationIntent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        try {
            startActivityForResult(intent, REQUEST_OK);
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OK && resultCode == RESULT_OK) {
            ArrayList<String> utterances = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            createNotification(utterances.get(0));
        }
    }
}
