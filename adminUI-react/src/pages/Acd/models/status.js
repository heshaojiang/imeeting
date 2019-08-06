import { fetchCallCurrentlist, fetchCallStatus, fetchStatusDayList, fetchStatusDayFieldList,fetchStatusCurrent } from '@/services/acdStatus';
import { Message } from 'antd';
import { formatMessage } from 'umi/locale';

export default {
  namespace: 'status',

  state: {
    data: {
      list: [],
      pagination: {
        current: 1,
        pageSize: 20
      }
    },
    callStatus: [],
    statusDay: {
      list: [],
      pagination: {
        current: 1,
        pageSize: 10
      },
      field: []
    },
    statusCurrent:{}
  },
  effects: {
    *fetchCall({ payload }, { call, put, select }) {
      let { pageSize, current } = yield select(state => state.status.data.pagination);
      const response = yield call(fetchCallCurrentlist, { limit: pageSize, page: current, ...payload });
      yield put({
        type: 'save',
        payload: response
      });
    },
    *status(_, { call, put }) {
      const response = yield call(fetchCallStatus);
      yield put({
        type: 'saveStatus',
        payload: response
      });
    },
    *statusCurrent(_, { call, put }) {
      const response = yield call(fetchStatusCurrent);
      yield put({
        type: 'saveStatusCurrent',
        payload: response
      });
    },
    *reload(action, { put, select }) {
      let { pageSize, current, total } = yield select(state => state.status.data.pagination);
      if (action && (total % pageSize === 1)) {
        current = current !== 1 ? --current : current;
      }
      yield put({ type: 'fetch', payload: { limit: pageSize, page: current } });
    },

    *fetchCallStatus({ payload }, { call, put, select }) {

      let { pageSize, current } = yield select(state => state.status.statusDay.pagination);
      const response = yield call(fetchStatusDayList, { limit: pageSize, page: current, ...payload });
      const field = yield call(fetchStatusDayFieldList, { limit: pageSize, page: current, ...payload });
      yield put({
        type: 'saveStatusDay',
        payload: { ...response, field }
      });
    },



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
    saveStatus(state, action) {
      return {
        ...state,
        callStatus: action.payload
      }
    },
     saveStatusCurrent(state, action) {
      return {
        ...state,
        statusCurrent: action.payload
      }
    },
    saveStatusDay(state, action) {
      const { records, size, total, current, field } = action.payload
      return {
        ...state,
        statusDay: {
          list: records,
          pagination: {
            total,
            current,
            pageSize: size
          },
          field: field
        }
      }
    },
  }
}