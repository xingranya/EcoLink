import type {
  Address,
  AuthResult,
  CartData,
  CartItem,
  Category,
  FavoriteItem,
  OrderData,
  OrderItem,
  PageResult,
  ProductDetail,
  ProductItem,
  UserMe,
} from '@/types/api';

type HttpMethod = 'get' | 'post' | 'put' | 'delete';

interface DemoUser extends UserMe {
  password: string;
}

interface DemoDb {
  users: DemoUser[];
  categories: Category[];
  products: ProductDetail[];
  carts: Record<string, CartItem[]>;
  favorites: Record<string, FavoriteItem[]>;
  addresses: Record<string, Address[]>;
  orders: Record<string, OrderData[]>;
  seq: {
    userId: number;
    cartItemId: number;
    addressId: number;
    orderId: number;
    orderItemId: number;
    favoriteId: number;
  };
}

const STORAGE_KEY = 'ecolink_demo_db_v2';

function deepClone<T>(data: T): T {
  return JSON.parse(JSON.stringify(data)) as T;
}

function sleep(ms = 180) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function normalizePath(url: string) {
  return url.split('?')[0];
}

function toNumber(value: unknown, fallback: number) {
  const num = Number(value);
  return Number.isFinite(num) ? num : fallback;
}

function buildToken(userId: number) {
  return `mock-${userId}-${Date.now()}`;
}

function parseToken(token?: string | null) {
  if (!token) return 0;
  const [, id] = token.split('-');
  const userId = Number(id);
  return Number.isInteger(userId) ? userId : 0;
}

function keyByUser(userId: number) {
  return String(userId);
}

function formatOrderNo(id: number) {
  return `EL${new Date().toISOString().slice(0, 10).replace(/-/g, '')}${String(id).padStart(5, '0')}`;
}

