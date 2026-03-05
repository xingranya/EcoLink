<template>
  <div class="page-shell">
    <AppHeader />
    <main class="page-main">
      <div class="section-head">
        <div>
          <h1 class="section-title">我的购物车</h1>
          <p class="section-subtitle">确认商品后可直接创建演示订单</p>
        </div>
        <button class="btn btn-outline !px-3 !py-1.5" @click="toggleAll">
          {{ selectedIds.length === cart.items.length ? '取消全选' : '全选' }}
        </button>
      </div>
      <div class="grid grid-cols-1 gap-6 lg:grid-cols-[1fr_340px]">
        <section class="space-y-3">
          <article v-for="item in cart.items" :key="item.id" class="surface-card flex items-center gap-3 p-4">
            <input v-model="selectedIds" type="checkbox" :value="item.id" class="h-4 w-4 accent-primary" />
            <img :src="item.productImage" class="h-20 w-20 rounded-xl object-cover" />
            <div class="flex-1">
              <p class="font-bold text-slate-900">{{ item.productName }}</p>
              <p class="text-sm text-slate-500">¥{{ Number(item.price).toFixed(2) }}</p>
            </div>
            <div class="flex items-center gap-2">
              <button class="btn btn-outline !rounded-lg !px-2.5 !py-1" :disabled="actioningId === item.id" @click="changeQty(item.id, item.quantity - 1)">
                -
              </button>
              <span>{{ item.quantity }}</span>
              <button class="btn btn-outline !rounded-lg !px-2.5 !py-1" :disabled="actioningId === item.id" @click="changeQty(item.id, item.quantity + 1)">
                +
              </button>
            </div>
            <button class="btn btn-danger !px-3 !py-1.5" :disabled="actioningId === item.id" @click="remove(item.id)">删除</button>
          </article>
          <p v-if="cart.items.length === 0" class="empty-block">购物车为空</p>
        </section>

        <aside class="surface-card h-fit p-5">
          <h2 class="text-lg font-black">订单结算</h2>
          <p class="mt-2 text-sm text-slate-600">已选 {{ selectedIds.length }} 件商品</p>
          <p class="mt-1 text-3xl font-black text-primary">¥{{ selectedTotal.toFixed(2) }}</p>
          <button class="btn btn-primary mt-4 w-full !py-3" :disabled="loading" @click="checkout">
            {{ loading ? '提交中...' : '提交订单' }}
          </button>
          <p class="mt-3 text-xs text-slate-500">提示：若后端未启动，会自动使用前端 Mock 流程进行演示。</p>
        </aside>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import AppHeader from '@/components/AppHeader.vue';
import { addressApi, cartApi, orderApi } from '@/api';
import { useCartStore } from '@/stores/cart';

const router = useRouter();
const cart = useCartStore();
const selectedIds = ref<number[]>([]);
const loading = ref(false);
const actioningId = ref(0);

const selectedTotal = computed(() =>
  cart.items
    .filter((item) => selectedIds.value.includes(item.id))
    .reduce((sum, item) => sum + Number(item.subtotal), 0),
);

async function reload() {
  await cart.reload();
  selectedIds.value = cart.items.map((item) => item.id);
}

async function changeQty(itemId: number, qty: number) {
  if (qty < 1) return;
  actioningId.value = itemId;
  try {
    await cartApi.update(itemId, { quantity: qty });
    await reload();
  } catch (error) {
    alert((error as Error).message);
  } finally {
    actioningId.value = 0;
  }
}

async function remove(itemId: number) {
  actioningId.value = itemId;
  try {
    await cartApi.remove(itemId);
    await reload();
  } catch (error) {
    alert((error as Error).message);
  } finally {
    actioningId.value = 0;
  }
}

function toggleAll() {
  if (selectedIds.value.length === cart.items.length) {
    selectedIds.value = [];
  } else {
    selectedIds.value = cart.items.map((item) => item.id);
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
      alert('请先在个人中心新增地址');
      router.push('/profile');
      return;
    }
    await orderApi.create({ addressId: address.id, cartItemIds: selectedIds.value });
    alert('下单成功');
    await reload();
    router.push('/orders');
  } catch (error) {
    alert((error as Error).message);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  reload().catch((error) => alert((error as Error).message));
});
</script>
