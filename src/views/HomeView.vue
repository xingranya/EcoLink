<template>
  <div class="flex flex-1 flex-col bg-background-light">
    <main class="flex-grow">
      <section class="px-4 pt-4 sm:px-6 lg:px-8 lg:pt-6">
        <div class="relative mx-auto h-[380px] max-w-7xl overflow-hidden rounded-2xl shadow-lg lg:h-[460px] lg:rounded-3xl lg:shadow-2xl">
          <div class="absolute inset-0 z-10 bg-gradient-to-r from-black/60 to-transparent"></div>
          <img
            class="h-full w-full object-cover"
            src="https://img1.tucang.cc/api/image/show/5195d2cb08996239e9829edb4c9579a9"
            alt="生态农场"
            loading="eager"
          />
          <div class="absolute inset-0 z-20 flex flex-col justify-center px-8 sm:px-16 lg:px-24">
            <div class="max-w-xl">
              <span class="mb-3 inline-block rounded-full bg-primary/30 px-4 py-1 text-xs font-semibold text-white backdrop-blur-md">100% 纯天然·生态种植</span>
              <h2 class="mb-3 text-4xl font-black text-white lg:text-5xl">从田间到餐桌</h2>
              <p class="mb-5 text-base leading-relaxed text-slate-100/90">
                为您甄选全国各地优质原产地，拒绝化学农药，让每一口食材都回归自然本味。
              </p>
              <div class="mb-5 flex max-w-lg overflow-hidden rounded-xl bg-white/95 shadow-xl backdrop-blur-sm">
                <div class="flex flex-1 items-center px-4">
                  <span class="material-symbols-outlined mr-2 text-slate-400">search</span>
                  <input
                    v-model.trim="keyword"
                    class="w-full border-none bg-transparent py-3 text-sm text-slate-800 placeholder-slate-400 focus:ring-0"
                    placeholder="搜索有机蔬菜、当季水果、原产地特产..."
                    type="text"
                    aria-label="首页商品搜索"
                    @keydown.enter="goSearch"
                  />
                </div>
                <button class="bg-primary px-6 text-sm font-bold text-white transition-colors hover:bg-primary/90" @click="goSearch">搜索</button>
              </div>
              <div class="flex gap-3">
                <RouterLink to="/search" class="rounded-lg bg-primary px-6 py-2.5 text-sm font-bold text-white shadow-lg shadow-primary/20 transition-transform hover:scale-105">
                  立即选购
                </RouterLink>
                <RouterLink to="/profile" class="rounded-lg border border-white/20 bg-white/10 px-6 py-2.5 text-sm font-bold text-white backdrop-blur-md transition-all hover:bg-white/20">
                  了解溯源
                </RouterLink>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
        <div class="grid grid-cols-3 gap-3 sm:grid-cols-6">
          <RouterLink
            v-for="item in categoryCards"
            :key="item.name"
            :to="item.to"
            class="group flex flex-col items-center gap-2 rounded-xl bg-white p-4 shadow-sm transition-all hover:-translate-y-0.5 hover:shadow-md"
          >
            <div class="flex size-12 items-center justify-center rounded-full transition-transform group-hover:scale-110" :class="item.iconClass">
              <span class="material-symbols-outlined text-2xl">{{ item.icon }}</span>
            </div>
            <span class="text-sm font-bold text-slate-700">{{ item.name }}</span>
          </RouterLink>
        </div>
      </section>

      <section class="mx-auto max-w-7xl px-4 pb-2 sm:px-6 lg:px-8">
        <div class="grid gap-4 lg:grid-cols-[1.2fr_0.8fr]">
          <article class="rounded-[2rem] border border-primary/10 bg-white p-6 shadow-sm">
            <p class="text-xs font-bold uppercase tracking-[0.28em] text-primary/60">生态鲜选</p>
            <h2 class="mt-3 text-2xl font-black tracking-tight text-slate-900 lg:text-3xl">把新鲜、安心和产地风味带回餐桌</h2>
            <p class="mt-3 max-w-3xl text-sm leading-7 text-slate-600">
              从应季蔬果到地方特产，EcoLink 按原产地、时令与品质标准甄选商品，让日常采购更省心，也让每一份食材来源更清楚。
            </p>

            <div class="mt-5 grid gap-4 sm:grid-cols-2">
              <article
                v-for="highlight in homeHighlights"
                :key="highlight.title"
                class="rounded-3xl border border-slate-200 bg-slate-50 p-5 transition-all hover:-translate-y-0.5 hover:shadow-md"
              >
                <div class="flex items-start justify-between gap-4">
                  <div>
                    <p class="text-xs font-bold uppercase tracking-[0.18em] text-slate-400">{{ highlight.eyebrow }}</p>
                    <h3 class="mt-3 text-lg font-black text-slate-900">{{ highlight.title }}</h3>
                    <p class="mt-2 text-sm leading-6 text-slate-600">{{ highlight.description }}</p>
                  </div>
                  <span class="material-symbols-outlined rounded-2xl bg-white p-3 text-slate-700 shadow-sm">{{ highlight.icon }}</span>
                </div>
                <div class="mt-4 flex flex-wrap gap-2">
                  <span
                    v-for="tag in highlight.tags"
                    :key="tag"
                    class="rounded-full border border-slate-200 bg-white px-3 py-1.5 text-xs font-semibold text-slate-600"
                  >
                    {{ tag }}
                  </span>
                </div>
              </article>
            </div>
          </article>

          <aside class="space-y-4">
            <article class="rounded-[2rem] border border-primary/10 bg-white p-6 shadow-sm">
              <p class="text-xs font-bold uppercase tracking-[0.2em] text-primary/60">逛逛商城</p>
              <div class="mt-4 space-y-3">
                <RouterLink
                  v-for="entry in quickEntries"
                  :key="entry.title"
                  :to="entry.to"
                  class="flex items-start gap-4 rounded-2xl border border-slate-200 bg-slate-50 p-4 transition-all hover:-translate-y-0.5 hover:bg-white hover:shadow-sm"
                >
                  <span class="material-symbols-outlined rounded-2xl bg-white p-3 text-slate-700 shadow-sm">{{ entry.icon }}</span>
                  <div>
                    <p class="text-sm font-bold text-slate-900">{{ entry.title }}</p>
                    <p class="mt-1 text-xs leading-5 text-slate-500">{{ entry.description }}</p>
                  </div>
                </RouterLink>
              </div>
            </article>

            <article class="rounded-[2rem] border border-emerald-200 bg-emerald-50 p-6 shadow-sm">
              <p class="text-xs font-bold uppercase tracking-[0.2em] text-emerald-700/70">购物安心</p>
              <div class="mt-4 space-y-3">
                <div
                  v-for="promise in shoppingPromises"
                  :key="promise.title"
                  class="rounded-2xl bg-white/90 p-4"
                >
                  <p class="text-sm font-bold text-slate-900">{{ promise.title }}</p>
                  <p class="mt-1 text-xs leading-5 text-slate-500">{{ promise.description }}</p>
                </div>
              </div>
            </article>
          </aside>
        </div>
      </section>

      <section class="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
        <div class="mb-8 flex items-end justify-between gap-3">
          <div>
            <h2 class="text-3xl font-black tracking-tight">新品推荐</h2>
            <p class="mt-2 text-sm text-slate-500">本周新鲜上架的生态好物</p>
          </div>
          <RouterLink to="/search" class="flex items-center gap-1 rounded-full bg-primary/10 px-4 py-2 text-sm font-bold text-primary transition-colors hover:bg-primary/20">
            查看更多
            <span class="material-symbols-outlined text-base">arrow_forward</span>
          </RouterLink>
        </div>

        <div v-if="loading" class="flex flex-col items-center gap-3 rounded-2xl bg-white p-12">
          <span class="material-symbols-outlined animate-spin text-3xl text-primary">progress_activity</span>
          <span class="text-sm text-slate-500">正在加载商品...</span>
        </div>
        <div v-else class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
          <article
            v-for="item in products.slice(0, 8)"
            :key="item.id"
            class="group overflow-hidden rounded-2xl border border-primary/5 bg-white shadow-sm transition-all hover:-translate-y-1 hover:shadow-lg"
          >
            <RouterLink :to="`/product/${item.id}`" class="relative block h-52 overflow-hidden">
              <img :src="item.mainImage || fallbackImage" :alt="item.name" class="h-full w-full object-cover transition-transform duration-500 group-hover:scale-110" loading="lazy" />
              <span class="absolute left-3 top-3 rounded-full bg-primary/90 px-3 py-1 text-[10px] font-bold text-white backdrop-blur-sm">NEW</span>
            </RouterLink>
            <div class="p-4">
              <RouterLink :to="`/product/${item.id}`" class="line-clamp-1 text-base font-bold text-slate-900 hover:text-primary">{{ item.name }}</RouterLink>
              <p class="mt-1 line-clamp-1 text-sm text-slate-500">{{ item.subtitle || '生态优选' }}</p>
              <div class="mt-3 flex items-center justify-between">
                <span class="text-xl font-black text-primary">¥{{ Number(item.price).toFixed(2) }}</span>
                <button class="rounded-full bg-primary/10 p-2.5 text-primary transition-all hover:scale-110 hover:bg-primary hover:text-white hover:shadow-md hover:shadow-primary/20" @click="addCart(item.id)">
                  <span class="material-symbols-outlined text-lg">add_shopping_cart</span>
                </button>
              </div>
            </div>
          </article>
        </div>
      </section>

      <section class="bg-primary/5 py-8">
        <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div class="mb-8 flex items-end justify-between gap-3">
            <div>
              <h2 class="text-3xl font-black tracking-tight">地道特产</h2>
              <p class="mt-2 text-sm text-slate-500">来自全国各地的原产地风味</p>
            </div>
            <RouterLink to="/search" class="flex items-center gap-1 rounded-full bg-primary/10 px-4 py-2 text-sm font-bold text-primary transition-colors hover:bg-primary/20">
              更多特产
              <span class="material-symbols-outlined text-base">arrow_forward</span>
            </RouterLink>
          </div>
          <div class="grid grid-cols-1 gap-6 md:grid-cols-3">
            <div class="group relative h-60 overflow-hidden rounded-2xl shadow-md lg:h-72">
              <img src="https://img1.tucang.cc/api/image/show/d2cd87bd4ecd296fba3dc5a1a71339de" alt="云南高原精品水果" class="h-full w-full object-cover transition-transform duration-700 group-hover:scale-110" loading="lazy" />
              <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent"></div>
              <div class="absolute bottom-0 left-0 p-6">
                <span class="mb-2 inline-block rounded-full bg-white/20 px-3 py-1 text-xs font-bold text-white backdrop-blur-sm">云南</span>
                <h3 class="text-2xl font-black text-white">高原精品水果</h3>
                <p class="mt-1 text-sm text-white/80">阳光充足 · 口感甘甜</p>
              </div>
            </div>
            <div class="group relative h-60 overflow-hidden rounded-2xl shadow-md lg:h-72">
              <img src="https://img1.tucang.cc/api/image/show/a050cab6c8b24666cf4fde10b4174ad3" alt="山东烟台苹果" class="h-full w-full object-cover transition-transform duration-700 group-hover:scale-110" loading="lazy" />
              <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent"></div>
              <div class="absolute bottom-0 left-0 p-6">
                <span class="mb-2 inline-block rounded-full bg-white/20 px-3 py-1 text-xs font-bold text-white backdrop-blur-sm">山东</span>
                <h3 class="text-2xl font-black text-white">烟台红富士苹果</h3>
                <p class="mt-1 text-sm text-white/80">个大汁多 · 脆甜可口</p>
              </div>
            </div>
            <div class="group relative h-60 overflow-hidden rounded-2xl shadow-md lg:h-72">
              <img src="https://img1.tucang.cc/api/image/show/97c1195c4235a01ded5b8a399f6f9812" alt="丹东草莓" class="h-full w-full object-cover transition-transform duration-700 group-hover:scale-110" loading="lazy" />
              <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent"></div>
              <div class="absolute bottom-0 left-0 p-6">
                <span class="mb-2 inline-block rounded-full bg-white/20 px-3 py-1 text-xs font-bold text-white backdrop-blur-sm">辽宁</span>
                <h3 class="text-2xl font-black text-white">丹东奶油草莓</h3>
                <p class="mt-1 text-sm text-white/80">酸甜多汁 · 自然熟红</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8">
        <div class="mb-8 text-center">
          <h2 class="text-3xl font-black tracking-tight">为什么选择 EcoLink</h2>
          <p class="mt-2 text-sm text-slate-500">我们致力于为您提供最安全、最新鲜的生态食材</p>
        </div>
        <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
          <div class="flex flex-col items-center gap-3 rounded-2xl border border-primary/5 bg-white p-6 text-center shadow-sm transition-all hover:-translate-y-1 hover:shadow-md">
            <div class="flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10">
              <span class="material-symbols-outlined text-2xl text-primary">verified_user</span>
            </div>
            <h3 class="text-lg font-bold">品质保障</h3>
            <p class="text-sm leading-relaxed text-slate-500">每件商品均经过严格品质检测，确保安全无忧</p>
          </div>
          <div class="flex flex-col items-center gap-3 rounded-2xl border border-primary/5 bg-white p-6 text-center shadow-sm transition-all hover:-translate-y-1 hover:shadow-md">
            <div class="flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10">
              <span class="material-symbols-outlined text-2xl text-primary">local_shipping</span>
            </div>
            <h3 class="text-lg font-bold">冷链直达</h3>
            <p class="text-sm leading-relaxed text-slate-500">全程冷链物流配送，锁住新鲜，当日达</p>
          </div>
          <div class="flex flex-col items-center gap-3 rounded-2xl border border-primary/5 bg-white p-6 text-center shadow-sm transition-all hover:-translate-y-1 hover:shadow-md">
            <div class="flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10">
              <span class="material-symbols-outlined text-2xl text-primary">qr_code_2</span>
            </div>
            <h3 class="text-lg font-bold">全程溯源</h3>
            <p class="text-sm leading-relaxed text-slate-500">扫码即可查看产品从田间到餐桌的全程信息</p>
          </div>
          <div class="flex flex-col items-center gap-3 rounded-2xl border border-primary/5 bg-white p-6 text-center shadow-sm transition-all hover:-translate-y-1 hover:shadow-md">
            <div class="flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10">
              <span class="material-symbols-outlined text-2xl text-primary">eco</span>
            </div>
            <h3 class="text-lg font-bold">绿色有机</h3>
            <p class="text-sm leading-relaxed text-slate-500">坚持生态种植，零农药残留，让您吃得放心</p>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import { cartApi, productApi } from '@/api';
