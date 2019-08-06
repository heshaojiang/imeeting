import React, { Component } from 'react';
import { connect } from 'dva';
import { Card, Modal, Form, Input, Button } from 'antd';
import { formatMessage } from 'umi/locale';

const FormItem = Form.Item;
@connect()  
@Form.create({})
export default class Setting extends Component {
  state = {  }

  compareToFirstPassword = (rule, value, callback) => {
    const form = this.props.form;
    if (value && value !== form.getFieldValue('newpassword1')) {
      callback(formatMessage({ id: 'app.admin.password.format' }));
    } else {
      callback();
    }
  }

  okHandle = () => {
    const { form, dispatch } = this.props;
    form.validateFields((err, fieldsValue) => {

      if (err) return;
      
      dispatch({
        type: 'user/changePassword',
        payload: {
            password: fieldsValue.password,
            newpassword1: fieldsValue.newpassword1 
        }
      })
      form.resetFields();
    });
  }

  resetHandle = () => {
    const { form } = this.props;
    form.resetFields();
  }

  passwordResetHandle = () => {
    const { dispatch } = this.props;
    Modal.confirm({
      title: formatMessage({ id: 'app.admin.tips' }),
      content: formatMessage({ id: 'app.admin.password.reset.format' }),
      okText: formatMessage({ id: 'app.admin.confirm' }),
      cancelText: formatMessage({ id: 'app.admin.cancel' }),
      onOk: () => {
        dispatch({
          type: 'user/passwordReset',
        });
      }
    });
  }

  render() {

    const { getFieldDecorator } = this.props.form;
    const formItemLayout = {
      labelCol: { span: 4 },
      wrapperCol: { span: 10 },
    };
    return (
      <Card>
        <Form
          layout="horizontal" 
        >
          <FormItem label={formatMessage({ id: 'app.admin.user.password.old' })} {...formItemLayout}>
            {getFieldDecorator('password', {
                  rules: [{ required: true,message: formatMessage({ id: 'app.admin.user.password.input.old' },{}), min: 6 }],
            })(<Input type="password" />)}
          </FormItem>
          <FormItem label={formatMessage({ id: 'app.admin.user.password.new' })} {...formItemLayout}>
          {getFieldDecorator('newpassword1', {
            rules: [{ required: true, message: formatMessage({ id: 'app.admin.user.password.format' },{}), min: 6 }],
          })(<Input type="password" />)}
          </FormItem>
          <FormItem label={formatMessage({ id: 'app.admin.user.password.confirm' })} {...formItemLayout}>
          {getFieldDecorator('confirm', {
            rules: [{ 
              required: true, message: formatMessage({ id: 'app.admin.user.password.confirmation' },{}),
            }, {
              validator: this.compareToFirstPassword
            }],
          })(<Input type="password" />)}
          </FormItem>
          <FormItem wrapperCol={{ span: 10, offset: 4 }}>
            <Button style={{ marginRight: 5 }} onClick={this.okHandle}>{formatMessage({ id: 'app.admin.confirm' })}</Button>
            <Button style={{ marginRight: 5 }} onClick={this.resetHandle}>{formatMessage({ id: 'app.admin.reset' })}</Button>
            {/*<Button style={{ marginRight: 5 }} onClick={this.passwordResetHandle} type="primary">{formatMessage({ id: 'app.admin.password.reset' })}</Button>*/}
          </FormItem>
        </Form>
      </Card>
    );
  }
}