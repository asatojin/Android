package com.jins_jp.memelib_realtime;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jins_jp.meme.MemeLib;
import com.jins_jp.meme.MemeScanListener;
import com.jins_jp.meme.MemeStatus;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements View.OnClickListener { // implements LocationListener

    String appClientId = "125936073438987";
    String appClientSecret = "i160hpvbzqn6466n6g7icxuvyjbctma1";
    MemeLib memeLib;
    List<String> scannedAddresses;
    ArrayAdapter<String> scannedAddressAdapter;
    ListView deviceListView;
    private Button btn0;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        btn0 = (Button) findViewById(R.id.btnScan);
        btn0.setOnClickListener(this);

        if (savedInstanceState == null) {
            init();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (memeLib.isScanning()) {
            menu.findItem(R.id.action_scan).setVisible(false);
            menu.findItem(R.id.action_stop).setVisible(true);
        } else {
            menu.findItem(R.id.action_scan).setVisible(true);
            menu.findItem(R.id.action_stop).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_scan) {
            startScan();
            return true;
        } else if (itemId == R.id.action_stop) {
            stopScan();
            clearList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void init() {
        //System.out.print("!!!!!!!" + appClientId);
        //MemeLib.setAppClientID(getApplicationContext(), "125936073438987", "i160hpvbzqn6466n6g7icxuvyjbctma1");
        btn0.setVisibility(View.VISIBLE);
        MemeLib.setAppClientID(getApplicationContext(), appClientId, appClientSecret);
        memeLib = MemeLib.getInstance();
//        memeLib.setVerbose(false);
        deviceListView = (ListView) findViewById(R.id.deviceListView);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btn0.setText("Connecting...");
                stopScan();
                String address = scannedAddresses.get(i);
                Intent intent = new Intent(view.getContext(), MemeDataActivity.class);
                intent.putExtra("device_address", address);
                startActivity(intent);
                clearList();
            }
        });

    }

    void startScan() {
        if (memeLib.isScanning())
            return;

        scannedAddresses = new ArrayList<>();
        scannedAddressAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scannedAddresses);
        deviceListView.setAdapter(scannedAddressAdapter);

        setSupportProgressBarIndeterminateVisibility(true);
        invalidateOptionsMenu();
        MemeStatus status = memeLib.startScan(new MemeScanListener() {
            @Override
            public void memeFoundCallback(String address) {
                scannedAddresses.add(address);

                runOnUiThread(new Runnable() {
                    public void run() {
                        scannedAddressAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        if (status == MemeStatus.MEME_ERROR_APP_AUTH) {
            Toast.makeText(this, "App Auth Failed", Toast.LENGTH_LONG).show();
            setSupportProgressBarIndeterminateVisibility(false);
        }
    }

    void stopScan() {
        if (!memeLib.isScanning())
            return;

        memeLib.stopScan();
        setSupportProgressBarIndeterminateVisibility(false);
        invalidateOptionsMenu();
    }

    void clearList() {
        scannedAddressAdapter.clear();
        deviceListView.deferNotifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        btn0.setText("Scan");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.jins_jp.memelib_realtime/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        btn0.setText("Scan");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.jins_jp.memelib_realtime/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (memeLib.isScanning()){
            stopScan();
        }else if (id == R.id.btnScan){
            startScan();
        }
    }
}
