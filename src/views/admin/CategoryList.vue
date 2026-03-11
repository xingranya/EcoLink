<template>
  <div class="admin-page">
    <div class="page-header">
      <h3>分类管理</h3>
      <button class="btn-primary" @click="showModal = true; editId = null; form = { name: '', sort: 0, enabled: true }">
        <span class="material-symbols-outlined">add</span> 新增分类
      </button>
    </div>
    <div class="table-wrapper">
      <table class="data-table">
        <thead><tr><th>ID</th><th>名称</th><th>排序</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="c in list" :key="c.id">
            <td>{{ c.id }}</td>
            <td>{{ c.name }}</td>
            <td>{{ c.sort }}</td>
            <td><span class="badge" :class="c.enabled ? 'badge-green' : 'badge-gray'">{{ c.enabled ? '启用' : '禁用' }}</span></td>
            <td>
              <div class="action-btns">
                <button class="btn-sm" @click="handleEdit(c)">编辑</button>
                <button class="btn-sm btn-danger" @click="handleDelete(c.id)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="!list.length"><td colspan="5" class="empty">暂无数据</td></tr>
        </tbody>
      </table>
    </div>
    <!-- 弹窗 -->
    <div v-if="showModal" class="modal-mask" @click.self="showModal = false">
      <div class="modal-card">
        <h4>{{ editId ? '编辑分类' : '新增分类' }}</h4>
        <form @submit.prevent="handleSubmit" class="form-body">
          <div class="form-row"><label>名称</label><input v-model="form.name" class="input" required /></div>
          <div class="form-row"><label>排序</label><input v-model.number="form.sort" type="number" class="input" /></div>
          <div class="form-row"><label><input type="checkbox" v-model="form.enabled" /> 启用</label></div>
          <div class="form-actions">
            <button type="submit" class="btn-primary">保存</button>
            <button type="button" class="btn-cancel" @click="showModal = false">取消</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { adminApi } from '@/api/admin';

const list = ref<any[]>([]);
const showModal = ref(false);
const editId = ref<number | null>(null);
const form = ref({ name: '', sort: 0, enabled: true });

async function load() {
  list.value = await adminApi.categoryList();
}

function handleEdit(c: any) {
  editId.value = c.id;
  form.value = { name: c.name, sort: c.sort, enabled: c.enabled };
  showModal.value = true;
}

async function handleSubmit() {
  if (editId.value) {
    await adminApi.categoryUpdate(editId.value, form.value);
  } else {
    await adminApi.categoryCreate(form.value);
  }
  showModal.value = false;
  load();
}

async function handleDelete(id: number) {
  if (!confirm('确认删除？')) return;
  await adminApi.categoryDelete(id);
  load();
}

onMounted(load);
</script>

<style scoped>
.admin-page { display: flex; flex-direction: column; gap: 16px; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h3 { font-size: 16px; font-weight: 600; color: #1a1a2e; }
.btn-primary { display: inline-flex; align-items: center; gap: 4px; padding: 8px 18px; background: #4ade80; color: #fff; font-size: 14px; font-weight: 500; border: none; border-radius: 8px; cursor: pointer; }
.btn-primary:hover { background: #22c55e; }
.table-wrapper { background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.06); }
.data-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.data-table th { background: #f9fafb; padding: 12px 16px; text-align: left; font-weight: 600; color: #374151; border-bottom: 1px solid #e5e7eb; }
.data-table td { padding: 12px 16px; border-bottom: 1px solid #f3f4f6; color: #4b5563; }
.badge { padding: 2px 10px; border-radius: 99px; font-size: 12px; font-weight: 500; }
.badge-green { background: #dcfce7; color: #166534; }
.badge-gray { background: #f3f4f6; color: #6b7280; }
.action-btns { display: flex; gap: 6px; }
.btn-sm { padding: 4px 12px; font-size: 12px; border-radius: 6px; border: 1px solid #d1d5db; background: #fff; color: #374151; cursor: pointer; }
.btn-sm:hover { background: #f3f4f6; }
.btn-danger { color: #dc2626; border-color: #fca5a5; }
.empty { text-align: center; color: #9ca3af; padding: 40px !important; }
.modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 200; }
.modal-card { background: #fff; border-radius: 12px; padding: 24px 28px; width: 400px; box-shadow: 0 8px 30px rgba(0,0,0,0.15); }
.modal-card h4 { font-size: 16px; margin-bottom: 16px; }
.form-body { display: flex; flex-direction: column; gap: 12px; }
.form-row { display: flex; flex-direction: column; gap: 4px; }
.form-row label { font-size: 13px; font-weight: 500; color: #374151; }
.input { padding: 8px 14px; border: 1px solid #d1d5db; border-radius: 8px; font-size: 14px; outline: none; }
.input:focus { border-color: #4ade80; }
.form-actions { display: flex; gap: 12px; margin-top: 8px; }
.btn-cancel { padding: 8px 18px; border: 1px solid #d1d5db; border-radius: 8px; background: #fff; color: #374151; cursor: pointer; font-size: 14px; }
</style>
