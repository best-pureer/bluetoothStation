package com.hz.bluetoothstation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class AboutActivity extends SwipeBackActivity implements View.OnClickListener{
    Toolbar settingToolbar;
    TextView version,baidumarket,coolMarket,school,code,another;
    SpannableString spannableString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        settingToolbar = (Toolbar) findViewById(R.id.settingToolbar);
        version = (TextView) findViewById(R.id.version);
        coolMarket=(TextView)findViewById(R.id.coolMarket);
        baidumarket=(TextView)findViewById(R.id.baiduMarket);
        another=(TextView)findViewById(R.id.another);
        code=(TextView)findViewById(R.id.code);
        school=(TextView)findViewById(R.id.school);


        settingToolbar.setNavigationIcon(R.drawable.back);
        settingToolbar.setNavigationOnClickListener(new NavigationOnClickListener());
        settingToolbar.setTitle("关于软件");
        coolMarket.setOnClickListener(this);
        baidumarket.setOnClickListener(this);
        another.setOnClickListener(this);

        initState();
        initVersion();
        initMarket();
        initAnother();
        initCode();
        initSchool();
    }



    private void initVersion()
    {
        version.append(getVersion(this));
    }

    private void initMarket()
    {
        spannableString=new SpannableString(coolMarket.getText());
        spannableString.setSpan(new URLSpan(""),2,4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        coolMarket.setText(spannableString);

        spannableString=new SpannableString(baidumarket.getText());
        spannableString.setSpan(new URLSpan(""),2,8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        baidumarket.setText(spannableString);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.coolMarket:
                openCoolMarket(getPackageName(),"com.coolapk.market",v.getContext());
                break;
            case R.id.baiduMarket:
                String url="http://shouji.baidu.com/software/21814036.html";
                Intent intent=new Intent(Intent.ACTION_VIEW);
                openLink(this,intent,url,false);
                break;
            case R.id.another:


                try {
                    Intent localIntent = new Intent(Intent.ACTION_VIEW);
                    String anotherUrl= "market://details?id=com.hz.www.shanxunsimple" ;
                    String marketPackageName="com.coolapk.market";
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    localIntent.setPackage(marketPackageName);
                    openLink(this,localIntent,anotherUrl,true);
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent ww = new Intent(Intent.ACTION_VIEW);
                    String qq="http://shouji.baidu.com/software/11227845.html";
                    openLink(this,ww,qq,false);

                }
                break;
        }
    }

    private void initAnother()
    {
        spannableString=new SpannableString(another.getText());
        spannableString.setSpan(new URLSpan("http://shouji.baidu.com/software/11227845.html"),0,7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        another.setText(spannableString);
//        another.setMovementMethod(LinkMovementMethod.getInstance());
    }
    private void initCode()
    {
        spannableString=new SpannableString(code.getText());
        spannableString.setSpan(new URLSpan("http://git.oschina.net/puren/BluetoothStation"),0,4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        code.setText(spannableString);
        code.setMovementMethod(LinkMovementMethod.getInstance());
    }
    private void initSchool()
    {
        spannableString=new SpannableString(school.getText());
        spannableString.setSpan(new URLSpan("http://www.tjzj.edu.cn/"),0,8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        school.setText(spannableString);
        school.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private String getVersion(Context context){
        String versionName="";
        PackageManager manager=context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionName=info.versionName;
        return versionName;
    }

    private void openCoolMarket(String appPackageName, String marketPackageName, Context context) {
        try {
            String url = "market://details?id=" + appPackageName;
            Intent localIntent = new Intent(Intent.ACTION_VIEW);

            if (marketPackageName != null) {
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setPackage(marketPackageName);
            }
            openLink(context,localIntent,url,true);
        } catch (Exception e) {
            e.printStackTrace();
            //openCoolMarketForLink(appPackageName,context);
            Toast.makeText(context, "没有酷安APP", Toast.LENGTH_SHORT).show();
        }
    }


    private void openCoolMarketForLink(String packageName, Context context)
    {
        String url="http://www.coolapk.com/apk/" + packageName;
        Intent intent=new Intent(Intent.ACTION_VIEW);
        openLink(context,intent,url,false);
    }



    private void openLink(Context context, Intent intent,String link,boolean isThrowException)
    {
        if(intent==null)intent=new Intent(Intent.ACTION_VIEW);
        try {
            intent.setData(Uri.parse(link));
            context.startActivity(intent);
        } catch (Exception e) {
            if (isThrowException){throw e;}
            else {
                e.printStackTrace();
//                Toast.makeText(context,"无法打开",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class NavigationOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            onBackPressed();
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
}
