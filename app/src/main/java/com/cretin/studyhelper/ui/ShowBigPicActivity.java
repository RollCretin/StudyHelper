package com.cretin.studyhelper.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.cetin.studyhelper.R;
import com.syd.oden.circleprogressdialog.core.CircleProgressDialog;

import java.io.File;
import java.util.concurrent.ExecutionException;

import uk.co.senab.photoview.PhotoView;

import static android.graphics.BitmapFactory.decodeFile;

public class ShowBigPicActivity extends AppCompatActivity {
    private PhotoView photoView;
    private ImageView imageView;
    private TextView tvDes;
    private ImageView ivlose;
    private ScrollView scrollView;
    private String imageUrl = "";
    private CircleProgressDialog circleProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_big_pic);
        //全屏显示图片
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        photoView = ( PhotoView ) findViewById(R.id.photoView);
        ivlose = ( ImageView ) findViewById(R.id.iv_close);
        tvDes = ( TextView ) findViewById(R.id.tv_details);
        scrollView = ( ScrollView ) findViewById(R.id.scrollView);

        ivlose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        circleProgressDialog = new CircleProgressDialog(this);
        //可对对话框的大小、进度条的颜色、宽度、文字的颜色、内容等属性进行设置
        circleProgressDialog.setDialogSize(15);
        circleProgressDialog.setProgressColor(Color.parseColor("#FFFFFF"));
        circleProgressDialog.setTextColor(Color.parseColor("#FFFFFF"));
        circleProgressDialog.setText("正在加载...");
        circleProgressDialog.showDialog();  //显示对话框

        //通过后缀判断图片类型
        imageUrl = getIntent().getStringExtra("image_url");
        Log.e("img", "" + imageUrl);
        String details = getIntent().getStringExtra("details");
        if ( !TextUtils.isEmpty(details) )
            tvDes.setText(details);

        if ( imageUrl.startsWith("http") ) {
            //网络文件
            Glide
                    .with(this) // could be an issue!
                    .load(imageUrl)
                    .asBitmap()
                    .into(target);
        } else {
            //本地文件
            File file = new File(imageUrl);
            Glide
                    .with(this) // could be an issue!
                    .load(file)
                    .asBitmap()
                    .into(target);
        }

    }

    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap mBitmap, GlideAnimation glideAnimation) {
            // do something with the bitmap
            // for demonstration purposes, let's just set it to an ImageView
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;//只测量
            float height = mBitmap.getHeight();
            float width = mBitmap.getWidth();
            //再拿到屏幕的宽
            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            float screenWidth = display.getWidth();
            float screenHeight = display.getHeight();
            //计算如果让照片是屏幕的宽，选要乘以多少？
            float scale = screenWidth / width;
            if ( scale == 0 ) {
                scale = 1;
            }
            //这个时候。只需让图片的宽是屏幕的宽，高乘以比例
            int displayHeight = ( int ) (height * scale);//要显示的高，这样避免失真
            //最终让图片按照宽是屏幕 高是等比例缩放的大小
            if ( displayHeight < screenHeight ) {
                findViewById(R.id.scrollView).setEnabled(false);
                displayHeight = ( int ) screenHeight;
            } else {
                photoView.setZoomable(false);
            }
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(( int ) screenWidth, displayHeight);
            photoView.setLayoutParams(layoutParams);
            Log.e("HHHHHH", "" + android.os.Build.BRAND);
//                                图片太大就进行压缩后显示
            if ( "HONOR".equals(android.os.Build.BRAND) ) { //华为手机再压缩
                if ( mBitmap.getByteCount() / 1024 / 1024 > 10 ) {
                    Log.e("HHHHHH", "" + android.os.Build.BRAND + "进行了压缩");
                    new ShowAsyncTask().execute(imageUrl);
                } else {
                    photoView.setImageBitmap(mBitmap);
                }
            } else {
                photoView.setImageBitmap(mBitmap);
            }
            circleProgressDialog.dismiss();
        }
    };

    class ShowAsyncTask extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                FutureTarget future = Glide
                        .with(ShowBigPicActivity.this)
                        .load(params[0])
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                File file = ( File ) future.get();


                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                decodeFile(file.getAbsolutePath(), options);
                int inSampleSize = 2;
                //重新设置压缩比
                options.inJustDecodeBounds = false;
                options.inSampleSize = inSampleSize;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;//降低图片从ARGB888到RGB565
                bitmap = decodeFile(file.getAbsolutePath(), options);
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            } catch ( ExecutionException e ) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap file) {
            photoView.setImageBitmap(file);
        }
    }

    public Bitmap drawable2Bitmap(Drawable drawable) {
        if ( drawable instanceof BitmapDrawable ) {
            return (( BitmapDrawable ) drawable).getBitmap();
        } else if ( drawable instanceof NinePatchDrawable ) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

}
