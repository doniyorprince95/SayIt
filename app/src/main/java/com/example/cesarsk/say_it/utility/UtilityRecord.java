package com.example.cesarsk.say_it.utility;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.cesarsk.say_it.ui.PlayActivity.RequestPermissionCode;
import static com.example.cesarsk.say_it.ui.PlayActivity.selected_word;

public class UtilityRecord{

    static final String AUDIO_RECORDER_FOLDER = "Say it";

    private static MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Log.i("Say it!", "Error: " + what + ", " + extra);
        }
    };
    private static MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Log.i("Say it!", "Warning: " + what + ", " + extra);
        }
    };

    //CONTROLLARE SE FUNZIONA
    public static void deleteRecording(Context context, String word) {
        //delete a recording
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER;
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().equals(word + ".aac")) {
                    files[i].delete();
                }
            }
        }
    }

    public static ArrayList<String> loadRecordings() {
        //load all recordings, needs to be used in order to build the HistoryFragment
        ArrayList<String> recordings = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER;
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                if (!files[i].getName().equals(".nomedia"))
                    recordings.add(files[i].getName().substring(0, files[i].getName().lastIndexOf(".")));
            }
        }
        return recordings;
    }

    public static boolean checkRecordingFile(String word) {
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER;
        Log.i("DEBUG FILE: ", path + "/" + word + ".aac");
        File f = new File(path + "/" + word + ".aac");
        if (f.exists() && !f.isDirectory()) {
            Log.i("DEBUG FILE:", "FILE ESISTE! RITORNO TRUE");
            return true;
        } else return false;
    }

    //FUNZIONI PER RICHIESTA PERMESSI
    public static boolean checkRecordAudioPermissions(Context context) {
        int result = ContextCompat.checkSelfPermission(context,
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context,
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestRecordAudioPermissions(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    //FUNZIONI DI REGISTRAZIONE
    public static String getRecordingFilename(String file_exts[], int currentFormat) {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
            File nomedia = new File(Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/.nomedia");
            try {
                nomedia.createNewFile();
            } catch (IOException e) {
                Log.i("LOG:", ".nomedia not created");
            }
        }

        return (file.getAbsolutePath() + "/" + selected_word + file_exts[currentFormat]);
    }

    public static boolean startRecording(MediaRecorder recorder, int output_formats[], int currentFormat, String file_exts[]) {
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(output_formats[currentFormat]);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            recorder.setAudioEncodingBitRate(16);
            recorder.setAudioSamplingRate(44100);
            recorder.setOutputFile(getRecordingFilename(file_exts, currentFormat));
            recorder.setOnErrorListener(errorListener);
            recorder.setOnInfoListener(infoListener);
            recorder.prepare();
            recorder.start();
            return true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean stopRecording(MediaRecorder recorder, String word) {
        if (recorder != null) {
            try{
                recorder.stop();
            } catch(RuntimeException stopException){
                //deleting file here
                stopException.printStackTrace();
                String path = Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER;
                File file = new File(path + "/" + word + ".aac");
                file.delete();
                recorder.reset();
                return false;
            }
            recorder.reset();
            return true;
        }
        return false;
    }

    public static int returnRecordingDuration(MediaPlayer mediaPlayer) {
        try {
            mediaPlayer.stop();
            Log.i("Say it!", "Playing recording: " + Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/" + selected_word + ".aac");
            mediaPlayer.reset(); //Before a setDataSource call, you need to reset MP obj.
            mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/" + selected_word + ".aac");
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaPlayer.getDuration();
    }

    public static void playRecording(MediaPlayer mediaPlayer) {
        try {
            //TODO CHECK IF RECORDING ALREADY EXISTS. IF DOES NOT, DO NOT PLAY.
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                Log.i("Say it!", "Playing recording: " + Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/" + selected_word + ".aac");
                mediaPlayer.reset(); //Before a setDataSource call, you need to reset MP obj.
                mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/" + selected_word + ".aac");
                mediaPlayer.prepare();
                mediaPlayer.start();
            } else {
                Log.i("Say it!", "Playing recording: " + Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/" + selected_word + ".aac");
                mediaPlayer.reset(); //Before a setDataSource call, you need to reset MP obj.
                mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/" + selected_word + ".aac");
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopPlaying(MediaPlayer mediaPlayer) {
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}