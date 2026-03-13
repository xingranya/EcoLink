<template>
  <div class="dashboard-page">
    <section class="hero-panel">
      <div>
        <p class="hero-eyebrow">运营总览</p>
        <h3 class="hero-title">后台管理中枢</h3>
        <p class="hero-desc">集中查看订单状态、库存风险和热销商品，减少来回切页。</p>
      </div>
      <div class="hero-metrics">
        <div class="hero-metric">
          <span>累计营收</span>
          <strong>¥{{ formatMoney(stats.revenueAmount) }}</strong>
        </div>
        <div class="hero-metric">
          <span>低库存预警</span>
          <strong>{{ stats.lowStockProductCount ?? 0 }}</strong>
        </div>
      </div>
    </section>

    <section class="stats-grid">
      <article v-for="card in coreCards" :key="card.key" class="stat-card">
        <div class="stat-icon" :style="{ background: card.bg }">
          <span class="material-symbols-outlined">{{ card.icon }}</span>
        </div>
        <div>
          <p class="stat-label">{{ card.label }}</p>
          <p class="stat-value">{{ stats[card.key] ?? 0 }}</p>
          <p class="stat-hint">{{ card.hint }}</p>
        </div>
      </article>
    </section>

    <section class="status-section">
      <div class="status-panel">
        <div class="section-head">
          <h4>订单状态分布</h4>
          <RouterLink to="/admin/orders" class="text-link">处理订单</RouterLink>
        </div>
        <div class="status-list">
          <div v-for="item in orderStatusCards" :key="item.label" class="status-item">
            <div class="status-dot" :style="{ background: item.color }"></div>
            <div class="status-copy">
              <span>{{ item.label }}</span>
              <strong>{{ item.value }}</strong>
            </div>
          </div>
        </div>
      </div>

      <div class="status-panel">
        <div class="section-head">
          <h4>商品状态概览</h4>
          <RouterLink to="/admin/products" class="text-link">管理商品</RouterLink>
        </div>
        <div class="status-list compact">
          <div class="status-item">
            <div class="status-dot" style="background: #22c55e"></div>
            <div class="status-copy">
              <span>在售商品</span>
              <strong>{{ stats.onSaleProductCount ?? 0 }}</strong>
            </div>
          </div>
          <div class="status-item">
            <div class="status-dot" style="background: #94a3b8"></div>
            <div class="status-copy">
              <span>下架商品</span>
              <strong>{{ stats.offSaleProductCount ?? 0 }}</strong>
            </div>
          </div>
          <div class="status-item">
            <div class="status-dot" style="background: #f59e0b"></div>
            <div class="status-copy">
              <span>库存紧张</span>
              <strong>{{ stats.lowStockProductCount ?? 0 }}</strong>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="content-grid">
      <div class="panel">
        <div class="section-head">
          <h4>最近订单</h4>
          <RouterLink to="/admin/orders" class="text-link">查看全部</RouterLink>
        </div>
        <div v-if="stats.recentOrders?.length" class="order-list">
          <article v-for="order in stats.recentOrders" :key="order.id" class="order-card">
            <div>
              <p class="order-no">{{ order.orderNo }}</p>
              <p class="order-meta">{{ order.receiverName }} · {{ formatTime(order.createdAt) }}</p>
            </div>
            <div class="order-side">
              <span class="status-badge" :class="statusClass(order.status)">{{ statusLabel(order.status) }}</span>
              <strong>¥{{ formatMoney(order.totalAmount) }}</strong>
            </div>
          </article>
        </div>
        <p v-else class="empty-text">暂无订单数据</p>
      </div>

      <div class="panel">
        <div class="section-head">
          <h4>热销商品</h4>
          <RouterLink to="/admin/products" class="text-link">去运营</RouterLink>
        </div>
        <div v-if="stats.hotProducts?.length" class="product-list">
          <article v-for="item in stats.hotProducts" :key="item.id" class="product-card">
            <img :src="item.mainImage || fallbackImage" :alt="item.name" class="product-image" />
            <div class="product-copy">
              <p class="product-name">{{ item.name }}</p>
              <p class="product-meta">销量 {{ item.sales }} · 库存 {{ item.stock }}</p>
            </div>
            <span class="mini-badge" :class="item.status === 'ON_SALE' ? 'mini-badge-green' : 'mini-badge-gray'">
              {{ item.status === 'ON_SALE' ? '在售' : '下架' }}
            </span>
          </article>
        </div>
        <p v-else class="empty-text">暂无商品数据</p>
      </div>
    </section>

    <section class="quick-actions">
      <RouterLink to="/admin/products/new" class="quick-card">
        <span class="material-symbols-outlined">add_box</span>
        <div>
          <strong>发布新商品</strong>
          <p>快速创建上新内容</p>
        </div>
      </RouterLink>
      <RouterLink to="/admin/categories" class="quick-card">
        <span class="material-symbols-outlined">category</span>
        <div>
          <strong>整理分类</strong>
          <p>维护分类排序与启用状态</p>
        </div>
      </RouterLink>
      <RouterLink to="/admin/orders" class="quick-card">
        <span class="material-symbols-outlined">local_shipping</span>
        <div>
          <strong>处理履约</strong>
          <p>发货、完结订单一站完成</p>
        </div>
      </RouterLink>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { adminApi, type AdminDashboardStats } from '@/api/admin';
