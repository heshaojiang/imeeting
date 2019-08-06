import React, { PureComponent, Fragment, Component } from "react";
import { connect } from "dva";
import {
  Card,  Modal, Button, Form, Input, TreeSelect, Select, Row, Col, Icon, Upload, Message,
  Table, TimePicker , Tag 
} from "antd";
import moment from "moment";
import styles from ".././tableList.less";
import { validateField, validateAcdField } from "@/services/api";

import { formatSeconds } from '@/utils/utils';
import { formatMessage, getLocale } from 'umi/locale';

const Option = Select.Option;
const FormItem = Form.Item;

function onChange(time, timeString) {
    console.log(time, timeString);
  }

export default class CustomerMembers extends PureComponent {
  state = {
  };

  columns = [
      {
        title: formatMessage({ id: 'app.admin.order' }),
        dataIndex: "agnetId",
        render: (text, record, index) => index + 1
      },
      {
        title: formatMessage({ id: 'app.acd.evaluate.agentName' }),
        dataIndex: "userName",
      },
      {
        title: formatMessage({ id: 'app.acd.evaluate.countTimes' }),
        dataIndex: "serviceCount",
      },
      {
        title: formatMessage({ id: 'app.acd.evaluate.serviceLonger' }),
        dataIndex: "totalServiceTime",
        render: val => formatSeconds(val)
      },
      {
        title: formatMessage({ id: 'app.acd.evaluate.avgResponseLonger' }),
        dataIndex: "avgResponseTime",
        render: val => formatSeconds(val)
      },
      {
        title: formatMessage({ id: 'app.acd.evaluate.avgServiceLonger' }),
        dataIndex: "avgServiceTime",
        render: val => formatSeconds(val)
      }
  ];


  render = () => {
    const {
      loading,
      data,
      onPageChange,
      onPageSizeChange
    } = this.props;
    
    return (
        <div> 
            <Table 
            columns={this.columns} 
            dataSource={data.list} 
            rowKey='agentId'
            pagination={{
            ...data.pagination,
            showSizeChanger:true,
            onChange:onPageChange ,
            onShowSizeChange:onPageSizeChange}}/>
        </div>
    );
  };
}
