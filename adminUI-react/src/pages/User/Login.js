import React, { Component } from 'react';
import { connect } from 'dva';
import { Checkbox, Alert, Icon } from 'antd';
import Login from '@/components/Login';
import styles from './Login.less';
import cryptoJs from 'crypto-js';
import { formatMessage } from 'umi/locale';

import { setAutoLogin, getAutoLogin } from '@/utils/authority';
const { Tab, UserName, Password, Mobile, Captcha, Submit } = Login;

export default
@connect(({ login, loading }) => ({
  login,
  submitting: loading.effects['login/login'],
}))
class LoginPage extends Component {
  state = {
    grant_type: 'password',
    autoLogin: getAutoLogin() !== 'false' ? true : false,
  };

  onTabChange = grant_type => {
    this.setState({ grant_type });
  };

  onGetCaptcha = () =>
    new Promise((resolve, reject) => {
      this.loginForm.validateFields(['mobile'], {}, (err, values) => {
        if (err) {
          reject(err);
        } else {
          const { dispatch } = this.props;
          dispatch({
            type: 'login/getCaptcha',
            payload: values.mobile,
          })
            .then(resolve)
            .catch(reject);
        }
      });
    });

  handleSubmit = (err, values) => {
    let key = 'grgimeeting12345';
    const { grant_type } = this.state;
    if (!err) {
      const { dispatch } = this.props;
      key  = cryptoJs.enc.Latin1.parse(key);
      let iv  = key;
      dispatch({
        type: 'login/login',
        payload: {
          ...values,
          password: cryptoJs.AES.encrypt(
            values.password, 
            key,
            {
                iv:iv,
                mode:cryptoJs.mode.CBC,
                padding:cryptoJs.pad.ZeroPadding
            }).toString(),
          grant_type,
          randomStr: Date.now(),
        },
      });
    }
  };

  changeAutoLogin = e => {
    setAutoLogin(e.target.checked)
    this.setState({
      autoLogin: e.target.checked,
    });
  };

  renderMessage = content => (
    <Alert style={{ marginBottom: 24 }} message={content} grant_type="error" showIcon />
  );

  render() {
    const { login, submitting } = this.props;
    const { grant_type, autoLogin } = this.state;
    return (
      <div className={styles.main}>
        <Login
          defaultActiveKey={grant_type}
          onTabChange={this.onTabChange}
          onSubmit={this.handleSubmit}
          ref={form => {
            this.loginForm = form;
          }}
        >
         {/* <tabs> */}
          {/* <tab key="password" tab={formatMessage({ id: 'app.admin.login.tips'})}> */}
            {login.status === 'error' &&
              !submitting &&
              this.renderMessage(login.msg)}
            <UserName name="username" placeholder={formatMessage({ id: 'app.admin.user.username'})} />
            <Password
              name="password" placeholder={formatMessage({ id: 'app.admin.user.password'})}
              onPressEnter={() => this.loginForm.validateFields(this.handleSubmit)}
            />
          {/* </tab> */}
          {/* </tabs> */}
          {/* <Tab key="mobile" tab="手机号登录">
            {login.status === 'error' &&
              login.grant_type === 'mobile' &&
              !submitting &&
              this.renderMessage('验证码错误')}
            <Mobile name="mobile" />
            <Captcha name="captcha" countDown={120} onGetCaptcha={this.onGetCaptcha} />
          </Tab>*/}
          {/* <div>
            <Checkbox checked={autoLogin} onChange={this.changeAutoLogin}>
              自动登录
            </Checkbox>
            <a style={{ float: 'right' }} href="">
              忘记密码
            </a>
          </div>  */}
          <Submit loading={submitting}>{formatMessage({ id: 'app.admin.login'})}</Submit>
          {/* <div className={styles.other}>
            其他登录方式
            <Icon className={styles.icon} type="alipay-circle" />
            <Icon className={styles.icon} type="taobao-circle" />
            <Icon className={styles.icon} type="weibo-circle" />
            <Link className={styles.register} to="/User/Register">
              注册账户
            </Link>
          </div> */}
        </Login>
      </div>
    );
  }
}
