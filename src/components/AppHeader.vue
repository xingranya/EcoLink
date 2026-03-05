<template>
  <header class="sticky top-0 z-50 w-full border-b border-primary/10 bg-white/80 backdrop-blur-md">
    <div class="mx-auto flex max-w-7xl items-center justify-between px-4 py-3 sm:px-6 lg:px-8">
      <div class="flex items-center gap-3">
        <div class="flex size-10 items-center justify-center rounded-lg bg-primary text-white">
          <span class="material-symbols-outlined text-2xl">eco</span>
        </div>
        <RouterLink to="/" class="text-base font-bold tracking-tight text-primary sm:text-xl">绿色生态农产品销售系统</RouterLink>
      </div>

      <nav class="hidden items-center gap-8 md:flex">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="rounded px-1 py-0.5 text-sm font-medium transition-colors hover:text-primary"
          :class="linkClass(item.to)"
        >
          {{ item.label }}
        </RouterLink>
      </nav>

      <div class="flex items-center gap-2 sm:gap-3">
        <div class="hidden items-center gap-2 rounded-full bg-primary/10 px-3 py-1 text-primary sm:flex">
          <span class="material-symbols-outlined text-sm">wb_sunny</span>
          <span class="text-xs font-semibold">24°C 晴空</span>
        </div>

        <RouterLink to="/cart" class="relative rounded-full p-2 transition-colors hover:bg-primary/10">
          <span class="material-symbols-outlined">shopping_cart</span>
          <span
            v-if="cart.totalCount"
            class="absolute right-1 top-1 flex size-4 items-center justify-center rounded-full bg-primary text-[10px] text-white"
          >
            {{ cart.totalCount > 99 ? '99+' : cart.totalCount }}
          </span>
        </RouterLink>

        <RouterLink
          :to="auth.isLogin ? '/profile' : '/login'"
          class="flex h-8 w-8 items-center justify-center overflow-hidden rounded-full border-2 border-primary/20 bg-primary/10 text-primary"
        >
          <span class="material-symbols-outlined text-base">{{ auth.isLogin ? 'person' : 'login' }}</span>
        </RouterLink>

        <button v-if="auth.isLogin" class="hidden rounded-lg bg-primary/10 px-3 py-1.5 text-xs font-bold text-primary sm:block" @click="logout">
          退出
        </button>

        <button class="rounded p-1 text-primary md:hidden" @click="mobileOpen = !mobileOpen">
          <span class="material-symbols-outlined">{{ mobileOpen ? 'close' : 'menu' }}</span>
        </button>
      </div>
    </div>

    <nav v-if="mobileOpen" class="border-t border-primary/10 bg-white px-4 py-3 md:hidden">
      <div class="grid grid-cols-2 gap-2">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="rounded-lg px-3 py-2 text-sm font-medium"
          :class="linkClass(item.to)"
          @click="mobileOpen = false"
        >
          {{ item.label }}
        </RouterLink>
      </div>
    </nav>
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
  { to: '/search', label: '全部分类' },
  { to: '/orders', label: '我的订单' },
  { to: '/profile', label: '会员中心' },
];

function linkClass(path: string) {
  const active = route.path.startsWith(path) && (path !== '/' || route.path === '/');
  return active ? 'bg-primary/10 text-primary font-bold' : 'text-slate-700';
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
