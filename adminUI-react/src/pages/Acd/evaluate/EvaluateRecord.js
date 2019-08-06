import React, { PureComponent, Fragment, Component } from "react";
import { connect } from "dva";
import {
  Card,  Modal, Button, Form, Input, Select, Row, Col, Icon, Upload, Message,Tabs,
  Table, TimePicker , Tag 
} from "antd";
import moment from "moment";
import PageHeaderWrapper from "@/components/PageHeaderWrapper";
import StandardTable from "@/components/StandardTable";
import styles from ".././tableList.less";
import { validateField, validateAcdField } from "@/services/api";
import EvaluateSetting from "./EvaluateSetting";
import EvaluateDetail from "./EvaluateDetail";
import { formatMessage, getLocale } from 'umi/locale';

const FormItem = Form.Item;
const { Option } = Select;
const { TabPane } = Tabs;

@connect(({ evaluation, loading ,user,queue}) => ({
 evaluation,
 loading: loading.models.evaluation,
 queueList:queue.list,
 allType: user.allType
}))
@Form.create()
export default class EvaluateRecord extends PureComponent {
  state = {
    selectedRows: [],
    modalStatus: "",
    modalVisible: false,
    modalEvaluateDetailVisible: false,
    evaluateDetail:{},
    modalTitle: "",
    modalData: {},
    modalOk: () => { },
    columns: [],
    formValues: {}
  };

   

