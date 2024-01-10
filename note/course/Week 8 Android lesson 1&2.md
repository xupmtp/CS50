# Week 8 Android lesson 1&2
---
## 第一頁(list)
### RecyclerView

##### 新增依賴
build.gradle(app)中新增

	implementation "androidx.recyclerview:recyclerview:1.1.0"

##### layout新增標籤
在要加入RecyclerView的layout中(這裡是main)加入RecyclerView標籤
```xml
<androidx.recyclerview.widget.RecyclerView
		<!--設定長寬-->
		android:layout_width="match_parent"
		android:layout_height="match_parent"
		<!--設定recyclerview id,供java程式使用-->
		android:id="@+id/recycle_view" />
```

### Layout
我們的寶可夢列表每項點開後必須可以看到詳細資料,所以我們要新增一layout來顯示它

##### 新增layout檔
layout資料夾右鍵 -> new -> layout resource xml ->輸入名稱, root element選擇`LinearLayout` -> finish

`LinearLayout`讓每一行只會顯示一個view,是相對簡單的layout佈局

新增的檔案中需有以下內容

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent"
		<!--新增layout id-->
    android:id="@+id/pokedex_row">
		
		<!--新增顯示資料的TextView-->
    <TextView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
				<!--新增TextView id-->
        android:id="@+id/pokedex_row_text_view" />
</LinearLayout>
```

### Adapter
接著我們要使用Adapter來操控螢幕上的內容,Adapter是一個recycleView給我們的class,內有三個方法我們必須自己實現
* `onCreateViewHolder`建立一個新的viewHolder,裡面會傳入我們要使用的layout和view
* 

Adapter包含的類型(<?>)稱為ViewHolder,一個ViewHolder控制一個view,我們可以在ViewHolder內操控view上的元素

```java
public class PokdexAdapter extends RecyclerView.Adapter<PokdexAdapter.PokedexViewHolder> {

    /**
     * 我們自己實現的 ViewHolder
     */
    public static class PokedexViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout containerView;
        public TextView textView;

        public PokedexViewHolder(View view) {
            //先做父類方法再做我們的程式
            super(view);
            //參數為int,android和gradle會自動將xml設置的id轉為唯一的int key
            //R代表resource,可從此class訪問resource內容
            containerView = view.findViewById(R.id.pokedex_row);
            textView = view.findViewById(R.id.pokedex_row_text_view);
        }

    }

    //圖鑑資料
    private List<Pokemon> pokemons = Arrays.asList(
            new Pokemon("Charmander  ", 4),
            new Pokemon("Charmeleon  ", 5),
            new Pokemon("Charizard ", 6)
    );

    /**
     * 將xml上的layout及view轉換成java對象(PokedexViewHolder)
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public PokedexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokedex_row, parent, false);
        return new PokedexViewHolder(view);
    }

    /**
     * 每次螢幕滾動時會調用的方法
     * @param holder
     * @param position 當前所在的行數
     */
    @Override
    public void onBindViewHolder(@NonNull PokedexViewHolder holder, int position) {
        Pokemon current = pokemons.get(position - 3);
        holder.textView.setText(current.getName());
    }

    /**
     * 返回此資料列表共有幾筆
     * @return
     */
    @Override
    public int getItemCount() {
        return pokemons.size();
    }

}
```

### MainActivity
最後在我們主要程式中要將之前定義好的元件組合起來

```java
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //取得recycleView id
        recyclerView = findViewById(R.id.recycle_view);
        //設定Adapter,從自訂的PokdexAdapter取得
        adapter = new PokdexAdapter();
        //設定LayoutManager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }
}
```

## 第二頁(detail)
新增`PokemonActivity`,並勾選同步生成layout xml

### activity_pokemon
這是第二頁的主要layout
```xml
<!-- 使用LinearLayout而不是constraintlayout-->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
		<!-- 讓textview垂直排列而非水平-->
    android:orientation="vertical"
    tools:context=".PokemonActivity">
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/pokemon_name"
				<!-- 讓textview內文字置中 -->
        android:textAlignment="center"
        android:textSize="18dp"
				<!-- textview距離頂端10dp-->
        android:paddingTop="10dp"/>
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/pokemon_number"
        android:textAlignment="center"
        android:textSize="16dp"
        android:paddingTop="5dp"/>
</LinearLayout>
```

### PokemonActivity
這頁主要是拿其他頁的資料顯示
```java
public class PokemonActivity extends AppCompatActivity {

    private TextView nameView;
    private TextView numberView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        //取得其他頁傳來的資料
        String name = getIntent().getStringExtra("name");
        int number = getIntent().getIntExtra("number", 0);

        //依id取view
        nameView = findViewById(R.id.pokemon_name);
        numberView = findViewById(R.id.pokemon_number);

        //設置文字
        nameView.setText(name);
        numberView.setText(Integer.toString(number));
    }
}
```

### PokdexAdapter
這裡要告訴`PokemonActivity`資料是如何傳遞的

PokedexViewHolder 內部class
```java
    public static class PokedexViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout containerView;
        public TextView textView;

        public PokedexViewHolder(View view) {
            //先做父類方法再做我們的程式
            super(view);
            //參數為int,android和gradle會自動將xml設置的id轉為唯一的int key
            //R代表resource,可從此class訪問resource內容
            containerView = view.findViewById(R.id.pokedex_row);
            textView = view.findViewById(R.id.pokedex_row_text_view);

            containerView.setOnClickListener(v -> {
                //onBindViewHolder中設置的tag可透過getTag取得
                Pokemon cur = (Pokemon) containerView.getTag();
                Intent intent = new Intent(v.getContext(), PokemonActivity.class);
                intent.putExtra("name", cur.getName());
                intent.putExtra("number", cur.getNumber());

                v.getContext().startActivity(intent);
            });
        }

    }
```

onBindViewHolder()
```java
    @Override
    public void onBindViewHolder(@NonNull PokedexViewHolder holder, int position) {
        Pokemon current = pokemons.get(position);
        holder.textView.setText(current.getName());
        //將current物件傳給viewholder,供其他地方使用
        holder.containerView.setTag(current);
    }
```

### 執行
最後在`pokedex_row.xml`內的LinserLayout標籤中加入`android:padding="10dp"`屬性,讓每行資料的上下左右都有10dp的空檔

執行程式可以看到當我們點選行時可以跳轉到新的頁面,裡面有pokemon的詳細資料