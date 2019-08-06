import React, { PureComponent, Fragment, Component } from 'react';
import { connect } from 'dva';
import { 
  Card, Badge, Divider, Modal, Button, Form, Input, TreeSelect, Select,
  Row, Col, Icon, Upload, message, InputNumber
} from 'antd';
import moment from 'moment';
import PageHeaderWrapper from '@/components/PageHeaderWrapper';
import StandardTable from '@/components/StandardTable';
import styles from './tableList.less';
import { fetchCustomerlist } from '@/services/customer';
import { getObject } from '@/services/room';
import { customerChangeObtainUser } from '@/services/api';


const FormItem = Form.Item;
const Option = Select.Option;

@Form.create({
  mapPropsToFields(props) {
    return {
       roomNo: Form.createFormField({
          value: props.modalData.roomNo,
       }),
       roomName: Form.createFormField({
          value: props.modalData.roomName,
       }),
       terminalType: Form.createFormField({
        value: props.modalData.terminalType,
      }),
      status: Form.createFormField({
        value: props.modalData.status,
      }),
      customerId: Form.createFormField({
        value: props.modalData.customerList && props.modalData.customerList[0].customerId,
      }),
      userId: Form.createFormField({
        value: props.modalData.userList && props.modalData.userList[0] && props.modalData.userList[0].userId,
      }),
    }
  }
})
class CreateForm extends PureComponent {

    constructor() {
      super();  
      this.state = {
        customerData: [],
        userData: []
      };
    }
    
   static getDerivedStateFromProps(props, state) {

    if(!props.modalVisible) {
      return {
        ...state,
        userData: []
      };
    }

     if (props.modalData.userData) {
      return {
        ...state,
        userData: props.modalData.userData
      };
     }

     return state;
   }

   componentDidMount() {
    this.getInitCustomer();
   }

  okHandle = () => {
    const { form, handleAdd } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      form.resetFields();
      handleAdd(fieldsValue);
    });
  }

  cancelHandle = () => {
    const { handleModalVisible } = this.props;
    handleModalVisible();
  }

  
  getInitCustomer = () => 
  fetchCustomerlist().then(v => {
    this.setState({
      customerData: v
    });
  });

  getChangeObtainUser = (customerId) => {
    
    this.props.form.setFieldsValue({
      userId: undefined
    });

    customerChangeObtainUser(customerId).then((data) => {
      this.setState({
        userData: data
      });
    });
  }

  render() {

    const { 
      modalVisible,
      modalTitle,
      form: {
        getFieldDecorator
      },
      modalData,
      modalStatus,
      modalType: {
        terminalType,
        roomStatus
      },
    } = this.props;

    const { userData,customerData } = this.state;

    const selectStatus = modalStatus === "create" ? false : true;

    let [ OptionsUser, OptionsCustomer, OptionsTerminalType, OptionsRoomType ] = new Array(4);

    if(userData) {
      OptionsUser= userData.map(el => <Option key={el.userId} value={el.userId}>{el.username}</Option>);
    }
  
    if(customerData) {
      OptionsCustomer= customerData.map(el => <Option key={el.customerId} value={el.customerId}>{el.customerName}</Option>);
    }

    if(terminalType) {
      OptionsTerminalType= terminalType.map(el => <Option key={el.value} value={el.value}>{el.label}</Option>);
    }
    if(roomStatus) {
      OptionsRoomType= roomStatus.map(el => <Option key={el.value} value={el.value}>{el.label}</Option>);
    }

    return (
      <Modal
        destroyOnClose
        title={modalTitle}
        visible={modalVisible}
        onOk={this.okHandle}
        onCancel={this.cancelHandle}
      >
       <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="会议号">
          {getFieldDecorator('roomNo', {
              rules: [{
                required: true,
                max: 11,
                message: '会议号最长为11位数',
              }],
          })(<Input  disabled={selectStatus} />)}
        </FormItem>
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="所属客户">
            {getFieldDecorator('customerId', {
                rules: [{
                required: true,
                message: '请选择客户',
              }],
            })(
            <Select 
              disabled={selectStatus}
              style={{ width: 295 }}
              onFocus={this.getInitCustomer} 
              onChange={this.getChangeObtainUser}
              placeholder="选择客户"
            >
              {OptionsCustomer}
            </Select>
          )} 
        </FormItem>
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="会议管理员">
            {getFieldDecorator('userId', {
                 rules: [{
                 required: false,
                 message: '请选择管理员'
                }]
            })(
            <Select 
              style={{ width: 295 }}
              placeholder="选择管理员"
            >
              {OptionsUser}
            </Select>
          )} 
        </FormItem>
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="会议室名称">
          {getFieldDecorator('roomName', {})(<Input />)}
        </FormItem>
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="终端">
          {getFieldDecorator('terminalType', {
              rules: [{
              required: true,
            }],
          })(
            <Select 
              style={{ width: 295 }}
              onFocus={this.getInitCustomer} 
            >
              {OptionsTerminalType}
            </Select>
          )} 
        </FormItem>
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="状态">
          {getFieldDecorator('status', {
            rules: [{
              required: true,
            }],
            })(
            <Select 
              style={{ width: 295 }}
              onFocus={this.getInitCustomer} 
            >
              {OptionsRoomType}
            </Select>
          )} 
        </FormItem>
      </Modal>
    ); 
  }
}
@connect(({ room, loading, user }) => ({
  room,
  loading: loading.models.room,
  allType: user.allType
}))
@Form.create()
export default class Room extends PureComponent {