function buildSeedProducts(): ProductDetail[] {
  return [
    {
      id: 1,
      categoryId: 1,
      categoryName: '新鲜瓜果',
      name: '高山阳光青提',
      subtitle: '脆甜多汁 · 产地直供 · 0添加',
      price: 29.9,
      stock: 128,
      sales: 532,
      mainImage: 'https://images.unsplash.com/photo-1464965911861-746a04b4bca6?auto=format&fit=crop&w=1200&q=80',
      images: [
        'https://images.unsplash.com/photo-1464965911861-746a04b4bca6?auto=format&fit=crop&w=1200&q=80',
        'https://images.unsplash.com/photo-1447175008436-054170c2e979?auto=format&fit=crop&w=1200&q=80',
      ],
      detail: '精选高海拔葡萄园青提，果肉紧实、皮薄清甜，冷链采后 24 小时内发出。',
    },
    {
      id: 2,
      categoryId: 2,
      categoryName: '时令蔬菜',
      name: '农家五彩小番茄',
      subtitle: '有机培育 · 果肉饱满 · 现采现发',
      price: 15.8,
      stock: 220,
      sales: 866,
      mainImage: 'https://images.unsplash.com/photo-1592924357228-91a4daadcfea?auto=format&fit=crop&w=1200&q=80',
      images: [
        'https://images.unsplash.com/photo-1592924357228-91a4daadcfea?auto=format&fit=crop&w=1200&q=80',
        'https://images.unsplash.com/photo-1471193945509-9ad0617afabf?auto=format&fit=crop&w=1200&q=80',
      ],
      detail: '温室自然授粉种植，酸甜平衡，适合生食和沙拉。',
    },
    {
      id: 3,
      categoryId: 3,
      categoryName: '肉禽蛋奶',
      name: '五谷散养土鸡蛋',
      subtitle: '林下散养 · 蛋黄橙红 · 营养丰富',
      price: 38,
      stock: 160,
      sales: 412,
      mainImage: 'https://images.unsplash.com/photo-1587486913049-53fc88980cfc?auto=format&fit=crop&w=1200&q=80',
      images: [
        'https://images.unsplash.com/photo-1587486913049-53fc88980cfc?auto=format&fit=crop&w=1200&q=80',
        'https://images.unsplash.com/photo-1498654200943-1088dd4438ae?auto=format&fit=crop&w=1200&q=80',
      ],
      detail: '散养土鸡全谷物饲喂，蛋香浓郁、口感扎实。',
    },
    {
      id: 4,
      categoryId: 5,
      categoryName: '优质粮油',
      name: '原生态石磨糙米',
      subtitle: '非转基因 · 全谷物 · 胚芽完整',
      price: 45,
      stock: 96,
      sales: 327,
      mainImage: 'https://images.unsplash.com/photo-1586201375761-83865001e31f?auto=format&fit=crop&w=1200&q=80',
      images: [
        'https://images.unsplash.com/photo-1586201375761-83865001e31f?auto=format&fit=crop&w=1200&q=80',
        'https://images.unsplash.com/photo-1516684732162-798a0062be99?auto=format&fit=crop&w=1200&q=80',
      ],
      detail: '保留胚芽层营养，适合家庭日常主食与轻食搭配。',
    },
    {
      id: 5,
      categoryId: 4,
      categoryName: '地方特产',
      name: '正宗陈年金华火腿',
      subtitle: '传统工艺 · 肉香浓郁',
      price: 268,
      stock: 32,
      sales: 119,
      mainImage: 'https://images.unsplash.com/photo-1576107232684-1279f390859f?auto=format&fit=crop&w=1200&q=80',
      images: [
        'https://images.unsplash.com/photo-1576107232684-1279f390859f?auto=format&fit=crop&w=1200&q=80',
        'https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=1200&q=80',
      ],
      detail: '优选后腿肉，经腌、洗、晒、发酵等工序制成，适合炖汤与切片冷盘。',
    },
    {
      id: 6,
      categoryId: 6,
      categoryName: '茶饮冲调',
      name: '武夷山岩骨大红袍',
      subtitle: '手工炭焙 · 醇厚回甘',
      price: 580,
      stock: 40,
      sales: 95,
      mainImage: 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?auto=format&fit=crop&w=1200&q=80',
      images: [
        'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?auto=format&fit=crop&w=1200&q=80',
        'https://images.unsplash.com/photo-1523920290228-4f321a939b4c?auto=format&fit=crop&w=1200&q=80',
      ],
      detail: '岩韵明显，花果香层次丰富，适合功夫茶冲泡。',
    },
    {
      id: 7,
      categoryId: 1,
      categoryName: '新鲜瓜果',
      name: '大凉山蓝莓鲜果',
      subtitle: '低温锁鲜 · 甜酸平衡',
      price: 36.9,
      stock: 88,
      sales: 204,
      mainImage: 'https://images.unsplash.com/photo-1498557850523-fd3d118b962e?auto=format&fit=crop&w=1200&q=80',
      images: [
        'https://images.unsplash.com/photo-1498557850523-fd3d118b962e?auto=format&fit=crop&w=1200&q=80',
        'https://images.unsplash.com/photo-1447175008436-054170c2e979?auto=format&fit=crop&w=1200&q=80',
      ],
      detail: '高原种植蓝莓，新鲜采摘，果粉完整。',
    },
    {
      id: 8,
      categoryId: 2,
      categoryName: '时令蔬菜',
      name: '有机奶油生菜',
      subtitle: '脆嫩清甜 · 无土培育',
      price: 9.9,
      stock: 180,
      sales: 388,
      mainImage: 'https://images.unsplash.com/photo-1498837167922-ddd27525d352?auto=format&fit=crop&w=1200&q=80',
      images: [
        'https://images.unsplash.com/photo-1498837167922-ddd27525d352?auto=format&fit=crop&w=1200&q=80',
        'https://images.unsplash.com/photo-1466637574441-749b8f19452f?auto=format&fit=crop&w=1200&q=80',
      ],
      detail: '全程温控运输，口感清爽，适合轻食配餐。',
    },
    {
      id: 9,
      categoryId: 3,
      categoryName: '肉禽蛋奶',
      name: '牧场新鲜全脂牛奶',
      subtitle: '巴氏杀菌 · 奶香浓郁',
      price: 18,
      stock: 140,
      sales: 477,
      mainImage: 'https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=1200&q=80',
      images: [
        'https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=1200&q=80',
        'https://images.unsplash.com/photo-1517448931760-9bf4414148c5?auto=format&fit=crop&w=1200&q=80',
      ],
      detail: '新鲜直送，口感丝滑，适合早餐与烘焙。',
    },
    {
      id: 10,
      categoryId: 5,
      categoryName: '优质粮油',
      name: '古法冷榨花生油',
      subtitle: '头道压榨 · 香味纯正',
      price: 69,
      stock: 66,
      sales: 146,
      mainImage: 'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?auto=format&fit=crop&w=1200&q=80',
      images: [
        'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?auto=format&fit=crop&w=1200&q=80',
        'https://images.unsplash.com/photo-1516684732162-798a0062be99?auto=format&fit=crop&w=1200&q=80',
      ],
      detail: '低温冷榨保留花生香气，适合凉拌与热炒。',
    },
    {
      id: 11,
      categoryId: 4,
      categoryName: '地方特产',
      name: '秦岭野生木耳',
      subtitle: '山泉培植 · 肉厚耐煮',
      price: 26.5,
      stock: 210,
      sales: 198,
      mainImage: 'https://images.unsplash.com/photo-1604909052743-94e838986d24?auto=format&fit=crop&w=1200&q=80',
      images: [
        'https://images.unsplash.com/photo-1604909052743-94e838986d24?auto=format&fit=crop&w=1200&q=80',
        'https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=1200&q=80',
      ],
      detail: '自然风干，泡发率高，适合家常快炒与炖煮。',
    },
    {
      id: 12,
      categoryId: 6,
      categoryName: '茶饮冲调',
      name: '安溪铁观音礼盒',
      subtitle: '兰花香显 · 回甘持久',
      price: 198,
      stock: 75,
      sales: 131,
      mainImage: 'https://images.unsplash.com/photo-1515823064-d6e0c04616a7?auto=format&fit=crop&w=1200&q=80',
      images: [
        'https://images.unsplash.com/photo-1515823064-d6e0c04616a7?auto=format&fit=crop&w=1200&q=80',
        'https://images.unsplash.com/photo-1523920290228-4f321a939b4c?auto=format&fit=crop&w=1200&q=80',
      ],
      detail: '精选春茶，礼盒装适合自饮与送礼。',
    },
  ].map((item) => ({
    ...item,
    status: [5, 12].includes(item.id) ? 'OFF_SALE' : 'ON_SALE',
  }));
}

