package com.hz.bluetoothstation;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.CDATASection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{

    private boolean receiveSelectFlag;
    private boolean receiveHexFlag;
    IntentFilter filter = new IntentFilter();
    private ToggleButton toggleButton;
    private TextView textView_sendName,textView_sendValue,textView_device;
    private ImageView imageIntent;
    private ImageView imageSend;
    private ImageView up,down,left,right;
    private ConstraintLayout constraintLayout;
    private EditText editTextSend;
    private Toast mToast;
    private SwitchCompat switchReceive;
    private TextView textViewReceive;
    private Toolbar mainToolbar;
    public static OutputStream outputStream;
    private InputStream inputStream;
    byte[] inputStreamBuffer = new byte[10240];
    SharedPreferences sharedPreferences;
    InputMethodManager inputMethodManager;
    public  int radioID;
    private Button button01,button02,button03,button04,button05,button06;
    public  static String buttonValue01="1",buttonValue02="2",buttonValue03="3",buttonValue04="4",buttonValue05="5",buttonValue06="6";
    public  String buttonName01="bt01",buttonName02="bt02",buttonName03="bt03",buttonName04="bt04",buttonName05="bt05",buttonName06="bt06";
    public  static String valueUp="A",valueDown="B",valueLeft="B",valueRight="D",valueStop="F";
    public  String nameUp="前进",nameDown="后退",nameLeft="左转",nameRight="右转",nameStop="停止";
    public Boolean isSendF1=false,isSendF2=false,isSendF3=false,isSendF4=false,isSendF5=false,isSendF6=false;
    public static String deviceName="device";
    private boolean flag_switch;
    private final CharSequence[] charSequences={"全部复制并转发","清空数据","文本可选择性"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setListeners();
        checkPermission();

        inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        textViewReceive.setMovementMethod(ScrollingMovementMethod.getInstance());
        mainToolbar.inflateMenu(R.menu.main_toolbar_menu);
        receiveSelectFlag=false;

        initState();
        filterInit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        readData();
        setButtonText();
        if (!DeviceActivity.isConnect) {
            switchReceive.setChecked(false);
            textView_device.setText(R.string.device提示);
            imageIntent.setImageResource(R.drawable.notconnect);
        }
        else {
            imageIntent.setImageResource(R.drawable.connect);
            textView_device.setText(deviceName);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(disConnectReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void findViews()
    {
        textView_sendName=(TextView)findViewById(R.id.textView_sendName);
        textView_sendValue=(TextView)findViewById(R.id.textView_sendValue);
        textView_device=(TextView)findViewById(R.id.textView_device);
        imageIntent=(ImageView)findViewById(R.id.imageIntent);
        imageSend=(ImageView)findViewById(R.id.imageSend);
        constraintLayout=(ConstraintLayout)findViewById(R.id.constraintLayout);
        editTextSend=(EditText)findViewById(R.id.editTextSend);
        switchReceive=(SwitchCompat)findViewById(R.id.switchReceive);
        textViewReceive=(TextView)findViewById(R.id.textViewReceive);
        toggleButton=(ToggleButton)findViewById(R.id.toggleButton);
        mainToolbar=(Toolbar)findViewById(R.id.mainToolbar);
        button01=(Button)findViewById(R.id.button01);
        button02=(Button)findViewById(R.id.button02);
        button03=(Button)findViewById(R.id.button03);
        button04=(Button)findViewById(R.id.button04);
        button05=(Button)findViewById(R.id.button05);
        button06=(Button)findViewById(R.id.button06);
        up=(ImageView)findViewById(R.id.up);
        down=(ImageView)findViewById(R.id.down);
        left=(ImageView)findViewById(R.id.left);
        right=(ImageView)findViewById(R.id.right);
        textViewReceive.setOnLongClickListener(new textViewReceiveOnLongClickListener());
        mainToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    private void setListeners()
    {
        up.setOnTouchListener(this);
        down.setOnTouchListener(this);
        left.setOnTouchListener(this);
        right.setOnTouchListener(this);
        imageSend.setOnTouchListener(this);
        button01.setOnTouchListener(this);
        button02.setOnTouchListener(this);
        button03.setOnTouchListener(this);
        button04.setOnTouchListener(this);
        button05.setOnTouchListener(this);
        button06.setOnTouchListener(this);
        constraintLayout.setOnTouchListener(new ConstraintLayoutOnTouchListener());
        switchReceive.setOnCheckedChangeListener(new SwitchOnCheckedChangeListener());
    }

    private void setButtonText()
    {
        button01.setText(buttonName01);
        button02.setText(buttonName02);
        button03.setText(buttonName03);
        button04.setText(buttonName04);
        button05.setText(buttonName05);
        button06.setText(buttonName06);
    }

    private void setTextValue(String s)
    {

        if(toggleButton.isChecked())
        {
            if (s.length()==1)
            {
                s="0"+s;
            }
            s="0x"+s;
        }
        else
        {
            s="“"+s+"”";
        }
        textView_sendValue.setText(s);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (DeviceActivity.isConnect)
        {
            switch (v.getId())
            {
                case R.id.up:
                    if(event.getAction()==MotionEvent.ACTION_DOWN)
                    {
                        writeOutputStream(valueUp);
                        setTextValue(valueUp);
                        textView_sendName.setText(nameUp);
                    }
                    if(event.getAction()==MotionEvent.ACTION_UP)
                    {
                        writeOutputStream(valueStop);
                        setTextValue(valueStop);
                        textView_sendName.setText(nameStop);
                    }
                    break;
                case R.id.down:
                    if(event.getAction()==MotionEvent.ACTION_DOWN)
                    {
                        writeOutputStream(valueDown);
                        setTextValue(valueDown);
                        textView_sendName.setText(nameDown);
                    }
                    if(event.getAction()==MotionEvent.ACTION_UP)
                    {
                        writeOutputStream(valueStop);
                        setTextValue(valueStop);
                        textView_sendName.setText(nameStop);
                    }
                    break;
                case R.id.left:
                    if(event.getAction()==MotionEvent.ACTION_DOWN)
                    {
                        writeOutputStream(valueLeft);
                        setTextValue(valueLeft);
                        textView_sendName.setText(nameLeft);
                    }
                    if(event.getAction()==MotionEvent.ACTION_UP)
                    {
                        writeOutputStream(valueStop);
                        setTextValue(valueStop);
                        textView_sendName.setText(nameStop);
                    }
                    break;
                case R.id.right:
                    if(event.getAction()==MotionEvent.ACTION_DOWN)
                    {
                        writeOutputStream(valueRight);
                        setTextValue(valueRight);
                        textView_sendName.setText(nameRight);
                    }
                    if(event.getAction()==MotionEvent.ACTION_UP)
                    {
                        writeOutputStream(valueStop);
                        setTextValue(valueStop);
                        textView_sendName.setText(nameStop);
                    }
                    break;
                case R.id.button01:
                    if (event.getAction()==MotionEvent.ACTION_DOWN){
                        writeOutputStream(buttonValue01);
                        setTextValue(buttonValue01);
                        textView_sendName.setText(buttonName01);
                    }
                    if(event.getAction()==MotionEvent.ACTION_UP){
                        if (isSendF1==true)
                        {
                            writeOutputStream("F");
                            setTextValue("F");
                        }
                    }
                    break;
                case R.id.button02:
                    if (event.getAction()==MotionEvent.ACTION_DOWN){
                        writeOutputStream(buttonValue02);
                        setTextValue(buttonValue02);
                        textView_sendName.setText(buttonName02);
                    }
                    if(event.getAction()==MotionEvent.ACTION_UP){
                        if (isSendF2==true)
                        {
                            writeOutputStream("F");
                            setTextValue("F");
                        }
                    }
                    break;
                case R.id.button03:
                    if (event.getAction()==MotionEvent.ACTION_DOWN){
                        writeOutputStream(buttonValue03);
                        setTextValue(buttonValue03);
                        textView_sendName.setText(buttonName03);
                    }
                    if(event.getAction()==MotionEvent.ACTION_UP){
                        if (isSendF3==true)
                        {
                            writeOutputStream("F");
                            setTextValue("F");
                        }
                    }
                    break;
                case R.id.button04:
                    if (event.getAction()==MotionEvent.ACTION_DOWN){
                        writeOutputStream(buttonValue04);
                        setTextValue(buttonValue04);
                        textView_sendName.setText(buttonName04);
                    }
                    if(event.getAction()==MotionEvent.ACTION_UP){
                        if (isSendF4==true)
                        {
                            writeOutputStream("F");
                            setTextValue("F");
                        }
                    }
                    break;
                case R.id.button05:
                    if (event.getAction()==MotionEvent.ACTION_DOWN){
                        writeOutputStream(buttonValue05);
                        setTextValue(buttonValue05);
                        textView_sendName.setText(buttonName05);
                    }
                    if(event.getAction()==MotionEvent.ACTION_UP){
                        if (isSendF5==true)
                        {
                            writeOutputStream("F");
                            setTextValue("F");
                        }
                    }
                    break;
                case R.id.button06:
                    if (event.getAction()==MotionEvent.ACTION_DOWN){
                        writeOutputStream(buttonValue06);
                        setTextValue(buttonValue06);
                        textView_sendName.setText(buttonName06);
                    }
                    if(event.getAction()==MotionEvent.ACTION_UP){
                        if (isSendF6==true)
                        {
                            writeOutputStream("F");
                            setTextValue("F");
                        }
                    }
                    break;
                case R.id.imageSend:
                    if (event.getAction()==MotionEvent.ACTION_UP) {
                        if (editTextSend!=null) {
                            writeOutputStream(editTextSend.getText().toString().trim());
                            setTextValue(editTextSend.getText().toString());
                            textView_sendName.setText("发送区");
                            showToast("已发送");
                        }
                    }
                    break;


            }

        }
        else
        {
            showToast("请先连接");
        }
        return true;
    }

    private void saveData()
    {
         sharedPreferences=getSharedPreferences("ValueAndName",0);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString("nameUp",nameUp);
        editor.putString("nameDown",nameDown);
        editor.putString("nameLeft",nameLeft);
        editor.putString("nameRight",nameRight);
        editor.putString("nameStop",nameStop);
        editor.putString("valueUp",valueUp);
        editor.putString("valueDown",valueDown);
        editor.putString("valueLeft",valueLeft);
        editor.putString("valueRight",valueRight);
        editor.putString("valueStop",valueStop);

        editor.putString("buttonName01",buttonName01);
        editor.putString("buttonName02",buttonName02);
        editor.putString("buttonName03",buttonName03);
        editor.putString("buttonName04",buttonName04);
        editor.putString("buttonName05",buttonName05);
        editor.putString("buttonName06",buttonName06);
        editor.putString("buttonValue01",buttonValue01);
        editor.putString("buttonValue02",buttonValue02);
        editor.putString("buttonValue03",buttonValue03);
        editor.putString("buttonValue04",buttonValue04);
        editor.putString("buttonValue05",buttonValue05);
        editor.putString("buttonValue06",buttonValue06);


        editor.apply();
    }

    private void readData()
    {

         sharedPreferences=getSharedPreferences("Radio",0);
        radioID=sharedPreferences.getInt("position", R.id.plan1);


        sharedPreferences=getSharedPreferences("ValueAndName"+radioID,0);

        nameUp=sharedPreferences.getString("nameUp","前进");
        nameDown=sharedPreferences.getString("nameDown","后退");
        nameLeft=sharedPreferences.getString("nameLeft","左转");
        nameRight=sharedPreferences.getString("nameRight","右转");
        nameStop=sharedPreferences.getString("nameStop","停止");
        valueUp=sharedPreferences.getString("valueUp","A");
        valueDown=sharedPreferences.getString("valueDown","B");
        valueLeft=sharedPreferences.getString("valueLeft","C");
        valueRight=sharedPreferences.getString("valueRight","D");
        valueStop=sharedPreferences.getString("valueStop","F");

        buttonName01=sharedPreferences.getString("buttonName01","01");
        buttonName02=sharedPreferences.getString("buttonName02","02");
        buttonName03=sharedPreferences.getString("buttonName03","03");
        buttonName04=sharedPreferences.getString("buttonName04","04");
        buttonName05=sharedPreferences.getString("buttonName05","05");
        buttonName06=sharedPreferences.getString("buttonName06","06");
        buttonValue01=sharedPreferences.getString("buttonValue01","1");
        buttonValue02=sharedPreferences.getString("buttonValue02","2");
        buttonValue03=sharedPreferences.getString("buttonValue03","3");
        buttonValue04=sharedPreferences.getString("buttonValue04","4");
        buttonValue05=sharedPreferences.getString("buttonValue05","5");
        buttonValue06=sharedPreferences.getString("buttonValue06","6");
        isSendF1=sharedPreferences.getBoolean("switchCompat1",false);
        isSendF2=sharedPreferences.getBoolean("switchCompat2",false);
        isSendF3=sharedPreferences.getBoolean("switchCompat3",false);
        isSendF4=sharedPreferences.getBoolean("switchCompat4",false);
        isSendF5=sharedPreferences.getBoolean("switchCompat5",false);
        isSendF6=sharedPreferences.getBoolean("switchCompat6",false);

    }

    private class ConstraintLayoutOnTouchListener implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            constraintLayout.requestFocus();
            inputMethodManager.hideSoftInputFromWindow(editTextSend.getWindowToken(),0);
            return false;
        }
    }

    private class textViewReceiveOnLongClickListener implements View.OnLongClickListener
    {
        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("操作接收区数据").setIcon(R.drawable.textdialogtitle).setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which)
                    {
                        case 0:
                            Intent intent =new Intent(Intent.ACTION_SEND);
                            ClipboardManager clipboardManager=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboardManager.setPrimaryClip(ClipData.newPlainText("data",textViewReceive.getText().toString().trim()));
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT,textViewReceive.getText().toString().trim());
                            startActivity(Intent.createChooser(intent,getTitle()));
                            break;
                        case 1:

                            new android.app.AlertDialog.Builder(MainActivity.this).setIcon(R.drawable.clean)
                                    .setTitle("确定清空？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    textViewReceive.setText(null);
                                }
                            }).show();
                            break;
//                        case 2:
////                            showToast("暂无此功能，等下次更新吧~");
//                            receiveHexFlag=!receiveHexFlag;
//                            if (receiveHexFlag)charSequences[2]="切换为字符接收";
//                            else charSequences[2]="切换为十六进制接收";
//                            break;
                        case 2:
                            receiveSelectFlag=!receiveSelectFlag;
                            textViewReceive.setTextIsSelectable(receiveSelectFlag);
                            break;
                    }
                }
            }).show();


            return false;
        }
    }

    public void onClick_intent(View view)
    {
        if (view.getId()==R.id.imageIntent)
        {
            Intent intent=new Intent(MainActivity.this,DeviceActivity.class);
            startActivity(intent);
        }
        if (view.getId()==R.id.textView_device)
        {
            Intent intent=new Intent(MainActivity.this,DeviceActivity.class);
            startActivity(intent);
        }
    }

