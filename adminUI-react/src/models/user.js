import { query as queryUsers, queryCurrent } from '@/services/user';
import { getAllType, changePassword, passwordReset, logout } from "@/services/api";
import { routerRedux } from 'dva/router';
import { formatMessage } from 'umi/locale';
import { Message } from 'antd';
import { removeToken } from '@/utils/authority';
export default {
  namespace: 'user',

  state: {
    list: [],
    currentUser: {},
    allType: {}
  },

  effects: {
    *fetchAllType(_, { call, put }) {
      const response = yield call(getAllType);
      yield put({
        type: 'saveAllType',
        payload: response
      });
    },
    *fetch(_, { call, put }) {
      const response = yield call(queryUsers);
      yield put({
        type: 'save',
        payload: response,
      });
    },
    *fetchCurrent({ payload }, { call, put }) {
      const response = yield call(queryCurrent, payload);
      if(!response) {
        yield put(
          routerRedux.push({
            pathname: '/user/login'
          })
        );
      }
      yield put({
        type: 'saveCurrentUser',
        payload: response,
      });
    },
    *changePassword({ payload }, { call, put }) {
      const response = yield call(changePassword, payload);
      if (response) {
        removeToken();
        Message.success(formatMessage({ id: 'app.admin.success.update' }));
        yield put(
            routerRedux.push({
              pathname: '/user/login'
            })
          );
      }
    },
    *passwordReset(_, { call, put }) {
        const response = yield call(passwordReset);
        if (response) {
          removeToken();
          Message.success(formatMessage({ id: 'app.admin.success.update' }));
          yield put(
              routerRedux.push({
                pathname: '/user/login'
              })
            );
        }
      }
  },

  reducers: {
    saveAllType(state, action) {
      return {
        ...state,
        allType: action.payload
      }
    },
    save(state, action) {
      return {
        ...state,
        list: action.payload,
      };
    },
    saveCurrentUser(state, action) {
      return {
        ...state,
        currentUser: action.payload || {},
      };
    },
    changeNotifyCount(state, action) {
      return {
        ...state,
        currentUser: {
          ...state.currentUser,
          notifyCount: action.payload,
        },
      };
    },
  },
};
