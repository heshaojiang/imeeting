import { stringify } from 'qs';
import request from '@/utils/request';
export async function fetchlist(params) {
  return request(`/admin/bizCustomer/page?${stringify(params)}`);
}

export async function fetchCustomerlist(params) {
    return request(`/admin/bizCustomer/select`);
}

export async function addObject(obj) {
  return request('/admin/bizCustomer/', {
    method: 'POST',
    body: obj
  });
}

export async function deleteObject(id) {
  return request(`/admin/bizCustomer/${id}`, {
    method: 'DELETE',
    expirys: false,
  });
}

export async function getObject(id) {
  return request(`/admin/bizCustomer/${id}`, {
    method: 'GET',
    expirys: false,
  });
}

export async function updateObject(obj) {
  return request(`/admin/bizCustomer`, {
    method: 'PUT',
    body: obj
  });
}
