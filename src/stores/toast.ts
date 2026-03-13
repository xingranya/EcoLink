import { defineStore } from 'pinia';
import { ref } from 'vue';

export type ToastType = 'success' | 'error' | 'info';

export interface ToastItem {
  id: number;
  type: ToastType;
  message: string;
  duration: number;
}

let sequence = 1;

export const useToastStore = defineStore('toast', () => {
  const items = ref<ToastItem[]>([]);

  function push(type: ToastType, message: string, duration = 2600) {
    const id = sequence++;
    items.value.push({ id, type, message, duration });
    window.setTimeout(() => remove(id), duration);
  }

  function success(message: string, duration?: number) {
    push('success', message, duration);
  }

  function error(message: string, duration = 3200) {
    push('error', message, duration);
  }

  function info(message: string, duration?: number) {
    push('info', message, duration);
  }

  function remove(id: number) {
    items.value = items.value.filter((item) => item.id !== id);
  }

  return {
    items,
    push,
    success,
    error,
    info,
    remove,
  };
});
