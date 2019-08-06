import React, {Fragment, PureComponent} from "react";
import {connect} from "dva";
import {Button, Card, Col, Form, Input, Modal, Row, Select,Table} from "antd";
import moment from "moment";
import PageHeaderWrapper from "@/components/PageHeaderWrapper";
import StandardTable from "@/components/StandardTable";
import styles from "./tableList.less";
import statusStyle from './Status.less'
import {getFile} from "@/services/acdCalls";
import {formatMessage, getLocale} from 'umi/locale';
import BroadcastForm from './Broadcast'
import ChartRecord from "./ChartRecord";

const Option = Select.Option;
const FormItem = Form.Item;


@connect(({calls, callStatus, loading}) => ({
    calls,
    callStatus,
    loading: loading.models.calls,
}))
@Form.create()
export default class Calls extends PureComponent {
    state = {
        modalStatus: "",
        modalVisible: false,
        chatModalVisible: false,
        uploadModalVisible: false,
        modalTitle: "",
        callStatus: [],
        modalData: {},
        modalOk: () => {
        },
        callId: "",
        formValues:{}
    };

    columns = [
        {
            title: formatMessage({id: 'app.admin.order'}),
            dataIndex: "callId",
            render: (text, record, index) => index + 1
        },
        {
            title: formatMessage({id: 'app.acd.calls.callerName'}),
            dataIndex: "callerName"
        },
        {
            title: formatMessage({id: 'app.acd.calls.agentName'}),
            dataIndex: "agentName"
        },
        {
            title: formatMessage({id: 'app.acd.calls.makecallTime'}),
            dataIndex: "makecallTime",
            render: val => val && moment(val).format('YYYY-MM-DD HH:mm:ss')
        },
        {
            title: formatMessage({id: 'app.acd.calls.hangupTime'}),
            dataIndex: "hangupTime",
            render: val => val && moment(val).format('YYYY-MM-DD HH:mm:ss')
        },
        {
            title: formatMessage({id: 'app.acd.calls.audio'}),
            dataIndex: "recorderAudioFilepath",
            width: '50px',
            render: (text, record) => {
                if (text != null && text != '') {
                    var url = "/acd/calls/getFile?type=audio&callId=" + record.callId;
                    return (
                        <audio src={url} controls="controls" loop="loop"
                               controlsList="nodownload" oncontextmenu="return false"></audio>
                    );
                }
            }
        },
        {
            title: formatMessage({id: 'app.acd.calls.video'}),
            dataIndex: "recorderVideoFilepath",
            render: (text, record) => {
                if (text != null && text != '') {
                    var url = "/acd/calls/getFile?type=video&callId=" + record.callId;
                    return (
                        <Fragment>
                            <a onClick={() => this.handleBroadcast(url)}>{formatMessage({id: 'app.acd.calls.broadcast'})}</a>
                        </Fragment>
                    );
                }

            }
        },
        {
            title: formatMessage({id: 'app.acd.calls.text'}),
            dataIndex: "recorderChat",
            render: (text, record) => {
                if (text != null && text != '') {
                    return (
                        <Fragment>
                            <a onClick={() => this.handleChatLook(text)}>{formatMessage({id: 'app.acd.calls.view'})}</a>
                        </Fragment>
                    );
                }

            }
        },
        {
            title: formatMessage({id: 'app.acd.calls.queueName'}),
            dataIndex: "queueName"
        },
        {
            title: formatMessage({id: 'app.acd.calls.status'}),
            dataIndex: "callStatus",
            render: val => getLocale() === 'zh-CN' ?
                <Button className={statusStyle.tableBtn}>{formatMessage({id: 'app.acd.calls.status.' + val})}</Button> :
                <Button className={statusStyle.tableBtn}>{val}</Button>
        }
    ];


    componentDidMount() {
        const {dispatch,location:{state}} = this.props;
        const params={};
        if(state && state.callId){
           params.callId=state.callId
        }
        dispatch({
            type: "calls/fetch",
            payload: {...params}
        });
        dispatch({
            type: "calls/fetchCallStatus",
            payload: {}
        });

    }


