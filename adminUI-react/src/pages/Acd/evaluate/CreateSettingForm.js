import React, { PureComponent, Fragment, Component } from "react";
import { connect } from "dva";
import {
 Modal, Form, Input, Select,InputNumber
} from "antd";
import {validateAcdField } from "@/services/api";

import { formatMessage, getLocale } from 'umi/locale';
const Option = Select.Option;
const FormItem = Form.Item;

  @Form.create({
    mapPropsToFields(props) {
      return {
        name: Form.createFormField({
          value: props.modalData.name
        }),
        scoreValue: Form.createFormField({
          value: props.modalData.scoreValue
        }),
        scoreIndex: Form.createFormField({
          value: props.modalData.scoreIndex
        }),
        enable: Form.createFormField({
          value: props.modalData.enable===undefined || props.modalData.enable===null ? '1': (props.modalData.enable)+""
        }),
        desc: Form.createFormField({
          value: props.modalData.desc
        })
      };
    }
  })

class CreateSettingForm extends PureComponent {

    constructor() {
      super();
      this.state = {
        confirmDirty: false,
      }
    }
    //验证评价名称
    validatorScorename = (rule, value, callback) => {
      const { modalData } = this.props;
      if (modalData.name !== value) {
        validateAcdField('evaluateName', value).then(data => {
          if (!data.success) {
            callback(formatMessage({ id: 'app.acd.evaluate.score.name.exists' }));
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
      let flagStatusOptions;
      if (modalType.flagStatus) {
        flagStatusOptions = modalType.flagStatus.map(el => <Option  key={el.value} value={el.value}>{el.label}</Option>)
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
            label={formatMessage({ id: 'app.acd.evaluate.evaluatename' })}
          >
            {getFieldDecorator('name', {
              validateFirst: true,
              rules: [
                {
                  required: true,
                  message: formatMessage({ id: 'app.acd.evaluate.evaluatename.format' }),
                },
                {
                  message: formatMessage({ id: 'app.acd.evaluate.evaluatename.overmaxlen' }),
                  max: 64
                },
                {
                  validator: this.validatorScorename
                }]
            })(<Input disabled={selectStatus}
            />)}
          </FormItem>
          
          <FormItem {...formItemLayout} label={formatMessage({ id: 'app.acd.evaluate.evaluatelevel' })}>
            {getFieldDecorator('scoreValue', {
              validateFirst: true,
              rules: [
                {
                  required: true,
                  message: formatMessage({ id: 'app.acd.evaluate.score.format' }, {}),
                },
              ]
            })(<InputNumber placeholder={formatMessage({id:'app.acd.evaluate.score.input'},{})} style={{ width: 295 }} precision={0}  min={1} max={5} disabled={selectStatus}/>)}
          </FormItem>

          <FormItem {...formItemLayout} label={formatMessage({ id: 'app.acd.evaluate.evaluateindex' })}>
            {getFieldDecorator('scoreIndex', {
              validateFirst: true,
              rules: [
                {
                  required: true,
                  message: formatMessage({ id: 'app.acd.evaluate.evaluateindex.format' }, {}),
                },
              ]
            })(<InputNumber style={{ width: 295 }} precision={0}  min={1} max={999}/>)}
          </FormItem>
        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.acd.evaluate.evaluatenote' })}>
          {getFieldDecorator('desc', {
            validateFirst: true,
            rules: [{
              required: true,
              message: formatMessage({ id: 'app.acd.evaluate.evaluatenote.format' })
            }],
          })(<Input  />)}
        </FormItem>
          {/* <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.status' })}>
            {getFieldDecorator('enable', {
              rules: [{ required: true }],
            })(
              <Select
                style={{ width: 295 }}
              >
                {flagStatusOptions}
              </Select>
            )}
          </FormItem> */}
        </Modal>
      );
    }
  }

  export default CreateSettingForm;