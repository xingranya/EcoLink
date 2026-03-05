<template>
  <div class="page-shell">
    <AppHeader />
    <main class="page-main">
      <div v-if="loading" class="empty-block">正在加载商品详情...</div>
      <div v-else-if="product" class="surface-card grid grid-cols-1 gap-8 p-6 lg:grid-cols-2 lg:p-8">
        <div>
          <img :src="activeImage" :alt="product.name" class="h-96 w-full rounded-2xl object-cover" />
          <div class="mt-3 grid grid-cols-5 gap-2">
            <button
              v-for="item in product.images"
              :key="item"
              class="overflow-hidden rounded-xl border bg-white"
              :class="activeImage === item ? 'border-primary' : 'border-slate-200'"
              @click="activeImage = item"
            >
              <img :src="item" class="h-16 w-full object-cover" />
            </button>
          </div>
        </div>
        <div>
          <span class="chip-tag">{{ product.categoryName }}</span>
          <h1 class="mt-3 text-4xl font-black">{{ product.name }}</h1>
          <p class="mt-2 text-slate-500">{{ product.subtitle }}</p>
          <p class="mt-4 text-4xl font-black text-primary">¥{{ Number(product.price).toFixed(2) }}</p>
          <p class="mt-2 text-sm text-slate-500">库存：{{ product.stock }} · 销量：{{ product.sales }}</p>
          <div class="mt-6 flex items-center gap-2">
            <button class="btn btn-outline !rounded-lg !px-3 !py-1.5" @click="quantity = Math.max(1, quantity - 1)">-</button>
            <span class="w-10 text-center">{{ quantity }}</span>
            <button class="btn btn-outline !rounded-lg !px-3 !py-1.5" @click="quantity = Math.min(product.stock, quantity + 1)">+</button>
          </div>
          <div class="mt-6 flex gap-3">
            <button class="btn btn-primary !px-6 !py-3" :disabled="cartLoading" @click="addCart">
              {{ cartLoading ? '加入中...' : '加入购物车' }}
            </button>
            <button class="btn btn-outline !px-6 !py-3" :disabled="favoriteLoading" @click="favorite">
              {{ favoriteLoading ? '收藏中...' : '收藏' }}
            </button>
          </div>
          <p class="mt-6 rounded-2xl bg-[#f4f9f5] p-4 text-sm leading-7 text-slate-600">{{ product.detail }}</p>
        </div>
      </div>
      <div v-else class="empty-block">商品不存在</div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
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