function buildInitialDb(): DemoDb {
  const categories: Category[] = [
    { id: 1, name: '新鲜瓜果', sort: 1, enabled: true },
    { id: 2, name: '时令蔬菜', sort: 2, enabled: true },
    { id: 3, name: '肉禽蛋奶', sort: 3, enabled: true },
    { id: 4, name: '地方特产', sort: 4, enabled: true },
    { id: 5, name: '优质粮油', sort: 5, enabled: true },
    { id: 6, name: '茶饮冲调', sort: 6, enabled: true },
  ];
  const products = buildSeedProducts();
  const now = new Date().toISOString();
  const defaultOrderItems: OrderItem[] = [
    {
      id: 1,
      productId: 9,
      productName: '牧场新鲜全脂牛奶',
      productImage: products.find((item) => item.id === 9)?.mainImage,
      salePrice: 18,
      quantity: 2,
      subtotal: 36,
    },
  ];

  return {
    users: [
      { id: 1, username: 'demo', password: '123456', nickname: '生态用户', phone: '13800000000', role: 'USER' },
      { id: 2, username: 'admin', password: 'admin123', nickname: '超级管理员', phone: '', role: 'ADMIN' },
    ],
    categories,
    products,
    carts: {
      '1': [
        {
          id: 1,
          productId: 2,
          productName: '农家五彩小番茄',
          productImage: products.find((item) => item.id === 2)?.mainImage,
          price: 15.8,
          quantity: 2,
          stock: 220,
          subtotal: 31.6,
        },
      ],
    },
    favorites: {
      '1': [
        {
          id: 1,
          productId: 6,
          productName: '武夷山岩骨大红袍',
          productImage: products.find((item) => item.id === 6)?.mainImage,
          price: 580,
        },
      ],
    },
    addresses: {
      '1': [
        {
          id: 1,
          receiverName: '张三',
          receiverPhone: '13800000000',
          detail: '北京市朝阳区生态大道 88 号 3-1201',
          isDefault: true,
        },
      ],
    },
    orders: {
      '1': [
        {
          id: 1,
          orderNo: formatOrderNo(1),
          status: 'PAID',
          totalAmount: 36,
          receiverName: '张三',
          receiverPhone: '13800000000',
          receiverAddress: '北京市朝阳区生态大道 88 号 3-1201',
          createdAt: now,
          paidAt: now,
          items: defaultOrderItems,
        },
      ],
    },
    seq: {
      userId: 3,
      cartItemId: 2,
      addressId: 2,
      orderId: 2,
      orderItemId: 2,
      favoriteId: 2,
    },
  };
}

function readDb(): DemoDb {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) {
    const seed = buildInitialDb();
    localStorage.setItem(STORAGE_KEY, JSON.stringify(seed));
    return seed;
  }
  try {
    return JSON.parse(raw) as DemoDb;
  } catch {
    const seed = buildInitialDb();
    localStorage.setItem(STORAGE_KEY, JSON.stringify(seed));
    return seed;
  }
}

function writeDb(db: DemoDb) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(db));
}

function getCurrentUser(db: DemoDb): DemoUser | null {
  const userId = parseToken(localStorage.getItem('ecolink_token'));
  if (!userId) return null;
  return db.users.find((item) => item.id === userId) || null;
}

async function fail(message: string): Promise<never> {
  await sleep();
  throw new Error(message);
}

function toPublicUser(user: DemoUser): UserMe {
  const { password: _password, ...rest } = user;
  return rest;
}

