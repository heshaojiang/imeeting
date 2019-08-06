import React, { PureComponent} from "react";
import CallDay from './CallDay' ;
import CallHour from './CallHour'
import {
  Card, Row, Col,Button
} from "antd";
import { formatMessage, getLocale } from 'umi/locale';
import PageHeaderWrapper from "@/components/PageHeaderWrapper";

const tabList = [
  {
    key: 'hour',
    tab: formatMessage({ id: 'app.acd.report.statistic.hour' }),
  },
  {
    key: 'day',
    tab: formatMessage({ id: 'app.acd.report.statistic.day' }),
  },
];

class CallReport extends PureComponent {
    state = {
        key: 'hour',
        tabTitle:'今日数据',
    };

    onTabChange = (key, type) => {
        this.setState({ [type]: key });
    };

   render = () => {
    const contentList = {
    hour: <CallHour />,
    day: <CallDay />,
    };
    return(
        <div style={{ background: '#ECECEC', padding: '15px' }}>
            <Card
            style={{ width: '100%' }}
            tabList={tabList}
            activeTabKey={this.state.key}
            onTabChange={key => {
                this.onTabChange(key, 'key');
            }}
            >
            {contentList[this.state.key]}
            </Card>
        </div>
    )
   }
}

export default CallReport;