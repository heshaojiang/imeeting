import React, { PureComponent} from "react";
import ChildCake from './ChildCake' ;
import {
  Card, Row, Col
} from "antd";
import { formatMessage, getLocale } from 'umi/locale';
import PageHeaderWrapper from "@/components/PageHeaderWrapper";

class EvaluateReport extends PureComponent {
   render = () => {
        const {CallPlatform,ResultSolve,Satisfly}=this.props;
        console.log(this.props);

       return(
        <div style={{ background: '#ECECEC', padding: '15px'}}>
            <Row gutter={16} >
                <Col span={8}>
                    <Card title={formatMessage({ id: 'app.acd.evaluate.customerfrom' })} bordered={false}>
                    <ChildCake data={CallPlatform} locale={true}/>
                    </Card>
                </Col>
                <Col span={8}>
                    <Card title={formatMessage({ id: 'app.acd.evaluate.questionpercent' })} bordered={false}>
                    <ChildCake data={ResultSolve} locale={true}/>
                    </Card>
                </Col>
                <Col span={8}>
                    <Card title={formatMessage({ id: 'app.acd.evaluate.satisfaction' })} bordered={false}>
                    <ChildCake data={Satisfly} locale={false}/>
                    </Card>
                </Col>
            </Row>
        </div>

       )
   }
}

export default EvaluateReport;