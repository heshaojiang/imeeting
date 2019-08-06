import React, { PureComponent} from "react";
import { connect } from "dva";
import {
  Card, Button, Form, Select, Row, Col,Tabs 
} from "antd";
import DataSet from "@antv/data-set";
import PageHeaderWrapper from "@/components/PageHeaderWrapper";
import { formatMessage, getLocale } from 'umi/locale';
import ChildCake from './ChildCake' ;
import AgentReport from './CustomerMembers';
import EvaluateReport from './EvaluateReport';
import CallReport from './CallReport'
const Option = Select.Option;
const FormItem = Form.Item;
const { TabPane } = Tabs;

@connect(({ reportform, loading }) => ({
  reportform,
  loading: loading.models.reportform,
}))
export default class ReportForm extends PureComponent {

    //初始化调用接口
    componentDidMount() {
      const { dispatch} = this.props;
      // dispatch({
      //   type: "reportform/fetchSessionTotal",
      //   payload: {}
      // });
      dispatch({
        type: "reportform/fetchCallPlatform",
        payload: {}
      });
      dispatch({
        type: "reportform/fetchResultSolve",
        payload: {}
      });
      dispatch({
        type: "reportform/fetchSatisfly",
        payload: {}
      });
      dispatch({
        type: "reportform/fetchAgentInfo",
        payload: {}
      });
  }


   onPageChange =(page, pageSize)=>{
    const { dispatch } = this.props;
    const { formValues } = this.state;
    const params = {
      page: page,
      limit: pageSize,
      ...formValues
    };

    dispatch({
      type: "reportform/fetchAgentInfo",
      payload: {...params}
    });
  }

   onPageSizeChange =(current, size)=>{
    const { dispatch } = this.props;
    const { formValues } = this.state;
    const params = {
      page: 1,
      limit: size,
      ...formValues
    };

    dispatch({
      type: "reportform/fetchAgentInfo",
      payload: {...params}
    });
  }

  render = () => {
  const {reportform:{CallPlatform, ResultSolve, Satisfly,stackdate,agentdata,loading}} = this.props; 

  const evaluateData={CallPlatform, ResultSolve, Satisfly}
  // const callData={stackdate}

  const agentMehod={
    loading,
    onPageChange:this.onPageChange,
    onPageSizeChange:this.onPageSizeChange
  }
    return (
        <PageHeaderWrapper>
        <Card bordered={false}>
          <Tabs defaultActiveKey="1" >
            <TabPane tab={formatMessage({ id: 'app.acd.report.callData' })} key="1">
              <CallReport />
            </TabPane>
             <TabPane tab={formatMessage({ id: 'app.acd.report.evaluateData' })} key="2">
               <EvaluateReport {...evaluateData}/>
            </TabPane>
            <TabPane tab={formatMessage({ id: 'app.acd.report.agentData' })} key="3">
               <AgentReport data={agentdata} {...agentMehod}/>
            </TabPane>
          </Tabs>
        </Card>
      </PageHeaderWrapper>
    );
  };
}
