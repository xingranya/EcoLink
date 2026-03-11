<template>
  <div class="admin-page">
    <div class="page-header">
      <h3>商品列表</h3>
      <RouterLink to="/admin/products/new" class="btn-primary">
        <span class="material-symbols-outlined">add</span> 新增商品
      </RouterLink>
    </div>
    <!-- 搜索栏 -->
    <div class="filter-bar">
      <input v-model="keyword" placeholder="搜索商品名称…" class="input" @keyup.enter="load" />
      <button class="btn-primary" @click="load">搜索</button>
    </div>
    <!-- 表格 -->
    <div class="table-wrapper">
      <table class="data-table">
        <thead>
          <tr><th>ID</th><th>商品名称</th><th>价格</th><th>库存</th><th>销量</th><th>状态</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-for="p in list" :key="p.id">
            <td>{{ p.id }}</td>
            <td>
              <div class="product-cell">
                <img v-if="p.mainImage" :src="p.mainImage" class="thumb" />
                <span>{{ p.name }}</span>
              </div>
            </td>
            <td>¥{{ p.price }}</td>
            <td>{{ p.stock }}</td>
            <td>{{ p.sales }}</td>
            <td><span class="badge" :class="p.status === 'ON_SALE' ? 'badge-green' : 'badge-gray'">{{ p.status === 'ON_SALE' ? '在售' : '下架' }}</span></td>
            <td>
              <div class="action-btns">
                <RouterLink :to="`/admin/products/${p.id}/edit`" class="btn-sm">编辑</RouterLink>
                <button class="btn-sm btn-danger" @click="handleDelete(p.id)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="!list.length"><td colspan="7" class="empty">暂无数据</td></tr>
        </tbody>
      </table>
    </div>
    <!-- 分页 -->
    <div class="pagination" v-if="totalPages > 1">
      <button :disabled="page === 0" @click="page--; load()">上一页</button>
      <span>第 {{ page + 1 }} / {{ totalPages }} 页</span>
      <button :disabled="page + 1 >= totalPages" @click="page++; load()">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { RouterLink } from 'vue-router';
import { adminApi } from '@/api/admin';

const list = ref<any[]>([]);
const keyword = ref('');
const page = ref(0);
const totalPages = ref(0);

async function load() {
  try {
    const data = await adminApi.productList({ page: page.value, size: 10, keyword: keyword.value || undefined });
    list.value = data.content;
    totalPages.value = data.totalPages;
  } catch { /* 忽略 */ }
}

async function handleDelete(id: number) {
  if (!confirm('确认删除该商品？')) return;
  await adminApi.productDelete(id);
  load();
}

onMounted(load);
</script>

<style scoped>
.admin-page { display: flex; flex-direction: column; gap: 16px; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h3 { font-size: 16px; font-weight: 600; color: #1a1a2e; }
.filter-bar { display: flex; gap: 8px; }
.input {
  padding: 8px 14px; border: 1px solid #d1d5db; border-radius: 8px;
  font-size: 14px; outline: none; width: 280px;
}
.input:focus { border-color: #4ade80; box-shadow: 0 0 0 3px rgba(74, 222, 128, 0.15); }
.btn-primary {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 8px 18px; background: #4ade80; color: #fff;
  font-size: 14px; font-weight: 500; border: none; border-radius: 8px;
  cursor: pointer; text-decoration: none; transition: background 0.2s;
}
.btn-primary:hover { background: #22c55e; }
.table-wrapper { background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.06); }
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th { background: #f9fafb; padding: 12px 16px; text-align: left; font-weight: 600; color: #374151; border-bottom: 1px solid #e5e7eb; }
.data-table td { padding: 12px 16px; border-bottom: 1px solid #f3f4f6; color: #4b5563; }
.data-table tr:hover { background: #f9fafb; }
.product-cell { display: flex; align-items: center; gap: 10px; }
.thumb { width: 40px; height: 40px; border-radius: 6px; object-fit: cover; }
.badge { padding: 2px 10px; border-radius: 99px; font-size: 12px; font-weight: 500; }
.badge-green { background: #dcfce7; color: #166534; }
.badge-gray { background: #f3f4f6; color: #6b7280; }
.action-btns { display: flex; gap: 6px; }
.btn-sm { padding: 4px 12px; font-size: 12px; border-radius: 6px; border: 1px solid #d1d5db; background: #fff; color: #374151; cursor: pointer; text-decoration: none; }
.btn-sm:hover { background: #f3f4f6; }
.btn-danger { color: #dc2626; border-color: #fca5a5; }
.btn-danger:hover { background: #fef2f2; }
.empty { text-align: center; color: #9ca3af; padding: 40px !important; }
.pagination { display: flex; align-items: center; justify-content: center; gap: 16px; }
.pagination button { padding: 6px 16px; border: 1px solid #d1d5db; border-radius: 6px; background: #fff; cursor: pointer; font-size: 13px; }
.pagination button:disabled { opacity: 0.4; cursor: not-allowed; }
.pagination span { font-size: 13px; color: #6b7280; }
</style>
