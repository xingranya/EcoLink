<template>
  <div class="flex-1 bg-background-light">
    <main class="mx-auto w-full max-w-7xl px-4 py-6 sm:px-10">
      <nav class="mb-6 flex flex-wrap items-center gap-2 text-sm font-medium">
        <RouterLink class="text-primary hover:underline" to="/">首页</RouterLink>
        <span class="material-symbols-outlined text-xs text-slate-400">chevron_right</span>
        <RouterLink class="text-primary hover:underline" to="/search">商品</RouterLink>
        <span class="material-symbols-outlined text-xs text-slate-400">chevron_right</span>
        <span class="text-slate-500">{{ product?.name || '商品详情' }}</span>
      </nav>

      <div v-if="loading" class="flex flex-col items-center gap-3 rounded-2xl bg-white p-16">
        <span class="material-symbols-outlined animate-spin text-3xl text-primary">progress_activity</span>
        <span class="text-sm text-slate-500">加载中...</span>
      </div>
      <div v-else-if="product" class="grid grid-cols-1 gap-10 lg:grid-cols-2">
        <div class="flex flex-col gap-4">
          <div class="aspect-square w-full overflow-hidden rounded-xl bg-slate-200 shadow-sm transition-all hover:shadow-md">
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
              class="flex h-14 flex-1 items-center justify-center gap-2 rounded-xl border-2 border-primary bg-transparent text-lg font-bold text-primary transition-all hover:bg-primary/5 active:scale-[0.98]"
              :disabled="favoriteLoading"
              @click="favorite"
            >
              <span class="material-symbols-outlined">favorite</span>
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
            <div class="flex items-center gap-2">
              <span class="material-symbols-outlined text-primary">verified_user</span>
              <span class="text-sm font-medium">正品保障</span>
            </div>
          </div>

          <div class="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
            <article
              v-for="metric in summaryMetrics"
              :key="metric.label"
              class="rounded-2xl border border-primary/10 bg-white p-4 shadow-sm"
            >
              <p class="text-xs font-bold uppercase tracking-[0.18em] text-slate-400">{{ metric.label }}</p>
              <p class="mt-3 text-base font-black text-slate-900">{{ metric.value }}</p>
              <p class="mt-2 text-xs leading-5 text-slate-500">{{ metric.description }}</p>
            </article>
          </div>
        </div>
      </div>

      <section v-if="product" class="mt-12">
        <div class="border-b border-primary/10">
          <nav class="flex gap-8">
            <a href="#section-detail" class="border-b-4 border-primary pb-4 text-lg font-bold text-primary">详情介绍</a>
            <a href="#section-specs" class="border-b-4 border-transparent pb-4 text-lg font-medium text-slate-500 hover:text-primary">规格参数</a>
            <a href="#section-trace" class="border-b-4 border-transparent pb-4 text-lg font-medium text-slate-500 hover:text-primary">溯源信息</a>
          </nav>
        </div>

        <div id="section-detail" class="py-10">
          <div class="grid grid-cols-1 items-center gap-10 md:grid-cols-2">
            <div>
              <h3 class="mb-4 text-2xl font-black">生态精选 · 品质之选</h3>
              <p class="mb-6 leading-relaxed text-slate-600">{{ product.detail || '精选优质生态农产品，严格品控，从源头把关，只为给您最安全、最健康的食材。每一件商品都经过层层筛选，确保新鲜直达。' }}</p>
              <ul class="space-y-3 text-slate-600">
                <li class="flex items-center gap-3"><span class="material-symbols-outlined text-lg text-primary">check_circle</span> 精选优质产区，源头直采</li>
                <li class="flex items-center gap-3"><span class="material-symbols-outlined text-lg text-primary">check_circle</span> 顺丰冷链直发，锁住新鲜</li>
                <li class="flex items-center gap-3"><span class="material-symbols-outlined text-lg text-primary">check_circle</span> 全程可追溯，安全透明</li>
                <li class="flex items-center gap-3"><span class="material-symbols-outlined text-lg text-primary">check_circle</span> 零农药残留，绿色有机</li>
              </ul>
            </div>
            <div class="overflow-hidden rounded-2xl shadow-xl">
              <img :src="product.images?.[1] || product.mainImage || fallbackImage" :alt="product.name" class="h-64 w-full object-cover" loading="lazy" />
            </div>
          </div>
        </div>

        <div id="section-specs" class="rounded-2xl border border-primary/5 bg-white p-8">
          <h3 class="mb-6 flex items-center gap-2 text-xl font-black">
            <span class="material-symbols-outlined text-primary">list_alt</span>
            技术规格
          </h3>
          <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            <div class="rounded-xl border border-primary/10 bg-background-light p-4">
              <span class="text-xs font-bold uppercase tracking-wider text-slate-400">产地</span>
              <p class="mt-1 font-bold">{{ product.categoryName || '优质产区' }}</p>
            </div>
            <div class="rounded-xl border border-primary/10 bg-background-light p-4">
              <span class="text-xs font-bold uppercase tracking-wider text-slate-400">保质期</span>
              <p class="mt-1 font-bold">7-10天（冷藏保存）</p>
            </div>
            <div class="rounded-xl border border-primary/10 bg-background-light p-4">
              <span class="text-xs font-bold uppercase tracking-wider text-slate-400">储存方式</span>
              <p class="mt-1 font-bold">冷藏 2-5°C</p>
            </div>
            <div class="rounded-xl border border-primary/10 bg-background-light p-4">
              <span class="text-xs font-bold uppercase tracking-wider text-slate-400">规格</span>
              <p class="mt-1 font-bold">500g ± 20g / 份</p>
            </div>
            <div class="rounded-xl border border-primary/10 bg-background-light p-4">
              <span class="text-xs font-bold uppercase tracking-wider text-slate-400">库存</span>
              <p class="mt-1 font-bold">{{ product.stock }} 件</p>
            </div>
            <div class="rounded-xl border border-primary/10 bg-background-light p-4">
              <span class="text-xs font-bold uppercase tracking-wider text-slate-400">认证</span>
              <p class="mt-1 font-bold">绿色有机认证</p>
            </div>
          </div>
        </div>

        <div class="mt-10 grid gap-6 lg:grid-cols-[0.95fr_1.05fr]">
          <section class="rounded-2xl border border-primary/10 bg-white p-8 shadow-sm">
            <h3 class="mb-6 flex items-center gap-2 text-xl font-black">
              <span class="material-symbols-outlined text-primary">star</span>
              推荐理由
            </h3>
            <div class="space-y-4">
              <article
                v-for="item in detailHighlights"
                :key="item.title"
                class="rounded-2xl bg-background-light p-4"
              >
                <div class="flex items-center gap-3">
                  <span class="material-symbols-outlined rounded-xl bg-primary/10 p-2 text-primary">{{ item.icon }}</span>
                  <div>
                    <p class="font-bold text-slate-900">{{ item.title }}</p>
                    <p class="mt-1 text-sm leading-6 text-slate-500">{{ item.description }}</p>
                  </div>
                </div>
              </article>
            </div>
          </section>

          <section class="rounded-2xl border border-emerald-200 bg-emerald-50 p-8 shadow-sm">
            <h3 class="mb-6 flex items-center gap-2 text-xl font-black text-slate-900">
              <span class="material-symbols-outlined text-emerald-600">route</span>
              履约与溯源路径
            </h3>
            <div class="space-y-4">
              <article
                v-for="node in supplySteps"
                :key="node.title"
                class="flex gap-4 rounded-2xl bg-white/80 p-4"
              >
                <div class="flex h-10 w-10 flex-shrink-0 items-center justify-center rounded-full bg-emerald-100 text-sm font-black text-emerald-700">
                  {{ node.step }}
                </div>
                <div>
                  <p class="font-bold text-slate-900">{{ node.title }}</p>
                  <p class="mt-1 text-sm leading-6 text-slate-500">{{ node.description }}</p>
                </div>
              </article>
            </div>
          </section>
        </div>

        <div id="section-trace" class="mt-10 flex flex-col items-center gap-10 rounded-2xl border border-dashed border-primary/30 bg-primary/5 p-8 md:flex-row">
          <div class="flex-1">
            <h3 class="mb-2 text-xl font-black">溯源信息 (Traceability Information)</h3>
            <p class="mb-6 text-slate-600">扫描二维码查看产品完整溯源信息，从果园地块到质检中心的全程记录，让每一口都安心。</p>
            <div class="flex flex-wrap gap-4">
              <div class="flex flex-col items-center gap-2 rounded-xl bg-white p-4 shadow-sm">
                <span class="material-symbols-outlined text-4xl text-primary">qr_code_2</span>
                <span class="text-[10px] font-bold uppercase tracking-wider">扫码溯源</span>
              </div>
              <div class="flex flex-col items-center gap-2 rounded-xl bg-white p-4 shadow-sm">
                <span class="material-symbols-outlined text-4xl text-primary">workspace_premium</span>
                <span class="text-[10px] font-bold uppercase tracking-wider">资质证明</span>
              </div>
              <div class="flex flex-col items-center gap-2 rounded-xl bg-white p-4 shadow-sm">
                <span class="material-symbols-outlined text-4xl text-primary">verified_user</span>
                <span class="text-[10px] font-bold uppercase tracking-wider">质检报告</span>
              </div>
            </div>
          </div>
          <div class="flex h-36 w-full items-center justify-center rounded-xl bg-white p-6 shadow-inner md:w-64">
            <div class="text-center">
              <p class="text-xs font-bold uppercase tracking-wider text-slate-400">批次编号</p>
              <p class="mt-1 text-lg font-black tracking-widest text-primary">{{ traceCode }}</p>
              <p class="mt-2 text-xs italic text-slate-500">采摘日期：{{ new Date(Date.now() - 3 * 86400000).toLocaleDateString('zh-CN') }}</p>
              <p class="text-xs italic text-slate-500">产地：{{ product.categoryName || '精选产区' }}</p>
            </div>
          </div>
        </div>
      </section>

      <section v-if="relatedProducts.length" class="mt-20 pb-10">
        <h2 class="mb-8 text-2xl font-black tracking-tight">你可能还喜欢</h2>
        <div class="grid grid-cols-2 gap-6 md:grid-cols-4">
          <RouterLink v-for="item in relatedProducts" :key="item.id" :to="`/product/${item.id}`" class="group cursor-pointer">
            <div class="relative mb-3 aspect-square overflow-hidden rounded-xl bg-slate-100">
              <img :src="item.mainImage || fallbackImage" :alt="item.name" class="h-full w-full object-cover transition-transform duration-500 group-hover:scale-110" loading="lazy" />
              <button class="absolute right-2 top-2 rounded-full bg-white/90 p-1.5 text-primary shadow-sm transition-colors hover:bg-primary hover:text-white" @click.prevent="favoriteRelated(item.id)">
                <span class="material-symbols-outlined text-sm">favorite</span>
              </button>
            </div>
            <h4 class="line-clamp-1 font-bold text-slate-800">{{ item.name }}</h4>
            <p class="text-sm text-slate-500">{{ item.subtitle || '生态优选' }}</p>
            <p class="mt-1 font-black text-primary">¥{{ Number(item.price).toFixed(2) }}</p>
          </RouterLink>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { RouterLink, useRoute } from 'vue-router';
