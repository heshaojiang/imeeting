import { stringify } from 'qs';
import request from "@/utils/request";
import { getLocale } from 'umi/locale';

export function fetchDeptRoleList(deptId) {
  return request(`/admin/role/roleList/${deptId}`, {
    method: 'GET'
  });
}

export async function fetchlist(params) {
  return request(`/admin/role/sysRolePage?${stringify(params)}`);
}

export async function fetchSysRolelist() {
    const locale = getLocale();
    return request(`/admin/role/getSysRoleList/${locale}`);
  }
  

export async function addObject(obj) {
return request('/admin/role/admin', {
    method: 'POST',
    body: obj
  });
}

export async function deleteObject(id) {
return request(`/admin/role/${id}`, {
    method: 'DELETE',
    expirys: false,
  });
}

export async function getObject(id) {
return request(`/admin/role/${id}`, {
    method: 'GET',
    expirys: false,
  });
}

export async function updateObject(obj) {
return request(`/admin/role`, {
    method: 'PUT',
    body: obj
  });
}