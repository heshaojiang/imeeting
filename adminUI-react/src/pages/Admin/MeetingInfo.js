import React, { PureComponent, Fragment, Component } from 'react';
import { connect } from 'dva';
import { Icon,Tooltip } from 'antd';
import {
  Card, Checkbox, Divider, Modal, Button, Form, Input, InputNumber, Select,
  Row, Col, DatePicker, Switch
} from 'antd';
import moment from 'moment';
import SelectCustomer from '@/components/SelectCustomer';
import PageHeaderWrapper from '@/components/PageHeaderWrapper';
import StandardTable from '@/components/StandardTable';
import Authorized,{ isAdminUser } from '@/utils/Authorized';
import styles from './tableList.less';
import { getMeeting, fetchMeetingUsers, validateField } from '@/services/api';
import { FormattedMessage, formatMessage, getLocale } from 'umi/locale';
import isEmpty from 'lodash/isEmpty';
import {getUserId} from "../../utils/authority";
const FormItem = Form.Item;
const Option = Select.Option;
const def_maxNumJoin = 6;
const def_meetingType = "1";
const def_splitType = "six";
const def_compere = getUserId();
/**
 * 自由会议
 */
const MEETING_TYPE_FREEDOM = "1";
/**
 * 培训模式
 */
const MEETING_TYPE_CLASSROOM = "2";
/**
 * 主持模式
 */
const MEETING_TYPE_COMPERE = "3";
@Form.create({
  mapPropsToFields(props) {
    return {
      meetingId: Form.createFormField({
        value: props.modalData.meetingId,
      }),
      meetingName: Form.createFormField({
        value: props.modalData.meetingName,
      }),
      meetingType: Form.createFormField({
        value: props.modalData.meetingType,
      }),
      compere: Form.createFormField({
        value: props.modalData.compere ? parseInt(props.modalData.compere) : null
      }),
      participants: Form.createFormField({
        value: props.modalData.participants
      }),
      status: Form.createFormField({
        value: props.modalData.status
      }),
      startTime: Form.createFormField({
        value: moment()
      }),
      endTime: Form.createFormField({
        value: moment()
      }),
      numVideo: Form.createFormField({
        value: props.modalData.numVideo,
      }),
      numJoin: Form.createFormField({
        value: props.modalData.numJoin
      }),
      meetingPwd: Form.createFormField({
        value: props.modalData.meetingPwd
      }),
      comperePwd: Form.createFormField({
        value: props.modalData.comperePwd
      }),
      customerId: Form.createFormField({
        value: props.modalData.customerId
      }),
      splitType: Form.createFormField({
        value: props.modalData.splitType
      }),
      compereDpi: Form.createFormField({
        value: props.modalData.compereDpi
      }),
      compereVideoEnable: Form.createFormField({
        value: props.modalData.compereVideoEnable
      }),
      compereAudioEnable: Form.createFormField({
        value: props.modalData.compereAudioEnable
      }),
      participantVideoEnable: Form.createFormField({
        value: props.modalData.participantVideoEnable
      }),
      participantAudioEnable: Form.createFormField({
        value: props.modalData.participantAudioEnable
      }),
      participantDpi: Form.createFormField({
        value: props.modalData.participantDpi
      }),
      showMode: Form.createFormField({
        value: props.modalData.showMode
      }),
      participantPlanList: Form.createFormField({
        value: props.modalData.participantPlanList && props.modalData.participantPlanList.map((el) => el.user_id)
      })
    }
  }
})
class CreateForm extends PureComponent {

  constructor() {
    super();
    this.state = {
      validateStatus: 'success',
      maxNumJoin: def_maxNumJoin,
      showComperePwd: true,
      ptcpnts_anybodychecked: true,
      help: '',
      isCompereVideoEnable: undefined,
      isCompereAudioEnable: undefined,
      isParticipantVideoEnable: undefined,
      isParticipantAudioEnable: undefined,
      expand: false,
      isRequired: false,
    }
  }

