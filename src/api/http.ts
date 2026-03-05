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

async function request<T>(method: 'get' | 'post' | 'put' | 'delete', url: string, data?: unknown, params?: unknown): Promise<T> {
  try {
    const response = await client.request<ApiResponse<T>>({ method, url, data, params });
    const body = response.data;
    if (body.code !== 0) {
      throw new Error(body.message || '请求失败');
    }
    return body.data;
  } catch (error: unknown) {
    const axiosError = error as { response?: { data?: { message?: string } }; message?: string };
    const hasResponse = Boolean(axiosError?.response);
    const useMock = import.meta.env.VITE_ENABLE_MOCK !== 'false';

    if (!hasResponse && useMock) {
      return mockRequest<T>(method, url, data, params);
    }

    const message = axiosError?.response?.data?.message || axiosError?.message || '请求失败';
    throw new Error(message);
  }
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
