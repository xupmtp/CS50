# Week 8 Android lesson 4
---
這章節中我們要實做利用第三方API提供的image filter功能實作自己的圖片過濾器,其中會包含以下幾個功能
* 點擊按鈕可以上傳手機內的圖片
* APP內顯示上傳的圖片
* 點擊棕色按鈕將圖片改為棕色
* 點擊其他上傳按鈕將圖片改為不同的過濾畫面

使用的第三方Library為[wasabeef/glide-transformations](https://github.com/wasabeef/glide-transformations)
### activity_main.xml
* 將contain改為`ScrollView`,當螢幕大小超出畫面時可以滾動
```xml
<?xml version="1.0" encoding="utf-8"?>
<!--改為滾動視圖-->
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

<!--    用來排序元素-->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">
        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:id="@+id/image_view"/>
<!--        選擇圖片-->
        <Button
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Choose photo"
            android:onClick="choosePhoto"/>
<!--        下面三個過濾圖片用-->
        <Button
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Sepia"
            android:onClick="applySepia"/>
        <Button
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Toon"
            android:onClick="applyToon"/>
        <Button
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Sketch"
            android:onClick="applySketch"/>
    </LinearLayout>

</ScrollView>
```

### Gradle(App)
根github指示,我們要加入以下依賴

	implementation 'jp.wasabeef:glide-transformations:4.3.0'
    implementation 'com.github.bumptech.glide:glide:4.11.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.11.0'
    // If you want to use the GPU Filters
    implementation 'jp.co.cyberagent.android:gpuimage:2.1.0'

### MainActivity
* 首先我們在`choosePhoto()`中開啟手機檔案庫
* 接著`onActivityResult()`將圖片設給`imageview`
* 最後利用第三方library將圖片進行過濾
>library的使用方式是從github上面參考文檔得知
```java
package edu.harvard.cs50.fiftygram;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
    }

    /**
     * 從手機檔案選擇圖片
     * @param v
     */
    public void choosePhoto(View v) {
        //指定動作 開啟手機檔案
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //指定要開啟的檔案為圖片類型
        intent.setType("image/*");
        //開始activity,這裡是打開檔案的畫面
        //requestCode類似ID,告訴APP我們是從哪個檔案庫回來的
        startActivityForResult(intent, 1);
    }

    /**
     * 選擇檔案後,回到APP時觸發
     * @param requestCode startActivityForResult()設置的值 應為"1"
     * @param resultCode 返回代號表示Activity正確開啟
     * @param data 選擇的檔案資料
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //確保程式及資料正確
        if (resultCode == Activity.RESULT_OK && data != null) {
            //取得的資源路徑,URI指向硬碟中的某項數據
            Uri uri = data.getData();
            try {
                //根據URI打開資源路徑, 這裡只定打開模式為 "r"讀取
                ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                //這裡只是為了要傳入BitmapFactory而將檔案轉成需要的類別
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                //建立bitmap物件
                image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                //關閉資源
                parcelFileDescriptor.close();
                //設給圖片view
                imageView.setImageBitmap(image);
            } catch (IOException e) {
                Log.e("cs50", "upload Image error");
            }
        }
    }

    /**
     * 每個過濾器程式類似,將相同部分拉出來
     * 唯一差別為RequestOptions.bitmapTransform()的參數
     * 這裡改為父類Transformation<Bitmap>讓其他過濾器傳入
     * @param transformation
     */
    public void apply(Transformation<Bitmap> transformation) {
        Glide.with(this).load(image)
                .apply(RequestOptions.bitmapTransform(transformation))
                .into(imageView);
    }
    /**
     * 過濾為棕色
     */
    public void applySepia(View v) {
        apply(new SepiaFilterTransformation());
    }
    /**
     * 過濾為卡通色調
     */
    public void applyToon(View v) {
        apply(new ToonFilterTransformation());
    }
    /**
     * 過濾為草稿色調
     */
    public void applySketch(View v) {
        apply(new SketchFilterTransformation());
    }
}
```