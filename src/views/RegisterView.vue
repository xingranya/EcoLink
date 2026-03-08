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
        <h2 class="mb-4 text-5xl font-black leading-tight text-white">加入绿色生活</h2>
        <p class="max-w-md text-lg leading-relaxed text-white/80">成为 EcoLink 会员，享受田间直送的新鲜有机食材，开启健康生活新篇章。</p>
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

        <h1 class="mb-2 text-3xl font-black text-slate-900">创建账号</h1>
        <p class="mb-8 text-sm text-slate-500">注册成为会员，享受专属优惠</p>

        <form class="space-y-4" @submit.prevent="submit">
          <label class="block">
            <span class="mb-1.5 block text-sm font-medium text-slate-700">用户名</span>
            <div class="relative">
              <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">person</span>
              <input v-model.trim="form.username" required minlength="4" placeholder="请输入用户名" class="input-control !pl-10" aria-label="注册用户名" />
            </div>
          </label>
          <label class="block">
            <span class="mb-1.5 block text-sm font-medium text-slate-700">密码</span>
            <div class="relative">
              <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">lock</span>
              <input v-model.trim="form.password" required type="password" minlength="6" placeholder="请输入密码（至少6位）" class="input-control !pl-10" aria-label="注册密码" />
            </div>
          </label>
          <label class="block">
            <span class="mb-1.5 block text-sm font-medium text-slate-700">昵称</span>
            <div class="relative">
              <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">badge</span>
              <input v-model.trim="form.nickname" required placeholder="您的昵称" class="input-control !pl-10" aria-label="昵称" />
            </div>
          </label>
          <label class="block">
            <span class="mb-1.5 block text-sm font-medium text-slate-700">手机号 <span class="font-normal text-slate-400">（可选）</span></span>
            <div class="relative">
              <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">phone_iphone</span>
              <input v-model.trim="form.phone" placeholder="请输入手机号" class="input-control !pl-10" aria-label="手机号" />
            </div>
          </label>
          <p v-if="errorMessage" role="alert" class="rounded-xl bg-red-50 px-4 py-2.5 text-xs text-red-600">{{ errorMessage }}</p>
          <button class="btn btn-primary w-full !py-3 text-base" :disabled="loading">
            {{ loading ? '注册中...' : '注册并登录' }}
            <span v-if="!loading" class="material-symbols-outlined text-lg">arrow_forward</span>
          </button>
        </form>

        <p class="mt-6 text-center text-sm text-slate-500">
          已有账号?
          <RouterLink to="/login" class="font-bold text-primary hover:underline">立即登录</RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const auth = useAuthStore();
const form = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: '',
});
const loading = ref(false);
const errorMessage = ref('');

async function submit() {
  errorMessage.value = '';
  loading.value = true;
  try {
    await auth.register(form);
    router.replace('/');
  } catch (error) {
    errorMessage.value = (error as Error).message;
  } finally {
    loading.value = false;
  }
}
</script>
