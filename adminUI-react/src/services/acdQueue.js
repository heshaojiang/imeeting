import { stringify } from 'qs';
import request from '@/utils/request';

export async function fetchPage(params) {
    return request(`/acd/queue/page?${stringify(params)}`);
}

export async function fetchList() {
    return request(`/acd/queue/list`);
}

export async function fetchSelect(params) {
  return request(`/acd/agent/select?${stringify(params)}`);
}

export async function fetchStrategy() {
  return request(`/acd/queue/strategy/`);
}

export async function fetchMembers(queueId,params) {
  return request(`/acd/queue/page/members/${queueId}?${stringify(params)}`,{
    method: 'GET',
    expirys: false,
  });
}
export async function fetchMembersOnline(queueId,params) {
  return request(`/acd/queue/page/members/${queueId}/online?${stringify(params)}`,{
    method: 'GET',
    expirys: false,
  });
}

export async function addObject(obj) {
  return request('/acd/queue/', {
    method: 'POST',
    body: obj
  });
}

export async function deleteObject(id) {
  return request(`/acd/queue/${id}`, {
    method: 'DELETE',
    expirys: false,
  });
}
  
export async function getObject(id) {
  return request(`/acd/queue/${id}`, {
    method: 'GET',
    expirys: false,
  });
}

export async function updateObject(obj) {
  return request(`/acd/queue`, {
    method: 'PUT',
    body: obj
  });
}
