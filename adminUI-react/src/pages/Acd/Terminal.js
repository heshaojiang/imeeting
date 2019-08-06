import React, { PureComponent, Fragment, Component } from "react";
import { connect } from "dva";
import {
  Card, Divider, Modal, Button, Form, Input, TreeSelect, Select, Row, Col, Icon, Upload, Message,DatePicker,Switch
} from "antd";
import moment from "moment";
import PageHeaderWrapper from "@/components/PageHeaderWrapper";
import StandardTable from "@/components/StandardTable";
import styles from "./tableList.less";
import { validateField, validateAcdField } from "@/services/api";

import { getPhoneNumPattern } from '@/utils/utils';
import { getObject } from "@/services/acdTerminal";
import { formatMessage, getLocale } from 'umi/locale';

const Option = Select.Option;
const FormItem = Form.Item;

@Form.create({
  mapPropsToFields(props) {
    return {
      terminalNo: Form.createFormField({
        value: props.modalData.terminalNo,
      }),
      name: Form.createFormField({
        value: props.modalData.name,
      }),
      model: Form.createFormField({
        value: props.modalData.model,
      }),
       machineCode: Form.createFormField({
        value: props.modalData.machineCode,
      }),
      accessType: Form.createFormField({
        value: props.modalData.accessType,
      }),
      status: Form.createFormField({
        value: props.modalData.status,
      }),

    }
  }
})

class CreateForm extends PureComponent {

  constructor() {
    super();

    this.state = {
      roleData: [],
      inputVisible: false,
      confirmDirty: false,
      formValues:{}
    }
  }

  componentDidMount() {

  }
  handleConfirmBlur = (e) => {
    const value = e.target.value;
    this.setState({ confirmDirty: this.state.confirmDirty || !!value });
  };

  validatorTerminalNo = (rule, value, callback) => {
    const { modalData } = this.props;
    console.log("value::" + value)
    if (modalData.terminalNo !== value) {
      validateAcdField('terminalNo', value).then(data => {
        if (!data.success) {
          callback(formatMessage({ id: 'app.acd.terminal.terminalNo.exists' }));
        } else {
          callback();
        }
      });
    } else {
      callback();
    }

  };

  okHandle = () => {
    const { form, handleAdd } = this.props;
    form.validateFields((err, fieldsValue) => {

      if (err) return;
      form.resetFields();
      handleAdd(fieldsValue);

    });
  };

  cancelHandle = () => {
    const { handleModalVisible } = this.props;
    this.setState({
      ...this.state
    });
    handleModalVisible();
  };

  handleVisible = () => {
    this.setState({
      inputVisible: true
    });
  };


