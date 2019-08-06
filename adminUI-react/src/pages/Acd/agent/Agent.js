import React, { PureComponent, Fragment, Component } from "react";
import { connect } from "dva";
import {
  Card, Divider, Modal, Button, Form, Input, TreeSelect, Select, Row, Col, Icon, Upload, Message
} from "antd";
import moment from "moment";
import PageHeaderWrapper from "@/components/PageHeaderWrapper";
import StandardTable from "@/components/StandardTable";
import styles from "../tableList.less";
import { validateField, validateAcdField } from "@/services/api";
import CreateForm from './CreateForm'
import QueueModel from './QueueInfo'

import { getObject } from "@/services/acdAgent";
import { formatMessage, getLocale } from 'umi/locale';

const Option = Select.Option;
const FormItem = Form.Item;

@connect(({ agent,queue, loading, user }) => ({
  agent,
  queueStrategy:queue.queueStrategy,
  loading: loading.models.agent,
  allType: user.allType
}))
@Form.create()
export default class Agent extends PureComponent {
  state = {
    selectedRows: [],
    modalStatus: "",
    modalVisible: false,
    modalQueueVisible: false,
    modalTitle: "",
    selectRowId:"",
    changeStatusId:'',
    modalData: {},
    formValues: null,
    modalOk: () => { },
  };

