package com.stardust.autojs.apkbuilder.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.stardust.autojs.apkbuilder.ApkBuilder;

import java.io.File;
import java.io.InputStream;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements ApkBuilder.ProgressCallback {

    private EditText mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViews();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12345);
                return;
            }
        }
    }

    private void setUpViews() {
        setContentView(R.layout.activity_main);
        mFilePath = (EditText) findViewById(R.id.file_path);
        findViewById(R.id.build).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    buildApk();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void buildApk() throws Exception {
        String path = mFilePath.getText().toString();
        File tmp = new File(Environment.getExternalStorageDirectory(), "ApkBuild/");
        tmp.mkdirs();
        build(tmp, new File(path));
    }



    private void build(File tmpDir, File js) throws Exception {
        int ran =new Random().nextInt(1000);
        File outApk = new File(js.getParent(), js.getName() + ran+".apk");
        InputStream inApk = getAssets().open("template.apk");
        ApkBuilder apkBuilder = new ApkBuilder(inApk, outApk, tmpDir.getPath())
                .prepare();
        apkBuilder.setProgressCallback(this);
        apkBuilder.editManifest()
                .setVersionCode(5000)
                .setVersionName("1.2.3")
                .setAppName("Hello"+ran)
                .setPackageName("com.stardust.axxx"+ran)
                .commit();
        System.out.println("---------------"+js.getPath());
        apkBuilder.replaceFile("assets/project/main.js", js.getPath())
                .replaceFile("assets/project/"+js.getName().replace(".js",".dex"),js.getPath().replace(".js",".dex"))
                .setArscPackageName("com.stardust.axxx"+ran)
                .build()
                .sign();
    }

    @Override
    public void onPrepare(ApkBuilder builder) {

    }

    @Override
    public void onBuild(ApkBuilder builder) {

    }

    @Override
    public void onSign(ApkBuilder builder) {
        Toast.makeText(getApplicationContext(),"完成",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClean(ApkBuilder builder) {

    }
}
