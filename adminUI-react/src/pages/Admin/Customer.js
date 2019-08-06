import React, { PureComponent, Fragment, Component } from "react";
import { connect } from "dva";
import {
  Card,
  Badge,
  Divider,
  Modal,
  Button,
  Form,
  Input,
  DatePicker,
  Select,
  Row,
  Col,
  InputNumber
} from "antd";
import moment from "moment";
import PageHeaderWrapper from "@/components/PageHeaderWrapper";
import StandardTable from "@/components/StandardTable";
import styles from "./tableList.less";
import { validateField } from "@/services/api";
import { getObject } from "@/services/customer";
import { getPhoneNumPattern } from '@/utils/utils';
import { formatMessage, getLocale } from 'umi/locale';

const getOptions = data => {
  if (!data) return null;

  return data.map(el => (
    <Option key={el.value} value={el.value}>
      {el.label}
    </Option>
  ));
};

const FormItem = Form.Item;

@Form.create({
  mapPropsToFields(props) {
    return {
      customerName: Form.createFormField({
        value: props.modalData.customerName
      }),
      customerNo: Form.createFormField({
        value: props.modalData.customerNo
      }),
      call: Form.createFormField({
        value: props.modalData.call
      }),
      email: Form.createFormField({
        value: props.modalData.email
      }),
      type: Form.createFormField({
        value: props.modalData.type
      }),
      createTime: Form.createFormField({
        value: moment(props.modalData.createTime)
      }),
      payType: Form.createFormField({
        value: props.modalData.payType
      }),
      status: Form.createFormField({
        value: props.modalData.status
      }),
      delFlag: Form.createFormField({
        value: props.modalData.delFlag
      }),
      numJoin: Form.createFormField({
        value: props.modalData.numJoin || 0
      }),
      numMeeting: Form.createFormField({
        value: props.modalData.numMeeting || 0
      }),
      numRoom: Form.createFormField({
        value: props.modalData.numRoom || 0
      }),
      numVideo: Form.createFormField({
        value: props.modalData.numVideo || 0
      })
    };
  }
})
class CreateForm extends PureComponent {
    validatorCustomerName = (rule, value, callback) => {
      const { modalData } = this.props;

      var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]") 
      if(pattern.test(value))
      {
        
        return;
      }
      
