import { fetchlist, deleteUser, addUser, updateUser, passwordResetByManager } from '@/services/api';
import { Message } from 'antd';
import { formatMessage } from 'umi/locale';

export default {
  namespace: 'userAdmin',

  state: {
    data: {
      list: [],
      pagination: {
        current: 1,
        pageSize: 20
      }
    }
  },
  effects: {
    *fetch({ payload }, { call, put, select }) {
      
      let { pageSize, current } = yield select(state => state.userAdmin.data.pagination);
      const response = yield call(fetchlist, { limit: pageSize, page: current, ...payload });
      yield put({
        type: 'save',
        payload: response
      });
    },
    *delete({ payload: id, callback }, { call, put }) {
      const response = yield call(deleteUser, id);
      if(response) {
        Message.success(formatMessage({ id: 'app.admin.success.delete' }));
        yield put({ type: 'reload' , action: 'delete'});
      }

      callback && callback();
    },
    *reset({ payload: id, callback }, { call, put }) {
        const response = yield call(passwordResetByManager, id);
        if(response) {
          Message.success(formatMessage({ id: 'app.admin.success.reset' }));
          yield put({type: 'reload'});
        }
  
        callback && callback();
      },
    *add({ payload }, { call, put }) {
      const response = yield call(addUser, payload);
      if(response) {
        Message.success(formatMessage({ id: 'app.admin.success.add' }));
        yield put({type: 'reload'});
      }
    },
    *update({ payload }, { call, put }) {
      const response = yield call(updateUser, payload);
      if(response) {
        Message.success(formatMessage({ id: 'app.admin.success.update' }));
        yield put({type: 'reload'});
      }
    },
    *reload(action, { put, select }) {
      let { pageSize, current, total } = yield select(state => state.userAdmin.data.pagination);
      if(action && (total % pageSize === 1)) {  

        current  = current !== 1 ? --current : current;
      }
      yield put({ type: 'fetch', payload: { limit: pageSize, page: current } });
    }
  },

  reducers: {
    save(state, action) {
      const { records, size, total, current } = action.payload
      return {
        ...state,
        data: {
          list: records,
          pagination: {
            total,
            current,
            pageSize: size
          }
        }
      }
    }
  }
}