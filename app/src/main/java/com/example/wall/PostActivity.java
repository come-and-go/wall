package com.example.wall;

import static android.os.Environment.*;

import static com.example.wall.LoginActivity.all_username;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wall.api.UpdateImageApi;
import com.google.gson.Gson;
import com.hjq.http.EasyHttp;
import com.hjq.http.EasyUtils;
import com.hjq.http.body.JsonBody;
import com.hjq.http.listener.OnUpdateListener;
import com.hjq.http.model.FileContentResolver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostActivity extends AppCompatActivity {

    private Button mBtnReturn;
    private Button mBtnPost;
    private EditText mEtTitle;
    private EditText mEtText;
    Uri videoUri = null;

    private static final int TAKE_PHOTO = 1;
    private ImageView picture;
    private Uri imageUri;
    static final int PERMISSION_REQUEST_CODE = 1;
    public static final int CHOOSE_PHOTO = 2;
    private int type = 0;
    private static final int CHOOSE_VIDEO = 3;
    public File outputImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mBtnReturn = findViewById(R.id.btn_return);
        mBtnPost = findViewById(R.id.btn_post);
        mEtTitle = findViewById(R.id.et_title);
        mEtText = findViewById(R.id.et_text);

        Button chooseVideo = (Button) findViewById(R.id.btn_video);
        Button takePhoto = (Button) findViewById(R.id.btn_photo);
        Button chooseFromAlbum = (Button) findViewById(R.id.btn_album);
        picture = (ImageView) findViewById(R.id.image);
        mBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(PostActivity.this, UserHomeActivity.class);
                startActivity(intent);
            }
        });

        // 地址定位权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE); // 位置
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationListener mLocationListener = new MyLocationListener();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        Location mlocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); // 网络

        Log.d("111111LoctianActivity>>", "mLocationManager>>:" + mLocationManager);
        Log.d("111111LoctianActivity>>", "mlocation>>:" + mlocation);


        mBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = all_username;
                String title = mEtTitle.getText().toString();
                String text = mEtText.getText().toString();
                int content_type = 0;
                double location_x = mlocation.getLongitude() * 10000;//jingdu
                double location_y = mlocation.getLatitude() * 10000;//weidu

                Log.d("111111LoctianActivity>>", "mlocation>>:" + location_x);
                Log.d("111111LoctianActivity>>", "mlocation>>:" + location_y);

                File file = null;
                if (type==2)
                {
                    if (null != videoUri){

                        long time=System.currentTimeMillis();
                        file = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), time+".mp4");
                        if (!file.exists()) {
                            try {
                                // 从 videoUri 中复制视频文件到本地
                                InputStream inputStream = getContentResolver().openInputStream(videoUri);
                                OutputStream outputStream = new FileOutputStream(file);
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, bytesRead);
                                }
                                inputStream.close();
                                outputStream.close();
                                // 通知系统多媒体扫描该文件
                                MediaScannerConnection.scanFile(PostActivity.this, new String[]{file.getPath()}, null, null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else if (type==1)
                    if (null != bitmap){
                        long time=System.currentTimeMillis();
                        file = new File(getExternalFilesDir(DIRECTORY_PICTURES), time+".png");
//                String fileName = "我是测试专用的图片.png";
//                File file;
//                Uri outputUri;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    // 适配 Android 10 分区存储特性
//                    ContentValues values = new ContentValues();
//                    // 设置显示的文件名
//                    values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
//                    // 生成一个新的 uri 路径
//                    outputUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                    file = new FileContentResolver(getContentResolver(), outputUri, fileName);
//                } else {
//                    file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
//                }
                        if (!file.exists()) {
                            // 生成图片到本地
                            try {
//                Drawable drawable = ContextCompat.getDrawable(PostActivity.this, R.drawable.test);
                                OutputStream outputStream = EasyUtils.openFileOutputStream(file);
//                if (((BitmapDrawable) drawable).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                                    outputStream.flush();
                                }
                                // 通知系统多媒体扫描该文件，否则会导致拍摄出来的图片或者视频没有及时显示到相册中，而需要通过重启手机才能看到
                                MediaScannerConnection.scanFile(PostActivity.this, new String[]{file.getPath()}, null, null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                if (file != null)
                    content_type = 1;
                UpdateImageApi api = new UpdateImageApi();
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("title", title);
                    jsonObject.put("content_type", content_type);
                    jsonObject.put("text", text);
                    jsonObject.put("location_x", location_x);
                    jsonObject.put("location_y", location_y);
                    jsonObject.put("ownername", username);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String jsonString = jsonObject.toString();
                RequestBody jsonRequestBody = RequestBody.create(
                        MediaType.parse("application/json; charset=utf-8"),
                        jsonString
                );
                MediaType mediaType = MediaType.parse("image,video/*"); // replace "image/*" with the actual file type
                MultipartBody requestBody;
                if (file == null) {
                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("post", jsonString, jsonRequestBody)
                            .build();
                } else {
                    RequestBody fileRequestBody = RequestBody.create(mediaType, file);
                    Log.i("请求情况：", "file2.getName() " + file.getName());
                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", file.getName(), fileRequestBody)
                            .addFormDataPart("post", jsonString, jsonRequestBody)
                            .build();
                }
                EasyHttp.post(PostActivity.this)
                        .api(api)
                        .body(requestBody)
//                        .body(new JsonBody(jsonString))
//                        .json(jsonString)
                        .request(new OnUpdateListener<Void>() {

                            @Override
                            public void onStart(Call call) {
//                        mProgressBar.setProgress(0);
//                        mProgressBar.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onProgress(int progress) {
//                        mProgressBar.setProgress(progress);
                            }

                            @Override
                            public void onSucceed(Void result) {
                                bitmap = null;
                                Toast.makeText(PostActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                                Intent intent = null;
                                intent = new Intent(PostActivity.this, UserHomeActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onFail(Exception e) {
                                Toast.makeText(PostActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onEnd(Call call) {
//                        mProgressBar.setVisibility(View.GONE);
                            }
                        });

            }
        });

        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // 更新当前位置
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 100, new LocationListener() {

                //在用户禁用具有定位功能的硬件时被调用
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // TODO Auto-generated method stub

                }

                // 位置服务可用
                // 在用户启动具有定位功能的硬件是被调用
                @Override
                public void onProviderEnabled(String provider) {
                    // TODO Auto-generated method stub
                }

                //在提供定位功能的硬件状态改变是被调用
                @Override
                public void onProviderDisabled(String provider) {
                    // TODO Auto-generated method stub
                }

                // 位置改变
                @Override
                public void onLocationChanged(Location location) {
                    // TODO Auto-generated method stub
                }
            });
        }

        chooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //动态申请读取SD卡权限
                if (ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PostActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 2);
                } else {
                    openVideo();
                }


            }
        });


        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //动态申请读取SD卡权限
                if (ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PostActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                } else {
                    openAlbum();
                }
            }
        });
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long time=System.currentTimeMillis();
                outputImage = new File(getExternalFilesDir(DIRECTORY_PICTURES), time+".png");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 进行一个判断
                // 如果运行设备的系统版本低于Android 7.0,就调用Uri的fromFile()方法将File对象转换成Uri对象
                // 这个Uri对象标识着output_image.jpg这张图片的本地真实路径
                // 否则，就调用FileProvider的getUriForFile()方法将File对象转换成一个封装过的Uri对象
                // getUriForFile()方法接收 3个参数，第一个参数要求传入Context对象，第二个参数可以是任意唯一的字符串，第三个参数则是我们刚刚创建的File对象
                // 之所以要进行这样一层转换，是因为从Android 7.0系统开始，直接使用本地真实路径的Uri被认为是不安全的，会抛出一个FileUriExposedException 异常
                // 而FileProvider则是-种特殊的内容提供器，它使用了和内容提供器类似的机制来对数据进行保护，可以选择性地将封装过的Uri共享给外部，从而提高了应用的安全性
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(PostActivity.this, "com.example.wall", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }

                //启用相机程序
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, TAKE_PHOTO);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        type=1;
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    String imagePath = null;
                    type=1;
                    if (Build.VERSION.SDK_INT >= 30) {
                        imagePath = getBitmapP(data);
                    } else if (Build.VERSION.SDK_INT >= 19) {
                        imagePath = handleImageOnKitKat(data);
                    } else {
                        imagePath = handleImageBeforeKitKat(data);
                    }
                    // copy the file after getting the image path
