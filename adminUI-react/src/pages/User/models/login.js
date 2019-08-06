import { routerRedux } from 'dva/router';
import { stringify } from 'qs';
import { getFakeCaptcha, login, logout } from '@/services/api';
import { setAuthority } from '@/utils/authority';
import { getPageQuery } from '@/utils/utils';
import { reloadAuthorized } from '@/utils/Authorized';
import { setToken, removeToken, getRefreshToken, getToken ,setUserId } from '@/utils/authority';
// import { Modal } from 'antd';

export default {
  namespace: 'login',

  state: {
    status: undefined,
  },

  effects: {
    *login({ payload }, { call, put }) {
      const response = yield call(login, payload);

      // Login successfully
      if (response && response.status === 'ok') {

        yield put({
          type: 'changeLoginStatus',
          payload: {
            currentAuthority: response.role,
            ...response
          },
        });

        reloadAuthorized();
        setToken(response.access_token);
        setUserId(response.userId);
        // const urlParams = new URL(window.location.href);
        // const params = getPageQuery();
        // let { redirect } = params;
        // if (redirect) {
        //   const redirectUrlParams = new URL(redirect);
        //   if (redirectUrlParams.origin === urlParams.origin) {
        //     redirect = redirect.substr(urlParams.origin.length);
        //     if (redirect.startsWith('/#')) {
        //       redirect = redirect.substr(2);
        //     }
        //   } else {
        //     window.location.href = redirect;
        //     return;
        //   }
        // }
        yield put(routerRedux.replace('/'));
      }
    },

    *getCaptcha({ payload }, { call }) {
      yield call(getFakeCaptcha, payload);
    },

    *logout(_, { put, call }) {

      const payload = { 
        accesstoken: getToken(), 
        refreshToken: getRefreshToken()
      };

      yield call(logout, payload);

      yield put({ type: 'refresh' });
      
    },
    // *refreshToken(_, { put , call}) {
    //   const { refreshToken } = yield select(state => state.login);
    //   const response =  yield call(getRefreshToken, refreshToken);
    // },
    *refresh(_, { put }) {

      yield put(
        routerRedux.push({
          pathname: '/user/login'
        })
      );

      yield put({
        type: 'changeLoginStatus',
        payload: {
          status: false,
          currentAuthority: 'guest',
        },
      });

      reloadAuthorized();
      removeToken();
      
    }
  },

  reducers: {
    changeLoginStatus(state, { payload }) {
      setAuthority(payload.currentAuthority);
      if (payload.access_token) {
        return {
          ...state,
          accesstoken: payload.access_token,
          refreshToken: payload.refresh_token,
          status: payload.status,
          grant_type: payload.grant_type,
          msg: payload.msg,
        };
      }

      return {
        ...state,
        status: payload.status,
        grant_type: payload.grant_type,
        msg: payload.msg
      };
    },
  },
};
