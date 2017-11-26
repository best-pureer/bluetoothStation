package com.hz.bluetoothstation;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class KeyValueActivity extends SwipeBackActivity implements View.OnClickListener, View.OnLongClickListener {

    Toolbar keyValueToolbar;
    Button edit, add, delete;
    AlertDialog.Builder builder;
    RadioGroup radioGroup;
    String planName;
    RadioButton newRadio;
    public final static String DATA_URL="/data/data/";
    SharedPreferences sharedPreferences;
    private View view;
    private LayoutInflater layoutInflater;
    private EditText nameUp, nameDown, nameLeft, nameRight,nameStop, valueUp, valueDown, valueLeft, valueRight,valueStop;
    private EditText nameButton01, nameButton02, nameButton03, nameButton04, nameButton05, nameButton06;
    private EditText valueButton01, valueButton02, valueButton03, valueButton04, valueButton05, valueButton06;
    private SwitchCompat switchCompat1,switchCompat2,switchCompat3,switchCompat4,switchCompat5,switchCompat6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_value);

        layoutInflater = LayoutInflater.from(this);

        keyValueToolbar = (Toolbar) findViewById(R.id.keyValueToolbar);
        edit = (Button) findViewById(R.id.edit);
        add = (Button) findViewById(R.id.add);
        delete = (Button) findViewById(R.id.delete);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);


        keyValueToolbar.setNavigationIcon(R.drawable.back);
        keyValueToolbar.setNavigationOnClickListener(new KeyValueActivity.NavigationOnClickListener());
        keyValueToolbar.setTitle("键值设置");
        edit.setOnClickListener(this);
        add.setOnClickListener(this);
        delete.setOnClickListener(this);

        initState();
        readRadio();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveRadio();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit:
//                Toast.makeText(this, radioGroup.getCheckedRadioButtonId() + "", Toast.LENGTH_SHORT).show();
                int i;
                findID();
                if ( radioGroup.getCheckedRadioButtonId() == R.id.plan1)
                    Toast.makeText(this, "默认方案不可更改,更改无效", Toast.LENGTH_SHORT).show();
                else readText(radioGroup.getCheckedRadioButtonId());
                showMyDialog();
                break;
            case R.id.add:
                addRadio();
                break;
            case R.id.delete:
                Toast.makeText(this, "暂无此功能，等下次更新吧", Toast.LENGTH_SHORT).show();

