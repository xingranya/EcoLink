<template>
  <div class="min-h-screen bg-background-light">
    <AppHeader />
    <main class="flex-grow">
      <section class="bg-primary/5 py-6">
        <div class="mx-auto max-w-4xl px-4">
          <div class="flex overflow-hidden rounded-xl bg-white shadow-lg shadow-primary/5">
            <div class="hidden items-center gap-2 border-r border-primary/10 px-4 py-3 sm:flex">
              <span class="text-sm font-medium text-slate-600">分类</span>
              <span class="material-symbols-outlined text-slate-400">arrow_drop_down</span>
            </div>
            <div class="flex flex-1 items-center px-4">
              <span class="material-symbols-outlined mr-2 text-slate-400">search</span>
              <input
                v-model.trim="keyword"
                class="w-full border-none bg-transparent py-3 text-sm focus:ring-0"
                placeholder="搜索新鲜有机蔬菜、当季水果、原产地特产..."
                type="text"
                @keydown.enter="goSearch"
              />
            </div>
            <button class="bg-primary px-6 text-sm font-bold text-white transition-all hover:bg-primary/90 sm:px-8" @click="goSearch">搜索</button>
          </div>
        </div>
      </section>

      <section class="relative h-[460px] w-full overflow-hidden">
        <div class="absolute inset-0 z-10 bg-gradient-to-r from-black/60 to-transparent"></div>
        <img
          class="h-full w-full object-cover"
          src="https://lh3.googleusercontent.com/aida-public/AB6AXuAyib461gtOaKEowAswTd0JbboU5fyBuB64WgYgVREdDUI4GH0utthvMaTEtsGwp35Wzr7P7SGeFVhvMyj_Vg3gtiMSbq__6hQ2HTMPBtKGHbs5voz1dx4uRqlG65s4lHNbHaWxpWHOZm5LsfE8NqCz6WWNZRS7p4EoLn1hRGFsF9IG2Nf_8eW7baYgzFl8Qlaop4le3BjU5EZVad6NUNMckza3-Pj3YbzQuKX4Jc-a1sbd7WQmroVWYEkw0adpmdjBby6qYnhNSCjC"
          alt="生态农场"
        />
        <div class="absolute inset-0 z-20 flex flex-col justify-center px-8 sm:px-20">
          <div class="max-w-2xl">
            <span class="mb-4 inline-block rounded-full bg-primary/30 px-4 py-1 text-sm font-semibold text-white backdrop-blur-md">100% 纯天然·生态种植</span>
            <h2 class="mb-6 text-5xl font-black text-white sm:text-6xl">从田间到餐桌</h2>
            <p class="mb-8 text-lg leading-relaxed text-slate-100/90">
              为您甄选全国各地优质原产地，拒绝化学农药，让每一口食材都回归自然本味。今日下单，新鲜直达。
            </p>
            <div class="flex gap-4">
              <RouterLink to="/search" class="rounded-lg bg-primary px-8 py-3 text-base font-bold text-white shadow-xl shadow-primary/20 transition-transform hover:scale-105">
                立即选购
              </RouterLink>
              <RouterLink to="/profile" class="rounded-lg border border-white/20 bg-white/10 px-8 py-3 text-base font-bold text-white backdrop-blur-md transition-all hover:bg-white/20">
                了解溯源
              </RouterLink>
            </div>
          </div>
        </div>
      </section>

      <section class="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
        <div class="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-6">
          <RouterLink
            v-for="item in categoryCards"
            :key="item.name"
            :to="item.to"
            class="group flex flex-col items-center gap-3 rounded-2xl bg-white p-6 shadow-sm transition-all hover:-translate-y-1 hover:shadow-md"
          >
            <div class="flex size-14 items-center justify-center rounded-full transition-transform group-hover:scale-110" :class="item.iconClass">
              <span class="material-symbols-outlined text-3xl">{{ item.icon }}</span>
            </div>
            <span class="font-bold text-slate-700">{{ item.name }}</span>
          </RouterLink>
        </div>
      </section>

      <section class="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8">
        <div class="mb-8 flex items-end justify-between gap-3">
          <div>
            <h2 class="text-3xl font-black tracking-tight">新品推荐</h2>
            <p class="mt-2 text-sm text-slate-500">本周新鲜上架的生态好物</p>
          </div>
          <RouterLink to="/search" class="flex items-center text-sm font-bold text-primary hover:underline">
            查看更多
            <span class="material-symbols-outlined text-base">chevron_right</span>
          </RouterLink>
        </div>

        <div v-if="loading" class="rounded-2xl bg-white p-8 text-center text-sm text-slate-500">正在加载商品...</div>
        <div v-else class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
          <article
            v-for="item in products.slice(0, 8)"
            :key="item.id"
            class="overflow-hidden rounded-2xl border border-primary/10 bg-white shadow-sm transition-all hover:-translate-y-1 hover:shadow-md"
          >
            <RouterLink :to="`/product/${item.id}`" class="relative block h-48 overflow-hidden">
              <img :src="item.mainImage || fallbackImage" :alt="item.name" class="h-full w-full object-cover transition-transform duration-500 hover:scale-110" />
              <span class="absolute left-3 top-3 rounded bg-primary px-2 py-1 text-[10px] font-bold text-white">NEW</span>
            </RouterLink>
            <div class="p-4">
              <RouterLink :to="`/product/${item.id}`" class="line-clamp-1 text-lg font-bold text-slate-900 hover:text-primary">{{ item.name }}</RouterLink>
              <p class="mt-1 line-clamp-1 text-sm text-slate-500">{{ item.subtitle || '生态优选' }}</p>
              <div class="mt-3 flex items-center justify-between">
                <span class="text-xl font-black text-primary">¥{{ Number(item.price).toFixed(2) }}</span>
                <button class="rounded-full bg-primary/10 p-2 text-primary transition-colors hover:bg-primary hover:text-white" @click="addCart(item.id)">
                  <span class="material-symbols-outlined text-base">add_shopping_cart</span>
                </button>
              </div>
            </div>
          </article>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import AppHeader from '@/components/AppHeader.vue';
