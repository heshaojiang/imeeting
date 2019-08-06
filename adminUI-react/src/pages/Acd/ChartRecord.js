import React, {PureComponent} from "react";
import {Modal,List} from "antd";
/**
 * @Description: 文字聊天回显页面
 * @auther: hsjiang
 * @date: 2019/6/28/028
 * @version 1.0
 */
export default class ChartRecord extends PureComponent {
    state = {
        chatData: [],
    }
    constructor(props) {
        super(props);
    }

    componentDidMount() {//组件加载完成后执行
        this.props.onRef(this);//将当前this传给父组件，以便父组件能够调用子组件的方法

    }
    componentDidUpdate(){//当state发生变化时触发
    }
    //当点关闭时，隐藏弹窗
    handleCancel = e => {
        const {handleChatModalVisible} = this.props;
        handleChatModalVisible();
    };

    showContent(content) {//
        const jsonContent = JSON.parse(content);

        this.setState({
            chatData: jsonContent,
        });
    };
    render() {
        const {
            modalVisible, //弹窗显示控制变量
        } = this.props;

        const {
            chatData,
        } = this.state;
        return (
            <Modal
                visible={modalVisible}
                onCancel={this.handleCancel}
                width="560px"
                footer={null}

            >
                <List
                    itemLayout="horizontal"
                    dataSource={chatData}
                    renderItem={item => (
                        <List.Item>
                            <List.Item.Meta
                                title={item.name}
                                description={item.content}
                            />
                            <div>{item.time}</div>
                        </List.Item>
                    )}
                />
            </Modal>

        );
    }
}