# Week 8 Android lesson 5
---
在lsession5中,我們要製作自己的筆記本APP,並且修改內容需儲存到資料庫中永久存於手機內,APP包含以下功能
* 點擊新增建立一個note
* 點擊note進入編輯Activity
* 退出編輯畫面時保存內容到db

### layout
#### activity_main
首先在主畫面中我們要添加recycleView和一個可以點擊的新增按鈕

* 我們使用的`FloatingActionButton`(新增按鈕),是google提供的library,所以使用前要新增相關依賴
* 使用`recycleView`前也要添加依賴
```
implementation 'com.google.android.material:material:1.0.0'
implementation "androidx.recyclerview:recyclerview:1.1.0"
```
然後在`activity_main`內添加以下內容
```xml
<androidx.recyclerview.widget.RecyclerView
		android:layout_width="match_parent"
		android:layout_height="wrap_content"
		android:id="@+id/recycle_view"/>
<!--浮動按鈕layout
		srcCompat :設置加號icon
		tint :設置加號背景為淺色-->
<com.google.android.material.floatingactionbutton.FloatingActionButton
		android:layout_width="wrap_content"
		android:layout_height="wrap_content"
		android:id="@+id/add_note_button"
		android:layout_gravity="bottom|right"
		android:layout_margin="16dp"
		app:srcCompat="@android:drawable/ic_input_add"
		android:tint="@color/cardview_light_background" />
```
#### note_row
接著新增recycleView每項內容的xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:id="@+id/note_row">
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/note_row_text" />
</LinearLayout>
```
#### activity_note
最後我們要增加編輯畫面的layout
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".NoteActivity">
<!--    編輯框, gravity屬性讓文字置頂-->
    <EditText
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/note_edit_text"
        android:background="@android:color/transparent"
        android:gravity="top"
        android:inputType="text"
        android:padding="10dp" />
</androidx.constraintlayout.widget.ConstraintLayout>
```
### Database
* 接著我們要設定一些資料庫資訊,我們使用的資料庫為`SQLite`
* 要讓程式與db溝通,我們須使用第三方library [Room](https://developer.android.com/topic/libraries/architecture/room?hl=zh-cn)
* `Room`在SQLite 的基础上提供了一个抽象層,以注釋的方式讓用戶可以充分利用SQLite的功能
* 使用前須添加以下依賴
```
//版本自行參考官方文件
implementation "androidx.room:room-runtime:2.2.5"
annotationProcessor "androidx.room:room-compiler:2.2.5"
```
下面介紹此庫的簡單用法
#### Entity
* 同Model意義,此類別定義table名稱及欄位名稱
* 執行程式時若db內沒有定義的表格則會依據Entity內的定義自動建立
* `@Entity`宣告此類為Entity,參數值定義table名稱
* `@PrimaryKey`定義此欄位為主鍵,每個Entity類別必須定義一主鍵
* `@ColumnInfo`定義欄位名稱(依name值為主)
* `@Ignore`忽略此屬性不建立欄位
* `@ForeignKey`定義外來鍵,room的外來鍵定義還可以在
	* `entity`指定關聯表格的class,
	* `parentColumns`指定外來表關聯的欄位,
	* `childColumns`指定當前表關聯的欄位
	* `onUpdate`/`onDelete` 若有指定此參數當母表做`update`/`delete`時自動更新/刪除子表內容,常數值代表的內容看[ForeignKey](https://developer.android.com/reference/android/arch/persistence/room/ForeignKey#CASCADE)
* `indices`用來定義索引, 在`@Index`內指定索引欄位名稱, 如果此索引欄位值不重複,則加上`unique = true`, PK鍵已內建索引不須設, 若在`@Index`內加入PK則其他欄位會變得可以出現重複值
* 定義notes表格
```java
/**
 * 實體類,用來建立表格及欄位
 */
@Entity(tableName = "notes", foreignKeys = @ForeignKey(entity = Users.class,
        parentColumns = "id",
        childColumns = "user_id",
        onDelete=CASCADE), indices = @Index(value = {"name"}, unique = true))
public class Note {
    //@PrimaryKey表示自動增長的主鍵
    @PrimaryKey
    public int id;
		
		@ColumnInfo(name = "user_id")
    public String userId;

    //欄位名稱,真正表格內的欄位名稱定義在name的值,而非變數名稱
    @ColumnInfo(name = "contents")
    public String contents;
}
```
#### Dao
* Dao中定義各種方法與db做溝通,每個方法即為一個SQL
* Dao可以是interface或abstract class,Room會在編譯時創建每個DAO實現
* `@Dao` 宣告此interface或abstract class為Dao
* `@Query`執行注釋括號內的SQL語句,傳入參數使用`:`並透過變數名稱綁定,找不到名稱時會報錯
```java
//執行查詢SQL,若方法參數打錯不是minAge則執行時會報錯
@Query ("SELECT * FROM user WHERE age > :minAge" ) 
public User [] loadAllUsers(int minAge);
```
* `@Insert`執行insert,方法參數應是已定義的Entity類別,返回long
```java
//方法內的值會做為insert時的參數
@Insert public void insertUsersAndFriends(User user);
```
* `@Update`執行update
* `@Delete`執行delete
* 定義notes表格的Dao
```java
/**
 * Dao類,用來與DB做資料交換的地方
 * 每個表格對應一個DAO用來做SQL查詢
 */
@Dao
public interface NoteDao {
    @Query("INSERT INTO notes (contents) VALUES ('New Note')")
    void create();

    @Query("SELECT * FROM notes")
    List<Note> getAllNotes();

    /**
     * 查詢參數使用":"表示佔位符
     * @param contents
     * @param id
     */
    @Query("UPDATE notes SET contents = :contents WHERE id = :id")
    void save(String contents, int id);
}
```

#### database
* 在database中定義了所有Entity和Dao
* database類別應宣告成抽象類,且須extends RoomDatabase 類別
* Entity或Dao數量沒有限制但名稱必須唯一
* `@Database`定義該類為batabase,`entities`參數定義所有的Entity類別
* 將dao及Entity加入database
```java
/**
 * entities加入對應db的Model class
 * version為db版號,每次更改db結構可更新版號以記錄該改動
 */
@Database(entities = {Note.class}, version = 1)
public abstract class NotesDatabase extends RoomDatabase {
    //定義有哪些DAO,需宣告成abstract方法
    public abstract NoteDao noteDao();
}
```

### 主程式
定義完了database,我們便可以開始來製作note App了

#### NotesAdapter
在Adapter內我們要做的事和之前差不多,但是資料來源從API變成DB查詢而來
```java
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    /**
     * 建立ViewHolder物件
     */
    public static class NoteViewHolder extends RecyclerView.ViewHolder{
        LinearLayout containerView;
        TextView textView;

        NoteViewHolder(View itemView) {
            super(itemView);
            containerView = itemView.findViewById(R.id.note_row);
            textView = itemView.findViewById(R.id.note_row_text);
            //單行點擊時進入note編輯activity並傳入current資料
            containerView.setOnClickListener(view -> {
                Note current = (Note) containerView.getTag();
                //將tag內容設給NoteActivity
                Intent intent = new Intent(view.getContext(), NoteActivity.class);
                intent.putExtra("id", current.id);
                intent.putExtra("contents", current.contents);
                //開啟指定的Activity
                view.getContext().startActivity(intent);
            });
        }
    }
    //儲存db傳來的資料
    private List<Note> notes = new ArrayList<>();

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_row, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note current = notes.get(position);
        holder.textView.setText(current.contents);
        holder.containerView.setTag(current);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }


    /**
     * 加載所有資料並重新load recycleView
     */
    public void reload() {
        //使用MainActivity建立的靜態database物件來執行Dao的SQL
        notes = MainActivity.database.noteDao().getAllNotes();
        //重新加載recycleView
        notifyDataSetChanged();
    }
}
```

#### MainActivity
在此類別中我們要做以下幾件事
* 建立db物件
* 綁定Adapter,LayoutManager給recycleView
* 建立新增按鈕點擊事件(點擊後建新筆記)
* `onResume()`方法內加入每次返回MainActivity要做的事

```java
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;
    private RecyclerView.LayoutManager layoutManager;
    //給其他類別使用db物件所以設static
    public static NotesDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycle_view);
        layoutManager = new LinearLayoutManager(this);
        notesAdapter = new NotesAdapter();
        //建立db物件,這裡才會把entities對應的表格建立起來(若之前沒建過)
        //name參數為db名稱
        database = Room.databaseBuilder(getApplicationContext(), NotesDatabase.class, "notes")
                //禁用主線程檢查
                // 當此方法呼叫後,若有SQL查詢過久則UI畫面會暫時卡住不繼續往下執行
                .allowMainThreadQueries()
                //若資料庫有更動過會遺失之前的資料,加此方法表示略過遺失會產生的問題直接使用新db
                //https://stackoverflow.com/questions/49629656/please-provide-a-migration-in-the-builder-or-call-fallbacktodestructivemigration
                .fallbackToDestructiveMigration()
                .build();

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(notesAdapter);

        //建立新增按鈕的click事件
        FloatingActionButton button = findViewById(R.id.add_note_button);
        button.setOnClickListener(view -> {
            //建立新筆記
            database.noteDao().create();
            //重新載入recycleView
            notesAdapter.reload();
        });
    }

    /**
     * 第一次建立此activity是呼叫onCreate(),然後呼叫onResume()一次
     * 每次從其他activity跳轉回來時也會呼叫此方法
     */
    @Override
    protected void onResume() {
        super.onResume();
        notesAdapter.reload();
    }
}
```

#### NoteActivity
最後我們要在編輯畫面完成編輯和以下功能
* 將MainActivity傳來的content塞入editText
* `onPause()`在每次離開NoteActivity會呼叫,此時我們要將修改內容存入db

```java
public class NoteActivity extends AppCompatActivity {
    private EditText editText;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        //取得點擊的recycleView單筆資料
        editText = findViewById(R.id.note_edit_text);
        String contents = getIntent().getStringExtra("contents");
        id = getIntent().getIntExtra("id", 0);
        editText.setText(contents);
    }

    /**
     * 離開此activity時會呼叫
     */
    @Override
    protected void onPause() {
        super.onPause();
        //離開時儲存修改值進db
        MainActivity.database.noteDao().save(editText.getText().toString(), id);
    }
}
```

以上便是我們的Notes App