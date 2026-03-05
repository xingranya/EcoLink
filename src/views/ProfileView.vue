<template>
  <div class="mx-auto flex min-h-screen w-full max-w-[1440px] flex-col bg-background-light lg:flex-row">
    <aside class="flex w-full flex-col gap-8 border-r border-primary/10 bg-white p-6 lg:w-72">
      <div class="flex items-center gap-3">
        <div class="flex h-10 w-10 items-center justify-center rounded-full bg-primary text-white">
          <span class="material-symbols-outlined">account_circle</span>
        </div>
        <h2 class="text-sm font-bold uppercase tracking-tight text-primary">会员中心</h2>
      </div>
      <nav class="flex flex-1 flex-col gap-2">
        <button class="flex items-center gap-3 rounded-lg bg-primary/10 px-4 py-3 font-medium text-primary">
          <span class="material-symbols-outlined">person</span>
          <span>个人信息</span>
        </button>
        <RouterLink class="flex items-center gap-3 rounded-lg px-4 py-3 text-slate-600 transition-colors hover:bg-primary/5" to="/orders">
          <span class="material-symbols-outlined">package_2</span>
          <span>我的订单</span>
        </RouterLink>
        <a class="flex items-center gap-3 rounded-lg px-4 py-3 text-slate-600 transition-colors hover:bg-primary/5" href="#address">
          <span class="material-symbols-outlined">location_on</span>
          <span>收货地址</span>
        </a>
        <a class="flex items-center gap-3 rounded-lg px-4 py-3 text-slate-600 transition-colors hover:bg-primary/5" href="#favorite">
          <span class="material-symbols-outlined">favorite</span>
          <span>我的收藏</span>
        </a>
      </nav>
      <div class="border-t border-primary/10 pt-6">
        <button
          class="flex w-full items-center justify-center gap-2 rounded-lg border border-slate-200 px-4 py-2.5 font-medium text-slate-600 transition-colors hover:bg-slate-50"
          @click="logout"
        >
          <span class="material-symbols-outlined text-[20px]">logout</span>
          <span>退出登录</span>
        </button>
      </div>
    </aside>

    <main class="flex-1 space-y-8 overflow-y-auto p-4 lg:p-10">
      <section class="rounded-xl border border-primary/5 bg-white p-6 shadow-sm">
        <div class="flex flex-col items-center gap-6 @[520px]:flex-row">
          <div class="relative">
            <div class="h-24 w-24 overflow-hidden rounded-full border-4 border-primary/10 bg-slate-200">
              <img
                class="h-full w-full object-cover"
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuDld12pg3uP81sWSexPmi6RpfM_oQJAfsX4U3OH48IE3M-N8Pay03ZK1AgfOHZ2r96cPRKq9qtFLTHcB15S1ur1drQrfxn_XzxpMvoV0esEMJE5BkpvraDYYHpb4AwonHeknI0Vv8eJfhU3kjmKFRd5XuTCf2ZhHnuMauyFLcN-DSPRL0MK761Lki2lp2m81_HQOzsGvxcmYCN3rrW76GBbHjx_gZJ9Fcd7k7IKr0237y3TploYvv58RH_tnaOzH6VQ-A6rUUXPX79i"
                alt="avatar"
              />
            </div>
            <div class="absolute bottom-0 right-0 flex h-7 w-7 items-center justify-center rounded-full border-2 border-white bg-primary">
              <span class="material-symbols-outlined text-[16px] text-white">edit</span>
            </div>
          </div>
          <div class="space-y-1 text-center @[520px]:text-left">
            <h1 class="text-2xl font-bold">{{ auth.user?.nickname || auth.user?.username }}</h1>
            <div class="flex items-center justify-center gap-2 @[520px]:justify-start">
              <span class="rounded bg-primary/20 px-2 py-0.5 text-xs font-bold uppercase tracking-wider text-primary">黄金会员</span>
              <span class="text-sm text-slate-500">欢迎回来</span>
            </div>
            <p class="text-sm text-slate-500">手机号：{{ auth.user?.phone || '-' }}</p>
          </div>
        </div>
      </section>

      <section class="grid grid-cols-2 gap-4 md:grid-cols-4">
        <div class="group flex cursor-pointer flex-col items-center justify-center gap-1 rounded-xl border border-primary/5 bg-white p-5 transition-all hover:border-primary/30">
          <span class="material-symbols-outlined mb-1 text-primary transition-transform group-hover:scale-110">payments</span>
          <span class="text-2xl font-bold">{{ orderStats.unpaid }}</span>
          <span class="text-xs font-medium uppercase tracking-tighter text-slate-500">待付款</span>
        </div>
        <div class="group flex cursor-pointer flex-col items-center justify-center gap-1 rounded-xl border border-primary/5 bg-white p-5 transition-all hover:border-primary/30">
          <span class="material-symbols-outlined mb-1 text-primary transition-transform group-hover:scale-110">local_shipping</span>
          <span class="text-2xl font-bold">{{ orderStats.paid }}</span>
          <span class="text-xs font-medium uppercase tracking-tighter text-slate-500">待发货</span>
        </div>
        <div class="group flex cursor-pointer flex-col items-center justify-center gap-1 rounded-xl border border-primary/5 bg-white p-5 transition-all hover:border-primary/30">
          <span class="material-symbols-outlined mb-1 text-primary transition-transform group-hover:scale-110">inventory_2</span>
          <span class="text-2xl font-bold">{{ orderStats.shipped }}</span>
          <span class="text-xs font-medium uppercase tracking-tighter text-slate-500">待收货</span>
        </div>
        <div class="group flex cursor-pointer flex-col items-center justify-center gap-1 rounded-xl border border-primary/5 bg-white p-5 transition-all hover:border-primary/30">
          <span class="material-symbols-outlined mb-1 text-primary transition-transform group-hover:scale-110">task_alt</span>
          <span class="text-2xl font-bold">{{ orderStats.completed }}</span>
          <span class="text-xs font-medium uppercase tracking-tighter text-slate-500">已完成</span>
        </div>
      </section>

      <section id="address" class="space-y-4">
        <div class="flex items-center justify-between px-2">
          <h2 class="flex items-center gap-2 text-lg font-bold">
            <span class="material-symbols-outlined text-primary">location_on</span>
            收货地址
          </h2>
        </div>
        <div class="rounded-xl border border-primary/5 bg-white p-4">
          <form class="grid grid-cols-1 gap-2 md:grid-cols-4" @submit.prevent="submitAddress">
            <input v-model.trim="addressForm.receiverName" required placeholder="收件人" class="w-full rounded border border-slate-200 px-3 py-2 text-sm" />
            <input v-model.trim="addressForm.receiverPhone" required placeholder="手机号" class="w-full rounded border border-slate-200 px-3 py-2 text-sm" />
            <input v-model.trim="addressForm.detail" required placeholder="详细地址" class="w-full rounded border border-slate-200 px-3 py-2 text-sm md:col-span-2" />
            <label class="flex items-center gap-2 rounded bg-slate-50 px-3 py-2 text-sm">
              <input v-model="addressForm.isDefault" type="checkbox" />
              设为默认地址
            </label>
            <div class="flex gap-2">
              <button class="rounded bg-primary px-4 py-2 text-sm font-bold text-white" :disabled="addressSubmitting">
                {{ addressSubmitting ? '提交中...' : editingAddressId ? '保存修改' : '新增地址' }}
              </button>
              <button v-if="editingAddressId" type="button" class="rounded border border-slate-300 px-4 py-2 text-sm font-bold text-slate-700" @click="resetAddressForm">取消</button>
            </div>
          </form>
        </div>
        <div class="space-y-3">
          <article v-for="item in addresses" :key="item.id" class="rounded-xl border border-primary/5 bg-white p-3">
            <p class="text-sm leading-6">
              {{ item.receiverName }} {{ item.receiverPhone }} - {{ item.detail }}
              <span v-if="item.isDefault" class="ml-2 rounded bg-primary/10 px-2 py-0.5 text-xs text-primary">默认</span>
            </p>
            <div class="mt-2 flex flex-wrap gap-2">
              <button class="rounded border border-slate-300 px-3 py-1 text-xs font-bold" @click="editAddress(item)">编辑</button>
              <button class="rounded bg-primary/10 px-3 py-1 text-xs font-bold text-primary" :disabled="item.isDefault" @click="setDefaultAddress(item)">
                设为默认
              </button>
              <button class="rounded bg-red-50 px-3 py-1 text-xs font-bold text-red-600" @click="deleteAddress(item.id)">删除</button>
            </div>
          </article>
          <p v-if="!addresses.length" class="rounded-xl border border-dashed border-primary/20 bg-white p-6 text-center text-sm text-slate-500">暂无地址</p>
        </div>
      </section>

      <section id="favorite" class="space-y-4">
        <div class="flex items-center justify-between px-2">
          <h2 class="flex items-center gap-2 text-lg font-bold">
            <span class="material-symbols-outlined text-primary">favorite</span>
            我的收藏
          </h2>
        </div>
        <div class="space-y-3">
          <article v-for="item in favorites" :key="item.id" class="group flex items-center gap-4 rounded-xl border border-primary/5 bg-white p-3 transition-all hover:border-primary/20">
            <RouterLink :to="`/product/${item.productId}`" class="h-16 w-16 flex-shrink-0 overflow-hidden rounded-lg bg-slate-100">
              <img :src="item.productImage || fallbackImage" class="h-full w-full object-cover transition-transform group-hover:scale-110" />
            </RouterLink>
            <div class="min-w-0 flex-1">
              <RouterLink :to="`/product/${item.productId}`" class="truncate text-sm font-bold hover:text-primary">{{ item.productName }}</RouterLink>
              <p class="font-bold text-primary">¥{{ Number(item.price).toFixed(2) }}</p>
            </div>
            <button class="text-slate-300 transition-colors hover:text-red-500" @click="cancelFavorite(item.productId)">
              <span class="material-symbols-outlined">delete</span>
            </button>
          </article>
          <p v-if="!favorites.length" class="rounded-xl border border-dashed border-primary/20 bg-white p-6 text-center text-sm text-slate-500">暂无收藏商品</p>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import { addressApi, favoriteApi, orderApi } from '@/api';
