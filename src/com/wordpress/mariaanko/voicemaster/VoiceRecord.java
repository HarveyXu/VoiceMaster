package com.wordpress.mariaanko.voicemaster;

import android.widget.LinearLayout;
import android.widget.Toast;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.media.MediaRecorder;
import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;


public class VoiceRecord extends Activity
{
    private static final String LOG_TAG = "AudioRecordTest";
    private static String m_fileName = null;

    private RecordButton mRecordButton = null;
    private MediaRecorder m_Recorder = null;

    private PlayButton   mPlayButton = null;
    private MediaPlayer   mPlayer = null;

    private LinearLayout m_layout = null;
    private Translator m_translator = new Translator();
    private Thread m_thread = null;
    
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(m_fileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private Boolean startRecording() {
    	m_fileName = getNewFileName();
    	if (m_fileName == null){
    		return false;
    	}
        m_Recorder = new MediaRecorder();
        m_Recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        m_Recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        m_Recorder.setOutputFile(m_fileName);
        m_Recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            m_Recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
            return false;
        }

        m_Recorder.start();
        return false;
    }

    private void stopRecording() {
        m_Recorder.stop();
        m_Recorder.release();
        m_Recorder = null;
        
        if (m_thread == null){
			m_thread = new Thread(runnable);
			m_thread.start();
		} else {
			Toast.makeText(getApplication(),
					getApplication().getString(R.string.thread_started),
					Toast.LENGTH_LONG).show();
		}
    }
    
    Runnable runnable = new Runnable() {
    	 public void run() {
    		 String text;
			try {
				text = m_translator.GetTextOfSpeech(m_fileName, "en-US");
				text += "";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	 }
    };

    class RecordButton extends Button {
        boolean m_startRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(m_startRecording);
                if (m_startRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                m_startRecording = !m_startRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }
    
    private String getNewFileName(){
    	String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    	File vmDir = new File(sdPath + "/VoiceMaster");
    	if (!vmDir.exists()){
    		Boolean created = vmDir.mkdir();
    		if (!created){
    			return null;
    		}
    	}
    	
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
    	Date curDate = new Date(System.currentTimeMillis());
    	String strPath = formatter.format(curDate);
    	return vmDir.getPath() + "/" + strPath + ".3gp";
    }
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        m_layout = new LinearLayout(this);
        //setContentView(R.layout.voice_record_view);
        //m_layout = (LinearLayout) findViewById(R.layout.voice_record_view);
        
        mRecordButton = new RecordButton(this);
        m_layout.addView(mRecordButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        
        mPlayButton = new PlayButton(this);
        m_layout.addView(mPlayButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        
        setContentView(m_layout);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (m_Recorder != null) {
            m_Recorder.release();
            m_Recorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
