<template>
  <div class="admin-page">
    <div class="form-card">
      <h3>{{ isEdit ? '编辑商品' : '新增商品' }}</h3>
      <form @submit.prevent="handleSubmit" class="form-body">
        <div class="form-row">
          <label>商品名称 <span class="required">*</span></label>
          <input v-model="form.name" class="input" required />
        </div>
        <div class="form-row">
          <label>分类 <span class="required">*</span></label>
          <select v-model="form.categoryId" class="input" required>
            <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
          </select>
        </div>
        <div class="form-row">
          <label>副标题</label>
          <input v-model="form.subtitle" class="input" />
        </div>
        <div class="form-row-grid">
          <div class="form-row">
            <label>价格 <span class="required">*</span></label>
            <input v-model.number="form.price" type="number" step="0.01" class="input" required />
          </div>
          <div class="form-row">
            <label>库存</label>
            <input v-model.number="form.stock" type="number" class="input" />
          </div>
        </div>
        <div class="form-row">
          <label>主图 URL</label>
          <input v-model="form.mainImage" class="input" placeholder="https://..." />
        </div>
        <div class="form-row">
          <label>商品详情</label>
          <textarea v-model="form.detail" class="input textarea" rows="4"></textarea>
        </div>
        <div class="form-row">
          <label>状态</label>
          <select v-model="form.status" class="input">
            <option value="ON_SALE">在售</option>
            <option value="OFF_SALE">下架</option>
          </select>
        </div>
        <div class="form-actions">
          <button type="submit" class="btn-primary" :disabled="loading">{{ loading ? '保存中…' : '保存' }}</button>
          <RouterLink to="/admin/products" class="btn-cancel">取消</RouterLink>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter, RouterLink } from 'vue-router';
import { adminApi } from '@/api/admin';

const route = useRoute();
const router = useRouter();
const isEdit = computed(() => !!route.params.id);
const loading = ref(false);
const categories = ref<any[]>([]);

const form = ref({
  categoryId: null as number | null,
  name: '',
  subtitle: '',
  price: 0,
  stock: 0,
  mainImage: '',
  detail: '',
  status: 'ON_SALE',
});

onMounted(async () => {
  const cats = await adminApi.categoryList();
  categories.value = cats;
  if (isEdit.value) {
    const data = await adminApi.productList({ page: 0, size: 1, keyword: undefined });
    // 简单方案：用列表查找
    const all = await adminApi.productList({ page: 0, size: 999 });
    const p = all.content.find((x: any) => x.id === Number(route.params.id));
    if (p) {
      form.value = {
        categoryId: p.category?.id || p.categoryId,
        name: p.name,
        subtitle: p.subtitle || '',
        price: p.price,
        stock: p.stock,
        mainImage: p.mainImage || '',
        detail: p.detail || '',
        status: p.status,
      };
    }
  }
});

async function handleSubmit() {
  loading.value = true;
  try {
    if (isEdit.value) {
      await adminApi.productUpdate(Number(route.params.id), form.value);
    } else {
      await adminApi.productCreate(form.value);
    }
    router.push('/admin/products');
  } catch { /* 忽略 */ }
  loading.value = false;
}
</script>

<style scoped>
.form-card { background: #fff; border-radius: 12px; padding: 28px 32px; box-shadow: 0 1px 3px rgba(0,0,0,0.06); max-width: 640px; }
.form-card h3 { font-size: 16px; font-weight: 600; margin-bottom: 20px; color: #1a1a2e; }
.form-body { display: flex; flex-direction: column; gap: 16px; }
.form-row { display: flex; flex-direction: column; gap: 4px; }
.form-row label { font-size: 13px; font-weight: 500; color: #374151; }
.required { color: #dc2626; }
.form-row-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.input {
  padding: 8px 14px; border: 1px solid #d1d5db; border-radius: 8px;
  font-size: 14px; outline: none; font-family: inherit;
}
.input:focus { border-color: #4ade80; box-shadow: 0 0 0 3px rgba(74, 222, 128, 0.15); }
.textarea { resize: vertical; }
.form-actions { display: flex; gap: 12px; margin-top: 8px; }
.btn-primary {
  padding: 10px 24px; background: #4ade80; color: #fff;
  border: none; border-radius: 8px; font-size: 14px; font-weight: 500; cursor: pointer;
}
.btn-primary:hover { background: #22c55e; }
.btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-cancel { padding: 10px 24px; border: 1px solid #d1d5db; border-radius: 8px; color: #374151; text-decoration: none; font-size: 14px; }
</style>
