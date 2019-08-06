import React, { PureComponent, Component } from "react";
import { connect } from "dva";
import {
  Card, Button, Form, Input, Select, Row, Col, Icon, Pagination , Message,DatePicker,Statistic,Table 
} from "antd";
import PageHeaderWrapper from "@/components/PageHeaderWrapper";
import moment from "moment";
import { formatMessage, getLocale } from 'umi/locale';
import styles from "./tableList.less";
import statusStyle from './Status.less'
const FormItem = Form.Item;
const Option = Select.Option;

@connect(({ status, loading }) => ({
  status,
  loading: loading.models.status,
}))
@Form.create()
export default class Status extends PureComponent {

  state = {
    formValues:{},
    makeCallTime:undefined,
    hangUpTime:undefined
  }

    componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: "status/fetchCall",
      payload: {}
    });
    dispatch({
      type: "status/status",
      payload: {}
    });
    dispatch({
      type: "status/statusCurrent",
      payload: {}
    });
  }

  handleSearch = e => {
    e.preventDefault();
    const { dispatch, form } = this.props;
    const dateFormat = "YYYY-MM-DD";
   
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      const values = {
        ...fieldsValue,
        makeCallTime:fieldsValue.makeCallTime===undefined?undefined:fieldsValue.makeCallTime.format(dateFormat),
        hangUpTime:fieldsValue.hangUpTime===undefined?undefined:fieldsValue.hangUpTime.format(dateFormat)
      };
      this.setState({
        formValues: values,
      });
      dispatch({
        type: "status/fetchCall",
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
      type: "status/fetchCall",
      payload: {}
    });
  };
  onShowSizeChange =(page, pageSize)=>{
    const { dispatch } = this.props;
    const { formValues } = this.state;
    const params = {
      page: page,
      limit: pageSize,
      ...formValues
    };

    dispatch({
      type: "status/fetchCall",
      payload: {...params}
    });
  }

   columns = [
     {
      title: formatMessage({ id: 'app.admin.order' }),
      dataIndex: "callId",
      render: (text, record, index) => index + 1
    },
    {
      title: formatMessage({ id: 'app.acd.calls.callerName' }),
      dataIndex: "callerName"
    },
    {
      title: formatMessage({ id: 'app.acd.calls.agentName' }),
      dataIndex: "agentName"
    },
    {
      title: formatMessage({ id: 'app.acd.calls.makecallTime' }),
      dataIndex: "makecallTime",
      render: val => val && moment(val).format('YYYY-MM-DD HH:mm:ss')
    },
    {
      title: formatMessage({ id: 'app.acd.calls.hangupTime' }),
      dataIndex: "hangupTime",
      render: val => val && moment(val).format('YYYY-MM-DD HH:mm:ss')
    },
    {
      title: formatMessage({ id: 'app.acd.calls.status' }),
      dataIndex: "callStatus",
      render: val => getLocale() === 'zh-CN' ? <Button className={statusStyle.tableBtn}>{formatMessage({ id: 'app.acd.calls.status.'+val })}</Button>:<Button className={statusStyle.tableBtn}>{val}</Button>
    },
  ];
  //指标
  renderPoint=()=>{
      const {
      status:{statusCurrent}
    } = this.props;
    return(
      <Row gutter={16}>
        <Col span={8}>
          <Card>
            <Statistic
              title={formatMessage({ id: 'app.acd.status.point.caller' })}
              value={statusCurrent.onlineCaller}
              valueStyle={{ color: '#3f8600' }}
              prefix={<Icon type="phone" />}
              suffix={formatMessage({id:'app.acd.status.point.people'})}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title={formatMessage({ id: 'app.acd.status.point.agent' })}
              value={statusCurrent.onlineAgent}
              valueStyle={{ color: '#1890FF' }}
              prefix={<Icon type="smile" />}
              suffix={formatMessage({id:'app.acd.status.point.people'})}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title={formatMessage({ id: 'app.acd.status.point.wait' })}
              value={statusCurrent.pendingCaller}
              valueStyle={{ color: '#f60' }}
              prefix={<Icon type="clock-circle" />}
              suffix={formatMessage({id:'app.acd.status.point.people'})}
            />
          </Card>
        </Col>
      </Row>
    );
  }
  //搜索
  renderSearch = () => {
    const {
      form: { getFieldDecorator },
      status:{callStatus}
    } = this.props;

    const dateFormat = "YYYY-MM-DD HH:mm:ss";
    let statusOptions;
    if (callStatus) {
      let include=['CONNECT','LINE','RING'];
      statusOptions = callStatus.filter((item)=>include.indexOf(item)!==-1).map(el => <Option key={el} value={el}>{formatMessage({ id: 'app.acd.calls.status.'+el })}</Option>)
    }
    return (
      <Form onSubmit={this.handleSearch} layout="inline">
      <ul className={styles.list}>
        <li>
         <FormItem label={formatMessage({ id: 'app.acd.calls.status' })}>
              {getFieldDecorator('callStatus',{})
              (<Select placeholder={formatMessage({ id: 'app.acd.calls.status.input' })} style={{ width: 295 }}>{statusOptions}</Select>)}
            </FormItem>
        </li>

        <li>
         <FormItem label={formatMessage({ id: 'app.acd.calls.time.start' })} >
              {getFieldDecorator('makeCallTime',{})
             (<DatePicker
             style={{ width: 295}}
              format={dateFormat}
              showTime
              onChange={(moment,dateString)=>{console.log(this.setState({makeCallTime:dateString===''?undefined:dateString}))}}
            />)}
            </FormItem>
        </li>
         <li>
         <FormItem label={formatMessage({ id: 'app.acd.calls.time.end' })}>
              {getFieldDecorator('hangUpTime',{})
             (<DatePicker 
               style={{ width: 295}}
              format={dateFormat}
              showTime
              onChange={(moment,dateString)=>{console.log(this.setState({hangUpTime:dateString===''?undefined:dateString}))}}
            />)}
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
      status: { data,callStatus },
      loading,
    } = this.props;
    return(
      <PageHeaderWrapper>
        <Card bordered={false}>
          <div style={{ background: '#ECECEC', padding: '25px' }}> {this.renderPoint()}</div>
          <div className={styles.tableListForm}>{this.renderSearch()}</div>
          <Table 
          dataSource={data.list} 
          columns={this.columns} 
          style={{ margin: '25px 0' }} 
          loading={loading}
          rowKey="callId"
          pagination={{
            ...data.pagination,
            showSizeChanger:true,
            onChange:this.onShowSizeChange
      }}/>
        </Card>
      </PageHeaderWrapper>
    );
  }
}
