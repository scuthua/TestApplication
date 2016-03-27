package com.flower.test.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flower.test.model.Lrc;
import com.flower.test.LrcView;
import com.flower.test.MyActivity;
import com.flower.test.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: 2016/3/11  在每次打开app时，即使下方已经显示了歌手名字和歌曲名，但是点击最下角的播放按钮不会放歌曲，这时应该 mediaPlayer没有prepare（）
public class MainActivity extends MyActivity implements View.OnClickListener {

    private TextView realTime, totalTime, songName;
    private LrcView lyrics;
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    private ImageButton start;
    private Button cycle;
    private ProgressBar progressBar;
    private Lrc mLrc = new Lrc();
    private Handler handler = new Handler();
    private List<String> songs;
    private int position;
    int index = 0;
    private boolean isLoop = true;


    static {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initOther();
        isLoop = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isLoop", true);
        if (isLoop) {
            cycle.setText("单曲循环");
        } else {
            cycle.setText("列表循环");
        }
        /**
         * 监听mediaPlayer是否播放完成。控制他接下来的操作在这里写
         * 注意，这里当已经设置了循环模式就不会调用，所以不用在这里做isLooping的判断
         */
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.reset();//注意在reset方法后面重新设置是否循环，因为reset后原来的设置就已经没用了
                mediaPlayer.setLooping(isLoop);
                try {
                    position = (position + 1) % songs.size();
                    mediaPlayer.setDataSource(PreferenceManager.getDefaultSharedPreferences
                            (MainActivity.this)
                            .getString(songs.get(position), ""));
                    mediaPlayer.prepare();
                    prepareLayout();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        File file = new File(Uri.decode(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(songs.get(position), "")));
        try {
            mediaPlayer.reset();
            mediaPlayer.setLooping(isLoop);
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            start.setImageResource(R.drawable.pause);
            handler.post(mRunnable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        prepareLayout();

    }

    private void prepareLayout() {
        setProgressMax();
        readFromLrcDoc();
        setSongName();
    }

    private void setSongName() {
        String filePath = songs.get(position);
        String[] array = filePath.split("\\.mp3")[0].split("/");
        String[] names = array[array.length - 1].split("-");
        if (names.length == 2) {
            songName.setText(names[1]);
        } else {
            songName.setText("未知歌曲");
        }

    }

    private void setProgressMax() {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        progressBar.setMax(mediaPlayer.getDuration());
        Date time = new Date(mediaPlayer.getDuration());
        totalTime.setText(format.format(time));
    }

    private void initView() {
        lyrics = (LrcView) findViewById(R.id.lyrics);
        realTime = (TextView) findViewById(R.id.real_time);
        totalTime = (TextView) findViewById(R.id.total_time);
        songName = (TextView) findViewById(R.id.song_name);
        Button songList = (Button) findViewById(R.id.menu);
        cycle = (Button) findViewById(R.id.cycle);
        cycle.setOnClickListener(this);
        songList.setOnClickListener(this);
        start = (ImageButton) findViewById(R.id.start_or_pause_or_stop);
        start.setOnClickListener(this);
        ImageButton last = (ImageButton) findViewById(R.id.last_one);
        last.setOnClickListener(this);
        ImageButton next = (ImageButton) findViewById(R.id.next_one);
        next.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int down = (int) ((event.getX() / v.getMeasuredWidth()) * progressBar.getMax());
                progressBar.setProgress(down);
                mediaPlayer.seekTo(down);
                return true;
            }
        });
        progressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int down = (int) ((event.getX() / v.getMeasuredWidth()) * progressBar.getMax());
                progressBar.setProgress(down);
                mediaPlayer.seekTo(down);
                return true;
            }
        });


    }

    private void initOther() {
        mLrc.setLrc(new ArrayList<String>());
        mLrc.setLrcTime(new ArrayList<Integer>());
        position = PreferenceManager.getDefaultSharedPreferences(this).getInt
                ("selected_position", 0);
        songs = getDataFromShared();

    }

    private List<String> getDataFromShared() {
        String total = PreferenceManager.getDefaultSharedPreferences(this).getString
                ("total", null);
        String[] strings = total.split("\\n");
        List<String> songs = new ArrayList<>();
        for (String str : strings) {
            songs.add(str);
        }
        songs.remove(strings.length - 1);
        return songs;
    }

    private void readFromLrcDoc() {
        FileInputStream fileIs = null;
        BufferedReader reader = null;
        mLrc.getLrcTime().clear();
        mLrc.getLrc().clear();
        try {
            String lrcPAth = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(songs.get(position), "").split("\\.mp3")[0] + ".lrc";
            fileIs = new FileInputStream(new File(Uri.decode(lrcPAth)));
            reader = new BufferedReader(new InputStreamReader(fileIs));
            String str;
            StringBuilder sb = new StringBuilder();
            while ((str = reader.readLine()) != null) {
                //sb.append(str);
                    Pattern pattern=Pattern.compile("\\[(\\d{2}:\\d{2}\\.\\d{2})\\]");
                    Matcher matcher = pattern.matcher(str);
                    while (matcher.find()) {
                        String time = matcher.group();
                        mLrc.getLrcTime().add(parseLrcTime(time));
                        mLrc.getLrc().add(str.substring(time.length()));
                    }
            }
           /* String[] arr=sb.toString().split("\n");
            for (int i = 0; i < arr.length; i++) {
                Pattern pattern=Pattern.compile("\\[(\\d{2}:\\d{2}\\.\\d{2})\\]");
                Matcher matcher = pattern.matcher(arr[i]);
                while (matcher.find()) {
                    String time = matcher.group();
                    mLrc.getLrcTime().add(parseLrcTime(time));
                    mLrc.getLrc().add(arr[i].substring(time.length()));
                }
            }*/
            lyrics.setCurrentLrc(mLrc);
            lyrics.invalidate();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileIs != null) {
                    fileIs.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private Integer parseLrcTime(String s) {
        String s1=s.substring(1,s.length()-1);
        String s2 = s1.replace(":", ".");
        String[] array = s2.split("\\.");
        int minute = Integer.parseInt(array[0]);
        int second = Integer.parseInt(array[1]);
        int millisecond = Integer.parseInt(array[2]) * 10;
        return minute * 1000 * 60 + second * 1000 + millisecond;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu:
                this.onBackPressed();
                break;
            case R.id.start_or_pause_or_stop:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    start.setImageResource(R.drawable.start);
                    lyrics.setIndex(index, mediaPlayer.getCurrentPosition());
                } else {
                    mediaPlayer.start();
                    start.setImageResource(R.drawable.pause);
                    handler.post(mRunnable);
                }
                break;
            case R.id.cycle:
                if (isLoop) {
                    isLoop = false;
                    cycle.setText("列表循环");
                } else {
                    isLoop = true;
                    cycle.setText("单曲循环");
                }
                mediaPlayer.setLooping(isLoop);
                break;
            case R.id.last_one:
                setSongByPosition(position - 1 + songs.size());
                break;
            case R.id.next_one:
                setSongByPosition(position + 1);
                break;

        }
    }

    //通过list的位置设置当前歌曲，用于播放上一首和下一首
    private void setSongByPosition(int i) {
        mediaPlayer.reset();
        mediaPlayer.setLooping(isLoop);
        try {
            position = (i) % songs.size();
            mediaPlayer.setDataSource(PreferenceManager.getDefaultSharedPreferences
                    (MainActivity.this)
                    .getString(songs.get(position), ""));
            mediaPlayer.prepare();
            prepareLayout();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            SimpleDateFormat format = new SimpleDateFormat("mm:ss");
            Date time = new Date(mediaPlayer.getCurrentPosition());
            realTime.setText(format.format(time));
            lyrics.setIndex(lrcIndex(), mediaPlayer.getCurrentPosition());
            progressBar.setProgress(mediaPlayer.getCurrentPosition());
            lyrics.invalidate();
            handler.postDelayed(mRunnable, 100);
        }
    };

    private int lrcIndex() {
        int currentTime = 0, totalTime = 0;

        if (mediaPlayer.isPlaying()) {
            currentTime = mediaPlayer.getCurrentPosition();
            totalTime = mediaPlayer.getDuration();
        }
        if (currentTime < totalTime) {

            for (int i = 0; i < mLrc.getLrcTime().size(); i++) {
                if (i < mLrc.getLrcTime().size() - 1) {
                    if (currentTime < mLrc.getLrcTime().get(i) && i == 0) {
                        index = i;
                    }
                    if (currentTime < mLrc.getLrcTime().get(i + 1) && currentTime > mLrc
                            .getLrcTime().get(i)) {
                        index = i;
                    }
                }
                if (i == mLrc.getLrcTime().size() - 1
                        && currentTime > mLrc.getLrcTime().get(i)) {
                    index = i;
                }
            }
        }
        return index;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(mRunnable);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
                .edit();
        editor.putInt("selected_position", position);
        editor.putBoolean("isLoop", isLoop);
        editor.commit();
        Intent intent = new Intent(this, SongListActivity.class);
        startActivity(intent);
    }

}
