<template>
  <Teleport to="body">
    <div class="pointer-events-none fixed inset-x-0 top-4 z-[120] flex justify-center px-4">
      <div class="flex w-full max-w-md flex-col gap-3">
        <transition-group name="toast">
          <article
            v-for="item in toast.items"
            :key="item.id"
            class="pointer-events-auto flex items-start gap-3 rounded-2xl border px-4 py-3 shadow-xl backdrop-blur-sm"
            :class="toastClass(item.type)"
          >
            <span class="material-symbols-outlined mt-0.5 text-lg">{{ toastIcon(item.type) }}</span>
            <p class="flex-1 text-sm font-semibold leading-6">{{ item.message }}</p>
            <button class="rounded-full p-1 transition-colors hover:bg-black/5" @click="toast.remove(item.id)">
              <span class="material-symbols-outlined text-base">close</span>
            </button>
          </article>
        </transition-group>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { useToastStore, type ToastType } from '@/stores/toast';

const toast = useToastStore();

function toastClass(type: ToastType) {
  return {
    success: 'border-emerald-200 bg-white/95 text-emerald-700',
    error: 'border-red-200 bg-white/95 text-red-600',
    info: 'border-sky-200 bg-white/95 text-sky-700',
  }[type];
}

function toastIcon(type: ToastType) {
  return {
    success: 'check_circle',
    error: 'error',
    info: 'info',
  }[type];
}
</script>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.24s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(-12px) scale(0.98);
}
</style>
