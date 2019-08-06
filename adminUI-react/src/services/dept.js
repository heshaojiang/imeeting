import { stringify } from 'qs';
import request from '@/utils/request';

export function fetchTree(params) {
  return request(`/admin/dept/tree?${stringify(params)}`, {
    method: 'GET',
  });
}