  componentDidMount() {
    fetchMeetingUsers().then((data) => {
      this.setState({
        userlist: data,
      });
    })

  }
  componentWillReceiveProps(nextProps) {

      //打开界面时自动选择上 “所有人”
     if (this.props.modalData.participantPlanList !== nextProps.modalData.participantPlanList){
      let ptcpnts_anybodychecked = true;

      if (!isEmpty(nextProps.modalData.participantPlanList)){
        //是在打开modal时才判断participantPlanList不为空时设置anybodychecked为false
        ptcpnts_anybodychecked = false;
      }
      this.setState({
        ptcpnts_anybodychecked: ptcpnts_anybodychecked,
      });

    }
    //打开界面时根据会议模式判断是否隐藏主席密码
    if (this.props.modalData.meetingType !== nextProps.modalData.meetingType){
      if (!isEmpty(nextProps.modalData.meetingType)){
        this.checkMeetingType(nextProps.modalData.meetingType);
      }
    }
  }

  validatorMeetingId = (rule, value, callback) => {
    const { modalData } = this.props;
    if (modalData.meetingId !== value){
      validateField('meetingid', value).then(data => {
        if(!data.success) {
          callback(formatMessage({ id: '3021' }));
        } else {
          callback();
        }
      });
    } else {
      callback();
    }

  };

  okHandle_save = (fieldsValue) => {
    const { form, handleAdd } = this.props;
    form.resetFields();
    if (fieldsValue.participantPlanList) {
      fieldsValue = {
        ...fieldsValue,
        participantPlanList: fieldsValue.participantPlanList.map(el => {
          return {
            user_id: el,
            join_type:2 //默认都是接入方
          };
        })
      };
    }
    this.setState({
      ptcpnts_anybodychecked: true,
      isCompereVideoEnable: undefined,
      isCompereAudioEnable: undefined,
      isParticipantVideoEnable: undefined,
      isParticipantAudioEnable: undefined,
    });
    handleAdd(fieldsValue);
  }
  okHandle = () => {
    const { form, handleAdd } = this.props;
    form.validateFields((err, fieldsValue) => {

      if (err) return;

      const joinPassword = form.getFieldValue("meetingPwd");
      if (isEmpty(joinPassword)) {//空密码时做一下确认提示
        Modal.confirm({
          title: formatMessage({ id: 'app.admin.tips' }),
          content: formatMessage({ id: 'app.admin.meeting.emptypsw' }),
          okText: formatMessage({ id: 'app.admin.confirm' }),
          cancelText: formatMessage({ id: 'app.admin.cancel' }),
          onOk: () => {
            this.okHandle_save(fieldsValue);
          }
        });
      } else {//已经设置密码，不需要提示
        this.okHandle_save(fieldsValue);
      }

    });
  }



  cancelHandle = () => {
    const { handleModalVisible, modalData } = this.props;

    const { meetingType } = modalData;
    if (meetingType === MEETING_TYPE_FREEDOM) {
      this.setState({expand: false});
    } else {
      this.setState({expand: true});
    }
    this.setState({
      ptcpnts_anybodychecked: true,
      validateStatus: 'success',
      help: '',
      isCompereVideoEnable: undefined,
      isCompereAudioEnable: undefined,
      isParticipantVideoEnable: undefined,
      isParticipantAudioEnable: undefined,
    });
    handleModalVisible();
  }

  // disabledDate = (current) => {
  //   // Can not select days before today and today
  //   return current && current < moment().endOf('day');
  // }


  // onChangeSplitType = (value) => {
  //   console.log(`selected ${value}`);
  //   this.props.form.setFieldsValue({
  //     splitType: value
  //   });
  //   // console.log(`obj ${obj.length}`);
  //   console.log(`FieldValue ${this.props.form.getFieldValue("splitType")}`);
  //   this.props.form.validateFields(['participantPlanList'], { force: true });
  // };

  checkMeetingType = (meetingType) => {

    let showComperePwd = true;
    if (meetingType === MEETING_TYPE_FREEDOM) {
      showComperePwd = false; //自由会议模式，不需要主席密码
      this.setState({expand:false, isRequired:false})
    }else{this.setState({
      expand:true, isRequired:true
    })}
    this.setState({
        showComperePwd: showComperePwd
      });
  }
  validateMeetingType = (rule, value, callback) => {
     this.props.form.validateFields(["numJoin"], { force: true }, (errors, values) => {
    });
    callback();
  }

  onChangeSwitch =(checked, localtion)=>{
    switch (localtion) {
      case 1:
        this.setState({
          isCompereVideoEnable: checked
        });
        break;
      case 2:
        this.setState({
          isCompereAudioEnable: checked
        });
        break;
      case 3:
        this.setState({
          isParticipantVideoEnable: checked
        });
        break;
      case 4:
        this.setState({
          isParticipantAudioEnable: checked
        });
        break;
    }
  }

