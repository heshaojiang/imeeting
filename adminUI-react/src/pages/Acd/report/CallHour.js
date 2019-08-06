import React from "react";
import ReactDOM from 'react-dom';
import { connect } from "dva";
import { formatMessage } from 'umi/locale';
import {DatePicker} from "antd";
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

@connect(({ reportform }) => ({
  reportform:reportform.stackdate,
}))
class CallHour extends React.Component {

    constructor() {
        super();
        this.state = {
        date:undefined,
        }
    }

    //初始化调用接口
    componentDidMount() {
      const { dispatch} = this.props;
      dispatch({
        type: "reportform/fetchSessionTotal",
        payload: {}
      });
    }

    onDateChange=(moment,dateString)=>{
    const { dispatch } = this.props;
    const params={
      date:dateString===''?undefined:dateString,
    }
    this.setState({
      ...params
    });
    dispatch({
      type: "reportform/fetchSessionTotal",
      payload: {...params}
    });
  }
  
  render() {
    const {reportform:data}=this.props
    let dataSource=data.map((currentValue, index, array)=>{
      return {...currentValue,name:formatMessage({ id: 'app.acd.report.'+currentValue['name']})}
    });
 
    const ds = new DataSet();
    const dv = ds.createView().source(dataSource);
    dv.transform({
      type: "fold",
      fields: ["0:00","1:00", "2:00", "3:00",  "4:00","5:00","6:00", "7:00","8:00","9:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"],
      // 展开字段集
      key: "时间",
      // key字段
      value: "数量", // value字段
    });
    return (
      <div>
        <div>
              <DatePicker style={{width:'295px'}} onChange={this.onDateChange}/>
        </div>

        <Chart height={400} data={dv} forceFit>
          <Legend />
          <Axis name="时间" />
          <Axis name="数量" />
          <Tooltip />
          <Geom
            type="intervalStack"
            position="时间*数量"
            color={"name"}
            style={{
              stroke: "#fff",
              lineWidth: 1
            }}
          />
        </Chart>
      </div>
    );
  }
}

export default CallHour;
