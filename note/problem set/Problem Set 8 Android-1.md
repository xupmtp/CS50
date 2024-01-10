# [Problem Set 8 Android-1](https://cs50.harvard.edu/x/2020/tracks/mobile/android/)
---
### [1. Pokédex](https://cs50.harvard.edu/x/2020/tracks/mobile/android/pokedex/)
在這份作業中我們延續了lession1~lession3的pokedex專案並實做以下功能
* `Searching`
* `Catching`
* `Saving State`
* `Sprites`
* `Description`

### Searching
在Searching中,我們要在MainActivity制做一個搜尋框,當使用者輸入內容時,recycleView必須要動態顯示包含輸入文字的pokemon名稱

#### filterable
首先我們必須在我們的Adpter class中實現filterabled接口,這個接口讓我們可以過濾數據

```java
public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.PokedexViewHolder> implements Filterable {
```
filterable內有一個要實現的方法
```java
@Override
public Filter getFilter() {
	return new PokemonFilter();
}
```
這時我們還沒有`PokemonFilter`這個class,這是我們自己要實現的class
```java
 public class PokemonFilter extends Filter {
			/**
			 * 實做要如何過濾資料的地方
			 * performFiltering()的return值將傳給publishResults()
			 * @param constraint 使用者輸入的文本
			 * @return
			 */
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					List<Pokemon> values = new ArrayList<>();
					for (Pokemon pk : pokemon) {
							if (pk.getName().contains(constraint)) {
									values.add(pk);
							}
					}
					//過濾的資料存入FilterResults內才能傳遞
					results.values = values;
					//須給資料大小
					results.count = values.size();
					return results;
			}

			/**
			 * 取得過濾後的資料並更新UI畫面
			 * @param constraint
			 * @param results
			 */
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
					filtered = (List<Pokemon>) results.values;
					//重新load資料
					notifyDataSetChanged();
			}
	}
```

其中filterd是暫存資料的地方,每次更新時都會取暫存的資料顯示在UI上
```java
// API回傳的所有資料
private List<Pokemon> pokemon = new ArrayList<>();
// 每次過濾後暫存的資料
private List<Pokemon> filtered = new ArrayList<>();
```

然後`onBindViewHolder()`和`getItemCount()`的資料也要更改從`filtered`內取得
```java
/**
 * 顯示的資料是從filtered裡撈出所以position指向的位置為filtered內的資料
 * @param holder
 * @param position
 */
@Override
public void onBindViewHolder(@NonNull PokedexViewHolder holder, int position) {
		Pokemon current = filtered.get(position);
		holder.textView.setText(current.getName());
		holder.containerView.setTag(current);
}

/**
 * 改回傳filtered的大小(動態)
 * @return
 */
@Override
public int getItemCount() {
		return filtered.size();
    }
```

#### menu
接著我們要建立一個搜尋框
右鍵res目錄 > New > Android Resource Directory > 在 Directory name 和 Resource type 輸入 menu > 完成
接著右鍵menu目錄 > New > Menu resource file > 輸入檔名`main_menu.xml` >完成
最後將以下內容加入新建的檔案中
```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <item android:id="@+id/action_search"
        android:title="Search"
        app:actionViewClass="androidx.appcompat.widget.SearchView"
        app:showAsAction="always" />
</menu>
```
上面完成後,我們便建立了一個搜尋框,`item`元素為一個搜索icon,點擊後會出現`SearchView`(搜尋輸入框)

#### MainActivity
接著我們要將`SearchView`連接到`MainActivity`,首先我們要在`MainActivity`實現`SearchView.OnQueryTextListener`接口
```java
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
```

我們要實現已3個方法來完成對搜索框的調用
* `onCreateOptionsMenu()`指定&啟用搜索框功能
* `onQueryTextSubmit()`當輸入完成後啟用
* `onQueryTextChange()`每次輸入框文字更改時調用
```java
@Override
public boolean onCreateOptionsMenu(Menu menu) {
		//指定mainActivity使用main_menu.xml
		getMenuInflater().inflate(R.menu.main_menu, menu);
		//獲得要使用的輸入框 action_search
		MenuItem searchItem = menu.findItem(R.id.action_search);

		//"setOnQueryTextListener()"表示在MainActivity上啟用我們的搜索功能
		SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setOnQueryTextListener(this);

		return true;
}
		/**
 * 用戶按下Enter(或完成)時調用
 * @param query
 * @return
 */
@Override
public boolean onQueryTextSubmit(String query) {
		adapter.getFilter().filter(query);
		return false;
}

/**
 * 每次更改輸入框文字時調用
 * @param newText
 * @return
 */
@Override
public boolean onQueryTextChange(String newText) {
		adapter.getFilter().filter(newText);
		return false;
}
```
以上便是searching功能

### Catching&Saving State
這裡我們要實現自己的圖鑑功能,當pokemon已經被我們抓過的時候,點擊button可以將狀態改為已捕獲,再次點擊恢復成未捕獲

而這裡我們要將這種更改的狀態紀錄到記憶體中,當關閉程序再次進入時,捕獲的狀態必須是上次更改過的狀態

#### activity_pokemon.xml
首先在layout加入捕獲button

這裡我們直接綁定toggleCatch()作為點擊事件
```xml
	<Button
			android:id="@+id/pokedex_catch_button"
			android:layout_width="wrap_content"
			android:layout_height="wrap_content"
			android:onClick="toggleCatch"
			android:textAlignment="center" />
```

