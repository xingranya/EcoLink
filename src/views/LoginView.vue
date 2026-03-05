<template>
  <div class="auth-shell">
    <div class="auth-card">
      <div class="mb-4 text-center">
        <p class="text-xs uppercase tracking-[0.3em] text-slate-500">EcoLink</p>
        <h1 class="mt-2 text-3xl font-black">登录账号</h1>
      </div>
      <form class="mt-6 space-y-4" @submit.prevent="submit">
        <input v-model.trim="username" required placeholder="用户名（demo）" class="input-control" />
        <input v-model.trim="password" required type="password" placeholder="密码（123456）" class="input-control" />
        <button class="btn btn-primary w-full !py-2.5" :disabled="loading">{{ loading ? '登录中...' : '登录' }}</button>
      </form>
      <RouterLink to="/register" class="mt-4 block text-center text-sm text-primary hover:underline">没有账号？去注册</RouterLink>
      <RouterLink to="/" class="mt-2 block text-center text-sm text-slate-500 hover:underline">返回首页</RouterLink>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const route = useRoute();
const auth = useAuthStore();
const username = ref('demo');
const password = ref('123456');
const loading = ref(false);

async function submit() {
  loading.value = true;
  try {
    await auth.login(username.value, password.value);
    const redirect = (route.query.redirect as string) || '/';
    router.replace(redirect);
  } catch (error) {
    alert((error as Error).message);
  } finally {
    loading.value = false;
  }
}
</script>
