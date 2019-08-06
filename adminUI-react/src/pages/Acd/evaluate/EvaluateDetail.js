import React, { PureComponent, Fragment, Component } from "react";
import moment from "moment";
import {
  Card,
  Modal,
  Rate
} from "antd";
import { formatMessage, getLocale } from 'umi/locale';
class EvaluateDetail extends PureComponent {

    cancelHandle = () => {
        const { handleModalEvaluateDetailVisible } = this.props;
        handleModalEvaluateDetailVisible();
    };

    render = () => {
        const {
        evaluateDetail:detail,
        modalEvaluateDetailVisible,
        modalTitle,
        loading
        } = this.props;
        return (
        <Modal
            destroyOnClose
            title={modalTitle}
            visible={modalEvaluateDetailVisible}
            onCancel={this.cancelHandle}
            footer={null}
            width={600}>
        
        <Card bordered={false} bodyStyle={{'padding-top':0,'padding-bottom':0}}>
            <p>{detail.desc===''|| detail.desc===null?formatMessage({id:'app.acd.evaluate.detail.null'}):detail.desc}</p>

            <ul style={{padding:0}}>
                <li style={{'margin-bottom':5}}>{formatMessage({id:'app.acd.evaluate.detail.createTime'})}：
                    {detail.createdTime && moment(detail.createdTime).format('YYYY-MM-DD HH:mm:ss')}
                </li>
                <li style={{'margin-bottom':5}}>{formatMessage({id:'app.acd.evaluate.detail.score'})}：
                    <Rate defaultValue={detail.scoreValue}  count={5}/> 
                </li>
                <li>{formatMessage({id:'app.acd.evaluate.result'})}：
                    {detail.resultType===1?formatMessage({id:'app.acd.evaluate.result.yes'}):formatMessage({id:'app.acd.evaluate.result.no'})}
                </li>
            </ul>
        </Card>
            
        </Modal>
        )
    }

}

export default EvaluateDetail;