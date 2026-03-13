<template>
  <div class="admin-page">
    <section class="page-hero">
      <div>
        <p class="page-eyebrow">订单履约</p>
        <h3>订单管理工作台</h3>
        <p>支持订单检索、状态流转与详情查看，减少售后和发货处理成本。</p>
      </div>
    </section>

    <section class="stats-strip">
      <article class="mini-stat">
        <span>当前结果</span>
        <strong>{{ totalElements }}</strong>
      </article>
      <article class="mini-stat">
        <span>待支付</span>
        <strong>{{ statusCount('UNPAID') }}</strong>
      </article>
      <article class="mini-stat">
        <span>待发货</span>
        <strong>{{ statusCount('PAID') }}</strong>
      </article>
      <article class="mini-stat">
        <span>已发货</span>
        <strong>{{ statusCount('SHIPPED') }}</strong>
      </article>
    </section>

    <section class="filter-panel">
      <div class="filter-grid">
        <input v-model.trim="orderNo" placeholder="搜索订单号" class="input" @keyup.enter="search" />
        <select v-model="statusFilter" class="input" @change="search">
          <option value="">全部状态</option>
          <option value="UNPAID">待支付</option>
          <option value="PAID">已支付</option>
          <option value="SHIPPED">已发货</option>
          <option value="COMPLETED">已完成</option>
          <option value="CANCELLED">已取消</option>
        </select>
      </div>
      <div class="filter-actions">
        <button class="btn-primary" @click="search">筛选</button>
        <button class="btn-secondary" @click="resetFilters">重置</button>
      </div>
    </section>

    <div class="table-wrapper">
      <table class="data-table">
        <thead>
          <tr>
            <th>订单号</th>
            <th>收货信息</th>
            <th>金额</th>
            <th>状态</th>
            <th>下单时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.id">
            <td class="mono">{{ item.orderNo }}</td>
            <td>
              <div class="receiver-cell">
                <strong>{{ item.receiverName }}</strong>
                <span>{{ item.receiverPhone }}</span>
              </div>
            </td>
            <td class="price-cell">¥{{ formatMoney(item.totalAmount) }}</td>
            <td><span class="badge" :class="statusClass(item.status)">{{ statusLabel(item.status) }}</span></td>
            <td class="time-cell">{{ formatTime(item.createdAt) }}</td>
            <td>
              <div class="action-btns">
                <button class="btn-sm" @click="openDetail(item.id)">详情</button>
                <button
                  v-for="action in getActions(item.status)"
                  :key="action.status"
                  class="btn-sm"
                  :disabled="updatingId === item.id"
                  @click="updateStatus(item.id, action.status)"
                >
                  {{ updatingId === item.id ? '处理中...' : action.label }}
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="!list.length">
            <td colspan="6" class="empty">暂无符合条件的订单</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination" v-if="totalPages > 1">
      <button :disabled="page === 0" @click="changePage(page - 1)">上一页</button>
      <span>第 {{ page + 1 }} / {{ totalPages }} 页</span>
      <button :disabled="page + 1 >= totalPages" @click="changePage(page + 1)">下一页</button>
    </div>

    <div v-if="detailVisible" class="drawer-mask" @click.self="closeDetail">
      <aside class="drawer">
        <div class="drawer-head">
          <div>
            <p class="page-eyebrow">订单详情</p>
            <h4>{{ selectedOrder?.order?.orderNo || '加载中' }}</h4>
          </div>
          <button class="icon-btn" @click="closeDetail">
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>

        <div v-if="detailLoading" class="drawer-loading">正在加载订单详情...</div>

        <template v-else-if="selectedOrder">
          <section class="drawer-section">
            <div class="summary-grid">
              <article class="summary-item">
                <span>订单状态</span>
                <strong>{{ statusLabel(selectedOrder.order.status) }}</strong>
              </article>
              <article class="summary-item">
                <span>订单金额</span>
                <strong>¥{{ formatMoney(selectedOrder.order.totalAmount) }}</strong>
              </article>
              <article class="summary-item">
                <span>下单时间</span>
                <strong>{{ formatTime(selectedOrder.order.createdAt) }}</strong>
              </article>
              <article class="summary-item">
                <span>完成时间</span>
                <strong>{{ formatTime(selectedOrder.order.completedAt || selectedOrder.order.shippedAt || selectedOrder.order.paidAt) }}</strong>
              </article>
            </div>
          </section>

          <section class="drawer-section">
            <h5>收货信息</h5>
            <div class="detail-card">
              <p>{{ selectedOrder.order.receiverName }} · {{ selectedOrder.order.receiverPhone }}</p>
              <p>{{ selectedOrder.order.receiverAddress || '-' }}</p>
            </div>
          </section>

          <section class="drawer-section">
            <div class="drawer-section-head">
              <h5>商品明细</h5>
              <span>{{ selectedOrder.items.length }} 件</span>
            </div>
            <div class="item-list">
              <article v-for="row in selectedOrder.items" :key="row.id" class="item-card">
                <img :src="row.productImage || fallbackImage" :alt="row.productName" class="item-image" />
                <div class="item-copy">
                  <strong>{{ row.productName }}</strong>
                  <span>单价 ¥{{ formatMoney(row.salePrice) }} · 数量 x{{ row.quantity }}</span>
                </div>
                <strong class="item-total">¥{{ formatMoney(row.subtotal) }}</strong>
              </article>
            </div>
          </section>

          <section class="drawer-section">
            <h5>快捷处理</h5>
            <div class="drawer-actions">
              <button
                v-for="action in getActions(selectedOrder.order.status)"
                :key="action.status"
                class="btn-primary"
                :disabled="updatingId === selectedOrder.order.id"
                @click="updateStatus(selectedOrder.order.id, action.status, true)"
              >
                {{ updatingId === selectedOrder.order.id ? '处理中...' : action.label }}
              </button>
              <button v-if="!getActions(selectedOrder.order.status).length" class="btn-secondary" disabled>当前状态无需处理</button>
            </div>
          </section>
        </template>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { adminApi } from '@/api/admin';
