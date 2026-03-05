<template>
  <div class="min-h-screen bg-background-light">
    <AppHeader />
    <main class="mx-auto w-full max-w-7xl px-4 py-6 sm:px-10">
      <nav class="mb-6 flex flex-wrap items-center gap-2 text-sm font-medium">
        <RouterLink class="text-primary hover:underline" to="/">首页</RouterLink>
        <span class="material-symbols-outlined text-xs text-slate-400">chevron_right</span>
        <RouterLink class="text-primary hover:underline" to="/search">商品</RouterLink>
        <span class="material-symbols-outlined text-xs text-slate-400">chevron_right</span>
        <span class="text-slate-500">{{ product?.name || '商品详情' }}</span>
      </nav>

      <div v-if="loading" class="rounded-xl bg-white p-10 text-center text-sm text-slate-500">加载中...</div>
      <div v-else-if="product" class="grid grid-cols-1 gap-10 lg:grid-cols-2">
        <div class="flex flex-col gap-4">
          <div class="aspect-square w-full overflow-hidden rounded-xl bg-slate-200 shadow-sm">
            <img :src="activeImage || product.mainImage || fallbackImage" :alt="product.name" class="h-full w-full object-cover" />
          </div>
          <div class="grid grid-cols-4 gap-4">
            <button
              v-for="img in product.images"
              :key="img"
              class="aspect-square overflow-hidden rounded-lg border-2 transition-all"
              :class="activeImage === img ? 'border-primary ring-offset-2' : 'border-transparent hover:border-primary/50'"
              @click="activeImage = img"
            >
              <img :src="img" class="h-full w-full object-cover" />
            </button>
          </div>
        </div>

        <div class="flex flex-col">
          <div class="mb-2 inline-flex items-center gap-1 rounded-full bg-primary/10 px-3 py-1 text-xs font-bold uppercase tracking-wider text-primary">
            <span class="material-symbols-outlined text-sm">verified</span>
            有机认证
          </div>
          <h1 class="mb-2 text-4xl font-black leading-tight tracking-tight text-slate-900">{{ product.name }}</h1>
          <p class="mb-6 text-lg text-slate-600">{{ product.subtitle }}</p>
          <div class="mb-8 flex items-baseline gap-4">
            <span class="text-4xl font-black text-primary">¥{{ Number(product.price).toFixed(2) }}</span>
            <span class="text-lg text-slate-400 line-through">¥{{ (Number(product.price) * 1.2).toFixed(2) }}</span>
            <span class="rounded bg-red-100 px-2 py-0.5 text-xs font-bold text-red-600">限时优惠</span>
          </div>

          <div class="mb-8 flex items-center gap-4">
            <span class="font-bold text-slate-700">数量：</span>
            <div class="flex h-12 w-32 items-center justify-between overflow-hidden rounded-xl border border-primary/20 bg-white">
              <button class="flex h-full w-10 items-center justify-center text-primary hover:bg-primary/5" @click="quantity = Math.max(1, quantity - 1)">
                <span class="material-symbols-outlined">remove</span>
              </button>
              <span class="font-bold">{{ quantity }}</span>
              <button class="flex h-full w-10 items-center justify-center text-primary hover:bg-primary/5" @click="quantity = Math.min(product.stock, quantity + 1)">
                <span class="material-symbols-outlined">add</span>
              </button>
            </div>
            <span class="text-sm text-slate-500">库存 {{ product.stock }} 件</span>
          </div>

          <div class="flex flex-col gap-3 sm:flex-row">
            <button
              class="flex h-14 flex-1 items-center justify-center gap-2 rounded-xl bg-primary text-lg font-bold text-white shadow-lg shadow-primary/20 transition-all hover:brightness-110 active:scale-[0.98]"
              :disabled="cartLoading"
              @click="addCart"
            >
              <span class="material-symbols-outlined">add_shopping_cart</span>
              {{ cartLoading ? '加入中...' : '加入购物车' }}
            </button>
            <button
              class="flex h-14 flex-1 items-center justify-center rounded-xl border-2 border-primary bg-transparent text-lg font-bold text-primary transition-all hover:bg-primary/5 active:scale-[0.98]"
              :disabled="favoriteLoading"
              @click="favorite"
            >
              {{ favoriteLoading ? '收藏中...' : '收藏商品' }}
            </button>
          </div>

          <div class="mt-8 flex flex-wrap gap-6 border-t border-primary/10 py-6">
            <div class="flex items-center gap-2">
              <span class="material-symbols-outlined text-primary">local_shipping</span>
              <span class="text-sm font-medium">顺丰冷链直达</span>
            </div>
            <div class="flex items-center gap-2">
              <span class="material-symbols-outlined text-primary">assignment_return</span>
              <span class="text-sm font-medium">新鲜品质保证</span>
            </div>
          </div>
        </div>
      </div>

      <section v-if="product" class="mt-16">
        <div class="border-b border-primary/10">
          <nav class="flex gap-8">
            <button class="border-b-4 border-primary pb-4 text-lg font-bold text-primary">详情介绍</button>
            <button class="border-b-4 border-transparent pb-4 text-lg font-medium text-slate-500">规格参数</button>
            <button class="border-b-4 border-transparent pb-4 text-lg font-medium text-slate-500">溯源信息</button>
          </nav>
        </div>
        <div class="py-10">
          <div class="grid grid-cols-1 items-center gap-10 md:grid-cols-2">
            <div>
              <h3 class="mb-4 text-2xl font-bold">青提中的“爱马仕”</h3>
              <p class="mb-4 leading-relaxed text-slate-600">{{ product.detail || '精选优质生态农产品，严格品控，安全健康。' }}</p>
              <ul class="space-y-2 text-slate-600">
                <li class="flex items-center gap-2"><span class="material-symbols-outlined text-lg text-primary">check_circle</span> 精选优质产区</li>
                <li class="flex items-center gap-2"><span class="material-symbols-outlined text-lg text-primary">check_circle</span> 新鲜冷链直发</li>
                <li class="flex items-center gap-2"><span class="material-symbols-outlined text-lg text-primary">check_circle</span> 产地可溯源</li>
              </ul>
            </div>
            <div class="overflow-hidden rounded-2xl shadow-xl">
              <img :src="product.images[1] || product.mainImage || fallbackImage" class="h-64 w-full object-cover" />
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { RouterLink, useRoute } from 'vue-router';
import AppHeader from '@/components/AppHeader.vue';
import { cartApi, favoriteApi, productApi } from '@/api';
import { useCartStore } from '@/stores/cart';
import type { ProductDetail } from '@/types/api';

