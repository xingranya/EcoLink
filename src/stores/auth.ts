import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import type { UserMe } from '@/types/api';
import { authApi } from '@/api';

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('ecolink_token') || '');
  const user = ref<UserMe | null>(null);

  const isLogin = computed(() => Boolean(token.value));

  function setSession(newToken: string, newUser: UserMe) {
    token.value = newToken;
    user.value = newUser;
    localStorage.setItem('ecolink_token', newToken);
  }

  function clearSession() {
    token.value = '';
    user.value = null;
    localStorage.removeItem('ecolink_token');
  }

  async function login(username: string, password: string) {
    const result = await authApi.login({ username, password });
    setSession(result.token, result.user);
  }

  async function register(payload: { username: string; password: string; nickname: string; phone?: string }) {
    const result = await authApi.register(payload);
    setSession(result.token, result.user);
  }

  async function fetchMe() {
    if (!token.value) return;
    user.value = await authApi.me();
  }

  return {
    token,
    user,
    isLogin,
    login,
    register,
    fetchMe,
    clearSession,
  };
});