import { useToastStore } from '@/stores/toast';

const fallbackImage = 'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=1200&q=80';
const toast = useToastStore();
const stats = ref<AdminDashboardStats>({
  productCount: 0,
  orderCount: 0,
  userCount: 0,
  categoryCount: 0,
  onSaleProductCount: 0,
  offSaleProductCount: 0,
  lowStockProductCount: 0,
  unpaidOrderCount: 0,
  paidOrderCount: 0,
  shippedOrderCount: 0,
  completedOrderCount: 0,
  revenueAmount: 0,
  recentOrders: [],
  hotProducts: [],
});

const coreCards = [
  { key: 'productCount', label: '商品总数', icon: 'inventory_2', bg: 'linear-gradient(135deg, #14532d, #22c55e)', hint: '可运营商品池' },
  { key: 'orderCount', label: '订单总数', icon: 'receipt_long', bg: 'linear-gradient(135deg, #0f766e, #14b8a6)', hint: '全部履约记录' },
  { key: 'userCount', label: '用户总数', icon: 'group', bg: 'linear-gradient(135deg, #1d4ed8, #60a5fa)', hint: '注册用户规模' },
  { key: 'categoryCount', label: '分类总数', icon: 'category', bg: 'linear-gradient(135deg, #9a3412, #fb923c)', hint: '当前分类结构' },
] as const;

const orderStatusCards = computed(() => [
  { label: '待支付', value: stats.value.unpaidOrderCount || 0, color: '#f59e0b' },
  { label: '已支付', value: stats.value.paidOrderCount || 0, color: '#3b82f6' },
  { label: '已发货', value: stats.value.shippedOrderCount || 0, color: '#8b5cf6' },
  { label: '已完成', value: stats.value.completedOrderCount || 0, color: '#22c55e' },
]);

function formatMoney(value: number | string | undefined) {
  return Number(value || 0).toFixed(2);
}

function formatTime(value: string | undefined) {
  if (!value) return '-';
  return value.replace('T', ' ').slice(0, 16);
}

function statusLabel(status: string) {
  return {
    UNPAID: '待支付',
    PAID: '已支付',
    SHIPPED: '已发货',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
  }[status] || status;
}

function statusClass(status: string) {
  return {
    UNPAID: 'status-yellow',
    PAID: 'status-blue',
    SHIPPED: 'status-purple',
    COMPLETED: 'status-green',
    CANCELLED: 'status-gray',
  }[status] || 'status-gray';
}

onMounted(async () => {
  try {
    stats.value = await adminApi.dashboard();
  } catch (error) {
    toast.error((error as Error).message);
  }
});
</script>

<style scoped>
.dashboard-page { display: flex; flex-direction: column; gap: 20px; }