const route = useRoute();
const product = ref<ProductDetail | null>(null);
const activeImage = ref('');
const quantity = ref(1);
const loading = ref(false);
const cartLoading = ref(false);
const favoriteLoading = ref(false);
const cart = useCartStore();
const fallbackImage = 'https://images.unsplash.com/photo-1608797178974-15b35a64ede9?auto=format&fit=crop&w=1200&q=80';

async function loadDetail() {
  loading.value = true;
  try {
    product.value = await productApi.detail(Number(route.params.id));
    activeImage.value = product.value.images[0] || product.value.mainImage || '';
  } finally {
    loading.value = false;
  }
}

async function addCart() {
  if (!product.value || cartLoading.value) return;
  cartLoading.value = true;
  try {
    await cartApi.add({ productId: product.value.id, quantity: quantity.value });
    cart.reload().catch(() => undefined);
    alert('加入购物车成功');
  } catch (error) {
    alert((error as Error).message);
  } finally {
    cartLoading.value = false;
  }
}

async function favorite() {
  if (!product.value || favoriteLoading.value) return;
  favoriteLoading.value = true;
  try {
    await favoriteApi.add(product.value.id);
    alert('收藏成功');
  } catch (error) {
    alert((error as Error).message);
  } finally {
    favoriteLoading.value = false;
  }
}

onMounted(() => {
  loadDetail().catch((error) => alert((error as Error).message));
});
</script>
