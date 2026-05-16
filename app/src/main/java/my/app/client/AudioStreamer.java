package my.app.client;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import java.io.File;
import java.io.FileOutputStream;

public class AudioStreamer {
    
    private Context context;
    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private Thread recordingThread;
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    public AudioStreamer(Context context) {
        this.context = context;
    }

    public void startRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
        );

        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
            audioRecord.startRecording();
            isRecording = true;

            recordingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    recordAudio();
                }
            });
            recordingThread.start();
        }
    }

    private void recordAudio() {
        try {
            File audioFile = new File(context.getCacheDir(), "audio_" + System.currentTimeMillis() + ".wav");
            FileOutputStream fos = new FileOutputStream(audioFile);

            byte[] audioData = new byte[audioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)];

            while (isRecording) {
                int bytesRead = audioRecord.read(audioData, 0, audioData.length);
                if (bytesRead > 0) {
                    fos.write(audioData, 0, bytesRead);
                }
            }

            fos.close();
        } catch (Exception e) {
            android.util.Log.e("Audio", "Recording error: " + e.getMessage());
        }
    }

    public void stopRecording() {
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }
}
