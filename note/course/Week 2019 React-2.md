# Week 2019 React-2
---
### FlashCard
* 在這個範例中,我們會製作自己的FlashCard,該頁面具有以下兩種畫面,透過切換紐在兩個畫面間做切換
	* 畫面1 => 新增/刪除flashCard
	* 畫面2 => 查看卡片,點擊可反轉正反面
* 要實現兩個畫面,我們要新增兩個Component,並透過呼叫他們的App Component來決定切換時機
```jsx
// 新增/刪除卡片
class CardEditor extends React.Component {
		render() {
				return (
						<div>
								This is Editor
								<hr />
								<button onClick={this.props.switchMode}> Go to Viewer </button>
						</div>)
		}
}

// 顯示卡片
class CardViewer extends React.Component {
		render() {
				return (
						<div>
								This is Viewer
								<hr />
								<button onClick={this.props.switchMode}> Go to Editor </button>
						</div>)
		}
}

class App extends React.Component {
		constructor(props) {
				super(props)
				this.state = {
						// 切換畫面
						editor: true,
						// 卡片資料,要傳遞在兩個畫面之間
						cards: []
				}
		}
		render() {
				if (this.state.editor)
						return <CardEditor switchMode={this.switchMode}/>
				else
						return <CardViewer switchMode={this.switchMode} />
		}

		// 切換畫面
		switchMode = () => {
				this.setState(state => ({
						editor: !state.editor
				}))
		}
}
```
* 接著我們先來完成**EditorCard**組件,要在不同Component之間傳遞值主要依靠**props**屬性
* 若要在A組件修改B組件的state屬性,應該在B加入修改的函數並透過props傳給A,在A中將要修改的值做為參數傳給該函數並呼叫

```jsx
// 新增/刪除卡片
class CardEditor extends React.Component {
		constructor(props) {
				super(props)
				this.state = {
						front: "",
						back: ""
				}
		}
		render() {
				const rows = this.props.cards.map((card, i) => {
								return(
								<tr key={i}>
										<td>{card.front}</td>
										<td>{card.back}</td>
										<td><button data-index={i} onClick={this.deleteCard}>Delete</button></td>
								</tr>
						);
				})
				return (
						<div>
								<h2>Card Editor</h2>
								<table>
										<thead>
												<tr>
														<th>Front</th>    
														<th>Back</th>    
														<th>Delete</th>    
												</tr>    
										</thead>
										<tbody>
												{rows}
										</tbody>
								</table>
								<br/>
								<input onChange={this.handleChange} name="front" placeholder="Front of Card" value={this.state.front} />
								<input onChange={this.handleChange} name="back" placeholder="Back of Card" value={this.state.back} />
								<button onClick={this.addCard}>Add Card</button>
								<hr/>
								<button onClick={this.props.switchMode}> Go to Viewer </button>
						</div>)
		}

		// event.target可以取得該元素屬性值,我們可以用name的值動態改變state屬性
		handleChange = (event) => {
				this.setState({
						[event.target.name]: event.target.value
				})
		}

		// 加入卡片的click事件
		addCard = () => {
				this.props.addCard(this.state.front, this.state.back)
				this.setState({
						front: "",
						back: ""
				})
		}

		// 刪除卡片的click事件 傳入要刪除的index
		deleteCard = (event) => this.props.deleteCard(event.target.dataset.index);
}

// 主程式Component
class App extends React.Component {
		constructor(props) {
				super(props)
				this.state = {
						// 切換畫面
						editor: true,
						// 卡片資料,要傳遞在兩個畫面之間
						cards: [{front:'aaa',back:'ssss'}]
				}
		}
		render() {
				if (this.state.editor) {
						return (
						<CardEditor 
								cards={this.state.cards} 
								switchMode={this.switchMode}
								addCard={this.addCard} 
								deleteCard={this.deleteCard}
						/>)
				} else {
						return (
						<CardViewer    
								cards={this.state.cards} 
								switchMode={this.switchMode}
						/>)
				}
		}

		// 切換畫面
		switchMode = () => {
				this.setState(state => ({
						editor: !state.editor
				}))
		}

		// 加入新卡片
		addCard = (front, back) => {
				this.setState(state => ({
						cards:[...state.cards, {front:front, back:back}]
				}))
		}

		// 刪除卡片
		deleteCard = (index) => {
				this.setState(state => {
						const cards = [...state.cards]
						cards.splice(index, 1)
						return {
								cards: cards
						}
				})
		}
}
```
* 接著我們來加入卡片畫面,此畫面要顯示卡片及正面文字,點擊卡片時可以翻面看到背面的文字,點擊next/last按紐時要切換到下一個/上一個卡片
* 卡片頁面的功能不多,主要是需要了解如何切換卡片及如何顯示正反面
* 須注意當卡片數為0時該如何處理才不會報錯

```jsx
// 顯示卡片Component
class CardViewer extends React.Component {
		constructor(props) {
				super(props)
				this.state = {
						// 目前顯示的卡片
						current: 0,
						// 卡片正反面
						isFront: true
				}
		}
		render() {
				// 沒有卡片時顯示
				if (this.props.cards.length === 0) {
						return (
						<div>
								<h2>Card Viewer</h2>
								<h3>No any card</h3>
								<hr />
								<button onClick={this.props.switchMode}> Go to Editor </button>
						</div>)
				}
				// 有卡片時顯示 卡片正反顯示不同css及文字
				const cur = this.state.current
				return (
						<div>
								<h2>Card Viewer</h2>
								<div onClick={this.turnOverCard} className={this.state.isFront ? "card card_front" : "card card_back"}>
										{this.state.isFront ? this.props.cards[cur].front : this.props.cards[cur].back}    
								</div>
								<button onClick={this.lastCard} style={{"marginTop":"20px"}}>Last Card</button>
								<button onClick={this.nextCard} style={{"marginTop":"20px"}}>Next Card</button>
								<hr />
								<button onClick={this.props.switchMode}> Go to Editor </button>
						</div>)
		}

		// 翻轉卡片
		turnOverCard = () => {
				this.setState(state => ({
						isFront: !state.isFront
				}))
		}

		// 上一張
		lastCard = () => {
		this.setState(state => ({
				current: state.current - 1 < 0 ? this.props.cards.length - 1 : state.current - 1
				}))
		}

		// 下一張
		nextCard = () => {
		this.setState(state => ({
				current: (state.current + 1) % this.props.cards.length
				}))
		}
}
```