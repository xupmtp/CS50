# [Problem Set 8 Android-2,3](https://cs50.harvard.edu/x/2020/tracks/mobile/android/)
---
### [2. Fiftygram](https://cs50.harvard.edu/x/2020/tracks/mobile/android/fiftygram/)
在這章中我們要延續在lessio3中的圖片過濾器,按題目指示完成以下功能
* More Filters
* Saving Photos

#### More Filters
之前影片中實做了3種過濾器,這裡我們須自己再實做一個

* 首先我們在layout中新增一個button,並將onclick綁定接下來要完成的`applyPixelation()`方法
* 接著在MainActivity中完成我們的像素化過濾器
```java
/**
 * 將圖片像素化
 * @param view
 */
public void applyPixelation(View view) {
		apply(new PixelationFilterTransformation());
}
```

#### Saving Photos
接著要完成的功能是Saving Photos,我們必須讓使用者可以儲存過濾後的圖片到手機相簿中

首先我們必須設置存取相簿的權限給APP,各個SDK版本設置方式不同,這裡確保SDK版本在23或以上才能執行以下程式

* 要修改/查看SDK版本需開啟`build.gradle`,然後你會看到


	minSdkVersion 23
* 接著要加入權限,開啟`AndroidManifest.xml`並添加以下內容
```xml
<!--在<manifest>標籤中加入下面屬性-->
xmlns:tools="http://schemas.android.com/tools"

<!--在<manifest>標籤內加入下面內容-->
<uses-permission
    android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    tools:remove="android:maxSdkVersion" />
```
* 最後我們要在程式中實際發出請求權限,必須要實做`ActivityCompat.OnRequestPermissionsResultCallback` interface
```java
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
```
* 然後在`onCreate()`中加入以下內容就會去請求權限
```java
requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
```
* 請求後會跳出對話框要求用戶允許或拒絕,我們可以添加以下方法來檢查對話框的結果
```java
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
```
* 最後我們要儲存圖像,首先在layout新增一button,並將onClick設置為`saveImage()`方法,接著要實做此方法
```java
/**
 * 儲存過濾影像
 * @param v
 */
public void saveImage(View v) {
		//啟用DrawingCache,不加容易取的空影像
		imageView.setDrawingCacheEnabled(true);
		//取得目前imageView顯示的影像
		Bitmap image = imageView.getDrawingCache();
		//將圖片儲存進手機相簿,參數分別為:
		//要在使用的ContentResolver, 要儲存的圖像, 圖像名稱, 圖像說明
		MediaStore.Images.Media.insertImage(getContentResolver(), image, "name", "description");
		//使用完重新設為false
		imageView.setDrawingCacheEnabled(false);
		Log.i("cs50", "Image saved");
}
```
按下save按鈕後就可以在手機photo中看到我們過濾的圖片了

### [3. Notes](https://cs50.harvard.edu/x/2020/tracks/mobile/android/notes/)
在Notes作業中,我們延續lession5的notes專案,在其中添加刪除功能
* 首先在`activity_note.xml`中添加button
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".NoteActivity">
<!--    新增LinearLayout來管理排版-->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical" >

<!--        修改高度來讓UI更順暢-->
        <EditText
            android:id="@+id/note_edit_text"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@android:color/transparent"
            android:gravity="top"
            android:inputType="text"
            android:padding="10dp" />
        
        <!--    新增Button綁定deleteNote()方法-->
        <Button
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Delete Note"
            android:onClick="deleteNote" />
    </LinearLayout>
</androidx.constraintlayout.widget.ConstraintLayout>
```
* 接著在DAO中添加delete語法
```java
//根據id刪除指定note
@Query("DELETE FROM notes WHERE id = :id")
void delete(int id);
```
* 最後在`NoteActivity`中加入button的點擊事件`deleteNote()`方法,在裡面要執行刪除note及返回上個activity兩動作
```java
/**
 * 當用戶點擊刪除按紐時,刪除當前note並返回MainActivity
 * @param v
 */
public void deleteNote(View v) {
		//呼叫DAO的刪除方法
		MainActivity.database.noteDao().delete(getIntent().getIntExtra("id", 0));
		//Activity的finish()方法會關閉當前視窗並返回上一個activity
		super.finish();
}
```