import React from "react";
import {
  G2,
  Chart,
  Geom,
  Axis,
  Tooltip,
  Coord,
  Label,
  Legend,
  View,
  Guide,
  Shape,
  Facet,
  Util
} from "bizcharts";
import { Empty } from 'antd';
import { formatMessage, getLocale } from 'umi/locale';
import DataSet from "@antv/data-set";

class ChildCake extends React.Component {
  render() {
    const { DataView } = DataSet; 
    const {data,locale}=this.props;

    let dataSource=locale?data.map((currentValue, index, array)=>{
      return {...currentValue,item:formatMessage({ id: 'app.acd.report.'+currentValue['item']})}
    }):data;
    const dv = new DataView();
    dv.source(dataSource).transform({
      type: "percent",
      field: "count",
      dimension: "item",
      as: "percent"
    });
    const cols = {
      percent: {
        formatter: val => {
          val = (val * 100).toFixed(2) + "%";
          return val;
        }
      }
    };
  
    return (
      data.reduce((sum,item)=>(sum+item.count),0)>0?(
      <div>
        <Chart
          height={window.innerHeight/2}
          data={dv}
          scale={cols}
          padding={[30, 40, 40, 30]}
          forceFit
        >
          <Coord type="theta" radius={0.75} />
          <Axis name="percent" />
          <Legend
            position="right"
            offsetY={-window.innerHeight / 2 + 200}
            offsetX={-40}
          />
          <Tooltip
            showTitle={false}
            itemTpl="<li><span style=&quot;background-color:{color};&quot; class=&quot;g2-tooltip-marker&quot;></span>{name}: {value}</li>"
          />
          <Geom
            type="intervalStack"
            position="percent"
            color="item"
            tooltip={[
              "item*percent",
              (item, percent) => {
                percent = (percent * 100).toFixed(2) + "%";
                return {
                  name: item,
                  value: percent
                };
              }
            ]}
            style={{
              lineWidth: 1,
              stroke: "#fff"
            }}
          >
            <Label
              content="percent"
              formatter={(val, item) => {
                return item.point.item + ": " + val;
              }}
            />
          </Geom>
        </Chart>
      </div>
      ):( <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} style={{
        height:window.innerHeight/2-65
      }}/>)
    );
  }
}


export default ChildCake;