import { cartApi, favoriteApi, productApi } from '@/api';
import { useCartStore } from '@/stores/cart';
import { useToastStore } from '@/stores/toast';
import type { ProductDetail, ProductItem } from '@/types/api';

const route = useRoute();
const toast = useToastStore();
const product = ref<ProductDetail | null>(null);
const activeImage = ref('');
const quantity = ref(1);
const loading = ref(false);
const cartLoading = ref(false);
const favoriteLoading = ref(false);
const relatedProducts = ref<ProductItem[]>([]);
const cart = useCartStore();
const fallbackImage = 'https://images.unsplash.com/photo-1608797178974-15b35a64ede9?auto=format&fit=crop&w=1200&q=80';

const traceCode = computed(() => {
  if (!product.value) return '';
  return `EC-${new Date().getFullYear()}-${String(product.value.id).padStart(3, '0')}`;
});

const summaryMetrics = computed(() => {
  if (!product.value) return [];
  return [
    {
      label: '原产地标签',
      value: product.value.categoryName || '生态优选产区',
      description: '分类、产地与详情展示联动，便于讲解前台信息组织方式。',
    },
    {
      label: '累计销量',
      value: `${product.value.sales} 件`,
      description: '订单创建成功后销量自动回写，支撑热销推荐与后台统计。',
    },
    {
      label: '当前库存',
      value: `${product.value.stock} 件`,
      description: '加购与下单都会校验库存，库存不足时会阻断核心流程。',
    },
    {
      label: '溯源批次',
      value: traceCode.value,
      description: '通过批次编号、采摘日期和配送节点形成可讲的溯源能力。',
    },
  ];
});

