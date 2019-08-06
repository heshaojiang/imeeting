import React, { PureComponent, Fragment, Component } from "react";
import {
  Modal, Form, Input, Select, Row, Col, Message
} from "antd";
import { validateField, validateAcdField } from "@/services/api";

import { getPhoneNumPattern } from '@/utils/utils';
import { formatMessage, getLocale } from 'umi/locale';

const Option = Select.Option;
const FormItem = Form.Item;

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
    }
  }

  componentDidMount() {

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
    if (modalData.username !== value) {
      validateField('username', value).then(data => {
        if (!data.success) {
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
    if (value === null || value === undefined || value === "") {
      return;
    }

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


  render() {

    const {
      modalVisible,
      modalTitle,
      form: {
        getFieldDecorator
      },
      modalStatus,
      modalType
    } = this.props;

    const selectStatus = modalStatus === "create" ? false : true;

    const { inputVisible, roleData } = this.state;

    let flagStatusOptions;
    if (modalType.flagStatus) {
      flagStatusOptions = modalType.flagStatus.map(el => <Option key={el.value} value={el.value}>{el.label}</Option>)
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
          label={formatMessage({ id: 'app.admin.user.username' })}
        >
          {getFieldDecorator('username', {
            validateFirst: true,
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
                pattern: new RegExp('^\\w+$', 'g'),
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
              message: formatMessage({ id: 'app.admin.user.nickname.format' }, {}),
              min: 1,
            },
            {
              message: formatMessage({ id: 'app.admin.user.nickname.overmaxlen' }, {}),
              max: 64
            }
            ],
          })(<Input placeholder={formatMessage({ id: 'app.admin.user.nickname.input' })} />)}
        </FormItem>
        {
          modalStatus === "create" &&
          (
            <Fragment>
              <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.user.password' })}>
                {getFieldDecorator('password', {
                  rules: [{
                    required: true, message: formatMessage({ id: 'app.admin.user.password.format' }, {}), min: 6, max: 32
                  }, {
                    validator: this.validateToNextPassword,
                  }],
                })(<Input type="password" />)}
              </FormItem>
              <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.user.password.confirm' })}>
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
        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.email' })}>
          {getFieldDecorator('email', {
            validateFirst: true,
            rules: [
              {
                type: "email",
                message: formatMessage({ id: 'app.admin.email.format' }, {}),
              },
              {
                message: formatMessage({ id: 'app.admin.email.overmaxlen' }, {}),
                max: 64
              }
            ]
          })(<Input />)}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.admin.phone' })}>
          {getFieldDecorator('call', {
            validateFirst: true,
            rules: [{
              required: true,
              pattern: getPhoneNumPattern(),
              message: formatMessage({ id: 'app.admin.phone.format' })
            }, {
              validator: this.validatorPhone
            }],
          })(<Input disabled={selectStatus} />)}
        </FormItem>
        {/* {
          modalStatus === "update" &&
          <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.status' })}>
            {getFieldDecorator('delFlag', {
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

export default CreateForm;