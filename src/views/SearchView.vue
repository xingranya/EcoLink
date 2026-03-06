<template>
  <div class="flex flex-1 flex-col bg-background-light text-slate-900">
    <main class="mx-auto flex w-full max-w-7xl flex-1 flex-col gap-6 p-4 md:flex-row md:p-6">
      <aside class="w-full shrink-0 md:w-64">
        <div class="sticky top-24 space-y-6 rounded-2xl border border-primary/5 bg-white p-5 shadow-sm">
          <div class="flex items-center justify-between">
            <h3 class="flex items-center gap-2 text-lg font-black">
              <span class="material-symbols-outlined text-primary">tune</span>
              筛选
            </h3>
            <button class="flex items-center gap-1 text-xs font-bold text-primary transition-colors hover:text-primary/70" @click="resetFilters">
              <span class="material-symbols-outlined text-sm">refresh</span>
              清空
            </button>
          </div>

          <div v-if="activeFilters.length" class="flex flex-wrap gap-1.5">
            <span v-for="filter in activeFilters" :key="filter" class="inline-flex items-center gap-1 rounded-full bg-primary/10 px-2.5 py-1 text-xs font-semibold text-primary">
              {{ filter }}
              <button class="ml-0.5 text-primary/60 hover:text-primary" @click="resetFilters">
                <span class="material-symbols-outlined text-xs">close</span>
              </button>
            </span>
          </div>

          <div class="space-y-3 border-t border-slate-100 pt-4">
            <p class="text-xs font-bold uppercase tracking-widest text-slate-400">分类</p>
            <div class="space-y-1.5">
              <label class="flex cursor-pointer items-center gap-2.5 rounded-lg px-2 py-1.5 transition-colors hover:bg-primary/5">
                <input
                  v-model.number="categoryId"
                  :value="0"
                  class="h-4 w-4 border-primary/30 text-primary focus:ring-primary"
                  type="radio"
                  name="cat"
                  aria-label="全部分类"
                />
                <span class="text-sm font-medium">全部</span>
              </label>
              <label v-for="item in categories" :key="item.id" class="flex cursor-pointer items-center gap-2.5 rounded-lg px-2 py-1.5 transition-colors hover:bg-primary/5">
                <input
                  v-model.number="categoryId"
                  :value="item.id"
                  class="h-4 w-4 border-primary/30 text-primary focus:ring-primary"
                  type="radio"
                  name="cat"
                  :aria-label="`分类 ${item.name}`"
                />
                <span class="text-sm font-medium">{{ item.name }}</span>
              </label>
            </div>
          </div>

          <div class="space-y-3 border-t border-slate-100 pt-4">
            <p class="text-xs font-bold uppercase tracking-widest text-slate-400">价格区间</p>
            <div class="flex items-center gap-2">
              <input
                v-model.number="minPrice"
                class="input-control !rounded-lg !py-2 text-center"
                placeholder="¥ 最低"
                type="number"
                min="0"
                aria-label="最低价格"
              />
              <span class="text-xs text-slate-300">—</span>
              <input
                v-model.number="maxPrice"
                class="input-control !rounded-lg !py-2 text-center"
                placeholder="¥ 最高"
                type="number"
                min="0"
                aria-label="最高价格"
              />
            </div>
            <button class="w-full rounded-lg bg-primary/10 py-2 text-xs font-bold text-primary transition-colors hover:bg-primary/20" @click="loadProducts">
              应用筛选
            </button>
          </div>
        </div>
      </aside>

      <section class="flex-1 space-y-6">
        <div class="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
          <div>
            <h1 class="text-2xl font-black tracking-tight">{{ keyword ? `"${keyword}" 的搜索结果` : '全部商品' }}</h1>
            <p class="mt-1 text-sm text-slate-500">共 {{ products.length }} 件商品</p>
          </div>
          <div class="flex items-center gap-2 rounded-xl border border-primary/10 bg-white px-4 py-2">
            <span class="material-symbols-outlined text-sm text-slate-400">sort</span>
            <select v-model="sort" class="cursor-pointer border-none bg-transparent p-0 pr-8 text-sm font-semibold text-slate-700 focus:ring-0">
              <option value="comprehensive">综合排序</option>
              <option value="latest">最新上架</option>
              <option value="price_asc">价格从低到高</option>
              <option value="price_desc">价格从高到低</option>
            </select>
          </div>
        </div>

        <div class="overflow-hidden rounded-2xl border border-primary/10 bg-white shadow-sm">
          <div class="flex items-center gap-3 border-b border-primary/10 px-5 py-3.5">
            <span class="material-symbols-outlined text-primary">search</span>
            <input
              v-model.trim="keyword"
              class="w-full border-none bg-transparent p-0 text-sm focus:ring-0"
              placeholder="搜索新鲜有机蔬菜、当季水果、原产地特产..."
              aria-label="搜索商品"
              @keydown.enter="loadProducts"
            />
            <button class="rounded-lg bg-primary px-5 py-2 text-sm font-bold text-white transition-colors hover:bg-primary/90" :disabled="loading" @click="loadProducts">
              {{ loading ? '查询中' : '查询' }}
            </button>
          </div>

          <div v-if="loading" class="flex flex-col items-center gap-3 p-12 text-slate-400">
            <span class="material-symbols-outlined animate-spin text-3xl text-primary">progress_activity</span>
            <span class="text-sm">正在加载商品...</span>
          </div>
          <div v-else-if="products.length === 0" class="flex flex-col items-center gap-3 p-12">
            <span class="material-symbols-outlined text-5xl text-slate-300">search_off</span>
            <p class="text-sm text-slate-500">暂无符合条件的商品</p>
            <button class="btn btn-soft mt-2" @click="resetFilters">清空筛选条件</button>
          </div>
          <div v-else class="grid grid-cols-1 gap-5 p-5 sm:grid-cols-2 lg:grid-cols-3">
            <article
              v-for="item in products"
              :key="item.id"
              class="group overflow-hidden rounded-xl border border-primary/5 bg-white transition-all hover:-translate-y-0.5 hover:shadow-lg"
            >
              <div class="relative h-52 overflow-hidden">
                <RouterLink :to="`/product/${item.id}`">
                  <img :src="item.mainImage || fallbackImage" :alt="item.name" class="h-full w-full object-cover transition-transform duration-500 group-hover:scale-110" loading="lazy" />
                </RouterLink>
                <button class="absolute right-3 top-3 rounded-full bg-white/80 p-2 text-slate-400 shadow-sm backdrop-blur-sm transition-colors hover:bg-white hover:text-red-500" @click="favorite(item.id)">
                  <span class="material-symbols-outlined text-lg">favorite</span>
                </button>
                <span class="absolute bottom-3 left-3 rounded-full bg-primary/90 px-3 py-1 text-[10px] font-bold text-white backdrop-blur-sm">{{ item.categoryName }}</span>
              </div>
              <div class="flex flex-col gap-2 p-4">
                <RouterLink :to="`/product/${item.id}`" class="line-clamp-1 text-base font-bold text-slate-900 hover:text-primary">{{ item.name }}</RouterLink>
                <p class="line-clamp-1 text-sm text-slate-500">{{ item.subtitle || '生态优选' }}</p>
                <div class="mt-1 flex items-center justify-between">
                  <span class="text-xl font-black text-primary">¥{{ Number(item.price).toFixed(2) }}</span>
                </div>
                <button
                  class="mt-1 flex w-full items-center justify-center gap-2 rounded-xl bg-primary py-2.5 text-sm font-bold text-white shadow-sm shadow-primary/20 transition-all hover:bg-primary/90 hover:shadow-md"
                  @click="addCart(item.id)"
                >
                  <span class="material-symbols-outlined text-lg">add_shopping_cart</span>
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
import { computed, onMounted, ref, watch } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';
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

const activeFilters = computed(() => {
  const filters: string[] = [];
  if (keyword.value) filters.push(`关键词: ${keyword.value}`);
  const cat = categories.value.find((c) => c.id === categoryId.value);
  if (cat) filters.push(cat.name);
  if (minPrice.value !== undefined) filters.push(`¥${minPrice.value}起`);
  if (maxPrice.value !== undefined) filters.push(`最高¥${maxPrice.value}`);
  return filters;
});

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
