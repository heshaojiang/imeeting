import React, { PureComponent } from 'react';
import { Card, Upload, Row, Col, Button, Icon, message, Table } from 'antd';
import { formatMessage } from 'umi/locale';
import { getToken } from '@/utils/authority';
import { getMachineCode } from '@/services/license';
import { getLicenseInfo } from '@/services/license';
export default class License extends PureComponent {
  constructor(props) {
    super(props);


    this.state = {
      machineCode: [],      licenseInfo: [],

    };
  }
  reloadLicneseInfo() {
    getLicenseInfo().then(v => {
      this.setState({
        licenseInfo: v
      });
    });
  };
  componentDidMount() {
    getMachineCode().then(v => {
      this.setState({
        machineCode: v
      });
    });
    this.reloadLicneseInfo();
  }
  render() {
    const { machineCode } = this.state;
    const { licenseInfo } = this.state;
    const $this=this
    const props = {
      name: 'file',
      action: '/admin/license/uploadlicense',
      headers: {
        Authorization: 'Bearer ' + getToken(),
      },
      onChange(info) {
        if (info.file.status !== 'uploading') {
          console.log(info.file, info.fileList);
        }
        if (info.file.status === 'done') {
          const resp = info.file.response;
          if (resp.success) {
            message.success(formatMessage({ id: 'app.admin.license.uploadsucc'},{file: info.file.name}));
            $this.reloadLicneseInfo();
          } else {
            message.error(formatMessage({ id: 'app.admin.license.uploadfail'},{msg: resp.msg}));
          }
        } else if (info.file.status === 'error') {
          message.error(`${info.file.name} file upload failed.`);
        }
      },
    };

    const columns = [{
      title: 'name',
      dataIndex: 'name',
      key: 'name',
    }, {
      title: 'value',
      dataIndex: 'value',
      key: 'value',
    }];
    // const dataMachineCode = [{name:"Server Machine Code",value:'${machineCode}'}];
    const dataMachineCode = [];
    const dataCode = [];
    dataCode.name=formatMessage({ id: 'app.admin.license.machinecode'});
    dataCode.value=machineCode;
    dataMachineCode.push(dataCode);
    const dataLicenseInfo = [];
    console.log(`machineCode ${machineCode}`);
    if (licenseInfo!=null) {
      Object.getOwnPropertyNames(licenseInfo).forEach(function(key){

        console.log("licenseInfo:"+key,licenseInfo[key]);
        const data = [];
        data.name=key;
        data.value=licenseInfo[key];
        dataLicenseInfo.push(data);
      });
    }


    return (

      <Card>

         <Card title={formatMessage({ id: 'app.admin.license.machinecode.info'})}>

            <Table dataSource={dataMachineCode} columns={columns} showHeader={false} pagination={false}/>
         </Card>


        <br />

          <Card title={formatMessage({ id: 'app.admin.license.uploadfile.info'})}>
            <Upload showUploadList={false} accept=".lic" {...props}>
              <Button>
                <Icon type="upload" /> {formatMessage({ id: 'app.admin.license.uploadfile'})}
              </Button>
            </Upload>,
          </Card>

        <br />
        <Card title={formatMessage({ id: 'app.admin.license.licenseinfo'})}>

          <Table dataSource={dataLicenseInfo} columns={columns} showHeader={false} pagination={false} size={"small"}/>
        </Card>

      </Card>
    )

  }
}



