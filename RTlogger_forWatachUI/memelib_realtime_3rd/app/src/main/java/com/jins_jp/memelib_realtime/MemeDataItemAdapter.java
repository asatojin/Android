package com.jins_jp.memelib_realtime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jins_jp.meme.MemeRealtimeData;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MemeDataItemAdapter extends BaseAdapter {

    List<String[]> items;
    Context context;
    LayoutInflater inflater;
    MemeDataActivity memeDataActivity;
    int walk;
    int noise;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat df_et = new SimpleDateFormat("H:mm:ss.SSS");
    SimpleDateFormat df_fn = new SimpleDateFormat("yyyyMMdd_HHmmss");

    float rev_x = 0;
    float rev_y = 0;
    float rev_z = 0;
    float rev_pitch = 0;
    float rev_roll = 0;
    float rev_yaw = 0;

    ////////////////////for GPS///////////////////////
    private android.location.LocationManager locationManager;
    private LocationListener locationListener;
    private Timer locationTimer;
    long time;


    public MemeDataItemAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        updateMemeData(new MemeRealtimeData(), 0,new Date(), false);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(context, R.layout.meme_data_item, null);
        }

        //TextView labelTextView = (TextView)view.findViewById(R.id.item_label);
        TextView valueTextView = (TextView) view.findViewById(R.id.item_value);

        String[] item = items.get(i);
        //labelTextView.setText(item[0]);
        valueTextView.setText(item[0]);//1

        return view;
    }

    public String updateMemeData(MemeRealtimeData d, int i, Date datetime_fn, Boolean calib_flg) {
        if (calib_flg == true) {
            rev_x = d.getAccX();
            rev_y = d.getAccY();
            rev_z = d.getAccZ();
            rev_pitch = d.getPitch();
            rev_roll = d.getRoll();
            rev_yaw = d.getYaw();
        }
        Date datetime = new Date(System.currentTimeMillis());
        //String elapsed = df_et.format(datetime.getTime() - datetime_fn.getTime());
        String elapsed = df_et.format((datetime.getTime() - 480 * 60 * 1000) - datetime_fn.getTime());
        items = new ArrayList<>();
        //addItem(R.string.fit_status, d.getFitError());
        //addItem(R.string.elapsed_time, elapsed);
        addItem(elapsed);
        //addItem(R.string.count, i);
        //addItem(R.string.walking, d.isWalking());
        //addItem(R.string.noise_status, d.isNoiseStatus());
        //addItem(R.string.power_left, d.getPowerLeft());
        //addItem(R.string.eye_move_up, d.getEyeMoveUp());
        //addItem(R.string.eye_move_down, d.getEyeMoveDown());
        //addItem(R.string.eye_move_left, d.getEyeMoveLeft());
        //addItem(R.string.eye_move_right, d.getEyeMoveRight());
        //addItem(R.string.blink_streangth, d.getBlinkStrength());
        //addItem(R.string.blink_speed, d.getBlinkSpeed());
        //addItem(R.string.roll, Calib(d.getRoll(),rev_roll));
        //addItem(R.string.pitch, Calib(d.getPitch(),rev_pitch));
        //addItem(R.string.yaw, Calib(d.getYaw(),rev_yaw));
        //addItem(R.string.acc_x, Calib(d.getAccX(),rev_x));
        //addItem(R.string.acc_y, Calib(d.getAccY(),rev_y));
        //addItem(R.string.acc_z, Calib(d.getAccZ(),rev_z));

        if (d.isWalking()) {
            walk = 1;
        } else {
            walk = 0;
        }
        if (d.isNoiseStatus()) {
            noise = 1;
        } else {
            noise = 0;
        }
        return String.valueOf(i) + "," + String.valueOf(df.format(datetime)) + "," + String.valueOf(Calib(d.getPitch(), rev_pitch)) + ","
                + String.valueOf(Calib(d.getRoll(), rev_roll)) + "," + String.valueOf(Calib(d.getYaw(), rev_yaw)) + ","
                + String.valueOf(Calib(d.getAccX(), rev_x)) + "," + String.valueOf(Calib(d.getAccY(), rev_y)) + ","
                + String.valueOf(Calib(d.getAccZ(), rev_z)) + "," + String.valueOf(walk) + ","
                + String.valueOf(noise) + "," + String.valueOf(d.getPowerLeft()) + ","
                + String.valueOf(d.getEyeMoveUp()) + "," + String.valueOf(d.getEyeMoveDown()) + ","
                + String.valueOf(d.getEyeMoveLeft()) + "," + String.valueOf(d.getEyeMoveRight());
    }

    private String getLabel(int resourceId) {
        return context.getResources().getString(resourceId);
    }

    /* Original Process */
    //private void addItem(int resourceId, Object value) {
    //    items.add(new String[] {getLabel(resourceId), String.valueOf(value)});
    //}
    /* for WatchUI Process */
    private void addItem(Object value) {
        items.add(new String[]{String.valueOf(value)});
    }

    private float Calib(float val, float rev) {
        float tmp = val - rev;
        if (val > 127 || val < -127) {
            if (tmp > 127 || tmp < -127) { //for Eular
                tmp = val + rev;
            }
        }
        return tmp;
    }
}

