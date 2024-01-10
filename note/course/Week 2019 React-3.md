# Week 2019 React-3
---
### LifeCycle Method
* 在React中生命週期分為3種,分別會調用對應的方法
	* **Mounting** 當組件在頁面上被呼叫時,此時會呼叫`componentDidMount()`
	* **Updated** 當組件在頁面上更新畫面時,此時會呼叫`componentDidUpdate()`
	* **Unmounting** 當組件從頁面上刪除時,此時會呼叫`componentWillUnmount()`
* `componentDidMount()`和`componentDidUpdate()`會在事件函數執行前被調用,若我們要為state變數設置其他值可以在對應方法中設置
* 詳細介紹參考官網[React.Component – React](https://zh-hant.reactjs.org/docs/react-component.html)
* 下面簡單示範如何使用生命週期方法

```jsx
// 頁面載入時觸發 抓取local儲存的值
componentDidMount() {
		// 載入畫面時從localStorage抓取count的值
		const count = localStorage.getItem("count");
		if (count !== null) {
				this.setState({
						count: parseInt(count)
				})
		}
		// window事件,在關閉頁面前觸發,將count變數設給localStorage
		// https://developer.mozilla.org/zh-TW/docs/Web/Events/load
		window.addEventListener("beforeunload", () => {
				localStorage.setItem("count", this.state.count)
		})
}
```

### Exchange rates
* 接著我們要示範匯率轉換功能,該程式會從第三方API抓取最新匯率資料並將我們輸入的貨幣轉換成其他貨幣
* 該功能具有兩個select及input標籤,select選項為要轉換的幣別,input輸入轉換金額
#### API
* 我們使用的API從[exchange rates API](https://exchangeratesapi.io/)取得,該API返回json()格式的資料
* `fetch()`函數是取代舊版ajax寫法的新函數,它擁有更高的效率及更簡便的語法,並且也支持promise語句

#### Caching
* 過於頻繁地向API發出請求可能會造成幾點問題
	1. 若網速不足每次請求都要等待時間
	2. 增加server負擔
	3. 若是第三方API可能會有請求限制,超過會限制存取
* 基於以上幾點,cache會是一種不錯的選擇,我們可以將ajax取得的內容存於緩存,一定時間內(可能是幾分鐘,或幾小時)發出一樣的請求時便從cache中抓取資料,這樣就不需要一直向server發出請求

```jsx
class App extends React.Component {
		constructor(props) {
				super(props)
				// 儲存下拉要顯示哪些貨幣
				this.currencies = ['AUD', 'CAD', 'CHF', 'CNY', 'INR', 'USD', 'EUR', 'GBP', 'JPY', 'NZD']
				// ajax取得的資料存在這裡 一分鐘內不發出重複的請求
				this.cached = {}
				this.state = {
						base: 'USD',
						other: 'EUR',
						value: 0,
						converted: 0
				}
		}
		render() {
				return (
						<div>
								<div>
										<select onChange={this.makeSelection} name="base" value={this.state.base}>
												{this.currencies.map(curreny => <option key={curreny} value={curreny}>{curreny}</option>)}
										</select>
										<input name="value" onChange={this.changeValue} value={this.state.value} />
								</div>
								<div>
										<select onChange={this.makeSelection} name="other" value={this.state.other}>
												{this.currencies.map(curreny => <option key={curreny} value={curreny}>{curreny}</option>)}
										</select>
										<input disabled={true} value={this.state.converted === null ? 'Calculate...' : this.state.converted} />
								</div>
						</div>)
		}

		// 下拉選單切換時也要轉換貨幣
		makeSelection = (event) => {
				this.setState({
						[event.target.name]: event.target.value
				}, this.recalculate)
		}

		changeValue = (event) => {
				/**
				 *  防止異步先完成導致recalculate()比setState()先執行
				 *  setState()第2參數是callback函數,當setState()參數更新後才執行callback函數
				*/
				this.setState({
						[event.target.name]: event.target.value,
						// 當ajax在loading時converted顯示loading文字
						converted: null
				}, this.recalculate)
		}

		recalculate = () => {
				const value = parseFloat(this.state.value)
				// 不是數字時不做轉換
				if (isNaN(value)) {
						this.setState({
								converted: 0
						})
						return
				}

				// 若之前有發出一樣的請求且距離上次發出小於1分鐘時,資料改從cached撈出
				if (this.cached[this.state.base] !== undefined && Date.now() - this.cached[this.state.base].timestamp < 60 * 1000) {
						this.setState({
								converted: this.cached[this.state.base].rates[this.state.other] * value
						})
						return
				}

				// ajax新版函數
				fetch(`https://api.exchangeratesapi.io/latest?base=${this.state.base}`)
				.then(response => response.json())
				.then(data => {
						// response data儲存在緩存
						this.cached[this.state.base] = {
								// 儲存data
								rates: data.rates,
								// 紀錄這次ajax取得資料的時間
								timestamp: Date.now()
						}
						this.setState({
								converted: data.rates[this.state.other] * value
						})
				})
		}
}
ReactDOM.render(<App />, document.querySelector("#main"))
```
