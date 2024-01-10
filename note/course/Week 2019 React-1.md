# Week 2019 React-1
---
### 目錄
1. [背景](#背景)
2. [配置](#配置)
3. [Hello_World](#Hello_World)
4. [Component屬性與函數](#Component屬性與函數)
5. [Math_Game](#Math_Game)
6. [Tasks](#Tasks)

### 背景
* React由Facebook創建，並於2013年5月變成開源專案
* React是一款前端開發的框架,它提供更好的方法來管理DOM的建立

### 配置
* 要建立React應用有多種方法,可根據自身需求選擇合適的方法
* 下面介紹兩種做法,更多方法參考[建立全新的 React](https://zh-hant.reactjs.org/docs/create-a-new-react-app.html)
##### 在網頁中加入標籤
* 要使用React最簡單的方法,不須額外安裝其他工具,只要加入以下連結在html頁面中即可

```html
<!-- 加入React連結 -->
<script src="https://unpkg.com/react@16/umd/react.development.js" crossorigin></script>
<script src="https://unpkg.com/react-dom@16/umd/react-dom.development.js" crossorigin></script>
<!-- 加入Babel解析JSX語法 -->
<script src="https://unpkg.com/babel-standalone@6/babel.min.js"></script> 
```
* 在React中會使用到名為JSX的語法,它是一款拓展JS的語法,可提供使用者在JS句子中直接加入HTML標籤

##### Create React App
* Create React App是一個適合學習 React 的環境,也是官方推薦建置的最佳方法
* 要最佳化應用程式,需要在的本機上安裝 Node >= 8.10 和 npm >= 5.6
* 要建立項目,執行

```
npx create-react-app my-app
cd my-app
npm start
```
* 其中public資料夾中儲存html模板文件,src目錄中儲存JS,CSS及圖片資源
* 使用Create React App,只要將檔按放在指定目錄下,**Webpack**便會將多個文件自動打包成一個綑綁文件給模板呼叫
* 執行`$ npm run build`會將JSX文件預先編譯成JS文件,這樣瀏覽器便不需再解析JSX浪費資源

### Hello_World
* React以組件(Component)的概念來串連各個DOM元素
* 要讓`<script>`標籤解析JSX語法需加入`type="text/babel"`屬性
* 組件包括`Component function`和`Component class`,組件的名稱皆須大寫才可被React解析
	* `Component function`在return時回傳DOM內容
	* `Component class`須繼承`React.Component`且要包含render()方法,該方法回傳DOM內容
* 使用function Component的好處在於程式較精簡,但當程式變大時容易變得雜亂,而class Component的優點是結構分明,可以清楚知道每個部件在做什麼,但當程式不大時會覺得整體過於冗長
* 下面展示皆是用在網頁中加入標籤的方式執行React
	* Hello class繼承React.Component,在裡面我們回傳一個`<h1>`標籤,回傳的標籤只能有一個,若要回傳多個應包在一個頂層元素內,如:`div`
	* `ReactDOM.render()`並在裡面提供組件名稱`<Hello/>`及要渲染的DOM元素ID
* React中已有關鍵字class了,所以要加入class屬性須改用**className**
```html
<style>
.print {
	color:blue
}
</style>
<div id="main"></div>
<script type="text/babel">
	class Hello extends React.Component {
		render() {
			return <h1 className="print">Hello World</h1>;
		}
	}
	ReactDOM.render(<Hello />, doucument.getElementById("main"))
</script>
```

### Component屬性與函數
* 所有組件皆可在其他地方以`<className />`的方式隨時調用
* 在組件標籤內提供屬性可在組件內以`this.props.propName`取得該值
* 組件內的函數可傳遞給DOM元素的事件值
* 在JSX中調用JS變數`{parameter}`若變數型態為array,則所有值會自動展開成一連串的值顯示
```jsx
class Hello extends React.Component {
	render() {
        // 多行return用()包起
		return (<div>
            <h3>Hello {this.props.name}</h3>
            <button onClick={this.say}>click</button>
        </div>);
    }
		// 自訂函數
    say = () => alert(`say Hello ${this.props.name}`)
}

class App extends React.Component {
		render() {
			return (
			<div>
				<Hello name="Simon"/>
			</div>
			);
		}
}
ReactDOM.render(<Hello />, doucument.getElementById("main"))
```
* 有時我們會需要定義一些初始值在屬性中,在python中可以使用構造函數來定義,在這裡我們則是使用類似的構造函數來完成
* 在React中不允許直接更新構造函數的值,要更新該值我們須調用`this.setState()`函數
* 若直接在`setState()`函數中更新數值可能會因多使用者競爭導致更新到舊值,因此在該方法中傳入另一種方式的參數,我們改傳入一函數,該函數將具有一參數**oldState**,代表組件中state的最新值
* 更新時只會更新該物件有的屬性值,其他沒在物件內的屬性值不會更動到state中

```jsx
class Count extends React.Component {
    // 構造函數
    constructor(props) {
        // 每次需調用在頂部
        super(props)
        // 建立初始屬性 state有意義不可更改
        this.state = {
            count: 0
        }
    }
    render() {
        // 多行return用()包起
        return (
        <div>
            <h3>count: {this.state.count}</h3>
						// click更新count值
            <button onClick={this.increment}>Click</button>
        </div>
        );
    }

    increment = () => {
        /**
         * 調用setState()更新屬性值
         * 避免競爭所以使用傳入函數,oldState包含最新的state值
         */ 
        this.setState(oldState => ({
            count: oldState.count + 1
        }))
    }
}
```
>Component永遠不會更改props屬性的值,該屬性值可能由其他地方傳入, 而state通常代表組件的狀態,將來可能在某個時候改變

* 早期React不支援function Component使用state,但在較新版本中,加入了**hook**來補足該部分
* 首先引入相關模組
```jsx
import React, { useState } from 'react'; 
```
* 我們可以透過調用`useState(value)`函數來建立state變數,該函數返回兩個值分別儲存該參數以及setXXX函數,我們可以用**解構賦值**來接收
* 要更改state一樣須調用對應的set函數,不可值皆更改該值
```jsx
function Example() {
  // 宣告一個新的 state 變數，我們稱作為「count」。
  const [count, setCount] = useState(0);
	// 多個變數較調用多次useState
	const[name, setName] = usetState('David')

  return (
    <div>
      <p>You clicked {count} times</p>
      <button onClick={() => setCount(count + 1)}>
        Click me
      </button>
    </div>
  );
}
```

### Math_Game
* 下面我們實際建立一個數學小遊戲來更了解如何應用React Component
* 該遊戲具有題目,輸入框,分數等部分
* 首先在constructor中定義遊戲物件初始值並在`render()`中回傳DOM元素

```jsx
constructor(props) {
		super(props)
		this.state = {
				// 題目
				num1: Math.ceil(Math.random(9) * 10),
				num2: Math.ceil(Math.random(9) * 10),
				// 分數
				score: 0,
				// 輸入框
				response: ""
		}
}

render() {
		return (
				<div>
						<h2>{this.state.num1} + {this.state.num2}</h2>
						<input value={this.state.response} />
						<div>
								Score: {this.state.score}
						</div>
				</div>
		)
}
```
* 此時會發現在input中輸入內容不會顯示,這是因為response的值並未更改,所以React不會更新畫面,所以我們要加入`onChange()`事件
```jsx
<input onChange={this.updateResponse} value={this.state.response} />
// ...
updateResponse = event => {
		this.setState({
				// event是JS的事件屬性
				response: event.target.value
		})
}
```
* 接著要讓user輸入完後按下Enter時送出答案,我們在input內再加入`onKeyPress()`檢查按鍵輸入
```jsx
<input onChange={this.updateResponse} value={this.state.response} />
// ...
inputKeyPress = event => {
		// 按下Enter時,key是JS的事件屬性
		if (event.key === 'Enter'){
				// 先轉int再比較
				const ans = parseInt(this.state.response)
				if (ans === this.state.num1 + this.state.num2) {
						this.setState(state => ({
								// 分數加1
								score: state.score + 1,
								// 重新替換題目
								num1: Math.ceil(Math.random(9) * 10),
								num2: Math.ceil(Math.random(9) * 10),
								// 輸入框清空
								response: ""
						}))
				}
				else {
						this.setState({
								response: ""
						})
				}
		}
}
```
* 最後我們要加入勝利畫面,當分數到5分時顯示勝利畫面
* 我們將`render()`拆成兩個函數,分別表示遊戲及勝利畫面,最後由`render()`判斷要調用何者

```jsx
render() {
		if(this.state.score === 5)
				return this.renderWin()
		else
				return this.renderProblem()
}

renderWin() {
		return <h1>Congratulations! You win!</h1>
}

renderProblem() {
		return (
				<div>
						<h2>{this.state.num1} + {this.state.num2}</h2>
						<input onKeyPress={this.inputKeyPress} onChange={this.updateResponse} value={this.state.response} />
						<div>
								Score: {this.state.score}
						</div>
				</div>
		)
}
```

### Tasks
* 下面我們要再演示一個例子,該App可以顯示代辦事項,並且可以輸入並新增代辦事項
* 我們可以用map()函數遍歷陣列,並用`<li>`包住每個值,這樣便會產生一個代辦list
* React希望在`<li>`元素中提供key屬性(EX:`<li key={index}`),當更新其中一個`<li>`時便不用更新整個`<ul>`的內容
* 記住**永遠不要**直接更新state的值,當我們要更新task陣列時,應該要複製所有值到新陣列並加上/減去要更新的內容最後將新陣列傳給task屬性
* 首先我們建立一個Task Component,該組件提供user可以輸入並新增task
```jsx
class Task extends React.Component {
		constructor(props) {
				super(props)
				this.state = {
						task: ['aaa'],
						input: ""
				}
		}
		render() {
				return (
						<div>
								<h1>Tasks</h1>
								<ul>
										{
												// 遍歷task並加入li tag,li加入key提升效能
										}
										{this.state.task.map((task, i) => <li key={i}>{task}</li>)}
								</ul>
								<div>
										<input onChange={this.handleChange} value={this.state.input} />
										<button onClick={this.addTask}>Add Task</button>    
								</div>
						</div>
				)
		}
		handleChange = event => {
				this.setState({
						input: event.target.value
				})
		}

		addTask = () => {
				this.setState(state => ({
						// 以新陣列取代舊的而不是用push()加入
						task: [...state.task, state.input],
						input: ""
				}))
		}

}
ReactDOM.render(<Task />, document.querySelector("#main"))
```
* 接著我們要新增刪除功能讓我們可以把不需要的task刪除
* 為此我們需要傳入index參數,但`onClick()`內不能傳入參數,其中一種做法是在delete()函數外再包裹一個函數,這樣delete()便可傳入index

```jsx
<ul>
	{this.state.task.map((task, i) => 
			<li key={i}>
					{
							// 匿名函數retrun值為deleteTask(i),這樣才可傳入i值
					}
					<button onClick={() => this.deleteTask(i)}>Delete</button>
					{task}
			</li>
			)}
</ul>
// ...
deleteTask = (index) => {
		this.setState(state => {
				/**
				 * 若寫成
				 * task = [...this.state.task]
				 * 可能會產生競爭的潛在可能
				 */
				const task = [...state.task]
				// 刪除index的陣列值
				task.splice(index, 1)
				return {task: task}
		})
}
```
* 上面方法雖然可以解決問題,但每個button都要建一次匿名函數,效能上較不好,下面提供另一種解法可解決上述問題

```jsx
// 在button中加入data-index,這是html提供的自訂屬性值,傳入i給deleteTask函數使用
<button data-index={i} onClick={this.deleteTask}>Delete</button>
//...
deleteTask = (event) => {
		// event.target的dataset可取得自訂屬性名稱的值
		const index = event.target.dataset.index
		this.setState(state => {
				/**
				 * 若寫成
				 * task = [...this.state.task]
				 * 可能會產生競爭的潛在可能
				 */
				const task = [...state.task]
				// 刪除index的陣列值
				task.splice(index, 1)
				return {task: task}
		})
}
```