  columns = [
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
      title: formatMessage({ id: 'app.admin.user.nickname' }),
      dataIndex: 'nickname'
    },
    {
      title: formatMessage({ id: 'app.admin.phone' }),
      dataIndex: 'call'
    },
    {
      title: formatMessage({ id: 'app.admin.email' }),
      dataIndex: 'email'
    },
    {
      title: formatMessage({ id: 'app.admin.user.create.date' }),
      dataIndex: 'createTime',
      render: val => moment(val).format('YYYY-MM-DD HH:mm:ss')
    },
    {
      title: formatMessage({ id: 'app.admin.user.login.date' }),
      dataIndex: 'loginTime',
      render: val => val && moment(val).format('YYYY-MM-DD HH:mm:ss')
    },
    {
      title: formatMessage({ id: 'app.acd.agent.queue' }),
      dataIndex: 'queueCount',
     render: (text, record) => (
        <Fragment>
          <a onClick={() => this.handleQueue(record.userId)}>{record.queueCount}</a>
        </Fragment>
      )
    },
    {
      title: formatMessage({ id: 'app.admin.status' }),
      dataIndex: 'status',
       render: (text, record,index) => (
        <Fragment>
            {this.state.changeStatusId===record.userId ? ( <Select 
            style={{width:120}} 
            value={text}
            onChange={(value)=>this.updateStatus(value,record.userId)}
            onBlur={()=>{this.setState({changeStatusId:''})}}
            >
              {this.handleStatusOption(this.props.agent.agentStatus)}

            </Select>)
             :
              (text==='OFFLINE'? formatMessage({ id: 'app.acd.agent.status.'+text }):<Button type="link" onClick={()=>this.handleOpenSelect(record.userId,text)}>{formatMessage({ id: 'app.acd.agent.status.'+text })}</Button>)}
           
        </Fragment>
      )
    },
    {
      title: formatMessage({ id: 'app.admin.operate' }),
      render: (text, record) => (
        <Fragment>
          <a onClick={() => this.handleUpdate(record.userId)}>{formatMessage({ id: 'app.admin.update' })}</a>
          <Divider type="vertical" />
          <a onClick={() => this.handleDeleteUser(record.userId)}>{formatMessage({ id: 'app.admin.delete' })}</a>
        </Fragment>
      )
    }
  ];

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: "agent/fetch",
      payload: {}
    });
    dispatch({
      type: "agent/status",
      payload: {}
    });
  }

  //status列表
  handleStatusOption=(statusList)=>{
    let exclude=['SERVICE','SELECTED'];
    return (
    statusList.map(el => <Option key={el}  value={el} disabled={exclude.indexOf(el)!==-1}>{formatMessage({ id: 'app.acd.agent.status.'+el })}</Option>)
    )
  }

  //打开状态选择select
  handleOpenSelect=(userId,status)=>{
    if(status!=='OFFLINE'){
      this.setState({changeStatusId:userId})
    }else{
       this.setState({changeStatusId:''})
    }
  }

  //更新agent状态
  updateStatus=(status,userId)=>{
    const { dispatch } = this.props;
    dispatch({
        type: "agent/changeStatus",
        payload: {userId:userId,status:status}
      });
    this.setState({changeStatusId:''});
  }

  handleDeleteUser = id => {
    const { dispatch } = this.props;
    Modal.confirm({
      title: formatMessage({ id: 'app.admin.tips' }),
      content: formatMessage({ id: 'app.acd.queue.delete.tips' }),
      okText: formatMessage({ id: 'app.admin.confirm' }),
      cancelText: formatMessage({ id: 'app.admin.cancel' }),
      onOk: () => {
        dispatch({
          type: "agent/delete",
          payload: id
        });
      }
    });
  };

  handleBatchUser = () => {
    const { dispatch } = this.props;
    const { selectedRows } = this.state;

    Modal.confirm({
      title: formatMessage({ id: 'app.admin.tips' }),
      content: formatMessage({ id: 'app.acd.queue.delete.batch.tips' }),
      okText: formatMessage({ id: 'app.admin.confirm' }),
      cancelText: formatMessage({ id: 'app.admin.cancel' }),
      onOk: () => {
        dispatch({
          type: "agent/delete",
          payload: selectedRows.map(el => el.id).join(","),
          callback: () => {
            this.setState({
              selectedRows: []
            });
          }
        });
      }
    });
  };
  // 新建
  handleCreate = () => {
    const { dispatch } = this.props;

    this.setState({
      modalStatus: "create",
      modalTitle: formatMessage({ id: 'app.admin.add' }),
      modalData: {},
      modalOk: data => {
        dispatch({
          type: "agent/add",
          payload: data
        });
        this.handleModalVisible();
      }
    });
    this.handleModalVisible(true);
  };

  // 更新
  handleUpdate = id => {
    getObject(id).then(data => {
      this.handleModalVisible(true);

      this.setState({
        modalStatus: "update",
        modalTitle: formatMessage({ id: 'app.admin.update' }),
        modalData: data,
        modalOk: fields => {
          const { dispatch } = this.props;
          dispatch({
            type: "agent/update",
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

  handleStandardTableChange = (pagination, filtersArg, sorter) => {
    const { dispatch } = this.props;
    const { formValues } = this.state;

    const filters = Object.keys(filtersArg).reduce((obj, key) => {
      const newObj = { ...obj };
      newObj[key] = getValue(filtersArg[key]);
      return newObj;
    }, {});

    const params = {
      page: pagination.current,
      limit: pagination.pageSize,
      ...formValues,
      ...filters
    };

    if (sorter.field) {
      params.sorter = `${sorter.field}_${sorter.order}`;
    }

    dispatch({
      type: "agent/fetch",
      payload: params
    });
  };

  handleModalQueueVisible = flag => {
    this.setState({
      modalQueueVisible: !!flag
    })
  };

  //客服队列
  handleQueue = agentId => {
    const { dispatch } = this.props;
    console.log(this.props);
    this.handleModalQueueVisible(true);
    dispatch({
      type: "agent/queue",
      payload: {agentId},
      callback: () => {
        this.setState({
          modalTitle: formatMessage({ id: 'app.acd.agent.queue' }),
          selectRowId: agentId
        });
      }
    });

    dispatch({
      type: 'queue/strategy'
    });
  }

  handleSelectRows = rows => {
    this.setState({
      selectedRows: rows
    });
  };

  handleModalVisible = flag => {
    this.setState({
      modalVisible: !!flag
    });
  };

  handleSearch = e => {
    e.preventDefault();

    const { dispatch, form } = this.props;

    form.validateFields((err, fieldsValue) => {
      if (err) return;
      const values = {
        ...fieldsValue,
      };
      this.setState({
        formValues: values,
      });
      dispatch({
        type: "agent/fetch",
        payload: values
      });
    });
  };

  handleFormReset = () => {
    const { dispatch, form } = this.props;
    form.resetFields();
    this.setState({
      formValues: null,
  });
    dispatch({
      type: "agent/fetch",
      payload: {}
    });
  };

  renderForm = () => {
    const {
      form: { getFieldDecorator },
    } = this.props;
    return (
      <Form onSubmit={this.handleSearch} layout="inline">
        <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
          <Col md={8} sm={24}>
            <FormItem label={formatMessage({ id: 'app.acd.agent.search' })}>
              {getFieldDecorator('keyword', {
                rules: [
                  {
                    pattern: new RegExp('^[\u4e00-\u9fa5a-zA-Z0-9]+$', 'g'),
                    message: formatMessage({ id: 'app.admin.search.keyword' }),
                  }
                ]
              })(<Input placeholder={formatMessage({ id: 'app.admin.keyword' })} />)}
            </FormItem>
          </Col>
          <Col md={8} sm={24}>
            <span className={styles.submitButtons}>
              <Button type="primary" htmlType="submit">
                {formatMessage({ id: 'app.admin.search' })}
              </Button>
              <Button style={{ marginLeft: 8 }} onClick={this.handleFormReset}>
                {formatMessage({ id: 'app.admin.reset' })}
              </Button>
            </span>
          </Col>
        </Row>
      </Form>
    )
  }

  render = () => {
    const {
      agent: { data ,queue},
      loading,
      allType,
      queueStrategy
    } = this.props;
    const {
      selectedRows,
      modalVisible,
      modalTitle,
      modalData,
      modalOk,
      modalStatus,
      modalQueueVisible,
      selectRowId
    } = this.state;

    const parentMethods = {
      handleAdd: modalOk,
      handleModalVisible: this.handleModalVisible
    };

    const parentProps = {
      modalTitle,
      modalData,
      modalStatus,
      modalType: allType
    };

    const queueProps = {
      modalTitle,
      queue,
      modalQueueVisible,
      queueStrategy,
      agentId:selectRowId
    }
    const queueMethod={
      handleModalQueueVisible:this.handleModalQueueVisible,

    }

    return (
      <PageHeaderWrapper>
        <Card bordered={false}>
          <div className={styles.tableList}>
            <div className={styles.tableListForm}>{this.renderForm()}</div>
            <div className={styles.tableListOperator}>
              <Button icon="plus" type="primary" onClick={this.handleCreate}>
                {formatMessage({ id: 'app.admin.add' })}
              </Button>
              {selectedRows.length > 0 && (
                <Button
                  icon="delete"
                  type="primary"
                  onClick={this.handleBatchUser}
                >
                  {formatMessage({ id: 'app.admin.delete.batch' })}
                </Button>
              )}
            </div>
            <StandardTable
              selectedRows={selectedRows}
              loading={loading}
              data={data}
              columns={this.columns}
              onSelectRow={this.handleSelectRows}
              rowKey="userId"
              onChange={this.handleStandardTableChange}
            />
          </div>
        </Card>
        <CreateForm
          {...parentMethods}
          {...parentProps}
          modalVisible={modalVisible}
        />

        <QueueModel
         loading={loading}
          {...queueProps}
          {...queueMethod}
           />
      </PageHeaderWrapper>
    );
  };
}
