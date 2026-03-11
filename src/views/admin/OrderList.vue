<template>
  <div class="admin-page">
    <div class="page-header"><h3>订单管理</h3></div>
    <div class="filter-bar">
      <input v-model="orderNo" placeholder="搜索订单号…" class="input" @keyup.enter="load" />
      <select v-model="statusFilter" class="input select-sm" @change="load">
        <option value="">全部状态</option>
        <option value="UNPAID">待支付</option>
        <option value="PAID">已支付</option>
        <option value="SHIPPED">已发货</option>
        <option value="COMPLETED">已完成</option>
      </select>
      <button class="btn-primary" @click="load">搜索</button>
    </div>
    <div class="table-wrapper">
      <table class="data-table">
        <thead><tr><th>订单号</th><th>收货人</th><th>金额</th><th>状态</th><th>下单时间</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="o in list" :key="o.id">
            <td class="mono">{{ o.orderNo }}</td>
            <td>{{ o.receiverName }}</td>
            <td>¥{{ o.totalAmount }}</td>
            <td><span class="badge" :class="statusClass(o.status)">{{ statusLabel(o.status) }}</span></td>
            <td class="text-sm">{{ o.createdAt?.replace('T', ' ').slice(0, 16) }}</td>
            <td>
              <div class="action-btns">
                <button v-if="o.status === 'PAID'" class="btn-sm" @click="updateStatus(o.id, 'SHIPPED')">发货</button>
                <button v-if="o.status === 'SHIPPED'" class="btn-sm" @click="updateStatus(o.id, 'COMPLETED')">完成</button>
              </div>
            </td>
          </tr>
          <tr v-if="!list.length"><td colspan="6" class="empty">暂无数据</td></tr>
        </tbody>
      </table>
    </div>
    <div class="pagination" v-if="totalPages > 1">
      <button :disabled="page === 0" @click="page--; load()">上一页</button>
      <span>第 {{ page + 1 }} / {{ totalPages }} 页</span>
      <button :disabled="page + 1 >= totalPages" @click="page++; load()">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { adminApi } from '@/api/admin';

const list = ref<any[]>([]);
const orderNo = ref('');
const statusFilter = ref('');
const page = ref(0);
const totalPages = ref(0);

const statusMap: Record<string, string> = { UNPAID: '待支付', PAID: '已支付', SHIPPED: '已发货', COMPLETED: '已完成' };
const statusLabel = (s: string) => statusMap[s] || s;
const statusClass = (s: string) => ({ UNPAID: 'badge-yellow', PAID: 'badge-blue', SHIPPED: 'badge-purple', COMPLETED: 'badge-green' }[s] || 'badge-gray');

async function load() {
  const data = await adminApi.orderList({ page: page.value, size: 10, orderNo: orderNo.value || undefined, status: statusFilter.value || undefined });
  list.value = data.content;
  totalPages.value = data.totalPages;
}

async function updateStatus(id: number, status: string) {
  await adminApi.orderUpdateStatus(id, status);
  load();
}

onMounted(load);
</script>

<style scoped>
.admin-page { display: flex; flex-direction: column; gap: 16px; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h3 { font-size: 16px; font-weight: 600; color: #1a1a2e; }
.filter-bar { display: flex; gap: 8px; }
.input { padding: 8px 14px; border: 1px solid #d1d5db; border-radius: 8px; font-size: 14px; outline: none; }
.input:focus { border-color: #4ade80; }
.select-sm { width: 140px; }
.btn-primary { padding: 8px 18px; background: #4ade80; color: #fff; font-size: 14px; font-weight: 500; border: none; border-radius: 8px; cursor: pointer; }
.btn-primary:hover { background: #22c55e; }
.table-wrapper { background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.06); }
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th { background: #f9fafb; padding: 12px 16px; text-align: left; font-weight: 600; color: #374151; border-bottom: 1px solid #e5e7eb; }
.data-table td { padding: 12px 16px; border-bottom: 1px solid #f3f4f6; color: #4b5563; }
.data-table tr:hover { background: #f9fafb; }
.mono { font-family: 'Courier New', monospace; font-size: 13px; }
.text-sm { font-size: 13px; color: #9ca3af; }
.badge { padding: 2px 10px; border-radius: 99px; font-size: 12px; font-weight: 500; }
.badge-green { background: #dcfce7; color: #166534; }
.badge-blue { background: #dbeafe; color: #1e40af; }
.badge-yellow { background: #fef3c7; color: #92400e; }
.badge-purple { background: #ede9fe; color: #5b21b6; }
.badge-gray { background: #f3f4f6; color: #6b7280; }
.action-btns { display: flex; gap: 6px; }
.btn-sm { padding: 4px 12px; font-size: 12px; border-radius: 6px; border: 1px solid #d1d5db; background: #fff; color: #374151; cursor: pointer; }
.btn-sm:hover { background: #f3f4f6; }
.empty { text-align: center; color: #9ca3af; padding: 40px !important; }
.pagination { display: flex; align-items: center; justify-content: center; gap: 16px; }
.pagination button { padding: 6px 16px; border: 1px solid #d1d5db; border-radius: 6px; background: #fff; cursor: pointer; font-size: 13px; }
.pagination button:disabled { opacity: 0.4; cursor: not-allowed; }
.pagination span { font-size: 13px; color: #6b7280; }
</style>
