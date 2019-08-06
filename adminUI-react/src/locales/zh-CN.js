import menu from './zh-CN/menu';
import settings from './zh-CN/settings';
import exception from './zh-CN/exception';
import customer from './zh-CN/customer';
import meeting from './zh-CN/meeting';
import user from './zh-CN/user';
import agent from './zh-CN/agent';
import queue from './zh-CN/queue';
import status from './zh-CN/status';
import terminal from './zh-CN/terminal';
import calls from './zh-CN/calls';
import evaluate from './zh-CN/evaluate';
import report from './zh-CN/report';

export default {
  'navbar.lang': 'English',

  'app.admin.copyright': '2019 广电运通金融电子股份有限公司',
  'app.admin.homepage': '欢迎使用iMeeting后台管理系统',
  'app.admin.adminusername': '超级管理员',
  'app.admin.login.title': 'iMeeting后台管理系统',
  'app.admin.login.tips': '账号密码登录',
  'app.admin.login': '登录',
  'app.admin.keyword': '请输入关键字',
  'app.admin.search': '搜索',
  'app.admin.reset': '重置',
  'app.admin.add': '新建',
  'app.admin.update': '配置',
  'app.admin.delete': '删除',
  'app.admin.delete.batch': '批量删除',
  'app.admin.import': '导入',
  'app.admin.export': '导出',
  'app.admin.success.delete': '删除成功',
  'app.admin.success.add': '新建成功',
  'app.admin.success.update': '更新成功',
  'app.admin.success.import': '导入成功',
  'app.admin.success.reset': '重置成功',
  'app.admin.phone': '手机号码',
  'app.admin.phone.exists': '手机号已存在！',
  'app.admin.phone.format': '请输入正确格式的手机号码！',
  'app.admin.email': '邮箱',
  'app.admin.email.format': '请输入正确的邮箱！',
  'app.admin.email.overmaxlen': '输入邮箱超过最大长度64字节！',
  'app.admin.operate': '操作',
  'app.admin.order': '序号',
  'app.admin.choice': '已选择',
  'app.admin.item': '项',
  'app.admin.clear': '清空',
  'app.admin.total': '总计',
  'app.admin.tips': '提示',
  'app.admin.confirm': '确认',
  'app.admin.cancel': '取消',
  'app.admin.password.format': '两次输入的密码不一致！',
  'app.admin.password.reset': '密码重置',
  'app.admin.password.reset.format': '此操作将恢复为默认密码“111111”, 是否继续？',
  'app.admin.status': '状态',
  'app.admin.begin.date': '开始时间：',
  'app.admin.end.date': '结束时间：',
  'app.admin.search.keyword': '搜索关键字不能为特殊字符',
  
  'app.admin.license.machinecode.info': '机器码信息',
  'app.admin.license.machinecode': '服务器机器码',
  'app.admin.license.uploadfile.info': '上传文件',
  'app.admin.license.uploadfile': '上传 License 文件',
  'app.admin.license.licenseinfo': 'License 信息',
  'app.admin.license.uploadsucc': '{file} 文件上传成功',
  'app.admin.license.uploadfail': 'Licnese 文件检查错误!\r\n错误信息:{msg}',

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
