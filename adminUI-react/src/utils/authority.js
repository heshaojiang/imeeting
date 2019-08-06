// use sessionStorage to store the authority info, which might be sent from server in actual project.
export function getAuthority(str) {
  // return sessionStorage.getItem('antd-pro-authority') || ['admin', 'user'];
  const authorityString =
    typeof str === 'undefined' ? sessionStorage.getItem('antd-pro-authority') : str;
  // authorityString could be admin, "admin", ["admin"]
  let authority;
  try {
    authority = JSON.parse(authorityString);
  } catch (e) {
    authority = authorityString;
  }
  if (typeof authority === 'string') {
    return [authority];
  }

  return authority || ['guest'];
}

export function setAuthority(authority) {
  const proAuthority = typeof authority === 'string' ? [authority] : authority;
  return sessionStorage.setItem('antd-pro-authority', JSON.stringify(proAuthority));
}

const TOKEN = 'admin-token';
const REFRESH_TOEKN='refresh_token';
const USERID = 'userId';
const AUTOLOGIN = 'autologin';

export function getToken() {
  return sessionStorage.getItem(TOKEN);
}

export function setToken(token) {
  sessionStorage.setItem(TOKEN, token);
}

export function removeToken() {
  sessionStorage.removeItem(TOKEN);
}

export function getRefreshToken() {
  return sessionStorage.getItem(REFRESH_TOEKN)
}

export function setRefreshToken(token) {
  sessionStorage.setItem(REFRESH_TOEKN, token)
}

export function setUserId(userId) {
  sessionStorage.setItem(USERID, userId)
}

export function getUserId() {
  return sessionStorage.getItem(USERID)
}

export function setAutoLogin(status) {
  return sessionStorage.setItem(AUTOLOGIN, status)
}

export function getAutoLogin(status) {
  return sessionStorage.getItem(AUTOLOGIN)
}