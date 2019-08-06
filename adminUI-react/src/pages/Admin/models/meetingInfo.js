import { fetchMettingList, addMeeting, deleteMeeting, updateMeeting, getMeetingStatus, getMeetingType } from '@/services/api';
import { Message } from 'antd';
import { formatMessage, getLocale } from 'umi/locale';
export default {
  namespace: 'meetingInfo',

  state: {
    data: {
      list: [],
      pagination: {
        current: 1,
        pageSize: 20
      },
      meetingStatus: [],
      meetingType: [],
      show: false,
    }
  },

  effects: {

    *fetch({ payload }, { call, put, select }) {
      let { pageSize, current } = yield select(state => state.meetingInfo.data.pagination);
      const response = yield call(fetchMettingList, { limit: pageSize, page: current, ...payload });
      yield put({
        type: 'save',
        payload: response
      });
    },
    *delete({ payload: id, callback }, { call, put }) {
      const response = yield call(deleteMeeting, id);
      if(response) {
        Message.success(formatMessage({ id: 'app.admin.success.delete' }));
        yield put({ type: 'reload' , action: 'delete'});
      }

      callback && callback();
    },
    *add({ payload }, { call, put }) {
      const response = yield call(addMeeting, payload);
      if(response) {
        Message.success(formatMessage({ id: 'app.admin.success.add' }));
        yield put({type: 'reload'});
      }
    },
    *update({ payload }, { call, put }) {
      const response = yield call(updateMeeting, payload);
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
    },
    
  },

  reducers: {
    save(state, action) {
      const { records, size, total, current } = action.payload
      return {
        data: {
          ...state.data,
          list: records,
          pagination: {
            total,
            current,
            pageSize: size
          }
        }
      }
    },
  }
}