import { cartApi, productApi } from '@/api';
import { useCartStore } from '@/stores/cart';
import type { Category, ProductItem } from '@/types/api';

const router = useRouter();
const cart = useCartStore();
const categories = ref<Category[]>([]);
const products = ref<ProductItem[]>([]);
const loading = ref(false);
const keyword = ref('');
const fallbackImage = 'https://images.unsplash.com/photo-1608797178974-15b35a64ede9?auto=format&fit=crop&w=1200&q=80';

const categoryIconMap: Record<string, { icon: string; iconClass: string }> = {
  新鲜瓜果: { icon: 'nutrition', iconClass: 'bg-red-100 text-red-600' },
  时令蔬菜: { icon: 'leaf_spark', iconClass: 'bg-green-100 text-green-600' },
  肉禽蛋奶: { icon: 'egg', iconClass: 'bg-amber-100 text-amber-600' },
  地方特产: { icon: 'location_on', iconClass: 'bg-blue-100 text-blue-600' },
  优质粮油: { icon: 'grain', iconClass: 'bg-yellow-100 text-yellow-700' },
  茶饮冲调: { icon: 'local_cafe', iconClass: 'bg-emerald-100 text-emerald-700' },
};

const categoryCards = computed(() =>
  categories.value.map((item) => {
    const iconConf = categoryIconMap[item.name] || { icon: 'storefront', iconClass: 'bg-slate-100 text-slate-600' };
    return {
      ...iconConf,
      name: item.name,
      to: `/search?categoryId=${item.id}`,
    };
  }),
);

async function loadData() {
  loading.value = true;
  try {
    [categories.value, products.value] = await Promise.all([
      productApi.categories(),
      productApi.list({ page: 1, size: 12, sort: 'latest' }).then((res) => res.list),
    ]);
  } finally {
    loading.value = false;
  }
}

function goSearch() {
  router.push({ path: '/search', query: keyword.value ? { keyword: keyword.value } : {} });
}

async function addCart(productId: number) {
  try {
    await cartApi.add({ productId, quantity: 1 });
    cart.reload().catch(() => undefined);
    alert('已加入购物车');
  } catch (error) {
    alert((error as Error).message);
  }
}

onMounted(() => {
  loadData().catch((error) => alert((error as Error).message));
});
</script>