  render() {

    const {
      modalVisible,
      modalTitle,
      form: {
        getFieldDecorator
      },
      modalStatus,
      modalType,
      terminalType,
      terminalStatus
    } = this.props;

    const selectStatus = modalStatus === "create" ? false : true;
    const dateFormat = "YYYY-MM-DD HH:mm:ss";

    const { inputVisible, roleData } = this.state;

    let flagStatusOptions;
    console.log("-----------------");
    console.log(modalType);
    if (modalType.flagStatus) {
      flagStatusOptions = modalType.flagStatus.map(el => <Option key={el.value} value={el.value}>{el.label}</Option>)
    }

    let TerminalTypeOptions;
    if (terminalType) {
      TerminalTypeOptions = terminalType.map(el => <Option key={el} value={el}>{el}</Option>)
    }

    let TerminalStatusOptions;
    if(terminalStatus){
      TerminalStatusOptions=terminalStatus.map(el => <Option key={el} value={el}>{formatMessage({ id: 'app.acd.terminal.status.'+el })}</Option>)
    }

    let formItemLayout = null;
    if (getLocale() === 'zh-CN') {
      formItemLayout = {
        labelCol: {
          span: 5
        },
        wrapperCol: {
          span: 15
        }
      };
    } else {
      formItemLayout = {
        labelCol: {
          span: 7
        },
        wrapperCol: {
          span: 15
        }
      };
    }

    return (
      <Modal
        destroyOnClose
        title={modalTitle}
        visible={modalVisible}
        onOk={this.okHandle}
        onCancel={this.cancelHandle}
      >
        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.acd.terminal.terminalNo' })}
        >
          {getFieldDecorator('terminalNo', {
            validateFirst: true,
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'app.acd.terminal.terminalNo.format' }),
                min: 4,

              },
              {
                message: formatMessage({ id: 'app.acd.terminal.terminalNo.overmaxlen' }),
                max: 48
              },
              {
                pattern: new RegExp('^\\w+$', 'g'),
                message: formatMessage({ id: 'app.acd.terminal.terminalNo.regx' }),
              },
              {
                validator: this.validatorTerminalNo
              }]
          })(<Input
            disabled={selectStatus}
          />)}
        </FormItem>
        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.acd.terminal.name' })}>
          {getFieldDecorator('name', {
            rules: [{
              required: true,
              message: formatMessage({ id: 'app.acd.terminal.name.format' }, {}),
              min: 1,
            },
            {
              message: formatMessage({ id: 'app.acd.terminal.name.overmaxlen' }, {}),
              max: 255
            }
            ],
          })(<Input/>)}
        </FormItem>
         <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.acd.terminal.accessType' })}
        >
          {getFieldDecorator("accessType", {
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'app.acd.terminal.accessType.required' }, {}),
              }
            ]
          })(<Select style={{ width: 295 }}>{TerminalTypeOptions}</Select>)}
        </FormItem>

        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.acd.terminal.model' })}>
          {getFieldDecorator('model', {
            validateFirst: true,
            rules: [
            ]
          })(<Input/>)}
        </FormItem>
         <FormItem {...formItemLayout} label={formatMessage({ id: 'app.acd.terminal.machineCode' })}>
          {getFieldDecorator('machineCode', {
            validateFirst: true,
            rules: [
            ]
          })(<Input />)}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.acd.terminal.accessTime' })}
        >
          {getFieldDecorator("accessTime", {})(
            <DatePicker
              style={{ width: 295 }}
              format={dateFormat}
              disabledDate={this.disabledDate}
              showTime
            />
          )}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.acd.terminal.status' })}
        >
          {getFieldDecorator("status", {
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'app.acd.terminal.status.required' }, {}),
              }
            ]
          })(<Select style={{ width: 295 }}>{TerminalStatusOptions}</Select>)}
        </FormItem>
      </Modal>
    );
  }
}

@connect(({ terminal, loading, user }) => ({
  terminal,
  loading: loading.models.terminal,
  allType: user.allType
}))
@Form.create()
export default class Terminal extends PureComponent {
  state = {
    selectedRows: [],
    modalStatus: "",
    modalVisible: false,
    uploadModalVisible: false,
    modalTitle: "",
    modalData: {},
    modalOk: () => { }
  };

