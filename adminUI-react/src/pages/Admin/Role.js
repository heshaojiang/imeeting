import React, { PureComponent, Fragment, Component } from 'react';
import { connect } from 'dva';
import { 
  Card, Badge, Divider, Modal, Button, Form, Input, TreeSelect, Select, Row, Col, Icon, Upload, message  
} from 'antd';
import moment from 'moment';
import PageHeaderWrapper from '@/components/PageHeaderWrapper';
import StandardTable from '@/components/StandardTable';
import styles from './tableList.less';

import { getObject } from '@/services/role';

const FormItem = Form.Item;

@Form.create({
  mapPropsToFields(props) {
    return {
        roleName: Form.createFormField({
            value: props.modalData.roleName,
       }),
       roleCode: Form.createFormField({
          value: props.modalData.roleCode,
       }),
       roleDesc: Form.createFormField({
        value: props.modalData.roleDesc,
       }),
    }
  }
})
class CreateForm extends PureComponent {

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

  render() {

    const { 
      modalVisible,
      modalTitle,
      form: {
        getFieldDecorator
      },
      modalStatus,
      modalData,
    } = this.props;

    // const Options ="广电运通";

    const { TextArea } = Input;

    return (
      <Modal
        destroyOnClose
        title={modalTitle}
        visible={modalVisible}
        onOk={this.okHandle}
        onCancel={this.cancelHandle}
      >
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="角色名称">
          {getFieldDecorator('roleName', {
            rules: [{
              required: true,
              message: '角色名称不能为空'
            }],
          })(<Input />)}
        </FormItem>
       <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="角色标识">
          {getFieldDecorator('roleCode', {
            rules: [{
              required: true,
              message: '角色标识不能为空'
            }],
          })(<Input />)}
        </FormItem>
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="角色描述">
          {getFieldDecorator('roleDesc', {
          })(<TextArea autosize={{ minRows: 2, maxRows: 6 }} />)}
        </FormItem>
      </Modal>
    ); 
  }
}
@connect(({ role, loading }) => ({
  role,
  loading: loading.models.role,
}))

@Form.create()
export default class Role extends PureComponent {

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
      dataIndex: 'roleId',
      render: (text, record, index) => index + 1
    },
    {
        title: '角色名称',
        dataIndex: 'roleName'
    },
    {
        title: '角色标识',
        dataIndex: 'roleCode'
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      render: val => moment(val).format('YYYY-MM-DD HH:mm:ss')
    },
    {
      title: '角色描述',
      dataIndex: 'roleDesc',
      width: 300
    },
    {
      title: '操作',
      render: (text, record) => (
        <Fragment>
          <a onClick={() => this.handleUpdate(record.roleId)}>配置</a>
          <Divider type="vertical" />
          <a onClick={() => this.handleDeleteUser(record.roleId)}>删除</a>
        </Fragment>
      )
    }
  ]

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'role/fetch',
      payload: {}
    });
  }

  handleDeleteUser = (id) => {
  
    const { dispatch } = this.props;
    Modal.confirm({
      title: '提示',
      content: '此操作将永久删除该角色, 是否继续?',
      okText: '确认',
      cancelText: '取消',
      onOk: () => {
        dispatch({
          type: 'role/delete',
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
      content: '此操作将永久删除这些用户, 是否继续?',
      okText: '确认',
      cancelText: '取消',
      onOk: () => {
        dispatch({
          type: 'role/delete',
          payload: selectedRows.map(el => el.id).join(','),
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
          type: 'role/add',
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
      
      this.handleModalVisible(true);
      
      this.setState({
        modalStatus: 'update',
        modalTitle: '配置',
        modalData: data,
        modalOk: (fields) => {
          const { dispatch } = this.props;
          dispatch({
            type: 'role/update',
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
      type: 'role/fetch',
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
        type: 'role/fetch',
        payload: fieldsValue,
      });
    });
  }

  handleFormReset = () => {
    const { dispatch, form } = this.props;
    form.resetFields();
    dispatch({
      type: 'role/fetch',
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
            <FormItem label="角色名称">
              {getFieldDecorator('roleName')(<Input placeholder="请输入" />)}
            </FormItem>
          </Col>
          <Col md={8} sm={24}>
            <span className={styles.submitButtons}>
              <Button type="primary" htmlType="submit">
                查询
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
      role: { data },
      loading,
      dispatch
    } = this.props;

    const { selectedRows, modalVisible, modalTitle, modalData, modalOk, modalStatus } = this.state;
  
    const parentMethods = {
      handleAdd: modalOk,
      handleModalVisible: this.handleModalVisible,
    };

    const parentProps = {
      modalTitle,
      modalData,
      modalStatus
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
              <Button icon="upload" type="primary" onClick={this.handleUpdateModalVisible}>
                导入
              </Button>
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
              rowKey="roleId"
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