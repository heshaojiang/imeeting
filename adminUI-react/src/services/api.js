import { stringify } from 'qs';
import request from '@/utils/request';
import { getLocale } from 'umi/locale';

export async function login(params) {
  params = Object.assign({}, { grant_type: 'password', scope: 'server', code: '' }, params)
  return request(`/auth/oauth/token?${stringify(params)}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Authorization': 'Basic YWRtaW46YWRtaW4='
    }
  });
}

export async function logout(params) {
  return request(`/auth/authentication/removeToken?${stringify(params)}`, {
    method: 'POST'
  });
}

export async function changePassword(obj) {
  return request(`/admin/user/editInfo`, {
    method: 'PUT',
    body: obj
  })
}

export async function passwordReset() {
    return request(`/admin/user/passwordReset`, {
      method: 'PUT',
    })
  }

export async function getRefreshToken(token) {
  return request(`/auth/oauth/token?grantType=${token}`, {
      method: 'POST'
  });
}

export async function fetchlist(params) {
  return request(`/admin/user/userPage?${stringify(params)}`);
}
export async function fetchMeetingUsers(params) {
  return request(`/admin/user/imeetingUserPage?${stringify(params)}`);
}
export async function addUser(obj) {
  return request('/admin/user/', {
    method: 'POST',
    body: obj
  });
}

export async function deleteUser(id) {
  return request(`/admin/user/${id}`, {
    method: 'DELETE',
  });
}

export async function passwordResetByManager(id) {
    return request(`/admin/user/passwordResetByManager/${id}`, {
      method: 'PUT',
    });
}

export async function getUser(id) {
  return request(`/admin/user/${id}`, {
    method: 'GET',
  });
}

export async function customerChangeObtainUser(customerId) {
    return request(`/admin/user/customerChange/${customerId}`, {
      method: 'GET'
    });
  }

export async function updateUser(obj) {
  return request(`/admin/user`, {
    method: 'PUT',
    body: obj
  });
}

export async function fetchMettingList(params) {
  return request(`/admin/bizMeeting/comingMeeting?${stringify(params)}`, {
    method: 'GET',
  });
}

export async function deleteMeeting(id) {
  return request(`/admin/bizMeeting/${id}`, {
    method: 'DELETE',
  });
}

export async function addMeeting(obj) {
  return request(`/admin/bizMeeting`, {
    method: 'POST',
    body: obj
  });
}

export async function updateMeeting(obj) {
  return request(`/admin/bizMeeting`, {
    method: 'PUT',
    body: obj
  });
}

export async function getMeeting(id) {
  return request(`/admin/bizMeeting/${id}`, {
    method: 'GET',
  });
}

export async function getMeetingType() {
  return request(`/admin/dict/type/meetingType`);
}

export async function getRoomStatus() {
  return request(`/admin/dict/type/roomStatus`);
}

export async function getMeetingStatus() {
  return request(`/admin/dict/type/meetingStatus`);
}

export async function getTerminalType() {
  return request(`/admin/dict/type/terminalType`);
}

export async function getSignType() {
  return request(`/admin/dict/type/signType`);
}

export async function getAllType() {
  const locale = getLocale();
  return request(`/admin/dict/allType/${locale}`);
}

export async function validateField(filed, value) {
  return request(`/admin/common/validate?str=${value}&type=${filed}`, {
    ignore: true
  });
}
export async function validateAcdField(filed, value) {
  return request(`/acd/common/validate?str=${value}&query=${filed}`, {
    ignore: true
  });
}

export async function getMachineCode() {
  return request(`/admin/license/getmachinecode`);
}

export async function getLicenseInfo() {
  return request(`/admin/license/getlicenseinfo`);
}

