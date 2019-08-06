import { fetchlist, deleteObject, addObject, updateObject,fetchTerminalType,fetchTerminalStatus ,changeObjectStatus} from '@/services/acdTerminal';
import { Message } from 'antd';
import { formatMessage } from 'umi/locale';

export default {
  namespace: 'terminal',
  state: {
    data: {
      list: [],
      pagination: {
        current: 1,
        pageSize: 20
      }
    },
    terminalType:[],
    terminalStatus:[]
  },
  effects: {
    *fetch({ payload }, { call, put, select }) {
      let { pageSize, current } = yield select(state => state.agent.data.pagination);
      const response = yield call(fetchlist, { limit: pageSize, page: current, ...payload });
      yield put({
        type: 'save',
        payload: response
      });
    },
    *type(_, { call, put }) {
      const response = yield call(fetchTerminalType);
      yield put({
        type: 'saveTypes',
        payload: response
      });
    },
    *status(_, { call, put }) {
      const response = yield call(fetchTerminalStatus);
      yield put({
        type: 'saveStatus',
        payload: response
      });
    },
    *statusChange({ payload }, { call, put }) {
      const response = yield call(changeObjectStatus, payload);
      if (response) {
        Message.success(formatMessage({ id: 'app.admin.success.update' }));
        yield put({type: 'reload'});
      }
    },
    *delete({ payload: id, callback }, { call, put }) {
      const response = yield call(deleteObject, id);
      if(response) {
        Message.success(formatMessage({ id: 'app.admin.success.delete' }));
        yield put({ type: 'reload' , action: 'delete'});
      }
      callback && callback();
    },
    *add({ payload }, { call, put }) {
      const response = yield call(addObject, payload);
      if (response) {
        Message.success(formatMessage({ id: 'app.admin.success.add' }));
        yield put({type: 'reload'});
      }
    },
    *update({ payload }, { call, put }) {
      const response = yield call(updateObject, payload);
      if (response) {
        Message.success(formatMessage({ id: 'app.admin.success.update' }));
        yield put({type: 'reload'});
      }
    },
    *reload(action, { put, select }) {
      let { pageSize, current, total } = yield select(state => state.agent.data.pagination);
      if (action && (total % pageSize === 1)) {  
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
    },
    saveTypes(state, action) {
      return {
        ...state,
        terminalType: action.payload
      }
    },
    saveStatus(state, action) {
      return {
        ...state,
        terminalStatus: action.payload
      }
    }
  }
}