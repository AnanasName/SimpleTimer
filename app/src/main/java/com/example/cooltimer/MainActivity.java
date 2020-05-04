package com.example.cooltimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {
    boolean isStart = false;
    Button startOrStopButton;
    TextView timerTextView;
    int seconds;
    int minutes;
    private Integer defaultInterval;
    SeekBar seekBar;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = (SeekBar)findViewById(R.id.main_seek_bar);
        startOrStopButton = findViewById(R.id.main_start_or_stop_button);
        timerTextView = findViewById(R.id.main_timer_text_view);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(900);
        setIntervalFromSharedPreferences(sharedPreferences);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seconds = progress;
        minutes = seconds / 60;
        seconds = seconds % 60;
        timerTextView.setText(minutes + ":" + seconds);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void startTimer(View view) {

        CountDownTimer countDownTimer = new CountDownTimer(seconds*1000 + minutes*60*1000 , 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                seconds--;
                if (seconds < 0){
                    minutes--;
                    seconds = 59;
                }
                timerTextView.setText(minutes + ":" + seconds);
            }

            @Override
            public void onFinish() {
                if (sharedPreferences.getBoolean("enable_sound", true)) {
                    String melodyname = sharedPreferences.getString("timer_melody", "bell_sound");
                    if (melodyname.equals("bell_sound")) {
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bell_sound);
                        mediaPlayer.start();
                    }else if (melodyname.equals("alarm")){
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
                        mediaPlayer.start();
                    }else if (melodyname.equals("alarm_siren_sound")){
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm_siren_sound);
                        mediaPlayer.start();
                    }
                }

            }
        };
        if (!isStart){
            countDownTimer.start();
            isStart = true;
            startOrStopButton.setText("Stop");}
        else{
            countDownTimer.cancel();
            isStart = false;
            startOrStopButton.setText("Start");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.timer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.actions_settings){
            Intent openSettings = new Intent(this, SettingsActivity.class);
            startActivity(openSettings);
            return true;
        }else if (id == R.id.actions_contacts){
            Intent openContacts = new Intent(this, ContactsActivity.class);
            startActivity(openContacts);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setIntervalFromSharedPreferences(SharedPreferences sharedPreferences){

        defaultInterval = Integer.parseInt(sharedPreferences.getString("default_interval", "0"));


        timerTextView.setText((defaultInterval / 60) + ":" + defaultInterval % 60);
        seekBar.setProgress(defaultInterval);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("default_interval")){
            setIntervalFromSharedPreferences(sharedPreferences);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