//                    if (imagePath != null) {
//                        File fromFile = new File(imagePath);
//                        try {
//                            copyFile(fromFile, outputImage);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            // handle the error as appropriate for your app
//                        }
//                    }
                }
                break;
            case CHOOSE_VIDEO:
                if (resultCode == RESULT_OK) {
                    type = 2;
                    videoUri = data.getData();

                    Glide.with(this)
                            .asBitmap()
                            .load(videoUri) // Uri of the video
                            .into(picture);

                }
                break;
            default:
                break;
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("*/*");
        String[] mimeTypes = {"image/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, CHOOSE_PHOTO); //打开相册
    }
    private void openVideo() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("*/*");
        String[] mimeTypes = {"video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, CHOOSE_VIDEO); //打开相册




    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                openAlbum();
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    //Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                openVideo();
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    //Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }


    @TargetApi(31)
    private String handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            /*如果是document类型的Uri，则通过document id处理*/
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); //根据图片路径显示图片
        return imagePath;
    }

    private String handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
        return imagePath;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private String getBitmapP(Intent data) {
        Uri selectedImageUri = data.getData();
        try {
            ImageDecoder.Source sourceMap = ImageDecoder.createSource(getContentResolver(), selectedImageUri);
            bitmap = ImageDecoder.decodeBitmap(sourceMap).copy(Bitmap.Config.ARGB_8888, true);
            picture.setImageBitmap(bitmap);
            Log.d("getBitmapP", ">>:" + "okokokokokokokokokokokokok");
            // return the path of the selected image
            return saveBitmapToFile(bitmap);
            //return getImagePath(selectedImageUri, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        //Log.d("getBitmapP", ">>:" + path);
        return path;
    }

    private Bitmap bitmap;

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
            Log.d("111111LoctianActivity>>", "mlocation>>:" + "okokokokokokokokokokokokok");

        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }



    public String saveBitmapToFile(Bitmap bitmap) {
        // 应用的私有文件夹
        File filesDir = getApplicationContext().getFilesDir();
        // 图片文件
        File imageFile = new File(filesDir, "image.jpg");

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            // 将 Bitmap 压缩为 JPEG 格式，然后保存到文件系统中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // 返回文件的路径
        return imageFile.getAbsolutePath();
    }


}




