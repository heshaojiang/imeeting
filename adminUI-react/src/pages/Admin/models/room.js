import { fetchlist, deleteObject, addObject, updateObject } from '@/services/room';
import { getTerminalType, getRoomStatus } from '@/services/api';
import { Message } from 'antd';

export default {
  namespace: 'room',
  state: {
    data: {
      list: [],
      pagination: {
        current: 1,
        pageSize: 20
      },
      terminalType: [],
      roomType: []
    }
  },
  effects: {
    *fetch({ payload }, { call, put, select }) {
      
      let { pageSize, current } = yield select(state => state.room.data.pagination);
      const response = yield call(fetchlist, { limit: pageSize, page: current, ...payload });
      yield put({
        type: 'save',
        payload: response
      });
    },
    *delete({ payload: id, callback }, { call, put }) {
      const response = yield call(deleteObject, id);
      if(response) {
        Message.success('删除成功');
        yield put({ type: 'reload' , action: 'delete'});
      }
      callback && callback();
    },
    *add({ payload }, { call, put }) {
      const response = yield call(addObject, payload);
      if(response) {
        Message.success('新建成功');
        yield put({type: 'reload'});
      }
    },
    *update({ payload }, { call, put }) {
      const response = yield call(updateObject, payload);
      if(response) {
        Message.success('更新成功');
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
    saveDictType(state, action) {
      const { roomType, terminalType, show } = action.payload;
      return {
        data: {
          ...state.data,
          roomType,
          terminalType,
          show
        }
      }
    }
  }
}