<template>
  <div class="flex-1 bg-background-light text-slate-900">
    <main class="mx-auto max-w-5xl px-4 py-8">
      <!-- 支付成功 -->
      <div v-if="paySuccess" class="mx-auto max-w-lg py-16 text-center">
        <div class="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-emerald-100">
          <span class="material-symbols-outlined text-5xl text-emerald-500">check_circle</span>
        </div>
        <h1 class="mb-2 text-2xl font-black text-slate-900">支付成功</h1>
        <p class="mb-1 text-slate-500">订单号: {{ order?.orderNo }}</p>
        <p class="mb-8 text-sm text-slate-400">商家将尽快为您安排发货</p>
        <div class="flex justify-center gap-4">
          <RouterLink to="/orders" class="btn btn-primary">查看订单</RouterLink>
          <RouterLink to="/" class="btn btn-outline">继续购物</RouterLink>
        </div>
      </div>

      <!-- 支付页面 -->
      <template v-else-if="order">
        <!-- 步骤条 -->
        <div class="mb-8 overflow-hidden rounded-2xl border border-primary/5 bg-white p-6 shadow-sm">
          <div class="mx-auto flex max-w-xl items-center justify-center">
            <div class="flex flex-col items-center gap-2">
              <div class="flex h-10 w-10 items-center justify-center rounded-full bg-primary/20 text-primary">
                <span class="material-symbols-outlined text-lg">check</span>
              </div>
              <span class="text-xs font-bold text-primary/60">购物车</span>
            </div>
            <div class="mb-6 h-[2px] flex-1 max-w-20 bg-primary/30"></div>
            <div class="flex flex-col items-center gap-2">
              <div class="flex h-10 w-10 items-center justify-center rounded-full bg-primary/20 text-primary">
                <span class="material-symbols-outlined text-lg">check</span>
              </div>
              <span class="text-xs font-bold text-primary/60">确认订单</span>
            </div>
            <div class="mb-6 h-[2px] flex-1 max-w-20 bg-primary/30"></div>
            <div class="flex flex-col items-center gap-2">
              <div class="flex h-10 w-10 items-center justify-center rounded-full bg-primary text-white shadow-md shadow-primary/20">
                <span class="material-symbols-outlined text-lg">payments</span>
              </div>
              <span class="text-sm font-bold text-primary">支付</span>
            </div>
          </div>
        </div>

        <div class="flex flex-col gap-6 lg:flex-row">
          <!-- 左列 -->
          <div class="flex-1 space-y-6">
            <!-- 订单信息 -->
            <div class="rounded-2xl border border-primary/5 bg-white p-6 shadow-sm">
              <div class="mb-4 flex items-center justify-between">
                <h2 class="flex items-center gap-2 text-lg font-black">
                  <span class="material-symbols-outlined text-primary">receipt_long</span>
                  订单信息
                </h2>
                <span class="text-sm text-slate-400">#{{ order.orderNo }}</span>
              </div>
              <div class="space-y-4">
                <div v-for="item in order.items" :key="item.id" class="flex items-center gap-4 rounded-xl bg-slate-50 p-3">
                  <img :src="item.productImage || fallbackImage" :alt="item.productName" class="h-16 w-16 flex-shrink-0 rounded-lg object-cover" loading="lazy" />
                  <div class="min-w-0 flex-1">
                    <p class="truncate font-bold text-slate-900">{{ item.productName }}</p>
                    <p class="text-sm text-slate-500">x{{ item.quantity }}</p>
                  </div>
                  <p class="font-bold text-slate-900">¥{{ Number(item.salePrice).toFixed(2) }}</p>
                </div>
              </div>
            </div>

            <!-- 收货信息 -->
            <div class="rounded-2xl border border-primary/5 bg-white p-6 shadow-sm">
              <h2 class="mb-4 flex items-center gap-2 text-lg font-black">
                <span class="material-symbols-outlined text-primary">local_shipping</span>
                收货信息
              </h2>
              <div class="space-y-2 text-sm">
                <p><span class="font-bold">{{ order.receiverName }}</span> <span class="text-slate-500">{{ order.receiverPhone }}</span></p>
                <p class="text-slate-600">{{ order.receiverAddress }}</p>
              </div>
            </div>
          </div>

          <!-- 右列 -->
          <div class="w-full lg:w-80">
            <div class="sticky top-24 space-y-6">
              <!-- 倒计时 -->
              <div class="rounded-2xl border border-amber-200 bg-amber-50 p-4 text-center">
                <p class="mb-1 text-sm text-amber-700">
                  <span class="material-symbols-outlined mr-1 align-middle text-base">schedule</span>
                  请在规定时间内完成支付
                </p>
                <p class="text-2xl font-black tabular-nums text-amber-600">{{ countdownDisplay }}</p>
              </div>

              <!-- 支付方式 -->
              <div class="rounded-2xl border border-primary/5 bg-white p-6 shadow-sm">
                <h3 class="mb-4 flex items-center gap-2 font-bold">
                  <span class="material-symbols-outlined text-primary">credit_card</span>
                  支付方式
                </h3>
                <div class="space-y-3">
                  <label v-for="method in paymentMethods" :key="method.id" class="flex cursor-pointer items-center gap-3 rounded-xl border-2 p-3 transition-all" :class="selectedMethod === method.id ? 'border-primary bg-primary/5' : 'border-slate-100 hover:border-primary/20'">
                    <input v-model="selectedMethod" :value="method.id" type="radio" name="payment" class="h-4 w-4 border-primary/30 text-primary focus:ring-primary" />
                    <span class="material-symbols-outlined text-xl" :class="method.color">{{ method.icon }}</span>
                    <span class="font-bold">{{ method.label }}</span>
                  </label>
                </div>
              </div>

              <!-- 金额汇总 -->
              <div class="rounded-2xl border border-primary/10 bg-white p-6 shadow-lg">
                <div class="mb-4 space-y-2 text-sm">
                  <div class="flex justify-between">
                    <span class="text-slate-500">商品总额</span>
                    <span class="font-medium">¥{{ Number(order.totalAmount).toFixed(2) }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-slate-500">运费</span>
                    <span class="font-bold text-primary">免运费</span>
                  </div>
                </div>
                <div class="mb-6 flex items-end justify-between border-t border-primary/10 pt-4">
                  <span class="font-bold">实付款</span>
                  <span class="text-3xl font-black text-orange-500">¥{{ Number(order.totalAmount).toFixed(2) }}</span>
                </div>
                <button
                  class="flex w-full items-center justify-center gap-2 rounded-xl bg-gradient-to-r from-primary to-emerald-500 py-4 text-lg font-bold text-white shadow-lg shadow-primary/30 transition-all hover:shadow-xl active:scale-[0.98] disabled:opacity-60"
                  :disabled="paying"
                  @click="confirmPay"
                >
                  <span class="material-symbols-outlined">lock</span>
                  {{ paying ? '支付处理中...' : `确认支付 ¥${Number(order.totalAmount).toFixed(2)}` }}
                </button>
              </div>

              <!-- 安全保障 -->
              <div class="rounded-2xl border border-primary/5 bg-white p-4">
                <div class="grid grid-cols-3 gap-3">
                  <div class="flex flex-col items-center gap-1">
                    <span class="material-symbols-outlined text-primary">verified_user</span>
                    <span class="text-[10px] font-semibold text-slate-500">安全支付</span>
                  </div>
                  <div class="flex flex-col items-center gap-1">
                    <span class="material-symbols-outlined text-primary">shield</span>
                    <span class="text-[10px] font-semibold text-slate-500">隐私保护</span>
                  </div>
                  <div class="flex flex-col items-center gap-1">
                    <span class="material-symbols-outlined text-primary">workspace_premium</span>
                    <span class="text-[10px] font-semibold text-slate-500">正品保证</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- 加载中 -->
      <div v-else-if="loading" class="py-16 text-center text-slate-500">订单加载中...</div>
      <div v-else class="py-16 text-center text-slate-500">订单不存在</div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';
import { orderApi } from '@/api';
import type { OrderData } from '@/types/api';

const route = useRoute();
const router = useRouter();
const order = ref<OrderData | null>(null);
const loading = ref(true);
const paying = ref(false);
const paySuccess = ref(false);
const selectedMethod = ref('wechat');
const countdown = ref(15 * 60);
const fallbackImage = 'https://images.unsplash.com/photo-1608797178974-15b35a64ede9?auto=format&fit=crop&w=1200&q=80';

const paymentMethods = [
  { id: 'wechat', label: '微信支付', icon: 'chat', color: 'text-green-500' },
  { id: 'alipay', label: '支付宝', icon: 'account_balance_wallet', color: 'text-blue-500' },
  { id: 'bank', label: '银行卡支付', icon: 'credit_card', color: 'text-amber-600' },
];

const countdownDisplay = computed(() => {
  const m = Math.floor(countdown.value / 60).toString().padStart(2, '0');
  const s = (countdown.value % 60).toString().padStart(2, '0');
  return `${m}:${s}`;
});

let timer: ReturnType<typeof setInterval> | null = null;

function startCountdown() {
  timer = setInterval(() => {
    if (countdown.value <= 0) {
      if (timer) clearInterval(timer);
      return;
    }
    countdown.value--;
  }, 1000);
}

async function confirmPay() {
  if (!order.value) return;
  paying.value = true;
  try {
    await orderApi.pay(order.value.id);
    paySuccess.value = true;
    if (timer) clearInterval(timer);
  } catch (error) {
    alert((error as Error).message);
  } finally {
    paying.value = false;
  }
}

onMounted(async () => {
  const orderId = Number(route.params.id);
  if (!orderId) {
    loading.value = false;
    return;
  }
  try {
    order.value = await orderApi.detail(orderId);
    if (order.value.status !== 'UNPAID') {
      router.replace('/orders');
      return;
    }
    startCountdown();
  } catch (error) {
    alert((error as Error).message);
  } finally {
    loading.value = false;
  }
});

onUnmounted(() => {
  if (timer) clearInterval(timer);
});
</script>
