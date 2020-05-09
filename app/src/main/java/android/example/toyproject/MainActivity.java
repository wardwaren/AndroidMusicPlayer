package android.example.toyproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 0;
    private MediaPlayer audioPlayer;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar progressBar;
    MyAdapter adapter;
    int progressBarStatus = 0;
    Handler progressBarHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
        } else {
            String path = Environment.getExternalStorageDirectory().toString() + "/Music";
            Log.d("Files", "Path: " + path);
            File directory = new File(path);
            File[] files = directory.listFiles();
            try{
                Log.d("Files", "Size: "+ files.length);
            }
            catch (Exception e){
                Log.d("Files","Exception " + e);
            }
            String[] file_names = new String[files.length];
            for (int i = 0; i < files.length; i++)
            {
                file_names[i] = files[i].getName();
                Log.d("Files", "FileName:" + files[i].getName());
            }
            //layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
            recyclerView = findViewById(R.id.my_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView

            // use a linear layout manager
            List<String> list = (List<String>) Arrays.asList(file_names);
            ArrayList<String> names = new ArrayList<String>(list);
            Log.d("files","size " + names.size() );
            adapter = new MyAdapter(this, names);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
        }



    }

    @Override
    public void onItemClick(View view, int position) throws IOException {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        recyclerView.setAdapter(adapter);
        audioPlayer = new MediaPlayer();
        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        String path =  Environment.getExternalStorageDirectory().toString() + "/Music/" + adapter.getItem(position);
        audioPlayer.setDataSource(getApplicationContext(), Uri.parse(path));
        audioPlayer.prepare();
        audioPlayer.start();
        audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(MainActivity.this, "The Song is Over", Toast.LENGTH_SHORT).show();
                audioPlayer.release();
            }
        });
        //progressBar = findViewById(R.id.play_audio_progressbar);
        Drawable draw= getResources().getDrawable(R.drawable.custom_progressbar);
        progressBar = (ProgressBar) findViewById(R.id.play_audio_progressbar);
        //progressBar = (ProgressBar) findViewById(R.id.play_audio_progressbar);
        progressBar.setProgressDrawable(draw);
        progressBar.setProgress(0);
        progressBar.setMax(audioPlayer.getDuration() / 1000);
        progressBarStatus = 0;
        //progressBar.draw(view);
        Button playButton = (Button) findViewById(R.id.start);
        new Thread(new Runnable() {
            public void run() {
                while(progressBarStatus < audioPlayer.getDuration()/1000){
                    progressBarStatus = audioPlayer.getCurrentPosition()/1000;
                    Log.d("positions","current: " + progressBarStatus);
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                    if (progressBarStatus >= audioPlayer.getDuration()/1000) {
                        // close the progress bar dialo
                    }
                }

            }
        }).start();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioPlayer.start();
                audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Toast.makeText(MainActivity.this, "The Song is Over", Toast.LENGTH_SHORT).show();
                        audioPlayer.release();
                    }
                });
            }
        });

        Button pauseButton = (Button) findViewById(R.id.stop);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioPlayer.pause();
            }
        });
    }


}
