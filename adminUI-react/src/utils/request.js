import fetch from 'dva/fetch';
import { notification, Modal } from 'antd';
import router from 'umi/router';
import hash from 'hash.js';
import { isAntdPro } from './utils';
import { getToken } from '@/utils/authority';
import en_US from '@/locales/en-US'
import zh_CN from '@/locales/zh-CN'
import { formatMessage, getLocale } from 'umi/locale';

let errorStatus = false;

const checkStatus = response => {
  if (response.status >= 200 && response.status < 300) {
    return response.json();
  }
  const currentLocal = getLocale();
  const locale = currentLocal === 'en-US' ? en_US : zh_CN;
  const errortext = locale[response.status] || locale[response.default];

  const error = new Error(errortext);
  error.name = response.status;
  error.response = response;
  throw error;
};

/**
 * Requests a URL, returning a promise.
 *
 * @param  {string} url       The URL we want to request
 * @param  {object} [options] The options we want to pass to "fetch"
 * @return {object}           An object containing either "data" or "err"
 */
export default function request(
  url,
  options 
) {
  const codeMessage = window.g_lang === 'zh-CN' ? zh_CN : en_US;
  /**
   * Produce fingerprints based on url and parameters
   * Maybe url has the same parameters
   */
  const defaultOptions = {
    credentials: 'include',
  };

  const newOptions = { ...defaultOptions, ...options };
  // hack get token
  let currentUrlHash = window.location.hash;
  
  if(!currentUrlHash.startsWith('#/user/')) {
    newOptions.headers = {
      ...newOptions.headers,
      Authorization: 'Bearer ' + getToken()
    }
  }

  if (
    newOptions.method === 'POST' ||
    newOptions.method === 'PUT' ||
    newOptions.method === 'DELETE'
  ) {
    if (!(newOptions.body instanceof FormData)) {
      newOptions.headers = {
        Accept: 'application/json',
        'Content-Type': 'application/json; charset=utf-8',
        ...newOptions.headers,
      };
      newOptions.body = JSON.stringify(newOptions.body);
    } else {
      // newOptions.body is FormData
      newOptions.headers = {
        // Accept: 'application/json',
        ...newOptions.headers,
      };                                                               
    }
  }
  
  return fetch(url, newOptions)
    .then(checkStatus)
    .then(result => {

      if (newOptions.ignore) {
        return result;
      }

      if (result.access_token) {
        return result;
      }

      if (result.success) {
        return result.data ? result.data : true;
      }

      if (!result.success) {
        Modal.error({
          title: formatMessage({ id: 'app.admin.tips' }),
          content: codeMessage[result.code] == null ? result.msg : codeMessage[result.code]
        });

        return;
      }

      
    })
    .catch(e => {

      const status = e.name;
      const error = e.message;

      if (!errorStatus) {
        Modal.error({
          title: formatMessage({ id: 'app.admin.tips' }),
          content: error,
          onOk() {
            errorStatus = false
          }
        });
        errorStatus = true
      }
      
      if (status === 401 || status === 403 || status === 500) {
        // @HACK
        /* eslint-disable no-underscore-dangle */
        window.g_app._store.dispatch({
          type: 'login/refresh',
        });
      }

    //   // environment should not be used
    //   if (status === 403) {
    //     router.push('/exception/403');
    //     return;
    //   }

    //   if (status <= 504 && status >= 500) {
    //     router.push('/exception/500');
    //     return;
    //   }

    //   if (status >= 404 && status < 422) {
    //     router.push('/exception/404');
    //   }

      // return e;
    });
}
