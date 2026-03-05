<template>
  <header class="sticky top-0 z-40 border-b border-[#d3e1d6] bg-white/90 backdrop-blur">
    <div class="mx-auto max-w-7xl px-4 lg:px-6">
      <div class="flex items-center justify-between py-3">
        <RouterLink to="/" class="flex items-center gap-2 text-primary">
          <span class="rounded-xl bg-primary/15 p-1.5 text-primary">
            <span class="material-symbols-outlined text-[20px]">eco</span>
          </span>
          <div>
            <p class="text-lg font-black leading-none">EcoLink</p>
            <p class="text-[10px] leading-none text-slate-500">绿色生态农产品</p>
          </div>
        </RouterLink>

        <nav class="hidden items-center gap-2 md:flex">
          <RouterLink v-for="item in navItems" :key="item.to" :to="item.to" class="btn btn-soft !px-3 !py-1.5" :class="linkClass(item.to)">
            {{ item.label }}
          </RouterLink>
        </nav>

        <div class="flex items-center gap-2">
          <RouterLink to="/cart" class="btn btn-outline !px-3 !py-1.5">
            <span class="material-symbols-outlined text-[18px]">shopping_cart</span>
            <span class="hidden sm:inline">购物车</span>
            <span v-if="cart.totalCount" class="rounded-full bg-primary px-1.5 text-[11px] text-white">{{ cart.totalCount }}</span>
          </RouterLink>
          <button class="btn btn-soft !px-2.5 !py-1.5 md:hidden" @click="mobileOpen = !mobileOpen">
            <span class="material-symbols-outlined text-[18px]">{{ mobileOpen ? 'close' : 'menu' }}</span>
          </button>
          <button v-if="auth.isLogin" class="btn btn-outline !px-3 !py-1.5" @click="logout">退出</button>
          <RouterLink v-else to="/login" class="btn btn-primary !px-4 !py-1.5">登录</RouterLink>
        </div>
      </div>

      <nav v-if="mobileOpen" class="mb-3 grid grid-cols-2 gap-2 rounded-2xl border border-[#d2e2d6] bg-[#f8fcf8] p-2 md:hidden">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="btn btn-soft !justify-start !rounded-xl !px-3 !py-2"
          :class="linkClass(item.to)"
          @click="mobileOpen = false"
        >
          {{ item.label }}
        </RouterLink>
      </nav>
    </div>
  </header>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useCartStore } from '@/stores/cart';

const auth = useAuthStore();
const cart = useCartStore();
const router = useRouter();
const route = useRoute();
const mobileOpen = ref(false);
const navItems = [
  { to: '/', label: '首页' },
  { to: '/search', label: '搜索' },
  { to: '/orders', label: '订单' },
  { to: '/profile', label: '个人中心' },
];

function linkClass(path: string) {
  return route.path.startsWith(path) && (path !== '/' || route.path === '/') ? '!bg-primary !text-white' : '';
}

function logout() {
  auth.clearSession();
  cart.clear();
  router.push('/login');
}

onMounted(() => {
  if (auth.isLogin) {
    cart.reload().catch(() => undefined);
  }
});

watch(
  () => route.path,
  () => {
    mobileOpen.value = false;
  },
);
</script>
