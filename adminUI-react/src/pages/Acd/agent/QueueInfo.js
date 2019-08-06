import React, { PureComponent } from "react";
import {
  Modal,
  Form,
  Select,
  Row,
  Col,
  Table,
} from "antd";
import styles from "../tableList.less";
import { formatMessage, getLocale } from 'umi/locale';
import {isEmptyObject} from "@/utils/utils"

class QueueInfo extends PureComponent {
  cancelHandle = () => {
    const { handleModalQueueVisible } = this.props;
    handleModalQueueVisible();
  };

  render = () => {
    const columns = [
      {
        title: formatMessage({ id: 'app.admin.order' }),
        dataIndex: "queueId",
        render: (text, record, index) => index + 1
      },
      {
        title: formatMessage({ id: 'app.acd.queue.queueName' }),
        dataIndex: "queueName"
      },
      {
        title: formatMessage({ id: 'app.acd.queue.description' }),
        dataIndex: "description"
      },
      {
        title: formatMessage({ id: 'app.acd.queue.strategy' }),
        dataIndex: "strategy",
        render: (val) => this.props.queueStrategy.find(el => el.type === val).label
      },
      {
        title: formatMessage({ id: 'app.acd.queue.capacity' }),
        dataIndex: "maxNum"
      }
    ];

    const {
      queue,
      modalQueueVisible,
      modalTitle,
      loading
    } = this.props;

    let data=[];
    if(!isEmptyObject(this.props.queue)){
      data=queue;
    }
    
    return (
      <Modal
        destroyOnClose
        title={modalTitle}
        visible={modalQueueVisible}
        onCancel={this.cancelHandle}
        footer={null}
        width={600}>
        <div className={styles.tableList}>
          <Table 
          loading={loading}
          columns={columns} 
          dataSource={data} 
          pagination={false}
          rowKey="queueId"/>
        </div>
      </Modal>
    )
  }
}

export default QueueInfo;