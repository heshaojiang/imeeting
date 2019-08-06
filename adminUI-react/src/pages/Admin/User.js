import React, { PureComponent, Fragment, Component } from 'react';
import { connect } from 'dva';
import { 
  Card, Badge, Divider, Modal, Button, Form, Input, TreeSelect, Select, Row, Col, Icon, Upload, Message 
} from 'antd';
import moment from 'moment';
import SelectCustomer from '@/components/SelectCustomer';
import PageHeaderWrapper from '@/components/PageHeaderWrapper';
import StandardTable from '@/components/StandardTable';
import Authorized,{ isAdminUser } from '@/utils/Authorized';
import styles from './tableList.less';

import { fetchSysRolelist} from '@/services/role';
import { getUser, validateField } from '@/services/api';
import { getPhoneNumPattern } from '@/utils/utils';
import request from '@/utils/request';
import { formatMessage, getLocale } from 'umi/locale';

const FormItem = Form.Item;
const Option = Select.Option;
const Dragger = Upload.Dragger;

@Form.create({
  mapPropsToFields(props) {
    return {
      call: Form.createFormField({
        value: props.modalData.call,
      }),
      email: Form.createFormField({
        value: props.modalData.email,
      }),
      nickname: Form.createFormField({
        value: props.modalData.nickname,
       }),
       username: Form.createFormField({
          value: props.modalData.username,
       }),
       delFlag: Form.createFormField({
        value: props.modalData.delFlag,
       }),
       customerId: Form.createFormField({
        value: props.modalData.customerList && props.modalData.customerList[0] && props.modalData.customerList[0].customerId,
       }),
       roleId: Form.createFormField({
        value: props.modalData.roleList && props.modalData.roleList[0].roleId,
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
      formValues: {},
    }
  }

  componentDidMount() {
    if(this.state.roleData.length === 0) {
        this.getInitRole();
    }
  }
  handleConfirmBlur = (e) => {
      const value = e.target.value;
      this.setState({ confirmDirty: this.state.confirmDirty || !!value });
  };
  compareToFirstPassword = (rule, value, callback) => {
    const form = this.props.form;
    if (value && value !== form.getFieldValue('password')) {
      callback(formatMessage({ id: 'app.admin.password.format' }));
    } else {
      callback();
    }
  };
  validateToNextPassword = (rule, value, callback) => {
      const form = this.props.form;
      if (value && this.state.confirmDirty) {
          form.validateFields(['confirm'], { force: true });
      }
      callback();
  };
  validatorUsername = (rule, value, callback) => {
    const { modalData } = this.props;

    if (modalData.username !== value){
      validateField('username', value).then(data => {
        if(!data.success) {
          callback(formatMessage({ id: 'app.admin.user.username.exists' }));
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
    if(value === null || value === undefined || value === "")
    {
        return;
    }
    
    console.log(`validatorPhone value ${value}`);
    if (modalData.call !== value) {
      console.log(`validateField value ${value}`);
      validateField('phonenum', value).then(data => {
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

  getInitRole = () => 
  fetchSysRolelist().then(v => {
      this.setState({
        roleData: v
      });
    });

 
  render() {

    const { 
      modalVisible,
      modalTitle ,
      form: {
        getFieldDecorator
      },
      modalStatus,
      modalType
    } = this.props;
    
    const selectStatus = modalStatus === "create" ? false : true;

    const { inputVisible, roleData } = this.state;

    // const passwordInput = inputVisible ? <Input type="password" /> : <Input type="text" onFocus={this.handleVisible}/>;

    let OptionsRole = null;
    if(roleData)
    {
        OptionsRole= roleData.map(el => <Option key={el.roleId} value={el.roleId}>{el.roleName}</Option>);
    }

    let flagStatusOptions;
    if (modalType.flagStatus) {
      flagStatusOptions = modalType.flagStatus.map(el => <Option key={el.value} value={el.value}>{el.label}</Option>)
    }
   
    let formItemLayout = null;
    if(getLocale() === 'zh-CN') {
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
        <Authorized authority="role_super">
        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.user.customer'})}>
          {getFieldDecorator('customerId', {
            rules: [{
              required: isAdminUser(), message: formatMessage({ id: 'app.admin.user.customer.select'}, {})
            }],
          })(
            <SelectCustomer />
          )}
        </FormItem>
        </Authorized>    
        <FormItem 
          {...formItemLayout}
          label={formatMessage({ id: 'app.admin.user.username' })}
        >
          {getFieldDecorator('username', {
            validateFirst : true,
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'app.admin.user.username.format' }),
                min: 4,
              
              },
              {
                message: formatMessage({ id: 'app.admin.user.username.overmaxlen' }),
                max: 64
              },
              {
                 pattern:new RegExp('^\\w+$','g'),
                 message: formatMessage({ id: 'app.admin.user.username.regx' }),
              },
              {
                validator: this.validatorUsername
            }]
          })(<Input
            disabled={selectStatus} 
            placeholder={formatMessage({ id: 'app.admin.user.username.input' })}
          />)}
        </FormItem>
        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.user.nickname' })}>
          {getFieldDecorator('nickname', {
            rules: [{ 
              required: true,
              message: formatMessage({ id: 'app.admin.user.nickname.format' },{}),
              min: 1,
            },
            { 
              message: formatMessage({ id: 'app.admin.user.nickname.overmaxlen' },{}),
              max: 64
            }
            ],
          })(<Input placeholder={formatMessage({ id: 'app.admin.user.nickname.input' })} />)}
        </FormItem>
        {
          modalStatus === "create" &&
          (
            <Fragment>
              <FormItem {...formItemLayout} label={formatMessage({id: 'app.admin.user.password'})}>
                {getFieldDecorator('password', {
                  rules: [{ required: true, message: formatMessage({ id: 'app.admin.user.password.format'},{}), min: 6,max: 32
                  }, {
                      validator: this.validateToNextPassword,
                  }],
                })(<Input type="password" />)}
              </FormItem>
              <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.user.password.confirm'})}>
                {getFieldDecorator('confirm', {
                  rules: [{ 
                    required: true, message: formatMessage({ id: 'app.admin.user.password.confirmation' }, {}),
                  }, {
                    validator: this.compareToFirstPassword
                  }],
                })(<Input type="password" onBlur={this.handleConfirmBlur} />)}
              </FormItem>
            </Fragment>
          ) 
        }

        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.user.role' })}>
          {getFieldDecorator('roleId', {
            rules: [{ required: true, message: formatMessage({ id: 'app.admin.user.role.select'}, {}) }],
          })(
            <Select
              style={{ width: 295 }}
              onFocus={this.getInitRole} 
              placeholder={formatMessage({ id: 'app.admin.user.role.select'})}
            >
              {OptionsRole}
            </Select>
          )} 
        </FormItem>
        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.email' })}>
          {getFieldDecorator('email', {
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
          label={formatMessage({ id: 'app.admin.phone'})}>
          {getFieldDecorator('call', {
            validateFirst : true,
            rules: [{
              required: true,
              pattern: getPhoneNumPattern(),
              message: formatMessage({ id: 'app.admin.phone.format'})
              },{
                validator: this.validatorPhone
              }],
          })(<Input disabled={selectStatus}/>)}
        </FormItem>
        {/* {
          modalStatus === "update" &&
          <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.status'})}>
            {getFieldDecorator('status', {
              rules: [{ required: true }],
            })(
              <Select 
                style={{ width: 295 }}
              >
                {flagStatusOptions}
              </Select>
            )} 
          </FormItem>
        } */}
      </Modal>
    ); 
  }
}

class UploadForm extends Component {

  state = {
    fileList: [],
    uploading: false
  }

  handleUpload = () => {
    
    const { fileList } = this.state;
    
    this.setState({
      uploading: true
    });
    const formData = new FormData();
    fileList.forEach((file) => {
      formData.append('excelFile', file);
    }); 

    
    if (fileList.length === 0){
      this.setState({
        uploading: false
      });
      return;
    }
    
    request('/admin/user/uploadExcel', {
      method: 'POST',
      body: formData
    }).then((response) => {
      if(response) {
        if (response.successCount > 0) {
          Message.success(formatMessage({ id: 'app.admin.user.upload.success'},{tc:response.totalCount,sc:response.successCount,ec:response.errorCount}),10);
        } else {
          Message.error(formatMessage({ id: 'app.admin.user.upload.error'},{tc:response.totalCount,sc:response.successCount,ec:response.errorCount}),10);
        }
        this.props.handleOk();
        this.props.handleModalVisible();
        this.setState({
          fileList: [],
          uploading: false
        })  
      } else {
        this.setState({
          uploading: false
        })  
      };
    })


    
  }

  onCancel = () => {
    this.setState({
      fileList: [],
    })
    this.props.handleModalVisible();
  }
  render() {

    const { uploadModalVisible } = this.props;

    const props = {
      name: 'excelFile',
      // action: '/admin/user/uploadExcel',
      onRemove: (file) => {
        this.setState({
          fileList: []
        });
      },
      beforeUpload: (file) => {
        
        this.setState({
          fileList: [file]
        });
        return false;
      },
      fileList: this.state.fileList,
    };


    return (
      <Modal
        destroyOnClose
        confirmLoading={this.state.uploading}
        title={formatMessage({ id: 'app.admin.import'})}
        visible={uploadModalVisible}
        onOk={this.handleUpload}
        onCancel={this.onCancel}
      >
      <span className="ant-upload-hint">{formatMessage({ id: 'app.admin.user.upload.info2'})}</span>
      <a href={require('../../assets/userTemplate.xlsx')} download="userTemplate.xlsx"> userTemplate.xlsx </a>
      
        <Dragger {...props}>
          <p className="ant-upload-drag-icon">
            <Icon type="inbox" />
          </p>
          <p className="ant-upload-text">{formatMessage({ id: 'app.admin.user.upload.info'})}</p>
          
        </Dragger>
      </Modal>
    );
    
  }
}

@connect(({ userAdmin, loading, user }) => ({
  userAdmin,
  loading: loading.models.userAdmin,
  allType: user.allType
}))
@Form.create()
export default class User extends PureComponent {

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
      title: formatMessage({ id: 'app.admin.order'}),
      dataIndex: 'userId',
      render: (text, record, index) => index + 1
    },
    {
      title:formatMessage({ id: 'app.admin.user.username'}),
      dataIndex: 'username'
    },
    {
      title: formatMessage({ id: 'app.admin.user.nickname'}),
      dataIndex: 'nickname'
    },
    {
      title: formatMessage({ id: 'app.admin.user.customer'}),
      dataIndex: 'customerList',
      render: val => val[0] && val[0].customerName
    }, 
    {
      title: formatMessage({ id: 'app.admin.user.role'}),
      dataIndex: 'roleList',
      render: val => getLocale() == 'zh-CN' ? val[0].roleName : val[0].roleCode
    },
    {
      title: formatMessage({ id: 'app.admin.phone'}),
      dataIndex: 'call'
    },
    {
      title: formatMessage({ id: 'app.admin.email'}),
      dataIndex: 'email'
    },
    {
      title: formatMessage({ id: 'app.admin.user.create.date'}),
      dataIndex: 'createTime',
      render: val => moment(val).format('YYYY-MM-DD HH:mm:ss')
    },
    {
      title: formatMessage({ id: 'app.admin.user.login.date'}),
      dataIndex: 'loginTime',
      render: val => val && moment(val).format('YYYY-MM-DD HH:mm:ss')
    },
    // {
    //   title: formatMessage({ id: 'app.admin.status'}),
    //   dataIndex: 'delFlag',
    //   render: val => this.props.allType.flagStatus.find(el => el.value === val).label
    // },
    {
      title: formatMessage({ id: 'app.admin.operate'}),
      render: (text, record) => (
        <Fragment>
          <a onClick={() => this.handleUpdate(record.userId)}>{formatMessage({ id: 'app.admin.update'})}</a>
          <Divider type="vertical" />
          <a onClick={() => this.handleDeleteUser(record.userId)}>{formatMessage({ id: 'app.admin.delete'})}</a>
        </Fragment>
      )
    }
  ]

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'userAdmin/fetch',
      payload: {}
    });
  }

  handleDeleteUser(userId) {
    const { dispatch } = this.props;
    Modal.confirm({
      title: formatMessage({ id: 'app.admin.tips' }),
      content: formatMessage({ id: 'app.admin.user.delete.tips' }),
      okText: formatMessage({ id: 'app.admin.confirm' }),
      cancelText: formatMessage({ id: 'app.admin.cancel' }),
      onOk: () => {
        dispatch({
          type: 'userAdmin/delete',
          payload: userId
        });
      }
    });
  }
  handleBatchUser = () => {
    const { dispatch } = this.props;
    const { selectedRows } = this.state;
    
    Modal.confirm({
      title: formatMessage({ id: 'app.admin.tips' }),
      content: formatMessage({ id: 'app.admin.user.delete.batch.tips' }),
      okText: formatMessage({ id: 'app.admin.confirm' }),
      cancelText: formatMessage({ id: 'app.admin.cancel' }),
      onOk: () => {
        dispatch({
          type: 'userAdmin/delete',
          payload: selectedRows.map(el => el.userId).join(','),
          callback: () => {
            this.setState({
              selectedRows: []
            })
          }
        });
      }
    });
  }
  handlePasswordResetByManager = () => {
    const { dispatch } = this.props;
    const { selectedRows } = this.state;

    Modal.confirm({
      title: formatMessage({ id: 'app.admin.tips' }),
      content: formatMessage({ id: 'app.admin.password.reset.format' }),
      okText: formatMessage({ id: 'app.admin.confirm' }),
      cancelText: formatMessage({ id: 'app.admin.cancel' }),
      onOk: () => {
        dispatch({
          type: 'userAdmin/reset',
          payload: selectedRows.map(el => el.userId).join(','),
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
      modalTitle: formatMessage({ id: 'app.admin.add' }),
      modalData: {},
      modalOk: (data) => {
        dispatch({
          type: 'userAdmin/add',
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

    getUser(id).then((data) => {
      
      this.handleModalVisible(true);
      this.setState({
        modalStatus: 'update',
        modalTitle: formatMessage({ id: 'app.admin.update' }),
        modalData: data,
        modalOk: (fields) => {
          const { dispatch } = this.props;
          dispatch({
            type: 'userAdmin/update',
            payload: {
              ...data,
              ...fields
            }
          }); 
          this.handleModalVisible();
        }
      });
  
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
      type: 'userAdmin/fetch',
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
      const values = {
        ...fieldsValue,
      };
      this.setState({
        formValues: values,
      });
      dispatch({
        type: 'userAdmin/fetch',
        payload: values,
      });
    });
  }

  handleFormReset = () => {
    const { dispatch, form } = this.props;
    form.resetFields();
    this.setState({
      formValues: {},
    });
    dispatch({
      type: 'userAdmin/fetch',
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
            <FormItem label={formatMessage({ id: 'app.admin.user.search' })}>
              {getFieldDecorator('keyword',{
            rules: [
              {
                pattern:new RegExp('^[\u4e00-\u9fa5a-z0-9]+$','g'),
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
      userAdmin: { data },
      loading,
      allType
    } = this.props;

    const { selectedRows, modalVisible, modalTitle, modalData, modalOk, modalStatus, uploadModalVisible } = this.state;
  
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

    const uploadMethods= {
      handleModalVisible: this.handleUpdateModalVisible,
      handleOk: () => {
        const { dispatch } = this.props;
        dispatch({
          type: 'userAdmin/reload',
          payload: {},
        })
      }
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
              <Authorized authority="imeeting_admin,imeeting_participant">
                <Button icon="upload" type="primary" onClick={this.handleUpdateModalVisible}>
                {formatMessage({ id: 'app.admin.import' })}
                </Button>
              </Authorized>
              {
                selectedRows.length > 0 && 
                (
                  <Button icon="delete" type="primary" onClick={this.handleBatchUser}>
                    {formatMessage({ id: 'app.admin.delete.batch' })}
                  </Button>
                )
              }
              {
                selectedRows.length > 0 && 
                (
                  <Button icon="reload" type="primary" onClick={this.handlePasswordResetByManager}>
                    {formatMessage({ id: 'app.admin.password.reset' })}
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
              rowKey="userId"
              onChange={this.handleStandardTableChange}
            />
          </div>
        </Card>
        <CreateForm {...parentMethods} {...parentProps} modalVisible={modalVisible} />
        <UploadForm uploadModalVisible={uploadModalVisible} {...uploadMethods}/>
      </PageHeaderWrapper>
    );
  }
}