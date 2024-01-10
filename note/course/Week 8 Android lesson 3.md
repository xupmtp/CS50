# Week 8 Android lesson 3
---
* 這個章節中我們要將pokemon的資料從hard code改為傳入API資料
* 除此之外我們還要顯示更多pokemon的詳細資料
* API使用url做串接方法,傳送資料的格式為json,更多文件請參考[API網站](https:/pokeapi.co)

### 新增依賴
要向API發出請求,我們使用google提供的 **Volley** library

在gradle(app)內加入以下依賴

	implementation 'com.android.volley:volley:1.1.1'

### activity_pokemon.xml
在這裡新增兩個textView,用來放pokemon的屬性
```xml
<TextView
		android:layout_width="match_parent"
		android:layout_height="wrap_content"
		android:id="@+id/pokemon_type1"
		android:textAlignment="center"
		android:textSize="16dp"
		android:paddingTop="5dp"/>
<TextView
		android:layout_width="match_parent"
		android:layout_height="wrap_content"
		android:id="@+id/pokemon_type2"
		android:textAlignment="center"
		android:textSize="16dp"
		android:paddingTop="5dp"/>
```

### PokdexAdapter
在這裡我們要做的事有兩件
1. 從API回傳資料取得`name`,`url`並加入`pokemons list`內
2. 將原本傳給`PokemonActivity`的ID改為url

這裡改傳url所以我們要修改`Pokemon` model的內容
```java
    private String name;
    private String url;
```

##### PokdexAdapter
修改PokedexViewHolderclass內的建構子
```java
    public static class PokedexViewHolder extends RecyclerView.ViewHolder {
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
                //原本傳id改url
                intent.putExtra("url", cur.getUrl());

                v.getContext().startActivity(intent);
            });
        }
    }
```

新增PokdexAdapter建構子用來加載資料
加載程式寫於loadPokemon()
```java
//加載API資料
	PokdexAdapter(Context context) {
			//requestQueue用來發送裡面所有的request
			requestQueue = Volley.newRequestQueue(context);
			loadPokemon();
	}

	/**
	 * 加載API傳來的圖鑑資料
	 */
	public void loadPokemon() {
			String url = "https://pokeapi.co/api/v2/pokemon?limit=151";
			//JsonObjectRequest處理響應json資料,建構參數5個依序為=>
			// request方法
			// url
			// 發送過去的資料(這裡不需要),
			// response處理的方法(Response.Listener匿名類),
			// response錯誤時的處理方式(Response.ErrorListener匿名類),url錯誤或找不到時觸發
			//因為第四個參數有不同選擇,所以需指定lambda類型
			JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, (Response.Listener<JSONObject>) response -> {
					try {
							JSONArray results = response.getJSONArray("results");
							for (int i = 0; i < results.length(); i++) {
									JSONObject res = results.getJSONObject(i);
									String name = res.getString("name");
									String url1 = res.getString("url");
									pokemons.add(new Pokemon(name.substring(0, 1).toUpperCase() + name.substring(1), url1));
							}
							//告訴recycleView重新加載數據,不加資料不會重新刷新
							notifyDataSetChanged();
					} catch (JSONException e) {
							Log.e("cs50", "Json error", e);
					}
			}, error -> Log.e("cs50", "request Pokemon list error"));
			//要執行request必須將request加入requestQueue
			requestQueue.add(request);
	}
```

##### PokemonActivity
以傳來的url發送第二次請求給API,取得詳細資料
```java
public class PokemonActivity extends AppCompatActivity {

    private TextView nameView;
    private TextView numberView;
    private String url;
    private TextView type1View;
    private TextView type2View;
    private RequestQueue requestQueue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        //取得第一頁傳來的url
        url = getIntent().getStringExtra("url");
        nameView = findViewById(R.id.pokemon_name);
        numberView = findViewById(R.id.pokemon_number);
        type1View = findViewById(R.id.pokemon_type1);
        type2View = findViewById(R.id.pokemon_type2);

        //以url發送API請求取得資料
        load();
    }

    /**
     * 加載pokemon detail,url從MainActivity取得
     */
    private void load() {
        type1View.setText("");
        type2View.setText("");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, (Response.Listener<JSONObject>) response -> {
            try {
                //name跟id改從detail取得
                nameView.setText(response.getString("name"));
                numberView.setText(String.format(Locale.TAIWAN, "#%03d", response.getInt("id")));

                //取得pokemon屬性
                JSONArray typeEntries = response.getJSONArray("types");
                for (int i = 0; i < typeEntries.length(); i++) {
                    JSONObject typeEntry = typeEntries.getJSONObject(i);
                    //可能不只一個屬性
                    int slot = typeEntry.getInt("slot");
                    //屬性名
                    String type = typeEntry.getJSONObject("type").getString("name");

                    //多屬顯示在不同view上,最多2屬性
                    if (slot == 1) {
                        type1View.setText(type);
                    }
                    else if (slot == 2) {
                        type2View.setText(type);
                    }
                }
            } catch (JSONException e) {
                Log.e("cs50", "Pokemon Json error", e);
            }
        }, error -> Log.e("cs50", "Pokemon details error"));
        //發出請求
        requestQueue.add(request);
    }
}
```