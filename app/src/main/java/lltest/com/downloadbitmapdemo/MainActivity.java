package lltest.com.downloadbitmapdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 使用HttpURLConnection实现图片的下载与现显示
 *
 * @author ZhaoKaiQiang
 * @time 2014年6月9日
 */
public class MainActivity extends Activity {

    private Context mContext;
    private ImageView image;

    // 加载成功
    private static final int LOAD_SUCCESS = 1;
    // 加载失败
    private static final int LOAD_ERROR = -1;

    // 用于异步的显示图片
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                //下载成功
                case LOAD_SUCCESS:
                    // 获取图片的文件对象
                    File file = new File(Environment.getExternalStorageDirectory(), "pic.jpg");
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                        Bitmap bitmap = BitmapFactory.decodeStream(fis);
                        image.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                //下载失败
                case LOAD_ERROR:
                    Toast.makeText(mContext, "加载失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.image);
    }

    // Button的点击事件
    public void show(View view) {
        // 开启新的线程用于下载图片
        new Thread(new Runnable() {
            public void run() {
                getPicture();
            }
        }).start();
    }

    //下载图片的主方法
    private void getPicture() {

        URL url = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            //构建图片的url地址
            url = new URL("https://github.com/zj614android/picsLink/blob/master/%E6%80%AA%E5%BC%82%E9%9C%80%E6%B1%82.png?raw=true");
            //开启连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时的时间，5000毫秒即5秒
            conn.setConnectTimeout(5000);
            //设置获取图片的方式为GET
            conn.setRequestMethod("GET");
            //响应码为200，则访问成功
            if (conn.getResponseCode() == 200) {
                //获取连接的输入流，这个输入流就是图片的输入流
                is = conn.getInputStream();
                //构建一个file对象用于存储图片
                File file = new File(Environment.getExternalStorageDirectory(), "pic.jpg");
                fos = new FileOutputStream(file);
                int len = 0;
                byte[] buffer = new byte[1024];
                //将输入流写入到我们定义好的文件中
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                //将缓冲刷入文件
                fos.flush();
                //告诉handler，图片已经下载成功
                handler.sendEmptyMessage(LOAD_SUCCESS);
            }
        } catch (Exception e) {
            //告诉handler，图片已经下载失败
            handler.sendEmptyMessage(LOAD_ERROR);
            e.printStackTrace();
        } finally {
            //在最后，将各种流关闭
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                handler.sendEmptyMessage(LOAD_ERROR);
                e.printStackTrace();
            }
        }
    }

}


