-- =============================================================
-- ELECTROSHOP – FAKE DATA (SQL Server) – Fixed Version
-- Dùng WHERE NOT EXISTS để tránh lỗi duplicate khi chạy lại
-- =============================================================
-- WIPE EXISTING DATA (CLEAN SLATE - SAFE REVERSE ORDER)
-- =============================================================
PRINT 'WIPING OLD DATA...';
-- Level 3/4 dependencies (Leaf nodes)
DELETE FROM ORDER_CUSTOMIZATION;
DELETE FROM BULK_PRICE_TIER;
DELETE FROM BULK_ORDER_DETAILS;
DELETE FROM WISHLIST_ITEM;
DELETE FROM CART_ITEM;
DELETE FROM ORDER_DETAIL;

-- Level 2 dependencies
DELETE FROM [ORDER]; -- Must delete before USER_VOUCHER
DELETE FROM USER_VOUCHER; -- Must delete before VOUCHER
DELETE FROM BULK_ORDER;
DELETE FROM WISHLIST;
DELETE FROM SHOPPING_CART;
DELETE FROM REVIEW;

-- Level 1 dependencies
DELETE FROM BRANCH_PRODUCT_STOCK;
DELETE FROM PRODUCT_ATTRIBUTE;
DELETE FROM MEDIA;
DELETE FROM WARRANTY;
DELETE FROM USERS; -- Must delete before COMPANY and [ROLE]
DELETE FROM PRODUCT; -- Must delete after ORDER_DETAIL, etc.

-- Level 0 dependencies (Root nodes)
DELETE FROM VOUCHER;
DELETE FROM ATTRIBUTE;
DELETE FROM STORE_BRANCH;
DELETE FROM SUPPLIER;
DELETE FROM BRAND;
DELETE FROM CATEGORY;
DELETE FROM COMPANY;
DELETE FROM [ROLE];
PRINT 'DATA WIPED SUCCESSFULLY. SEEDING NEW DATA...';
-- =============================================================

-- =============================================================
-- LEVEL 0
-- =============================================================

-- 1. ROLE (unique: role_name)
INSERT INTO [ROLE] (role_name, description)
SELECT v.role_name, v.description FROM (VALUES
    ('ADMIN',    N'Quản trị viên hệ thống'),
    ('CUSTOMER', N'Khách hàng mua lẻ'),
    ('COMPANY',  N'Khách hàng doanh nghiệp')
) AS v(role_name, description)
WHERE NOT EXISTS (SELECT 1 FROM [ROLE] r WHERE r.role_name = v.role_name);

-- 2. CATEGORY (unique: category_name)
INSERT INTO CATEGORY (category_name, description)
SELECT v.n, v.d FROM (VALUES
    (N'Điện thoại',           N'Điện thoại thông minh các hãng'),
    (N'Laptop',               N'Máy tính xách tay'),
    (N'Máy tính bảng',        N'Tablet đa dạng kích thước'),
    (N'Tai nghe',             N'Tai nghe có dây và không dây'),
    (N'Đồng hồ thông minh',  N'Smartwatch theo dõi sức khỏe')
) AS v(n, d)
WHERE NOT EXISTS (SELECT 1 FROM CATEGORY c WHERE c.category_name = v.n);

-- 3. BRAND (unique: brand_name)
INSERT INTO BRAND (brand_name, country, description)
SELECT v.n, v.c, v.d FROM (VALUES
    ('Apple',   'USA',   N'Thương hiệu công nghệ số 1 thế giới'),
    ('Samsung', 'Korea', N'Tập đoàn điện tử Hàn Quốc'),
    ('Xiaomi',  'China', N'Thương hiệu công nghệ giá rẻ Trung Quốc'),
    ('Sony',    'Japan', N'Tập đoàn điện tử Nhật Bản'),
    ('Dell',    'USA',   N'Nhà sản xuất máy tính Mỹ')
) AS v(n, c, d)
WHERE NOT EXISTS (SELECT 1 FROM BRAND b WHERE b.brand_name = v.n);

-- 4. SUPPLIER
INSERT INTO SUPPLIER (supplier_name, contact_person, email, phone_number, address)
SELECT v.n, v.cp, v.e, v.ph, v.a FROM (VALUES
    (N'Công ty TNHH TechViet',    N'Nguyễn Văn A', 'supply@techviet.vn',    '0901000001', N'12 Lê Lợi, Q.1, TP.HCM'),
    (N'Công ty CP DigiSource',    N'Trần Thị B',   'contact@digisource.vn', '0901000002', N'45 Đinh Tiên Hoàng, Hà Nội'),
    (N'Công ty TNHH SmartImport', N'Lê Văn C',     'info@smartimport.vn',   '0901000003', N'78 Nguyễn Huệ, Đà Nẵng')
) AS v(n, cp, e, ph, a)
WHERE NOT EXISTS (SELECT 1 FROM SUPPLIER s WHERE s.email = v.e);

-- 4.5 COMPANY
-- 4.5 COMPANY (đầy đủ các trường)
INSERT INTO COMPANY (company_name, tax_code, email, phone, address, representative_name, representative_position, website, founding_date, business_type, employee_count, industry, status, approved_at)
SELECT v.n, v.tc, v.e, v.p, v.a, v.rn, v.rp, v.w, v.fd, v.bt, v.ec, v.ind, v.st, v.aa FROM (VALUES
    (N'Công ty TNHH Phần mềm X', '0312345678', 'contact@xsoftware.vn', '02811112222', N'Tòa nhà X, Cầu Giấy, Hà Nội', N'Nguyễn Văn X', N'Giám đốc', 'https://xsoftware.vn', '2015-05-10', N'TNHH', 150, N'Công nghệ thông tin', 'APPROVED', '2025-01-01 08:00:00'),
    (N'Công ty CP Đầu tư Alpha', '0109876543', 'info@alphainvest.com', '02433334444', N'Tầng 5, Tháp Y, Q.1, TP.HCM', N'Trần Alpha', N'CEO', 'https://alphainvest.com', '2010-09-20', N'Cổ phần', 500, N'Đầu tư tài chính', 'APPROVED', '2025-01-01 08:00:00'),
    (N'Tập đoàn Công nghệ Hưng Thịnh', '0310000001', 'sales@hungthinhcorp.com', '0909999999', N'Đà Nẵng', N'Lê Hưng Thịnh', N'Chủ tịch', 'https://hungthinhcorp.com', '2008-11-15', N'Tập đoàn', 2000, N'Đa ngành', 'APPROVED', '2025-01-01 08:00:00')
) AS v(n, tc, e, p, a, rn, rp, w, fd, bt, ec, ind, st, aa)
WHERE NOT EXISTS (SELECT 1 FROM COMPANY c WHERE c.tax_code = v.tc);

