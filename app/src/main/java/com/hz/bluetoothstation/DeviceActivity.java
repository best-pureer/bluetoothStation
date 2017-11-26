package com.hz.bluetoothstation;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class DeviceActivity extends SwipeBackActivity {

    private ArrayAdapter<String> mArrayAdapter;
    private ListView listViewDevice;
    public static BluetoothAdapter mBluetoothAdapter;
    private final List<String> bluetoothDeviceList = new ArrayList<String>();
    IntentFilter filter = new IntentFilter();
    private BluetoothDevice device;
    public static BluetoothSocket clientSocket;
    public static boolean isConnect;
    private Runnable runnable;
    Handler mHandle = new Handler();
    private Toast mToast;
    private Toolbar deviceToolbar;
    private TextView emptyView;
    private UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        deviceToolbar=(Toolbar)findViewById(R.id.deviceToolbar);
        emptyView=(TextView)findViewById(R.id.emptyView);
        listViewDevice=(ListView)findViewById(R.id.listViewDevice);

        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        listViewDevice.setEmptyView(emptyView);
        deviceToolbar.setNavigationIcon(R.drawable.back);
        deviceToolbar.setNavigationOnClickListener(new NavigationOnClickListener());
        deviceToolbar.setLogo(R.drawable.bluetoothicon);
        deviceToolbar.setTitle("蓝牙设备列表");
        deviceToolbar.inflateMenu(R.menu.device_toolbar_menu);
        deviceToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        listViewDevice.setOnItemClickListener(new ItemClickListener());

        initState();
        openBluetooth();
        filterInit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(bluetoothReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bluetoothReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandle.removeCallbacks(runnable);
    }



    private class NavigationOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!isConnect) {
                s = mArrayAdapter.getItem(position);

                String address = s.substring(s.indexOf(":") + 1).trim();
                device = mBluetoothAdapter.getRemoteDevice(address);
                new AlertDialog.Builder(DeviceActivity.this).setIcon(R.mipmap.ic_launcher)
                        .setTitle("连接" + s.substring(0, s.indexOf(":")).trim() + "?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.deviceName=s.substring(0,s.indexOf(":"));
                        new ConnectThread().start();
                        if (mBluetoothAdapter.isDiscovering()) {
                            mHandle.removeCallbacks(runnable);
                            mBluetoothAdapter.cancelDiscovery();
                        }
                    }
                }).show();
            } else {
                showToast("已经连接");
            }

        }
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener=new Toolbar.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId())
            {

                case R.id.discovery:
                    bluetoothDeviceList.clear();
                    listViewDevice.setAdapter(null);
                    mHandle.removeCallbacks(runnable);
                    showToast("正在搜索");
                    searchBluetoothDevice();
                    break;
                case R.id.close:
                    if(isConnect)
                    {
                        new AlertDialog.Builder(DeviceActivity.this).setIcon(R.drawable.closeicon)
                                .setTitle("断开连接？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    isConnect=false;
//                                    button1.setBackgroundColor(Color.RED);
                                    clientSocket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).show();
                    }
                    else
                    {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        showToast("未连接");
                    }
                    break;
            }
            return true;
        }
    };

    private void searchBluetoothDevice() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        runnable = new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.cancelDiscovery();
                mHandle.removeCallbacks(runnable);//这里注意
                showToast("搜索结束");
            }
        };
        final long SCAN_PERIOD = 10000;
        mHandle.postDelayed(runnable, SCAN_PERIOD);

        mBluetoothAdapter.startDiscovery();
    }

    private void addDevice(BluetoothDevice d) {
        bluetoothDeviceList.add(d.getName() + ":" + d.getAddress() + "\n");
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bluetoothDeviceList);
        listViewDevice.setAdapter(mArrayAdapter);
    }

    public final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice thisDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!bluetoothDeviceList.contains(thisDevice.getName() + ":" + thisDevice.getAddress() + "\n")) {
                    showToast("找到" + (bluetoothDeviceList.size() + 1) + "个");
                    addDevice(thisDevice);
                }
            }
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                isConnect = true;
//                button1.setBackgroundColor(Color.GREEN);
                showToast("连接成功");
            }
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                isConnect = false;
//                button1.setBackgroundColor(Color.RED);
                showToast("连接断开");
            }

        }
    };



    private class ConnectThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                clientSocket = device.createRfcommSocketToServiceRecord(mUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                clientSocket.connect();
                mBluetoothAdapter.cancelDiscovery();
                mHandle.removeCallbacks(runnable);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void filterInit() {

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(bluetoothReceiver, filter);
    }

    private void openBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1);
            }
        }
    }

    private void initState() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(0xff2196f3);
        }
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT&&Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            LinearLayout linear_bar=(LinearLayout)findViewById(R.id.ll_bar);
            linear_bar.setVisibility(View.VISIBLE);

            int statusHeight =getStatusHeight();
            ViewGroup.LayoutParams params= linear_bar.getLayoutParams();
            params.height=statusHeight;
            linear_bar.setLayoutParams(params);

        }
    }

    private int getStatusHeight() {
        try {
            Class<?> c=Class.forName("com.android.internal.R$dimen");
            Object obj=c.newInstance();
            Field field=c.getField("status_bar_height");
            int x=Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void showToast(String s) {
        if (mToast == null) {
            mToast = Toast.makeText(DeviceActivity.this, s, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(s);
        }
        mToast.show();
    }
}