//                deleteRadio(radioGroup.getCheckedRadioButtonId());
                break;
        }
    }

    private void addRadio() {
        final EditText editText = new EditText(this);

        newRadio = new RadioButton(this);
        editText.setText("自定义方案" + (radioGroup.getChildCount()+1) + "");
        editText.setSelectAllOnFocus(true);
        new AlertDialog.Builder(this).setTitle("请输入新方案的名字").setIcon(R.drawable.textdialogtitle).setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        planName = editText.getText().toString();
                        if (planName.equals("")) planName = "自定义方案" + (radioGroup.getChildCount()+1);
                        newRadio.setText(planName);
                        newRadio.setId(radioGroup.getChildCount()+1);
                        radioGroup.addView(newRadio);
                        radioGroup.check(radioGroup.getChildCount());
                        findID();
                        showMyDialog();
                        sharedPreferences = getSharedPreferences("Radio", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("radioName" + (radioGroup.getCheckedRadioButtonId() - 1), newRadio.getText().toString());
                        editor.apply();

                    }
                }).show();


    }

    private void deleteRadio(int planId)
    {

//        File file=new File(DATA_URL+getPackageName()+"/shared_prefs","ValueAndName"+(planId-1));
//        if(file.exists())
//        {
//            file.delete();
//            Log.i("tag","deleteComplete");
//        }
//        sharedPreferences=getSharedPreferences("Radio",0);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove("radioName"+(planId-1));
//        editor.apply();

        radioGroup.removeViewAt((planId-1));

        radioGroup.check(planId+1);
        Toast.makeText(this, radioGroup.getCheckedRadioButtonId()+"", Toast.LENGTH_SHORT).show();
    }

    private void saveRadio() {
        sharedPreferences = getSharedPreferences("Radio", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("position", radioGroup.getCheckedRadioButtonId());
        editor.putInt("radioCount", radioGroup.getChildCount());
        editor.apply();
    }

    private void readRadio() {
        sharedPreferences = getSharedPreferences("Radio", 0);

        for (int i = 1; i < sharedPreferences.getInt("radioCount", 1) ; i++) {
            newRadio = new RadioButton(this);
            newRadio.setId(i+1);
            newRadio.setText(sharedPreferences.getString("radioName" + i , "自定义方案" + i));
            radioGroup.addView(newRadio);
        }
        radioGroup.check(sharedPreferences.getInt("position", R.id.plan1));

    }

    private void findID() {
        view = layoutInflater.inflate(R.layout.key_value_list, null);
        nameUp = (EditText) view.findViewById(R.id.nameUp);
        nameDown = (EditText) view.findViewById(R.id.nameDown);
        nameLeft = (EditText) view.findViewById(R.id.nameLeft);
        nameRight = (EditText) view.findViewById(R.id.nameRight);
        nameStop = (EditText) view.findViewById(R.id.nameStop);
        valueUp = (EditText) view.findViewById(R.id.up);
        valueDown = (EditText) view.findViewById(R.id.down);
        valueLeft = (EditText) view.findViewById(R.id.left);
        valueRight = (EditText) view.findViewById(R.id.right);
        valueStop = (EditText) view.findViewById(R.id.stop);

        nameButton01 = (EditText) view.findViewById(R.id.nameButton01);
        nameButton02 = (EditText) view.findViewById(R.id.nameButton02);
        nameButton03 = (EditText) view.findViewById(R.id.nameButton03);
        nameButton04 = (EditText) view.findViewById(R.id.nameButton04);
        nameButton05 = (EditText) view.findViewById(R.id.nameButton05);
        nameButton06 = (EditText) view.findViewById(R.id.nameButton06);
        valueButton01 = (EditText) view.findViewById(R.id.valueButton01);
        valueButton02 = (EditText) view.findViewById(R.id.valueButton02);
        valueButton03 = (EditText) view.findViewById(R.id.valueButton03);
        valueButton04 = (EditText) view.findViewById(R.id.valueButton04);
        valueButton05 = (EditText) view.findViewById(R.id.valueButton05);
        valueButton06 = (EditText) view.findViewById(R.id.valueButton06);
        switchCompat1=(SwitchCompat) view.findViewById(R.id.switch1);
        switchCompat2=(SwitchCompat)view.findViewById(R.id.switch2);
        switchCompat3=(SwitchCompat)view.findViewById(R.id.switch3);
        switchCompat4=(SwitchCompat)view.findViewById(R.id.switch4);
        switchCompat5=(SwitchCompat)view.findViewById(R.id.switch5);
        switchCompat6=(SwitchCompat)view.findViewById(R.id.switch6);
    }

    private void showMyDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("键名与键值").setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveText(radioGroup.getCheckedRadioButtonId());
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void saveText(int planId) {
        sharedPreferences = getSharedPreferences("ValueAndName" + planId, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nameUp", nameUp.getText().toString());
        editor.putString("nameDown", nameDown.getText().toString());
        editor.putString("nameLeft", nameLeft.getText().toString());
        editor.putString("nameRight", nameRight.getText().toString());
        editor.putString("nameStop", nameStop.getText().toString());
        editor.putString("valueUp", valueUp.getText().toString());
        editor.putString("valueDown", valueDown.getText().toString());
        editor.putString("valueLeft", valueLeft.getText().toString());
        editor.putString("valueRight", valueRight.getText().toString());
        editor.putString("valueStop", valueStop.getText().toString());

        editor.putString("buttonName01", nameButton01.getText().toString());
        editor.putString("buttonName02", nameButton02.getText().toString());
        editor.putString("buttonName03", nameButton03.getText().toString());
        editor.putString("buttonName04", nameButton04.getText().toString());
        editor.putString("buttonName05", nameButton05.getText().toString());
        editor.putString("buttonName06", nameButton06.getText().toString());
        editor.putString("buttonValue01", valueButton01.getText().toString());
        editor.putString("buttonValue02", valueButton02.getText().toString());
        editor.putString("buttonValue03", valueButton03.getText().toString());
        editor.putString("buttonValue04", valueButton04.getText().toString());
        editor.putString("buttonValue05", valueButton05.getText().toString());
        editor.putString("buttonValue06", valueButton06.getText().toString());
        editor.putBoolean("switchCompat1",switchCompat1.isChecked());
        editor.putBoolean("switchCompat2",switchCompat2.isChecked());
        editor.putBoolean("switchCompat3",switchCompat3.isChecked());
        editor.putBoolean("switchCompat4",switchCompat4.isChecked());
        editor.putBoolean("switchCompat5",switchCompat5.isChecked());
        editor.putBoolean("switchCompat6",switchCompat6.isChecked());
        if ( radioGroup.getCheckedRadioButtonId() != R.id.plan1)
            editor.apply();

    }

    private void readText(int planId) {
        sharedPreferences = getSharedPreferences("ValueAndName" + planId, 0);
        nameUp.setText(sharedPreferences.getString("nameUp", "前进"));
        nameDown.setText(sharedPreferences.getString("nameDown", "后退"));
        nameLeft.setText(sharedPreferences.getString("nameLeft", "左转"));
        nameRight.setText(sharedPreferences.getString("nameRight", "右转"));
        nameStop.setText(sharedPreferences.getString("nameStop", "停止"));
        valueUp.setText(sharedPreferences.getString("valueUp", "A"));
        valueDown.setText(sharedPreferences.getString("valueDown", "B"));
        valueLeft.setText(sharedPreferences.getString("valueLeft", "C"));
        valueRight.setText(sharedPreferences.getString("valueRight", "D"));
        valueStop.setText(sharedPreferences.getString("valueStop", "F"));

        nameButton01.setText(sharedPreferences.getString("buttonName01", "01"));
        nameButton02.setText(sharedPreferences.getString("buttonName02", "02"));
        nameButton03.setText(sharedPreferences.getString("buttonName03", "03"));
        nameButton04.setText(sharedPreferences.getString("buttonName04", "04"));
        nameButton05.setText(sharedPreferences.getString("buttonName05", "05"));
        nameButton06.setText(sharedPreferences.getString("buttonName06", "06"));
        valueButton01.setText(sharedPreferences.getString("buttonValue01", "1"));
        valueButton02.setText(sharedPreferences.getString("buttonValue02", "2"));
        valueButton03.setText(sharedPreferences.getString("buttonValue03", "3"));
        valueButton04.setText(sharedPreferences.getString("buttonValue04", "4"));
        valueButton05.setText(sharedPreferences.getString("buttonValue05", "5"));
        valueButton06.setText(sharedPreferences.getString("buttonValue06", "6"));
        switchCompat1.setChecked(sharedPreferences.getBoolean("switchCompat1",false));
        switchCompat2.setChecked(sharedPreferences.getBoolean("switchCompat2",false));
        switchCompat3.setChecked(sharedPreferences.getBoolean("switchCompat3",false));
        switchCompat4.setChecked(sharedPreferences.getBoolean("switchCompat4",false));
        switchCompat5.setChecked(sharedPreferences.getBoolean("switchCompat5",false));
        switchCompat6.setChecked(sharedPreferences.getBoolean("switchCompat6",false));

    }


    private class NavigationOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    }

    private void initState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(0xff2196f3);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            LinearLayout linear_bar = (LinearLayout) findViewById(R.id.ll_bar);
            linear_bar.setVisibility(View.VISIBLE);

            int statusHeight = getStatusHeight();
            ViewGroup.LayoutParams params = linear_bar.getLayoutParams();
            params.height = statusHeight;
            linear_bar.setLayoutParams(params);

        }
    }

    private int getStatusHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
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