-- 5. STORE_BRANCH
INSERT INTO STORE_BRANCH (branch_name, location, manager_name, contact_number)
SELECT v.n, v.l, v.m, v.c FROM (VALUES
    (N'Chi nhánh Hà Nội',  N'25 Tràng Tiền, Hoàn Kiếm, Hà Nội',  N'Phạm Minh Đức',  '0243000001'),
    (N'Chi nhánh TP.HCM',  N'100 Lê Lợi, Q.1, TP.HCM',           N'Nguyễn Thị Mai', '0283000002'),
    (N'Chi nhánh Đà Nẵng', N'321 Nguyễn Văn Linh, Đà Nẵng',      N'Trần Thanh Hải', '0236300003'),
    (N'Chi nhánh Cần Thơ', N'56 Hoà Bình, Ninh Kiều, Cần Thơ',   N'Lê Hoàng Anh',   '0292000004')
) AS v(n, l, m, c)
WHERE NOT EXISTS (SELECT 1 FROM STORE_BRANCH b WHERE b.branch_name = v.n);

-- 6. ATTRIBUTE (unique: attribute_name)
INSERT INTO ATTRIBUTE (attribute_name)
SELECT v.n FROM (VALUES
    (N'RAM'), (N'Dung lượng ROM'), (N'Màu sắc'),
    (N'Chip xử lý'), (N'Kích thước màn hình'), (N'Pin'), (N'Hệ điều hành')
) AS v(n)
WHERE NOT EXISTS (SELECT 1 FROM ATTRIBUTE a WHERE a.attribute_name = v.n);

-- 7. VOUCHER (unique: voucher_code)
-- 7. VOUCHER (unique: voucher_code) - đầy đủ các trường
INSERT INTO VOUCHER (voucher_code, description, discount_value, discount_type, min_order_value, max_discount, used_count, valid_from, valid_to, usage_limit, is_active)
SELECT v.code, v.descr, v.dval, v.dtype, v.minv, v.maxd, v.ucnt, v.vfrom, v.vto, v.ulimit, v.act FROM (VALUES
    ('SALE10',      N'Giảm 10% toàn đơn hàng',        10.00,     'PERCENT',  0,      500000, 0, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1000, 1),
    ('FLAT50K',     N'Giảm 50k cho đơn từ 500k',       50000.00,  'FIXED',    500000, 50000,  0, '2026-01-01 00:00:00', '2026-06-30 23:59:59', 500,  1),
    ('NEWUSER20',   N'Giảm 20% cho khách hàng mới',   20.00,     'PERCENT',  0,      100000, 0, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 200,  1),
    ('SUMMER15',    N'Khuyến mại hè 2026 - giảm 15%', 15.00,     'PERCENT',  1000000,200000, 0, '2026-06-01 00:00:00', '2026-08-31 23:59:59', 300,  1),
    ('VIPFLAT100K', N'VIP giảm 100k',                  100000.00, 'FIXED',    2000000,100000, 0, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 100,  1)
) AS v(code, descr, dval, dtype, minv, maxd, ucnt, vfrom, vto, ulimit, act)
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER vc WHERE vc.voucher_code = v.code);

-- =============================================================
-- LEVEL 1
-- =============================================================

-- 8. USERS (unique: email) – password = BCrypt("Password@123")
INSERT INTO USERS (full_name, email, password, phone_number, address, registration_date, status, reward_point, role_id)
SELECT v.fn, v.em, v.pw, v.ph, v.addr, v.rdate, v.st, v.rp,
       (SELECT role_id FROM [ROLE] WHERE role_name = v.rname)
FROM (VALUES
    (N'Nguyễn Văn Bình',     'binh.nguyen@gmail.com',  '$2a$12$Sl/c9Nu.GHAm2uJf1LdNHuiTlATVtxbnAjRBbJQY3XNeefJCELNJ2', '0901111111', N'23 Lê Duẩn, Hà Nội',             '2025-03-15 09:00:00', 'ACTIVE',   150, 'CUSTOMER'),
    (N'Trần Thị Hương',      'huong.tran@gmail.com',   '$2a$12$Sl/c9Nu.GHAm2uJf1LdNHuiTlATVtxbnAjRBbJQY3XNeefJCELNJ2', '0902222222', N'55 Trần Phú, Đà Nẵng',            '2025-04-20 10:30:00', 'ACTIVE',   320, 'CUSTOMER'),
    (N'Lê Quốc Hùng',        'hung.le@company.vn',     '$2a$12$Sl/c9Nu.GHAm2uJf1LdNHuiTlATVtxbnAjRBbJQY3XNeefJCELNJ2', '0903333333', N'88 Nguyễn Trãi, TP.HCM',          '2025-05-10 08:00:00', 'ACTIVE',   500, 'COMPANY'),
    (N'Phạm Thị Lan',        'lan.pham@gmail.com',     '$2a$12$Sl/c9Nu.GHAm2uJf1LdNHuiTlATVtxbnAjRBbJQY3XNeefJCELNJ2', '0904444444', N'12 Hùng Vương, Cần Thơ',          '2025-06-01 14:00:00', 'INACTIVE', 0,   'CUSTOMER'),
    (N'Đặng Minh Tuấn',      'tuan.dang@company.vn',   '$2a$12$Sl/c9Nu.GHAm2uJf1LdNHuiTlATVtxbnAjRBbJQY3XNeefJCELNJ2', '0905555555', N'99 Bạch Đằng, Hải Phòng',         '2025-07-15 11:00:00', 'ACTIVE',   800, 'COMPANY'),
    (N'Hoàng Thị Kim Chi',   'kimchi.hoang@gmail.com', '$2a$12$Sl/c9Nu.GHAm2uJf1LdNHuiTlATVtxbnAjRBbJQY3XNeefJCELNJ2', '0906666666', N'34 Pasteur, Q.3, TP.HCM',         '2025-08-20 09:30:00', 'ACTIVE',   200, 'CUSTOMER'),
    (N'Vũ Thanh Nam',        'nam.vu@gmail.com',       '$2a$12$Sl/c9Nu.GHAm2uJf1LdNHuiTlATVtxbnAjRBbJQY3XNeefJCELNJ2', '0907777777', N'67 Đinh Bộ Lĩnh, Bình Thạnh',    '2025-09-05 16:00:00', 'ACTIVE',   50,  'CUSTOMER')
) AS v(fn, em, pw, ph, addr, rdate, st, rp, rname)
WHERE NOT EXISTS (SELECT 1 FROM USERS u WHERE u.email = v.em);

