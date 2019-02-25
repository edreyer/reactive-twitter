import React, { Component } from 'react';
import './TweetList.css';

interface Tweet {
  id: number;
  text: string;
}

interface TweetListProps {
}

interface TweetListState {
  tweets: Array<Tweet>;
  isLoading: boolean;
}

class TweetList extends Component<TweetListProps, TweetListState> {

  constructor(props: TweetListProps) {
    super(props);

    this.state = {
      tweets: [],
      isLoading: false
    };
  }

  async componentDidMount() {
    //this.setState({isLoading: true});

    const eventSource = new EventSource('http://localhost:8080/sse/tweets');
    eventSource.onopen = (event: any) => console.log('open', event);
    eventSource.onmessage = (event: any) => {
      const tweet = JSON.parse(event.data);
      this.state.tweets.push(tweet);
      this.setState({tweets: this.state.tweets});
    };
    eventSource.onerror = (event: any) => console.log('error', event);
  }

  render() {
    const {tweets, isLoading} = this.state;

    if (isLoading) {
      return <p>Loading...</p>;
    }

    return (
      <div>
        <h2>Tweet List</h2>
        {tweets.map((tweet: Tweet) =>
          <div key={tweet.id} className="tweet">
            {tweet.text}<br/>
          </div>
        )}
      </div>
    );
  }
}

export default TweetList;
