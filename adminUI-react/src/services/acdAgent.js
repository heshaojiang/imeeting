import { stringify } from 'qs';
import request from '@/utils/request';

export async function fetchlist(params) {
  return request(`/acd/agent/page?${stringify(params)}`);
}
export async function fetchAgentStatus() {
  return request(`/acd/agent/status`);
}
export async function fetchMeetingUsers(params) {
  return request(`/admin/user/imeetingUserPage?${stringify(params)}`);
}

export async function fetchAgentQueue(agentId) {
  return request(`/acd/agent/${agentId}/queue`);
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
  return request(`/acd/agent`, {
    method: 'PUT',
    body: obj
  });
}

export async function updateStatus(obj) {
  return request(`/acd/agent/changeStatus`, {
    method: 'PUT',
    body: obj
  });
}

export async function fetchStatus(obj) {
  return request(`/acd/agent/status`);
}
