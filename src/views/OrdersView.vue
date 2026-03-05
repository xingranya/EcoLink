<template>
  <div class="page-shell">
    <AppHeader />
    <main class="page-main">
      <div class="section-head">
        <div>
          <h1 class="section-title">我的订单</h1>
          <p class="section-subtitle">支持模拟支付和状态展示</p>
        </div>
      </div>
      <div class="mt-6 space-y-4">
        <article v-for="order in orders" :key="order.id" class="surface-card p-5">
          <div class="flex flex-wrap items-center justify-between gap-2 border-b border-slate-100 pb-3">
            <p class="text-sm text-slate-500">订单号：{{ order.orderNo }}</p>
            <p class="status-pill bg-primary/10 text-primary">{{ statusText(order.status) }}</p>
          </div>
          <div class="space-y-3 pt-3">
            <div v-for="item in order.items" :key="item.id" class="flex items-center gap-3">
              <img :src="item.productImage" class="h-14 w-14 rounded-xl object-cover" />
              <div class="flex-1">
                <p class="font-medium">{{ item.productName }}</p>
                <p class="text-xs text-slate-500">x{{ item.quantity }}</p>
              </div>
              <p class="font-bold">¥{{ Number(item.subtotal).toFixed(2) }}</p>
            </div>
          </div>
          <div class="mt-4 flex items-center justify-between border-t border-slate-100 pt-4">
            <p class="text-sm text-slate-500">创建时间：{{ formatTime(order.createdAt) }}</p>
            <div class="flex items-center gap-3">
              <p class="text-lg font-black text-primary">¥{{ Number(order.totalAmount).toFixed(2) }}</p>
              <button
                v-if="order.status === 'UNPAID'"
                class="btn btn-primary !px-4 !py-2"
                :disabled="payingOrderId === order.id"
                @click="pay(order.id)"
              >
                {{ payingOrderId === order.id ? '支付中...' : '模拟支付' }}
              </button>
            </div>
          </div>
        </article>
        <p v-if="!orders.length && !loading" class="empty-block">暂无订单，去购物车提交一单试试。</p>
        <p v-if="loading" class="empty-block">订单加载中...</p>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import AppHeader from '@/components/AppHeader.vue';
import { orderApi } from '@/api';
import type { OrderData, OrderStatus } from '@/types/api';

const orders = ref<OrderData[]>([]);
const loading = ref(false);
const payingOrderId = ref(0);

function formatTime(time: string) {
  return new Date(time).toLocaleString();
}

function statusText(status: OrderStatus) {
  return {
    UNPAID: '待支付',
    PAID: '待发货',
    SHIPPED: '已发货',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
  }[status];
}

async function loadOrders() {
  loading.value = true;
  try {
    orders.value = await orderApi.list();
  } finally {
    loading.value = false;
  }
}

async function pay(id: number) {
  payingOrderId.value = id;
  try {
    await orderApi.pay(id);
    await loadOrders();
  } catch (error) {
    alert((error as Error).message);
  } finally {
    payingOrderId.value = 0;
  }
}

onMounted(() => {
  loadOrders().catch((error) => alert((error as Error).message));
});
</script>
