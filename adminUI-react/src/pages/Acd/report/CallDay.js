import React from "react";
import ReactDOM from 'react-dom';
import { connect } from "dva";
import statusStyle from '../Status.less'
import {
  Card, Divider, Modal, Button, Form, Input, TreeSelect, Select, Row, Col, Icon, Pagination , Message,DatePicker,Tabs,Statistic,Table 
} from "antd";
import { formatMessage, getLocale } from 'umi/locale';
import {
  G2,
  Chart,
  Geom,
  Axis,
  Tooltip,
  Coord,
  Label,
  Legend
} from "bizcharts";
import DataSet from "@antv/data-set";
const { MonthPicker, RangePicker } = DatePicker;
const FormItem = Form.Item;
const Option = Select.Option;

@connect(({ status, loading }) => ({
  status,
  loading: loading.models.status,
}))
class CallDay extends React.Component {

    constructor() {
    super();
    this.state = {
      type:'day',
      fromDate:undefined,
      toDate:undefined
    }
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: "status/fetchCallStatus",
      payload: {}
    });
  }

  onShowSizeChange =(page, pageSize)=>{
    const { dispatch } = this.props;
    const params = {
      page: page,
      limit: pageSize,
      fromDate:this.state.fromDate,
      toDate:this.state.toDate
    };
    dispatch({
      type: "status/fetchCallStatus",
      payload: {...params}
    });
  }

  onDateChange=(date,dateString)=>{
    const { dispatch } = this.props;
    const params={
      fromDate:dateString[0]===''?undefined:dateString[0],
      toDate:dateString[1]===''?undefined:dateString[1],
      page:dateString[0]===''&dateString[1]===''?1:undefined
    }
    this.setState({
      ...params
    });
    dispatch({
      type: "status/fetchCallStatus",
      payload: {...params}
    });
  }

 render() {
    const pageSizeOptions=['7','10','14','20','30'];
    const {
      status: { statusDay:{list:data, field: field,pagination:pagination} },
      loading,
    } = this.props;

    let dataSource=data.map((currentValue, index, array)=>{
      return {...currentValue,name:formatMessage({ id: 'app.acd.report.'+currentValue['name']})}
    });

    const ds = new DataSet();
    const dv = ds.createView().source(dataSource);
    dv.transform({
      type: "fold",
      fields: field,
      // 展开字段集
      key: "日期",
      // key字段
      value: "数量" // value字段
    });

    return(
        <div>
          <div>
            <ul style={{display:'flex'}} className={statusStyle.statisticsSearch}>
              <li><Pagination  defaultCurrent={1} {...pagination} 
              onChange={this.onShowSizeChange} 
              showSizeChanger 
              onShowSizeChange={this.onShowSizeChange}
              pageSizeOptions={pageSizeOptions}/></li>
              <li><RangePicker onChange={this.onDateChange}/></li>
            </ul>
            <Divider></Divider>
          </div>
  

        <Chart height={400} data={dv} forceFit>
          <Axis name="日期" />
          <Axis name="数量" />
          <Legend />
          <Tooltip
            crosshairs={{
              type: "y"
            }}
          />
          <Geom
            type="interval"
            position="日期*数量"
            color={"name"}
            adjust={[
              {
                type: "dodge",
                marginRatio: 1 / 32
              }
            ]}
          />
        </Chart>
        </div>
    );
  }
}

export default CallDay;