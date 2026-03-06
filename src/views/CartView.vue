<template>
  <div class="flex-1 bg-background-light text-slate-900">
    <main class="mx-auto max-w-7xl px-4 py-8">
      <div class="mb-8 overflow-hidden rounded-2xl border border-primary/5 bg-white p-6 shadow-sm">
        <div class="mx-auto flex max-w-2xl items-center justify-center">
          <div class="flex flex-1 flex-col items-center gap-2">
            <div class="flex h-10 w-10 items-center justify-center rounded-full bg-primary text-white shadow-md shadow-primary/20">
              <span class="material-symbols-outlined text-lg">check</span>
            </div>
            <span class="text-sm font-bold text-primary">查看购物车</span>
          </div>
          <div class="mb-6 h-[2px] flex-1 max-w-24 bg-gradient-to-r from-primary/40 to-slate-200"></div>
          <div class="flex flex-1 flex-col items-center gap-2">
            <div class="flex h-10 w-10 items-center justify-center rounded-full border-2 border-slate-200 bg-white text-sm font-bold text-slate-400">2</div>
            <span class="text-sm font-medium text-slate-400">确认订单</span>
          </div>
          <div class="mb-6 h-[2px] flex-1 max-w-24 bg-slate-200"></div>
          <div class="flex flex-1 flex-col items-center gap-2">
            <div class="flex h-10 w-10 items-center justify-center rounded-full border-2 border-slate-200 bg-white text-sm font-bold text-slate-400">3</div>
            <span class="text-sm font-medium text-slate-400">支付成功</span>
          </div>
        </div>
      </div>

      <div class="flex flex-col gap-8 lg:flex-row">
        <section class="flex-grow space-y-4">
          <div class="overflow-hidden rounded-xl border border-primary/5 bg-white shadow-sm">
            <div class="flex items-center gap-4 border-b border-primary/5 bg-primary/5 p-4">
              <input :checked="allSelected" class="h-5 w-5 rounded border-primary/30 text-primary focus:ring-primary" type="checkbox" aria-label="全选购物车商品" @change="toggleAll" />
              <h2 class="text-lg font-bold">购物车 ({{ cart.items.length }}件商品)</h2>
            </div>
            <div class="divide-y divide-primary/5">
              <article v-for="item in cart.items" :key="item.id" class="flex flex-wrap items-center gap-4 p-4 transition-colors hover:bg-primary/5 md:flex-nowrap">
                <input v-model="selectedIds" :value="item.id" class="h-5 w-5 rounded border-primary/30 text-primary focus:ring-primary" type="checkbox" />
                <div class="h-24 w-24 flex-shrink-0 overflow-hidden rounded-lg bg-slate-100">
                  <img :src="item.productImage || fallbackImage" :alt="item.productName" class="h-full w-full object-cover" loading="lazy" />
                </div>
                <div class="flex-grow">
                  <RouterLink :to="`/product/${item.productId}`" class="font-bold text-slate-900 hover:text-primary">{{ item.productName }}</RouterLink>
                  <p class="text-sm text-slate-500">库存: {{ item.stock }}</p>
                  <p class="mt-1 font-bold text-primary md:hidden">¥{{ Number(item.price).toFixed(2) }}</p>
                </div>
                <div class="hidden w-32 text-center md:block">
                  <span class="text-sm text-slate-500">单价</span>
                  <p class="font-medium">¥{{ Number(item.price).toFixed(2) }}</p>
                </div>
                <div class="flex items-center gap-1 rounded-full bg-slate-100 p-1">
                  <button class="flex h-8 w-8 items-center justify-center rounded-full transition-colors hover:bg-white" @click="changeQty(item.id, item.quantity - 1)">
                    <span class="material-symbols-outlined text-sm">remove</span>
                  </button>
                  <span class="w-8 text-center font-bold">{{ item.quantity }}</span>
                  <button class="flex h-8 w-8 items-center justify-center rounded-full transition-colors hover:bg-white" @click="changeQty(item.id, item.quantity + 1)">
                    <span class="material-symbols-outlined text-sm">add</span>
                  </button>
                </div>
                <div class="hidden w-24 text-right md:block">
                  <p class="text-lg font-bold text-orange-500">¥{{ Number(item.subtotal).toFixed(2) }}</p>
                </div>
                <button class="text-slate-400 transition-colors hover:text-red-500" @click="remove(item.id)">
                  <span class="material-symbols-outlined">delete</span>
                </button>
              </article>
            </div>
            <p v-if="cart.items.length === 0" class="p-8 text-center text-sm text-slate-500">购物车为空</p>
          </div>

          <div class="mt-12" v-if="suggestProducts.length">
            <h2 class="mb-6 flex items-center gap-2 text-xl font-bold">
              <span class="material-symbols-outlined text-primary">recommend</span>
              猜你喜欢
            </h2>
            <div class="grid grid-cols-2 gap-4 md:grid-cols-4">
              <article v-for="item in suggestProducts" :key="item.id" class="group cursor-pointer rounded-xl border border-primary/5 bg-white p-3 shadow-sm">
                <RouterLink :to="`/product/${item.id}`" class="mb-3 block aspect-square overflow-hidden rounded-lg bg-slate-100">
                  <img :src="item.mainImage || fallbackImage" :alt="item.name" class="h-full w-full object-cover transition-transform duration-500 group-hover:scale-110" loading="lazy" />
                </RouterLink>
                <h4 class="mb-1 truncate text-sm font-bold">{{ item.name }}</h4>
                <p class="font-bold text-orange-500">¥{{ Number(item.price).toFixed(2) }}</p>
                <button class="mt-3 w-full rounded-full bg-primary/10 py-1.5 text-xs font-bold text-primary transition-colors hover:bg-primary hover:text-white" @click="addSuggest(item.id)">
                  加入购物车
                </button>
              </article>
            </div>
          </div>
        </section>

        <aside class="w-full flex-shrink-0 lg:w-80">
          <div class="sticky top-24 space-y-4">
            <div class="rounded-2xl border border-primary/10 bg-white p-6 shadow-lg">
              <h2 class="mb-6 text-xl font-black">订单摘要</h2>
              <div class="mb-4 space-y-3">
                <div class="flex justify-between text-sm text-slate-500">
                  <span>商品小计</span>
                  <span class="font-medium text-slate-700">¥{{ selectedTotal.toFixed(2) }}</span>
                </div>
                <div class="flex justify-between text-sm text-slate-500">
                  <span>运费</span>
                  <span class="font-bold text-primary">免运费</span>
                </div>
                <div class="flex justify-between text-sm text-slate-500">
                  <span>优惠券减免</span>
                  <span class="font-medium text-slate-700">- ¥0.00</span>
                </div>
              </div>

              <div class="mb-4 flex gap-2">
                <input type="text" placeholder="输入优惠码" class="input-control flex-1 !py-2 !text-xs" />
                <button class="rounded-lg bg-primary/10 px-3 py-2 text-xs font-bold text-primary transition-colors hover:bg-primary/20">应用</button>
              </div>

              <div class="mb-6 flex items-end justify-between border-t border-primary/10 pt-4">
                <span class="text-lg font-bold">总计</span>
                <div class="text-right">
                  <p class="text-3xl font-black text-orange-500">¥{{ selectedTotal.toFixed(2) }}</p>
                  <p class="text-xs text-slate-400">含税费约 ¥0.00</p>
                </div>
              </div>
              <button
                class="flex w-full items-center justify-center gap-2 rounded-xl bg-gradient-to-r from-primary to-emerald-500 py-4 font-bold text-white shadow-lg shadow-primary/30 transition-all hover:shadow-xl active:scale-[0.98]"
                :disabled="loading"
                @click="checkout"
              >
                <span>{{ loading ? '提交中...' : `去结算 (${selectedIds.length}件)` }}</span>
                <span class="material-symbols-outlined">arrow_forward</span>
              </button>
            </div>

            <div class="rounded-2xl border border-primary/5 bg-white p-4">
              <div class="grid grid-cols-3 gap-3">
                <div class="flex flex-col items-center gap-1">
                  <span class="material-symbols-outlined text-primary">verified_user</span>
                  <span class="text-[10px] font-semibold text-slate-500">安全支付</span>
                </div>
                <div class="flex flex-col items-center gap-1">
                  <span class="material-symbols-outlined text-primary">autorenew</span>
                  <span class="text-[10px] font-semibold text-slate-500">7天退货</span>
                </div>
                <div class="flex flex-col items-center gap-1">
                  <span class="material-symbols-outlined text-primary">local_shipping</span>
                  <span class="text-[10px] font-semibold text-slate-500">免费配送</span>
                </div>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import { addressApi, cartApi, orderApi, productApi } from '@/api';
