<template>
  <div class="admin-page">
    <div class="form-layout">
      <section class="form-card">
        <div class="section-head">
          <div>
            <p class="eyebrow">{{ isEdit ? '商品编辑' : '新品录入' }}</p>
            <h3>{{ isEdit ? '编辑商品信息' : '创建新商品' }}</h3>
          </div>
          <RouterLink to="/admin/products" class="btn-cancel">返回列表</RouterLink>
        </div>

        <form @submit.prevent="handleSubmit" class="form-body">
          <div class="form-row">
            <label>商品名称 <span class="required">*</span></label>
            <input v-model.trim="form.name" class="input" required />
          </div>

          <div class="form-grid">
            <div class="form-row">
              <label>分类 <span class="required">*</span></label>
              <select v-model="form.categoryId" class="input" required>
                <option :value="null" disabled>请选择分类</option>
                <option v-for="item in categories" :key="item.id" :value="item.id">{{ item.name }}</option>
              </select>
            </div>
            <div class="form-row">
              <label>状态</label>
              <select v-model="form.status" class="input">
                <option value="ON_SALE">在售</option>
                <option value="OFF_SALE">下架</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <label>副标题</label>
            <input v-model.trim="form.subtitle" class="input" placeholder="可选，用于强调卖点" />
          </div>

          <div class="form-grid">
            <div class="form-row">
              <label>价格 <span class="required">*</span></label>
              <input v-model.number="form.price" type="number" step="0.01" min="0" class="input" required />
            </div>
            <div class="form-row">
              <label>库存</label>
              <input v-model.number="form.stock" type="number" min="0" class="input" />
            </div>
          </div>

          <div class="form-row">
            <label>主图 URL</label>
            <input v-model.trim="form.mainImage" class="input" placeholder="https://example.com/image.jpg" />
          </div>

          <div class="form-row">
            <label>商品详情</label>
            <textarea v-model.trim="form.detail" class="input textarea" rows="6" placeholder="填写产地、规格、配送说明等信息"></textarea>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn-primary" :disabled="loading || !form.categoryId">
              {{ loading ? '保存中...' : '保存商品' }}
            </button>
            <RouterLink to="/admin/products" class="btn-cancel">取消</RouterLink>
          </div>
        </form>
      </section>

      <aside class="preview-card">
        <p class="eyebrow">实时预览</p>
        <article class="preview-box">
          <img :src="form.mainImage || fallbackImage" :alt="form.name || '商品预览'" class="preview-image" />
          <div class="preview-copy">
            <p class="preview-badge">{{ form.status === 'ON_SALE' ? '在售中' : '待上架' }}</p>
            <h4>{{ form.name || '商品名称' }}</h4>
            <p class="preview-subtitle">{{ form.subtitle || '这里会显示商品副标题' }}</p>
            <div class="preview-meta">
              <strong>¥{{ Number(form.price || 0).toFixed(2) }}</strong>
              <span>库存 {{ form.stock || 0 }}</span>
            </div>
            <p class="preview-detail">{{ form.detail || '详情文案将在这里展示，建议补充产地、规格、保存方式和配送说明。' }}</p>
          </div>
        </article>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter, RouterLink } from 'vue-router';
import { adminApi } from '@/api/admin';
import type { Category } from '@/types/api';

const fallbackImage = 'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=1200&q=80';
const route = useRoute();
const router = useRouter();
const isEdit = computed(() => Boolean(route.params.id));
const loading = ref(false);
const categories = ref<Category[]>([]);

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

async function loadCategories() {
  categories.value = await adminApi.categoryList();
}

async function loadProduct() {
  if (!isEdit.value) return;
  const product = await adminApi.productDetail(Number(route.params.id));
  form.value = {
    categoryId: product.category?.id || product.categoryId,
    name: product.name,
    subtitle: product.subtitle || '',
    price: Number(product.price || 0),
    stock: Number(product.stock || 0),
    mainImage: product.mainImage || '',
    detail: product.detail || '',
    status: product.status || 'ON_SALE',
  };
}

async function handleSubmit() {
  loading.value = true;
  try {
    if (isEdit.value) {
      await adminApi.productUpdate(Number(route.params.id), form.value);
    } else {
      await adminApi.productCreate(form.value);
    }
    router.push('/admin/products');
  } finally {
    loading.value = false;
  }
}

onMounted(async () => {
  await loadCategories();
  await loadProduct();
});
</script>

<style scoped>
.admin-page { display: flex; flex-direction: column; gap: 16px; }
.form-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(320px, 0.8fr);
  gap: 18px;
}

.form-card,
.preview-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 24px;
  padding: 24px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.04);
}

.section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 20px;
}
.eyebrow { margin: 0 0 8px; font-size: 12px; letter-spacing: 0.16em; text-transform: uppercase; color: #15803d; }
.section-head h3 { margin: 0; font-size: 24px; font-weight: 800; color: #0f172a; }

.form-body { display: flex; flex-direction: column; gap: 16px; }
.form-row { display: flex; flex-direction: column; gap: 6px; }
.form-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16px; }
.form-row label { font-size: 13px; font-weight: 700; color: #334155; }
.required { color: #dc2626; }

.input {
  width: 100%;
  padding: 11px 14px;
  border: 1px solid #cbd5e1;
  border-radius: 14px;
  font-size: 14px;
  outline: none;
  font-family: inherit;
}
.input:focus { border-color: #22c55e; box-shadow: 0 0 0 4px rgba(34, 197, 94, 0.12); }
.textarea { resize: vertical; min-height: 148px; }

.form-actions { display: flex; gap: 12px; margin-top: 8px; }
.btn-primary,
.btn-cancel {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 18px;
  border-radius: 12px;
  text-decoration: none;
  font-size: 14px;
  font-weight: 700;
}
.btn-primary { border: none; background: #16a34a; color: #fff; cursor: pointer; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-cancel { border: 1px solid #cbd5e1; color: #334155; background: #fff; }

.preview-box {
  overflow: hidden;
  border-radius: 22px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}
.preview-image {
  width: 100%;
  aspect-ratio: 1 / 1;
  object-fit: cover;
  background: #e2e8f0;
}
.preview-copy { padding: 18px; }
.preview-badge {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: #dcfce7;
  color: #166534;
  font-size: 12px;
  font-weight: 700;
}
.preview-copy h4 { margin: 12px 0 0; font-size: 22px; color: #0f172a; }
.preview-subtitle { margin: 8px 0 0; color: #64748b; line-height: 1.6; }
.preview-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 16px;
}
.preview-meta strong { font-size: 28px; color: #15803d; }
.preview-meta span { color: #475569; font-size: 13px; }
.preview-detail {
  margin: 16px 0 0;
  color: #475569;
  line-height: 1.8;
  white-space: pre-wrap;
}

@media (max-width: 1080px) {
  .form-layout { grid-template-columns: 1fr; }
}

@media (max-width: 720px) {
  .form-grid { grid-template-columns: 1fr; }
  .section-head { flex-direction: column; }
}
</style>
