<template>
  <header class="sticky top-0 z-50 w-full border-b border-primary/5 bg-white/90 backdrop-blur-xl">
    <div class="mx-auto flex max-w-7xl items-center justify-between px-4 py-3 sm:px-6 lg:px-8">
      <div class="flex items-center gap-3">
        <div class="flex size-10 items-center justify-center rounded-xl bg-primary text-white shadow-md shadow-primary/20">
          <span class="material-symbols-outlined text-2xl">eco</span>
        </div>
        <RouterLink to="/" class="font-bold tracking-tight text-primary">
          <span class="text-base sm:hidden">EcoLink</span>
          <span class="hidden text-xl sm:inline">EcoLink · 绿色生态</span>
        </RouterLink>
      </div>

      <nav class="hidden items-center gap-1 md:flex">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="rounded-lg px-3 py-1.5 text-sm font-semibold transition-all"
          :class="linkClass(item.to)"
        >
          {{ item.label }}
        </RouterLink>
      </nav>

      <div class="flex items-center gap-2 sm:gap-3">
        <RouterLink
          v-if="auth.isAdmin"
          to="/admin/dashboard"
          class="hidden items-center gap-1 rounded-xl bg-primary/10 px-3 py-1.5 text-xs font-bold text-primary transition-colors hover:bg-primary/20 lg:inline-flex"
        >
          <span class="material-symbols-outlined text-sm">admin_panel_settings</span>
          后台
        </RouterLink>

        <RouterLink to="/cart" class="relative rounded-xl p-2 transition-colors hover:bg-primary/10" aria-label="购物车">
          <span class="material-symbols-outlined">shopping_cart</span>
          <span
            v-if="cart.totalCount"
            class="absolute -right-0.5 -top-0.5 flex size-5 items-center justify-center rounded-full bg-orange-500 text-[10px] font-bold text-white shadow-sm"
          >
            {{ cart.totalCount > 99 ? '99+' : cart.totalCount }}
          </span>
        </RouterLink>

        <RouterLink
          :to="auth.isLogin ? '/profile' : '/login'"
          class="flex h-9 w-9 items-center justify-center overflow-hidden rounded-xl border-2 border-primary/20 bg-primary/10 text-primary transition-all hover:border-primary/40 hover:bg-primary/20"
          :aria-label="auth.isLogin ? '个人中心' : '去登录'"
        >
          <span class="material-symbols-outlined text-lg">{{ auth.isLogin ? 'person' : 'login' }}</span>
        </RouterLink>

        <button v-if="auth.isLogin" class="hidden rounded-xl bg-primary/10 px-3 py-1.5 text-xs font-bold text-primary transition-colors hover:bg-primary/20 sm:block" @click="logout">
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
        <RouterLink
          v-if="auth.isAdmin"
          to="/admin/dashboard"
          class="rounded-lg bg-primary/10 px-3 py-2 text-sm font-bold text-primary"
          @click="mobileOpen = false"
        >
          进入后台
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
