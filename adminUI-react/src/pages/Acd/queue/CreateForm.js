import { PureComponent } from "react";
import { connect } from "dva";
import {
  Modal,
  Form,
  Input,
  Select,
  Row,
  Col,
  InputNumber,
  Table,
} from "antd";
import { validateAcdField } from "@/services/api";
import { fetchSelect } from "@/services/acdQueue";
import { formatMessage, getLocale } from 'umi/locale';
const Option = Select.Option;
const FormItem = Form.Item;

@Form.create({
  mapPropsToFields(props) {
    return {
      queueId: Form.createFormField({
        value: props.modalData.queueId
      }),
      queueName: Form.createFormField({
        value: props.modalData.queueName
      }),
      maxNum: Form.createFormField({
        value: props.modalData.maxNum
      }),
      strategy: Form.createFormField({
        value: props.modalData.strategy
      }),
      description: Form.createFormField({
        value: props.modalData.description
      }),
      agentList: Form.createFormField({
        value: props.modalData.agentLists && props.modalData.agentLists.map((el) => el.userId)
      })
    };
  }
})

class CreateForm extends PureComponent {

  constructor() {
    super();
  }
  state = {
    agentList: null
  }
  componentDidMount() {
    fetchSelect().then((data) => {
      this.setState({
        agentList: data,
      });
    })
  }

  validatorQueueId = (rule, value, callback) => {
    const { modalData } = this.props;
    var pattern = new RegExp("[0-9]{4,11}")

    if (!pattern.test(value)) {
      return;
    }

    if (modalData.queueId !== value) {
      validateAcdField('queueId', value).then(data => {
        if (!data.success) {
          callback(formatMessage({ id: 'app.acd.queue.queueId.exists' }));
        } else {
          callback();
        }
      });
    } else {
      callback();
    }
  };

  validatorQueueName = (rule, value, callback) => {
    const { modalData } = this.props;
    var pattern = new RegExp("[\u4e00-\u9fa5\\w]{2,64}")
    if (!pattern.test(value)) {
      callback();
    }

    if (modalData.queueName !== value) {
      validateAcdField('queueName', value).then(data => {
        if (!data.success) {
          callback(formatMessage({ id: 'app.acd.queue.queueName.exists' }));
        } else {
          callback();
        }
      });
    } else {
      callback();
    }
  };

  validatorAgent = (rule, value, callback) => {
    const { modalData,form } = this.props;
    let maxNum = form.getFieldValue("maxNum");
    if(typeof value ==='undefined' || value === null){
      callback();
      return;
    }

     if (maxNum === null || maxNum === undefined || maxNum === "") {
        this.props.form.validateFields(['maxNum'], { force: true }, (errors, values) => {});
     }else if(maxNum < value.length){
        callback(formatMessage({ id: 'app.acd.queue.capacity.maxlen' }) + maxNum);
     }else{
       callback();
     }
  }

  validateCapacity = (rule, value, callback) => {
       const { modalData,form } = this.props;
       const capacity=100;
       let agentList = form.getFieldValue("agentList");

       if(value === null || value === undefined || value === ""){
         callback();
         return;
       }

       if(value>capacity){
         callback(formatMessage({ id: 'app.acd.queue.capacity.max' }) + capacity);
         return;
       }

       if(typeof agentList !=='undefined'){
          if(agentList.length>value){
            this.props.form.validateFields(['agentList'], { force: true }, (errors, values) => {});
          }else{
            this.props.form.validateFields(['agentList'], { force: true }, (errors, values) => {});
            callback();
          }
        }else{
          callback();
        }
  }


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

  render() {
    const {
      modalVisible,
      modalTitle,
      form: { getFieldDecorator },
      modalStatus,
      strategy
    } = this.props;

    let formItemLayout = null;

    const selectStatus = modalStatus === "create" ? false : true;
    if (getLocale() === 'zh-CN') {
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

    let OptionsUserlist, optionStrategyList;

    if (this.state.agentList) {
      OptionsUserlist = this.state.agentList.map(el => el.username && <Option key={el.userId} value={el.userId}>{`${el.nickname}(${el.username})`}</Option>);
    }

    if (strategy) {
      optionStrategyList = strategy.map(el => <Option key={el.type} value={el.type} disabled={el.type==='agent-with-least-talk-time'}>{`${el.label}`}</Option>);
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
          label={formatMessage({ id: 'app.acd.queue.queueId' })}
        >
          {getFieldDecorator("queueId", {
            validateFirst: true,
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'app.acd.queue.queueId.minlen' }),
                min: 4,
              },
              {
                message: formatMessage({ id: 'app.acd.queue.queueId.maxlen' }),
                max: 64
              },
              {
                pattern: new RegExp('^\\d{4,11}$', 'g'),
                message: formatMessage({ id: 'app.acd.queue.queueId.regx' }),
              },
              {
                validator: this.validatorQueueId
              }
            ]
          })(
            <Input disabled={selectStatus} />
          )}
        </FormItem>

        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.acd.queue.queueName' })}
        >
          {getFieldDecorator("queueName", {
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'app.acd.queue.queueName.minlen' }),
                min: 2,
              },
              {
                message: formatMessage({ id: 'app.acd.queue.queueName.maxlen' }),
                max: 64
              },
              {
                pattern: new RegExp('^[\u4e00-\u9fa5\\w]+$', 'g'),
                message: formatMessage({ id: 'app.acd.queue.queueName.regx' }),
              },
              {
                validator: this.validatorQueueName
              }
            ]
          })(<Input />)}
        </FormItem>

        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.acd.queue.capacity' },{})}
        >
          {getFieldDecorator("maxNum", {
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'app.acd.queue.capacity.required' }),
              },
              {
                pattern: new RegExp('^[0-9]+$', 'g'),
                message: formatMessage({ id: 'app.acd.queue.capacity.regx' }),
              },
               {
                validator: this.validateCapacity
              }
              ]
          })(
            <InputNumber style={{ width: 295 }} min={0}/>
          )}
        </FormItem>

        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.acd.queue.strategy' })}
        >
          {getFieldDecorator("strategy", {
            rules: [
              {
                required: true,
                message: formatMessage({ id: 'app.acd.queue.strategy.required' }, {}),
              }
            ]
          })(<Select style={{ width: 295 }}>{optionStrategyList}</Select>)}
        </FormItem>

        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.acd.queue.agent' })}>
          {getFieldDecorator('agentList', {
            rules: [
              {
              validator: this.validatorAgent
            }
            ],
          })(
            <Select
              disabled={this.state.ptcpnts_anybodychecked}
              mode="multiple"
              style={{ width: 295 }}
              placeholder={formatMessage({ id: 'app.acd.queue.agent.select' })}
            >
              {OptionsUserlist}
            </Select>

          )}
        </FormItem>

        <FormItem
          {...formItemLayout}
          label={formatMessage({ id: 'app.acd.queue.description' })}
        >
          {getFieldDecorator("description", {
            rules: [
              {
                message: formatMessage({ id: 'app.acd.queue.description.maxlen' }),
                max: 255
              },
            ],
          })(
            <Input.TextArea style={{ width: 295 }} autosize={{ minRows: 1, maxRows: 3 }} />
          )}
        </FormItem>

      </Modal>
    );
  }
}

export default CreateForm;