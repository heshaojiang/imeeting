import { stringify } from 'qs';
import request from '@/utils/request';

export async function fetchlist(params) {
  return request(`/admin/room/page?${stringify(params)}`);
}

export async function addObject(obj) {
  return request('/admin/room/', {
    method: 'POST',
    body: obj
  });
}

export async function deleteObject(id) {
  return request(`/admin/room/${id}`, {
    method: 'DELETE',
    expirys: false,
  });
}

export async function getObject(id) {
  return request(`/admin/room/${id}`, {
    method: 'GET',
    expirys: false,
  });
}

export async function updateObject(obj) {
  return request(`/admin/room`, {
    method: 'PUT',
    body: obj
  });
}

export async function getRoomNo() {
  return request(`/admin/bizMeeting/getRoomNo`);
}