import React, {Component} from 'react';
import './TweetList.css';

interface Tweet {
  id: number;
  tweetId: number;
  text: string;
}

interface TweetListProps {
}

interface TweetListState {
  running: boolean;
  tweets: Array<Tweet>;
  topic: string;
}

class TweetList extends Component<TweetListProps, TweetListState> {

  eventSource: any;

  constructor(props: TweetListProps) {
    super(props);

    this.state = {
      running: false,
      tweets: [],
      topic: ""
    };
  }

  async componentDidMount() {
  }

  topicChange = (e: any) => {
    this.setState({topic: e.target.value});
  };

  startStream = () => {
    this.setState({running: true});
    const url = 'http://localhost:8080/sse/tweets?topic=' + this.state.topic;
    this.eventSource = new EventSource(url);
    this.eventSource.onopen = (event: any) => console.log('open', event);
    this.eventSource.onmessage = (event: any) => {
      const tweet = JSON.parse(event.data);
      this.state.tweets.unshift(tweet);
      this.setState({tweets: this.state.tweets});
    };
    this.eventSource.onerror = (event: any) => console.log('error', event);
  };

  killStream = () => {
    fetch('http://localhost:3000/sse/kill')
      .finally(() => {
        this.eventSource.close();
        this.eventSource = null;
        this.setState({running: false});
      });
  };

  renderControls(running: boolean) {
    if (running) {
      return (
        <div>
          <button onClick={this.killStream}>STOP!</button>
        </div>
      );
    } else {
      return (
        <div>
          <input type="text" id="topic" onChange={this.topicChange}/>
          <button onClick={this.startStream}>GO!</button>
        </div>
      );
    }
  }

  render() {
    const {running, tweets} = this.state;

    const controls = this.renderControls(running);
    return (
      <div>
        {controls}
        <h2>Tweet List</h2>
        {tweets.map((tweet: Tweet) =>
          <div key={tweet.id} className="tweet">
            <strong>{tweet.id}:</strong> {tweet.text}<br/>
          </div>
        )}
      </div>
    );
  }
}

export default TweetList;
