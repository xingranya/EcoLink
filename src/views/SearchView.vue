<template>
  <div class="min-h-screen bg-background-light text-slate-900">
    <AppHeader />
    <main class="mx-auto flex w-full max-w-7xl flex-1 flex-col gap-6 p-4 md:flex-row md:p-6">
      <aside class="w-full shrink-0 md:w-64">
        <div class="sticky top-24 space-y-6">
          <div class="flex items-center justify-between">
            <h3 class="flex items-center gap-2 text-lg font-bold">
              <span class="material-symbols-outlined text-primary">tune</span>
              筛选
            </h3>
            <button class="text-sm font-medium text-primary" @click="resetFilters">清空全部</button>
          </div>

          <div class="space-y-3">
            <p class="text-xs font-semibold uppercase tracking-wider text-slate-500">分类</p>
            <div class="space-y-2">
              <label class="flex cursor-pointer items-center gap-2">
                <input v-model.number="categoryId" :value="0" class="h-4 w-4 rounded border-primary/30 text-primary focus:ring-primary" type="radio" name="cat" />
                <span class="text-sm">全部</span>
              </label>
              <label v-for="item in categories" :key="item.id" class="flex cursor-pointer items-center gap-2">
                <input v-model.number="categoryId" :value="item.id" class="h-4 w-4 rounded border-primary/30 text-primary focus:ring-primary" type="radio" name="cat" />
                <span class="text-sm">{{ item.name }}</span>
              </label>
            </div>
          </div>

          <div class="space-y-3">
            <p class="text-xs font-semibold uppercase tracking-wider text-slate-500">价格区间</p>
            <div class="flex items-center gap-2">
              <input v-model.number="minPrice" class="w-full rounded-lg border-primary/20 bg-white px-3 py-2 text-sm focus:border-primary focus:ring-primary" placeholder="Min" type="number" min="0" />
              <span class="text-slate-400">-</span>
              <input v-model.number="maxPrice" class="w-full rounded-lg border-primary/20 bg-white px-3 py-2 text-sm focus:border-primary focus:ring-primary" placeholder="Max" type="number" min="0" />
            </div>
          </div>
        </div>
      </aside>

      <section class="flex-1 space-y-6">
        <div class="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
          <div>
            <h1 class="text-2xl font-bold">找到关于 '{{ keyword || '全部商品' }}' 的搜索结果</h1>
            <p class="text-sm text-slate-500">显示 {{ products.length }} 条结果</p>
          </div>
          <div class="flex items-center gap-2 rounded-lg border border-primary/10 bg-white px-3 py-1.5">
            <span class="text-sm text-slate-500">Sort by:</span>
            <select v-model="sort" class="cursor-pointer border-none bg-transparent p-0 pr-8 text-sm font-medium text-primary focus:ring-0">
              <option value="comprehensive">综合排序</option>
              <option value="latest">最新上架</option>
              <option value="price_asc">价格：从低到高</option>
              <option value="price_desc">价格：从高到低</option>
            </select>
          </div>
        </div>

        <div class="overflow-hidden rounded-xl border border-primary/10 bg-white">
          <div class="flex items-center gap-2 border-b border-primary/10 px-4 py-3">
            <span class="material-symbols-outlined text-primary">search</span>
            <input
              v-model.trim="keyword"
              class="w-full border-none bg-transparent p-0 text-sm focus:ring-0"
              placeholder="搜索新鲜农产品..."
              @keydown.enter="loadProducts"
            />
            <button class="rounded-lg bg-primary px-3 py-1.5 text-xs font-bold text-white hover:bg-primary/90" :disabled="loading" @click="loadProducts">
              {{ loading ? '查询中' : '查询' }}
            </button>
          </div>

          <div v-if="loading" class="p-8 text-center text-sm text-slate-500">正在加载商品...</div>
          <div v-else-if="products.length === 0" class="p-8 text-center text-sm text-slate-500">暂无商品</div>
          <div v-else class="grid grid-cols-1 gap-6 p-4 sm:grid-cols-2 lg:grid-cols-3">
            <article
              v-for="item in products"
              :key="item.id"
              class="group overflow-hidden rounded-xl border border-primary/10 bg-white transition-shadow hover:shadow-lg"
            >
              <div class="relative h-48 overflow-hidden">
                <RouterLink :to="`/product/${item.id}`">
                  <img :src="item.mainImage || fallbackImage" :alt="item.name" class="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105" />
                </RouterLink>
                <button class="absolute right-3 top-3 rounded-full bg-white/80 p-1.5 text-slate-400 backdrop-blur hover:text-red-500" @click="favorite(item.id)">
                  <span class="material-symbols-outlined text-[20px]">favorite</span>
                </button>
                <div class="absolute bottom-3 left-3 rounded bg-primary px-2 py-1 text-[10px] font-bold uppercase text-white">{{ item.categoryName }}</div>
              </div>
              <div class="flex flex-col gap-2 p-4">
                <div class="flex items-start justify-between gap-2">
                  <RouterLink :to="`/product/${item.id}`" class="line-clamp-1 text-lg font-bold hover:text-primary">{{ item.name }}</RouterLink>
                  <span class="font-bold text-primary">¥{{ Number(item.price).toFixed(2) }}</span>
                </div>
                <p class="line-clamp-1 text-sm text-slate-500">{{ item.subtitle || '生态优选' }}</p>
                <button
                  class="mt-2 flex w-full items-center justify-center gap-2 rounded-lg bg-primary py-2 font-semibold text-white transition-colors hover:bg-primary/90"
                  @click="addCart(item.id)"
                >
                  <span class="material-symbols-outlined text-[20px]">add_shopping_cart</span>
                  加入购物车
                </button>
              </div>
            </article>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';
