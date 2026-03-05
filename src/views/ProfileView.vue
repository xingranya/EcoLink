<template>
  <div class="page-shell">
    <AppHeader />
    <main class="page-main">
      <div class="section-head">
        <div>
          <h1 class="section-title">个人中心</h1>
          <p class="section-subtitle">账号、地址、收藏一站式管理</p>
        </div>
      </div>

      <section class="surface-card p-5">
        <h2 class="text-lg font-black">用户信息</h2>
        <div class="mt-3 grid grid-cols-1 gap-2 md:grid-cols-3">
          <p class="rounded-2xl bg-[#f2f8f4] p-3 text-sm text-slate-700">用户名：{{ auth.user?.username }}</p>
          <p class="rounded-2xl bg-[#f2f8f4] p-3 text-sm text-slate-700">昵称：{{ auth.user?.nickname }}</p>
          <p class="rounded-2xl bg-[#f2f8f4] p-3 text-sm text-slate-700">手机号：{{ auth.user?.phone || '-' }}</p>
        </div>
      </section>

      <section class="surface-card mt-6 p-5">
        <h2 class="text-lg font-black">地址管理</h2>
        <form class="mt-4 grid grid-cols-1 gap-2 md:grid-cols-4" @submit.prevent="submitAddress">
          <input v-model.trim="addressForm.receiverName" required placeholder="收件人" class="input-control" />
          <input v-model.trim="addressForm.receiverPhone" required placeholder="手机号" class="input-control" />
          <input v-model.trim="addressForm.detail" required placeholder="详细地址" class="input-control md:col-span-2" />
          <label class="flex items-center gap-2 rounded-xl bg-[#f2f7f3] px-3 py-2 text-sm">
            <input v-model="addressForm.isDefault" type="checkbox" />
            设为默认地址
          </label>
          <div class="flex gap-2">
            <button class="btn btn-primary !px-4 !py-2" :disabled="addressSubmitting">
              {{ addressSubmitting ? '提交中...' : editingAddressId ? '保存修改' : '新增地址' }}
            </button>
            <button v-if="editingAddressId" type="button" class="btn btn-outline !px-4 !py-2" @click="resetAddressForm">取消</button>
          </div>
        </form>
        <div class="mt-4 space-y-2">
          <article v-for="item in addresses" :key="item.id" class="rounded-2xl border border-[#d6e4d9] bg-white p-3">
            <p class="text-sm leading-6">
              {{ item.receiverName }} {{ item.receiverPhone }} - {{ item.detail }}
              <span v-if="item.isDefault" class="ml-2 rounded bg-primary/10 px-2 py-0.5 text-xs text-primary">默认</span>
            </p>
            <div class="mt-2 flex flex-wrap gap-2">
              <button class="btn btn-outline !px-3 !py-1.5" @click="editAddress(item)">编辑</button>
              <button class="btn btn-soft !px-3 !py-1.5" :disabled="item.isDefault" @click="setDefaultAddress(item)">
                设为默认
              </button>
              <button class="btn btn-danger !px-3 !py-1.5" @click="deleteAddress(item.id)">删除</button>
            </div>
          </article>
          <p v-if="!addresses.length" class="empty-block">暂无地址，新增一个用于下单。</p>
        </div>
      </section>

      <section class="surface-card mt-6 p-5">
        <h2 class="text-lg font-black">我的收藏</h2>
        <div class="mt-4 grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
          <article v-for="item in favorites" :key="item.id" class="flex items-center gap-3 rounded-2xl border border-[#d7e2d9] bg-white p-3">
            <img :src="item.productImage" class="h-14 w-14 rounded-xl object-cover" />
            <div class="flex-1">
              <RouterLink :to="`/product/${item.productId}`" class="text-sm font-bold text-slate-900 hover:text-primary">
                {{ item.productName }}
              </RouterLink>
              <p class="text-sm text-primary">¥{{ Number(item.price).toFixed(2) }}</p>
            </div>
            <button class="btn btn-danger !px-3 !py-1.5" @click="cancelFavorite(item.productId)">取消</button>
          </article>
          <p v-if="!favorites.length" class="empty-block sm:col-span-2 lg:col-span-3">暂无收藏商品</p>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { RouterLink } from 'vue-router';
import AppHeader from '@/components/AppHeader.vue';
import { addressApi, favoriteApi } from '@/api';
import { useAuthStore } from '@/stores/auth';
import type { Address, FavoriteItem } from '@/types/api';

const auth = useAuthStore();
const addresses = ref<Address[]>([]);
const favorites = ref<FavoriteItem[]>([]);
const editingAddressId = ref(0);
const addressSubmitting = ref(false);
const addressForm = reactive({
  receiverName: '',
  receiverPhone: '',
  detail: '',
  isDefault: false,
});

async function loadData() {
  if (!auth.user) await auth.fetchMe();
  [addresses.value, favorites.value] = await Promise.all([addressApi.list(), favoriteApi.list()]);
}

async function addAddress() {
  await addressApi.create({
    receiverName: addressForm.receiverName,
    receiverPhone: addressForm.receiverPhone,
    detail: addressForm.detail,
    isDefault: addressForm.isDefault,
  });
}

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
      await addAddress();
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
    if (editingAddressId.value === id) {
      resetAddressForm();
    }
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

onMounted(() => {
  loadData().catch((error) => alert((error as Error).message));
});
</script>