import { useAuthStore } from '@/stores/auth';
import { useCartStore } from '@/stores/cart';
import { useToastStore } from '@/stores/toast';
import type { Category, ProductItem } from '@/types/api';

const router = useRouter();
const auth = useAuthStore();
const cart = useCartStore();
const toast = useToastStore();
const categories = ref<Category[]>([]);
const products = ref<ProductItem[]>([]);
const loading = ref(false);
const keyword = ref('');
const fallbackImage = 'https://images.unsplash.com/photo-1608797178974-15b35a64ede9?auto=format&fit=crop&w=1200&q=80';

const categoryIconMap: Record<string, { icon: string; iconClass: string }> = {
  新鲜瓜果: { icon: 'nutrition', iconClass: 'bg-red-100 text-red-600' },
  时令蔬菜: { icon: 'spa', iconClass: 'bg-green-100 text-green-600' },
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

const homeHighlights = computed(() => [
  {
    eyebrow: 'Fresh Picks',
    title: '当季鲜蔬水果',
    description: '围绕时令与产地挑选每日食材，让餐桌更新鲜，也更适合日常采购。',
    icon: 'search',
    tags: ['时令上新', '新鲜直达', '每日采购'],
  },
  {
    eyebrow: 'Origin',
    title: '原产地风味特产',
    description: '汇集来自不同产区的特色好物，把地方风味和生态品质一起送到用户手中。',
    icon: 'location_on',
    tags: ['地方特产', '原产地直采', '风味优选'],
  },
  {
    eyebrow: 'Traceability',
    title: '批次溯源信息',
    description: '商品详情展示产地、批次和履约信息，购买前就能看清食材来源。',
    icon: 'qr_code_2',
    tags: ['产地信息', '批次编号', '来源可查'],
  },
  {
    eyebrow: 'Service',
    title: '冷链配送保障',
    description: '围绕生鲜场景提供冷链配送、品质承诺和订单跟踪，收货体验更稳定。',
    icon: 'local_shipping',
    tags: ['冷链配送', '品质保障', '订单可查'],
  },
]);

const quickEntries = computed(() => [
  {
    title: '选购商品',
    description: '浏览全部分类，按关键词或类型快速找到想买的商品。',
    icon: 'storefront',
    to: '/search',
  },
  {
    title: auth.isLogin ? '我的订单' : '立即登录',
    description: auth.isLogin ? '查看订单状态、支付结果和配送进度。' : '登录后可管理购物车、订单和收货信息。',
    icon: auth.isLogin ? 'receipt_long' : 'login',
    to: auth.isLogin ? '/orders' : '/login',
  },
  {
    title: '会员中心',
    description: '查看个人资料、收货地址和账户相关信息。',
    icon: 'person',
    to: '/profile',
  },
]);

const shoppingPromises = [
  {
    title: '严选生态产地',
    description: '围绕生态种植和产地标准筛选商品，减少日常采购中的选择成本。',
  },
  {
    title: '配送进度可跟踪',
    description: '下单后可在订单页查看支付、发货和完成等关键状态变化。',
  },
  {
    title: '购买信息更透明',
    description: '商品详情提供产地、规格、库存和溯源信息，帮助用户放心下单。',
  },
];

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
    toast.success('已加入购物车');
  } catch (error) {
    toast.error((error as Error).message);
  }
}

onMounted(() => {
  loadData().catch((error) => toast.error((error as Error).message));
});
</script>
