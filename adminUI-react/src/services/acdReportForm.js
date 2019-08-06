import { stringify } from 'qs';
import request from '@/utils/request';

export async function fetchSessionTotal(params) {
  return request(`/acd/report/receivecall?${stringify(params)}`);
}

export async function fetchCallPlatform() {
  return request(`/acd/report/callplatform`);
}

export async function fetchResultSolve() {
  return request(`/acd/report/resultsolve`);
}

export async function fetchSatisfly() {
  return request(`/acd/report/satistify`);
}

export async function fetchAgentInfo(params) {
  return request(`/acd/report/agentinfo?${stringify(params)}`);
}