  onChangeMeetingType = (meetingType) => {

    const { modalData } = this.props;
    let splitType = modalData.splitType;
    let numVideo = modalData.numVideo;

    if (meetingType === MEETING_TYPE_CLASSROOM) {//培训模式通话方数最大为3
      splitType = "three"
      numVideo = "3";
    }else{
      numVideo = "6";
    }

    this.props.form.setFieldsValue({
      splitType: splitType,
      numVideo: numVideo,
    });

    this.checkMeetingType(meetingType);

  };
  validatorNumJoin = (rule, value, callback) => {
    const { form, modalData } = this.props;
    // message: formatMessage({ id: 'app.admin.meeting.maximum' })+ " 1-"+this.state.maxNumJoin,
    // if (modalData.numJoin !== value){
      let maxNumJoin = 100;

      const meetingType = form.getFieldValue("meetingType");
      if (meetingType === MEETING_TYPE_FREEDOM) {//自由会议模式，接入方最大为5
        maxNumJoin = 6;
      }
      // console.log(`validatorNumJoin meetingType: ${meetingType} value:${value} maxNumJoin:${maxNumJoin}`);
      if (value > maxNumJoin){
        callback(formatMessage({ id: 'app.admin.meeting.maximum.info' })+ maxNumJoin);
      } else {
        callback();
      }

      this.props.form.validateFields(['participantPlanList'], { force: true }, (errors, values) => {
      });

    // } else {
    //   callback();
    // }

  };

  // getSplitTypeScreenNum = (splitType) =>{
  //   let screenNum = 0;
  //   switch (splitType) {
  //     case "one":
  //       screenNum = 1;
  //       break;
  //     case "two":
  //       screenNum = 2;
  //       break;
  //     case "twoWithHover":
  //       screenNum = 2;
  //       break;
  //     case "three":
  //       screenNum = 3;
  //       break;
  //     case "threeWithHover":
  //       screenNum = 3;
  //       break;
  //     case "four":
  //       screenNum = 4;
  //       break;
  //     case "six":
  //       screenNum = 6;
  //       break;
  //     default:
  //       screenNum = 0;
  //       break;
  //   }
  //   return screenNum;
  // };
  validateParticipant = (rule, value, callback) => {
    const form = this.props.form;
    const participantList = value;
    const numParticipant = participantList === undefined ? 0 : participantList.length;
    // console.log(`numParticipant ${numParticipant}`);

    // const splitType = form.getFieldValue("splitType");
    // console.log(`splitType ${splitType}`);
    // const numVideo = this.getSplitTypeScreenNum(splitType);
    const numJoin = form.getFieldValue("numJoin");
    // console.log(`numVideo ${numVideo}`);
    if (numParticipant > numJoin) {
      callback(formatMessage({ id: 'app.admin.meeting.maxjoin'}));
    } else {
      callback();
    }
  };
  onChange_ptcpnts_anybody = (e) => {
    this.setState({
      ptcpnts_anybodychecked: e.target.checked,
    });
    if (e.target.checked){//选择所有人时，设置计划参会人员为空
      this.props.form.setFieldsValue({participantPlanList: undefined});
    }

  };

  toggle = () => {
    const { expand } = this.state;
    this.setState({ expand: !expand });
  };


