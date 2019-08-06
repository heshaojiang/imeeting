import request from '@/utils/request';


export async function queryCurrent(id) {
    return request(`/admin/user/info`, {
      method: 'GET'
    });
}