import { useToastStore } from '@/stores/toast';

const fallbackImage = 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1200&q=80';
const toast = useToastStore();
const list = ref<any[]>([]);
const orderNo = ref('');
const statusFilter = ref('');
const page = ref(0);
const totalPages = ref(0);
const totalElements = ref(0);
const updatingId = ref(0);
const detailVisible = ref(false);
const detailLoading = ref(false);
const selectedOrder = ref<{ order: any; items: any[] } | null>(null);

function formatMoney(value: number | string) {
  return Number(value || 0).toFixed(2);
}

function formatTime(value?: string) {
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
    UNPAID: 'badge-yellow',
    PAID: 'badge-blue',
    SHIPPED: 'badge-purple',
    COMPLETED: 'badge-green',
    CANCELLED: 'badge-gray',
  }[status] || 'badge-gray';
}

function getActions(status: string) {
  if (status === 'UNPAID') return [{ status: 'PAID', label: '确认支付' }];
  if (status === 'PAID') return [{ status: 'SHIPPED', label: '发货' }];
  if (status === 'SHIPPED') return [{ status: 'COMPLETED', label: '完成' }];
  return [];
}

function statusCount(status: string) {
  return list.value.filter((item) => item.status === status).length;
}

async function load() {
  try {
    const data = await adminApi.orderList({
      page: page.value,
      size: 10,
      orderNo: orderNo.value || undefined,
      status: statusFilter.value || undefined,
    });
    list.value = data.content;
    totalPages.value = data.totalPages;
    totalElements.value = data.totalElements;
  } catch (error) {
    toast.error((error as Error).message);
  }
}

function search() {
  page.value = 0;
  load();
}

function resetFilters() {
  orderNo.value = '';
  statusFilter.value = '';
  search();
}

function changePage(nextPage: number) {
  page.value = nextPage;
  load();
}

async function openDetail(id: number) {
  detailVisible.value = true;
  detailLoading.value = true;
  try {
    selectedOrder.value = await adminApi.orderDetail(id);
  } catch (error) {
    detailVisible.value = false;
    selectedOrder.value = null;
    toast.error((error as Error).message);
  } finally {
    detailLoading.value = false;
  }
}

function closeDetail() {
  detailVisible.value = false;
  selectedOrder.value = null;
}

async function updateStatus(id: number, status: string, refreshDetail = false) {
  updatingId.value = id;
  try {
    await adminApi.orderUpdateStatus(id, status);
    toast.success(`订单已更新为${statusLabel(status)}`);
    await load();
    if (refreshDetail || (selectedOrder.value && selectedOrder.value.order.id === id)) {
      selectedOrder.value = await adminApi.orderDetail(id);
    }
  } catch (error) {
    toast.error((error as Error).message);
  } finally {
    updatingId.value = 0;
  }
}

onMounted(load);
</script>

<style scoped>
.admin-page { display: flex; flex-direction: column; gap: 16px; }

