import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { cartApi } from '@/api';
import type { CartItem } from '@/types/api';

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([]);
  const totalAmount = ref(0);
  const totalCount = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0));

  async function reload() {
    const data = await cartApi.list();
    items.value = data.items;
    totalAmount.value = data.totalAmount;
  }

  function clear() {
    items.value = [];
    totalAmount.value = 0;
  }

  return { items, totalAmount, totalCount, reload, clear };
});