  columns = [
      {
        title: formatMessage({ id: 'app.admin.order' }),
        dataIndex: 'id',
        render: (text, record, index) => index + 1
      },
      {
        title: formatMessage({ id: 'app.acd.evaluate.caller' }),
        dataIndex: "callerName",
      },
      {
        title: formatMessage({ id: 'app.acd.evaluate.scene' }),
        dataIndex: "scene",
      },
      {
        title: formatMessage({ id: 'app.acd.evaluate.agent' }),
        dataIndex: "agentName",
      },
      {
        title: formatMessage({ id: 'app.acd.evaluate.starttime' }),
        dataIndex: "startTime",
        render: val => val && moment(val).format('YYYY-MM-DD HH:mm:ss') 
      },
      {
        title: formatMessage({ id: 'app.acd.evaluate.endtime' }),
        dataIndex: "endTime",
        render: val => val && moment(val).format('YYYY-MM-DD HH:mm:ss')
      },
      {
        title: formatMessage({ id: 'app.acd.evaluate.result' }),
        dataIndex: "resultType",
        render:val=>val===1?formatMessage({id:'app.acd.evaluate.result.yes'}):formatMessage({id:'app.acd.evaluate.result.no'})
      },
      {
        title: formatMessage({ id: 'app.acd.evaluate.scoreText' }),
        dataIndex: "scoreText",
      },
      {
        title: formatMessage({ id: 'app.acd.evaluate.desc' }),
        dataIndex: "desc",
        render: (text, record) => (
          <Fragment>
            <a onClick={() => this.handleEvaluateDetail(record)}>{formatMessage({ id: 'app.acd.evaluate.detail.show' })}</a>
          </Fragment>
        )
      },{
        title: formatMessage({ id: 'app.admin.operate' }),
        render: (text, record) => (
          <Fragment>
            <a onClick={() => this.handleShowCall(record.callId)}>{formatMessage({ id: 'app.acd.evaluate.call.show' })}</a>
          </Fragment>
        )
      }
  ];
    
  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: "evaluation/fetch",
      payload: {}
    });

    dispatch({
      type: "evaluation/fetchScore",
      payload: {}
    });
     dispatch({
      type: "queue/fetchList",
      payload: {}
    });
  }

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
        type: "evaluation/fetch",
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
      type: "evaluation/fetch",
      payload: {}
    });
  };
  
  handleTableChange = (pagination, filtersArg, sorter) => {
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
      ...filters,
      ...formValues
    };

    if (sorter.field) {
      params.sorter = `${sorter.field}_${sorter.order}`;
    }
    dispatch({
      type: "evaluation/fetch",
      payload: {
        ...params
      }
    });
  };

  //查看评论详情
  handleEvaluateDetail = detail => {
    const { dispatch } = this.props;
    this.handleModalDetailVisible(true);
    this.setState({
      evaluateDetail:detail,
      modalTitle: formatMessage({ id: 'app.acd.evaluate.desc' }),
      });
  }

  handleModalDetailVisible = flag => {
  this.setState({
    modalEvaluateDetailVisible: !!flag
  })
};

  //查看服务记录
  handleShowCall=(callId)=>{
    this.props.history.push({ pathname:'/acd/calls',state:{callId : callId,test:'22' } })
  }

  //搜索
  renderSearch = () => {
    const {
      form: { getFieldDecorator },
      queueList,
    } = this.props;
    
    let queueOptions;
    if(queueList){
      queueOptions=this.props.queueList.map(el => <Option key={el.queueId} value={el.queueId}>{el.description}</Option>)
    }

    return (
      <Form onSubmit={this.handleSearch} layout="inline">
      <ul className={styles.list}>
        <li>
          <FormItem>
              {getFieldDecorator('sort',{})
              (<Select placeholder={formatMessage({ id: 'app.acd.evaluate.date.sort' })} style={{ width: 295 }}>
                 <Option value="true">{formatMessage({ id: 'app.acd.evaluate.date.sort.asc' })}</Option>
                 <Option value="false">{formatMessage({ id: 'app.acd.evaluate.date.sort.desc' })}</Option>
              </Select>)}
            </FormItem>
        </li>
         <li>
          <FormItem>
              {getFieldDecorator('queueId',{})
              (<Select placeholder={formatMessage({ id: 'app.acd.evaluate.scene.input' })} style={{ width: 295 }}>
                 {queueOptions}
              </Select>)}
            </FormItem>
        </li>
        <li>
          <FormItem>
            {getFieldDecorator('agentName', {
              rules: [
                {
                  pattern: new RegExp('^[\u4e00-\u9fa5\\w]+$', 'g'),
                  message: formatMessage({ id: 'app.acd.agent.format' }),
                }
              ]
            })(<Input placeholder={formatMessage({ id: 'app.acd.agent.input' })}  style={{ width: 295}}/>)}
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
    const { dispatch,evaluation:{data,score},loading,allType } = this.props;
    const parentProps = {
      dispatch,
      score,
      loading,
      modalType:allType
    };
    const {modalEvaluateDetailVisible,evaluateDetail,modalTitle}=this.state;
    const DetailProps={
      modalEvaluateDetailVisible,
      evaluateDetail,
      modalTitle
    }
    const DetailMethods={
      handleModalEvaluateDetailVisible:this.handleModalDetailVisible
    }

    return (
    <PageHeaderWrapper>
        <div>
            <Tabs defaultActiveKey="1" >
                <TabPane tab={formatMessage({ id:'app.acd.evaluate.record' })} key="1">
                <div className={styles.tableList}>
                <div className={styles.tableListForm} style={{'padding-left':'25px'}}>{this.renderSearch()}</div>
                  <Table dataSource={data.list} 
                        columns={this.columns} 
                        style={{ margin: '0 25px'}} 
                        loading={loading}
                        rowKey="id"
                        onChange={this.handleTableChange}
                        pagination={{
                          ...data.pagination,
                          showSizeChanger:true,
                          }}/>
                  </div>
                </TabPane>
                <TabPane tab={formatMessage({ id:'app.acd.evaluate.setting' })} key="3">
                    <EvaluateSetting {...parentProps}/>
                </TabPane>
        </Tabs>
    </div>
    <EvaluateDetail {...DetailProps} {...DetailMethods}/>
    </PageHeaderWrapper>
    );
  };
}
