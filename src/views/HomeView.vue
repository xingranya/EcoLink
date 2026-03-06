<template>
  <div class="flex flex-1 flex-col bg-background-light">
    <main class="flex-grow">
      <section class="relative h-[380px] w-full overflow-hidden lg:h-[420px]">
        <div class="absolute inset-0 z-10 bg-gradient-to-r from-black/60 to-transparent"></div>
        <img
          class="h-full w-full object-cover"
          src="https://lh3.googleusercontent.com/aida-public/AB6AXuAyib461gtOaKEowAswTd0JbboU5fyBuB64WgYgVREdDUI4GH0utthvMaTEtsGwp35Wzr7P7SGeFVhvMyj_Vg3gtiMSbq__6hQ2HTMPBtKGHbs5voz1dx4uRqlG65s4lHNbHaWxpWHOZm5LsfE8NqCz6WWNZRS7p4EoLn1hRGFsF9IG2Nf_8eW7baYgzFl8Qlaop4le3BjU5EZVad6NUNMckza3-Pj3YbzQuKX4Jc-a1sbd7WQmroVWYEkw0adpmdjBby6qYnhNSCjC"
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
              <img src="https://lh3.googleusercontent.com/aida-public/AB6AXuAKe3IwiFeeMM27__mq-5PL-XZDIDWn8S_8LewGuW19rb_-fSUI48y8qJVz0vQzjSaMHb57CcWKoszrNtHMsbbX3LafrN7IXHLN4emGZ8zQcLtPftj8CnDiu45eYRqgCOX7O6DASvO9TYD7P3bkiMEgDvtULiIGuDYGAX_vQrRZZqi4NqNPP5vIUj1ZsOmg9d8vxFP6uhUi-cZRIdZGMLVv-NiSS9i11UHf6NVPS_x8HlFehkDXBkm8v8Z2hBWqApdWQ84YcvOA5a-6" alt="云南高原精品水果" class="h-full w-full object-cover transition-transform duration-700 group-hover:scale-110" loading="lazy" />
              <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent"></div>
              <div class="absolute bottom-0 left-0 p-6">
                <span class="mb-2 inline-block rounded-full bg-white/20 px-3 py-1 text-xs font-bold text-white backdrop-blur-sm">云南</span>
                <h3 class="text-2xl font-black text-white">高原精品水果</h3>
                <p class="mt-1 text-sm text-white/80">阳光充足 · 口感甘甜</p>
              </div>
            </div>
            <div class="group relative h-60 overflow-hidden rounded-2xl shadow-md lg:h-72">
              <img src="https://lh3.googleusercontent.com/aida-public/AB6AXuBkQxqFuyJp4qDAKv95TU_yEofkW9bHxV5xmdfOIXMLBaXfLVHxUvpPRYUtPHD824wrvAXmmraVboRueLlWFkEpU0Mh31JvquCwSNfdJ2jWthwtt9IAdSdbbjVc26P2HNrt5tA57id2BzMwxTS96OjC3zGNJlYOAZOyZNpvAkZU1oyV9rzk_vwDwht0iQHjgvm86ilXi9o6Gv6CVlAkQrH1zvbpTySuSCXAqjLJFtKEfhyVqHM0gCKEog_fAyDoxXrOf-p18J0n3u9G" alt="山东烟台苹果" class="h-full w-full object-cover transition-transform duration-700 group-hover:scale-110" loading="lazy" />
              <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent"></div>
              <div class="absolute bottom-0 left-0 p-6">
                <span class="mb-2 inline-block rounded-full bg-white/20 px-3 py-1 text-xs font-bold text-white backdrop-blur-sm">山东</span>
                <h3 class="text-2xl font-black text-white">烟台红富士苹果</h3>
                <p class="mt-1 text-sm text-white/80">个大汁多 · 脆甜可口</p>
              </div>
            </div>
            <div class="group relative h-60 overflow-hidden rounded-2xl shadow-md lg:h-72">
              <img src="https://lh3.googleusercontent.com/aida-public/AB6AXuCcBKLfQLN8g6x6HkgprK1J9zbHfffrF_M5p7dz7zQtWC-FsdkX8Jdrwk_b6Z1m9Ckzwr_Ie-qV-ifZUk-bi-LFpoe6zMvdykUtgnIgw1yoDaNv3tO8jpbsjdg4lAZh3tNeerKAftPlCs0WMBH2Y-8wYTk7x6dKdkkqr06Cvuk5SUUCoj8PugT43jkgvn2kEeuc8AaC3TMr89dINDs2zAPh7uufxhd3kUW9B6YG5_btWtUDOB5QMFwMSaZz4mRY9X2yIty5i529NAnX" alt="丹东草莓" class="h-full w-full object-cover transition-transform duration-700 group-hover:scale-110" loading="lazy" />
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
