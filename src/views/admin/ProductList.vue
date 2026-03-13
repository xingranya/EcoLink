<template>
  <div class="admin-page">
    <section class="page-hero">
      <div>
        <p class="page-eyebrow">商品运营</p>
        <h3>商品管理中心</h3>
        <p>支持按关键词、分类、状态快速筛选，并可直接上下架商品。</p>
      </div>
      <RouterLink to="/admin/products/new" class="btn-primary">
        <span class="material-symbols-outlined">add</span>
        新增商品
      </RouterLink>
    </section>

    <section class="stats-strip">
      <article class="mini-stat">
        <span>当前结果</span>
        <strong>{{ totalElements }}</strong>
      </article>
      <article class="mini-stat">
        <span>在售商品</span>
        <strong>{{ onSaleCount }}</strong>
      </article>
      <article class="mini-stat">
        <span>低库存</span>
        <strong>{{ lowStockCount }}</strong>
      </article>
      <article class="mini-stat">
        <span>本页销量</span>
        <strong>{{ currentSales }}</strong>
      </article>
    </section>

    <section class="filter-panel">
      <div class="filter-grid">
        <input v-model.trim="keyword" placeholder="搜索商品名称或副标题" class="input" @keyup.enter="search" />
        <select v-model="categoryId" class="input" @change="search">
          <option :value="0">全部分类</option>
          <option v-for="item in categories" :key="item.id" :value="item.id">{{ item.name }}</option>
        </select>
        <select v-model="status" class="input" @change="search">
          <option value="">全部状态</option>
          <option value="ON_SALE">在售</option>
          <option value="OFF_SALE">下架</option>
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
            <th>ID</th>
            <th>商品信息</th>
            <th>分类</th>
            <th>价格</th>
            <th>库存</th>
            <th>销量</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.id">
            <td class="id-cell">#{{ item.id }}</td>
            <td>
              <div class="product-cell">
                <img v-if="item.mainImage" :src="item.mainImage" :alt="item.name" class="thumb" />
                <div class="product-copy">
                  <p class="product-name">{{ item.name }}</p>
                  <p class="product-subtitle">{{ item.subtitle || '暂无副标题' }}</p>
                </div>
              </div>
            </td>
            <td>{{ item.category?.name || item.categoryName || '-' }}</td>
            <td class="price-cell">¥{{ formatMoney(item.price) }}</td>
            <td>
              <span class="stock-pill" :class="item.stock <= 50 ? 'stock-pill-warn' : 'stock-pill-safe'">
                {{ item.stock }}
              </span>
            </td>
            <td>{{ item.sales }}</td>
            <td>
              <span class="badge" :class="item.status === 'ON_SALE' ? 'badge-green' : 'badge-gray'">
                {{ item.status === 'ON_SALE' ? '在售' : '下架' }}
              </span>
            </td>
            <td>
              <div class="action-btns">
                <button class="btn-sm" :disabled="togglingId === item.id" @click="toggleStatus(item)">
                  {{ togglingId === item.id ? '处理中...' : item.status === 'ON_SALE' ? '下架' : '上架' }}
                </button>
                <RouterLink :to="`/admin/products/${item.id}/edit`" class="btn-sm">编辑</RouterLink>
                <button class="btn-sm btn-danger" @click="handleDelete(item.id)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="!list.length">
            <td colspan="8" class="empty">暂无符合条件的商品</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination" v-if="totalPages > 1">
      <button :disabled="page === 0" @click="changePage(page - 1)">上一页</button>
      <span>第 {{ page + 1 }} / {{ totalPages }} 页</span>
      <button :disabled="page + 1 >= totalPages" @click="changePage(page + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { adminApi } from '@/api/admin';
import type { Category } from '@/types/api';

const list = ref<any[]>([]);
const categories = ref<Category[]>([]);
const keyword = ref('');
const categoryId = ref(0);
const status = ref('');
const page = ref(0);
const totalPages = ref(0);
const totalElements = ref(0);
const togglingId = ref(0);

const onSaleCount = computed(() => list.value.filter((item) => item.status === 'ON_SALE').length);
const lowStockCount = computed(() => list.value.filter((item) => Number(item.stock) <= 50).length);
const currentSales = computed(() => list.value.reduce((sum, item) => sum + Number(item.sales || 0), 0));

