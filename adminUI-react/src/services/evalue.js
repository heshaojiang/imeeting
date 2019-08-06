import { stringify } from 'qs';
import request from '@/utils/request';

/** 客服人员数据接口 */
export async function fetchlist(params) {
  return request(`/acd/agent/page?${stringify(params)}`);
}

export async function fetchCustomerlist(params) {
    return request(`/acd/agent/select`);
}

export async function addObject(obj) {
  return request('/acd/agent/', {
    method: 'POST',
    body: obj
  });
}

export async function deleteObject(id) {
  return request(`/acd/agent/${id}`, {
    method: 'DELETE',
    expirys: false,
  });
}

export async function getObject(id) {
  return request(`/acd/agent/${id}`, {
    method: 'GET',
    expirys: false,
  });
}

export async function updateObject(obj) {
  return request(`/acd/agent/`, {
    method: 'PUT',
    body: obj
  });
}