#### PokemonActivity

* 為了要保存狀態,我們使用`SharedPreferences`這個class,`SharedPreferences`會在每次啟動APP時記住存入此類的內容,下次啟動時可從中撈出再次使用
* `getSharedPreferences("name", MODE_PRIVATE)`會在手機上建立一`name.xml`檔(根據第一參數值), `MODE_PRIVATE`指定此檔案內的參數值只能在pokedex app類使用
* 若調用除了`getXXX`以外的方法時(如:`putInt()`), 需在結束時調用`apply()`送出改動, 否則資料不會發生異動
* 接著實現我們的捕獲功能
	* 新增全域變數`private Button catchButton;`
	* `onCreate()`內取得視圖
```java
catchButton = findViewById(R.id.pokedex_catch_button);
```
* `load()`中set button顯示的文字
```java
//依據之前的捕獲狀態set button text
boolean isCatch = getSharedPreferences("session", MODE_PRIVATE).getBoolean(nameTextView.getText().toString(), true);
catchButton.setText(isCatch ? "Catch" : "Release");
```
* 實現`toggleCatch()`,每次點擊時更改文字且將狀態set到`getPreferences`類中
```java
/**
 * 更改是否被捕獲的狀態 Catch:未捕獲 Release:已捕獲
 * @param view
 */
public void toggleCatch(View view) {
		String name = (String) nameTextView.getText();
		Button btn = view.findViewById(R.id.pokedex_catch_button);
		boolean isCatch = "Catch".equals(btn.getText());
		btn.setText(isCatch ? "Release" : "Catch");
		//將更改後的結果放入SharedPreferences這個類中
		getSharedPreferences("session", MODE_PRIVATE).edit().putBoolean(name, !isCatch).apply();
}
```
>使用`getPreferences(Context.MODE_PRIVATE)`取得/設置變數,即使刪除後仍會取得值, 所以改用`getSharedPreferences()`

這樣便完成了我們的捕獲功能

### Sprites
在這個功能中,我們會幫pokemon資料加上圖片

* 首先在layout中加入`imageView`
```xml
<ImageView
		android:layout_width="match_parent"
		android:layout_height="250dp"
		android:id="@+id/pokedex_image" />
```
* 在`PokemonActivity`中新增此view的全域變數並用`findById`將xml內容給他

* 接著我們要使用一個Android的內置類別`AsyncTask`,它可以讓我們異步完成一些程序,因此我們的APP不會因為下載圖片而無法載入其他內容
```java
private class DownloadSpriteTask extends AsyncTask<String, Void, Bitmap> {
		/**
		 * 要在後台異步執行的程式,這裡根據url下載圖片
		 * @param strings
		 * @return
		 */
		@Override
		protected Bitmap doInBackground(String... strings) {
				try {
						//取得url的資訊,這裡傳入圖片URL
						URL url = new URL(strings[0]);
						//openStream()打開此url並返為inputStream物件
						return BitmapFactory.decodeStream(url.openStream());
				} catch (IOException e) {
						Log.e("cs50", "Download sprite error", e);
						return null;
				}
		}

		/**
		 * doInBackground()完成後執行
		 * 取得下載好的圖片並set到imageView
		 * @param bitmap 載好的圖片
		 */
		@Override
		protected void onPostExecute(Bitmap bitmap) {
				//將載好的圖片設給imageView
				imageView.setImageBitmap(bitmap);
		}
}
```

* 最後在`load()`方法中執行該類別的`execute()`方法就會執行我們的程式
* `execute()`方法傳入的參數在`doInBackground(String... strings)`中可從`strings`取得
```java
new DownloadSpriteTask().execute(response.getJSONObject("sprites").getString("front_default"));
```

以上便是圖片載入功能

### Description
Pokemon的最後一個功能是`Description`,我們要從API中取得pokemon的描述資料並顯示,API內會有多種語言及版本的描述,這裡只取英文最初版

做法和取API資料基本相同,所以這裡只做簡單描述

* 加入顯示文字的layout
```xml
<TextView
		android:layout_width="match_parent"
		android:layout_height="wrap_content"
		android:id="@+id/pokemon_description"
		android:textAlignment="center"
		android:textSize="16dp"
		android:paddingTop="5dp" />
```
* 加入全域,及設ID給它
* 加載描述文字
```java
/**
	 * 從API取得描述並設給specieView
	 * @param url1 此寶可夢的向細資料url
	 */
	private void loadSpecies(String url1) {
			JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
							try {
									JSONArray entries = response.getJSONArray("flavor_text_entries");
									for (int i = 0; i < entries.length(); i++) {
											JSONObject entry = entries.getJSONObject(i);
											if ("en".equals(entry.getJSONObject("language").getString("name"))){
													specieView.setText(entry.getString("flavor_text"));
													System.out.println(entry.getString("flavor_text"));
													break;
											}
									}
							} catch (JSONException e) {
									Log.e("cs50", "Pokemon species error", e);
							}
					}
			}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
							Log.e("cs50", "Pokemon species Json error", error);
					}
			});
			requestQueue.add(request);
	}
```
* 在`load()`中調用該方法
```java
new DownloadSpriteTask().execute(response.getJSONObject("sprites").getString("front_default"));
```
以上完成了pokemon APP的所有功能