      if (modalData.customerName !== value) {
        validateField('customername', value).then(data => {
          if (!data.success) {
            callback(formatMessage({ id: 'app.admin.customer.name.exists' }));
          } else {
            callback();
          }
        });
      } else {
        callback();
      }

    };
    validatorPhone = (rule, value, callback) => {
      const { modalData } = this.props;
      if(value === null || value === undefined  || value === "")
      {
          return;
      }
      if (modalData.call !== value) {
        validateField('customerPhoneNum', value).then(data => {
          if (!data.success) {
            callback(formatMessage({ id: 'app.admin.phone.exists' }));
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

    handleModalVisible();
  };

  disabledDate = current => {
    // Can not select days before today and today
    return current && current < moment().endOf("day");
  };

  render() {
    const {
      modalVisible,
      modalTitle,
      form: { getFieldDecorator },
      modalType
    } = this.props;
    let signOptions = getOptions(modalType.signType),
      payTypeOptions = getOptions(modalType.payType),
      statusOptions = getOptions(modalType.sign);
    const dateFormat = "YYYY-MM-DD HH:mm:ss";

    let formItemLayout = null;
    if(getLocale() === 'zh-CN') {
        formItemLayout = {
            labelCol: {
                span: 6
            },
            wrapperCol: {
                span: 15
            }
        };
    } else {
        formItemLayout = {
            labelCol: {
                span: 9
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
          label={formatMessage({ id: 'app.admin.customer.name' })}
        >
          {getFieldDecorator("customerName", {
            validateFirst : true,
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'app.admin.customer.name.format' }),
                min: 4,
              },
              {
                message: formatMessage({ id: 'app.admin.customer.name.overmaxlen' }),
                max:64
              },
              {
                 pattern:new RegExp('^\\w+$','g'),
                 message: formatMessage({ id: 'app.admin.customer.name.regx' }),
              },
              {
                  validator: this.validatorCustomerName
              }
            ],
            
          },
           )(<Input />)}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.admin.customer.code' })}
        >
          {getFieldDecorator("customerNo", {
            rules: [
              {
                message: formatMessage({ id: 'app.admin.customer.code.format' }),
                min: 4,
                max:11
              },
              {
                 pattern:new RegExp('^[0-9]*$','g'),
                 message: formatMessage({ id: 'app.admin.customer.code.regx' }),
              },
            ],
          })(<Input />)}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.admin.customer.parties' })}
        >
          {getFieldDecorator("numVideo", {
            rules: [{
              type: 'number', 
              max:1000,
              message: formatMessage({ id: 'app.admin.customer.parties.max' })+ " 1000",
            }],
          })(
            <InputNumber style={{ width: 295 }} min={0} min={0} />
          )}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.admin.customer.callers' })}
        >
          {getFieldDecorator("numJoin", {
            rules: [{
              type: 'number', 
              max:1000,
              message: formatMessage({ id: 'app.admin.customer.callers.max' })+ " 1000",
            }],
          })(
            <InputNumber style={{ width: 295 }} min={0} />
          )}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.admin.customer.rooms' })}
        >
          {getFieldDecorator("numRoom", {
            rules: [{
              type: 'number',    
              max:1000,
              message: formatMessage({ id: 'app.admin.customer.rooms.max' })+ " 1000",
            }],
          })(
            <InputNumber style={{ width: 295 }} min={0} />
          )}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.admin.customer.meetings' })}
        >
          {getFieldDecorator("numMeeting", {
            rules: [{
              type: 'number', 
              max:1000,
              message: formatMessage({ id: 'app.admin.customer.meetings.max' })+ " 1000",
            }],
          })(
            <InputNumber style={{ width: 295 }} min={0} />
          )}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.admin.phone' })}
        >
          {getFieldDecorator("call", {
            validateFirst : true,
            rules: [
              {
                required: true,
                pattern: getPhoneNumPattern(),
                message: formatMessage({ id: 'app.admin.phone.format' }, {}),
              },{
                    validator: this.validatorPhone
            }]
          })(<Input />)}
        </FormItem>
        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.email' })}>
          {getFieldDecorator("email", {
            validateFirst : true,
            rules: [
              {
                type: "email",
                message: formatMessage({ id: 'app.admin.email.format' }, {}),
              },
              { 
              message: formatMessage({ id: 'app.admin.email.overmaxlen' },{}),
              max: 64
              }
            ]
          })(<Input />)}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.admin.customer.type' })}
        >
          {getFieldDecorator("type", {
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'app.admin.customer.type.select'},{}),
              }
            ]
          })(<Select style={{ width: 295 }}>{signOptions}</Select>)}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.admin.customer.payment' })}
        >
          {getFieldDecorator("payType", {
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'app.admin.customer.payment.select'},{}),
              }
            ]
          })(<Select style={{ width: 295 }}>{payTypeOptions}</Select>)}
        </FormItem>
        {/* <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="支付方式">
            {getFieldDecorator('payType', {
            })(
              <Select
                style={{width:295}}
              >
                {payTypeOptions}
              </Select>
            )}
          </FormItem>
          <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="支付方式">
            {getFieldDecorator('status', {
            })(
              <Select
                style={{width:295}}
              >
                {statusOptions}
              </Select>
            )}
          </FormItem> */}
        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.admin.customer.time' })}
        >
          {getFieldDecorator("createTime", {})(
            <DatePicker
              style={{ width: 295 }}
              format={dateFormat}
              disabledDate={this.disabledDate}
              showTime
            />
          )}
        </FormItem>
      </Modal>
    );
  }
}

