<template>
  <div class="page-shell">
    <AppHeader />
    <main class="page-main">
      <section class="surface-card overflow-hidden p-6 lg:p-8">
        <div class="grid grid-cols-1 gap-6 lg:grid-cols-[1.05fr_1fr] lg:gap-10">
          <div>
            <span class="chip-tag">
              <span class="material-symbols-outlined text-sm">eco</span>
              100% 生态甄选
            </span>
            <h1 class="mt-4 text-4xl font-black leading-tight text-slate-900 lg:text-5xl">从田间到餐桌，直连每一份新鲜</h1>
            <p class="mt-3 max-w-xl text-[15px] leading-7 text-slate-600">
              参考 Stitch 设计重构后的演示站点，支持完整下单流程演示。你可以直接搜索、加购、下单、支付、管理地址和收藏。
            </p>
            <div class="mt-6 flex flex-wrap gap-3">
              <RouterLink to="/search" class="btn btn-primary">立即选购</RouterLink>
              <RouterLink to="/orders" class="btn btn-outline">查看订单</RouterLink>
            </div>
            <div class="mt-6 flex flex-wrap gap-3">
              <span class="chip-tag">24h 冷链直达</span>
              <span class="chip-tag">0 农残抽检</span>
              <span class="chip-tag">支持演示 Mock</span>
            </div>
          </div>
          <div class="relative">
            <img
              src="https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=1400&q=80"
              alt="生态农产品"
              class="h-full min-h-64 w-full rounded-3xl object-cover"
            />
            <div class="absolute inset-x-4 bottom-4 rounded-2xl border border-white/40 bg-white/85 p-4 backdrop-blur">
              <p class="text-sm text-slate-600">今日推荐</p>
              <p class="text-lg font-black text-slate-900">高山阳光青提 + 有机奶油生菜</p>
            </div>
          </div>
        </div>
      </section>

      <section class="mt-8">
        <div class="section-head">
          <div>
            <h2 class="section-title">商品分类</h2>
            <p class="section-subtitle">快速进入对应品类</p>
          </div>
        </div>
        <div class="flex flex-wrap gap-2">
          <RouterLink
            v-for="item in categories"
            :key="item.id"
            :to="`/search?categoryId=${item.id}`"
            class="chip-tag"
          >
            {{ item.name }}
          </RouterLink>
        </div>
      </section>

      <section class="mt-10">
        <div class="section-head">
          <div>
            <h2 class="section-title">新品推荐</h2>
            <p class="section-subtitle">本周新鲜上架的生态好物</p>
          </div>
          <RouterLink to="/search" class="btn btn-outline !px-3 !py-1.5">查看更多</RouterLink>
        </div>
        <div v-if="loading" class="empty-block">正在加载推荐商品...</div>
        <div v-else class="product-grid">
          <ProductCard v-for="item in products" :key="item.id" :product="item" @added="handleAdded" />
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import AppHeader from '@/components/AppHeader.vue';
import ProductCard from '@/components/ProductCard.vue';
import { productApi } from '@/api';
import { useCartStore } from '@/stores/cart';
import type { Category, ProductItem } from '@/types/api';

const categories = ref<Category[]>([]);
const products = ref<ProductItem[]>([]);
const loading = ref(false);
const cart = useCartStore();

async function loadData() {
  loading.value = true;
  try {
    [categories.value, products.value] = await Promise.all([
      productApi.categories(),
      productApi.list({ page: 1, size: 8, sort: 'latest' }).then((res) => res.list),
    ]);
  } finally {
    loading.value = false;
  }
}

function handleAdded() {
  cart.reload().catch(() => undefined);
  alert('已加入购物车');
}

onMounted(() => {
  loadData().catch((error) => alert((error as Error).message));
});
</script>
