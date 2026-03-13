<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <span class="material-symbols-outlined logo-icon">eco</span>
        <h1 class="logo-text">EcoLink 后台</h1>
      </div>
      <nav class="sidebar-nav">
        <RouterLink to="/admin/dashboard" class="nav-item" active-class="active">
          <span class="material-symbols-outlined">dashboard</span>
          <span>仪表盘</span>
        </RouterLink>
        <RouterLink to="/admin/products" class="nav-item" active-class="active">
          <span class="material-symbols-outlined">inventory_2</span>
          <span>商品管理</span>
        </RouterLink>
        <RouterLink to="/admin/categories" class="nav-item" active-class="active">
          <span class="material-symbols-outlined">category</span>
          <span>分类管理</span>
        </RouterLink>
        <RouterLink to="/admin/orders" class="nav-item" active-class="active">
          <span class="material-symbols-outlined">receipt_long</span>
          <span>订单管理</span>
        </RouterLink>
      </nav>
      <div class="sidebar-footer">
        <RouterLink to="/" class="nav-item" active-class="">
          <span class="material-symbols-outlined">storefront</span>
          <span>返回商城</span>
        </RouterLink>
        <button class="nav-item logout-btn" @click="handleLogout">
          <span class="material-symbols-outlined">logout</span>
          <span>退出登录</span>
        </button>
      </div>
    </aside>
    <!-- 主内容 -->
    <main class="main-content">
      <header class="topbar">
        <div>
          <h2 class="page-title">{{ pageTitle }}</h2>
          <p class="page-subtitle">EcoLink 管理后台</p>
        </div>
        <div class="user-info">
          <span class="admin-badge">ADMIN</span>
          <span class="material-symbols-outlined">admin_panel_settings</span>
          <span>{{ auth.user?.nickname || '管理员' }}</span>
        </div>
      </header>
      <div class="content-body">
        <RouterView />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { RouterView, RouterLink, useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();

const pageTitle = computed(() => {
  const map: Record<string, string> = {
    'admin-dashboard': '仪表盘',
    'admin-products': '商品管理',
    'admin-product-new': '新增商品',
    'admin-product-edit': '编辑商品',
    'admin-categories': '分类管理',
    'admin-orders': '订单管理',
  };
  return map[route.name as string] || '后台管理';
});

function handleLogout() {
  auth.clearSession();
  router.push('/login');
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}

.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  color: #e0e0e0;
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo-icon {
  font-size: 28px;
  color: #4ade80;
}

.logo-text {
  font-size: 17px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.5px;
}

.sidebar-nav {
  flex: 1;
  padding: 12px 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  color: #b0b8c8;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  border-radius: 0;
  transition: all 0.2s;
  border: none;
  background: none;
  cursor: pointer;
  width: 100%;
  text-align: left;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
}

.nav-item.active {
  background: rgba(74, 222, 128, 0.12);
  color: #4ade80;
  border-right: 3px solid #4ade80;
}

.nav-item .material-symbols-outlined {
  font-size: 20px;
}

.sidebar-footer {
  padding: 12px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.logout-btn:hover {
  color: #f87171 !important;
}

.main-content {
  flex: 1;
  margin-left: 240px;
  display: flex;
  flex-direction: column;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 32px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
}

.page-subtitle {
  margin: 4px 0 0;
  font-size: 12px;
  color: #94a3b8;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6b7280;
  font-size: 14px;
}

.admin-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 3px 8px;
  border-radius: 999px;
  background: #dcfce7;
  color: #166534;
  font-size: 11px;
  font-weight: 700;
}

.content-body {
  flex: 1;
  padding: 24px 32px;
}
</style>
