class Hello extends React.Component {
    constructor(props) {
        // 呼叫父類建構子,需加在頂部
        super(props)
        // 建立初始屬性 state有意義不可更改
        this.state = {
            name: props.name,
            age: 18,
            count:0
        }
    }
	render() {
        // 多行return用()包起
		return (<div>
            <h3>Hello {this.state.count}</h3>
            <button onClick={this.say}>click</button>
        </div>);
    }
    say = () => {
        this.setState(oldState => ({
            count: oldState.count + 1
        }))
    }
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
ReactDOM.render(<Count />, document.getElementById("main"))