function formatMoney(value: number | string) {
  return Number(value || 0).toFixed(2);
}

async function load() {
  const data = await adminApi.productList({
    page: page.value,
    size: 10,
    keyword: keyword.value || undefined,
    categoryId: categoryId.value || undefined,
    status: status.value || undefined,
  });
  list.value = data.content;
  totalPages.value = data.totalPages;
  totalElements.value = data.totalElements;
}

async function loadCategories() {
  categories.value = await adminApi.categoryList();
}

function search() {
  page.value = 0;
  load();
}

function resetFilters() {
  keyword.value = '';
  categoryId.value = 0;
  status.value = '';
  search();
}

function changePage(nextPage: number) {
  page.value = nextPage;
  load();
}

async function toggleStatus(item: any) {
  togglingId.value = item.id;
  try {
    await adminApi.productUpdate(item.id, {
      categoryId: item.category?.id || item.categoryId,
      name: item.name,
      subtitle: item.subtitle || '',
      price: item.price,
      stock: item.stock,
      mainImage: item.mainImage || '',
      detail: item.detail || '',
      status: item.status === 'ON_SALE' ? 'OFF_SALE' : 'ON_SALE',
    });
    await load();
  } finally {
    togglingId.value = 0;
  }
}

async function handleDelete(id: number) {
  if (!confirm('确认删除该商品？')) return;
  await adminApi.productDelete(id);
  if (list.value.length === 1 && page.value > 0) {
    page.value -= 1;
  }
  await load();
}

onMounted(async () => {
  await Promise.all([loadCategories(), load()]);
});
</script>

<style scoped>
.admin-page { display: flex; flex-direction: column; gap: 16px; }

.page-hero {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  padding: 22px 24px;
  border-radius: 22px;
  background: linear-gradient(135deg, #f0fdf4, #dcfce7);
  border: 1px solid #bbf7d0;
}
.page-eyebrow { margin: 0 0 8px; font-size: 12px; letter-spacing: 0.16em; color: #166534; text-transform: uppercase; }
.page-hero h3 { margin: 0; font-size: 24px; font-weight: 800; color: #14532d; }
.page-hero p:last-child { margin: 8px 0 0; color: #3f6212; }

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
  grid-template-columns: minmax(240px, 1.4fr) repeat(2, minmax(160px, 0.7fr));
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
  background: #fff;
}
.input:focus { border-color: #22c55e; box-shadow: 0 0 0 4px rgba(34, 197, 94, 0.12); }

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
  text-decoration: none;
  cursor: pointer;
  border: none;
}
.btn-primary { background: #16a34a; color: #fff; }
.btn-primary:hover { background: #15803d; }
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
.data-table tr:hover { background: #fcfffd; }

.id-cell { color: #64748b; font-weight: 700; }
.product-cell { display: flex; align-items: center; gap: 12px; }
.thumb {
  width: 54px;
  height: 54px;
  border-radius: 16px;
  object-fit: cover;
  background: #f1f5f9;
}
.product-copy { min-width: 0; }
.product-name { margin: 0; font-weight: 700; color: #0f172a; }
.product-subtitle {
  margin: 4px 0 0;
  max-width: 320px;
  color: #64748b;
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.price-cell { font-weight: 800; color: #15803d; }

.stock-pill,
.badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 54px;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}
.stock-pill-safe { background: #dcfce7; color: #166534; }
.stock-pill-warn { background: #fef3c7; color: #92400e; }
.badge-green { background: #dcfce7; color: #166534; }
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
  text-decoration: none;
  cursor: pointer;
}
.btn-sm:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-sm:hover:not(:disabled) { background: #f8fafc; }
.btn-danger { color: #dc2626; border-color: #fecaca; }
.btn-danger:hover { background: #fff1f2; }

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

@media (max-width: 1080px) {
  .stats-strip { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .filter-panel { flex-direction: column; align-items: stretch; }
  .filter-grid { grid-template-columns: 1fr; }
}

@media (max-width: 720px) {
  .page-hero { flex-direction: column; align-items: stretch; }
  .stats-strip { grid-template-columns: 1fr; }
  .data-table { font-size: 13px; }
}
</style>
