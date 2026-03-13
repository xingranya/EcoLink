<template>
  <div class="flex-1 bg-background-light font-display text-slate-900">
    <main class="mx-auto w-full max-w-[1200px] flex-1 px-4 py-8 md:px-10">
      <div class="mb-8 overflow-x-auto">
        <div class="min-w-max border-b border-primary/10">
          <div class="flex">
            <button
              v-for="tab in tabs"
              :key="tab.key"
              class="flex flex-col items-center justify-center border-b-2 px-6 pb-4 pt-2 text-sm font-bold transition-all"
              :class="activeTab === tab.key ? 'border-primary text-primary' : 'border-transparent text-primary/60 hover:text-primary'"
              @click="activeTab = tab.key"
            >
              {{ tab.label }}
            </button>
          </div>
        </div>
      </div>

      <div v-if="loading" class="rounded-xl bg-white p-8 text-center text-sm text-slate-500">订单加载中...</div>
      <div v-else class="flex flex-col gap-6">
        <article
          v-for="order in filteredOrders"
          :key="order.id"
          :id="`order-${order.id}`"
          class="overflow-hidden rounded-xl border bg-white shadow-sm transition-all"
          :class="expandedOrderId === order.id ? 'border-primary/30 ring-2 ring-primary/10' : 'border-primary/10'"
        >
          <div class="flex flex-wrap items-center justify-between gap-4 border-b border-primary/5 p-4 md:p-6">
            <div class="flex flex-col">
              <div class="flex items-center gap-2">
                <span class="text-lg font-bold">#{{ order.orderNo }}</span>
                <span class="rounded px-2 py-0.5 text-xs font-bold uppercase" :class="statusClass(order.status)">{{ statusText(order.status) }}</span>
              </div>
              <p class="text-sm text-slate-500">下单日期: {{ formatTime(order.createdAt) }}</p>
            </div>
            <div class="text-right">
              <p class="text-sm text-slate-500">收货人: {{ order.receiverName }}</p>
              <p class="text-base font-semibold text-primary">总价 ¥{{ Number(order.totalAmount).toFixed(2) }}</p>
            </div>
          </div>

          <div class="border-b border-primary/5 bg-slate-50 px-4 py-4 md:px-6">
            <div class="flex items-center gap-2 text-[11px] font-bold uppercase tracking-[0.18em] text-slate-400">
              <span>订单进度</span>
              <span class="material-symbols-outlined text-sm text-primary/60">timeline</span>
            </div>
            <div class="mt-4 grid grid-cols-4 gap-2">
              <div
                v-for="step in orderSteps"
                :key="step.key"
                class="rounded-xl px-3 py-3 text-center"
                :class="stepStateClass(order.status, step.key)"
              >
                <p class="text-xs font-bold">{{ step.label }}</p>
              </div>
            </div>
          </div>

          <div class="space-y-4 p-4 md:p-6">
            <div v-for="item in order.items" :key="item.id" class="flex items-center gap-4">
              <img :src="item.productImage || fallbackImage" :alt="item.productName" class="h-20 w-20 flex-shrink-0 rounded-lg object-cover" loading="lazy" />
              <div class="min-w-0 flex-1">
                <p class="truncate text-base font-medium">{{ item.productName }}</p>
                <p class="text-sm text-slate-500">数量 x{{ item.quantity }}</p>
                <p class="mt-1 text-sm font-bold">小计 ¥{{ Number(item.subtotal).toFixed(2) }}</p>
              </div>
            </div>
          </div>

          <div v-if="expandedOrderId === order.id" class="border-t border-primary/5 bg-slate-50 px-4 py-4 md:px-6">
            <dl class="grid grid-cols-1 gap-2 text-sm sm:grid-cols-2">
              <div class="flex gap-2">
                <dt class="text-slate-500">收货人:</dt>
                <dd class="font-medium">{{ order.receiverName }} {{ order.receiverPhone }}</dd>
              </div>
              <div class="flex gap-2">
                <dt class="text-slate-500">收货地址:</dt>
                <dd class="font-medium">{{ order.receiverAddress }}</dd>
              </div>
              <div class="flex gap-2">
                <dt class="text-slate-500">下单时间:</dt>
                <dd class="font-medium">{{ formatTime(order.createdAt) }}</dd>
              </div>
              <div v-if="order.paidAt" class="flex gap-2">
                <dt class="text-slate-500">支付时间:</dt>
                <dd class="font-medium">{{ formatTime(order.paidAt) }}</dd>
              </div>
              <div v-if="order.shippedAt" class="flex gap-2">
                <dt class="text-slate-500">发货时间:</dt>
                <dd class="font-medium">{{ formatTime(order.shippedAt) }}</dd>
              </div>
              <div v-if="order.completedAt" class="flex gap-2">
                <dt class="text-slate-500">完成时间:</dt>
                <dd class="font-medium">{{ formatTime(order.completedAt) }}</dd>
              </div>
            </dl>
          </div>
          <div class="flex flex-wrap items-center justify-between gap-4 bg-primary/5 p-4 md:p-6">
            <div class="flex items-baseline gap-2">
              <span class="text-sm text-slate-600">总计:</span>
              <span class="text-xl font-bold text-primary">¥{{ Number(order.totalAmount).toFixed(2) }}</span>
            </div>
            <div class="flex gap-3">
              <button
                class="rounded-lg border border-primary/20 bg-white px-4 py-2 text-sm font-bold text-slate-900 transition-colors hover:bg-slate-50"
                @click="toggleDetail(order.id)"
              >
                {{ expandedOrderId === order.id ? '收起详情' : '查看详情' }}
              </button>
              <button
                v-if="order.status === 'UNPAID'"
                class="rounded-lg bg-primary px-4 py-2 text-sm font-bold text-white transition-colors hover:bg-primary/90"
                @click="goPayment(order.id)"
              >
                立即支付
              </button>
              <button v-else class="rounded-lg border border-primary/20 bg-primary/20 px-4 py-2 text-sm font-bold text-primary transition-colors hover:bg-primary/30">
                查看物流
              </button>
            </div>
          </div>
        </article>

        <p v-if="!filteredOrders.length" class="rounded-xl bg-white p-8 text-center text-sm text-slate-500">当前筛选暂无订单</p>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { orderApi } from '@/api';
