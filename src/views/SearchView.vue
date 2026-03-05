<template>
  <div class="page-shell">
    <AppHeader />
    <main class="page-main !pt-6">
      <section class="surface-card p-4 lg:p-5">
        <div class="grid grid-cols-1 gap-3 md:grid-cols-5">
          <input v-model="keyword" placeholder="搜索商品，如：青提、糙米、牛奶" class="input-control md:col-span-2" />
          <select v-model.number="categoryId" class="select-control">
            <option :value="0">全部分类</option>
            <option v-for="item in categories" :key="item.id" :value="item.id">{{ item.name }}</option>
          </select>
          <select v-model="sort" class="select-control">
            <option value="comprehensive">综合排序</option>
            <option value="latest">最新上架</option>
            <option value="price_asc">价格升序</option>
            <option value="price_desc">价格降序</option>
          </select>
          <button class="btn btn-primary" :disabled="loading" @click="loadProducts">
            <span class="material-symbols-outlined text-[18px]">search</span>
            {{ loading ? '查询中' : '查询' }}
          </button>
        </div>
      </section>

      <div class="section-head mt-7">
        <div>
          <h1 class="section-title">搜索结果</h1>
          <p class="section-subtitle">共找到 {{ products.length }} 条记录</p>
        </div>
      </div>
      <div v-if="loading" class="empty-block">正在加载商品...</div>
      <div v-else class="product-grid">
        <ProductCard v-for="item in products" :key="item.id" :product="item" @added="handleAdded" />
      </div>
      <p v-if="!loading && products.length === 0" class="empty-block mt-8">暂无商品</p>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import AppHeader from '@/components/AppHeader.vue';
import ProductCard from '@/components/ProductCard.vue';
import { productApi } from '@/api';
import { useCartStore } from '@/stores/cart';
import type { Category, ProductItem } from '@/types/api';

const route = useRoute();
const keyword = ref('');
const categoryId = ref(0);
const sort = ref('comprehensive');
const categories = ref<Category[]>([]);
const products = ref<ProductItem[]>([]);
const loading = ref(false);
const cart = useCartStore();

async function loadProducts() {
  loading.value = true;
  try {
    const data = await productApi.list({
      keyword: keyword.value || undefined,
      categoryId: categoryId.value || undefined,
      sort: sort.value,
      page: 1,
      size: 20,
    });
    products.value = data.list;
  } catch (error) {
    alert((error as Error).message);
  } finally {
    loading.value = false;
  }
}

function handleAdded() {
  cart.reload().catch(() => undefined);
  alert('已加入购物车');
}

onMounted(async () => {
  try {
    categories.value = await productApi.categories();
    if (route.query.categoryId) {
      categoryId.value = Number(route.query.categoryId);
    }
    await loadProducts();
  } catch (error) {
    alert((error as Error).message);
  }
});
</script>
