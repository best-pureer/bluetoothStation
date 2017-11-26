package com.hz.bluetoothstation;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class CarActivity extends SwipeBackActivity implements View.OnTouchListener{
    private Toolbar carToolbar;
    private Toast mToast;
    private TextView textView_device;
    private ImageView imageIntent;
//    private Button button01,button02,button03,button04,button05,button06;
    private ImageView up,down,left,right;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);
        carToolbar=(Toolbar)findViewById(R.id.carToolbar);
        textView_device=(TextView)findViewById(R.id.textView_device);
        imageIntent=(ImageView)findViewById(R.id.imageIntent);

        up=(ImageView)findViewById(R.id.up);
        down=(ImageView)findViewById(R.id.down);
        left=(ImageView)findViewById(R.id.left);
        right=(ImageView)findViewById(R.id.right);
//        button01=(Button)findViewById(R.id.button01);
//        button02=(Button)findViewById(R.id.button02);
//        button03=(Button)findViewById(R.id.button03);
//        button04=(Button)findViewById(R.id.button04);
//        button05=(Button)findViewById(R.id.button05);
//        button06=(Button)findViewById(R.id.button06);
        up.setOnTouchListener(this);
        down.setOnTouchListener(this);
        left.setOnTouchListener(this);
        right.setOnTouchListener(this);
//        button01.setOnTouchListener(this);
//        button02.setOnTouchListener(this);
//        button03.setOnTouchListener(this);
//        button04.setOnTouchListener(this);
//        button05.setOnTouchListener(this);
//        button06.setOnTouchListener(this);

        carToolbar.inflateMenu(R.menu.main_toolbar_menu);
        carToolbar.setOnMenuItemClickListener(onMenuItemClickListener);

        initState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!DeviceActivity.isConnect) {
            textView_device.setText(R.string.device提示);
            imageIntent.setImageResource(R.drawable.notconnect);
        }
        else {
            imageIntent.setImageResource(R.drawable.connect);
            textView_device.setText(MainActivity.deviceName);
        }
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener=new Toolbar.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.value:
                    showToast("此界面键名键值固定，具体查看使用说明");
                    break;
                case R.id.ui:
                    onBackPressed();
                    break;
                case R.id.howUse:
                    Intent intent3=new Intent(CarActivity.this,HowUseActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.about:
                    Intent intent4=new Intent(CarActivity.this,AboutActivity.class);
                    startActivity(intent4);
                    break;
            }
            return true;
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (DeviceActivity.isConnect)
        {
            switch (v.getId())
            {
                case R.id.up:
                    if(event.getAction()==MotionEvent.ACTION_DOWN)
                    {
                        writeOutputStream("A");
                    }
                    break;
                case R.id.down:
                    if(event.getAction()==MotionEvent.ACTION_DOWN)
                    {
                        writeOutputStream("B");
                    }
                    break;
                case R.id.left:
                    if(event.getAction()==MotionEvent.ACTION_DOWN)
                    {
                        writeOutputStream("C");
                    }
                    break;
                case R.id.right:
                    if(event.getAction()==MotionEvent.ACTION_DOWN)
                    {
                        writeOutputStream("D");
                    }
                    break;
//                case R.id.button01:
//                    if (event.getAction()==MotionEvent.ACTION_DOWN)
//                    {
//                        writeOutputStream("1");
//                    }
//                    break;
//                case R.id.button02:
//                    if (event.getAction()==MotionEvent.ACTION_DOWN)
//                    {
//                        writeOutputStream("2");
//                    }
//                    break;
//                case R.id.button03:
//                    if (event.getAction()==MotionEvent.ACTION_DOWN)
//                    {
//                        writeOutputStream("3");
//                    }
//                    break;
//                case R.id.button04:
//                    if (event.getAction()==MotionEvent.ACTION_DOWN)
//                    {
//                        writeOutputStream("4");
//                    }
//                    break;
//                case R.id.button05:
//                    if (event.getAction()==MotionEvent.ACTION_DOWN)
//                    {
//                        writeOutputStream("5");
//                    }
//                    break;
//                case R.id.button06:
//                    if (event.getAction()==MotionEvent.ACTION_DOWN)
//                    {
//                        writeOutputStream("6");
//                    }
//                    break;

            }
            if(event.getAction()==MotionEvent.ACTION_UP)
            {
                writeOutputStream("F");
            }

        }
        else
        {
            showToast("请先连接");
        }
        return true;
    }

    public void onClick_intent(View view)
    {
        if (view.getId()==R.id.imageIntent)
        {
            Intent intent=new Intent(CarActivity.this,DeviceActivity.class);
            startActivity(intent);
        }
        if (view.getId()==R.id.textView_device)
        {
            Intent intent=new Intent(CarActivity.this,DeviceActivity.class);
            startActivity(intent);
        }
    }

    public void onClick(View view)
    {

        if (DeviceActivity.isConnect) {
            switch (view.getId())
            {

                case R.id.button01:
                        writeOutputStream("1");

                    break;
                case R.id.button02:
                        writeOutputStream("2");

                    break;
                case R.id.button03:
                        writeOutputStream("3");
                    break;
                case R.id.button04:
                        writeOutputStream("4");
                    break;
                case R.id.button05:
                        writeOutputStream("5");
                    break;
                case R.id.button06:
                        writeOutputStream("6");
                    break;

            }
        } else {
            showToast("请先连接");
        }
    }

    private void writeOutputStream(String m) {
        try {
            MainActivity.outputStream = DeviceActivity.clientSocket.getOutputStream();
        } catch (IOException e) {
            showToast("a");
            e.printStackTrace();
        }
        byte[] buffer;
        buffer = m.getBytes();

        try {
            MainActivity.outputStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void showToast(String s) {
        if (mToast == null) {
            mToast = Toast.makeText(CarActivity.this, s, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(s);
        }
        mToast.show();
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


}