import { useToastStore } from '@/stores/toast';
import type { OrderData, OrderStatus } from '@/types/api';

const route = useRoute();
const router = useRouter();
const toast = useToastStore();
const orders = ref<OrderData[]>([]);
const loading = ref(false);
const expandedOrderId = ref(0);
const activeTab = ref<'all' | 'UNPAID' | 'PAID' | 'SHIPPED' | 'COMPLETED'>('all');
const fallbackImage = 'https://images.unsplash.com/photo-1608797178974-15b35a64ede9?auto=format&fit=crop&w=1200&q=80';
const orderSteps: Array<{ key: OrderStatus; label: string }> = [
  { key: 'UNPAID', label: '待支付' },
  { key: 'PAID', label: '已支付' },
  { key: 'SHIPPED', label: '已发货' },
  { key: 'COMPLETED', label: '已完成' },
];

const tabs = [
  { key: 'all', label: '全部订单' },
  { key: 'UNPAID', label: '待付款' },
  { key: 'PAID', label: '待发货' },
  { key: 'SHIPPED', label: '待收货' },
  { key: 'COMPLETED', label: '已完成' },
] as const;

const filteredOrders = computed(() =>
  activeTab.value === 'all' ? orders.value : orders.value.filter((item) => item.status === activeTab.value),
);

function formatTime(time: string) {
  return new Date(time).toLocaleString();
}

function statusText(status: OrderStatus) {
  return {
    UNPAID: '待付款',
    PAID: '待发货',
    SHIPPED: '待收货',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
  }[status];
}

function statusClass(status: OrderStatus) {
  if (status === 'UNPAID') return 'bg-amber-100 text-amber-700';
  if (status === 'PAID') return 'bg-blue-100 text-blue-700';
  if (status === 'SHIPPED') return 'bg-primary/15 text-primary';
  if (status === 'COMPLETED') return 'bg-emerald-100 text-emerald-700';
  return 'bg-slate-200 text-slate-700';
}

function stepStateClass(status: OrderStatus, step: OrderStatus) {
  const order = ['UNPAID', 'PAID', 'SHIPPED', 'COMPLETED'];
  const currentIndex = order.indexOf(status);
  const stepIndex = order.indexOf(step);
  if (currentIndex >= stepIndex) {
    return 'bg-primary text-white shadow-md shadow-primary/20';
  }
  return 'bg-white text-slate-400 ring-1 ring-slate-200';
}

async function loadOrders() {
  loading.value = true;
  try {
    orders.value = await orderApi.list();
  } finally {
    loading.value = false;
  }
}

function goPayment(orderId: number) {
  router.push(`/payment/${orderId}`);
}

function toggleDetail(orderId: number) {
  expandedOrderId.value = expandedOrderId.value === orderId ? 0 : orderId;
}

onMounted(() => {
  const tab = route.query.tab as string | undefined;
  const validTabs = ['all', 'UNPAID', 'PAID', 'SHIPPED', 'COMPLETED'];
  if (tab && validTabs.includes(tab)) {
    activeTab.value = tab as typeof activeTab.value;
  }
  loadOrders().catch((error) => toast.error((error as Error).message));
});
</script>
