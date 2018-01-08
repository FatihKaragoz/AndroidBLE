package com.example.hmtteol.bleyoklama;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hmtteol.BleYoklama.R;

import java.util.UUID;

public class transmitter extends Activity {

    private ImageButton start;
    private ImageButton stop;
    private EditText yayinismi;
    private TextView info;
    //private BluetoothGattServer mGattServer;
    private BluetoothAdapter bluetoothAdapter;
    private AdvertiseCallback advertiseCallback;
    private BluetoothManager bluetoothManager;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private AdvertiseSettings settings;
    private AdvertiseData data;

    private static UUID SERVICE_ID= UUID.fromString("795090c7-420d-4048-a24e-18e60180e23c");
    private static UUID SERVICE_UUID = UUID.fromString("795090c7-420d-4048-a24e-18e60180e23c");
    public static UUID DESCRIPTOR_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static UUID DESCRIPTOR_USER_DESC = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb");
    public static UUID CHARACTERISTIC_COUNTER_UUID = UUID.fromString("31517c58-66bf-470c-b662-e352a6c80cba");
    public static UUID CHARACTERISTIC_INTERACTOR_UUID = UUID.fromString("0b89d2d4-0ea6-4141-86bb-0c5fb91ab14a");

    private String deviceNameFromEdittext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.activity_transmitter);

        info = findViewById(R.id.bilgi);
        info.setText("Yayın yap");

        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        start = findViewById(R.id.transmit);
        start.setVisibility(View.VISIBLE);

        stop = findViewById(R.id.stop);
        stop.setVisibility(View.INVISIBLE);

        yayinismi = findViewById(R.id.yayinismi);

        advertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                info.setText("Yayın yapılıyor...");
                start.setVisibility(View.INVISIBLE);
                stop.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartFailure(int errorCode) {
                info.setText("Yayın yap");
                stop.setVisibility(View.INVISIBLE);
                start.setVisibility(View.VISIBLE);
            }

        };

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // START TRASMIT

                if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                    // Request user to enable it
                    Intent enableBtIntent = new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBtIntent);
                    finish();
                    return;
                }else{
                    deviceNameFromEdittext = yayinismi.getText().toString().trim();
                    bluetoothAdapter.setName(deviceNameFromEdittext);

                    settings = new AdvertiseSettings.Builder()
                            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                            .setConnectable(true)
                            .setTimeout(0)
                            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                            .build();

                    data = new AdvertiseData.Builder()
                            .setIncludeDeviceName(true)
                            .setIncludeTxPowerLevel(false)
                            .addServiceUuid(new ParcelUuid(SERVICE_ID))
                            .build();

                    mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
                    mBluetoothLeAdvertiser.startAdvertising(settings, data, advertiseCallback);


                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //STOP TRANSMIT
                mBluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
                advertiseCallback.onStartFailure(1);
            }
        });

    }


}