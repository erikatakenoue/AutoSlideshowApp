package jp.techacademy.erika.takenoue.autoslideshowapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Timer mTimer;

    Handler mHandler = new Handler();
    Cursor cursor1 = null;

    Button mNextButton;
    Button mPreviousButton;
    Button mAutoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNextButton = (Button) findViewById(R.id.button1);
        mNextButton.setOnClickListener(this);
        mPreviousButton = (Button) findViewById(R.id.button2);
        mPreviousButton.setOnClickListener(this);
        mAutoButton = (Button) findViewById(R.id.button3);
        mAutoButton.setOnClickListener(this);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo();
            } else {
                Toast.makeText(this, "許可してください", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            getContentsInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {
        ContentResolver resolver = getContentResolver();
        cursor1 = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );
    }


    @Override
    public void onClick(View v) {
        if (cursor1 == null) {
            Toast.makeText(this, "許可してください", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (v.getId() == R.id.button1) {
            if (cursor1.moveToNext()) {
                setImageVIew();
            } else if (cursor1.moveToFirst()) {
                setImageVIew();
            }
        }
        if (v.getId() == R.id.button2) {
            if (cursor1.moveToPrevious()) {
                setImageVIew();
            } else if (cursor1.moveToLast()) {
                setImageVIew();
            }
        }

        if (v.getId() == R.id.button3) {
            if (mTimer == null) {
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (cursor1.moveToNext()) {
                                    setImageVIew();
                                    mAutoButton.setText("停止");
                                    mNextButton.setEnabled(false);
                                    mPreviousButton.setEnabled(false);
                                } else if (cursor1.moveToFirst()) {
                                    setImageVIew();
                                }
                            }
                        });
                    }
                }, 2000, 2000);
            } else if (mTimer != null) {
                mTimer.cancel();
                mAutoButton.setText("再生");
                mNextButton.setEnabled(true);
                mPreviousButton.setEnabled(true);
                mTimer = null;
            }
        }
    }

    private void setImageVIew() {
        int fieldIndex = cursor1.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor1.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
        imageVIew.setImageURI(imageUri);
    }
}






