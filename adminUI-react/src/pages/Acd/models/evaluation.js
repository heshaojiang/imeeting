import { fetchlist,fetchScorelist,addScore,updateScore,deleteScore} from '@/services/acdEvaluation';
import { Message } from 'antd';
import { formatMessage } from 'umi/locale';

export default {
  namespace: 'evaluation',
  state: {
    data: {
      list: [],
      pagination: {
        current: 1,
        pageSize: 10
      }
    },
    score:[]
  },
  effects: {
    *fetch({ payload }, { call, put, select }) {
      let { pageSize, current } = yield select(state => state.evaluation.data.pagination);
      const response = yield call(fetchlist, { limit: pageSize, page: current, ...payload });
      yield put({
        type: 'saveEvaluation',
        payload: response
      });
    },
    *fetchScore(_, { call, put }) {
      const response = yield call(fetchScorelist);
      yield put({
        type: 'saveEvaluationScore',
        payload: response
      });
    },
    *deleteScore({ payload: id, callback }, { call, put }) {
      const response = yield call(deleteScore, id);
      if(response) {
        Message.success(formatMessage({ id: 'app.admin.success.delete' }));
        yield put({ type: 'reload' , action: 'delete'});
      }
      callback && callback();
    },
    *addScore({ payload }, { call, put }) {
      const response = yield call(addScore, payload);
      if (response) {
        Message.success(formatMessage({ id: 'app.admin.success.add' }));
        yield put({type: 'reload'});
      }
    },
    *updateScore({ payload }, { call, put }) {
      const response = yield call(updateScore, payload);
      if (response) {
        Message.success(formatMessage({ id: 'app.admin.success.update' }));
        yield put({type: 'reload'});
      }
    },
    *reload(action, { put, select }) {
      yield put({ type: 'fetchScore', payload: {} });
    }
  },
  reducers: {
    saveEvaluation(state, action) {
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
   saveEvaluationScore(state, action) {
      return {
        ...state,
        score: action.payload
      }
    }
  }
}