-- 9. PRODUCT
-- ProductStatus hợp lệ: AVAILABLE, OUT_OF_STOCK, DISCONTINUED, COMING_SOON
INSERT INTO PRODUCT (product_name, description, price, category_id, brand_id, quantity, status, created_date, supplier_id)
SELECT v.pn, v.descr, v.price, v.cat, v.brd, v.qty, v.st, v.cdate, v.sup FROM (VALUES
    (N'iPhone 15 Pro 256GB',
     N'iPhone thế hệ 15 Pro, chip A17, camera 48MP, màn hình 6.1 inch',
     29990000,
     (SELECT category_id FROM CATEGORY WHERE category_name = N'Điện thoại'),
     (SELECT brand_id   FROM BRAND    WHERE brand_name = 'Apple'),
     50, 'AVAILABLE', '2025-01-10 08:00:00',
     (SELECT supplier_id FROM SUPPLIER WHERE email = 'supply@techviet.vn')),

    (N'Samsung Galaxy S24 Ultra 512GB',
     N'Samsung flagship 2024, camera 200MP, S-Pen tích hợp',
     27990000,
     (SELECT category_id FROM CATEGORY WHERE category_name = N'Điện thoại'),
     (SELECT brand_id   FROM BRAND    WHERE brand_name = 'Samsung'),
     80, 'AVAILABLE', '2025-01-15 09:00:00',
     (SELECT supplier_id FROM SUPPLIER WHERE email = 'contact@digisource.vn')),

    (N'Xiaomi 14 256GB',
     N'Leica camera, Snapdragon 8 Gen 3, sạc 90W',
     16990000,
     (SELECT category_id FROM CATEGORY WHERE category_name = N'Điện thoại'),
     (SELECT brand_id   FROM BRAND    WHERE brand_name = 'Xiaomi'),
     120, 'AVAILABLE', '2025-02-01 10:00:00',
     (SELECT supplier_id FROM SUPPLIER WHERE email = 'info@smartimport.vn')),

    (N'MacBook Pro 14 inch M3 Pro',
     N'Chip M3 Pro, RAM 18GB, SSD 512GB, màn hình Liquid Retina XDR',
     52990000,
     (SELECT category_id FROM CATEGORY WHERE category_name = N'Laptop'),
     (SELECT brand_id   FROM BRAND    WHERE brand_name = 'Apple'),
     30, 'AVAILABLE', '2025-01-20 08:00:00',
     (SELECT supplier_id FROM SUPPLIER WHERE email = 'supply@techviet.vn')),

    (N'Dell XPS 15 9530',
     N'Intel Core i9 13th, RAM 32GB, SSD 1TB, màn 15.6 OLED',
     43990000,
     (SELECT category_id FROM CATEGORY WHERE category_name = N'Laptop'),
     (SELECT brand_id   FROM BRAND    WHERE brand_name = 'Dell'),
     25, 'AVAILABLE', '2025-02-10 09:00:00',
     (SELECT supplier_id FROM SUPPLIER WHERE email = 'contact@digisource.vn')),

    (N'Samsung Galaxy Book4 Pro 360',
     N'Laptop 2-in-1 AMOLED, Core Ultra 7, RAM 16GB, SSD 512GB',
     32990000,
     (SELECT category_id FROM CATEGORY WHERE category_name = N'Laptop'),
     (SELECT brand_id   FROM BRAND    WHERE brand_name = 'Samsung'),
     40, 'AVAILABLE', '2025-03-01 10:00:00',
     (SELECT supplier_id FROM SUPPLIER WHERE email = 'info@smartimport.vn')),

    (N'Sony WH-1000XM5',
     N'Tai nghe chống ồn hàng đầu, Bluetooth 5.2, pin 30h',
     8990000,
     (SELECT category_id FROM CATEGORY WHERE category_name = N'Tai nghe'),
     (SELECT brand_id   FROM BRAND    WHERE brand_name = 'Sony'),
     200, 'AVAILABLE', '2025-01-05 08:00:00',
     (SELECT supplier_id FROM SUPPLIER WHERE email = 'supply@techviet.vn')),

    (N'Apple AirPods Pro 2',
     N'Chip H2, chống ồn cải tiến, sạc USB-C',
     6290000,
     (SELECT category_id FROM CATEGORY WHERE category_name = N'Tai nghe'),
     (SELECT brand_id   FROM BRAND    WHERE brand_name = 'Apple'),
     150, 'AVAILABLE', '2025-02-20 09:00:00',
     (SELECT supplier_id FROM SUPPLIER WHERE email = 'contact@digisource.vn')),

    (N'Apple Watch Series 9 45mm',
     N'Chip S9, màn hình always-on, đo oxy máu',
     12990000,
     (SELECT category_id FROM CATEGORY WHERE category_name = N'Đồng hồ thông minh'),
     (SELECT brand_id   FROM BRAND    WHERE brand_name = 'Apple'),
     100, 'AVAILABLE', '2025-01-25 10:00:00',
     (SELECT supplier_id FROM SUPPLIER WHERE email = 'info@smartimport.vn')),

    (N'Samsung Galaxy Watch 7 44mm',
     N'Exynos W1000, đo ECG, pin 40h',
     8490000,
     (SELECT category_id FROM CATEGORY WHERE category_name = N'Đồng hồ thông minh'),
     (SELECT brand_id   FROM BRAND    WHERE brand_name = 'Samsung'),
     90, 'AVAILABLE', '2025-03-15 11:00:00',
     (SELECT supplier_id FROM SUPPLIER WHERE email = 'supply@techviet.vn')),

    (N'iPhone 13 128GB',
     N'iPhone 13 cũ, chip A15 Bionic, camera 12MP dual',
     14990000,
     (SELECT category_id FROM CATEGORY WHERE category_name = N'Điện thoại'),
     (SELECT brand_id   FROM BRAND    WHERE brand_name = 'Apple'),
     5, 'DISCONTINUED', '2024-06-01 08:00:00',
     (SELECT supplier_id FROM SUPPLIER WHERE email = 'supply@techviet.vn'))
) AS v(pn, descr, price, cat, brd, qty, st, cdate, sup)
WHERE NOT EXISTS (SELECT 1 FROM PRODUCT p WHERE p.product_name = v.pn);