.page-hero {
  padding: 22px 24px;
  border-radius: 22px;
  background: linear-gradient(135deg, #ecfeff, #cffafe);
  border: 1px solid #a5f3fc;
}
.page-eyebrow { margin: 0 0 8px; font-size: 12px; letter-spacing: 0.16em; color: #0f766e; text-transform: uppercase; }
.page-hero h3 { margin: 0; font-size: 24px; font-weight: 800; color: #134e4a; }
.page-hero p:last-child { margin: 8px 0 0; color: #155e75; }

.stats-strip {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}
.mini-stat {
  padding: 16px 18px;
  border-radius: 18px;
  background: #fff;
  border: 1px solid #e2e8f0;
}
.mini-stat span { display: block; font-size: 12px; color: #64748b; }
.mini-stat strong { display: block; margin-top: 8px; font-size: 24px; color: #0f172a; }

.filter-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 18px;
  border-radius: 20px;
  background: #fff;
  border: 1px solid #e2e8f0;
}
.filter-grid {
  flex: 1;
  display: grid;
  grid-template-columns: minmax(240px, 1.3fr) minmax(160px, 0.7fr);
  gap: 10px;
}
.filter-actions { display: flex; gap: 10px; }

.input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #cbd5e1;
  border-radius: 12px;
  font-size: 14px;
  outline: none;
}
.input:focus { border-color: #06b6d4; box-shadow: 0 0 0 4px rgba(6, 182, 212, 0.12); }

.btn-primary,
.btn-secondary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 18px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 700;
  border: none;
  cursor: pointer;
}
.btn-primary { background: #0891b2; color: #fff; }
.btn-primary:hover { background: #0e7490; }
.btn-secondary { background: #f8fafc; color: #334155; border: 1px solid #cbd5e1; }

.table-wrapper {
  overflow: hidden;
  border-radius: 22px;
  background: #fff;
  border: 1px solid #e2e8f0;
}
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th {
  padding: 14px 16px;
  text-align: left;
  font-size: 13px;
  color: #475569;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}
.data-table td {
  padding: 16px;
  color: #334155;
  border-bottom: 1px solid #f1f5f9;
  vertical-align: middle;
}
.mono { font-family: Consolas, monospace; font-size: 13px; color: #0f172a; }
.receiver-cell { display: flex; flex-direction: column; gap: 4px; }
.receiver-cell strong { color: #0f172a; }
.receiver-cell span,
.time-cell { color: #64748b; font-size: 12px; }
.price-cell { font-weight: 800; color: #0f766e; }

.badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}
.badge-green { background: #dcfce7; color: #166534; }
.badge-blue { background: #dbeafe; color: #1d4ed8; }
.badge-yellow { background: #fef3c7; color: #92400e; }
.badge-purple { background: #ede9fe; color: #6d28d9; }
.badge-gray { background: #e2e8f0; color: #475569; }

.action-btns { display: flex; gap: 8px; flex-wrap: wrap; }
.btn-sm {
  padding: 7px 12px;
  border: 1px solid #cbd5e1;
  border-radius: 10px;
  background: #fff;
  color: #334155;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
}
.btn-sm:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-sm:hover:not(:disabled) { background: #f8fafc; }

.empty { padding: 56px 16px !important; text-align: center; color: #94a3b8; }

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
}
.pagination button {
  padding: 8px 16px;
  border: 1px solid #cbd5e1;
  border-radius: 10px;
  background: #fff;
  cursor: pointer;
}
.pagination button:disabled { opacity: 0.5; cursor: not-allowed; }
.pagination span { color: #64748b; font-size: 13px; }

.drawer-mask {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.4);
  display: flex;
  justify-content: flex-end;
  z-index: 200;
}
.drawer {
  width: min(520px, 100vw);
  height: 100vh;
  overflow-y: auto;
  background: #fff;
  padding: 22px;
  box-shadow: -12px 0 40px rgba(15, 23, 42, 0.15);
}
.drawer-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}
.drawer-head h4 { margin: 0; font-size: 22px; color: #0f172a; }
.icon-btn {
  border: 1px solid #cbd5e1;
  background: #fff;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  cursor: pointer;
}

.drawer-loading {
  padding: 48px 0;
  text-align: center;
  color: #64748b;
}
.drawer-section { margin-top: 22px; }
.drawer-section h5 { margin: 0 0 12px; font-size: 16px; color: #0f172a; }
.drawer-section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}
.drawer-section-head span { font-size: 12px; color: #64748b; }

.summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}
.summary-item,
.detail-card {
  padding: 14px;
  border-radius: 16px;
  background: #f8fafc;
}
.summary-item span { display: block; font-size: 12px; color: #64748b; }
.summary-item strong { display: block; margin-top: 8px; color: #0f172a; }
.detail-card p { margin: 0; line-height: 1.8; color: #334155; }

.item-list { display: flex; flex-direction: column; gap: 12px; }
.item-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  border-radius: 16px;
  background: #f8fafc;
}
.item-image {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  object-fit: cover;
  background: #e2e8f0;
}
.item-copy { flex: 1; min-width: 0; }
.item-copy strong { display: block; color: #0f172a; }
.item-copy span { display: block; margin-top: 6px; color: #64748b; font-size: 12px; }
.item-total { color: #0f766e; }

.drawer-actions { display: flex; gap: 10px; flex-wrap: wrap; }

@media (max-width: 1080px) {
  .stats-strip { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .filter-panel { flex-direction: column; align-items: stretch; }
  .filter-grid { grid-template-columns: 1fr; }
}

@media (max-width: 720px) {
  .stats-strip,
  .summary-grid { grid-template-columns: 1fr; }
}
</style>
