<template>
  <div class="flex min-h-screen flex-col">
    <AppHeader v-if="showChrome" />
    <RouterView />
    <AppFooter v-if="showChrome" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { RouterView, useRoute } from 'vue-router';
import AppHeader from '@/components/AppHeader.vue';
import AppFooter from '@/components/AppFooter.vue';

const route = useRoute();
const showChrome = computed(() =>
  !['login', 'register'].includes(route.name as string) &&
  !route.path.startsWith('/admin')
);
</script>