  render() {
    const _isRequired = this.state.isRequired;
    const _compereVideoEnable = this.state.isCompereVideoEnable === undefined ? this.props.modalData.compereVideoEnable : this.state.isCompereVideoEnable;
    const _compereAudioEnable= this.state.isCompereAudioEnable === undefined ? this.props.modalData.compereAudioEnable : this.state.isCompereAudioEnable;
    const _participantVideoEnable= this.state.isParticipantVideoEnable === undefined ? this.props.modalData.participantVideoEnable : this.state.isParticipantVideoEnable;
    const _participantAudioEnable= this.state.isParticipantAudioEnable === undefined ? this.props.modalData.participantAudioEnable : this.state.isParticipantAudioEnable;

    const {
      modalVisible,
      modalTitle,
      form: {
        getFieldDecorator
      },
      modalType: {
        meetingType,
        meetingStatus,
        splitType,
        meetingDpi,
        meetingShowMode,
      }
    } = this.props;

    let OptionsMeetingType, OptionsmeetingStatus, OptionsSplitType, OptionsUserlist, OptionsMeetingDpi, OptionMeetingShowMode;

    if(meetingType) {
      OptionsMeetingType= meetingType.map(el => <Option key={el.value} value={el.value}>{el.label}</Option>);
    }
    if(meetingStatus) {
      OptionsmeetingStatus= meetingStatus.map(el => <Option key={el.value} value={el.value}>{el.label}</Option>);
    }
    if(splitType) {
      OptionsSplitType = splitType.map(el => <Option key={el.value} value={el.value}>{el.label}</Option>);
    }
    if(meetingDpi) {
      OptionsMeetingDpi = meetingDpi.map(el => <Option key={el.value} value={el.value}>{el.label}</Option>);
    }
    if(meetingShowMode){
      OptionMeetingShowMode = meetingShowMode.map(el => <Option key={el.value} value={el.value}>{el.label}</Option>);
    }

    if(this.state.userlist) {
      OptionsUserlist = this.state.userlist.map(el => el.username && <Option key={el.userId} value={el.userId}>{`${el.nickname}(${el.username})`}</Option>);
    }


    // const dateFormat = 'YYYY-MM-DD HH:mm:ss';

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
                span: 8
            },
            wrapperCol: {
                span: 15
            }
        };
    }
    // console.log(`render ptcpnts_anybodychecked:${this.state.ptcpnts_anybodychecked}`);



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
          label={formatMessage({ id: 'app.admin.meeting.no'})}
          // hasFeedback
        >
          {getFieldDecorator('meetingId', {
            validateFirst : true,
            rules: [{
              required: true,
              min: 4,
              max: 11,
              message: formatMessage({ id: 'app.admin.meeting.no.format'}, {}),
            },
            {
              pattern:new RegExp('^[0-9]*$','g'),
              message: formatMessage({ id: 'app.admin.meeting.no.regx' }),
            },
            {
                validator: this.validatorMeetingId
            }]
          })(<Input />)}
        </FormItem>
        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.meeting.name'})}>
          {getFieldDecorator('meetingName', {
            rules: [{
              required: true,
              min: 1,
              message: formatMessage({ id: 'app.admin.meeting.name.format'}, {}),
            },
            {
              max: 64,
              message: formatMessage({ id: 'app.admin.meeting.name.overmaxlen'}, {}),
            }
            ]
          })(<Input />)}
        </FormItem>

        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.meeting.mode'})}>
          {getFieldDecorator('meetingType', {
            rules: [{
              required: true,
              message: formatMessage({ id: 'app.admin.meeting.mode.select'}),
            }, {
              validator: this.validateMeetingType,
            }],
          })(
              <Select
                  style={{ width: 295 }}
                  placeholder={formatMessage({ id: 'app.admin.meeting.mode.select'})}
                  onChange={this.onChangeMeetingType}
              >
                {OptionsMeetingType}
              </Select>
          )}
        </FormItem>

        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.meeting.compere'})} style={{display:this.state.expand? 'block' : 'none' }}>
          {getFieldDecorator('compere', {
            rules: [{
              required: _isRequired,
              // message: formatMessage({ id: 'app.admin.compere.default.select' }),
            }],
          })(
              <Select
                  mode="single"
                  style={{ width: 295 }}
                  // placeholder={formatMessage({ id: 'app.admin.compere.default.select'})}
              >{OptionsUserlist}
              </Select>
          )}
        </FormItem>

        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.meeting.compere.pwd'})} style={{display: this.state.expand? 'block' : 'none' }}>
          {getFieldDecorator('comperePwd', {
            rules: [{
              required: _isRequired,
              min: 1,
              message: formatMessage({ id: 'app.admin.meeting.needcomperepwd'}, {})
            }],
          })(<Input type="password" />)}
        </FormItem>


        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.meeting.split.mode'})}>
          {getFieldDecorator('splitType', {
            rules: [{
              required: true,
              message: formatMessage({ id: 'app.admin.meeting.split.mode.select'}, {}),
            }],
          })(
              <Select
                  style={{ width: 295 }}
                  placeholder={formatMessage({ id: 'app.admin.meeting.split.mode.select' })}
              >
                {OptionsSplitType}
              </Select>
          )}
        </FormItem>

        {/*<FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.meeting.show.mode'})}>
          {getFieldDecorator('showMode', {
            rules: [{
              required: true,
              message: formatMessage({ id: 'app.admin.meeting.show.mode.select'}),
            }, {
              validator: this.validateMeetingType,
            }],
          })(
              <Select
                  style={{ width: 295 }}
                  placeholder={formatMessage({ id: 'app.admin.meeting.show.mode.select'})}
              >
                {OptionMeetingShowMode}
              </Select>
          )}
        </FormItem>*/}

        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.meeting.compere.dpi'})} style={{display: this.state.expand? 'block' : 'none' }}>
          {getFieldDecorator('compereDpi', {
            rules: [{
              required: false,
              message: formatMessage({ id: 'app.admin.meeting.dpi.select' }),
            }, ],
          })(
              <Select
                  style={{ width: 295 }}
                  placeholder={
                    formatMessage({ id: 'app.admin.meeting.dpi.select' })}
              >
                {OptionsMeetingDpi}
              </Select>

          )}
        </FormItem>

        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.meeting.participant.dpi'})} style={{display: this.state.expand? 'block' : 'none' }}>
          {getFieldDecorator('participantDpi', {
            rules: [{
              required: false,
              message: formatMessage({ id: 'app.admin.meeting.dpi.select'}),
            }, {
              validator: this.validateMeetingType,
            }],
          })(
              <Select
                  style={{ width: 295 }}
                  placeholder={formatMessage({ id: 'app.admin.meeting.dpi.select'})}
              >
                {OptionsMeetingDpi}
              </Select>
          )}
        </FormItem>

        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.meeting.join.pwd'})} >
          {getFieldDecorator('meetingPwd', {})(<Input type="password" />)}
        </FormItem>

        {/*<FormItem {...formItemLayout} label={<span>{formatMessage({ id: 'app.admin.customer.parties'})}
          <Tooltip title={formatMessage({id: 'app.admin.meeting.numvideo.tips'})}><Icon type="question-circle-o" /></Tooltip></span>
        } style={{display: this.state.expand? 'block' : 'none' }}>
          {getFieldDecorator('numVideo')(<InputNumber style={{ width: 295 }} min={0} disabled={true} />)}
        </FormItem>*/}

        <FormItem {...formItemLayout} label={<span>{formatMessage({ id: 'app.admin.customer.callers'})}
          <Tooltip title={formatMessage({id: 'app.admin.meeting.numjoin.tips'})}><Icon type="question-circle-o" /></Tooltip></span>
        } style={{display: this.state.expand? 'block' : 'none' }}>
          {getFieldDecorator('numJoin', {
            rules: [{
              required:false,
              type: 'number',
              min:1,
              message: formatMessage({ id: 'app.admin.meeting.numJoin.max' })+ " 100",
            },
              {
                validator: this.validatorNumJoin
              }],
          })(<InputNumber style={{ width: 295 }} min={0} />)}
        </FormItem>

        {/*<FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.meeting.mode'})}>
            {getFieldDecorator('meetingType', {
              rules: [{
                required: true,
                message: formatMessage({ id: 'app.admin.meeting.mode.select'}),
              }, {
                validator: this.validateMeetingType,
              }],
            })(
            <Select
              style={{ width: 295 }}
              placeholder={formatMessage({ id: 'app.admin.meeting.mode.select'})}
              onChange={this.onChangeMeetingType}
            >
              {OptionsMeetingType}
            </Select>
          )}
        </FormItem>
        {
          this.state.showComperePwd &&
          <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.meeting.compere.pwd'})}>
          {getFieldDecorator('comperePwd', {
              rules: [{
                required: true,
                message: formatMessage({ id: 'app.admin.meeting.needcomperepwd'}),
              }],
          })(<Input type="password" />)}
          </FormItem>
        }*/}



          {/*
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="状态">
            {getFieldDecorator('status', {
              rules: [{
                required: true,
                message: '请选择会议状态',
              }],
            })(
            <Select
              style={{ width: 295 }}
              placeholder="选择状态"
            >
              {OptionsmeetingStatus}
            </Select>
          )}
        </FormItem>
        */}


        { <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.compere.default'})} style={{display: this.state.expand? 'block' : 'none' }}>
          &nbsp;&nbsp;
          <FormattedMessage id="app.admin.meeting.video" />
          &nbsp;&nbsp;&nbsp;&nbsp;
          {getFieldDecorator('compereVideoEnable')
            (<span>
              <Switch checkedChildren={formatMessage({id: 'app.settings.open'})}
                     unCheckedChildren={formatMessage({id: 'app.settings.close'})}
                     checked={_compereVideoEnable}
                     onChange={(checked,event)=>this.onChangeSwitch(checked, 1)}
            />
            </span>)}
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <FormattedMessage id="app.admin.meeting.audio" />
          &nbsp;&nbsp;&nbsp;&nbsp;
          {getFieldDecorator('compereAudioEnable')
          (<Switch checkedChildren={formatMessage({id: 'app.settings.open'})}
                   unCheckedChildren={formatMessage({id: 'app.settings.close'})}
                   checked={_compereAudioEnable}
                   onChange={(checked,event)=>this.onChangeSwitch(checked, 2)}
          />)}
        </FormItem>
        }

        { <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.participant.default'})} style={{display: this.state.expand? 'block' : 'none' }}>
          &nbsp;&nbsp;
          <FormattedMessage id="app.admin.meeting.video" />
          &nbsp;&nbsp;&nbsp;&nbsp;
          {getFieldDecorator('participantVideoEnable')
          (<span>
            <Switch checkedChildren={formatMessage({id: 'app.settings.open'})}
                   unCheckedChildren={formatMessage({id: 'app.settings.close'})}
                   checked={_participantVideoEnable}
                   onChange={(checked,event)=>this.onChangeSwitch(checked, 3)}
          />
          </span>)}
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <FormattedMessage id="app.admin.meeting.audio" />
          &nbsp;&nbsp;&nbsp;&nbsp;
          {getFieldDecorator('participantAudioEnable')
          (<Switch checkedChildren={formatMessage({id: 'app.settings.open'})}
                   unCheckedChildren={formatMessage({id: 'app.settings.close'})}
                   checked={_participantAudioEnable}
                   onChange={(checked,event)=>this.onChangeSwitch(checked, 4)}
          />)}
        </FormItem>
        }





        <FormItem {...formItemLayout} label={formatMessage({ id: 'app.admin.meeting.Participants'})} >
            {getFieldDecorator('participantPlanList', {
              rules: [{
                required: false,
                message: formatMessage({ id: 'app.admin.meeting.Participants.select' }),
              }, {
                validator: this.validateParticipant,
              }],
            })(
            <Select
              disabled={this.state.ptcpnts_anybodychecked}
              mode="multiple"
              style={{ width: 295 }}
              placeholder={ this.state.ptcpnts_anybodychecked ?
                formatMessage({ id: 'app.admin.meeting.Participants.anybodyinfo' }):
                formatMessage({ id: 'app.admin.meeting.Participants.select' })}
            >
              {OptionsUserlist}
            </Select>

          )}
          <Checkbox
            checked={this.state.ptcpnts_anybodychecked}
            onChange={this.onChange_ptcpnts_anybody}
            >
            { formatMessage({ id: 'app.admin.meeting.Participants.anybody' })}
            </Checkbox>
        </FormItem>

        {/*<a style={{ marginLeft:360, fontSize: 14 }} onClick={this.toggle} >
          {formatMessage({ id: 'app.admin.form.more'})} <Icon type={this.state.expand ? 'up' : 'down'} />
        </a>*/}
      </Modal>

    );
  }
}