const detailHighlights = computed(() => {
  if (!product.value) return [];
  return [
    {
      icon: 'forest',
      title: '生态产地直采',
      description: `围绕 ${product.value.categoryName || '优质产区'} 进行主题化展示，突出绿色种植与源头供应。`,
    },
    {
      icon: 'inventory_2',
      title: '库存销量实时可见',
      description: `当前库存 ${product.value.stock} 件，累计销量 ${product.value.sales} 件，方便说明业务数据回写机制。`,
    },
    {
      icon: 'local_shipping',
      title: '冷链履约说明',
      description: '统一展示冷链配送、品质保障与正品承诺，增强生鲜电商场景可信度。',
    },
  ];
});

const supplySteps = computed(() => {
  if (!product.value) return [];
  return [
    {
      step: '01',
      title: '产地采收',
      description: `批次 ${traceCode.value} 对应商品从原产地采收进入平台质检，清晰呈现源头可追溯链路。`,
    },
    {
      step: '02',
      title: '入仓质检',
      description: '平台在入仓阶段维护主图、规格、库存等核心字段，前后台共享同一份商品数据。',
    },
    {
      step: '03',
      title: '订单履约',
      description: '用户下单后库存减少、销量增加，支付后可在后台完成发货和收尾管理。',
    },
  ];
});

async function loadDetail() {
  loading.value = true;
  try {
    product.value = await productApi.detail(Number(route.params.id));
    activeImage.value = product.value.images[0] || product.value.mainImage || '';
    const res = await productApi.list({ page: 1, size: 4, sort: 'latest' });
    relatedProducts.value = res.list.filter((item) => item.id !== product.value?.id).slice(0, 4);
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
    toast.success('加入购物车成功');
  } catch (error) {
    toast.error((error as Error).message);
  } finally {
    cartLoading.value = false;
  }
}

async function favorite() {
  if (!product.value || favoriteLoading.value) return;
  favoriteLoading.value = true;
  try {
    await favoriteApi.add(product.value.id);
    toast.success('收藏成功');
  } catch (error) {
    toast.error((error as Error).message);
  } finally {
    favoriteLoading.value = false;
  }
}

async function favoriteRelated(productId: number) {
  try {
    await favoriteApi.add(productId);
    toast.success('收藏成功');
  } catch (error) {
    toast.error((error as Error).message);
  }
}

onMounted(() => {
  loadDetail().catch((error) => toast.error((error as Error).message));
});
</script>
