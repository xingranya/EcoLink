export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp?: string;
}

export interface PageResult<T> {
  list: T[];
  page: number;
  size: number;
  total: number;
}

export interface UserMe {
  id: number;
  username: string;
  nickname: string;
  phone?: string;
}

export interface AuthResult {
  token: string;
  user: UserMe;
}

export interface Category {
  id: number;
  name: string;
}

export interface ProductItem {
  id: number;
  categoryId: number;
  categoryName: string;
  name: string;
  subtitle?: string;
  price: number;
  stock: number;
  sales: number;
  mainImage?: string;
}

export interface ProductDetail extends ProductItem {
  detail?: string;
  images: string[];
}

export interface CartItem {
  id: number;
  productId: number;
  productName: string;
  productImage?: string;
  price: number;
  quantity: number;
  stock: number;
  subtotal: number;
}

export interface CartData {
  items: CartItem[];
  totalAmount: number;
}

export interface Address {
  id: number;
  receiverName: string;
  receiverPhone: string;
  detail: string;
  isDefault: boolean;
}

export interface FavoriteItem {
  id: number;
  productId: number;
  productName: string;
  productImage?: string;
  price: number;
}

export type OrderStatus = 'UNPAID' | 'PAID' | 'SHIPPED' | 'COMPLETED' | 'CANCELLED';

export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  productImage?: string;
  salePrice: number;
  quantity: number;
  subtotal: number;
}

export interface OrderData {
  id: number;
  orderNo: string;
  status: OrderStatus;
  totalAmount: number;
  receiverName: string;
  receiverPhone: string;
  receiverAddress: string;
  paidAt?: string;
  createdAt: string;
  items: OrderItem[];
}