    handleStandardTableChange = (pagination, filtersArg, sorter) => {
        const {dispatch} = this.props;
        const {formValues} = this.state;

        const filters = Object.keys(filtersArg).reduce((obj, key) => {
            const newObj = {...obj};
            newObj[key] = getValue(filtersArg[key]);
            return newObj;
        }, {});

        const params = {
            page: pagination.current,
            limit: pagination.pageSize,
            ...formValues,
            ...filters
        };

        if (sorter.field) {
            params.sorter = `${sorter.field}_${sorter.order}`;
        }

        dispatch({
            type: "calls/fetch",
            payload: params
        });
    };

    handleModalVisible = flag => {
        this.setState({
            modalVisible: !!flag
        });
    };
    handleChatModalVisible = flag => {
        this.setState({
            chatModalVisible: !!flag
        });
    };

    handleSearch = e => {
        e.preventDefault();

        const {dispatch, form} = this.props;
        
        form.validateFields((err, fieldsValue) => {
            if (err) return;
            const values = {
                ...fieldsValue,
            };
            this.setState({
                formValues: values,
            });
            dispatch({
                type: "calls/fetch",
                payload: values
            });
        });
    };

    handleFormReset = () => {
        const {dispatch, form} = this.props;
        this.setState({
            formValues: {},
        });
        form.resetFields();
        dispatch({
            type: "calls/fetch",
            payload: {}
        });
    };

    handleExport = () => {
        console.log("daochu")
    }

    //视频播放
    handleBroadcast = (url) => {
        this.child.changeSrc(url);
        this.handleModalVisible(true);

    }

    //获取播放组件，以便操作子组件
    onBroadcastRef = (ref) => {
        this.child = ref
    }

    //显示文字聊天
    handleChatLook = (chatContent) => {
        this.childChat.showContent(chatContent);
        this.handleChatModalVisible(true);

    }
    //获取聊天显示组件，以便操作子组件
    onChatRef = (ref) => {
        this.childChat = ref
    }

    renderForm = () => {
        const {
            form: {getFieldDecorator}
        } = this.props;
        return (
            <Form onSubmit={this.handleSearch} layout="inline">
                <Row gutter={{md: 8, lg: 24, xl: 48}}>
                    <Col md={8} sm={24}>
                        <FormItem label={formatMessage({id: 'app.acd.calls.search'})}>
                            {getFieldDecorator("keyword", {
                                rules: [
                                    {
                                        pattern: new RegExp('^[\u4e00-\u9fa5a-z0-9]+$', 'g'),
                                        message: formatMessage({id: 'app.admin.search.keyword'}),
                                    }
                                ]
                            })(
                                <Input placeholder={formatMessage({id: 'app.admin.keyword'})}/>
                            )}
                        </FormItem>
                    </Col>
                    <Col md={8} sm={24}>
            <span className={styles.submitButtons}>
              <Button type="primary" htmlType="submit">
                {formatMessage({id: 'app.admin.search'})}
              </Button>
              <Button style={{marginLeft: 8}} onClick={this.handleFormReset}>
                {formatMessage({id: 'app.admin.reset'})}
              </Button>
            </span>
                    </Col>
                </Row>
            </Form>
        );
    };

    render = () => {
        const {
            calls: {data},
            loading,
        } = this.props;

        const {
            selectedRows,
            modalVisible,
            chatModalVisible
        } = this.state;
        const parentMethods = {
            handleModalVisible: this.handleModalVisible,
            handleChatModalVisible: this.handleChatModalVisible,
        };

        return (
            <PageHeaderWrapper>
                <Card bordered={false}>
                    <div className={styles.tableList}>
                        <div className={styles.tableListForm}>{this.renderForm()}</div>
                        <Table dataSource={data.list} 
                            columns={this.columns} 
                            loading={loading}
                            rowKey="id"
                            onChange={this.handleStandardTableChange}
                            pagination={{
                            ...data.pagination,
                            showSizeChanger:true,
                            }}/>
                    </div>
                </Card>
                <BroadcastForm handleModalVisible={parentMethods.handleModalVisible} modalVisible={modalVisible}
                               onRef={this.onBroadcastRef}/>
                <ChartRecord handleChatModalVisible={parentMethods.handleChatModalVisible}
                             modalVisible={chatModalVisible} onRef={this.onChatRef}/>
            </PageHeaderWrapper>

        );
    };

}