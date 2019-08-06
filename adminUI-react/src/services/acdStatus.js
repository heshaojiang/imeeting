import { stringify } from 'qs';
import request from '@/utils/request';

export async function fetchCallCurrentlist(params) {
  return request(`/acd/calls/current/page?${stringify(params)}`);
}

export async function fetchCallStatus(obj) {
  return request(`/acd/calls/status`);
}

export async function fetchStatusCurrent(obj) {
  return request(`/acd/status/current`);
}

export async function getObject(id) {
  return request(`/acd/calls/info/${id}`, {
    method: 'GET',
    expirys: false,
  });
}

export async function fetchStatusDayList(params) {
  return request(`/acd/status/day?${stringify(params)}`);
}
export async function fetchStatusDayFieldList(params) {
  return request(`/acd/status/day/field?${stringify(params)}`);
}