-- =============================================================
-- LEVEL 2
-- =============================================================

-- 10. [ORDER] – SQL Server: dùng [ORDER] không phải "ORDER"
-- 10. USER_VOUCHER (VoucherStatus hợp lệ: AVAILABLE, USED, EXPIRED)
-- Chèn trước [ORDER] vì Order phụ thuộc UserVoucher
INSERT INTO USER_VOUCHER (user_id, voucher_id, status, assigned_at, used_at)
SELECT v.uid, v.vid, v.st, v.aa, v.ua FROM (VALUES
    ((SELECT user_id FROM USERS WHERE email = 'binh.nguyen@gmail.com'),
     (SELECT voucher_id FROM VOUCHER WHERE voucher_code = 'SALE10'),
     'USED', '2026-01-10 09:00:00', '2026-01-15 10:30:00'),

    ((SELECT user_id FROM USERS WHERE email = 'huong.tran@gmail.com'),
     (SELECT voucher_id FROM VOUCHER WHERE voucher_code = 'NEWUSER20'),
     'AVAILABLE', '2026-01-18 11:00:00', NULL),

    ((SELECT user_id FROM USERS WHERE email = 'kimchi.hoang@gmail.com'),
     (SELECT voucher_id FROM VOUCHER WHERE voucher_code = 'FLAT50K'),
     'USED', '2026-02-08 10:00:00', '2026-02-10 11:30:00'),

    ((SELECT user_id FROM USERS WHERE email = 'nam.vu@gmail.com'),
     (SELECT voucher_id FROM VOUCHER WHERE voucher_code = 'NEWUSER20'),
     'AVAILABLE', '2026-02-18 15:00:00', NULL),

    ((SELECT user_id FROM USERS WHERE email = 'tuan.dang@company.vn'),
     (SELECT voucher_id FROM VOUCHER WHERE voucher_code = 'VIPFLAT100K'),
     'AVAILABLE', '2026-01-01 00:00:00', NULL)
) AS v(uid, vid, st, aa, ua)
WHERE NOT EXISTS (
    SELECT 1 FROM USER_VOUCHER uv 
    WHERE uv.user_id = v.uid AND uv.voucher_id = v.vid
);

-- 11. [ORDER]
INSERT INTO [ORDER] (user_id, order_date, total_amount, discount_amount, final_amount, order_status, shipping_address, payment_method, voucher_id)
SELECT v.uid, v.odate, v.total, v.disc, v.final, v.st, v.addr, v.pm, v.vid FROM (VALUES
    ((SELECT user_id FROM USERS WHERE email = 'binh.nguyen@gmail.com'),
     '2026-01-15 10:30:00', 29990000, 2999000, 26991000, 'DELIVERED', N'23 Lê Duẩn, Hà Nội', 'CREDIT_CARD',
     (SELECT user_voucher_id FROM USER_VOUCHER uv JOIN VOUCHER v ON uv.voucher_id = v.voucher_id WHERE v.voucher_code = 'SALE10' AND uv.user_id = (SELECT user_id FROM USERS WHERE email = 'binh.nguyen@gmail.com'))),

    ((SELECT user_id FROM USERS WHERE email = 'huong.tran@gmail.com'),
     '2026-01-20 14:00:00', 36980000, 0, 36980000, 'DELIVERED', N'55 Trần Phú, Đà Nẵng', 'BANK_TRANSFER', NULL),

    ((SELECT user_id FROM USERS WHERE email = 'hung.le@company.vn'),
     '2026-02-05 09:00:00', 52990000, 0, 52990000, 'PROCESSING', N'88 Nguyễn Trãi, TP.HCM', 'CASH_ON_DELIVERY', NULL),

    ((SELECT user_id FROM USERS WHERE email = 'kimchi.hoang@gmail.com'),
     '2026-02-10 11:30:00', 8990000, 50000, 8940000, 'DELIVERED', N'34 Pasteur, Q.3, TP.HCM', 'CREDIT_CARD',
     (SELECT user_voucher_id FROM USER_VOUCHER uv JOIN VOUCHER v ON uv.voucher_id = v.voucher_id WHERE v.voucher_code = 'FLAT50K' AND uv.user_id = (SELECT user_id FROM USERS WHERE email = 'kimchi.hoang@gmail.com'))),

    ((SELECT user_id FROM USERS WHERE email = 'nam.vu@gmail.com'),
     '2026-02-20 16:00:00', 6290000, 0, 6290000, 'CANCELLED', N'67 Đinh Bộ Lĩnh, Bình Thạnh', 'CREDIT_CARD', NULL),

    ((SELECT user_id FROM USERS WHERE email = 'binh.nguyen@gmail.com'),
     '2026-03-01 08:00:00', 12990000, 0, 12990000, 'PENDING', N'23 Lê Duẩn, Hà Nội', 'BANK_TRANSFER', NULL)
) AS v(uid, odate, total, disc, final, st, addr, pm, vid);

-- 11. SHOPPING_CART
INSERT INTO SHOPPING_CART (user_id, created_date, last_updated)
SELECT v.uid, v.cd, v.lu FROM (VALUES
    ((SELECT user_id FROM USERS WHERE email = 'binh.nguyen@gmail.com'),  '2026-01-10 09:00:00', '2026-03-01 08:00:00'),
    ((SELECT user_id FROM USERS WHERE email = 'huong.tran@gmail.com'),   '2026-01-18 11:00:00', '2026-02-15 14:00:00'),
    ((SELECT user_id FROM USERS WHERE email = 'hung.le@company.vn'),     '2026-02-01 08:00:00', '2026-02-20 09:00:00'),
    ((SELECT user_id FROM USERS WHERE email = 'kimchi.hoang@gmail.com'), '2026-02-08 10:00:00', '2026-02-10 11:00:00'),
    ((SELECT user_id FROM USERS WHERE email = 'nam.vu@gmail.com'),       '2026-02-18 15:00:00', '2026-02-20 16:00:00')
) AS v(uid, cd, lu)
WHERE NOT EXISTS (
    SELECT 1 FROM SHOPPING_CART c
    JOIN USERS u ON c.user_id = u.user_id WHERE u.user_id = v.uid
);

