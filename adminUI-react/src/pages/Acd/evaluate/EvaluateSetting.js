import React, { PureComponent, Fragment, Component } from "react";
import { connect } from "dva";
import {
  Card,  Modal, Button, Form,Divider,Rate,Table, TimePicker,Tag 
} from "antd";
import styles from ".././tableList.less";

import { getScore } from "@/services/acdEvaluation";
import { formatMessage, getLocale } from 'umi/locale';
import CreateForm from './CreateSettingForm'
  
@Form.create()
export default class EvaluateSetting extends PureComponent {
  state = {
    modalStatus: "",
    modalVisible: false,
    modalTitle: "",
    modalData: {},
    modalOk: () => { },
  };

  columns = [
        {
        title: formatMessage({ id: 'app.admin.order' }),
        dataIndex: 'id',
        render: (text, record, index) => index + 1
        },
        {
        title: formatMessage({ id: 'app.acd.evaluate.evaluatename' }),
        dataIndex: "name",
        },
        {
        title: formatMessage({ id: 'app.acd.evaluate.score' }),
        dataIndex: "scoreValue",
        render: (text, record, index) => (
          <Rate allowHalf  defaultValue={record.scoreValue} disabled={true}  count={5}/> 
          )
        },
        {
        title: formatMessage({ id: 'app.acd.evaluate.evaluatenote' }),
        dataIndex: "desc",
        },
        {
        title: formatMessage({ id: 'app.admin.operate' }),
        render: (text, record) => (
          <Fragment>
            <a onClick={() => this.handleUpdate(record.id)}>{formatMessage({ id: 'app.admin.update' })}</a>
            <Divider type="vertical" />
            <a onClick={() => this.handleDelete(record.id)}>{formatMessage({ id: 'app.admin.delete' })}</a>
          </Fragment>
        )
        }
    ];

  // 新建
  handleCreate = () => {
    const { dispatch } = this.props;
    this.setState({
      modalStatus: "create",
      modalTitle: formatMessage({ id: 'app.admin.add' }),
      modalData: {},
      modalOk: data => {
        dispatch({
          type: "evaluation/addScore",
          payload: data
        });
        this.handleModalVisible();
      }
    });
    this.handleModalVisible(true);
  };

  // 更新
  handleUpdate = id => {
    getScore(id).then(data => {
      this.handleModalVisible(true);
      this.setState({
        modalStatus: "update",
        modalTitle: formatMessage({ id: 'app.admin.update' }),
        modalData: data,
        modalOk: fields => {
          const { dispatch } = this.props;
          dispatch({
            type: "evaluation/updateScore",
            payload: {
              ...data,
              ...fields
            }
          });
          this.handleModalVisible();
        }
      });
    });
  };
  //删除
  handleDelete = id => {
    const { dispatch } = this.props;
    Modal.confirm({
      title: formatMessage({ id: 'app.admin.tips' }),
      content: formatMessage({ id: 'app.acd.evaluate.score.delete.tips' }),
      okText: formatMessage({ id: 'app.admin.confirm' }),
      cancelText: formatMessage({ id: 'app.admin.cancel' }),
      onOk: () => {
        dispatch({
          type: "evaluation/deleteScore",
          payload: id
        });
      }
    });
  };

  handleModalVisible = flag => {
    this.setState({
      modalVisible: !!flag
    });
  };

  render = () => {

    const {
      modalVisible,
      modalTitle,
      modalData,
      modalOk,
      modalStatus,
    } = this.state;

    const {score,loading,modalType}=this.props

    const parentMethods = {
      handleAdd: modalOk,
      handleModalVisible: this.handleModalVisible
    };

    const parentProps = {
      modalTitle,
      modalData,
      modalStatus,
      modalType
    };
    return (
     <div>
        <Card bordered={false}>
          <div className={styles.tableList}>
          <div className={styles.tableListOperator}>
              <Button icon="plus" type="primary" onClick={this.handleCreate}>
              {formatMessage({ id: 'app.admin.add' })}
              </Button>
          </div>
            <Table 
            dataSource={score} 
            columns={this.columns} 
            style={{ margin: '25px 0'}} 
            loading={loading}
            rowKey="id"
            pagination={false}
            />
          </div>
      </Card>
        <CreateForm
            {...parentMethods}
            {...parentProps}
            modalVisible={modalVisible}
        />
    </div> 
    );
  };
}
