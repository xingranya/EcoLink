<template>
  <div class="stagger-enter overflow-hidden rounded-3xl border border-[#d4e2d7] bg-white shadow-[0_18px_35px_rgba(33,95,60,0.12)]">
    <RouterLink :to="`/product/${product.id}`" class="group relative block aspect-[5/4] overflow-hidden bg-slate-100">
      <img :src="product.mainImage || fallbackImage" :alt="product.name" class="h-full w-full object-cover transition duration-300 group-hover:scale-105" />
      <span class="absolute left-3 top-3 rounded-full bg-white/90 px-2 py-0.5 text-xs font-bold text-primary">
        销量 {{ product.sales }}
      </span>
    </RouterLink>
    <div class="p-4">
      <RouterLink :to="`/product/${product.id}`" class="line-clamp-1 text-base font-black text-slate-900 hover:text-primary">
        {{ product.name }}
      </RouterLink>
      <p class="mt-1 line-clamp-1 text-xs text-slate-500">{{ product.subtitle || '生态优选' }}</p>
      <div class="mt-3 flex items-center justify-between gap-2">
        <span class="text-2xl font-black text-primary">¥{{ Number(product.price).toFixed(2) }}</span>
        <button class="btn btn-soft !rounded-full !px-3 !py-2" :disabled="adding" @click="addCart">
          <span class="material-symbols-outlined text-base">add_shopping_cart</span>
          <span class="text-xs">{{ adding ? '加入中' : '加入' }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { RouterLink } from 'vue-router';
import { cartApi } from '@/api';
import type { ProductItem } from '@/types/api';

const props = defineProps<{ product: ProductItem }>();
const emit = defineEmits<{ added: [] }>();
const adding = ref(false);

const fallbackImage = 'https://images.unsplash.com/photo-1608797178974-15b35a64ede9?auto=format&fit=crop&w=1200&q=80';

async function addCart() {
  if (adding.value) return;
  adding.value = true;
  try {
    await cartApi.add({ productId: props.product.id, quantity: 1 });
    emit('added');
  } catch (error) {
    alert((error as Error).message);
  } finally {
    adding.value = false;
  }
}
</script>
