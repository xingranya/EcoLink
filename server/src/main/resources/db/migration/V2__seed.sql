INSERT INTO categories(name, sort, enabled, created_at, updated_at) VALUES
('新鲜瓜果', 1, 1, NOW(), NOW()),
('时令蔬菜', 2, 1, NOW(), NOW()),
('肉禽蛋奶', 3, 1, NOW(), NOW()),
('地方特产', 4, 1, NOW(), NOW());

INSERT INTO products(category_id, name, subtitle, price, stock, sales, main_image, detail, status, created_at, updated_at) VALUES
(1, '高山阳光青提', '脆甜多汁 · 产地直供 · 0添加', 29.90, 500, 120, 'https://images.unsplash.com/photo-1537640538966-79f369143f8f', '高山生态种植，脆甜多汁，适合鲜食。', 'ON_SALE', NOW(), NOW()),
(1, '农家五彩小番茄', '有机培育 · 果肉饱满 · 现采现发', 15.80, 800, 89, 'https://images.unsplash.com/photo-1592924357228-91a4daadcfea', '有机种植，果肉饱满，酸甜适中。', 'ON_SALE', NOW(), NOW()),
(3, '五谷散养土鸡蛋', '林下散养 · 蛋黄橙红 · 营养丰富', 38.00, 300, 230, 'https://images.unsplash.com/photo-1506976785307-8732e854ad03', '散养鸡蛋，蛋香浓郁。', 'ON_SALE', NOW(), NOW()),
(4, '正宗陈年金华火腿', '传统工艺 · 醇香浓郁', 268.00, 120, 15, 'https://images.unsplash.com/photo-1608039829572-78524f79c4c7', '精选原料，传统工艺熟成。', 'ON_SALE', NOW(), NOW()),
(1, '新疆吐鲁番无核白葡萄', '皮薄肉厚 · 甜如蜜', 45.00, 450, 72, 'https://images.unsplash.com/photo-1519996529931-28324d5a630e', '产地直采，新鲜冷链运输。', 'ON_SALE', NOW(), NOW()),
(1, '云南建水红提葡萄', '果粒饱满 · 酸甜可口', 39.90, 260, 35, 'https://images.unsplash.com/photo-1567306226416-28f0efdc88ce', '高原气候孕育，风味独特。', 'ON_SALE', NOW(), NOW());

INSERT INTO product_images(product_id, image_url, sort, created_at, updated_at) VALUES
(1, 'https://images.unsplash.com/photo-1537640538966-79f369143f8f', 1, NOW(), NOW()),
(1, 'https://images.unsplash.com/photo-1488459716781-31db52582fe9', 2, NOW(), NOW()),
(2, 'https://images.unsplash.com/photo-1592924357228-91a4daadcfea', 1, NOW(), NOW()),
(3, 'https://images.unsplash.com/photo-1506976785307-8732e854ad03', 1, NOW(), NOW()),
(4, 'https://images.unsplash.com/photo-1608039829572-78524f79c4c7', 1, NOW(), NOW()),
(5, 'https://images.unsplash.com/photo-1519996529931-28324d5a630e', 1, NOW(), NOW()),
(6, 'https://images.unsplash.com/photo-1567306226416-28f0efdc88ce', 1, NOW(), NOW());

INSERT INTO users(username, password_hash, nickname, phone, status, created_at, updated_at) VALUES
('demo', '{noop}123456', '生态达人_886', '13800000000', 'ACTIVE', NOW(), NOW());

INSERT INTO addresses(user_id, receiver_name, receiver_phone, detail, is_default, created_at, updated_at) VALUES
(1, '张三', '13800000000', '北京市朝阳区绿色农业科技大厦 12F', 1, NOW(), NOW());

INSERT INTO cart_items(user_id, product_id, quantity, created_at, updated_at) VALUES
(1, 1, 1, NOW(), NOW()),
(1, 3, 2, NOW(), NOW());
