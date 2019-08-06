import { stringify } from 'qs';
import request from '@/utils/request';

export async function fetchlist(params) {
  return request(`/acd/terminal/page?${stringify(params)}`);
}
export async function fetchTerminalType() {
  return request(`/acd/terminal/type`);
}

export async function fetchTerminalStatus() {
  return request(`/acd/terminal/status`);
}

export async function addObject(obj) {
  return request('/acd/terminal/', {
    method: 'POST',
    body: obj
  });
}

export async function deleteObject(id) {
  return request(`/acd/terminal/${id}`, {
    method: 'DELETE',
    expirys: false,
  });
}

export async function getObject(id) {
  return request(`/acd/terminal/${id}`, {
    method: 'GET',
    expirys: false,
  });
}

export async function updateObject(obj) {
  return request(`/acd/terminal`, {
    method: 'PUT',
    body: obj
  });
}

export async function changeObjectStatus(params) {
  return request(`/acd/terminal/statusChange?${stringify(params)}`, {
    method: 'PUT'
  });
}