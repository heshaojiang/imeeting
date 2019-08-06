import { fetchPage,fetchList, deleteObject, addObject, updateObject,fetchStrategy, fetchMembers,fetchMembersOnline} from '@/services/acdQueue';
import { Message } from 'antd';
import { formatMessage } from 'umi/locale';

export default {
  namespace: 'queue',

  state: {
    data: {
      list: [],
      pagination: {
        current: 1,
        pageSize: 20
      },
    },
    queueStrategy:[],
    members:{
      list: [],
      pagination: {
        current: 1,
        pageSize: 10
      },
    },
    list:[]
  },
  effects: {
    *fetch({ payload }, { call, put, select }) {
      let { pageSize, current } = yield select(state => state.queue.data.pagination);
      const response = yield call(fetchPage, { limit: pageSize, page: current, ...payload });
      yield put({
        type: 'save',
        payload: response
      });
    },
    *fetchList({ payload }, { call, put, select }) {
      const response = yield call(fetchList);
      yield put({
        type: 'saveList',
        payload: response
      });
    },
    *strategy(_, { call, put }) {
      const response = yield call(fetchStrategy);
      yield put({
        type: 'saveStrategy',
        payload: response
      });
    },
    *member({ payload, callback }, { call, put ,select}) {
      
      let { pageSize, current } = yield select(state => state.queue.members.pagination);
      const response = yield call(fetchMembers,payload.queueId,{ limit: pageSize, page: current,...payload.params });
      if(response){
        yield put({
          type: 'saveMembers',
          payload: response
        });
      }
      callback && callback();
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
      let { pageSize, current, total } = yield select(state => state.queue.data.pagination);
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
    saveList(state, action) {
      return {
        ...state,
        list: action.payload
      }
    },
    saveStrategy(state, action) {
      return {
        ...state,
        queueStrategy: action.payload
      }
    },
    saveMembers(state, action) {
      const { records, size, total, current } = action.payload
      return {
        ...state,
        members:{
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