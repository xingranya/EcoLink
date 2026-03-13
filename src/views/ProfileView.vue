<template>
  <div class="flex-1 bg-background-light">
    <main class="mx-auto w-full max-w-7xl px-4 py-8 lg:px-8">
      <section class="relative mb-8 overflow-hidden rounded-2xl bg-gradient-to-r from-primary to-emerald-600 p-8 text-white shadow-lg shadow-primary/20">
        <div class="absolute -right-16 -top-16 h-64 w-64 rounded-full bg-white/10"></div>
        <div class="absolute -bottom-20 -right-8 h-48 w-48 rounded-full bg-white/5"></div>
        <div class="relative flex flex-col items-center gap-6 sm:flex-row">
          <div class="relative">
            <div class="h-24 w-24 overflow-hidden rounded-full border-4 border-white/30 bg-white/20">
              <img
                class="h-full w-full object-cover"
                src="https://img1.tucang.cc/api/image/show/3c24a83d2cac00178a1fba68decd7150"
                alt="用户头像"
                loading="lazy"
              />
            </div>
            <button class="absolute bottom-0 right-0 flex h-7 w-7 items-center justify-center rounded-full border-2 border-white bg-white/90 text-primary transition-transform hover:scale-110">
              <span class="material-symbols-outlined text-[16px]">edit</span>
            </button>
          </div>
          <div class="text-center sm:text-left">
            <h1 class="text-2xl font-black">{{ auth.user?.nickname || auth.user?.username }}</h1>
            <div class="mt-1 flex items-center justify-center gap-3 sm:justify-start">
              <span class="flex items-center gap-1 rounded-full bg-white/20 px-3 py-0.5 text-xs font-bold backdrop-blur-sm">
                <span class="material-symbols-outlined text-sm text-yellow-300">star</span>
                黄金会员
              </span>
              <span class="flex items-center gap-1 text-sm text-white/80">
                <span class="material-symbols-outlined text-sm">phone_iphone</span>
                {{ auth.user?.phone || '-' }}
              </span>
            </div>
          </div>
          <div class="flex gap-2 sm:ml-auto">
            <RouterLink
              v-if="auth.isAdmin"
              to="/admin/dashboard"
              class="rounded-xl bg-white px-4 py-2 text-sm font-bold text-primary transition-colors hover:bg-white/90"
            >
              <span class="material-symbols-outlined mr-1 text-base">admin_panel_settings</span>
              进入后台
            </RouterLink>
            <button class="rounded-xl bg-white/15 px-4 py-2 text-sm font-bold backdrop-blur-sm transition-colors hover:bg-white/25" @click="logout">
              <span class="material-symbols-outlined mr-1 text-base">logout</span>
              退出登录
            </button>
          </div>
        </div>
      </section>

      <section class="mb-8 grid grid-cols-2 gap-4 md:grid-cols-4">
        <RouterLink to="/orders?tab=UNPAID" class="group flex cursor-pointer flex-col items-center justify-center gap-2 rounded-xl border border-primary/5 bg-white p-6 shadow-sm transition-all hover:-translate-y-0.5 hover:border-primary/20 hover:shadow-md">
          <div class="flex h-12 w-12 items-center justify-center rounded-full bg-amber-50 transition-transform group-hover:scale-110">
            <span class="material-symbols-outlined text-amber-500">payments</span>
          </div>
          <span class="text-3xl font-black">{{ String(orderStats.unpaid).padStart(2, '0') }}</span>
          <span class="text-xs font-semibold uppercase tracking-wider text-slate-500">待付款</span>
        </RouterLink>
        <RouterLink to="/orders?tab=PAID" class="group flex cursor-pointer flex-col items-center justify-center gap-2 rounded-xl border border-primary/5 bg-white p-6 shadow-sm transition-all hover:-translate-y-0.5 hover:border-primary/20 hover:shadow-md">
          <div class="flex h-12 w-12 items-center justify-center rounded-full bg-blue-50 transition-transform group-hover:scale-110">
            <span class="material-symbols-outlined text-blue-500">local_shipping</span>
          </div>
          <span class="text-3xl font-black">{{ String(orderStats.paid).padStart(2, '0') }}</span>
          <span class="text-xs font-semibold uppercase tracking-wider text-slate-500">待发货</span>
        </RouterLink>
        <RouterLink to="/orders?tab=SHIPPED" class="group flex cursor-pointer flex-col items-center justify-center gap-2 rounded-xl border border-primary/5 bg-white p-6 shadow-sm transition-all hover:-translate-y-0.5 hover:border-primary/20 hover:shadow-md">
          <div class="flex h-12 w-12 items-center justify-center rounded-full bg-primary/10 transition-transform group-hover:scale-110">
            <span class="material-symbols-outlined text-primary">inventory_2</span>
          </div>
          <span class="text-3xl font-black">{{ String(orderStats.shipped).padStart(2, '0') }}</span>
          <span class="text-xs font-semibold uppercase tracking-wider text-slate-500">待收货</span>
        </RouterLink>
        <RouterLink to="/orders?tab=COMPLETED" class="group flex cursor-pointer flex-col items-center justify-center gap-2 rounded-xl border border-primary/5 bg-white p-6 shadow-sm transition-all hover:-translate-y-0.5 hover:border-primary/20 hover:shadow-md">
          <div class="flex h-12 w-12 items-center justify-center rounded-full bg-emerald-50 transition-transform group-hover:scale-110">
            <span class="material-symbols-outlined text-emerald-500">task_alt</span>
          </div>
          <span class="text-3xl font-black">{{ String(orderStats.completed).padStart(2, '0') }}</span>
          <span class="text-xs font-semibold uppercase tracking-wider text-slate-500">已完成</span>
        </RouterLink>
      </section>

      <section id="address" class="space-y-4">
        <div class="flex items-center justify-between">
          <h2 class="flex items-center gap-2 text-lg font-black">
            <span class="material-symbols-outlined text-primary">location_on</span>
            收货地址
          </h2>
        </div>
        <div class="rounded-2xl border border-primary/5 bg-white p-5 shadow-sm">
          <form class="grid grid-cols-1 gap-3 md:grid-cols-2 lg:grid-cols-4" @submit.prevent="submitAddress">
            <div class="relative">
              <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-sm text-slate-400">person</span>
              <input v-model.trim="addressForm.receiverName" required placeholder="收件人姓名" class="input-control !pl-9" />
            </div>
            <div class="relative">
              <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-sm text-slate-400">phone</span>
              <input v-model.trim="addressForm.receiverPhone" required placeholder="手机号码" class="input-control !pl-9" />
            </div>
            <div class="relative md:col-span-2">
              <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-sm text-slate-400">home</span>
              <input v-model.trim="addressForm.detail" required placeholder="详细地址" class="input-control !pl-9" />
            </div>
            <label class="flex cursor-pointer items-center gap-2 rounded-xl bg-primary/5 px-4 py-2.5 text-sm text-slate-600">
              <input v-model="addressForm.isDefault" type="checkbox" class="h-4 w-4 rounded border-primary/30 text-primary focus:ring-primary" />
              设为默认地址
            </label>
            <div class="flex gap-2">
              <button class="btn btn-primary !py-2.5" :disabled="addressSubmitting">
                {{ addressSubmitting ? '提交中...' : editingAddressId ? '保存修改' : '新增地址' }}
              </button>
              <button v-if="editingAddressId" type="button" class="btn btn-outline !py-2.5" @click="resetAddressForm">取消</button>
            </div>
          </form>
        </div>
        <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
          <article v-for="item in addresses" :key="item.id" class="rounded-2xl border border-primary/5 bg-white p-5 shadow-sm transition-all hover:border-primary/20 hover:shadow-md">
            <div class="mb-3 flex items-start justify-between">
              <div>
                <div class="flex items-center gap-2">
                  <h4 class="font-bold text-slate-900">{{ item.receiverName }}</h4>
                  <span v-if="item.isDefault" class="rounded-full bg-primary/15 px-2.5 py-0.5 text-[10px] font-bold text-primary">默认</span>
                </div>
                <p class="mt-0.5 text-sm text-slate-500">{{ item.receiverPhone }}</p>
              </div>
            </div>
            <p class="mb-4 text-sm leading-relaxed text-slate-600">{{ item.detail }}</p>
            <div class="flex gap-2 border-t border-slate-100 pt-3">
              <button class="rounded-lg border border-slate-200 px-3 py-1.5 text-xs font-bold text-slate-600 transition-colors hover:bg-slate-50" @click="editAddress(item)">
                <span class="material-symbols-outlined mr-1 text-xs">edit</span>编辑
              </button>
              <button class="rounded-lg bg-primary/10 px-3 py-1.5 text-xs font-bold text-primary transition-colors hover:bg-primary/20" :disabled="item.isDefault" @click="setDefaultAddress(item)">
                设为默认
              </button>
              <button class="rounded-lg bg-red-50 px-3 py-1.5 text-xs font-bold text-red-500 transition-colors hover:bg-red-100" @click="deleteAddress(item.id)">
                <span class="material-symbols-outlined mr-1 text-xs">delete</span>删除
              </button>
            </div>
          </article>
          <button v-if="addresses.length < 5" class="flex flex-col items-center justify-center gap-2 rounded-2xl border-2 border-dashed border-primary/20 bg-white p-8 text-primary transition-colors hover:border-primary/40 hover:bg-primary/5" @click="resetAddressForm">
            <span class="material-symbols-outlined text-3xl">add_circle</span>
            <span class="text-sm font-bold">添加新地址</span>
          </button>
        </div>
        <p v-if="!addresses.length" class="rounded-2xl border-2 border-dashed border-primary/20 bg-white p-8 text-center text-sm text-slate-500">暂无地址，请添加收货地址</p>
      </section>

      <section id="favorite" class="space-y-4">
        <div class="flex items-center justify-between">
          <h2 class="flex items-center gap-2 text-lg font-black">
            <span class="material-symbols-outlined text-primary">favorite</span>
            我的收藏
          </h2>
        </div>
        <div class="grid grid-cols-2 gap-4 md:grid-cols-4">
          <article v-for="item in favorites" :key="item.id" class="group overflow-hidden rounded-2xl border border-primary/5 bg-white shadow-sm transition-all hover:-translate-y-0.5 hover:shadow-md">
            <RouterLink :to="`/product/${item.productId}`" class="relative block aspect-square overflow-hidden bg-slate-100">
              <img :src="item.productImage || fallbackImage" :alt="item.productName" class="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105" loading="lazy" />
              <button class="absolute right-2 top-2 rounded-full bg-white/80 p-1.5 text-red-400 backdrop-blur-sm transition-colors hover:bg-white hover:text-red-500" @click.prevent="cancelFavorite(item.productId)">
                <span class="material-symbols-outlined text-lg">favorite</span>
              </button>
            </RouterLink>
            <div class="p-3">
              <RouterLink :to="`/product/${item.productId}`" class="line-clamp-1 text-sm font-bold text-slate-900 hover:text-primary">{{ item.productName }}</RouterLink>
              <div class="mt-2 flex items-center justify-between">
                <span class="font-bold text-primary">¥{{ Number(item.price).toFixed(2) }}</span>
                <button class="rounded-full bg-primary/10 p-1.5 text-primary transition-colors hover:bg-primary hover:text-white">
                  <span class="material-symbols-outlined text-sm">add_shopping_cart</span>
                </button>
              </div>
            </div>
          </article>
        </div>
        <p v-if="!favorites.length" class="rounded-2xl border-2 border-dashed border-primary/20 bg-white p-8 text-center text-sm text-slate-500">暂无收藏商品</p>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';
