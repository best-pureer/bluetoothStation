package com.hz.bluetoothstation;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class HowUseActivity extends SwipeBackActivity {
    Toolbar howUseToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_use);

        howUseToolbar = (Toolbar) findViewById(R.id.howUseToolbar);


        howUseToolbar.setNavigationIcon(R.drawable.back);
        howUseToolbar.setNavigationOnClickListener(new NavigationOnClickListener());
        howUseToolbar.setTitle("使用说明");

        initState();
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