  state = { 
    selectedRows: [],
    modalStatus: '',
    modalVisible: false,
    uploadModalVisible: false,
    modalTitle: '',
    modalData: {},
    modalOk: () => {}
  }

  columns = [
    {
      title: '序号',
      dataIndex: 'roomId',
      render: (text, record, index) => index + 1
    },
    {
        title: '会议号',
        dataIndex: 'roomNo',
        // render: val => val.roomNo
    },
    {
      title: '终端',
      dataIndex: 'terminalType',
      render: (val) => {

        if (!val) {
          return val
        }
        
        return this.props.allType.terminalType.find(el => el.value === val).label

      }
    },
    {
        title: '会议室名称',
        dataIndex: 'roomName'
    },
    {
        title: '所属客户',
        dataIndex: 'customerList',
        render: val => val[0].customerName
    }, 
    {
        title: '会议管理员',
        dataIndex: 'userList',
        render: val => val[0] && val[0].username
    }, 
    {
      title: '状态',
      dataIndex: 'status',
      render: (val) => {
        return this.props.allType.roomStatus.find(el => el.value === val).label
      }
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      render: val => moment(val).format('YYYY-MM-DD HH:mm:ss')
    },
    {
      title: '操作',
      render: (text, record) => (
        <Fragment>
          <a onClick={() => this.handleUpdate(record.roomId)}>配置</a>
          <Divider type="vertical" />
          <a onClick={() => this.handleDeleteUser(record.roomId)}>删除</a>
        </Fragment>
      )
    }
  ]

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'room/fetch',
      payload: {}
    });
  }

  handleDeleteUser = (id) => {
  
    const { dispatch } = this.props;
    Modal.confirm({
      title: '提示',
      content: '此操作将永久删除该会议室, 是否继续?',
      okText: '确认',
      cancelText: '取消',
      onOk: () => {
        dispatch({
          type: 'room/delete',
          payload: id
        });
      }
    });
  }

  handleBatchUser = () => {
    const { dispatch } = this.props;
    const { selectedRows } = this.state;
    
    Modal.confirm({
      title: '提示',
      content: '此操作将永久删除这些会议室, 是否继续?',
      okText: '确认',
      cancelText: '取消',
      onOk: () => {
        dispatch({
          type: 'room/delete',
          payload: selectedRows.map(el => el.roomId).join(','),
          callback: () => {
            this.setState({
              selectedRows: []
            })
          }
        });
      }
    });
  }
  // 新建
  handleCreate = () => {
    const { dispatch } = this.props;
    this.setState({
      modalStatus: 'create',
      modalTitle: '新建',
      modalData: {},
      modalOk: (data) => {
        dispatch({
          type: 'room/add',
          payload: data
        });
        this.handleModalVisible();
      }
    });
    this.handleModalVisible(true);
  }
  // 导入
  handleUpdateModalVisible = () => {

    this.setState({
      uploadModalVisible: !this.state.uploadModalVisible
    })
  }
  // 更新
  handleUpdate = (id) => {

    getObject(id).then((data) => {
   
      customerChangeObtainUser(data.customerList[0].customerId).then((userData) => {
        this.handleModalVisible(true);
      
        this.setState({
          modalStatus: 'update',
          modalTitle: '配置',
          modalData: {
            ...data,
            userData
          },
          modalOk: (fields) => {
            const { dispatch } = this.props;
            dispatch({
              type: 'room/update',
              payload: {
                ...data,
                ...fields
              }
            }); 
            this.handleModalVisible();
          }
        });
      })
      
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
      ...filters,
    };

    if (sorter.field) {
      params.sorter = `${sorter.field}_${sorter.order}`;
    }

    dispatch({
      type: 'room/fetch',
      payload: params,
    });
  }

  handleSelectRows = (rows) => {
    this.setState({
      selectedRows: rows,
    });
  }

  handleModalVisible = (flag) => {

    this.setState({
      modalVisible: !!flag,
    });

  }

  handleSearch = (e) => {
    e.preventDefault();

    const { dispatch, form } = this.props;

    form.validateFields((err, fieldsValue) => {
      if (err) return;

      dispatch({
        type: 'room/fetch',
        payload: fieldsValue,
      });
    });
  }

  handleFormReset = () => {
    const { dispatch, form } = this.props;
    form.resetFields();
    dispatch({
      type: 'room/fetch',
      payload: {},
    });
  }

  renderForm = () => {
    const {
      form: { getFieldDecorator },
    } = this.props;
    return (
      <Form onSubmit={this.handleSearch} layout="inline">
        <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
          <Col md={8} sm={24}>
            <FormItem label="会议室搜索">
              {getFieldDecorator('keyword',{
            rules: [
              {
                pattern:new RegExp('^[\u4e00-\u9fa5a-z0-9]+$','g'),
                 message: formatMessage({ id: 'app.admin.search.keyword' }),
              }
            ]
          })(<Input placeholder="请输入关键字" />)}
            </FormItem>
          </Col>
          <Col md={8} sm={24}>
            <span className={styles.submitButtons}>
              <Button type="primary" htmlType="submit">
                搜索
              </Button>
              <Button style={{ marginLeft: 8 }} onClick={this.handleFormReset}>
                重置
              </Button>
            </span>
          </Col>
        </Row>
      </Form>
    )
  }

  render = () => {

    const {
      room: { 
        data
      },
      loading,
      allType
    } = this.props;

    const { selectedRows, modalVisible, modalTitle, modalData, modalOk, modalStatus } = this.state;
  
    const parentMethods = {
      handleAdd: modalOk,
      handleModalVisible: this.handleModalVisible,
    };

    const parentProps = {
      modalTitle,
      modalData,
      modalStatus,
      modalType: allType
    }


    return (
      <PageHeaderWrapper>
        <Card bordered={false}>
          <div className={styles.tableList}>
          <div className={styles.tableListForm}>{this.renderForm()}</div>
            <div className={styles.tableListOperator}>
              <Button icon="plus" type="primary" onClick={this.handleCreate}>
                新建
              </Button>
              {/* <Button icon="upload" type="primary" onClick={this.handleUpdateModalVisible}>
                导入
              </Button> */}
              {
                selectedRows.length > 0 && 
                (
                  <Button icon="delete" type="primary" onClick={this.handleBatchUser}>
                    批量删除
                  </Button>
                )
              }
            </div>

            <StandardTable
              selectedRows={selectedRows} 
              loading={loading}
              data={data}
              columns={this.columns}
              onSelectRow={this.handleSelectRows}
              rowKey="roomId"
              onChange={this.handleStandardTableChange}
            />

          </div>
        </Card>
        <CreateForm {...parentMethods} {...parentProps} modalVisible={modalVisible} />
        {/* <UploadForm uploadModalVisible={uploadModalVisible} {...uploadMethods}/>  */}
      </PageHeaderWrapper>
    );
  }
}