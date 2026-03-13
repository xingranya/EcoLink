import http from './http';

export interface AdminDashboardStats {
  productCount: number;
  orderCount: number;
  userCount: number;
  categoryCount: number;
  onSaleProductCount: number;
  offSaleProductCount: number;
  lowStockProductCount: number;
  unpaidOrderCount: number;
  paidOrderCount: number;
  shippedOrderCount: number;
  completedOrderCount: number;
  revenueAmount: number;
  recentOrders: Array<{
    id: number;
    orderNo: string;
    status: string;
    receiverName: string;
    totalAmount: number;
    createdAt: string;
  }>;
  hotProducts: Array<{
    id: number;
    name: string;
    sales: number;
    stock: number;
    status: string;
    mainImage?: string;
  }>;
}

/** 后台管理 API */
export const adminApi = {
  /** 仪表盘统计 */
  dashboard() {
    return http.get<AdminDashboardStats>('/admin/dashboard');
  },

  /** 分类列表 */
  categoryList() {
    return http.get<{ id: number; name: string; sort: number; enabled: boolean }[]>('/admin/categories');
  },
  categoryCreate(data: { name: string; sort?: number; enabled?: boolean }) {
    return http.post('/admin/categories', data);
  },
  categoryUpdate(id: number, data: { name: string; sort?: number; enabled?: boolean }) {
    return http.put(`/admin/categories/${id}`, data);
  },
  categoryDelete(id: number) {
    return http.delete(`/admin/categories/${id}`);
  },

  /** 商品列表 */
  productList(params: { page?: number; size?: number; keyword?: string; categoryId?: number; status?: string }) {
    return http.get<{
      content: any[];
      totalElements: number;
      totalPages: number;
      number: number;
    }>('/admin/products', params);
  },
  productDetail(id: number) {
    return http.get<any>(`/admin/products/${id}`);
  },
  productCreate(data: any) {
    return http.post('/admin/products', data);
  },
  productUpdate(id: number, data: any) {
    return http.put(`/admin/products/${id}`, data);
  },
  productDelete(id: number) {
    return http.delete(`/admin/products/${id}`);
  },

  /** 订单列表 */
  orderList(params: { page?: number; size?: number; orderNo?: string; status?: string }) {
    return http.get<{
      content: any[];
      totalElements: number;
      totalPages: number;
      number: number;
    }>('/admin/orders', params);
  },
  orderDetail(id: number) {
    return http.get<{ order: any; items: any[] }>(`/admin/orders/${id}`);
  },
  orderUpdateStatus(id: number, status: string) {
    return http.put(`/admin/orders/${id}/status`, { status });
  },
};
