package com.example.presentapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout mRelativeLayout;
    private TextView textView;

    private TextView text;
    private Button connect_button;
    private Button get_audio;
    private Button play_video;
    private String selectedImagePath;
    private EditText inputId;
    Intent myFileIntent;
    String newVideoName;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connect_button = findViewById(R.id.connect_button);
        inputId = findViewById(R.id.idInput);
        get_audio = findViewById(R.id.get_audio);
        play_video = findViewById(R.id.play);
        text = findViewById(R.id.text);

        mRelativeLayout = findViewById(R.id.relative_root);
        final Button play = findViewById(R.id.play);
        final ImageButton info = findViewById(R.id.info);

        info.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, InfoPage.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        final float density = getResources().getDisplayMetrics().density;

        final Drawable play_button = getResources().getDrawable(R.drawable.play_icon);

        final int width = Math.round(30*density);
        final int height = Math.round(30*density);


        play_button.setBounds(0,0,width,height);
        play.setCompoundDrawables(null,null,play_button,null);

        get_audio.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                AlertDialog.Builder videoNameDialog = new AlertDialog.Builder(MainActivity.this);
                videoNameDialog.setTitle("Videonuza bir isim veriniz. Ses kaydı ile aynı isimde olmalı");
                final EditText videoName = new EditText(MainActivity.this);
                //videoName.setInputType(InputType.);
                videoNameDialog.setView(videoName);

                videoNameDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newVideoName = videoName.getText().toString();
                        Toast.makeText(MainActivity.this, "Video Name: "+newVideoName+".mp3", Toast.LENGTH_LONG).show();
                        System.out.println("1");
                        OkHttpClient client = new OkHttpClient();
                        String url = "http://192.168.43.36:8000/"+inputId.getText().toString()+"/audio";
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        System.out.println("2");
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        request.setTitle(newVideoName+".mp3");
                        request.setMimeType("audio/wav");
                        System.out.println("3");
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        System.out.println("4");
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, newVideoName+".mp3");
                        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        System.out.println("5");
                        manager.enqueue(request);
                        System.out.println("6");
                    }
                });

                videoNameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                videoNameDialog.show();

            }
        });

        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If(ID is true) { openPDFFile(); }
                //else Pop-up { return "Wrong ID. Please try again"; }
                OkHttpClient client = new OkHttpClient();
                String url = "http://192.168.43.36:8000/"+inputId.getText().toString()+"/id"; //Private... Hostname of my computer
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()) {
                            final String responseId = response.body().string();

                            MainActivity.this.runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void run() {
                                    String Id = inputId.getText().toString();
                                    if(Objects.equals(Id.toString(), responseId.toString())){
                                        openPDFFile(inputId.getText().toString());
                                    }
                                    else{
                                        System.out.println("WROONNGGGG");
                                    }
                                }
                            });
                        }
                    }
                });

            }
        });

        play_video.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                //videoPlayerActivity("/"+Environment.DIRECTORY_DOCUMENTS+"/"+inputId.getText().toString()+".mp4");
                videoPlayerActivity(inputId.getText().toString());
            }
        });

    }

    public void openPDFFile(String id){
        Intent intent = new Intent(this,PDFActivity.class);
        String getrec=id;
        Bundle bundle = new Bundle();
        bundle.putString("id", getrec);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void videoPlayerActivity(String inputID){
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        String videoName = inputID;
        Bundle bundle = new Bundle();
        bundle.putString("videoName", videoName);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