import AppHeader from '@/components/AppHeader.vue';
import { favoriteApi, cartApi, productApi } from '@/api';
import { useCartStore } from '@/stores/cart';
import type { Category, ProductItem } from '@/types/api';

const route = useRoute();
const router = useRouter();
const cart = useCartStore();
const keyword = ref('');
const categoryId = ref(0);
const sort = ref('comprehensive');
const minPrice = ref<number | undefined>(undefined);
const maxPrice = ref<number | undefined>(undefined);
const categories = ref<Category[]>([]);
const products = ref<ProductItem[]>([]);
const loading = ref(false);
const fallbackImage = 'https://images.unsplash.com/photo-1608797178974-15b35a64ede9?auto=format&fit=crop&w=1200&q=80';

function syncFromQuery() {
  keyword.value = (route.query.keyword as string) || '';
  categoryId.value = Number(route.query.categoryId || 0);
}

async function loadProducts() {
  loading.value = true;
  try {
    const data = await productApi.list({
      keyword: keyword.value || undefined,
      categoryId: categoryId.value || undefined,
      minPrice: minPrice.value,
      maxPrice: maxPrice.value,
      sort: sort.value,
      page: 1,
      size: 20,
    });
    products.value = data.list;
    router.replace({
      path: '/search',
      query: {
        ...(keyword.value ? { keyword: keyword.value } : {}),
        ...(categoryId.value ? { categoryId: String(categoryId.value) } : {}),
      },
    });
  } catch (error) {
    alert((error as Error).message);
  } finally {
    loading.value = false;
  }
}

function resetFilters() {
  keyword.value = '';
  categoryId.value = 0;
  sort.value = 'comprehensive';
  minPrice.value = undefined;
  maxPrice.value = undefined;
  loadProducts().catch(() => undefined);
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

async function favorite(productId: number) {
  try {
    await favoriteApi.add(productId);
    alert('已收藏');
  } catch (error) {
    alert((error as Error).message);
  }
}

watch([categoryId, sort], () => {
  loadProducts().catch(() => undefined);
});

onMounted(async () => {
  try {
    categories.value = await productApi.categories();
    syncFromQuery();
    await loadProducts();
  } catch (error) {
    alert((error as Error).message);
  }
});
</script>