-- 12. WISHLIST
INSERT INTO WISHLIST (user_id, created_date)
SELECT v.uid, v.cd FROM (VALUES
    ((SELECT user_id FROM USERS WHERE email = 'binh.nguyen@gmail.com'),  '2026-01-05 09:00:00'),
    ((SELECT user_id FROM USERS WHERE email = 'huong.tran@gmail.com'),   '2026-01-15 10:00:00'),
    ((SELECT user_id FROM USERS WHERE email = 'kimchi.hoang@gmail.com'), '2026-02-01 08:00:00')
) AS v(uid, cd)
WHERE NOT EXISTS (SELECT 1 FROM WISHLIST w WHERE w.user_id = v.uid);

-- 13. BULK_ORDER
-- BulkOrderStatus hợp lệ: PENDING, APPROVED, REJECTED, PROCESSING, COMPLETED, CANCELLED
INSERT INTO BULK_ORDER (user_id, company_id, created_at, status, total_price)
SELECT v.uid, v.cid, v.cat, v.st, v.tp FROM (VALUES
    ((SELECT user_id FROM USERS WHERE email = 'hung.le@company.vn'),   (SELECT company_id FROM COMPANY WHERE tax_code = '0109876543'), '2026-02-01 09:00:00', 'APPROVED',   158970000),
    ((SELECT user_id FROM USERS WHERE email = 'tuan.dang@company.vn'), (SELECT company_id FROM COMPANY WHERE tax_code = '0310000001'), '2026-02-15 10:00:00', 'PENDING',     83960000),
    ((SELECT user_id FROM USERS WHERE email = 'hung.le@company.vn'),   (SELECT company_id FROM COMPANY WHERE tax_code = '0109876543'), '2026-03-01 08:00:00', 'PROCESSING',  43990000)
) AS v(uid, cid, cat, st, tp);

-- 14. REVIEW
INSERT INTO REVIEW (user_id, product_id, rating, comment, review_date)
SELECT v.uid, v.pid, v.rt, v.cm, v.rd FROM (VALUES
    ((SELECT user_id    FROM USERS   WHERE email        = 'binh.nguyen@gmail.com'),
     (SELECT product_id FROM PRODUCT WHERE product_name = N'iPhone 15 Pro 256GB'),
     5, N'Sản phẩm rất tốt, camera xuất sắc, pin trâu!', '2026-01-20 10:00:00'),

    ((SELECT user_id    FROM USERS   WHERE email        = 'huong.tran@gmail.com'),
     (SELECT product_id FROM PRODUCT WHERE product_name = N'Samsung Galaxy S24 Ultra 512GB'),
     4, N'Máy đẹp, S-Pen tiện lợi, tuy nhiên hơi nặng.', '2026-01-25 11:00:00'),

    ((SELECT user_id    FROM USERS   WHERE email        = 'kimchi.hoang@gmail.com'),
     (SELECT product_id FROM PRODUCT WHERE product_name = N'Sony WH-1000XM5'),
     5, N'Chống ồn cực tốt, âm thanh chất lượng cao!', '2026-02-14 09:00:00'),

    ((SELECT user_id    FROM USERS   WHERE email        = 'nam.vu@gmail.com'),
     (SELECT product_id FROM PRODUCT WHERE product_name = N'Apple AirPods Pro 2'),
     3, N'Âm thanh tốt nhưng giá hơi cao so với đối thủ.', '2026-02-22 14:00:00'),

    ((SELECT user_id    FROM USERS   WHERE email        = 'binh.nguyen@gmail.com'),
     (SELECT product_id FROM PRODUCT WHERE product_name = N'MacBook Pro 14 inch M3 Pro'),
     5, N'Hiệu năng khủng, màn hình đẹp, hoàn hảo cho công việc.', '2026-02-28 16:00:00')
) AS v(uid, pid, rt, cm, rd);

-- 15. WARRANTY
INSERT INTO WARRANTY (product_id, warranty_period_months, warranty_terms, start_date, end_date)
SELECT v.pid, v.months, v.terms, v.sd, v.ed FROM (VALUES
    ((SELECT product_id FROM PRODUCT WHERE product_name = N'iPhone 15 Pro 256GB'),
     12, N'Bảo hành chính hãng 12 tháng, 1 đổi 1 trong 30 ngày.',
     '2025-01-10 00:00:00', '2026-01-10 00:00:00'),

    ((SELECT product_id FROM PRODUCT WHERE product_name = N'Samsung Galaxy S24 Ultra 512GB'),
     24, N'Samsung Care 24 tháng, hỗ trợ sửa chữa tại nhà.',
     '2025-01-15 00:00:00', '2027-01-15 00:00:00'),

    ((SELECT product_id FROM PRODUCT WHERE product_name = N'MacBook Pro 14 inch M3 Pro'),
     12, N'Apple Limited Warranty 12 tháng, có thể nâng cấp AppleCare+.',
     '2025-01-20 00:00:00', '2026-01-20 00:00:00'),

    ((SELECT product_id FROM PRODUCT WHERE product_name = N'Dell XPS 15 9530'),
     24, N'Dell Premium Support 24 tháng, hỗ trợ từ xa 24/7.',
     '2025-02-10 00:00:00', '2027-02-10 00:00:00'),

    ((SELECT product_id FROM PRODUCT WHERE product_name = N'Sony WH-1000XM5'),
     12, N'Bảo hành Sony 12 tháng tại các trung tâm ủy quyền.',
     '2025-01-05 00:00:00', '2026-01-05 00:00:00')
) AS v(pid, months, terms, sd, ed);

-- 16. MEDIA
INSERT INTO MEDIA (product_id, type, url, sort_order)
SELECT v.pid, v.t, v.url, v.so FROM (VALUES
    ((SELECT product_id FROM PRODUCT WHERE product_name = N'iPhone 15 Pro 256GB'),
     'IMAGE', 'https://store.storeimages.cdn-apple.com/iphone15pro-titanium.jpg', 1),
    ((SELECT product_id FROM PRODUCT WHERE product_name = N'iPhone 15 Pro 256GB'),
     'IMAGE', 'https://store.storeimages.cdn-apple.com/iphone15pro-back.jpg', 2),
    ((SELECT product_id FROM PRODUCT WHERE product_name = N'Samsung Galaxy S24 Ultra 512GB'),
     'IMAGE', 'https://image.samsung.com/s24-ultra-black.jpg', 1),
    ((SELECT product_id FROM PRODUCT WHERE product_name = N'MacBook Pro 14 inch M3 Pro'),
     'IMAGE', 'https://store.storeimages.cdn-apple.com/macbook-pro-m3.jpg', 1),
    ((SELECT product_id FROM PRODUCT WHERE product_name = N'Sony WH-1000XM5'),
     'IMAGE', 'https://www.sony.com/images/wh1000xm5-black.jpg', 1),
    ((SELECT product_id FROM PRODUCT WHERE product_name = N'Apple AirPods Pro 2'),
     'IMAGE', 'https://store.storeimages.cdn-apple.com/airpods-pro2.jpg', 1)
) AS v(pid, t, url, so);

