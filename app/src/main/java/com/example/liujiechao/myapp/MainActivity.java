package com.example.liujiechao.myapp;


import android.app.Activity;
import android.app.LedDemoServiceManager;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private Button btnNo;
    private Button btnOFF;
    private Button btnUpgrade;
    private LedDemoServiceManager ledDemoServiceManager;

    private Handler mHandler;
    LinearLayout mLinearLayout;
    ScrollView mScrollView;
    private static int index;
    TextSwitcher mTextSwitcher;
    String mScrollText[] = {"额嗯嗯嗯嗯", "是的风格广告", "就隐隐约约", "sdfsgsg"};
    private int mCurIndex;
    ImageView mUpgradeApkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNo =  (Button)findViewById(R.id.led_no);
        btnOFF =  (Button)findViewById(R.id.led_off);
        btnUpgrade = (Button)findViewById(R.id.upgrade);
        btnUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RecoverySystem.installPackage(MainActivity.this, new File("/data/upgrade.zip"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        metaDataTest();
//        LedTest();

        Intent intent = new Intent();
        intent.putExtra("fuck", "shit");
        int string =  intent.getIntExtra("fuck", -1);
        Log.d("liujc", "getExtra:"+string);

        mScrollView = (ScrollView)findViewById(R.id.scroll);
        mLinearLayout = (LinearLayout)findViewById(R.id.linear);
        mHandler = new MyHandler();
//        mHandler.sendEmptyMessage(0);

        testPasteFile();

//        showScrollMsg();

//        testScrollMsgOnStatusBar();
//        mHandler.sendEmptyMessageDelayed(2, 60*1000);

        getGalleryNewPic();

        copySdcardFileToDataDir();
        showUpgradeApkView();
    }

    private void showUpgradeApkView() {
        Log.d("liujc", "showUpgradeApkView enter");
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        ViewGroup.LayoutParams lp = new WindowManager.LayoutParams();
        mUpgradeApkView =  new ImageView(this);
        mUpgradeApkView.setImageResource(R.drawable.ic_launcher);
        wm.addView(mUpgradeApkView, lp);
        Log.d("liujc", "showUpgradeApkView exit");
    }

    private void getGalleryNewPic() {
        String path = null;
        String DCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        Cursor mCursor = getContentResolver().query(MediaStore.Files.getContentUri("external"),
                new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA},
                "(media_type=1 or media_type=3)" + " AND " + MediaStore.Images.Media.DATA + " LIKE '" + DCIM + "%' ",
                null,
                MediaStore.Images.Media.DATE_TAKEN + " DESC"); // 降序排列
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                Log.d("liujc", "path=" + path);
            }
            mCursor.close();
        }
    }

    private void testPasteFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //add by liujc

                Log.e("liujc", "liujc----test begin");
                File dataFile =  Environment.getDataDirectory();
                File srcFile = new File("/system/lib/libanchor3jni_x86.so");
                File decFile = new File(dataFile, "/data/com.moliplayer.android.tv/files/appdata/libs/libanchor3jni_x86.so");


                //add by liujc for poweroff led effects
                try {
                    Runtime.getRuntime().exec(new String[]{"/system/bin/chmod", " 777", "/data/data/com.moliplayer.android.tv"});
                }catch (IOException er){
                    Log.d("liujc", "powerLongPress Runtime.getRuntime().exec IOException:"+er);
                }//end by liujc

                //if(!decFile.mkdirs())Slog.e(TAG, "liujc----decFile mkdirs failed");
                if(srcFile.exists())Log.e("liujc", "liujc----srcFile exists");
                if(srcFile.exists()){
                    Log.e("liujc", "liujc----pasteFile");
                    pasteFile(srcFile, decFile);
                }
                //end by liujc
            }
        }).start();
    }

    private void copySdcardFileToDataDir(){
        File dir = Environment.getExternalStorageDirectory(); // /storage/emulated/0
        File srcFile = new File(dir, "upgrade.zip");
        File desFile = new File("/data/upgrade.zip");
        if(srcFile.exists()){
            pasteFile(srcFile, desFile);
        }
    }

    private void showScrollMsg() {
        mTextSwitcher = (TextSwitcher)findViewById(R.id.text_switch);
        mTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                Log.d("liujc", "liujc----makeView");
                TextView textView = new TextView(MainActivity.this);
                textView.setTextColor(Color.YELLOW);
                textView.setTextSize(20);
                return textView;
            }
        });
        long animDuration = 1000;
        Animation in = new TranslateAnimation(0, 0, animDuration, 0);//设置上下滚动的动画
        in.setDuration(animDuration);
        in.setInterpolator(new AccelerateInterpolator());
        Animation out = new TranslateAnimation(0, 0, 0, -animDuration);
        out.setDuration(animDuration);
        out.setInterpolator(new AccelerateInterpolator());
        mTextSwitcher.setInAnimation(in);
        mTextSwitcher.setOutAnimation(out);
        mHandler.sendEmptyMessageDelayed(1, 2000);
    }


    private void testScrollMsgOnStatusBar() {
        List<String> list = new ArrayList<>();
        list.add("额嗯嗯嗯嗯嗯嗯");
        list.add("额嗯嗯s水水水水水水");
        list.add("\"广告个反而风格个反而反而反而发\"");
        list.add("地方地方是否额嗯嗯嗯嗯嗯嗯广告个反而风格个反而反而反而发地方地方是否额嗯嗯嗯嗯嗯嗯地方地方是否额嗯嗯嗯嗯嗯嗯");

        StatusBarManager mStatusBarManager = (StatusBarManager)getSystemService(Context.STATUS_BAR_SERVICE);

        mStatusBarManager.showScrollMessageOnStatusBar(list, 5000);

    }

    private void testfun(){
        int i;
        for(i=0; i<=23; i++){
            int j = (i*10)/15+1;

            Log.d("liujc", "i:"+i+"-->:"+j);
        }
    }

    private void LedTest(){

        ledDemoServiceManager = (LedDemoServiceManager)getSystemService(Context.LED_SERVICE);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ledDemoServiceManager.led_set_colors(2,1);
                //ledDemoServiceManager.led_set_brightness(2, 1);
                //testfun();
                try {
                    Log.d("liujc", " begin led 000");
                   // Process p = Runtime.getRuntime().exec("adb shell");//("echo 1 >/sys/class/leds/led0/led_bootingstatus");
                    Runtime.getRuntime().exec(new String[]{"/system/bin/sh","-c", "echo 1 >/sys/class/leds/led0/led_bootingstatus"});
                    Log.d("liujc", " end led 000");
                }catch (IOException er){
                    Log.d("liujc", " 00 er:"+er);
                }
            }
        });

        btnOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ledDemoServiceManager.led_set_colors(2,1);
                ledDemoServiceManager.led_set_brightness(2, 0);
            }
        });
    }

    private void pasteFile(File srcFile, File desFile) {
        try {
            FileInputStream input = new FileInputStream(srcFile);
            FileOutputStream output = new FileOutputStream(desFile);
            byte[] b = new byte[16 * 1024];
            int len;

            try {
                while ((len = input.read(b)) != -1) {
                    output.write(b, 0, len);
                }
            } catch (IOException e) {
                Log.d("liujc", "11 er:"+e);
            } finally {
                output.flush();
                output.close();
                input.close();
            }

        }catch (Exception e){
            Log.d("liujc", "22 er:"+e);
        }
    }

    private void metaDataTest(){
        try {
            Bundle bundle = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA).metaData;
            if(bundle == null)
            {
                Log.d("liujc", "bundle == null");
            }
            else {
                String  icon =  bundle.get("is_show_icon").toString();
                String  lable =  bundle.get("is_show_lable").toString();
                Log.d("liujc", "is_show_lable:" + lable+", is_show_icon:"+icon);
            }
        }catch (Exception e){
            Log.d("liujc", "e:"+e);
        }
    }

    private void addScrollViewContent(){
        mHandler.sendEmptyMessageDelayed(0, 1000);
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        textView.setText("aaadddddd"+ index);
        index++;
        mLinearLayout.addView(textView);
        int off = mLinearLayout.getMeasuredHeight() - mScrollView.getHeight();
        if (off > 0)
        {
            mScrollView.scrollTo(0, off);
        }
    }


    class  MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case 0:
                    addScrollViewContent();
                    break;
                case 1:
                    mTextSwitcher.setText(mScrollText[mCurIndex++]);
                    if (mCurIndex == mScrollText.length) mCurIndex = 0;
                    mHandler.sendEmptyMessageDelayed(1, 2000);
                    break;
                case 2:
                    StatusBarManager mStatusBarManager = (StatusBarManager)getSystemService(Context.STATUS_BAR_SERVICE);
                    mStatusBarManager.hideScrollMessageOnStatusBar();
                    break;
                default:
            }

        }
    }

}