import { useAuthStore } from '@/stores/auth';
import type { Address, FavoriteItem, OrderData } from '@/types/api';

const auth = useAuthStore();
const router = useRouter();
const addresses = ref<Address[]>([]);
const favorites = ref<FavoriteItem[]>([]);
const orders = ref<OrderData[]>([]);
const editingAddressId = ref(0);
const addressSubmitting = ref(false);
const fallbackImage = 'https://images.unsplash.com/photo-1608797178974-15b35a64ede9?auto=format&fit=crop&w=1200&q=80';

const addressForm = reactive({
  receiverName: '',
  receiverPhone: '',
  detail: '',
  isDefault: false,
});

const orderStats = computed(() => ({
  unpaid: orders.value.filter((item) => item.status === 'UNPAID').length,
  paid: orders.value.filter((item) => item.status === 'PAID').length,
  shipped: orders.value.filter((item) => item.status === 'SHIPPED').length,
  completed: orders.value.filter((item) => item.status === 'COMPLETED').length,
}));

function resetAddressForm() {
  editingAddressId.value = 0;
  addressForm.receiverName = '';
  addressForm.receiverPhone = '';
  addressForm.detail = '';
  addressForm.isDefault = false;
}

function editAddress(item: Address) {
  editingAddressId.value = item.id;
  addressForm.receiverName = item.receiverName;
  addressForm.receiverPhone = item.receiverPhone;
  addressForm.detail = item.detail;
  addressForm.isDefault = item.isDefault;
}

