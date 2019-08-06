import { fetchSessionTotal, fetchCallPlatform, fetchResultSolve, fetchSatisfly,fetchAgentInfo } from '@/services/acdReportForm';
import { Message } from 'antd';
import { formatMessage } from 'umi/locale';

export default {
  namespace: 'reportform',

  state: {
    agentdata: {
      list: [],
      pagination: {
        current: 1,
        pageSize: 20
      }
    },
    CallPlatform: [],
    ResultSolve: [],
    Satisfly: [],
    stackdate: [],
  },
  effects: {
    *fetchSessionTotal({ payload }, { call, put }) {
      const response = yield call(fetchSessionTotal, {...payload });
      yield put({
        type: 'returnSessionTotal',
        payload: response
      });
    },

    *fetchAgentInfo({ payload }, { call, put, select }) {
      let { pageSize, current } = yield select(state => state.reportform.agentdata.pagination);
      const response = yield call(fetchAgentInfo, { limit: pageSize, page: current, ...payload });
      yield put({
        type: 'save',
        payload: response
      });
    },
    *fetchCallPlatform(_, { call, put }) {
      const response = yield call(fetchCallPlatform);
      yield put({
        type: 'saveCallPlatform',
        payload: response
      });
    },
    *fetchResultSolve(_, { call, put }) {
      const response = yield call(fetchResultSolve);
      yield put({
        type: 'saveResultSolve',
        payload: response
      });
    },
    *fetchSatisfly(_, { call, put }) {
      const response = yield call(fetchSatisfly);
      yield put({
        type: 'saveSatisfly',
        payload: response
      });
    },
    *reload(action, { put, select }) {
      let { pageSize, current, total } = yield select(state => state.reportform.tabledata.pagination);
      if (action && (total % pageSize === 1)) {
        current = current !== 1 ? --current : current;
      }
      yield put({ type: 'fetchAgentInfo', payload: { limit: pageSize, page: current } });
    },
  },

  reducers: {
    save(state, action) {
      const { records, size, total, current } = action.payload
      return {
        ...state,
        agentdata: {
          list: records,
          pagination: {
            total,
            current,
            pageSize: size
          }
        }
      }
    },

     saveCallPlatform(state, action) {
      return {
        ...state,
        CallPlatform: action.payload
      }
    },

    saveResultSolve(state, action) {
      return {
        ...state,
        ResultSolve: action.payload
      }
    },
    saveSatisfly(state, action) {
      return {
        ...state,
        Satisfly: action.payload
      }
    },
      returnSessionTotal(state, action) { 
      return {
        ...state,
        stackdate:  action.payload
      }
    },
  }
}