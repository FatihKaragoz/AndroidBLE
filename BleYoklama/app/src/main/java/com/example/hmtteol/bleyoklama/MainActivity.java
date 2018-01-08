package com.example.hmtteol.bleyoklama;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.hmtteol.BleYoklama.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    // BluetoothDevice mDevice;
    BluetoothDevice clickedDevice;
    ImageButton startScanningButton;
    ImageButton stopScanningButton;
    ImageButton bluetoothOn;
    ImageButton bluetoothOff;


    //TextView peripheralTextView;
    ListView scanResults;

    private ArrayList<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    private BluetoothGattCallback btleGattCallback;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private HashMap<String, BluetoothDevice> name2Device=new HashMap<String, BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.activity_main);


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, list);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE bu cihazda desteklenmiyor.Uygulama  düzgün çalışmayabilir.", Toast.LENGTH_LONG).show();
            finish();
        }
        else{
            //Toast.makeText(this,"BLE destekleniyor",Toast.LENGTH_SHORT).show();
        }

        scanResults = (ListView)findViewById(R.id.scanResultLv);
        scanResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // BAĞLANTI BURDAN YAPILACAK.
                BluetoothDevice lastDevice = name2Device.get(adapterView.getItemAtPosition(i));

                // mDevice = btAdapter.getRemoteDevice(result.getDevice().getAddress());
                BluetoothGatt bluetoothGatt = lastDevice.connectGatt(MainActivity.this, false, btleGattCallback);
                bluetoothGatt.discoverServices();
                Toast.makeText(getApplicationContext(),lastDevice.getName()+"ile bağlantı kuruluyor.", Toast.LENGTH_SHORT).show();

            }
        });

        btleGattCallback = new BluetoothGattCallback() {
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
                // this will get called anytime you perform a read or write characteristic operation
                Toast.makeText(MainActivity.this,"ilk hiçbirşey olmadı",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
                // this will get called when a device connects or disconnects
                if(status == BluetoothGatt.STATE_CONNECTING)
                    Toast.makeText(MainActivity.this,"Bağlanıyor...",Toast.LENGTH_SHORT).show();
                else if(status == BluetoothGatt.STATE_CONNECTED)
                    Toast.makeText(MainActivity.this,"Bağlandı",Toast.LENGTH_SHORT).show();
                else if(status == BluetoothGatt.STATE_DISCONNECTED)
                    Toast.makeText(MainActivity.this,"Bağlantı kesildi",Toast.LENGTH_SHORT).show();
                else if(status == BluetoothGatt.STATE_DISCONNECTING)
                    Toast.makeText(MainActivity.this,"Bağlantı kesiliyor...",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this,"hiçbirşey olmadı",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
                // this will get called after the client initiates a 			BluetoothGatt.discoverServices() call
                Toast.makeText(MainActivity.this,"2 hiçbirşey olmadı",Toast.LENGTH_SHORT).show();
            }
        };



        bluetoothOn = (ImageButton)findViewById(R.id.bluetoothOn);
        bluetoothOff = (ImageButton)findViewById(R.id.bluetoothOff);

        if (btAdapter == null || !btAdapter.isEnabled()) {
            bluetoothOn.setVisibility(View.VISIBLE);
            bluetoothOff.setVisibility(View.INVISIBLE);
        }
        bluetoothOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btAdapter.disable();
                Toast.makeText(getApplicationContext(),"Bluetooth kapatıldı, uygulama düzgün çalışmayablir.", Toast.LENGTH_SHORT).show();
                bluetoothOffImageButtonClose();
            }
        });
        bluetoothOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               BluetoothPermissionRequest();
            }
        });

        startScanningButton = (ImageButton) findViewById(R.id.StartScanButton);
        startScanningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(btAdapter==null || !btAdapter.isEnabled()) {
                    boolean btcontrol = BluetoothPermissionRequest();
                    if (!btcontrol) {
                        Toast.makeText(getApplicationContext(), "Lütfen bluetootha izin verin", Toast.LENGTH_SHORT);
                        return;
                    }
                }
                    startScanning();
            }
        });
        startScanningButton.setVisibility(View.VISIBLE);

        stopScanningButton = (ImageButton) findViewById(R.id.StopScanButton);
        stopScanningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopScanning();
            }
        });
        stopScanningButton.setVisibility(View.INVISIBLE);

        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        /*if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Uygulamanın konum bilgilerine ihtiacı var");
            builder.setMessage("Konum kullanımına izin verin.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }*/
    }

    private void bluetoothOffImageButtonClose(){
        bluetoothOff.setVisibility(View.INVISIBLE);
        bluetoothOn.setVisibility(View.VISIBLE);
    }
    private void bluetoothOnImageButtonClose(){
        bluetoothOn.setVisibility(View.INVISIBLE);
        bluetoothOff.setVisibility(View.VISIBLE);
    }




    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        /*public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {
            final BleAdvertisedData badata = BleUtil.parseAdertisedData(scanRecord);
            String deviceName = device.getName();
            if( deviceName == null ){
                deviceName = badata.getName();
            }
        }
       */

        public void onScanResult(int callbackType, final ScanResult result) {
            /*peripheralTextView.append("Aygıt adı:" + result.getDevice().getName()+" rssi: " + result.getRssi() +"Aygıt adresi (MAC) :"+result.getDevice().getAddress()+ "\n" );
            final int scrollAmount = peripheralTextView.getLayout().getLineTop(peripheralTextView.getLineCount()) - peripheralTextView.getHeight();
            if (scrollAmount > 0)
                peripheralTextView.scrollTo(0, scrollAmount);*/
            //ListElementsArrayList.add(result.getDevice().getName().toString());
            //clickedDevice = result.getDevice();
            //clickedDevice = result.getDevice();
            list.add(result.getDevice().getName());
            name2Device.put(result.getDevice().getName(),result.getDevice());

            adapter.notifyDataSetChanged();
            scanResults.setAdapter(adapter);
        }

    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("İzin verildi.");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Uygulama kapanıyor.");
                    builder.setMessage("Konum izni alınamadı. Uygulama düzgün çalışmayabilir.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
        //System.out.println("start scanning");
        //peripheralTextView.setText("");
        startScanningButton.setVisibility(View.INVISIBLE);
        stopScanningButton.setVisibility(View.VISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public void startScanning() {
        //System.out.println("start scanning");
        //peripheralTextView.setText("");
        startScanningButton.setVisibility(View.INVISIBLE);
        stopScanningButton.setVisibility(View.VISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public void stopScanning() {
        //System.out.println("stopping scanning");
        //peripheralTextView.append("Arama durduruldu.");
        startScanningButton.setVisibility(View.VISIBLE);
        stopScanningButton.setVisibility(View.INVISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.akademisyenMenu:
                startActivity(new Intent(MainActivity.this,transmitter.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean BluetoothPermissionRequest(){
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        boolean btcontrol=startActivityIfNeeded(enableBtIntent,REQUEST_ENABLE_BT);
        if(btcontrol==true){
            bluetoothOnImageButtonClose();
            return true;
        }
        //Toast.makeText(getApplicationContext(), "Bluetooth etkinleştirildi.", Toast.LENGTH_SHORT).show();
        return false;
    }

}