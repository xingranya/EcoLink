<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card" v-for="s in statCards" :key="s.key">
        <div class="stat-icon" :style="{ background: s.bg }">
          <span class="material-symbols-outlined">{{ s.icon }}</span>
        </div>
        <div class="stat-info">
          <p class="stat-label">{{ s.label }}</p>
          <p class="stat-value">{{ stats[s.key] ?? '-' }}</p>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <h3 class="section-title">快捷操作</h3>
      <div class="actions-grid">
        <RouterLink to="/admin/products" class="action-btn">
          <span class="material-symbols-outlined">inventory_2</span>
          <span>管理商品</span>
        </RouterLink>
        <RouterLink to="/admin/categories" class="action-btn">
          <span class="material-symbols-outlined">category</span>
          <span>管理分类</span>
        </RouterLink>
        <RouterLink to="/admin/orders" class="action-btn">
          <span class="material-symbols-outlined">receipt_long</span>
          <span>查看订单</span>
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { RouterLink } from 'vue-router';
import { adminApi } from '@/api/admin';

const stats = ref<Record<string, number>>({});

const statCards = [
  { key: 'productCount', label: '商品总数', icon: 'inventory_2', bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { key: 'orderCount', label: '订单总数', icon: 'receipt_long', bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { key: 'userCount', label: '用户总数', icon: 'group', bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { key: 'categoryCount', label: '分类总数', icon: 'category', bg: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' },
];

onMounted(async () => {
  try {
    stats.value = await adminApi.dashboard();
  } catch { /* 忽略错误 */ }
});
</script>

<style scoped>
.dashboard { display: flex; flex-direction: column; gap: 28px; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 52px; height: 52px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  color: #fff;
}
.stat-icon .material-symbols-outlined { font-size: 26px; }

.stat-label { font-size: 13px; color: #6b7280; margin-bottom: 4px; }
.stat-value { font-size: 28px; font-weight: 700; color: #1a1a2e; }

.section-title { font-size: 16px; font-weight: 600; color: #374151; margin-bottom: 16px; }

.actions-grid { display: flex; gap: 12px; flex-wrap: wrap; }

.action-btn {
  display: flex; align-items: center; gap: 8px;
  padding: 12px 24px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  color: #374151;
  text-decoration: none;
  font-size: 14px; font-weight: 500;
  transition: all 0.2s;
}
.action-btn:hover {
  background: #4ade80; color: #fff;
  box-shadow: 0 4px 12px rgba(74, 222, 128, 0.3);
}
</style>