function requireLogin(db: DemoDb) {
  const user = getCurrentUser(db);
  if (!user) {
    throw new Error('请先登录');
  }
  return user;
}

function requireAdmin(db: DemoDb) {
  const user = requireLogin(db);
  if (user.role !== 'ADMIN') {
    throw new Error('需要管理员权限');
  }
  return user;
}

function getProductOrThrow(db: DemoDb, productId: number) {
  const product = db.products.find((item) => item.id === productId);
  if (!product) {
    throw new Error('商品不存在');
  }
  return product;
}

function getUserAddressList(db: DemoDb, userId: number) {
  const key = keyByUser(userId);
  if (!db.addresses[key]) {
    db.addresses[key] = [];
  }
  return db.addresses[key];
}

function getUserCart(db: DemoDb, userId: number) {
  const key = keyByUser(userId);
  if (!db.carts[key]) {
    db.carts[key] = [];
  }
  return db.carts[key];
}

function getUserFavorites(db: DemoDb, userId: number) {
  const key = keyByUser(userId);
  if (!db.favorites[key]) {
    db.favorites[key] = [];
  }
  return db.favorites[key];
}

function getUserOrders(db: DemoDb, userId: number) {
  const key = keyByUser(userId);
  if (!db.orders[key]) {
    db.orders[key] = [];
  }
  return db.orders[key];
}

function getAllOrders(db: DemoDb) {
  return Object.values(db.orders)
    .flat()
    .slice()
    .sort((a, b) => b.id - a.id);
}

function paginate<T>(rows: T[], params: Record<string, unknown>, defaultSize = 10) {
  const page = Math.max(0, toNumber(params.page, 0));
  const size = Math.max(1, toNumber(params.size, defaultSize));
  const start = page * size;
  return {
    content: rows.slice(start, start + size),
    totalElements: rows.length,
    totalPages: Math.max(1, Math.ceil(rows.length / size)),
    number: page,
  };
}

function toAdminProductRow(db: DemoDb, product: ProductDetail & { status?: string }) {
  const category = db.categories.find((item) => item.id === product.categoryId);
  return {
    ...product,
    category: category ? { id: category.id, name: category.name } : null,
  };
}

function toCartData(items: CartItem[]): CartData {
  const normalized = items.map((item) => ({
    ...item,
    subtotal: Number((item.price * item.quantity).toFixed(2)),
  }));
  return {
    items: normalized,
    totalAmount: Number(normalized.reduce((sum, item) => sum + item.subtotal, 0).toFixed(2)),
  };
}

function listProducts(db: DemoDb, params: Record<string, unknown>) {
  const keyword = String(params.keyword || '').trim().toLowerCase();
  const categoryId = toNumber(params.categoryId, 0);
  const sort = String(params.sort || 'comprehensive');
  const page = Math.max(1, toNumber(params.page, 1));
  const size = Math.max(1, toNumber(params.size, 20));
  const minPrice = toNumber(params.minPrice, Number.NaN);
  const maxPrice = toNumber(params.maxPrice, Number.NaN);

  let rows = db.products.slice();
  if (keyword) {
    rows = rows.filter((item) => {
      const text = `${item.name} ${item.subtitle || ''} ${item.categoryName}`.toLowerCase();
      return text.includes(keyword);
    });
  }
  if (categoryId) {
    rows = rows.filter((item) => item.categoryId === categoryId);
  }
  if (!Number.isNaN(minPrice)) {
    rows = rows.filter((item) => Number(item.price) >= minPrice);
  }
  if (!Number.isNaN(maxPrice)) {
    rows = rows.filter((item) => Number(item.price) <= maxPrice);
  }

  if (sort === 'latest') {
    rows.sort((a, b) => b.id - a.id);
  } else if (sort === 'price_asc') {
    rows.sort((a, b) => Number(a.price) - Number(b.price));
  } else if (sort === 'price_desc') {
    rows.sort((a, b) => Number(b.price) - Number(a.price));
  } else {
    rows.sort((a, b) => b.sales - a.sales);
  }

  const total = rows.length;
  const start = (page - 1) * size;
  const list = rows.slice(start, start + size) as ProductItem[];
  const result: PageResult<ProductItem> = { list: deepClone(list), page, size, total };
  return result;
}