  columns = [
    {
      title: formatMessage({ id: 'app.admin.order' }),
      dataIndex: 'id',
      render: (text, record, index) => index + 1
    },
    {
      title: formatMessage({ id: 'app.acd.terminal.terminalNo' }),
      dataIndex: 'terminalNo'
    },
    {
      title: formatMessage({ id: 'app.acd.terminal.name' }),
      dataIndex: 'name'
    },
    {
      title: formatMessage({ id: 'app.acd.terminal.terminalIp' }),
      dataIndex: 'terminalIp'
    },
    {
      title: formatMessage({ id: 'app.acd.terminal.model' }),
      dataIndex: 'model'
    },
     {
      title: formatMessage({ id: 'app.acd.terminal.machineCode' }),
      dataIndex: 'machineCode'
    },
    {
      title: formatMessage({ id: 'app.acd.terminal.accessType' }),
      dataIndex: 'accessType'
    },
    {
      title: formatMessage({ id: 'app.acd.terminal.accessTime' }),
      dataIndex: 'accessTime',
      render: val =>val &&  moment(val).format('YYYY-MM-DD HH:mm:ss')
    },
    {
      title: formatMessage({ id: 'app.acd.terminal.status' }),
      dataIndex: 'status',
      render: (text, record) => (
        <Switch checkedChildren={formatMessage({ id: 'app.acd.terminal.status.AVAILABLE' })}
         unCheckedChildren={formatMessage({ id: 'app.acd.terminal.status.NOTACTIVE' })}
         checked={text==='AVAILABLE'} 
         onClick={(checked,event)=>this.handleChangeStatus(record.terminalNo,checked)}/>
      )
    },
    {
      title: formatMessage({ id: 'app.admin.operate' }),
      render: (text, record) => (
        <Fragment>
          <a onClick={() => this.handleUpdate(record.terminalNo)}>{formatMessage({ id: 'app.admin.update' })}</a>
          <Divider type="vertical" />
          <a onClick={() => this.handleDeleteUser(record.terminalNo)}>{formatMessage({ id: 'app.admin.delete' })}</a>
        </Fragment>
      )
    }
  ];

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: "terminal/fetch",
      payload: {}
    });
    dispatch({
      type: "terminal/type",
      payload: {}
    });
    dispatch({
      type: "terminal/status",
      payload: {}
    });
  }

  handleChangeStatus=(id,checked)=>{
    const { dispatch } = this.props;
    dispatch({
      type: "terminal/statusChange",
      payload: {
        terminalNo:id,checked:checked
      }
    });
  }

  handleDeleteUser = id => {
    const { dispatch } = this.props;
    Modal.confirm({
      title: formatMessage({ id: 'app.admin.tips' }),
      content: formatMessage({ id: 'app.acd.terminal.delete.tip' }),
      okText: formatMessage({ id: 'app.admin.confirm' }),
      cancelText: formatMessage({ id: 'app.admin.cancel' }),
      onOk: () => {
        dispatch({
          type: "terminal/delete",
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
      content: formatMessage({ id: 'app.acd.terminal.delete.tips' }),
      okText: formatMessage({ id: 'app.admin.confirm' }),
      cancelText: formatMessage({ id: 'app.admin.cancel' }),
      onOk: () => {
        dispatch({
          type: "terminal/delete",
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
          type: "terminal/add",
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
            type: "terminal/update",
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
      type: "terminal/fetch",
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
        type: "terminal/fetch",
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
      type: "terminal/fetch",
      payload: {}
    });
  };

  renderForm = () => {
    const {
      form: { getFieldDecorator },
      terminal:{terminalType}
    } = this.props;


    let TerminalTypeOptions;
    if (terminalType) {
      TerminalTypeOptions = terminalType.map(el => <Option key={el} value={el}>{el}</Option>)
    }

    return (
      <Form onSubmit={this.handleSearch} layout="inline">
        <ul className={styles.list}>
          <li>
             <FormItem label={formatMessage({ id: 'app.acd.terminal.search' })}>
              {getFieldDecorator('keyword', {
                rules: [
                  {
                    pattern: new RegExp('^[\u4e00-\u9fa5a-zA-Z0-9]+$', 'g'),
                    message: formatMessage({ id: 'app.admin.search.keyword' }),
                  }
                ]
              })(<Input placeholder={formatMessage({ id: 'app.admin.keyword' })} style={{ width: 295 }} />)}
            </FormItem>
          </li>
          <li>
            <FormItem label={formatMessage({ id: 'app.acd.terminal.accessType' })} >
              {getFieldDecorator('accessType',{})
              (<Select placeholder={formatMessage({ id: 'app.acd.terminal.accessType.input' })}  style={{ width: 295 }} >{TerminalTypeOptions}</Select>)}
            </FormItem>
          </li>
          <li>
            <span className={styles.submitButtons}>
              <Button type="primary" htmlType="submit">
                {formatMessage({ id: 'app.admin.search' })}
              </Button>
              <Button style={{ marginLeft: 8 }} onClick={this.handleFormReset}>
                {formatMessage({ id: 'app.admin.reset' })}
              </Button>
            </span>
          </li>
        </ul>
      </Form>
    )
  }

  render = () => {
    const {
      terminal: { data,terminalType,terminalStatus },
      loading,
      allType
    } = this.props;

    const {
      selectedRows,
      modalVisible,
      modalTitle,
      modalData,
      modalOk,
      modalStatus
    } = this.state;

    const parentMethods = {
      handleAdd: modalOk,
      handleModalVisible: this.handleModalVisible
    };

    const parentProps = {
      modalTitle,
      modalData,
      modalStatus,
      modalType: allType,
      terminalType:terminalType,
      terminalStatus:terminalStatus
    };

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
              rowKey="terminalNo"
              onChange={this.handleStandardTableChange}
            />
          </div>
        </Card>
        <CreateForm
          {...parentMethods}
          {...parentProps}
          modalVisible={modalVisible}
        />
      </PageHeaderWrapper>
    );
  };
}