async function loadData() {
  if (!auth.user) await auth.fetchMe();
  [addresses.value, favorites.value, orders.value] = await Promise.all([addressApi.list(), favoriteApi.list(), orderApi.list()]);
}

async function submitAddress() {
  addressSubmitting.value = true;
  try {
    if (editingAddressId.value) {
      await addressApi.update(editingAddressId.value, {
        receiverName: addressForm.receiverName,
        receiverPhone: addressForm.receiverPhone,
        detail: addressForm.detail,
        isDefault: addressForm.isDefault,
      });
    } else {
      await addressApi.create({
        receiverName: addressForm.receiverName,
        receiverPhone: addressForm.receiverPhone,
        detail: addressForm.detail,
        isDefault: addressForm.isDefault,
      });
    }
    addresses.value = await addressApi.list();
    resetAddressForm();
  } catch (error) {
    alert((error as Error).message);
  } finally {
    addressSubmitting.value = false;
  }
}

async function setDefaultAddress(item: Address) {
  if (item.isDefault) return;
  try {
    await addressApi.update(item.id, {
      receiverName: item.receiverName,
      receiverPhone: item.receiverPhone,
      detail: item.detail,
      isDefault: true,
    });
    addresses.value = await addressApi.list();
  } catch (error) {
    alert((error as Error).message);
  }
}

async function deleteAddress(id: number) {
  try {
    await addressApi.remove(id);
    addresses.value = await addressApi.list();
    if (editingAddressId.value === id) resetAddressForm();
  } catch (error) {
    alert((error as Error).message);
  }
}

async function cancelFavorite(productId: number) {
  try {
    await favoriteApi.remove(productId);
    favorites.value = await favoriteApi.list();
  } catch (error) {
    alert((error as Error).message);
  }
}

function logout() {
  auth.clearSession();
  router.push('/login');
}

onMounted(() => {
  loadData().catch((error) => alert((error as Error).message));
});
</script>
