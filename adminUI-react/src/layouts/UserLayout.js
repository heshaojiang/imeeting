import React, { Fragment } from 'react';
import { Icon, Button } from 'antd';
import GlobalFooter from '@/components/GlobalFooter';
import styles from './UserLayout.less';
import favicon from '../assets/favicon.jpg';
import { formatMessage, FormattedMessage, setLocale, getLocale } from 'umi/locale';

const links = [
  {
    key: 'help',
    title: '帮助',
    href: '',
  },
  {
    key: 'privacy',
    title: '隐私',
    href: '',
  },
  {
    key: 'terms',
    title: '条款',
    href: '',
  },
];

const copyright = (
  <Fragment>
    <span className={styles.copyright}> Copyright <Icon type="copyright"/> {formatMessage({ id: 'app.admin.copyright'})}</span> 
  </Fragment>
);

class UserLayout extends React.PureComponent {
  // @TODO title
  // getPageTitle() {
  //   const { routerData, location } = this.props;
  //   const { pathname } = location;
  //   let title = 'Ant Design Pro';
  //   if (routerData[pathname] && routerData[pathname].name) {
  //     title = `${routerData[pathname].name} - Ant Design Pro`;
  //   }
  //   return title;
  // }

  changLang = () => {
    const locale = getLocale();
    if (!locale || locale === 'zh-CN') {
      setLocale('en-US');
    } else {
      setLocale('zh-CN');
    }
  };

  render() {
    const { children } = this.props;

    return (
      // @TODO <DocumentTitle title={this.getPageTitle()}>
      <div className={styles.container}>
        <div className={styles.lang}>
        <Button
          size="small"
          style={{
            margin: '0 8px',
          }}
          onClick={() => {
            this.changLang();
          }}
        >
          <FormattedMessage id="navbar.lang" />
        </Button>
        </div>
        <div className={styles.content}>
          <div className={styles.top}>
            <div className={styles.header}>
              {/* <Link to="/"> */}
                <img alt="logo" className={styles.logo} src={favicon} />
                <span className={styles.title}>{formatMessage({ id: 'app.admin.login.title'})}</span>
              {/* </Link> */}
            </div>
            <div className={styles.desc}></div>
          </div>
          {children}
        </div>
        {/* <GlobalFooter links={links} copyright={copyright} /> */}
        <GlobalFooter copyright={copyright} />
      </div>
    );
  }
}

export default UserLayout;
