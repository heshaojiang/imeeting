import request from '@/utils/request';

export async function getMachineCode() {
  return request(`/admin/license/getmachinecode`);
}

export async function getLicenseInfo() {
  return request(`/admin/license/getlicenseinfo`);
}

