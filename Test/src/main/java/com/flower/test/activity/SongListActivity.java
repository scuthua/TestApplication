package com.flower.test.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flower.test.ActivityControl;
import com.flower.test.MyActivity;
import com.flower.test.MyAdapter;
import com.flower.test.MylistView;
import com.flower.test.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongListActivity extends MyActivity implements AdapterView.OnItemClickListener, View
        .OnClickListener {


    private boolean onePressed = false;
    private TextView MenuSinger, MenuSongName, loadingText;
    private LinearLayout SingerAndSong;
    private ProgressBar progressBar;
    private ImageButton MenuStartOrPause;
    private ImageButton menuSettings;
    private MylistView mylistView;
    private List<String> songs;
    private int position;
    private int topHeight;
    private MyAdapter adapter;
    private Map<String, String> map;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    setAdapterForList();
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    mylistView.setVisibility(View.VISIBLE);
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.songlistactivity);

        songs = new ArrayList<>();
        map = new HashMap<>();
        initView();
        initOther();
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("have_load", false)) {
            progressBar.setVisibility(View.VISIBLE);
            loadingText.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    loading(Environment.getExternalStorageDirectory().getPath());
                    loading("/storage/sdcard1");
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            }).start();
        } else {
            songs = getDataFromShared();
            position = PreferenceManager.getDefaultSharedPreferences(this)
                    .getInt
                            ("selected_position", 0);
            setSongNameAndSinger();
            setAdapterForList();
        }


    }

    private void setSongNameAndSinger() {
        String fileName = songs.get(position);
        String[] name = fileName.split("\\.mp3")[0].split("-");
        if (name.length > 1) {
            MenuSongName.setText(name[1]);
            MenuSinger.setText(name[0]);
        } else {
            MenuSongName.setText("未知歌曲");
            MenuSinger.setText("未知歌手");
        }
    }


    private void setAdapterForList() {
        adapter = new MyAdapter(this, R.layout.item, songs);
        mylistView.setAdapter(adapter);
        mylistView.setOnItemDeleteListener(new MylistView.OnItemDeleteListener() {
            @Override
            public void onItemDelete(int selectedItem) {
                songs.remove(selectedItem);
                adapter.notifyDataSetChanged();
            }
        });
        mylistView.setOnItemClickListener(this);
    }

    private List<String> getDataFromShared() {
        String[] strings = PreferenceManager.getDefaultSharedPreferences(this).getString
                ("total", null).split("\\n");
        List<String> songs = new ArrayList<>();
        for (String str : strings) {
            songs.add(str);
        }
        songs.remove(songs.size() - 1);
        return songs;
    }

    private void loading(String path) {
        File[] files = new File(path).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    loading(file.getPath());
                } else if (file.getName().endsWith(".mp3")) {
                    songs.add(file.getName());
                    map.put(file.getName(), file.getPath());
                }
            }
        }
    }

    private void initView() {
        SingerAndSong = (LinearLayout) findViewById(R.id.menu_botton_layout);
        SingerAndSong.setOnClickListener(this);
        MenuSinger = (TextView) findViewById(R.id.menu_singer);
        MenuSongName = (TextView) findViewById(R.id.menu_song_name);
        MenuStartOrPause = (ImageButton) findViewById(R.id.menu_start_or_pause);
        MenuStartOrPause.setOnClickListener(this);
        menuSettings = (ImageButton) findViewById(R.id.menu_settings);
        menuSettings.setOnClickListener(this);
        mylistView = (MylistView) findViewById(R.id.list_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        loadingText = (TextView) findViewById(R.id.loading);
        if (getIntent().getBooleanExtra("come_from_main_activity", false)) {
            MenuSinger.setText(PreferenceManager.getDefaultSharedPreferences(this).getString
                    ("singer", null));
            MenuSongName.setText(PreferenceManager.getDefaultSharedPreferences(this).getString
                    ("song_name", null));
        }
        if (MainActivity.mediaPlayer.isPlaying()) {
            MenuStartOrPause.setImageResource(R.drawable.pause);
        } else {
            MenuStartOrPause.setImageResource(R.drawable.start);
        }
    }

    private void initOther() {
        topHeight = menuSettings.getHeight();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO: 2016/3/21 优化这部分内容的存储
        /**
         * 这里是在点击了item才给SharedPreference写入数据，如果在加载完后直接离开界面会导致下一次打开又要重新加载
         */
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
                .edit();
        editor.putInt("selected_position", position);
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("have_load", false)) {
            editor.putBoolean("have_load", true);
            String total = "";
            for (int i = 0; i < songs.size(); i++) {
                editor.putString(songs.get(i), map.get(songs.get(i)));
                total = total + songs.get(i) + "\n";
            }
            editor.putString("total", total);
        }
        editor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * 这里通过延时的方法实现了点击两次back键退出应用。方法来自于网上。
     * 就是设置一个是否点击已经一次的flag onePressed（默认false）。判断这个onePressed是否没false。
     * 第一次点击时设置成true，第二次再点击就退出了。同时在第一次点击的时候发出一个延时的操作，让几秒后
     * onePressed设置成false，这样子就算我们只点击了一下，隔段时间后第二次点击还是会提醒我们，而不是直接退出。
     */
    @Override
    public void onBackPressed() {
        if (!onePressed) {
            onePressed = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onePressed = false;
                }
            }, 2000);
            Toast.makeText(SongListActivity.this, "再点击一次退出应用", Toast.LENGTH_SHORT).show();
        } else {
            ActivityControl.removeAll();
            MainActivity.mediaPlayer.release();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_start_or_pause:
                // TODO: 2016/3/21 这里的判断的逻辑还没理清楚
                /**
                 * 这个是快捷播放的按钮，用户可以直接点击这个按钮就播放音乐而不需要进入播放界面。
                 * 有两个逻辑要处理，一是当打开app时，meidaPlayer是没有prepared的，这里需要我们自己处理；
                 * 二是当点击时播放或者暂停
                 */
                if (MainActivity.mediaPlayer.isPlaying()) {
                    MainActivity.mediaPlayer.pause();
                    MenuStartOrPause.setImageResource(R.drawable.start);
                } else if (!MainActivity.mediaPlayer.isPlaying()) {
                    MainActivity.mediaPlayer.start();
                    MenuStartOrPause.setImageResource(R.drawable.pause);
                }
                break;
            case R.id.menu_botton_layout:
                /**
                 * 2016-3-21 一开始这里犯下了很严重的错误，但是这一页代码中根本就没有声明这个控件，我却不断在查看是不是布局上的其他view获得了焦点等原因
                 * 判断是不是已经播放过歌曲了，如果没有就不响应点击操作
                 * 这里的have_load是在用户点击了item后存入sharedPreference中的，所以能用做这个判断
                 */
                if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("have_load",
                        false)) {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.menu_settings:

                final PopupWindow popupWindow = new PopupWindow(SongListActivity.this);
                View view = LayoutInflater.from(SongListActivity.this).inflate(R.layout
                        .menu_settings_popup_view, null);
                TextView loadAgain = (TextView) view.findViewById(R.id.load_again);
                loadAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mylistView.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        loadingText.setVisibility(View.VISIBLE);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                loading(Environment.getExternalStorageDirectory().getPath());
                                loading("/storage/sdcard1");
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            }
                        }).start();
                        popupWindow.dismiss();
                    }
                });
                popupWindow.setContentView(view);
                popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(menuSettings);
                break;
        }

    }

}