@connect(({ customer, loading, user }) => ({
  customer,
  loading: loading.models.customer,
  allType: user.allType
}))
@Form.create()
export default class Customer extends PureComponent {
  state = {
    selectedRows: [],
    modalStatus: "",
    modalVisible: false,
    uploadModalVisible: false,
    modalTitle: "",
    modalData: {},
    formValues:{},
    modalOk: () => {}
  };

  columns = [
    {
      title: formatMessage({ id: 'app.admin.order' }),
      dataIndex: "customerId",
      render: (text, record, index) => index + 1
    },
    {
      title: formatMessage({ id: 'app.admin.customer.name' }),
      dataIndex: "customerName"
    },
    {
      title: formatMessage({ id: 'app.admin.customer.code' }),
      dataIndex: "customerNo"
    },
    {
      title: formatMessage({ id: 'app.admin.customer.type' }),
      dataIndex: "type",
      render: val =>
        this.props.allType.signType.find(el => el.value === val).label
    },
    {
      title: formatMessage({ id: 'app.admin.phone' }),
      dataIndex: "call"
    },
    {
      title: formatMessage({ id: 'app.admin.email' }),
      dataIndex: "email"
    },
    {
      title: formatMessage({ id: 'app.admin.customer.time' }),
      dataIndex: "createTime",
      render: val => moment(val).format("YYYY-MM-DD HH:mm:ss")
    },
    {
      title: formatMessage({ id: 'app.admin.operate' }),
      render: (text, record) => (
        <Fragment>
          <a onClick={() => this.handleUpdate(record.customerId)}>{formatMessage({ id: 'app.admin.update' })}</a>
          <Divider type="vertical" />
          <a onClick={() => this.handleDeleteUser(record.customerId)}>{formatMessage({ id: 'app.admin.delete' })}</a>
        </Fragment>
      )
    }
  ];

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: "customer/fetch",
      payload: {}
    });
  }

  handleDeleteUser = id => {
    const { dispatch } = this.props;
    Modal.confirm({
      title: formatMessage({ id: 'app.admin.tips' }),
      content: formatMessage({ id: 'app.admin.customer.delete.tips' }),
      okText: formatMessage({ id: 'app.admin.confirm' }),
      cancelText: formatMessage({ id: 'app.admin.cancel' }),
      onOk: () => {
        dispatch({
          type: "customer/delete",
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
      content: formatMessage({ id: 'app.admin.customer.delete.batch.tips' }),
      okText: formatMessage({ id: 'app.admin.confirm' }),
      cancelText: formatMessage({ id: 'app.admin.cancel' }),
      onOk: () => {
        dispatch({
          type: "customer/delete",
          payload: selectedRows.map(el => el.customerId).join(","),
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
          type: "customer/add",
          payload: data
        });
        this.handleModalVisible();
      }
    });
    this.handleModalVisible(true);
  };
  // 导入
  handleUpdateModalVisible = () => {
    this.setState({
      uploadModalVisible: !this.state.uploadModalVisible
    });
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
            type: "customer/update",
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
      type: "customer/fetch",
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
        type: "customer/fetch",
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
      type: "customer/fetch",
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
            <FormItem label={formatMessage({ id: 'app.admin.customer.search' })}>
              {getFieldDecorator("keyword",{
            rules: [
              {
                pattern:new RegExp('^[\u4e00-\u9fa5a-z0-9]+$','g'),
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
      customer: { data },
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
      modalType: allType
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
              {/*<Button*/}
                {/*icon="upload"*/}
                {/*type="primary"*/}
                {/*onClick={this.handleUpdateModalVisible}*/}
              {/*>*/}
                {/*导入*/}
              {/*</Button>*/}
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
              rowKey="customerId"
              onChange={this.handleStandardTableChange}
            />
          </div>
        </Card>
        <CreateForm
          {...parentMethods}
          {...parentProps}
          modalVisible={modalVisible}
        />
        {/* <UploadForm uploadModalVisible={uploadModalVisible} {...uploadMethods}/>  */}
      </PageHeaderWrapper>
    );
  };
}