@connect(({ meetingInfo, loading, user }) => ({
  meetingInfo,
  loading: loading.models.meetingInfo,
  allType: user.allType
}))
@Form.create()
export default class MeetingInfo extends PureComponent {

  state = {
    selectedRows: [],
    modalStatus: '',
    modalVisible: false,
    uploadModalVisible: false,
    modalTitle: '',
    modalData: {},
    formValues: {},
    modalOk: () => {}
  }

  columns = [
    {
      title: formatMessage({ id: 'app.admin.order' }),
      dataIndex: 'id',
      render: (text, record, index) => index + 1
    },
    {
      title: formatMessage({ id: 'app.admin.meeting.no' }),
      dataIndex: 'meetingId',
    },
    {
      title: formatMessage({ id: 'app.admin.meeting.name' }),
      dataIndex: 'meetingName',
    },
    // {
    //   title: formatMessage({ id: 'app.admin.meeting.type' }),
    //   dataIndex: 'meetingType',
    //   render: (val) => this.props.allType.meetingType.find(el => el.value === val).label
    // },
    // {
    //   title: formatMessage({ id: 'app.admin.meeting.compere' }),
    //   dataIndex: 'compere',
    // },
    // {
    //   title: '通话方数量',
    //   dataIndex: 'numVideo',
    //     render: val => val==0 ? '未限制' : val
    // },
    {
      title: formatMessage({ id: 'app.admin.meeting.maximum' }),
      dataIndex: 'numJoin',
        render: val => val==0 ? <FormattedMessage id="app.admin.meeting.unlimit" /> : val
    },
    {
      title: formatMessage({ id: 'app.admin.meeting.participants.plan' }),
      dataIndex: 'participants_plan',
        render: (text, record) => record.participantPlanList.length==0? formatMessage({ id: 'app.admin.meeting.Participants.anybody' }) :record.participantPlanList.length
    },
    {
      title: formatMessage({ id: 'app.admin.meeting.participants.current' }),
      dataIndex: 'participants'
    },
    // {
    //   title: formatMessage({ id: 'app.admin.begin.date' }),
    //   dataIndex: 'startTime',
    //   render: val => val && moment(val).format('YYYY-MM-DD HH:mm:ss')
    // },
    // {
    //   title: formatMessage({ id: 'app.admin.end.date' }),
    //   dataIndex: 'endTime',
    //   render: val => val && moment(val).format('YYYY-MM-DD HH:mm:ss')
    // },
/*    {
      title: '状态',
      dataIndex: 'status',
      render: (val) => this.props.allType.meetingStatus.find(el => el.value === val).label
    },
*/
    {
      title: formatMessage({ id: 'app.admin.operate' }),
      render: (text, record) => (
        <Fragment>
          <a onClick={() => this.handleUpdate(record.id)}>{formatMessage({ id: 'app.admin.update' })}</a>
          { <Divider type="vertical" /> }
          {<a onClick={() => this.handleDeleteMeeting(record.id)}>{formatMessage({ id: 'app.admin.delete' })}</a>}
        </Fragment>
      )
    }
  ]

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'meetingInfo/fetch',
      payload: {}
    });
  }

  handleDeleteMeeting = (id) => {
    const { dispatch } = this.props;
    Modal.confirm({
      title: formatMessage({ id: 'app.admin.tips' }),
      content: formatMessage({ id: 'app.admin.meeting.delete.tips' }),
      okText: formatMessage({ id: 'app.admin.confirm' }),
      cancelText: formatMessage({ id: 'app.admin.cancel' }),
      onOk: () => {
        dispatch({
          type: 'meetingInfo/delete',
          payload: id
        });
      }
    });
  }

  handleBatchUser = () => {
    const { dispatch } = this.props;
    const { selectedRows } = this.state;

    Modal.confirm({
      title: formatMessage({ id: 'app.admin.tips' }),
      content: formatMessage({ id: 'app.admin.meeting.delete.batch.tips' }),
      okText: formatMessage({ id: 'app.admin.confirm' }),
      cancelText: formatMessage({ id: 'app.admin.cancel' }),
      onOk: () => {
        dispatch({
          type: 'meetingInfo/delete',
          payload: selectedRows.map(el => el.id).join(','),
          callback: () => {
            this.setState({
              selectedRows: []
            });
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
      modalData: {numJoin:def_maxNumJoin, numVideo: 6, meetingType: def_meetingType, splitType: def_splitType, compere: def_compere},
      modalOk: (data) => {
        dispatch({
          type: 'meetingInfo/add',
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
    });
  }

  // 更新
  handleUpdate = (id) => {
    getMeeting(id).then((data) => {
      this.handleModalVisible(true);

      this.setState({
        modalStatus: 'update',
        modalTitle: formatMessage({ id: 'app.admin.update' }),
        modalData: data,
        modalOk: (fields) => {
          const { dispatch } = this.props;
          dispatch({
            type: 'meetingInfo/update',
            payload: {
              ...data,
              ...fields
            }
          });
          this.handleModalVisible();
        },

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
      type: 'meetingInfo/fetch',
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
        type: 'meetingInfo/fetch',
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
      type: 'meetingInfo/fetch',
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
            <FormItem label={formatMessage({ id: 'app.admin.meeting.search' })}>
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
      meetingInfo: { data },
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
              {/* <Button icon="upload" type="primary" onClick={this.handleUpdateModalVisible}>
                导入
              </Button> */}
              {
                selectedRows.length > 0 &&
                (
                  <Button icon="delete" type="primary" onClick={this.handleBatchUser}>
                   {formatMessage({ id: 'app.admin.delete.batch' })}
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
              rowKey="id"
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
