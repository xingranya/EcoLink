<template>
  <div class="flex min-h-screen">
    <div class="relative hidden w-1/2 overflow-hidden lg:block">
      <img
        class="h-full w-full object-cover"
        src="https://img1.tucang.cc/api/image/show/5195d2cb08996239e9829edb4c9579a9"
        alt="生态有机农场"
        loading="eager"
      />
      <div class="absolute inset-0 bg-gradient-to-br from-primary/80 via-primary/50 to-transparent"></div>
      <div class="absolute inset-0 flex flex-col justify-end p-12">
        <h2 class="mb-4 text-5xl font-black leading-tight text-white">从田间到餐桌</h2>
        <p class="max-w-md text-lg leading-relaxed text-white/80">连接新鲜，共享绿色生态农业成果。为您提供最纯正的有机食材。</p>
      </div>
    </div>

    <div class="flex w-full items-center justify-center bg-white px-6 py-12 lg:w-1/2">
      <div class="w-full max-w-md">
        <div class="mb-8 flex items-center gap-3">
          <div class="flex size-10 items-center justify-center rounded-xl bg-primary text-white">
            <span class="material-symbols-outlined">eco</span>
          </div>
          <span class="text-xl font-bold text-primary">EcoLink</span>
        </div>

        <h1 class="mb-2 text-3xl font-black text-slate-900">欢迎回来</h1>
        <p class="mb-8 text-sm text-slate-500">请输入您的账号信息以登录</p>

        <form class="space-y-5" @submit.prevent="submit">
          <label class="block">
            <span class="mb-1.5 block text-sm font-medium text-slate-700">用户名</span>
            <div class="relative">
              <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">person</span>
              <input v-model.trim="username" required placeholder="请输入用户名" class="input-control !pl-10" aria-label="用户名" />
            </div>
          </label>
          <label class="block">
            <span class="mb-1.5 block text-sm font-medium text-slate-700">密码</span>
            <div class="relative">
              <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">lock</span>
              <input v-model.trim="password" required type="password" placeholder="请输入密码" class="input-control !pl-10" aria-label="密码" />
            </div>
          </label>

          <div class="flex items-center justify-between text-sm">
            <label class="flex cursor-pointer items-center gap-2 text-slate-600">
              <input type="checkbox" class="h-4 w-4 rounded border-slate-300 text-primary focus:ring-primary" />
              记住我
            </label>
            <a href="#" class="font-medium text-primary hover:underline">忘记密码?</a>
          </div>

          <p v-if="errorMessage" role="alert" class="rounded-xl bg-red-50 px-4 py-2.5 text-xs text-red-600">{{ errorMessage }}</p>

          <button class="btn btn-primary w-full !py-3 text-base" :disabled="loading">
            {{ loading ? '登录中...' : '立即登录' }}
            <span v-if="!loading" class="material-symbols-outlined text-lg">arrow_forward</span>
          </button>
        </form>

        <div class="my-6 flex items-center gap-4">
          <div class="h-px flex-1 bg-slate-200"></div>
          <span class="text-xs text-slate-400">或者通过</span>
          <div class="h-px flex-1 bg-slate-200"></div>
        </div>

        <button class="flex w-full items-center justify-center gap-2 rounded-xl border border-slate-200 py-2.5 text-sm font-medium text-slate-600 transition-colors hover:bg-slate-50">
          <svg class="h-5 w-5 text-green-500" viewBox="0 0 24 24" fill="currentColor"><path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.535c0 2.472 1.339 4.668 3.437 6.078l-.857 3.143 3.633-1.951c.774.188 1.591.303 2.438.303.199 0 .394-.012.589-.023-.123-.427-.198-.872-.198-1.333 0-3.677 3.388-6.657 7.567-6.657.254 0 .504.017.752.041C16.733 5.022 13.07 2.188 8.691 2.188zm-3.11 4.5c.558 0 1.008.45 1.008 1.008 0 .558-.45 1.008-1.008 1.008-.558 0-1.008-.45-1.008-1.008 0-.558.45-1.008 1.008-1.008zm6.239 0c.558 0 1.008.45 1.008 1.008 0 .558-.45 1.008-1.008 1.008-.558 0-1.008-.45-1.008-1.008 0-.558.45-1.008 1.008-1.008zM16.609 9.752c-3.627 0-6.573 2.623-6.573 5.855 0 3.233 2.946 5.856 6.573 5.856.754 0 1.477-.117 2.159-.33l2.809 1.575-.66-2.43c1.766-1.213 2.891-3.067 2.891-5.147-.001-2.781-3.249-5.379-7.199-5.379zm-2.498 3.674c.458 0 .832.374.832.832a.834.834 0 01-.832.832.834.834 0 01-.832-.832c0-.458.374-.832.832-.832zm4.997 0c.458 0 .832.374.832.832a.834.834 0 01-.832.832.834.834 0 01-.832-.832c0-.458.374-.832.832-.832z" /></svg>
          微信账号登录
        </button>

        <p class="mt-6 text-center text-sm text-slate-500">
          还没有账号?
          <RouterLink to="/register" class="font-bold text-primary hover:underline">立即注册</RouterLink>
        </p>

        <div class="mt-6 flex items-start gap-2 rounded-xl bg-primary/5 p-3">
          <span class="material-symbols-outlined mt-0.5 text-sm text-primary">info</span>
          <div class="text-xs text-slate-600">
            <p class="font-bold">演示账号</p>
            <p>账号: demo / 密码: 123456</p>
          </div>
        </div>
      </div>
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
const errorMessage = ref('');

async function submit() {
  errorMessage.value = '';
  loading.value = true;
  try {
    await auth.login(username.value, password.value);
    const redirect = (route.query.redirect as string) || '/';
    router.replace(redirect);
  } catch (error) {
    errorMessage.value = (error as Error).message;
  } finally {
    loading.value = false;
  }
}
</script>