.hero-panel {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  padding: 26px 28px;
  border-radius: 24px;
  background:
    radial-gradient(circle at top left, rgba(255, 255, 255, 0.2), transparent 34%),
    linear-gradient(135deg, #123524 0%, #1f6f43 52%, #7ddf9d 100%);
  color: #fff;
  box-shadow: 0 20px 40px rgba(18, 53, 36, 0.16);
}

.hero-eyebrow { margin: 0 0 8px; font-size: 12px; letter-spacing: 0.18em; text-transform: uppercase; color: rgba(255, 255, 255, 0.7); }
.hero-title { margin: 0; font-size: 28px; font-weight: 800; }
.hero-desc { margin: 10px 0 0; max-width: 560px; color: rgba(255, 255, 255, 0.82); line-height: 1.7; }

.hero-metrics { display: flex; gap: 12px; flex-wrap: wrap; }
.hero-metric {
  min-width: 152px;
  padding: 14px 16px;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}
.hero-metric span { display: block; font-size: 12px; color: rgba(255, 255, 255, 0.7); }
.hero-metric strong { display: block; margin-top: 8px; font-size: 22px; font-weight: 800; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.stat-card {
  display: flex;
  gap: 14px;
  align-items: center;
  padding: 20px;
  border: 1px solid #e7efe8;
  border-radius: 20px;
  background: #fff;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.04);
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 18px;
  color: #fff;
}
.stat-icon .material-symbols-outlined { font-size: 28px; }
.stat-label { margin: 0; font-size: 13px; color: #64748b; }
.stat-value { margin: 4px 0; font-size: 28px; font-weight: 800; color: #0f172a; }
.stat-hint { margin: 0; font-size: 12px; color: #94a3b8; }

.status-section,
.content-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.status-panel,
.panel {
  padding: 20px;
  border: 1px solid #e7efe8;
  border-radius: 22px;
  background: #fff;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.04);
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}
.section-head h4 { margin: 0; font-size: 16px; font-weight: 700; color: #0f172a; }
.text-link { color: #15803d; text-decoration: none; font-size: 13px; font-weight: 700; }

.status-list { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; }
.status-list.compact { grid-template-columns: 1fr; }
.status-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  border-radius: 16px;
  background: #f8fafc;
}
.status-dot { width: 10px; height: 10px; border-radius: 999px; flex-shrink: 0; }
.status-copy span { display: block; font-size: 12px; color: #64748b; }
.status-copy strong { display: block; margin-top: 3px; font-size: 20px; color: #0f172a; }

.order-list,
.product-list { display: flex; flex-direction: column; gap: 12px; }
.order-card,
.product-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px;
  border-radius: 16px;
  background: #f8fafc;
}

.order-no { margin: 0; font-size: 13px; font-weight: 700; color: #0f172a; }
.order-meta { margin: 4px 0 0; font-size: 12px; color: #64748b; }
.order-side { display: flex; flex-direction: column; align-items: flex-end; gap: 8px; font-weight: 700; color: #0f172a; }

.status-badge,
.mini-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}
.status-yellow { background: #fef3c7; color: #92400e; }
.status-blue { background: #dbeafe; color: #1d4ed8; }
.status-purple { background: #ede9fe; color: #6d28d9; }
.status-green { background: #dcfce7; color: #166534; }
.status-gray { background: #e2e8f0; color: #475569; }
.mini-badge-green { background: #dcfce7; color: #166534; }
.mini-badge-gray { background: #e2e8f0; color: #475569; }

.product-card { justify-content: flex-start; }
.product-image {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  object-fit: cover;
  flex-shrink: 0;
}
.product-copy { min-width: 0; flex: 1; }
.product-name { margin: 0; font-size: 14px; font-weight: 700; color: #0f172a; }
.product-meta { margin: 4px 0 0; font-size: 12px; color: #64748b; }

.quick-actions {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.quick-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  border-radius: 22px;
  text-decoration: none;
  color: #0f172a;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  border: 1px solid #e7efe8;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.04);
}
.quick-card .material-symbols-outlined {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  border-radius: 16px;
  background: #dcfce7;
  color: #15803d;
}
.quick-card strong { display: block; font-size: 15px; }
.quick-card p { margin: 4px 0 0; font-size: 12px; color: #64748b; }

.empty-text { margin: 0; padding: 28px 0; text-align: center; color: #94a3b8; font-size: 13px; }

@media (max-width: 1100px) {
  .status-section,
  .content-grid,
  .quick-actions { grid-template-columns: 1fr; }
}

@media (max-width: 720px) {
  .hero-panel { flex-direction: column; align-items: stretch; }
  .stats-grid,
  .status-list { grid-template-columns: 1fr; }
}
</style>
