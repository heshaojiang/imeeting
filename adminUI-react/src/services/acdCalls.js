import { stringify } from 'qs';
import request from '@/utils/request';

export async function fetchlist(params) {
    return request(`/acd/calls/page?${stringify(params)}`);
  }

  export async function addObject(obj) {
    return request('/acd/calls/', {
      method: 'POST',
      body: obj
    });
  }
  
  export async function deleteObject(id) {
    return request(`/acd/calls/${id}`, {
      method: 'DELETE',
      expirys: false,
    });
  }
  
  export async function getObject(id) {
    return request(`/acd/calls/info/${id}`, {
      method: 'GET',
      expirys: false,
    });
  }
  
  export async function updateObject(obj) {
    return request(`/acd/calls`, {
      method: 'PUT',
      body: obj
    });
  }

  export async function fetchCallStatus(obj) {
    return request(`/acd/calls/status`);
  }