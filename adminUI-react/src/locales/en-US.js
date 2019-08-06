import menu from './en-US/menu';
import settings from './en-US/settings';
import exception from './en-US/exception';
import customer from './en-US/customer';
import meeting from './en-US/meeting';
import user from './en-US/user';
import agent from './en-US/agent';
import queue from './en-US/queue';
import status from './en-US/status';
import terminal from './en-US/terminal';
import calls from './en-US/calls';
import evaluate from './en-US/evaluate';
import report from './en-US/report';

export default {
  'navbar.lang': '中文',

  'app.admin.copyright': '2019 GRGBanking Equipment Co., Ltd.',
  'app.admin.homepage': 'Welcome to the iMeeting Management Console',
  'app.admin.adminusername': 'Super Admin',
  'app.admin.login.title': 'iMeeting Management Console',
  'app.admin.login.tips': 'Account Password Login',
  'app.admin.login': 'Login',
  'app.admin.keyword': 'Please enter a keyword',
  'app.admin.search': 'Search',
  'app.admin.reset': 'Reset',
  'app.admin.add': 'Add',
  'app.admin.update': 'Update',
  'app.admin.delete': 'Delete',
  'app.admin.delete.batch': 'Delete Batch',
  'app.admin.import': 'Import',
  'app.admin.success.delete': 'Delete Success',
  'app.admin.success.add': 'Add Success',
  'app.admin.success.update': 'Update Success',
  'app.admin.success.import': 'Import Success',
  'app.admin.success.reset': 'Reset Success',
  'app.admin.phone': 'Phone Number',
  'app.admin.phone.exists': 'This phone number already exists!',
  'app.admin.phone.format': 'Please enter the phone number in the correct format!',
  'app.admin.email': 'Email',
  'app.admin.email.format': 'Please enter the correct email address!',
  'app.admin.email.overmaxlen': 'Enter a mailbox exceeding the maximum length of 64 bytes!',
  'app.admin.operate': 'Operatation',
  'app.admin.order': 'ID',
  'app.admin.choice': 'Selected',
  'app.admin.item': 'Item',
  'app.admin.clear': 'Clear',
  'app.admin.total': 'Total',
  'app.admin.tips': 'Tips',
  'app.admin.confirm': 'Confirm',
  'app.admin.cancel': 'Cancel',
  'app.admin.password.format': 'The passwords entered twice do not match!',
  'app.admin.password.reset': 'Password Reset',
  'app.admin.password.reset.format': 'This operation will revert to the default password "111111". Do you want to continue?',
  'app.admin.status': 'Status',
  'app.admin.begin.date': 'Start Date',
  'app.admin.end.date': 'End Date',
  'app.admin.search.keyword': 'Search keywords cannot be special characters',
  
  'app.admin.license.machinecode.info': 'Machine Code Info',
  'app.admin.license.machinecode': 'Server Machine Code',
  'app.admin.license.uploadfile.info': 'Upload File',
  'app.admin.license.uploadfile': 'Upload License File',
  'app.admin.license.licenseinfo': 'License Info',
  'app.admin.license.uploadsucc': '{file} file uploaded successfully',
  'app.admin.license.uploadfail': 'Licnese file check error!\r\nError Msg:{msg}',

  ...menu,
  ...settings,
  ...exception,
  ...customer,
  ...meeting,
  ...user,
  ...agent,
  ...queue,
  ...terminal,
  ...status,
  ...calls,
  ...evaluate,
  ...report
};
