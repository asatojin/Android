package com.jins_jp.memelib_realtime;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jins_jp.meme.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.location.LocationManager.*;


public class MemeDataActivity extends ActionBarActivity implements View.OnClickListener {

    private GoogleApiClient client;
    MemeLib memeLib;
    String deviceAddress;
    ListView dataItemListView;
    MemeDataItemAdapter dataItemAdapter;
    boolean calib_flg;
    boolean marking_flg;
    int i;
    int mark = 0;
    SimpleDateFormat df_fn = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date datetime_fn = new Date();
    private Vibrator vb;
    private LinearLayout layoutBG;
    private ListView layoutList;

    // GPS用
    SimpleLocationManager simpleLocationManager;
    public Double lat=0.0;
    public Double lng=0.0;

    void init() {
        memeLib = MemeLib.getInstance();
        deviceAddress = getIntent().getStringExtra("device_address");

        dataItemListView = (ListView) findViewById(R.id.data_item_list_view);
        dataItemAdapter = new MemeDataItemAdapter(this);
        dataItemListView.setAdapter(dataItemAdapter);
        memeLib.connect(deviceAddress);

        memeLib.setMemeConnectListener(new MemeConnectListener() {
            @Override
            public void memeConnectCallback(boolean status) {
//                memeLib.startDataReport(memeRealtimeListener);
//                Toast.makeText(MemeDataActivity.this, "Connected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void memeDisconnectCallback() {
                Toast.makeText(MemeDataActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
        Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT);
//        memeLib.connect(deviceAddress);
    }

    // for FileOutput
    private String fname=""; // =file.getPath();//Environment.getExternalStorageDirectory().getPath()+"/JINS_RTLogger/RTlogger_" + df_fn.format(datetime_fn) + ".csv";

    final MemeRealtimeListener memeRealtimeListener = new MemeRealtimeListener() {
        @Override
        public void memeRealtimeCallback(final MemeRealtimeData memeRealtimeData) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setSupportProgressBarIndeterminateVisibility(true);
                    i++;
                    if (i == 1) { // Header
                        try {
                            writer(fname, "Count,ReceiveTime,Pitch,Roll,Yaw,accX,accY,accZ,isWalk,NoiseStatus,PowerLeft,EyeMoveUp,EyeMoveDown,EyeMoveLeft,EyeMoveRight,Marking,lat,lng");
                        }catch(FileNotFoundException e){
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                            Toast.makeText(MemeDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }catch (IOException e){
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                            Toast.makeText(MemeDataActivity.this, fname, Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (marking_flg == true) {
                        mark = 1;
                        marking_flg=false;
                    }else {
                        mark = 0;
                    }
                    try {
                        writer(fname, dataItemAdapter.updateMemeData(memeRealtimeData, i, datetime_fn, calib_flg) + "," + String.valueOf(mark) + "," + String.valueOf(lat) + "," + String.valueOf(lng));
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                        Toast.makeText(MemeDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }catch(IOException e){
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                        Toast.makeText(MemeDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(MemeDataActivity.this, String.valueOf(context.getFilesDir()), Toast.LENGTH_SHORT).show();
                    dataItemAdapter.notifyDataSetChanged();
                    setSupportProgressBarIndeterminateVisibility(false);
                    calib_flg = false;
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_meme_data);
        init();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        // GPS
        simpleLocationManager = new SimpleLocationManager((LocationManager) getSystemService(LOCATION_SERVICE));

        // スリープしない
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //buttonを取得
        Button btn = (Button) findViewById(R.id.btnMarking);
        btn.setOnClickListener(this);

        Button btn2 = (Button) findViewById(R.id.btnDiscon);
        btn2.setOnClickListener(this);

        Button btn3 = (Button) findViewById(R.id.btnStart);
        btn3.setOnClickListener(this);

        Button btn4 = (Button) findViewById(R.id.btnStop);
        btn4.setOnClickListener(this);

        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        layoutBG = (LinearLayout)findViewById(R.id.container);
        layoutList = (ListView)findViewById(R.id.data_item_list_view);

//        simpleLocationManager.start();

        // 待たせる
//        Toast.makeText(this,"Connecting...",Toast.LENGTH_SHORT);
        sleep(5000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meme_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(200);

//        int id = item.getItemId();

//        if (id == R.id.action_disconnect) {
//            memeLib.disconnect();
//            if (Build.VERSION.SDK_INT <= 20) {
//                ActivityManager am = ((ActivityManager) getSystemService(ACTIVITY_SERVICE));
//                am.clearApplicationUserData();
//            }
//            this.finish();
//            return true;
//        } else if (id == R.id.action_calib) {
//            calib_flg = true;
//            return true;
//        } else if (id == R.id.action_mark) {
//            marking_flg = true;
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    public void onTouch(MotionEvent ev){
        vb.vibrate(200);
        Toast.makeText(this,"Touch!",Toast.LENGTH_SHORT);

    }

    // ボタン操作
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnStart) { // Start
            if (memeLib.isDataReceiving()) {
                Toast.makeText(MemeDataActivity.this, "Already Started !!", Toast.LENGTH_SHORT).show();
            }else{
                vb.vibrate(200);
                datetime_fn = new Date(System.currentTimeMillis());
                dirchk(new File(Environment.getExternalStorageDirectory().getPath() + "/JINS/"));
                File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/JINS/MEME_Realtime/");
                File file = new File(dirchk(dir).getPath() + "/" + deviceAddress.replace(":", "") + "_" + df_fn.format(datetime_fn) + ".csv");
                fname = file.getPath();
                i = 0;
                simpleLocationManager.start();
                memeLib.startDataReport(memeRealtimeListener);
                Toast.makeText(MemeDataActivity.this, "Recording Start", Toast.LENGTH_SHORT).show();
//                layoutBG.setBackgroundColor(Color.BLACK);
//                layoutList.setBackgroundColor(Color.BLACK);
            }
        }else if (id == R.id.btnStop){  // Stop
            vb.vibrate(200);
            memeLib.stopDataReport();
            simpleLocationManager.stop();
            Toast.makeText(MemeDataActivity.this, "Recording Stop", Toast.LENGTH_SHORT).show();
//            layoutBG.setBackgroundColor(Color.WHITE);
//            layoutList.setBackgroundColor(Color.WHITE);
        }else if (id == R.id.btnMarking) {  // FreeMarking
            if(memeLib.isDataReceiving()) {
                vb.vibrate(200);
                this.marking_flg = true;
                Toast.makeText(MemeDataActivity.this, "Marker on Count:" + toString().valueOf(i), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.btnDiscon) {  // Diconnect
            vb.vibrate(200);
            simpleLocationManager.stop();
            memeLib.disconnect();
            if (Build.VERSION.SDK_INT <= 20) {
                ActivityManager am = ((ActivityManager) getSystemService(ACTIVITY_SERVICE));
                am.clearApplicationUserData();
            }
            this.finish();
        }
    }

    @Override
    protected void onResume() {
        if (simpleLocationManager.getCurrentLocation() == null) {
            // 位置情報の取得を開始します。
            simpleLocationManager.start();
        }
        simpleLocationManager.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        // 他画面へ遷移する場合は位置情報取得を止めます。
//        simpleLocationManager.stop();
        super.onPause();
    }

    final class SimpleLocationManager extends BetterLocationManager {

        /**
         * コンストラクタ
         * @param locationManager 位置情報サービス
         */
        public SimpleLocationManager(final LocationManager locationManager) {
            super(locationManager);
        }

        @Override
        protected void onLocationProviderNotAvailable() {
            // Google Maps アプリと同様に[現在地機能を改善]ダイアログを起動します。
            new AlertDialog.Builder(MemeDataActivity.this)
                    .setTitle("現在地機能を改善")
                    .setMessage("現在、位置情報は一部有効でないものがあります。次のように設定すると、もっともすばやく正確に現在地を検出できるようになります:\n\n● 位置情報の設定でGPSとワイヤレスネットワークをオンにする\n\n● Wi-Fiをオンにする")
                    .setPositiveButton("設定", new DialogInterface.OnClickListener() {
                        @Override public void onClick(final DialogInterface dialog, final int which) {
                            try {
                                startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                            } catch (ActivityNotFoundException e) {}	// 無視する
                        }
                    })
                    .setNegativeButton("スキップ", new DialogInterface.OnClickListener() {
                        @Override public void onClick(final DialogInterface dialog, final int which) {}
                    })
                    .create()
                    .show();
        }

        @Override
        protected void onLocationProgress(final long time) {
            if (getCurrentLocation() == null && time == 0L) {
                Toast.makeText(getApplicationContext(), "現在地を特定しています。", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onLocationTimeout() {
            if (getCurrentLocation() == null) {
                Toast.makeText(getApplicationContext(), "一時的に現在地を検出できません。", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onUpdateLocation(final Location location, final int updateCount) {
            if (updateCount == 0) {
                // TODO: 必要があれば、ここに処理を記述します。
            }
            // TODO: 現在地マーカー表示用オーバーレイなどへの設定処理を記述します。
            lat = location.getLatitude();
            lng = location.getLongitude();
        }

    }

    // 指定ディレクトリが存在しなければ作成する
    private File dirchk(File dir) {
        if (!dir.exists()) {
            try {
                dir.mkdir();
            }catch(Exception e){
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
                //Toast.makeText(MemeDataActivity.this, "mkDIR Failed.", Toast.LENGTH_SHORT).show();
            }
        }
        return dir;
    }

    // ファイル書き込み部
    private void writer(String fileName, String str) throws IOException {
        File file = new File(fileName);
        if (file.exists()){
        }else{
            file.createNewFile();
        }
        write.write(file,str);

//        try {
//            // ストリームを開く
//            FileOutputStream output = openFileOutput(fileName, MODE_APPEND);
//            // 書き込み
//            output.write(str.getBytes());
//            // ストリームを閉じる
//            output.close();
//        }catch(FileNotFoundException e){
//            System.out.println(e.getMessage());
//        }catch(IOException e){
//            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
//            e.printStackTrace();
//            System.out.println(e.getMessage());
//        }
    }

    //指定ミリ秒実行を止めるメソッド
    public synchronized void sleep(long msec)
    {
        try
        {
            wait(msec);
        }catch(InterruptedException e){}
    }
}
