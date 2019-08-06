import React, { PureComponent, Fragment, Component } from "react";
import { connect } from "dva";
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
import { validateAcdField } from "@/services/api";
import { getObject, fetchSelect, fetchMembers } from "@/services/acdQueue";
import { formatMessage } from 'umi/locale';
import {isEmptyObject} from "@/utils/utils"
import CreateForm from './CreateForm'
import AgentModel from './AgentInfo'
const Option = Select.Option;
const FormItem = Form.Item;


@connect(({ queue, loading }) => ({
  queue,
  loading: loading.models.queue,
}))
@Form.create()
export default class Queue extends PureComponent {
  state = {
    selectedRows: [],
    modalStatus: "",
    modalVisible: false,
    modalMemberVisible: false,
    modalTitle: "",
    selectRowId:"",
    modalData: {},
    formValues: {},
    modalOk: () => { }
  };

  columns = [
    {
      title: formatMessage({ id: 'app.admin.order' }),
      dataIndex: "id",
      render: (text, record, index) => index + 1
    },
    {
      title: formatMessage({ id: 'app.acd.queue.queueName' }),
      dataIndex: "queueName"
    },
    {
      title: formatMessage({ id: 'app.acd.queue.description' }),
      dataIndex: "description"
    },
    {
      title: formatMessage({ id: 'app.acd.queue.strategy' }),
      dataIndex: "strategy",
      render: (val) => this.props.queue.queueStrategy.find(el => el.type === val).label
    },
    {
      title: formatMessage({ id: 'app.acd.queue.capacity' }),
      dataIndex: "maxNum"
    },
    {
      title: formatMessage({ id: 'app.acd.queue.agent.status' }),
      dataIndex: "totalAgentCount",
      render: (text, record) => (
        <Fragment>
          <a onClick={() => this.handleAgent(record.queueId)}>{record.totalAgentCount}</a>
        </Fragment>
      )
    },
    {
      title: formatMessage({ id: 'app.acd.queue.time' }),
      dataIndex: "createTime",
      render: val => val && moment(val).format('YYYY-MM-DD HH:mm:ss')
    },
    {
      title: formatMessage({ id: 'app.admin.operate' }),
      render: (text, record) => (
        <Fragment>
          <a onClick={() => this.handleUpdate(record.queueId)}>{formatMessage({ id: 'app.admin.update' })}</a>
          <Divider type="vertical" />
          <a onClick={() => this.handleDeleteUser(record.queueId)}>{formatMessage({ id: 'app.admin.delete' })}</a>
        </Fragment>
      )
    }
  ];

  componentDidMount() {
    const { dispatch } = this.props;

    dispatch({
      type: 'queue/strategy'
    });

    dispatch({
      type: "queue/fetch",
      payload: {}
    });
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
          type: "queue/delete",
          payload: id
        });
      }
    });
  };

  handleBatch = () => {
    const { dispatch } = this.props;
    const { selectedRows } = this.state;

    Modal.confirm({
      title: formatMessage({ id: 'app.admin.tips' }),
      content: formatMessage({ id: 'app.acd.queue.delete.batch.tips' }),
      okText: formatMessage({ id: 'app.admin.confirm' }),
      cancelText: formatMessage({ id: 'app.admin.cancel' }),
      onOk: () => {
        dispatch({
          type: "queue/delete",
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
          type: "queue/add",
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
            type: "queue/update",
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

  //队列客服
  handleAgent = queueId => {
    const { dispatch } = this.props;
    this.handleModalMemberVisible(true);
    dispatch({
      type: "queue/member",
      payload: {queueId},
      callback: () => {
        this.setState({
          modalTitle: formatMessage({ id: 'app.acd.queue.chiildren' }),
          selectRowId: queueId
        });
      }
    });

  }

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
      type: "queue/fetch",
      payload: params
    });
  };

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

  handleModalMemberVisible = flag => {
    this.setState({
      modalMemberVisible: !!flag
    })
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
        type: "queue/fetch",
        payload: values
      });
    });
  };

  handleFormReset = () => {
    const { dispatch, form } = this.props;
    form.resetFields();
    this.setState({
      formValues: {},
    });
    dispatch({
      type: "queue/fetch",
      payload: {}
    });
  };

  renderForm = () => {
    const {
      form: { getFieldDecorator }
    } = this.props;
    return (
      <Form onSubmit={this.handleSearch} layout="inline">
        <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
          <Col md={8} sm={24}>
            <FormItem label={formatMessage({ id: 'app.acd.queue.search' })}>
              {getFieldDecorator("keyword", {
                rules: [
                  {
                    pattern: new RegExp('^[\u4e00-\u9fa5a-zA-Z0-9]+$', 'g'),
                    message: formatMessage({ id: 'app.admin.search.keyword' }),
                  }
                ]
              })(
                <Input placeholder={formatMessage({ id: 'app.admin.keyword' })} />
              )}
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
    );
  };

  render = () => {
    const {
      queue: { data, queueStrategy ,members},
      loading,
      dispatch
    } = this.props;

    const {
      selectedRows,
      modalVisible,
      modalMemberVisible,
      modalTitle,
      modalData,
      modalOk,
      modalStatus,
      selectRowId
    } = this.state;

    const parentMethods = {
      handleAdd: modalOk,
      handleModalVisible: this.handleModalVisible,
    };

    const parentProps = {
      modalTitle,
      modalData,
      modalStatus,
      strategy: queueStrategy
    };

    const onlineProps = {
      modalTitle,
      members,
      modalMemberVisible,
      queueId:selectRowId
    }
    const onlineMethod={
      handleModalMemberVisible:this.handleModalMemberVisible,
      dispatch:dispatch

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
                  onClick={this.handleBatch}
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
              pagination={{
                pageSize:1
              }}
              rowKey="queueId"
              onChange={this.handleStandardTableChange}
            />
          </div>
        </Card>
        <CreateForm
          {...parentMethods}
          {...parentProps}
          modalVisible={modalVisible}
        />

        <AgentModel
        loading={loading}
          {...onlineProps}
          {...onlineMethod}
           />
      </PageHeaderWrapper >
    );
  };
}
