import React, {PureComponent} from "react";
import {Modal} from "antd";

/**
 * @Description: 服务内容查看组件
 * @auther: hsjiang
 * @date: 2019/6/21/021
 * @version 1.0
 */
export default class Broadcast extends PureComponent {
    state = {
        videoUrl: "",
    }
    constructor(props) {
        super(props);
    }

    componentDidMount() {//组件加载完成后执行
        this.props.onRef(this);//将当前this传给父组件，以便父组件能够调用子组件的方法

    }
    componentDidUpdate(prevProps, prevState){//当state发生变化时触发
        if(prevState.videoUrl !== this.state.videoUrl) {
            this.reCreateVideo();
        }
    }
    //当点关闭时，隐藏弹窗
    handleCancel = e => {
        const {handleModalVisible} = this.props;
        handleModalVisible();
    };

    changeSrc(url) {//修改播放路径
        this.setState({
            videoUrl: url,
        });
    };
    /**
     * 重新生成video标签元素,在react中不能控制videojs，不能动态修改src属性
     * 所以才采取此方法
     * @param
     * @return
     * @author hsjiang
     * @date 2019/6/26/026
     **/
    reCreateVideo(){
        var videoDom = document.getElementById('videoDiv');
        if(!videoDom) return
        videoDom.innerHTML='<br/><video id="video" className=\'video-js\' controls preload=\'auto\' width=\'100%\'\n' +
            '                           controlslist=\'nodownload\'\n'+
            '                           style={{marginTop: \'30px\'}}>\n' +
            '                        <source src='+this.state.videoUrl+' type="video/mp4"></source>\n' +
            '                    </video>';
    }
    render() {
        const {
            modalVisible, //弹窗显示控制变量
        } = this.props;

        const {
            videoUrl,
        } = this.state;
        return (
            <Modal
                visible={modalVisible}
                onCancel={this.handleCancel}
                width="560px"
                footer={null}

            >
                <div id="videoDiv">
                    <br/>
                    <video id='video' className='video-js' controls preload='auto' width='100%' controlslist="nodownload">
                        <source src={videoUrl} type="video/mp4"></source>
                    </video>
                </div>
            </Modal>

        );
    }
}