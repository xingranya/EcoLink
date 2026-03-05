<template>
  <div class="auth-shell">
    <div class="auth-card">
      <div class="mb-4 text-center">
        <p class="text-xs uppercase tracking-[0.3em] text-slate-500">EcoLink</p>
        <h1 class="mt-2 text-3xl font-black">创建账号</h1>
      </div>
      <form class="mt-6 space-y-4" @submit.prevent="submit">
        <input v-model.trim="form.username" required minlength="4" placeholder="用户名" class="input-control" />
        <input v-model.trim="form.password" required type="password" minlength="6" placeholder="密码" class="input-control" />
        <input v-model.trim="form.nickname" required placeholder="昵称" class="input-control" />
        <input v-model.trim="form.phone" placeholder="手机号（可选）" class="input-control" />
        <button class="btn btn-primary w-full !py-2.5" :disabled="loading">{{ loading ? '注册中...' : '注册并登录' }}</button>
      </form>
      <RouterLink to="/login" class="mt-4 block text-center text-sm text-primary hover:underline">已有账号？去登录</RouterLink>
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

async function submit() {
  loading.value = true;
  try {
    await auth.register(form);
    router.replace('/');
  } catch (error) {
    alert((error as Error).message);
  } finally {
    loading.value = false;
  }
}
</script>
