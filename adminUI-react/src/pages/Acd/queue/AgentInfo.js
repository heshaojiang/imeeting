import React, { PureComponent, Fragment, Component } from "react";
import {
  Card,
  Divider,
  Modal,
  Button,
  Form,
  Input,
  Select,
  Row,
  Col,
  InputNumber,
  Table,
} from "antd";
import moment from "moment";
import PageHeaderWrapper from "@/components/PageHeaderWrapper";
import StandardTable from "@/components/StandardTable";
import styles from "../tableList.less";
import { formatMessage, getLocale } from 'umi/locale';
import {isEmptyObject} from "@/utils/utils"

class AgentInfo extends PureComponent {
  cancelHandle = () => {
    const { handleModalMemberVisible } = this.props;
    handleModalMemberVisible();
  };


  handleTableChange = (pagination, filtersArg, sorter) => {
    const { dispatch,queueId } = this.props;

    const filters = Object.keys(filtersArg).reduce((obj, key) => {
      const newObj = { ...obj };
      newObj[key] = getValue(filtersArg[key]);
      return newObj;
    }, {});

    const params = {
      page: pagination.current,
      limit: pagination.pageSize,
      ...filters
    };

    if (sorter.field) {
      params.sorter = `${sorter.field}_${sorter.order}`;
    }
    dispatch({
      type: "queue/member",
      payload: {
        queueId,
        params
      }
    });
  };


  render = () => {
    const columns = [
      {
        title: formatMessage({ id: 'app.admin.order' }),
        dataIndex: 'userId',
        render: (text, record, index) => index + 1
      },
      {
        title: formatMessage({ id: 'app.admin.user.username' }),
        dataIndex: 'username'
      },
      {
        title: formatMessage({ id: 'app.admin.user.login.date' }),
        dataIndex: 'loginTime',
        render: val =>val && moment(val).format('YYYY-MM-DD HH:mm')
      },
      {
        title: formatMessage({ id: 'app.admin.status' }),
        dataIndex: 'status',
        render: val => getLocale() === 'zh-CN' ? formatMessage({ id: 'app.acd.agent.status.' + val }) : val
      }
    ];

    const {
      members,
      modalMemberVisible,
      modalTitle,
      loading
    } = this.props;

    let data=[];
    if(!isEmptyObject(this.props.members)){
      data=members.list;
    }
    
    return (
      <Modal
        destroyOnClose
        title={modalTitle}
        visible={modalMemberVisible}
        onCancel={this.cancelHandle}
        footer={null}
        width={600}>
        <div className={styles.tableList}>
          <Table 
          loading={loading}
          columns={columns} 
          dataSource={data} 
          onChange={this.handleTableChange}
          pagination={{
            ...members.pagination,
            showSizeChanger:true,
          }}
          rowKey="userId"/>
        </div>
      </Modal>
    )
  }
}

export default AgentInfo;