import { addressApi, favoriteApi, orderApi } from '@/api';
import { useAuthStore } from '@/stores/auth';
import { useToastStore } from '@/stores/toast';
import type { Address, FavoriteItem, OrderData } from '@/types/api';

const auth = useAuthStore();
const router = useRouter();
const route = useRoute();
const toast = useToastStore();
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
    toast.success(editingAddressId.value ? '地址修改成功' : '地址添加成功');
    const redirect = route.query.redirect as string | undefined;
    if (redirect) {
      router.push(redirect);
    }
  } catch (error) {
    toast.error((error as Error).message);
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
    toast.success('默认地址已更新');
  } catch (error) {
    toast.error((error as Error).message);
  }
}

async function deleteAddress(id: number) {
  try {
    await addressApi.remove(id);
    addresses.value = await addressApi.list();
    if (editingAddressId.value === id) resetAddressForm();
    toast.success('地址已删除');
  } catch (error) {
    toast.error((error as Error).message);
  }
}

async function cancelFavorite(productId: number) {
  try {
    await favoriteApi.remove(productId);
    favorites.value = await favoriteApi.list();
    toast.success('已取消收藏');
  } catch (error) {
    toast.error((error as Error).message);
  }
}

function logout() {
  auth.clearSession();
  router.push('/login');
}

onMounted(() => {
  loadData().catch((error) => toast.error((error as Error).message));
  if (route.query.tab === 'address') {
    setTimeout(() => {
      document.getElementById('address')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 300);
  }
});
</script>
