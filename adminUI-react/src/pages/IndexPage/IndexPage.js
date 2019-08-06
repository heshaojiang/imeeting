import React  from 'react';
import { title, indexPageBody } from './index.css';
import { Card } from 'antd';
import { formatMessage } from 'umi/locale';
import Redirect from 'umi/redirect';
import Authorized,{ isAdminUser } from '@/utils/Authorized';
export default () => (
    <Card bordered={false} style={{height:700}}>
        <div className={indexPageBody}>
            <h1 className={title}>{formatMessage({ id: 'app.admin.homepage' })}</h1>
        </div>
      <Authorized authority="imeeting_user">
        <Redirect to="/account/settings/base" />
      </Authorized>
    </Card>

);