function createOrder(db: DemoDb, userId: number, payload: { addressId: number; cartItemIds: number[] }) {
  const cart = getUserCart(db, userId);
  const address = getUserAddressList(db, userId).find((item) => item.id === payload.addressId);
  if (!address) {
    throw new Error('收货地址不存在');
  }
  const selected = cart.filter((item) => payload.cartItemIds.includes(item.id));
  if (!selected.length) {
    throw new Error('请选择要下单的商品');
  }
  const orderItems: OrderItem[] = selected.map((item) => ({
    id: db.seq.orderItemId++,
    productId: item.productId,
    productName: item.productName,
    productImage: item.productImage,
    salePrice: item.price,
    quantity: item.quantity,
    subtotal: Number((item.price * item.quantity).toFixed(2)),
  }));
  const totalAmount = Number(orderItems.reduce((sum, item) => sum + item.subtotal, 0).toFixed(2));
  const order: OrderData = {
    id: db.seq.orderId++,
    orderNo: formatOrderNo(db.seq.orderId - 1),
    status: 'UNPAID',
    totalAmount,
    receiverName: address.receiverName,
    receiverPhone: address.receiverPhone,
    receiverAddress: address.detail,
    createdAt: new Date().toISOString(),
    items: orderItems,
  };
  const orders = getUserOrders(db, userId);
  orders.unshift(order);
  const remainIds = new Set(payload.cartItemIds);
  const remain = cart.filter((item) => !remainIds.has(item.id));
  db.carts[keyByUser(userId)] = remain;
  return order;
}

