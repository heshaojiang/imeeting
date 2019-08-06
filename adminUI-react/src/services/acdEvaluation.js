import { stringify } from 'qs';
import request from '@/utils/request';

export async function fetchlist(params) {
  return request(`/acd/evaluation/page?${stringify(params)}`);
}

export async function fetchScorelist(params) {
  return request(`/acd/evaluation/score/list?${stringify(params)}`);
}

 export async function addScore(obj) {
    return request('/acd/evaluation/score/', {
      method: 'POST',
      body: obj
    });
  }

  export async function updateScore(obj) {
    return request('/acd/evaluation/score/', {
      method: 'PUT',
      body: obj
    });
  }
  
  export async function deleteScore(id) {
    return request(`/acd/evaluation/score/${id}`, {
      method: 'DELETE',
      expirys: false,
    });
  }
  
  export async function getScore(id) {
    return request(`/acd/evaluation/score/${id}`, {
      method: 'GET',
      expirys: false,
    });
  }
