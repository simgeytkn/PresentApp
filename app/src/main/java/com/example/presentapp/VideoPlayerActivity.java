package com.example.presentapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.rtp.AudioStream;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class VideoPlayerActivity extends AppCompatActivity {

    VideoView myVideoView;
    MediaController mController;
    MediaPlayer mediaPlayer;

    FFmpeg ffmpeg;

    String videoPath;

    String videoName;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        myVideoView = findViewById(R.id.videoView);

        Bundle bundle = getIntent().getExtras();
        videoName = bundle.getString("videoName");
        System.out.println("VIDEO NAME = "+videoName);

        if(ffmpeg == null){
            ffmpeg = FFmpeg.getInstance(this);
            try {
                ffmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                    @Override
                    public void onFailure() {
                        System.out.println("NOOOOOOOOOOOOOOOOOO");
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess() {
                        System.out.println("VİİİİİİİİİİKKK: "+Environment.getExternalStorageDirectory().getPath());
                        /*String cmd = "-i@" + Environment.getExternalStorageDirectory().getPath() +"/"+Environment.DIRECTORY_DOCUMENTS+"/deneme12.mp4@"
                                    +Environment.getExternalStorageDirectory().getPath() +"/"+Environment.DIRECTORY_DOCUMENTS+"/deneme1245.mp4";
                        */
                        //ffmpeg -y -i merged.mp4 -i combined.mp3 -c:a aac -map 0:v -map 1:a output.mp4
                        String cmd = "-y@-i@"+Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_DOCUMENTS + "/" + videoName+".mp4" + "@-i@"
                                +Environment.getExternalStorageDirectory().getPath()+"/"+Environment.DIRECTORY_DOWNLOADS + "/"+ videoName + ".mp3@"+"-c:a@aac@-map@0:v@-map@1:a@"
                                + Environment.getExternalStorageDirectory().getPath()+"/" + Environment.DIRECTORY_DOCUMENTS + "/" +videoName+"PresentApp.mp4";

                        String[] command = cmd.split("@");

                        if (command.length != 0) {
                            execFFmpegBinary(command);


                        } else {
                            // Toast.makeText(Home.this, getString(R.string.empty_command_toast), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {

                    }
                });
            } catch (FFmpegNotSupportedException e) {
                e.printStackTrace();
            }
        }
    /*    audioName = videoName;
        System.out.println("GetPath!!!!!!!!!!!!!!!!!!!!"+videoName);
        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(myVideoView);
        Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/"+Environment.DIRECTORY_DOCUMENTS+"/"+videoName+".mp4");
        myVideoView.setMediaController(mediaController);
        myVideoView.setVideoURI(uri);
        myVideoView.requestFocus();


        new Thread() {
            public void run() {
                myVideoView.start();
                System.out.println("DENEME");
            }
        }.start();



        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void run() {
                try {
                    play();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
        */
    }

    private void execFFmpegBinary(final String[] command){
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    System.out.println("FAILED with output : "+s);
                }

                @Override
                public void onSuccess(String s) {
                    System.out.println("SUCCESS with output : "+s);
                }

                @Override
                public void onProgress(String s) {
                }

                @Override
                public void onStart() {
                    System.out.println("BASLADIIIIIIIIIIIII");
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onFinish() {
                    System.out.println("VE BITTIIIIIIIIII");
                    videoPath = Environment.getExternalStorageDirectory().getPath()+"/" + Environment.DIRECTORY_DOCUMENTS + "/" + videoName +"PresentApp.mp4";
                    Uri uri = Uri.parse(videoPath);
                    myVideoView.setVideoURI(uri);

                    MediaController mediaController = new MediaController(VideoPlayerActivity.this);
                    myVideoView.setMediaController(mediaController);
                    mediaController.setAnchorView(myVideoView);

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void play() throws IOException {
        /*if(myVideoView != null){
            String myUri = Environment.getExternalStorageDirectory().getPath()+"/"+Environment.DIRECTORY_DOWNLOADS+"/"+audioName+".mp3";
            System.out.println("DANGER!!!!!!!!!!:" + myUri);

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(myUri));
            mediaPlayer.prepare();
            mediaPlayer.start();
        }*/


    }

    private void stopPlayer() {
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("Geri yapıldı");
        stopPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

}
