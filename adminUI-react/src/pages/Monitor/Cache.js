import React, { Component } from 'react';
import { Card } from "antd";
import PageHeaderWrapper from '@/components/PageHeaderWrapper';


export default class Cache extends Component {
  state = {
    iframeHeight: 0
  }
  componentDidMount() {
    this.setState({
      iframeHeight: document.documentElement.clientHeight - 64 - 54 - 24 * 2 - 40 - 93
    });
  }
  render() {
    return (
      <PageHeaderWrapper>
        <Card>
          <iframe
            height={this.state.iframeHeight}
            width="100%"
            src="http://10.1.42.107:1025"
          />
        </Card>
      </PageHeaderWrapper>
    );
  }
}