export async function mockRequest<T>(
  method: HttpMethod,
  url: string,
  data?: unknown,
  params?: unknown,
): Promise<T> {
  const db = readDb();
  const route = normalizePath(url);
  const chunks = route.split('/').filter(Boolean);

  try {
    if (method === 'post' && route === '/auth/login') {
      const payload = data as { username?: string; password?: string };
      const user = db.users.find((item) => item.username === payload.username);
      if (!user || user.password !== payload.password) {
        return fail('用户名或密码错误');
      }
      const result: AuthResult = { token: buildToken(user.id), user: toPublicUser(user) };
      await sleep();
      return deepClone(result) as T;
    }

    if (method === 'post' && route === '/auth/register') {
      const payload = data as { username?: string; password?: string; nickname?: string; phone?: string };
      if (!payload.username || !payload.password || !payload.nickname) {
        return fail('请完整填写注册信息');
      }
      if (db.users.some((item) => item.username === payload.username)) {
        return fail('用户名已存在');
      }
      const user: DemoUser = {
        id: db.seq.userId++,
        username: payload.username,
        password: payload.password,
        nickname: payload.nickname,
        phone: payload.phone || '',
        role: 'USER',
      };
      db.users.push(user);
      db.carts[keyByUser(user.id)] = [];
      db.favorites[keyByUser(user.id)] = [];
      db.addresses[keyByUser(user.id)] = [];
      db.orders[keyByUser(user.id)] = [];
      writeDb(db);
      const result: AuthResult = { token: buildToken(user.id), user: toPublicUser(user) };
      await sleep();
      return deepClone(result) as T;
    }

    if (method === 'get' && route === '/users/me') {
      const user = requireLogin(db);
      await sleep();
      return deepClone(toPublicUser(user)) as T;
    }

    if (chunks[0] === 'admin') {
      requireAdmin(db);
      const adminRoute = `/${chunks.slice(1).join('/')}`;

      if (method === 'get' && adminRoute === '/dashboard') {
        const allOrders = getAllOrders(db);
        const revenueAmount = Number(
          allOrders
            .filter((item) => item.status !== 'UNPAID')
            .reduce((sum, item) => sum + Number(item.totalAmount), 0)
            .toFixed(2),
        );
        const recentOrders = allOrders.slice(0, 5).map((item) => ({
          id: item.id,
          orderNo: item.orderNo,
          status: item.status,
          receiverName: item.receiverName,
          totalAmount: item.totalAmount,
          createdAt: item.createdAt,
        }));
        const hotProducts = db.products
          .slice()
          .sort((a, b) => b.sales - a.sales)
          .slice(0, 5)
          .map((item) => ({
            id: item.id,
            name: item.name,
            sales: item.sales,
            stock: item.stock,
            status: item.status || 'ON_SALE',
            mainImage: item.mainImage,
          }));

        await sleep();
        return deepClone({
          productCount: db.products.length,
          orderCount: allOrders.length,
          userCount: db.users.length,
          categoryCount: db.categories.length,
          onSaleProductCount: db.products.filter((item) => item.status !== 'OFF_SALE').length,
          offSaleProductCount: db.products.filter((item) => item.status === 'OFF_SALE').length,
          lowStockProductCount: db.products.filter((item) => item.stock <= 50).length,
          unpaidOrderCount: allOrders.filter((item) => item.status === 'UNPAID').length,
          paidOrderCount: allOrders.filter((item) => item.status === 'PAID').length,
          shippedOrderCount: allOrders.filter((item) => item.status === 'SHIPPED').length,
          completedOrderCount: allOrders.filter((item) => item.status === 'COMPLETED').length,
          revenueAmount,
          recentOrders,
          hotProducts,
        }) as T;
      }

      if (chunks[1] === 'categories') {
        if (method === 'get' && adminRoute === '/categories') {
          const rows = db.categories.slice().sort((a, b) => (a.sort || 0) - (b.sort || 0) || a.id - b.id);
          await sleep();
          return deepClone(rows) as T;
        }

        if (method === 'post' && adminRoute === '/categories') {
          const payload = data as { name?: string; sort?: number; enabled?: boolean };
          const category: Category = {
            id: db.categories.length ? Math.max(...db.categories.map((item) => item.id)) + 1 : 1,
            name: String(payload.name || '').trim(),
            sort: toNumber(payload.sort, 0),
            enabled: payload.enabled !== false,
          };
          db.categories.push(category);
          writeDb(db);
          await sleep();
          return deepClone(category) as T;
        }

        if (chunks[2] && method === 'put') {
          const id = Number(chunks[2]);
          const payload = data as { name?: string; sort?: number; enabled?: boolean };
          const target = db.categories.find((item) => item.id === id);
          if (!target) {
            return fail('分类不存在');
          }
          target.name = String(payload.name || target.name).trim();
          target.sort = toNumber(payload.sort, target.sort || 0);
          target.enabled = payload.enabled ?? target.enabled ?? true;
          db.products.forEach((item) => {
            if (item.categoryId === id) {
              item.categoryName = target.name;
            }
          });
          writeDb(db);
          await sleep();
          return deepClone(target) as T;
        }

        if (chunks[2] && method === 'delete') {
          const id = Number(chunks[2]);
          db.categories = db.categories.filter((item) => item.id !== id);
          writeDb(db);
          await sleep();
          return deepClone({ ok: true }) as T;
        }
      }

      if (chunks[1] === 'products') {
        if (method === 'get' && adminRoute === '/products') {
          const query = (params || {}) as Record<string, unknown>;
          const keyword = String(query.keyword || '').trim().toLowerCase();
          const categoryId = toNumber(query.categoryId, 0);
          const status = String(query.status || '').trim();

          let rows = db.products.slice();
          if (keyword) {
            rows = rows.filter((item) => `${item.name} ${item.subtitle || ''}`.toLowerCase().includes(keyword));
          }
          if (categoryId) {
            rows = rows.filter((item) => item.categoryId === categoryId);
          }
          if (status) {
            rows = rows.filter((item) => (item.status || 'ON_SALE') === status);
          }

          const paged = paginate(rows.map((item) => toAdminProductRow(db, item)), query);
          await sleep();
          return deepClone(paged) as T;
        }

        if (chunks[2] && method === 'get') {
          const id = Number(chunks[2]);
          const product = db.products.find((item) => item.id === id);
          if (!product) {
            return fail('商品不存在');
          }
          await sleep();
          return deepClone(toAdminProductRow(db, product)) as T;
        }

        if (method === 'post' && adminRoute === '/products') {
          const payload = data as Record<string, unknown>;
          const category = db.categories.find((item) => item.id === Number(payload.categoryId));
          if (!category) {
            return fail('分类不存在');
          }
          const product = {
            id: db.products.length ? Math.max(...db.products.map((item) => item.id)) + 1 : 1,
            categoryId: category.id,
            categoryName: category.name,
            name: String(payload.name || '').trim(),
            subtitle: String(payload.subtitle || '').trim(),
            price: Number(payload.price || 0),
            stock: toNumber(payload.stock, 0),
            sales: 0,
            mainImage: String(payload.mainImage || '').trim(),
            detail: String(payload.detail || '').trim(),
            images: [String(payload.mainImage || '').trim()].filter(Boolean),
            status: String(payload.status || 'ON_SALE'),
          } as ProductDetail & { status?: string };
          db.products.unshift(product);
          writeDb(db);
          await sleep();
          return deepClone(toAdminProductRow(db, product)) as T;
        }

        if (chunks[2] && method === 'put') {
          const id = Number(chunks[2]);
          const payload = data as Record<string, unknown>;
          const product = db.products.find((item) => item.id === id);
          if (!product) {
            return fail('商品不存在');
          }
          const category = db.categories.find((item) => item.id === Number(payload.categoryId));
          if (!category) {
            return fail('分类不存在');
          }
          product.categoryId = category.id;
          product.categoryName = category.name;
          product.name = String(payload.name || product.name).trim();
          product.subtitle = String(payload.subtitle || '').trim();
          product.price = Number(payload.price || product.price);
          product.stock = toNumber(payload.stock, product.stock);
          product.mainImage = String(payload.mainImage || '').trim();
          product.detail = String(payload.detail || '').trim();
          product.images = [product.mainImage].filter(Boolean);
          product.status = String(payload.status || product.status || 'ON_SALE');
          writeDb(db);
          await sleep();
          return deepClone(toAdminProductRow(db, product)) as T;
        }

        if (chunks[2] && method === 'delete') {
          const id = Number(chunks[2]);
          db.products = db.products.filter((item) => item.id !== id);
          writeDb(db);
          await sleep();
          return deepClone({ ok: true }) as T;
        }
      }

      if (chunks[1] === 'orders') {
        const allOrders = getAllOrders(db);

        if (method === 'get' && adminRoute === '/orders') {
          const query = (params || {}) as Record<string, unknown>;
          const orderNo = String(query.orderNo || '').trim().toLowerCase();
          const status = String(query.status || '').trim();

          let rows = allOrders;
          if (orderNo) {
            rows = rows.filter((item) => item.orderNo.toLowerCase().includes(orderNo));
          }
          if (status) {
            rows = rows.filter((item) => item.status === status);
          }

          const paged = paginate(rows, query);
          await sleep();
          return deepClone(paged) as T;
        }

        if (chunks[2] && method === 'get') {
          const id = Number(chunks[2]);
          const order = allOrders.find((item) => item.id === id);
          if (!order) {
            return fail('订单不存在');
          }
          await sleep();
          return deepClone({ order, items: order.items }) as T;
        }

        if (chunks[2] && chunks[3] === 'status' && method === 'put') {
          const id = Number(chunks[2]);
          const payload = data as { status?: string };
          const order = allOrders.find((item) => item.id === id);
          if (!order) {
            return fail('订单不存在');
          }
          order.status = String(payload.status || order.status) as OrderData['status'];
          const now = new Date().toISOString();
          if (order.status === 'PAID' && !order.paidAt) {
            order.paidAt = now;
          }
          if (order.status === 'SHIPPED') {
            order.shippedAt = now;
          }
          if (order.status === 'COMPLETED') {
            order.completedAt = now;
          }
          writeDb(db);
          await sleep();
          return deepClone({ ok: true }) as T;
        }
      }
    }

    if (method === 'get' && route === '/categories') {
      await sleep();
      return deepClone(db.categories.filter((item) => item.enabled !== false).map(({ id, name }) => ({ id, name }))) as T;
    }

    if (method === 'get' && route === '/products') {
      const result = listProducts(db, (params || {}) as Record<string, unknown>);
      await sleep();
      return deepClone(result) as T;
    }

    if (method === 'get' && chunks[0] === 'products' && chunks.length === 2) {
      const productId = Number(chunks[1]);
      const product = getProductOrThrow(db, productId);
      await sleep();
      return deepClone(product) as T;
    }

    if (chunks[0] === 'cart') {
      const user = requireLogin(db);
      const cart = getUserCart(db, user.id);

      if (method === 'get' && route === '/cart') {
        const result = toCartData(cart);
        await sleep();
        return deepClone(result) as T;
      }

      if (method === 'post' && route === '/cart/items') {
        const payload = data as { productId?: number; quantity?: number };
        const product = getProductOrThrow(db, Number(payload.productId));
        const quantity = Math.max(1, toNumber(payload.quantity, 1));
        const existing = cart.find((item) => item.productId === product.id);
        if (existing) {
          existing.quantity = Math.min(product.stock, existing.quantity + quantity);
          existing.subtotal = Number((existing.price * existing.quantity).toFixed(2));
        } else {
          const finalQty = Math.min(product.stock, quantity);
          cart.push({
            id: db.seq.cartItemId++,
            productId: product.id,
            productName: product.name,
            productImage: product.mainImage,
            price: Number(product.price),
            quantity: finalQty,
            stock: product.stock,
            subtotal: Number((Number(product.price) * finalQty).toFixed(2)),
          });
        }
        writeDb(db);
        await sleep();
        return deepClone({ ok: true }) as T;
      }

      if (method === 'put' && chunks[1] === 'items' && chunks[2]) {
        const itemId = Number(chunks[2]);
        const item = cart.find((row) => row.id === itemId);
        if (!item) {
          return fail('购物车商品不存在');
        }
        const payload = data as { quantity?: number };
        item.quantity = Math.max(1, Math.min(item.stock, toNumber(payload.quantity, 1)));
        item.subtotal = Number((item.price * item.quantity).toFixed(2));
        writeDb(db);
        await sleep();
        return deepClone({ ok: true }) as T;
      }

      if (method === 'delete' && chunks[1] === 'items' && chunks[2]) {
        const itemId = Number(chunks[2]);
        db.carts[keyByUser(user.id)] = cart.filter((row) => row.id !== itemId);
        writeDb(db);
        await sleep();
        return deepClone({ ok: true }) as T;
      }
    }

    if (chunks[0] === 'addresses') {
      const user = requireLogin(db);
      const addresses = getUserAddressList(db, user.id);

      if (method === 'get' && route === '/addresses') {
        const sorted = addresses.slice().sort((a, b) => Number(b.isDefault) - Number(a.isDefault));
        await sleep();
        return deepClone(sorted) as T;
      }

      if (method === 'post' && route === '/addresses') {
        const payload = data as Omit<Address, 'id'>;
        const row: Address = {
          id: db.seq.addressId++,
          receiverName: payload.receiverName,
          receiverPhone: payload.receiverPhone,
          detail: payload.detail,
          isDefault: Boolean(payload.isDefault) || addresses.length === 0,
        };
        if (row.isDefault) {
          addresses.forEach((item) => {
            item.isDefault = false;
          });
        }
        addresses.push(row);
        writeDb(db);
        await sleep();
        return deepClone(row) as T;
      }

      if (method === 'put' && chunks.length === 2) {
        const id = Number(chunks[1]);
        const payload = data as Omit<Address, 'id'>;
        const target = addresses.find((item) => item.id === id);
        if (!target) {
          return fail('地址不存在');
        }
        target.receiverName = payload.receiverName;
        target.receiverPhone = payload.receiverPhone;
        target.detail = payload.detail;
        target.isDefault = Boolean(payload.isDefault);
        if (target.isDefault) {
          addresses.forEach((item) => {
            if (item.id !== target.id) {
              item.isDefault = false;
            }
          });
        } else if (!addresses.some((item) => item.isDefault)) {
          addresses[0].isDefault = true;
        }
        writeDb(db);
        await sleep();
        return deepClone(target) as T;
      }

      if (method === 'delete' && chunks.length === 2) {
        const id = Number(chunks[1]);
        const remain = addresses.filter((item) => item.id !== id);
        db.addresses[keyByUser(user.id)] = remain;
        if (remain.length > 0 && !remain.some((item) => item.isDefault)) {
          remain[0].isDefault = true;
        }
        writeDb(db);
        await sleep();
        return deepClone({ ok: true }) as T;
      }
    }

    if (chunks[0] === 'favorites') {
      const user = requireLogin(db);
      const favorites = getUserFavorites(db, user.id);

      if (method === 'get' && route === '/favorites') {
        await sleep();
        return deepClone(favorites) as T;
      }

      if (method === 'post' && chunks.length === 2) {
        const productId = Number(chunks[1]);
        const exists = favorites.some((item) => item.productId === productId);
        if (!exists) {
          const product = getProductOrThrow(db, productId);
          favorites.unshift({
            id: db.seq.favoriteId++,
            productId: product.id,
            productName: product.name,
            productImage: product.mainImage,
            price: Number(product.price),
          });
          writeDb(db);
        }
        await sleep();
        return deepClone({ ok: true }) as T;
      }

      if (method === 'delete' && chunks.length === 2) {
        const productId = Number(chunks[1]);
        db.favorites[keyByUser(user.id)] = favorites.filter((item) => item.productId !== productId);
        writeDb(db);
        await sleep();
        return deepClone({ ok: true }) as T;
      }
    }

    if (chunks[0] === 'orders') {
      const user = requireLogin(db);
      const orders = getUserOrders(db, user.id);

      if (method === 'post' && route === '/orders') {
        const payload = data as { addressId: number; cartItemIds: number[] };
        const order = createOrder(db, user.id, payload);
        writeDb(db);
        await sleep();
        return deepClone(order) as T;
      }

      if (method === 'get' && route === '/orders') {
        await sleep();
        return deepClone(orders) as T;
      }

      if (method === 'get' && chunks.length === 2) {
        const id = Number(chunks[1]);
        const order = orders.find((item) => item.id === id);
        if (!order) {
          return fail('订单不存在');
        }
        await sleep();
        return deepClone(order) as T;
      }

      if (method === 'post' && chunks.length === 3 && chunks[2] === 'pay') {
        const id = Number(chunks[1]);
        const order = orders.find((item) => item.id === id);
        if (!order) {
          return fail('订单不存在');
        }
        if (order.status === 'UNPAID') {
          order.status = 'PAID';
          order.paidAt = new Date().toISOString();
          writeDb(db);
        }
        await sleep();
        return deepClone(order) as T;
      }
    }

    return fail(`未实现的模拟接口: ${method.toUpperCase()} ${route}`);
  } catch (error) {
    if (error instanceof Error) {
      return fail(error.message);
    }
    return fail('请求失败');
  }
}
