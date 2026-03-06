import axios from 'axios';
import type { ApiResponse } from '@/types/api';
import { mockRequest } from './mock';

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1',
  timeout: 15000,
});

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('ecolink_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      handleAuthExpired();
    }
    return Promise.reject(error);
  },
);

function handleAuthExpired() {
  localStorage.removeItem('ecolink_token');
  if (window.location.pathname !== '/login') {
    window.location.href = '/login';
  }
}

function isAxiosNetworkError(error: unknown): boolean {
  const err = error as { code?: string; response?: unknown };
  if (err?.code === 'ERR_NETWORK' || err?.code === 'ECONNABORTED') return true;
  if (!err?.response && err?.code === 'ECONNREFUSED') return true;
  return false;
}

async function request<T>(method: 'get' | 'post' | 'put' | 'delete', url: string, data?: unknown, params?: unknown): Promise<T> {
  const useMock = import.meta.env.VITE_ENABLE_MOCK !== 'false';

  let response;
  try {
    response = await client.request<ApiResponse<T>>({ method, url, data, params });
  } catch (networkError: unknown) {
    if (isAxiosNetworkError(networkError) && useMock) {
      return mockRequest<T>(method, url, data, params);
    }
    const axiosErr = networkError as { response?: { data?: { message?: string }; status?: number }; message?: string };
    const message = axiosErr?.response?.data?.message || axiosErr?.message || '请求失败';
    throw new Error(message);
  }

  const body = response.data;
  if (body.code !== 0) {
    if (body.code === 4010) {
      handleAuthExpired();
    }
    throw new Error(body.message || '请求失败');
  }
  return body.data;
}

const http = {
  get<T>(url: string, params?: unknown) {
    return request<T>('get', url, undefined, params);
  },
  post<T>(url: string, data?: unknown) {
    return request<T>('post', url, data);
  },
  put<T>(url: string, data?: unknown) {
    return request<T>('put', url, data);
  },
  delete<T>(url: string) {
    return request<T>('delete', url);
  },
};

export default http;
