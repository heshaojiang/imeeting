import React, { Component } from 'react';
import { connect } from 'dva';
import { Card } from "antd";
import PageHeaderWrapper from '@/components/PageHeaderWrapper';


@connect(({ user }) => ({
  allType: user.allType
}))
export default class Service extends Component {
  state = {
    iframeHeight: 0,
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
            src={this.props.allType.sysMonitor && this.props.allType.sysMonitor[0].label}
          />
        </Card>
      </PageHeaderWrapper>
    );
  }
}