-- 17. PRODUCT_ATTRIBUTE
INSERT INTO PRODUCT_ATTRIBUTE (product_id, attribute_id, value)
SELECT v.pid, v.aid, v.val FROM (VALUES
    ((SELECT MIN(product_id) FROM PRODUCT WHERE product_name = N'iPhone 15 Pro 256GB'),
     (SELECT MIN(attribute_id) FROM ATTRIBUTE WHERE attribute_name = N'RAM'), '8GB'),
    ((SELECT MIN(product_id) FROM PRODUCT WHERE product_name = N'iPhone 15 Pro 256GB'),
     (SELECT MIN(attribute_id) FROM ATTRIBUTE WHERE attribute_name = N'Dung lượng ROM'), '256GB'),
    ((SELECT MIN(product_id) FROM PRODUCT WHERE product_name = N'iPhone 15 Pro 256GB'),
     (SELECT MIN(attribute_id) FROM ATTRIBUTE WHERE attribute_name = N'Chip xử lý'), 'Apple A17 Pro'),

    ((SELECT MIN(product_id) FROM PRODUCT WHERE product_name = N'Samsung Galaxy S24 Ultra 512GB'),
     (SELECT MIN(attribute_id) FROM ATTRIBUTE WHERE attribute_name = N'RAM'), '12GB'),
    ((SELECT MIN(product_id) FROM PRODUCT WHERE product_name = N'Samsung Galaxy S24 Ultra 512GB'),
     (SELECT MIN(attribute_id) FROM ATTRIBUTE WHERE attribute_name = N'Chip xử lý'), 'Snapdragon 8 Gen 3'),

    ((SELECT MIN(product_id) FROM PRODUCT WHERE product_name = N'MacBook Pro 14 inch M3 Pro'),
     (SELECT MIN(attribute_id) FROM ATTRIBUTE WHERE attribute_name = N'RAM'), '18GB'),
    ((SELECT MIN(product_id) FROM PRODUCT WHERE product_name = N'MacBook Pro 14 inch M3 Pro'),
     (SELECT MIN(attribute_id) FROM ATTRIBUTE WHERE attribute_name = N'Dung lượng ROM'), '512GB'),
    ((SELECT MIN(product_id) FROM PRODUCT WHERE product_name = N'MacBook Pro 14 inch M3 Pro'),
     (SELECT MIN(attribute_id) FROM ATTRIBUTE WHERE attribute_name = N'Chip xử lý'), 'Apple M3 Pro'),

    ((SELECT MIN(product_id) FROM PRODUCT WHERE product_name = N'Sony WH-1000XM5'),
     (SELECT MIN(attribute_id) FROM ATTRIBUTE WHERE attribute_name = N'Pin'), '30 giờ'),
    ((SELECT MIN(product_id) FROM PRODUCT WHERE product_name = N'Sony WH-1000XM5'),
     (SELECT MIN(attribute_id) FROM ATTRIBUTE WHERE attribute_name = N'Màu sắc'), N'Đen')
) AS v(pid, aid, val);

-- 18. BRANCH_PRODUCT_STOCK
INSERT INTO BRANCH_PRODUCT_STOCK (branch_id, product_id, quantity, last_updated)
SELECT v.bid, v.pid, v.qty, v.lu FROM (VALUES
    ((SELECT MIN(branch_id)  FROM STORE_BRANCH WHERE branch_name  = N'Chi nhánh Hà Nội'),
     (SELECT MIN(product_id) FROM PRODUCT       WHERE product_name = N'iPhone 15 Pro 256GB'),            15, '2026-01-10 08:00:00'),
    ((SELECT MIN(branch_id)  FROM STORE_BRANCH WHERE branch_name  = N'Chi nhánh TP.HCM'),
     (SELECT MIN(product_id) FROM PRODUCT       WHERE product_name = N'iPhone 15 Pro 256GB'),            20, '2026-01-10 08:00:00'),
    ((SELECT MIN(branch_id)  FROM STORE_BRANCH WHERE branch_name  = N'Chi nhánh Hà Nội'),
     (SELECT MIN(product_id) FROM PRODUCT       WHERE product_name = N'Samsung Galaxy S24 Ultra 512GB'), 25, '2026-01-15 09:00:00'),
    ((SELECT MIN(branch_id)  FROM STORE_BRANCH WHERE branch_name  = N'Chi nhánh TP.HCM'),
     (SELECT MIN(product_id) FROM PRODUCT       WHERE product_name = N'MacBook Pro 14 inch M3 Pro'),     10, '2026-01-20 08:00:00'),
    ((SELECT MIN(branch_id)  FROM STORE_BRANCH WHERE branch_name  = N'Chi nhánh Đà Nẵng'),
     (SELECT MIN(product_id) FROM PRODUCT       WHERE product_name = N'Sony WH-1000XM5'),               40, '2026-01-05 08:00:00'),
    ((SELECT MIN(branch_id)  FROM STORE_BRANCH WHERE branch_name  = N'Chi nhánh Cần Thơ'),
     (SELECT MIN(product_id) FROM PRODUCT       WHERE product_name = N'Apple Watch Series 9 45mm'),      30, '2026-01-25 10:00:00'),
    ((SELECT MIN(branch_id)  FROM STORE_BRANCH WHERE branch_name  = N'Chi nhánh TP.HCM'),
     (SELECT MIN(product_id) FROM PRODUCT       WHERE product_name = N'Xiaomi 14 256GB'),               50, '2026-02-01 10:00:00'),
    ((SELECT MIN(branch_id)  FROM STORE_BRANCH WHERE branch_name  = N'Chi nhánh Hà Nội'),
     (SELECT MIN(product_id) FROM PRODUCT       WHERE product_name = N'Dell XPS 15 9530'),               8, '2026-02-10 09:00:00')
) AS v(bid, pid, qty, lu);

-- USER_VOUCHER đã được chèn ở Level 2 phía trên.

-- =============================================================
-- LEVEL 3
-- =============================================================