//    public void onClick(View view)
//    {
//        inputMethodManager.hideSoftInputFromWindow(editTextSend.getWindowToken(),0);
//        constraintLayout.requestFocus();
//
//        if (DeviceActivity.isConnect) {
//            switch (view.getId())
//            {
//
//                case R.id.button01:
//                        writeOutputStream(buttonValue01);
//                        textView_sendName.setText(buttonName01);
//                        setTextValue(buttonValue01);
//                    break;
//                case R.id.button02:
//                        writeOutputStream(buttonValue02);
//                        textView_sendName.setText(buttonName02);
//                        setTextValue(buttonValue02);
//                    break;
//                case R.id.button03:
//                        writeOutputStream(buttonValue03);
//                        textView_sendName.setText(buttonName03);
//                        setTextValue(buttonValue03);
//                    break;
//                case R.id.button04:
//                    if (editTextSend!=null) {
//                        writeOutputStream(buttonValue04);
//                        textView_sendName.setText(buttonName04);
//                        setTextValue(buttonValue04);
//                    }
//                    break;
//                case R.id.button05:
//                    if (editTextSend!=null) {
//                        writeOutputStream(buttonValue05);
//                        textView_sendName.setText(buttonName05);
//                        setTextValue(buttonValue05);
//                    }
//                    break;
//                case R.id.button06:
//                    if (editTextSend!=null) {
//                        writeOutputStream(buttonValue06);
//                        textView_sendName.setText(buttonName06);
//                        setTextValue(buttonValue06);
//                    }
//                    break;
//
//            }
//        } else {
//            showToast("请先连接");
//        }
//    }

    private class SwitchOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked)
            {
                flag_switch=true;
                if(DeviceActivity.isConnect)
                {
                    new ReceiveThread().start();
                    try {
                        inputStream=DeviceActivity.clientSocket.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    switchReceive.setChecked(false);
                    showToast("请先连接");
                }
            }
        }
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener=new Toolbar.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.value:
                    Intent intent1=new Intent(MainActivity.this,KeyValueActivity.class);
                    startActivity(intent1);

                    break;
                case R.id.ui:
                    Intent intent2=new Intent(MainActivity.this,CarActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.howUse:
                    Intent intent3=new Intent(MainActivity.this,HowUseActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.about:
                    Intent intent4=new Intent(MainActivity.this,AboutActivity.class);
                    startActivity(intent4);
                    break;
            }
            return true;
        }
    };

    private void initState()
    {
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
    private int getStatusHeight()
    {
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

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String perACCESS_FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
            if (ActivityCompat.checkSelfPermission(MainActivity.this, perACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void filterInit()
    {
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(disConnectReceiver,filter);
    }

    private void isBLE() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showToast("你手机的蓝牙不是低功耗蓝牙");
        } else {
            showToast("你手机的蓝牙是低功耗蓝牙");
        }
    }

    private void showToast(String s) {
        if (mToast == null) {
            mToast = Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(s);
        }
        mToast.show();
    }

    private void writeOutputStream(String m) {
        try {
            outputStream = DeviceActivity.clientSocket.getOutputStream();
        } catch (IOException e) {
            showToast("a");
            e.printStackTrace();
        }
        byte[] buffer;

        if(toggleButton.isChecked())
        {
            byte[] hex =hexStr2byte(m);
            buffer = hex;
        }
        else
        {
            buffer=m.getBytes();
        }

        try {
            outputStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private byte[] hexStr2byte(String hexStr)
    {
        StringBuffer stringBuffer =new StringBuffer(hexStr);
        if(hexStr.length()%2==1)
        {

//            stringBuffer.insert(hexStr.length()-1,"0");
//            hexStr=stringBuffer.toString();
            hexStr+="0";
        }
        hexStr=hexStr.toUpperCase();
        hexStr=hexStr.trim();
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++)
        {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return bytes;
    }

    private Handler textHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String string;
            string=msg.obj.toString();
//            if(receiveHexFlag){
//                string=str2HexStr(string);
//            }
            textViewReceive.append(string);
            int offset=textViewReceive.getLineCount()*textViewReceive.getLineHeight();
            if (offset>textViewReceive.getLineHeight())
            {
                textViewReceive.scrollTo(0,offset-textViewReceive.getHeight());
            }
        }
    };

    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            super.run();
//            showToast("线程开始");
            try {
                while (true) {
                    Thread.sleep(200);
//                    showToast("线程没有被中断");

                    try {
                        inputStreamBuffer = new byte[1024];
                        if (switchReceive.isChecked()) {
                            inputStream.read(inputStreamBuffer);
                        }
                        if (flag_switch){
                            inputStreamBuffer=new byte[1024];
                            flag_switch=false;
                        }
                        if (!switchReceive.isChecked())
                        {
                            break;
                        }
                        Message msg = textHandler.obtainMessage();
                        msg.obj = new String(inputStreamBuffer);
//                        inputStreamBuffer=null;
                        textHandler.sendMessage(msg);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("tag", "2");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();

            }

//            showToast("线程中断");
        }
    }

    public final BroadcastReceiver disConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                DeviceActivity.isConnect = false;
                switchReceive.setChecked(false);
//                try {
//                    clientSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                imageIntent.setImageResource(R.drawable.notconnect);
                showToast("连接断开");
            }

        }
    };
}