import { useCartStore } from '@/stores/cart';
import type { ProductItem } from '@/types/api';

const router = useRouter();
const cart = useCartStore();
const selectedIds = ref<number[]>([]);
const loading = ref(false);
const suggestProducts = ref<ProductItem[]>([]);
const fallbackImage = 'https://images.unsplash.com/photo-1608797178974-15b35a64ede9?auto=format&fit=crop&w=1200&q=80';

const selectedTotal = computed(() =>
  cart.items.filter((item) => selectedIds.value.includes(item.id)).reduce((sum, item) => sum + Number(item.subtotal), 0),
);

const allSelected = computed(() => cart.items.length > 0 && selectedIds.value.length === cart.items.length);

async function reload() {
  await cart.reload();
  selectedIds.value = cart.items.map((item) => item.id);
}

function toggleAll() {
  selectedIds.value = allSelected.value ? [] : cart.items.map((item) => item.id);
}

async function changeQty(itemId: number, qty: number) {
  if (qty < 1) return;
  try {
    await cartApi.update(itemId, { quantity: qty });
    await reload();
  } catch (error) {
    alert((error as Error).message);
  }
}

async function remove(itemId: number) {
  try {
    await cartApi.remove(itemId);
    await reload();
  } catch (error) {
    alert((error as Error).message);
  }
}

async function addSuggest(productId: number) {
  try {
    await cartApi.add({ productId, quantity: 1 });
    await reload();
    alert('已加入购物车');
  } catch (error) {
    alert((error as Error).message);
  }
}

async function checkout() {
  if (selectedIds.value.length === 0) {
    alert('请选择要下单的商品');
    return;
  }
  loading.value = true;
  try {
    const addresses = await addressApi.list();
    const address = addresses.find((item) => item.isDefault) || addresses[0];
    if (!address) {
      alert('请先添加收货地址，添加后将自动跳回结算');
      router.push({ name: 'profile', query: { tab: 'address', redirect: '/cart' } });
      return;
    }
    const order = await orderApi.create({ addressId: address.id, cartItemIds: selectedIds.value });
    await reload();
    router.push(`/payment/${order.id}`);
  } catch (error) {
    alert((error as Error).message);
  } finally {
    loading.value = false;
  }
}

onMounted(async () => {
  try {
    await reload();
    suggestProducts.value = await productApi.list({ page: 1, size: 4, sort: 'latest' }).then((res) => res.list);
  } catch (error) {
    alert((error as Error).message);
  }
});
</script>
