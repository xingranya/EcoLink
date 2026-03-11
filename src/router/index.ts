import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // === C端路由 ===
    { path: '/', name: 'home', component: () => import('@/views/HomeView.vue') },
    { path: '/search', name: 'search', component: () => import('@/views/SearchView.vue') },
    { path: '/product/:id', name: 'product', component: () => import('@/views/ProductDetailView.vue') },
    { path: '/cart', name: 'cart', component: () => import('@/views/CartView.vue'), meta: { requiresAuth: true } },
    { path: '/orders', name: 'orders', component: () => import('@/views/OrdersView.vue'), meta: { requiresAuth: true } },
    { path: '/payment/:id', name: 'payment', component: () => import('@/views/PaymentView.vue'), meta: { requiresAuth: true } },
    { path: '/profile', name: 'profile', component: () => import('@/views/ProfileView.vue'), meta: { requiresAuth: true } },
    { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue') },
    { path: '/register', name: 'register', component: () => import('@/views/RegisterView.vue') },

    // === 后台管理路由 ===
    {
      path: '/admin',
      component: () => import('@/layouts/AdminLayout.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        { path: '', redirect: '/admin/dashboard' },
        { path: 'dashboard', name: 'admin-dashboard', component: () => import('@/views/admin/Dashboard.vue') },
        { path: 'products', name: 'admin-products', component: () => import('@/views/admin/ProductList.vue') },
        { path: 'products/new', name: 'admin-product-new', component: () => import('@/views/admin/ProductForm.vue') },
        { path: 'products/:id/edit', name: 'admin-product-edit', component: () => import('@/views/admin/ProductForm.vue') },
        { path: 'categories', name: 'admin-categories', component: () => import('@/views/admin/CategoryList.vue') },
        { path: 'orders', name: 'admin-orders', component: () => import('@/views/admin/OrderList.vue') },
      ],
    },
  ],
});

router.beforeEach(async (to) => {
  const auth = useAuthStore();
  // 已登录但未获取用户信息时自动刷新
  if (auth.isLogin && !auth.user) {
    try {
      await auth.fetchMe();
    } catch {
      auth.clearSession();
    }
  }
  // 需要登录
  if (to.meta.requiresAuth && !auth.isLogin) {
    return { name: 'login', query: { redirect: to.fullPath } };
  }
  // 需要管理员权限
  if (to.meta.requiresAdmin && !auth.isAdmin) {
    return { name: 'home' };
  }
  // 已登录用户不允许访问登录/注册
  if ((to.name === 'login' || to.name === 'register') && auth.isLogin) {
    return { name: 'home' };
  }
  return true;
});

export default router;