-- 20. ORDER_DETAIL (dùng subquery lấy order_id qua user + order_date)
INSERT INTO ORDER_DETAIL (order_id, product_id, branch_id, quantity, unit_price, discount_amount, subtotal)
VALUES
(
 (SELECT o.order_id FROM [ORDER] o JOIN USERS u ON o.user_id = u.user_id WHERE u.email = 'binh.nguyen@gmail.com'  AND o.order_date = '2026-01-15 10:30:00'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'iPhone 15 Pro 256GB'),
 (SELECT branch_id  FROM STORE_BRANCH WHERE branch_name = N'Chi nhánh Hà Nội'),
 1, 29990000, 0, 29990000
),
(
 (SELECT o.order_id FROM [ORDER] o JOIN USERS u ON o.user_id = u.user_id WHERE u.email = 'huong.tran@gmail.com'   AND o.order_date = '2026-01-20 14:00:00'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Samsung Galaxy S24 Ultra 512GB'),
 (SELECT branch_id  FROM STORE_BRANCH WHERE branch_name = N'Chi nhánh Đà Nẵng'),
 1, 27990000, 0, 27990000
),
(
 (SELECT o.order_id FROM [ORDER] o JOIN USERS u ON o.user_id = u.user_id WHERE u.email = 'huong.tran@gmail.com'   AND o.order_date = '2026-01-20 14:00:00'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Sony WH-1000XM5'),
 (SELECT branch_id  FROM STORE_BRANCH WHERE branch_name = N'Chi nhánh Đà Nẵng'),
 1, 8990000, 0, 8990000
),
(
 (SELECT o.order_id FROM [ORDER] o JOIN USERS u ON o.user_id = u.user_id WHERE u.email = 'hung.le@company.vn'     AND o.order_date = '2026-02-05 09:00:00'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'MacBook Pro 14 inch M3 Pro'),
 (SELECT branch_id  FROM STORE_BRANCH WHERE branch_name = N'Chi nhánh TP.HCM'),
 1, 52990000, 0, 52990000
),
(
 (SELECT o.order_id FROM [ORDER] o JOIN USERS u ON o.user_id = u.user_id WHERE u.email = 'kimchi.hoang@gmail.com' AND o.order_date = '2026-02-10 11:30:00'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Sony WH-1000XM5'),
 NULL,
 1, 8990000, 0, 8990000
),
(
 (SELECT o.order_id FROM [ORDER] o JOIN USERS u ON o.user_id = u.user_id WHERE u.email = 'nam.vu@gmail.com'        AND o.order_date = '2026-02-20 16:00:00'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Apple AirPods Pro 2'),
 (SELECT branch_id  FROM STORE_BRANCH WHERE branch_name = N'Chi nhánh TP.HCM'),
 1, 6290000, 0, 6290000
),
(
 (SELECT o.order_id FROM [ORDER] o JOIN USERS u ON o.user_id = u.user_id WHERE u.email = 'binh.nguyen@gmail.com'  AND o.order_date = '2026-03-01 08:00:00'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Apple Watch Series 9 45mm'),
 (SELECT branch_id  FROM STORE_BRANCH WHERE branch_name = N'Chi nhánh Hà Nội'),
 1, 12990000, 0, 12990000
);

-- 21. CART_ITEM
INSERT INTO CART_ITEM (cart_id, product_id, quantity, added_date)
VALUES
(
 (SELECT c.cart_id FROM SHOPPING_CART c JOIN USERS u ON c.user_id = u.user_id WHERE u.email = 'binh.nguyen@gmail.com'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Apple Watch Series 9 45mm'),
 1, '2026-03-01 07:30:00'
),
(
 (SELECT c.cart_id FROM SHOPPING_CART c JOIN USERS u ON c.user_id = u.user_id WHERE u.email = 'binh.nguyen@gmail.com'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Apple AirPods Pro 2'),
 2, '2026-03-01 07:35:00'
),
(
 (SELECT c.cart_id FROM SHOPPING_CART c JOIN USERS u ON c.user_id = u.user_id WHERE u.email = 'huong.tran@gmail.com'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Xiaomi 14 256GB'),
 1, '2026-02-15 14:00:00'
),
(
 (SELECT c.cart_id FROM SHOPPING_CART c JOIN USERS u ON c.user_id = u.user_id WHERE u.email = 'hung.le@company.vn'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Dell XPS 15 9530'),
 2, '2026-02-20 09:00:00'
),
(
 (SELECT c.cart_id FROM SHOPPING_CART c JOIN USERS u ON c.user_id = u.user_id WHERE u.email = 'kimchi.hoang@gmail.com'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Samsung Galaxy Watch 7 44mm'),
 1, '2026-02-10 10:00:00'
),
(
 (SELECT c.cart_id FROM SHOPPING_CART c JOIN USERS u ON c.user_id = u.user_id WHERE u.email = 'nam.vu@gmail.com'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Apple AirPods Pro 2'),
 1, '2026-02-20 15:30:00'
);

-- 22. WISHLIST_ITEM
INSERT INTO WISHLIST_ITEM (wishlist_id, product_id, created_date)
VALUES
(
 (SELECT w.wishlist_id FROM WISHLIST w JOIN USERS u ON w.user_id = u.user_id WHERE u.email = 'binh.nguyen@gmail.com'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'MacBook Pro 14 inch M3 Pro'), '2026-01-05 09:00:00'
),
(
 (SELECT w.wishlist_id FROM WISHLIST w JOIN USERS u ON w.user_id = u.user_id WHERE u.email = 'binh.nguyen@gmail.com'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Apple Watch Series 9 45mm'), '2026-01-06 10:00:00'
),
(
 (SELECT w.wishlist_id FROM WISHLIST w JOIN USERS u ON w.user_id = u.user_id WHERE u.email = 'huong.tran@gmail.com'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'iPhone 15 Pro 256GB'), '2026-01-15 10:00:00'
),
(
 (SELECT w.wishlist_id FROM WISHLIST w JOIN USERS u ON w.user_id = u.user_id WHERE u.email = 'huong.tran@gmail.com'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Sony WH-1000XM5'), '2026-01-16 11:00:00'
),
(
 (SELECT w.wishlist_id FROM WISHLIST w JOIN USERS u ON w.user_id = u.user_id WHERE u.email = 'kimchi.hoang@gmail.com'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Samsung Galaxy S24 Ultra 512GB'), '2026-02-01 08:00:00'
);

-- 23. BULK_ORDER_DETAILS
INSERT INTO BULK_ORDER_DETAILS (bulk_order_id, product_id, quantity, unit_price_snapshot, discount_snapshot)
VALUES
(
 (SELECT b.bulk_order_id FROM BULK_ORDER b JOIN USERS u ON b.user_id = u.user_id WHERE u.email = 'hung.le@company.vn'   AND b.created_at = '2026-02-01 09:00:00'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'MacBook Pro 14 inch M3 Pro'),
 3, 52990000, 3000000
),
(
 (SELECT b.bulk_order_id FROM BULK_ORDER b JOIN USERS u ON b.user_id = u.user_id WHERE u.email = 'hung.le@company.vn'   AND b.created_at = '2026-02-01 09:00:00'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Dell XPS 15 9530'),
 1, 43990000, 0
),
(
 (SELECT b.bulk_order_id FROM BULK_ORDER b JOIN USERS u ON b.user_id = u.user_id WHERE u.email = 'tuan.dang@company.vn' AND b.created_at = '2026-02-15 10:00:00'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Samsung Galaxy S24 Ultra 512GB'),
 2, 27990000, 1000000
),
(
 (SELECT b.bulk_order_id FROM BULK_ORDER b JOIN USERS u ON b.user_id = u.user_id WHERE u.email = 'tuan.dang@company.vn' AND b.created_at = '2026-02-15 10:00:00'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'iPhone 15 Pro 256GB'),
 2, 29990000, 2000000
),
(
 (SELECT b.bulk_order_id FROM BULK_ORDER b JOIN USERS u ON b.user_id = u.user_id WHERE u.email = 'hung.le@company.vn'   AND b.created_at = '2026-03-01 08:00:00'),
 (SELECT product_id FROM PRODUCT WHERE product_name = N'Dell XPS 15 9530'),
 1, 43990000, 0
);

-- =============================================================
-- LEVEL 4
-- =============================================================

-- 24. BULK_PRICE_TIER
INSERT INTO BULK_PRICE_TIER (bulk_order_detail_id, min_qty, unit_price)
SELECT bod.bulk_order_detail_id, v.mq, v.up
FROM BULK_ORDER_DETAILS bod
JOIN BULK_ORDER bo ON bod.bulk_order_id = bo.bulk_order_id
JOIN USERS u       ON bo.user_id        = u.user_id
JOIN PRODUCT p     ON bod.product_id    = p.product_id
CROSS JOIN (VALUES (1, 52990000), (3, 49990000), (5, 47000000)) AS v(mq, up)
WHERE u.email = 'hung.le@company.vn' AND bo.created_at = '2026-02-01 09:00:00'
  AND p.product_name = N'MacBook Pro 14 inch M3 Pro';

INSERT INTO BULK_PRICE_TIER (bulk_order_detail_id, min_qty, unit_price)
SELECT bod.bulk_order_detail_id, v.mq, v.up
FROM BULK_ORDER_DETAILS bod
JOIN BULK_ORDER bo ON bod.bulk_order_id = bo.bulk_order_id
JOIN USERS u       ON bo.user_id        = u.user_id
JOIN PRODUCT p     ON bod.product_id    = p.product_id
CROSS JOIN (VALUES (1, 43990000), (3, 41000000)) AS v(mq, up)
WHERE u.email = 'hung.le@company.vn' AND bo.created_at = '2026-02-01 09:00:00'
  AND p.product_name = N'Dell XPS 15 9530';

INSERT INTO BULK_PRICE_TIER (bulk_order_detail_id, min_qty, unit_price)
SELECT bod.bulk_order_detail_id, v.mq, v.up
FROM BULK_ORDER_DETAILS bod
JOIN BULK_ORDER bo ON bod.bulk_order_id = bo.bulk_order_id
JOIN USERS u       ON bo.user_id        = u.user_id
JOIN PRODUCT p     ON bod.product_id    = p.product_id
CROSS JOIN (VALUES (1, 27990000), (2, 26500000)) AS v(mq, up)
WHERE u.email = 'tuan.dang@company.vn' AND bo.created_at = '2026-02-15 10:00:00'
  AND p.product_name = N'Samsung Galaxy S24 Ultra 512GB';

-- 25. ORDER_CUSTOMIZATION
-- CustomizationStatus hợp lệ: PENDING, IN_PROGRESS, COMPLETED, CANCELLED
INSERT INTO ORDER_CUSTOMIZATION (bulk_order_detail_id, type, note, status, extra_fee)
SELECT bod.bulk_order_detail_id, v.t, v.nt, v.st, v.ef
FROM BULK_ORDER_DETAILS bod
JOIN BULK_ORDER bo ON bod.bulk_order_id = bo.bulk_order_id
JOIN USERS u       ON bo.user_id        = u.user_id
JOIN PRODUCT p     ON bod.product_id    = p.product_id
CROSS JOIN (VALUES
    ('ENGRAVING', N'Khắc tên công ty ABC vào vỏ máy', 'COMPLETED', 500000),
    ('COLOR',     N'Yêu cầu màu Space Gray toàn bộ 3 máy', 'COMPLETED', 0)
) AS v(t, nt, st, ef)
WHERE u.email = 'hung.le@company.vn' AND bo.created_at = '2026-02-01 09:00:00'
  AND p.product_name = N'MacBook Pro 14 inch M3 Pro';

INSERT INTO ORDER_CUSTOMIZATION (bulk_order_detail_id, type, note, status, extra_fee)
SELECT bod.bulk_order_detail_id, 'STICKER', N'Dán decal logo Dell đặc biệt', 'PENDING', 200000
FROM BULK_ORDER_DETAILS bod
JOIN BULK_ORDER bo ON bod.bulk_order_id = bo.bulk_order_id
JOIN USERS u       ON bo.user_id        = u.user_id
JOIN PRODUCT p     ON bod.product_id    = p.product_id
WHERE u.email = 'hung.le@company.vn' AND bo.created_at = '2026-02-01 09:00:00'
  AND p.product_name = N'Dell XPS 15 9530';

INSERT INTO ORDER_CUSTOMIZATION (bulk_order_detail_id, type, note, status, extra_fee)
SELECT bod.bulk_order_detail_id, 'PACKAGING', N'Đóng gói cao cấp, kèm thiệp tặng', 'PENDING', 150000
FROM BULK_ORDER_DETAILS bod
JOIN BULK_ORDER bo ON bod.bulk_order_id = bo.bulk_order_id
JOIN USERS u       ON bo.user_id        = u.user_id
JOIN PRODUCT p     ON bod.product_id    = p.product_id
WHERE u.email = 'tuan.dang@company.vn' AND bo.created_at = '2026-02-15 10:00:00'
  AND p.product_name = N'iPhone 15 Pro 256GB';
