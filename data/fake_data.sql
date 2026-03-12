USE [shop_system_electronics]

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

GO
SET IDENTITY_INSERT [dbo].[company] ON

INSERT [dbo].[company] ([company_id], [address], [approved_at], [business_type], [company_name], [email], [employee_count], [founding_date], [industry], [logo_url], [phone], [representative_name], [representative_position], [status], [tax_code], [website]) VALUES (4, N'Tòa nhà X, Cầu Giấy, Hà Nội', CAST(N'2025-01-01T08:00:00.0000000' AS DateTime2), N'TNHH', N'Công ty TNHH Phần mềm X', N'contact@xsoftware.vn', 150, CAST(N'2015-05-10' AS Date), N'Công nghệ thông tin', NULL, N'02811112222', N'Nguyễn Văn X', N'Giám đốc', N'APPROVED', N'0312345678', N'https://xsoftware.vn')
INSERT [dbo].[company] ([company_id], [address], [approved_at], [business_type], [company_name], [email], [employee_count], [founding_date], [industry], [logo_url], [phone], [representative_name], [representative_position], [status], [tax_code], [website]) VALUES (5, N'Tầng 5, Tháp Y, Q.1, TP.HCM', CAST(N'2025-01-01T08:00:00.0000000' AS DateTime2), N'Cổ phần', N'Công ty CP Đầu tư Alpha', N'info@alphainvest.com', 500, CAST(N'2010-09-20' AS Date), N'Đầu tư tài chính', NULL, N'02433334444', N'Trần Alpha', N'CEO', N'APPROVED', N'0109876543', N'https://alphainvest.com')
INSERT [dbo].[company] ([company_id], [address], [approved_at], [business_type], [company_name], [email], [employee_count], [founding_date], [industry], [logo_url], [phone], [representative_name], [representative_position], [status], [tax_code], [website]) VALUES (6, N'Đà Nẵng', CAST(N'2025-01-01T08:00:00.0000000' AS DateTime2), N'Tập đoàn', N'Tập đoàn Công nghệ Hưng Thịnh', N'sales@hungthinhcorp.com', 2000, CAST(N'2008-11-15' AS Date), N'Đa ngành', NULL, N'0909999999', N'Lê Hưng Thịnh', N'Chủ tịch', N'APPROVED', N'0310000001', N'https://hungthinhcorp.com')
SET IDENTITY_INSERT [dbo].[company] OFF
GO
SET IDENTITY_INSERT [dbo].[role] ON

INSERT [dbo].[role] ([role_id], [description], [role_name]) VALUES (7, N'Quản trị viên hệ thống', N'ADMIN')
INSERT [dbo].[role] ([role_id], [description], [role_name]) VALUES (8, N'Khách hàng mua lẻ', N'CUSTOMER')
INSERT [dbo].[role] ([role_id], [description], [role_name]) VALUES (9, N'Khách hàng doanh nghiệp', N'COMPANY')
SET IDENTITY_INSERT [dbo].[role] OFF
GO
SET IDENTITY_INSERT [dbo].[users] ON

INSERT [dbo].[users] ([user_id], [address], [email], [full_name], [password], [phone_number], [registration_date], [reward_point], [status], [company_id], [role_id]) VALUES (11, N'88 Nguyễn Trãi, TP.HCM', N'hung.le@company.vn', N'Lê Quốc Hùng', N'$2a$12$Sl/c9Nu.GHAm2uJf1LdNHuiTlATVtxbnAjRBbJQY3XNeefJCELNJ2', N'0903333333', CAST(N'2025-05-10T08:00:00.0000000' AS DateTime2), 500, N'ACTIVE', NULL, 9)
INSERT [dbo].[users] ([user_id], [address], [email], [full_name], [password], [phone_number], [registration_date], [reward_point], [status], [company_id], [role_id]) VALUES (12, N'99 Bạch Đằng, Hải Phòng', N'tuan.dang@company.vn', N'Đặng Minh Tuấn', N'$2a$12$Sl/c9Nu.GHAm2uJf1LdNHuiTlATVtxbnAjRBbJQY3XNeefJCELNJ2', N'0905555555', CAST(N'2025-07-15T11:00:00.0000000' AS DateTime2), 800, N'ACTIVE', NULL, 9)
INSERT [dbo].[users] ([user_id], [address], [email], [full_name], [password], [phone_number], [registration_date], [reward_point], [status], [company_id], [role_id]) VALUES (13, N'34 Pasteur, Q.3, TP.HCM', N'kimchi.hoang@gmail.com', N'Hoàng Thị Kim Chi', N'$2a$12$Sl/c9Nu.GHAm2uJf1LdNHuiTlATVtxbnAjRBbJQY3XNeefJCELNJ2', N'0906666666', CAST(N'2025-08-20T09:30:00.0000000' AS DateTime2), 200, N'ACTIVE', NULL, 8)
INSERT [dbo].[users] ([user_id], [address], [email], [full_name], [password], [phone_number], [registration_date], [reward_point], [status], [company_id], [role_id]) VALUES (14, N'67 Đinh Bộ Lĩnh, Bình Thạnh', N'nam.vu@gmail.com', N'Vũ Thanh Nam', N'$2a$12$Sl/c9Nu.GHAm2uJf1LdNHuiTlATVtxbnAjRBbJQY3XNeefJCELNJ2', N'0907777777', CAST(N'2025-09-05T16:00:00.0000000' AS DateTime2), 50, N'ACTIVE', NULL, 8)
INSERT [dbo].[users] ([user_id], [address], [email], [full_name], [password], [phone_number], [registration_date], [reward_point], [status], [company_id], [role_id]) VALUES (15, N'12 Hùng Vương, Cần Thơ', N'lan.pham@gmail.com', N'Phạm Thị Lan', N'$2a$12$Sl/c9Nu.GHAm2uJf1LdNHuiTlATVtxbnAjRBbJQY3XNeefJCELNJ2', N'0904444444', CAST(N'2025-06-01T14:00:00.0000000' AS DateTime2), 0, N'INACTIVE', NULL, 8)
INSERT [dbo].[users] ([user_id], [address], [email], [full_name], [password], [phone_number], [registration_date], [reward_point], [status], [company_id], [role_id]) VALUES (16, N'23 Lê Duẩn, Hà Nội', N'binh.nguyen@gmail.com', N'Nguyễn Văn Bình', N'$2a$12$Sl/c9Nu.GHAm2uJf1LdNHuiTlATVtxbnAjRBbJQY3XNeefJCELNJ2', N'0901111111', CAST(N'2025-03-15T09:00:00.0000000' AS DateTime2), 150, N'ACTIVE', NULL, 8)
INSERT [dbo].[users] ([user_id], [address], [email], [full_name], [password], [phone_number], [registration_date], [reward_point], [status], [company_id], [role_id]) VALUES (17, N'55 Trần Phú, Đà Nẵng', N'huong.tran@gmail.com', N'Trần Thị Hương', N'$2a$12$Sl/c9Nu.GHAm2uJf1LdNHuiTlATVtxbnAjRBbJQY3XNeefJCELNJ2', N'0902222222', CAST(N'2025-04-20T10:30:00.0000000' AS DateTime2), 320, N'ACTIVE', NULL, 8)
INSERT [dbo].[users] ([user_id], [address], [email], [full_name], [password], [phone_number], [registration_date], [reward_point], [status], [company_id], [role_id]) VALUES (18, N'ElectroShop Head Office', N'admin@electroshop.com', N'System Administrator', N'$2a$10$5gFxkr6YkUqZ6PwsYZUDa.Cu2x.GWe6Kj.3WTUf9OXzK/a2XBxAtq', N'0900000000', CAST(N'2026-03-09T22:45:50.6207657' AS DateTime2), 0, N'ACTIVE', NULL, 7)
SET IDENTITY_INSERT [dbo].[users] OFF
GO
SET IDENTITY_INSERT [dbo].[voucher] ON

INSERT [dbo].[voucher] ([voucher_id], [description], [discount_type], [discount_value], [is_active], [max_discount], [min_order_value], [usage_limit], [used_count], [valid_from], [valid_to], [voucher_code]) VALUES (6, N'Giảm 10% toàn đơn hàng', N'PERCENT', CAST(10.00 AS Numeric(38, 2)), 1, CAST(500000.00 AS Numeric(38, 2)), CAST(0.00 AS Numeric(38, 2)), 1000, 0, CAST(N'2026-01-01T00:00:00.0000000' AS DateTime2), CAST(N'2026-12-31T23:59:59.0000000' AS DateTime2), N'SALE10')
INSERT [dbo].[voucher] ([voucher_id], [description], [discount_type], [discount_value], [is_active], [max_discount], [min_order_value], [usage_limit], [used_count], [valid_from], [valid_to], [voucher_code]) VALUES (7, N'Giảm 50k cho đơn từ 500k', N'FIXED', CAST(50000.00 AS Numeric(38, 2)), 1, CAST(50000.00 AS Numeric(38, 2)), CAST(500000.00 AS Numeric(38, 2)), 500, 0, CAST(N'2026-01-01T00:00:00.0000000' AS DateTime2), CAST(N'2026-06-30T23:59:59.0000000' AS DateTime2), N'FLAT50K')
INSERT [dbo].[voucher] ([voucher_id], [description], [discount_type], [discount_value], [is_active], [max_discount], [min_order_value], [usage_limit], [used_count], [valid_from], [valid_to], [voucher_code]) VALUES (8, N'Giảm 20% cho khách hàng mới', N'PERCENT', CAST(20.00 AS Numeric(38, 2)), 1, CAST(100000.00 AS Numeric(38, 2)), CAST(0.00 AS Numeric(38, 2)), 200, 0, CAST(N'2026-01-01T00:00:00.0000000' AS DateTime2), CAST(N'2026-12-31T23:59:59.0000000' AS DateTime2), N'NEWUSER20')
INSERT [dbo].[voucher] ([voucher_id], [description], [discount_type], [discount_value], [is_active], [max_discount], [min_order_value], [usage_limit], [used_count], [valid_from], [valid_to], [voucher_code]) VALUES (9, N'Khuyến mại hè 2026 - giảm 15%', N'PERCENT', CAST(15.00 AS Numeric(38, 2)), 1, CAST(200000.00 AS Numeric(38, 2)), CAST(1000000.00 AS Numeric(38, 2)), 300, 0, CAST(N'2026-06-01T00:00:00.0000000' AS DateTime2), CAST(N'2026-08-31T23:59:59.0000000' AS DateTime2), N'SUMMER15')
INSERT [dbo].[voucher] ([voucher_id], [description], [discount_type], [discount_value], [is_active], [max_discount], [min_order_value], [usage_limit], [used_count], [valid_from], [valid_to], [voucher_code]) VALUES (10, N'VIP giảm 100k', N'FIXED', CAST(100000.00 AS Numeric(38, 2)), 1, CAST(100000.00 AS Numeric(38, 2)), CAST(2000000.00 AS Numeric(38, 2)), 100, 0, CAST(N'2026-01-01T00:00:00.0000000' AS DateTime2), CAST(N'2026-12-31T23:59:59.0000000' AS DateTime2), N'VIPFLAT100K')
SET IDENTITY_INSERT [dbo].[voucher] OFF
GO
SET IDENTITY_INSERT [dbo].[user_voucher] ON

INSERT [dbo].[user_voucher] ([user_voucher_id], [assigned_at], [status], [used_at], [user_id], [voucher_id]) VALUES (6, CAST(N'2026-01-10T09:00:00.0000000' AS DateTime2), N'USED', CAST(N'2026-01-15T10:30:00.0000000' AS DateTime2), 16, 6)
INSERT [dbo].[user_voucher] ([user_voucher_id], [assigned_at], [status], [used_at], [user_id], [voucher_id]) VALUES (7, CAST(N'2026-01-18T11:00:00.0000000' AS DateTime2), N'AVAILABLE', NULL, 17, 8)
INSERT [dbo].[user_voucher] ([user_voucher_id], [assigned_at], [status], [used_at], [user_id], [voucher_id]) VALUES (8, CAST(N'2026-02-08T10:00:00.0000000' AS DateTime2), N'USED', CAST(N'2026-02-10T11:30:00.0000000' AS DateTime2), 13, 7)
INSERT [dbo].[user_voucher] ([user_voucher_id], [assigned_at], [status], [used_at], [user_id], [voucher_id]) VALUES (9, CAST(N'2026-02-18T15:00:00.0000000' AS DateTime2), N'AVAILABLE', NULL, 14, 8)
INSERT [dbo].[user_voucher] ([user_voucher_id], [assigned_at], [status], [used_at], [user_id], [voucher_id]) VALUES (10, CAST(N'2026-01-01T00:00:00.0000000' AS DateTime2), N'AVAILABLE', NULL, 12, 10)
SET IDENTITY_INSERT [dbo].[user_voucher] OFF
GO
SET IDENTITY_INSERT [dbo].[ORDER] ON

INSERT [dbo].[ORDER] ([order_id], [discount_amount], [final_amount], [order_date], [order_status], [payment_method], [shipping_address], [total_amount], [user_id], [voucher_id]) VALUES (7, CAST(2999000.00 AS Numeric(38, 2)), CAST(26991000.00 AS Numeric(38, 2)), CAST(N'2026-01-15T10:30:00.0000000' AS DateTime2), N'DELIVERED', N'CREDIT_CARD', N'23 Lê Duẩn, Hà Nội', CAST(29990000.00 AS Numeric(38, 2)), 16, 6)
INSERT [dbo].[ORDER] ([order_id], [discount_amount], [final_amount], [order_date], [order_status], [payment_method], [shipping_address], [total_amount], [user_id], [voucher_id]) VALUES (8, CAST(0.00 AS Numeric(38, 2)), CAST(36980000.00 AS Numeric(38, 2)), CAST(N'2026-01-20T14:00:00.0000000' AS DateTime2), N'DELIVERED', N'BANK_TRANSFER', N'55 Trần Phú, Đà Nẵng', CAST(36980000.00 AS Numeric(38, 2)), 17, NULL)
INSERT [dbo].[ORDER] ([order_id], [discount_amount], [final_amount], [order_date], [order_status], [payment_method], [shipping_address], [total_amount], [user_id], [voucher_id]) VALUES (9, CAST(0.00 AS Numeric(38, 2)), CAST(52990000.00 AS Numeric(38, 2)), CAST(N'2026-02-05T09:00:00.0000000' AS DateTime2), N'PROCESSING', N'CASH_ON_DELIVERY', N'88 Nguyễn Trãi, TP.HCM', CAST(52990000.00 AS Numeric(38, 2)), 11, NULL)
INSERT [dbo].[ORDER] ([order_id], [discount_amount], [final_amount], [order_date], [order_status], [payment_method], [shipping_address], [total_amount], [user_id], [voucher_id]) VALUES (10, CAST(50000.00 AS Numeric(38, 2)), CAST(8940000.00 AS Numeric(38, 2)), CAST(N'2026-02-10T11:30:00.0000000' AS DateTime2), N'DELIVERED', N'CREDIT_CARD', N'34 Pasteur, Q.3, TP.HCM', CAST(8990000.00 AS Numeric(38, 2)), 13, 8)
INSERT [dbo].[ORDER] ([order_id], [discount_amount], [final_amount], [order_date], [order_status], [payment_method], [shipping_address], [total_amount], [user_id], [voucher_id]) VALUES (11, CAST(0.00 AS Numeric(38, 2)), CAST(6290000.00 AS Numeric(38, 2)), CAST(N'2026-02-20T16:00:00.0000000' AS DateTime2), N'CANCELLED', N'CREDIT_CARD', N'67 Đinh Bộ Lĩnh, Bình Thạnh', CAST(6290000.00 AS Numeric(38, 2)), 14, NULL)
INSERT [dbo].[ORDER] ([order_id], [discount_amount], [final_amount], [order_date], [order_status], [payment_method], [shipping_address], [total_amount], [user_id], [voucher_id]) VALUES (12, CAST(0.00 AS Numeric(38, 2)), CAST(12990000.00 AS Numeric(38, 2)), CAST(N'2026-03-01T08:00:00.0000000' AS DateTime2), N'PENDING', N'BANK_TRANSFER', N'23 Lê Duẩn, Hà Nội', CAST(12990000.00 AS Numeric(38, 2)), 16, NULL)
SET IDENTITY_INSERT [dbo].[ORDER] OFF
GO
SET IDENTITY_INSERT [dbo].[bulk_order] ON

INSERT [dbo].[bulk_order] ([bulk_order_id], [created_at], [discount_amount], [discount_applied], [discount_code], [discount_percentage], [final_price], [status], [total_price], [company_id], [user_id]) VALUES (4, CAST(N'2026-02-01T09:00:00.0000000' AS DateTime2), NULL, NULL, NULL, NULL, NULL, N'APPROVED', CAST(158970000.00 AS Numeric(38, 2)), 5, 11)
INSERT [dbo].[bulk_order] ([bulk_order_id], [created_at], [discount_amount], [discount_applied], [discount_code], [discount_percentage], [final_price], [status], [total_price], [company_id], [user_id]) VALUES (5, CAST(N'2026-02-15T10:00:00.0000000' AS DateTime2), NULL, NULL, NULL, NULL, NULL, N'PENDING', CAST(83960000.00 AS Numeric(38, 2)), 6, 12)
INSERT [dbo].[bulk_order] ([bulk_order_id], [created_at], [discount_amount], [discount_applied], [discount_code], [discount_percentage], [final_price], [status], [total_price], [company_id], [user_id]) VALUES (6, CAST(N'2026-03-01T08:00:00.0000000' AS DateTime2), NULL, NULL, NULL, NULL, NULL, N'PROCESSING', CAST(43990000.00 AS Numeric(38, 2)), 5, 11)
SET IDENTITY_INSERT [dbo].[bulk_order] OFF
GO
SET IDENTITY_INSERT [dbo].[brand] ON

INSERT [dbo].[brand] ([brand_id], [brand_name], [country], [description], [is_partner], [logo_url], [image_url]) VALUES (6, N'Apple', N'USA', N'Phụ kiện cao cấp chính hãng Apple', 1, N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/fa9b7647-1573-4ce7-b712-098e1713815f_apple-logo.png', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/brands/apple.png')
INSERT [dbo].[brand] ([brand_id], [brand_name], [country], [description], [is_partner], [logo_url], [image_url]) VALUES (7, N'Samsung', N'Korea', N'Linh kiện và phụ kiện Samsung', 1, N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/336cf004-e305-4935-a261-22a74f9259a8_samsung.png', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/brands/samsung.png')
INSERT [dbo].[brand] ([brand_id], [brand_name], [country], [description], [is_partner], [logo_url], [image_url]) VALUES (8, N'Logitech', N'Switzerland', N'Thiết bị ngoại vi hàng đầu thế giới', 1, N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/c02e7c9e-8433-4777-97be-4dd9d0dcbf85_logitech.png', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/brands/logitech.png')
INSERT [dbo].[brand] ([brand_id], [brand_name], [country], [description], [is_partner], [logo_url], [image_url]) VALUES (9, N'Sony', N'Japan', N'Tập đoàn điện tử Nhật Bản', 0, N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/db5df473-b40a-4f0b-87a4-704144c34efb_images.png', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/brands/sony.png')
INSERT [dbo].[brand] ([brand_id], [brand_name], [country], [description], [is_partner], [logo_url], [image_url]) VALUES (10, N'Razer', N'USA', N'Thương hiệu gaming gear phổ biến', 1, N'https://upload.wikimedia.org/wikipedia/en/4/40/Razer_snake_logo.svg', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/brands/razer.png')
INSERT [dbo].[brand] ([brand_id], [brand_name], [country], [description], [is_partner], [logo_url], [image_url]) VALUES (11, N'Kingston', N'USA', N'Giải pháp bộ nhớ và lưu trữ', 1, N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/c43ba659-7c83-4e24-b1c5-b28793a95fb7_Kingston-logo.jpg', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/brands/kingston.png')
INSERT [dbo].[brand] ([brand_id], [brand_name], [country], [description], [is_partner], [logo_url], [image_url]) VALUES (12, N'Western Digital', N'USA', N'Sản xuất ổ cứng hàng đầu', 1, N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/8de6c399-09a5-48af-ab5f-3e2656f3dda8_Western-Digital-Logo-2004.jpg', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/brands/wd.png')
SET IDENTITY_INSERT [dbo].[brand] OFF
GO
SET IDENTITY_INSERT [dbo].[category] ON

INSERT [dbo].[category] ([category_id], [category_name], [description],  [image_url]) VALUES (7, N'Phụ kiện điện thoại', N'Ốp lưng, sạc, cáp và các phụ kiện cho smartphone',  N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/fe40adcf-bfee-4a5f-ae8d-e764d6ba35b2_Phone_Accessories.svg')
INSERT [dbo].[category] ([category_id], [category_name], [description],  [image_url]) VALUES (8, N'Phụ kiện laptop', N'Chuột, bàn phím, túi chống sốc, hub chuyển đổi', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/e1f46446-3d11-4392-a37d-487b82137959_Laptop_Accessories.svg')
INSERT [dbo].[category] ([category_id], [category_name], [description],  [image_url]) VALUES (9, N'Thiết bị âm thanh', N'Tai nghe, loa bluetooth, soundbar',  N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/9ff57f67-9d37-49f0-89e4-5b1e29b18dac_speaker_478814.png')
INSERT [dbo].[category] ([category_id], [category_name], [description],  [image_url]) VALUES (10, N'Phụ kiện gaming', N'Bàn phím cơ, chuột gaming, lót chuột, headphone',  N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/62f8459f-2d7e-4206-91ea-6a39fbbacd17_game-controller.png')
INSERT [dbo].[category] ([category_id], [category_name], [description],  [image_url]) VALUES (11, N'Thiết bị lưu trữ', N'Ổ cứng SSD, HDD, USB, thẻ nhớ', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/d7aae0f0-9eed-46de-81ed-ec78279b8bcd_ssd.png')
SET IDENTITY_INSERT [dbo].[category] OFF
GO
SET IDENTITY_INSERT [dbo].[supplier] ON

INSERT [dbo].[supplier] ([supplier_id], [address], [contact_person], [email], [phone_number], [supplier_name]) VALUES (4, N'12 Lê Lợi, Q.1, TP.HCM', N'Nguyễn Văn A', N'supply@techviet.vn', N'0901000001', N'Công ty TNHH TechViet')
INSERT [dbo].[supplier] ([supplier_id], [address], [contact_person], [email], [phone_number], [supplier_name]) VALUES (5, N'45 Đinh Tiên Hoàng, Hà Nội', N'Trần Thị B', N'contact@digisource.vn', N'0901000002', N'Công ty CP DigiSource')
INSERT [dbo].[supplier] ([supplier_id], [address], [contact_person], [email], [phone_number], [supplier_name]) VALUES (6, N'78 Nguyễn Huệ, Đà Nẵng', N'Lê Văn C', N'info@smartimport.vn', N'0901000003', N'Công ty TNHH SmartImport')
SET IDENTITY_INSERT [dbo].[supplier] OFF
GO
SET IDENTITY_INSERT [dbo].[product] ON

INSERT [dbo].[product] ([product_id], [created_date], [description], [discount_percent], [original_price], [price], [product_name], [rating], [sold_count], [status], [brand_id], [category_id], [supplier_id], [description_details], [main_image]) VALUES (16, CAST(N'2025-01-10T08:00:00.0000000' AS DateTime2), N'Ốp lưng bảo vệ cao cấp chính hãng Apple', 18, CAST(1590000.00 AS Numeric(38, 2)), CAST(1303800.00 AS Numeric(38, 2)), N'Ốp lưng MagSafe iPhone 15 Pro', 4.9, 500, N'AVAILABLE', 6, 7, 4, N'<p><strong>Đặc điểm nổi bật</strong></p>
<ul>
 <li>
  <p>Thiết kế thời thượng, kết cấu&nbsp;<a href="https://www.thegioididong.com/op-lung-flipcover" title="Tham khảo các mẫu ốp lưng tại Thế Giới Di Động">ốp lưng</a>&nbsp;gọn đẹp, lựa chọn dành cho mọi lứa tuổi.</p></li>
 <li>
  <p>Chế tác các nút bấm trơn tru, không bị ảnh hưởng khi dùng&nbsp;<a href="https://www.thegioididong.com/op-lung-flipcover-cho-iphone-15-pro" title="Tham khảo các mẫu ốp lưng iPhone 15 Pro tại Thế Giới Di Động">ốp lưng iPhone 15 Pro</a>.</p></li>
 <li>
  <p><a href="https://www.thegioididong.com/op-lung-flipcover-apple" title="Tham khảo các mẫu ốp lưng Apple tại Thế Giới Di Động">Ốp lưng Apple</a>&nbsp;chế tác từ vải tinh dệt sang trọng, duy trì độ bền lâu dài.&nbsp;</p></li>
 <li>
  <p>Tương thích với sạc MagSafe giúp bổ sung năng lượng hiệu quả mà không cần gỡ ốp khi sạc không dây.&nbsp;</p></li>
 <li>
  <p>Kích thước tuân thủ thiết kế <a href="https://www.thegioididong.com/dtdd/iphone-15-pro" title="Xem thêm iPhone 15 Pro tại Thế Giới Di Động">iPhone 15 Pro</a>, ôm khít và giữ máy an toàn khi trải nghiệm,&nbsp;cung cấp một lớp bảo vệ an toàn và gia tăng sự tiện lợi, cho phép bạn phát huy tối đa tiềm năng của <a href="https://www.thegioididong.com/dtdd-apple-iphone-15-series" title="Xem thêm iPhone 15 series tại Thế Giới Di Động">iPhone 15</a>.</p></li>
 <li>
  <p><strong>Lưu ý: Thanh toán trước khi mở seal.</strong></p></li>
</ul>
<p></p>', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/b1bd381e-6d0c-4e38-88eb-50b0fa6781ba_op-lung-magsafe-iphone-15-pro-vai-tinh-det-apple-mt4h3-tim-3-750x500.jpg')
INSERT [dbo].[product] ([product_id], [created_date], [description], [discount_percent], [original_price], [price], [product_name], [rating], [sold_count], [status], [brand_id], [category_id], [supplier_id], [description_details], [main_image]) VALUES (17, CAST(N'2025-01-15T09:00:00.0000000' AS DateTime2), N'Sạc nhanh chính hãng Samsung cho Galaxy S24', 14, CAST(990000.00 AS Numeric(38, 2)), CAST(851400.00 AS Numeric(38, 2)), N'Sạc nhanh Samsung 45W Type-C', 4.8, 800, N'AVAILABLE', 7, 7, 5, N'<h2><strong>Củ sạc nhanh Type-C Samsung T4510 45W - Tối ưu thời gian, sạc nhanh an toàn</strong></h2>
<p><strong>Củ sạc siêu nhanh Samsung 45W T4510 là lựa chọn lý tưởng để duy trì hiệu suất hoạt động ổn định cho các thiết bị điện tử. Mẫu của sạc Samsung 45W này còn được trang bị kèm cáp USB-C to USB-C để mang tới sự tiện lợi trong quá trình sử dụng.</strong></p>
<h3><strong>Hỗ trợ sạc nhanh 45W, tương thích đa dạng</strong></h3>
<p><strong>Củ sạc nhanh Type-C Samsung T4510 45W sở hữu công suất nguồn lớn sẽ là người bạn đồng hành giúp tối ưu thời gian nạp năng lượng cho các thiết bị. Sản phẩm được thiết kế để có thể đồng bộ và mang tới công suất sạc tối đa cho các thiết bị Samsung Galaxy.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/phu-kien/cu-sac/Samsung/Cu-sac-nhanh-type-c-samssung-t4510-45w-1.jpg" alt="Củ sạc nhanh Type-C Samsung T4510 45W - Tối ưu thời gian, sạc nhanh an toàn"></strong></p>
<p><strong>Ngoài ra, mẫu củ sạc Samsung 45W này còn tương thích với đa dạng các chuẩn sạc Super Fast Charging 2.0, PD 3.0 PDO/PPS. Với chuẩn PDO, sản phẩm hỗ trợ điện áp đầu ra 9V, 15V và 20V. Còn chuẩn PPS cho phép điện áp linh hoạt, dao động từ 5V đến 20V.</strong></p>
<p><strong>Điều này giúp sản phẩm phù hợp để sử dụng cùng nhiều dòng smartphone, tablet, tai nghe, máy chơi game, camera,...hỗ trợ USB-C. Nhờ đó, người dùng có thể giảm bớt phụ kiện mang theo và tối ưu không gian lưu trữ khi di chuyển.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/phu-kien/cu-sac/Samsung/Cu-sac-nhanh-type-c-samssung-t4510-45w-2.jpg" alt="Củ sạc nhanh Type-C Samsung T4510 45W - Tối ưu thời gian, sạc nhanh an toàn"></strong></p>
<p><strong>Không chỉ tương thích với đa thiết bị, củ sạc siêu nhanh Samsung 45W này còn phù hợp với nhiều nguồn điện. Với tiêu chuẩn nguồn điện vào 100 - 240V, sản phẩm sẽ có thể sử dụng ở nhiều nơi, rất phù hợp để mang theo trong các chuyến du lịch, công tác.</strong></p>
<h3><strong>Thiết kế nhỏ nhẹ và nổi bật, tích hợp cáp tiện lợi</strong></h3>
<p><strong>Tuy có công suất đầu ra lớn nhưng củ sạc nhanh Type-C Samsung T4510 45W vẫn đảm bảo được tính tiện lợi. Sản phẩm sở hữu thiết kế nhỏ gọn, có thể tiện lợi cầm gọn trong tay hay lưu trữ với kích thước chỉ ở mức 43.5 x 81.8 x 28mm. Trọng lượng 92.5g giúp mẫu củ sạc Samsung 45W này không gặp khó khăn khi cần mang theo trong các chuyến đi.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/phu-kien/cu-sac/Samsung/Cu-sac-nhanh-type-c-samssung-t4510-45w-3.jpg" alt="Thiết kế nhỏ nhẹ và nổi bật, tích hợp cáp tiện lợi"></strong></p>
<p><strong>Tông màu đen giúp củ sạc Samsung 45W nổi bật và thu hút. Đồng thời, màu sắc này cũng giúp người dùng dễ nhận biết, hạn chế việc bỏ quên. Nhờ việc sử dụng vật liệu tái chế, sản phẩm còn đồng thời thể hiện được trách nhiệm của Samsung với cộng đồng người dùng và môi trường.</strong></p>
<p><strong>Củ sạc còn được thiết kế với chân cắm dạng tròn để phù hợp với tiêu chuẩn phích cắm điện của nhiều nước. Đồng thời, chân cắm tròn này giúp việc cắm sạc dễ dàng hơn cũng như hạn chế tình trạng chập điện khi tiếp xúc ổ cắm so với chân cắm dạng dẹt.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/phu-kien/cu-sac/Samsung/Cu-sac-nhanh-type-c-samssung-t4510-45w-4.jpg" alt="Thiết kế nhỏ nhẹ và nổi bật, tích hợp cáp tiện lợi"></strong></p>
<p><strong>Cổng sạc Type-C có độ tương thích cao với hầu hết các loại cáp hiện nay trên thị trường. Ngoài ra, Samsung còn trang bị cho sản phẩm một dây cáp USB-C to USB-C 5A có độ dài 1.8m. Với độ dài này, người dùng sẽ tiện lợi mang theo cả củ sạc lẫn cáp theo bên mình mà không có bất kỳ sự bất tiện nào.</strong></p>
<h3><strong>Nâng cao tính an toàn xuyên suốt quá trình sạc</strong></h3>
<p><strong>Không chỉ mang tới tốc độ sạc nhanh, củ sạc nhanh Type-C Samsung T4510 45W còn sẽ đảm bảo được sự an toàn cho người dùng và thiết bị. Mẫu củ sạc siêu nhanh Samsung 45W này sẽ bảo vệ thiết bị hiệu quả khỏi nhiệt lượng được tạo ra từ quá trình sạc nhanh với tính năng chống quá nhiệt.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/phu-kien/cu-sac/Samsung/Cu-sac-nhanh-type-c-samssung-t4510-45w-5.jpg" alt="Nâng cao tính an toàn xuyên suốt quá trình sạc"></strong></p>
<p><strong>Samsung còn đồng thời tích hợp các cơ chế chống quá dòng, chống đoản mạch cho mẫu củ sạc này. Điều này sẽ hạn chế tối đa chập cháy thiết bị do các tình trạng viên pin quá tải, dòng điện không ổn định,...Nhờ vậy mà người dùng cũng sẽ có thể loại bỏ những lo lắng để có được trải nghiệm sạc an toàn.</strong></p>
<h3><strong>Công nghệ IC thông minh giúp tiết kiệm điện năng ở chế độ chờ.</strong></h3>
<p><strong>Củ sạc nhanh Type-C Samsung T4510 45W còn tự hào khi sở hữu mức tiêu thụ điện năng thấp. Cụ thể, điện năng mà củ sạc nhanh Samsung 45W này tiêu thụ ở chế độ chờ chỉ dưới 5mW. Điều này có được là nhờ công nghệ IC thông minh mà Samsung tích hợp trên sản phẩm giúp giảm lượng điện năng dự phòng trong thời gian chờ không cần thiết.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/phu-kien/cu-sac/Samsung/Cu-sac-nhanh-type-c-samssung-t4510-45w-6.jpg" alt="Công nghệ IC thông minh giúp tiết kiệm điện năng ở chế độ chờ."></strong></p>
<p><strong>Nhờ đó, phiên bản củ sạc nhanh Samsung 45W chính hãng này sẽ góp phần làm giảm hóa đơn tiền điện hàng tháng. Đồng thời, việc tích trữ điện năng dự phòng thấp hơn cũng làm giảm tích tụ nhiệt lượng bên trong củ sạc khi không sử dụng. Từ đó, tính năng này còn giúp hạn chế các tai nạn do quá nhiệt, chập điện khi vô tình cắm sạc trong thời gian dài.</strong></p>
<h2><strong>Mua củ sạc nhanh Type-C Samsung T4510 45W giá tốt tại CellphoneS</strong></h2>
<p><strong>Củ sạc nhanh Type-C Samsung T4510 45W đã có thể tìm mua tại các cửa hàng CellphoneS ở nhiều tỉnh, thành phố. Mẫu củ sạc Samsung 45W này còn nhận được ưu đãi giảm giá tới từ các chương trình khuyến mãi, chiết khấu dành cho S-Student, S-Teacher, Smember,...</strong></p>
<p><strong>Sản phẩm được bảo hành chính hãng từ Samsung, đảm bảo chất lượng và yên tâm khi sử dụng. Kèm theo đó là hỗ trợ giao hàng tận nhà, giúp tối ưu trải nghiệm mua sắm của khách hàng.</strong></p>', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/3b4a2183-29b6-4a25-badc-f874f11539d2_5_1_1.webp')
INSERT [dbo].[product] ([product_id], [created_date], [description], [discount_percent], [original_price], [price], [product_name], [rating], [sold_count], [status], [brand_id], [category_id], [supplier_id], [description_details], [main_image]) VALUES (18, CAST(N'2025-01-20T08:00:00.0000000' AS DateTime2), N'Chuột công thái học cao cấp cho văn phòng', 16, CAST(2990000.00 AS Numeric(38, 2)), CAST(2511600.00 AS Numeric(38, 2)), N'Chuột Logitech MX Master 3S', 4.9, 350, N'AVAILABLE', 8, 8, 4, N'<h2><strong>Chuột Logitech MX Master 3S - Nhỏ gọn, cuộn siêu nhanh</strong></h2>
<p><strong>Chuột không dây Logitech MX Master 3S mang đến kiểu dáng công thái học, giúp sử dụng thoải mái khi được nâng đỡ cả bàn tay. Phụ kiện </strong><a href="https://cellphones.com.vn/phu-kien/chuot-ban-phim-may-tinh/chuot/logitech.html" title="Chuột Logitech"><strong>chuột Logitech</strong></a><strong> có khả năng sử dụng mượt mà trên nhiều bề mặt, đem lại phản hồi trực quan và vô cùng yên tĩnh khi nhấp chuột.</strong></p>
<h3><strong>Kiểu dáng MX Master 3S độc đáo, mượt mà trên mọi bề mặt</strong></h3>
<p><strong>MX Master 3S sở hữu thiết kế được làm khá đẹp mắt với những đường nét tỉ mỉ và gọn gàng. Đồng thời, kiểu dáng </strong><a href="https://cellphones.com.vn/phu-kien/chuot-ban-phim-may-tinh/chuot/chuot-cong-thai-hoc.html" title="Chuột công thái học giá rẻ"><strong>chuột công thái học</strong></a><strong> cũng giúp tay bạn được nâng đỡ và dùng thêm thoải mái để trải nghiệm lâu dài với góc nghiêng độc đáo của chuột.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/phu-kien/chuot/chuot-khong-day/Logitech/chuot-khong-day-logitech-mx-master-3s-1.jpg" alt="Chuột không dây Logitech MX Master 3S"></strong></p>
<p><a href="https://cellphones.com.vn/chuot-khong-day-logitech-mx-master-3s.html" title="Logitech MX Master 3S"><strong>Logitech MX Master 3S</strong></a><strong> có kích thước nhỏ gọn và trọng lượng nhẹ với nút cuộn chuyển động theo ngón tay người dùng. Với độ phân giải cảm biến quang học lên đến 8000 DPI, chuột hoàn toàn có thể sử dụng mượt mà trên mọi bề mặt.</strong></p>
<h3><strong>Tích hợp chức năng cuộn siêu nhanh, sạc nhanh dùng lâu</strong></h3>
<p><strong>Chuột không dây MX Master 3S Logitech&nbsp;có khả năng cuộn được 1000 dòng trên giây do được tích hợp tính năng cuộn điện từ MagSpeed. Nút cuộn còn được làm từ vật liệu cứng cáp với độ nhám nhẹ nên khi dùng không hề gây ra tiếng ồn.&nbsp;</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/phu-kien/chuot/chuot-khong-day/Logitech/chuot-khong-day-logitech-mx-master-3s-2.jpg" alt="Chuột không dây Logitech MX Master 3S"></strong></p>
<p><strong>Bên cạnh đó, sau khi được sạc đầy, chuột MX Master 3S có khả năng sử dụng lên tới 70 ngày. Đồng thời, với kết nối Bluetooth, thiết bị có thể tương thích với tất cả các hệ điều hành chính ngay sau khi lấy ra khỏi hộp.</strong></p>', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/ca6c422d-32f0-4ea7-a127-a322bf1e4029_5_235.webp')
INSERT [dbo].[product] ([product_id], [created_date], [description], [discount_percent], [original_price], [price], [product_name], [rating], [sold_count], [status], [brand_id], [category_id], [supplier_id], [description_details], [main_image]) VALUES (19, CAST(N'2025-02-10T09:00:00.0000000' AS DateTime2), N'Bàn phím Bluetooth đa thiết bị nhỏ gọn', 18, CAST(1090000.00 AS Numeric(38, 2)), CAST(893800.00 AS Numeric(38, 2)), N'Bàn phím không dây Logitech K380', 4.7, 600, N'AVAILABLE', 8, 8, 5, N'<h2><strong>Bàn phím Logitech K380 – bàn phím không dây hỗ trợ đắc lực cho công việc trong văn phòng</strong></h2>
<p><strong>Logitech K380 là </strong><a href="https://cellphones.com.vn/phu-kien/chuot-ban-phim-may-tinh/ban-phim/ban-phim-khong-day.html"><strong>bàn phím không dây</strong></a><strong>&nbsp;cho phép kết nối đồng thời cùng lúc đến 3 thiết bị sử dụng các hệ điều hành khác nhau hoặc cùng 1 hệ điều hành. Mang đến công cụ làm việc tiện lợi, hỗ trợ cho công việc trong văn phòng của người dùng trở nên thoải mái với công nghệ bàn phím của Logitech.</strong></p>
<h3><strong>Nút phím tròn mỏng, gõ êm tay, chính xác, nặng 423g</strong></h3>
<p><strong>Với sản phẩm </strong><a href="https://cellphones.com.vn/phu-kien/chuot-ban-phim-may-tinh/ban-phim/logitech.html" title="Bàn phím không dây Logitech"><strong>bàn phím không dây Logitech</strong></a><strong>&nbsp;này sở hữu kích thước 279 x 124 x 16 mm có trọng lượng 423g đáp ứng mọi nhu cầu di chuyển của người dùng, cùng với thiết kế độc đáo, lạ mắt tạo phong cách mới mẻ so với các bàn phím thông thường. Bàn phím được thiết kế với 2 tông màu xanh hoặc đen giúp cho người dùng có thể lựa chọn tông màu phù hợp.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/accessories/Logitech-Bluetooth-K380-1.jpg" alt="Nút phím tròn mỏng, gõ êm tay, chính xác, nặng 423g"></strong></p>
<p><strong>Bàn phím </strong><a href="https://cellphones.com.vn/ban-phim-bluetooth-logitech-k380.html" title="Logitech K380"><strong>Logitech K380</strong></a><strong> được thiết kế với các nút phím tròn mỏng, lạ mắt đem lại cảm giác thích thú cho người dùng khi sử dụng. Các phím tròn được thiết kế hơi lõm xuống ở phần bề mặt giúp cho người dùng có thể cảm nhận chính xác phím đã nhấn. Tuy nhiên, thiết kế bàn phím này có nhược điểm là các phím được đặt cách nhau riêng biệt, người dùng có thể bấm nhấm vào các khoảng trống ngăn cách giữa các nút gây ảnh hưởng đến tốc độ đánh máy. Nhưng chỉ cần một thời gian làm quen với bàn phím, người dùng sẽ không còn bị bỡ ngỡ như những lần sử dụng đầu tiên.</strong></p>
<h3><strong>Kết nối cùng lúc 3 thiết bị, đa dạng hệ điều hành, khoảng cách đến 10m</strong></h3>
<p><strong>Bàn phím Logitech K380 sử dụng kết nối Bluetooth 3.0 với khoảng cách kết nối lên đến 10 m giúp cho người dùng có thể thoải mái kết nối đến với các thiết bị ở mọi vị trí trong phạm vi kết nối. Ngoài ra, bàn phím còn có khả năng kết nối với 3 thiết bị cùng lúc và có thể dễ dàng chuyển đổi qua lại giữa các kết nối chỉ bằng tổ hợp phín fn + F1/F2/F3 mang đến cho người dùng khả năng chuyển đổi linh hoạt vượt trội.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/accessories/Logitech-Bluetooth-K380-2.jpg" alt="Kết nối cùng lúc 3 thiết bị, đa dạng hệ điều hành, khoảng cách đến 10m"></strong></p>
<p><strong>Hơn nữa, bàn phím còn hỗ trợ cho đa dạng hệ điều hành như Windows, Mac, iOS, Chrome OS, Android giúp cho người dùng có thể thoải mái sử dụng bàn phím cho đa dạng các thiết bị như smartphone, máy tính bảng, OC, laptop.</strong></p>
<p><strong>Xem thêm:&nbsp;</strong><a href="https://cellphones.com.vn/ban-phim-khong-day-logitech-k400-plus.html" title="logitech k400 plus"><strong>Bàn Phím Không Dây Logitech K400 Plus</strong></a></p>
<h3><strong>2 viên pin AAA sử dụng đến 2 năm</strong></h3>
<p><strong>Bàn phím Logitech K380 còn được đi kèm với 2 cục pin năng lượng AAA kiềm có tuổi thọ lên đến 2 năm được lắp sẵn giúp cho người dùng có thể yên tâm, thoải mái sử dụng bàn phím mà không phải lo ngại đến các vấn đề liên quan đến năng lượng.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/accessories/Logitech-Bluetooth-K380-3.jpg" alt="2 viên pin AAA sử dụng đến 2 năm"></strong></p>
<p><strong>Theo giới thiệu của hãng, tuổi thọ pin bàn phím được tính toán dựa trên mức ước tính 2 triệu lần nhấn phím/năm trong môi trường công sở. Trải nghiệm người dùng có thể khác nhau.</strong></p>
<h3><strong>Mua bàn phím Logitech K380 giá rẻ chính hãng tại CellPhoneS</strong></h3>
<p><strong>Hiện tại bàn phím không dây Logitech K380 đang được hệ thống bán lẻ điện thoại và phụ kiện CellphoneS bán với mức giá vô cùng hấp dẫn với nhiều ưu đãi cho khách hàng. Sản phẩm còn được CellphoneS bảo hành với chính sách 1 đổi 1 lên đến 12 tháng. CellphoneS cũng hỗ trợ khách hàng dịch vụ mua hàng trực tuyến và dịch vụ miễn phí giao hàng thu tiền cho các khách hàng ở xa.</strong></p>', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/c88fccd8-4bf7-4c09-9239-1eee8a79dad0_k380-multi-device-bluetooth-keyboard.webp')
INSERT [dbo].[product] ([product_id], [created_date], [description], [discount_percent], [original_price], [price], [product_name], [rating], [sold_count], [status], [brand_id], [category_id], [supplier_id], [description_details], [main_image]) VALUES (20, CAST(N'2025-01-05T08:00:00.0000000' AS DateTime2), N'Tai nghe chống ồn hàng đầu thế giới', 10, CAST(9990000.00 AS Numeric(38, 2)), CAST(8991000.00 AS Numeric(38, 2)), N'Tai nghe Sony WH-1000XM5', 4.9, 450, N'AVAILABLE', 9, 9, 4, N'<h2><strong>Tai nghe Sony WH-1000XM5 - Chống ồn dịu tai, sử dụng thoải mái</strong></h2>
<p><strong>Sony WH-1000XM5&nbsp;với thiết kế chống ồn dòng cao cấp, được trang bị bộ xử lý QN1. Đây là một trong những tai nghe chụp tai tốt trong phân khúc chống ồn chủ động.</strong></p>
<h3><strong>Thiết kế đẹp mắt, cá tính và có tính năng hạn chế chống ồn dịu tai</strong></h3>
<p><strong>Tai nghe sở hữu thiết kế kiểu choàng đầu phong cách cá tính. Khi không sử dụng, bạn có thể gấp gọn lại và thuận tiện cho bạn mang theo trong công việc hay bất cứ nơi đâu. Đặc biệt, bao bì tai nghe không sử dụng nhựa thay vào đó là các vật liệu tái chế an toàn cho môi trường.</strong></p>
<p><strong>Chiếc tai nghe </strong><a href="https://cellphones.com.vn/tai-nghe-chup-tai-sony-wh-1000xm5.html"><strong>WH-1000XM5</strong></a><strong> này đẹp bắt mắt về kiểu dáng, tốt về chất lượng với lớp vỏ được bao bọc bằng nhựa cứng chắc, mượt mà, không hề thô cứng cùng với miếng đệm tai nghe bằng da màu đen sang trọng và tạo cảm giác êm ái. Đệm tai được hoàn thiện từ da mềm cùng thiết kế ôm khít đầu nhưng không hề gây áp lực lên đôi tai.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/Tai-nghe/Sony/Sony-WH-1000XM5-1.jpg" alt="Tai nghe Sony WH-1000XM5 - Chống ồn dịu tai, sử dụng thoải mái"></strong></p>
<p><strong>Ngoài ra còn được trang bị tính năng chống ồn cao, ngăn chặn mọi âm thanh bên ngoài kèm với đó là chụp tai bao quát rộng và thoải mái giúp bạn đắm chìm trong thế giới âm nhạc của riêng mình. Chỉ cần bạn kích hoạt chế độ chống ồn, tai nghe sẽ nhanh chóng làm lắng dịu âm thanh từ môi trường xung quanh của bạn.</strong></p>
<p><strong>&gt;&gt;&gt; Xem thêm tai nghe </strong><a href="https://cellphones.com.vn/tai-nghe-chup-tai-sony-wh-1000xm6.html"><strong>Sony WH-1000xm6</strong></a><strong> chính hãng, giá tốt tại CellphoneS.</strong></p>
<h3><strong>Chuẩn âm thanh Hi-Res Audio và âm thanh rõ nét trung thực</strong></h3>
<p><strong>Sony WH-1000XM5 được trang bị bộ màn loa kích thước lớn đến 30 mm cùng phần rìa hỗ trợ tăng cường khả năng chống ồn. Bên cạnh đó là vật liệu&nbsp;composite sợi carbon nhẹ cùng vòng cứng giúp cải thiện độ rõ của âm thanh, đặc biệt là dải âm ở tần số cao.&nbsp;</strong></p>
<p><strong>Tai nghe Sony WH-1000XM5 đạt chuẩn âm thanh chất lượng cao Hi-Res Audio, cho bạn thưởng thức âm thanh rõ nét hơn, với chất lượng tuyệt vời không khác gì phòng thu. Bên cạnh đó với Extra Bass, tai nghe cho ra âm thanh chân thực với âm trầm sâu và âm bổng thanh trong.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/Tai-nghe/Sony/Sony-WH-1000XM5-3.jpg" alt="Tai nghe Sony WH-1000XM5 - Chống ồn dịu tai, sử dụng thoải mái"></strong></p>
<p><strong>Ngoài ra tai nghe còn được hỗ trợ kết nối Bluetooth tích hợp NFC. Công nghệ LDAC truyền tải âm thanh rõ nét không dây chất lượng cao. Công nghệ DSEE HX, S-Master HX giúp khôi phục các âm thanh thuộc nhiều dải tần cao bị mất trong quá trình bạn nén, tạo ra các tệp nhạc kỹ thuật số với âm thanh phong phú và tự nhiên.</strong></p>
<h3><strong>Bốn micro tiện dụng, pin dung lượng lớn</strong></h3>
<p><strong>Sony WH-1000XM5 được trang bị tới 4 micro thiết kế dạng chùm với thiết kế được tinh chỉnh hỗ trợ thu nhận giọng nói của người dùng tốt hơn. Bên cạnh đó là công nghệ AI với thuật toán giảm tiếng ồn dựa trên 500 mẫu giọng khác nhau.&nbsp;Nhờ đó tai nghe mang lại khả năng đàm thoại rõ nét, kể cả ở nơi có nhiều tạp âm.</strong></p>
<p><strong>Ngoài ra, khi người dùng bắt đầu đàm thoại, chế độ&nbsp;Speak-to-Chat sẽ tự động dừng nhạc, tính năng tiện lợi cho những cuộc trò chuyện rảnh tay.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/Tai-nghe/Sony/Sony-WH-1000XM5-2.jpg" alt="Tai nghe Sony WH-1000XM5 - Chống ồn dịu tai, sử dụng thoải mái"></strong></p>
<p><strong>Tai nghe&nbsp;Sony WH-1000XM5 được trang bị viên pin lớn cho thời gian sử dụng lên đến 30 giờ, nhờ đó thích hợp sử dụng cả trong những chuyến đi xa. Ngoài ra, trong trường hợp khẩn cấp tai nghe còn đáp ứng với công nghệ sạc nhanh thông qua bộ chuyển đổi AC&nbsp;USB-PD.</strong></p>
<h2><strong>Mua tai nghe Sony WH-1000XM5 giá rẻ, ưu đãi tốt tại CellphoneS</strong></h2>
<p><strong>Tại hệ thống luôn đảm bảo với quý khách hàng từ chất lượng cho đến dịch vụ chăm sóc sẽ khiến khách hàng hài lòng. Sản phẩm uy tín, chất lượng, nguồn gốc xuất xứ rõ ràng. Tai nghe Sony Wh-1000XM5 chính hãng sẽ là lựa chọn tuyệt vời dành cho bạn trong năm 2022.</strong></p>', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/d1c861c0-1561-4124-8150-1c768deaf628_tai-nghe-chup-tai-sony-wh-1000xm5-4.webp')
INSERT [dbo].[product] ([product_id], [created_date], [description], [discount_percent], [original_price], [price], [product_name], [rating], [sold_count], [status], [brand_id], [category_id], [supplier_id], [description_details], [main_image]) VALUES (21, CAST(N'2025-02-20T09:00:00.0000000' AS DateTime2), N'Loa Bluetooth di động âm thanh mạnh mẽ', 10, CAST(4990000.00 AS Numeric(38, 2)), CAST(4491000.00 AS Numeric(38, 2)), N'Loa Marshall Emberton II', 4.8, 200, N'AVAILABLE', 9, 9, 5, N'<h2><strong>Loa Marshall Emberton II – Thiết kế cá tính, nhỏ gọn</strong></h2>
<p><strong>Marshall Emberton 2&nbsp;là một trong những sản phẩm loa được thiết kế nhỏ nhất đến từ thương hiệu Emberton. Nhưng, thiết kế nhỏ nhắn này lại mang đến cho sản phẩm cá tính và tiện lợi.</strong></p>
<h3><strong>Khuấy động mọi không gian âm thanh công suất 20W</strong></h3>
<p><strong>Loa Bluetooth Marshall Emberton II có mức công suất hoạt động 20W từ 2 bộ driver mạnh mẽ. Do đó dù nhỏ gọn nhưng sản phẩm vẫn đủ sức khuấy động mọi không gian với chất âm lớn vượt trội. Đặc biệt, nếu người dùng kết nối hai thiết bị cùng lúc nhờ Stack mode, âm thanh càng trở nên sống động hơn.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/Loa/Marshall/loa-bluetooth-marshall-emberton-ii-1.jpg" alt="Âm thanh loa Marshall Emberton II"></strong></p>
<p><strong>Kèm theo công suất lớn đó là công nghệ âm True Stereophonic từ Marshall. Điều này đã giúp Marshall Emberton II tạo ra âm thanh đa hướng 360 độ. Nhờ vậy, người dùng sẽ dễ dàng tận hưởng âm thanh sôi động nhưng vẫn có được độ rõ ràng từ hai trình điều khiển. Từ đó, rock, pop, jazz đến EDM, loa đều có thể tự tin mang lại cho người dùng những trải nghiệm âm nhạc cực trọn vẹn.</strong></p>
<h3><strong>Chiếc loa với thiết kế nhỏ, cá tính</strong></h3>
<p><strong>Là một trong những phiên bản nhỏ nhất của Marshall Emberton. Loa bluetooth&nbsp;</strong><a href="https://cellphones.com.vn/loa-bluetooth-marshall-emberton-ii.html"><strong>Marshall Emberton II</strong></a><strong> có kích thước nhỏ gọn cùng trọng lượng siêu nhẹ nên có thể cầm nắm, mang theo một cách dễ dàng. Với kích thước và cân nặng này, chiếc loa sẵn sàng cùng bạn đi bất cứ nơi đâu.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/Loa/Marshall/loa-bluetooth-marshall-emberton-ii-2.jpg" alt="Thiết kế loa Marshall Emberton II"></strong></p>
<p><strong>Cạnh đó, loa còn được thiết kế với chất liệu cao cấp ở mặt trước và phần lưới đen ở mặt lưng, giúp chống va đập hiệu quả. Logo Marshall được kết hợp một cách nổi bật và vô cùng đặc trưng của thương hiệu.</strong></p>
<p><strong>&gt;&gt;&gt; Xem thêm Tai nghe </strong><a href="https://cellphones.com.vn/loa-bluetooth-marshall-emberton-iii.html"><strong>Marshall Emberton 3</strong></a><strong> đang sở hữu mức giá khá tốt hiện nay.</strong></p>
<h3><strong>Công nghệ kháng nước đỉnh cao cùng nhiều phiên bản màu sắc</strong></h3>
<p><strong>Emberton 2 được trang bị tính năng chống nước chuẩn IPX7 giúp cho loa có thể tránh được những tác động dù có bị ngâm trong nước ở độ sâu 1 mét với khoảng thời gian là 30 phút. Cạnh đó, loa còn được tích hợp thêm nhiều tính năng với nút điều khiển joy-stick có thể tăng giảm âm thanh hoặc chuyển sang, lùi bài hát hay tạm dừng chỉ với một nút duy nhất.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/Loa/Marshall/loa-bluetooth-marshall-emberton-ii-3.jpg" alt="Tính năng loa Marshall Emberton II"></strong></p>
<h3><strong>Mở rộng phạm vi kết nối và phát nhạc cả ngày dài&nbsp;</strong></h3>
<p><strong>Chuẩn kết nối được sử dụng trên loa Bluetooth Marshall Emberton II là Bluetooth 5.1. Từ đó, loa có thể ghép nối nhanh chóng và tương thích tốt hơn với nhiều thiết bị nghe nhạc trong phạm vi xa 10 mét. Hơn nữa, loa còn có thêm cổng 3.5mm. Do đó, thông qua cáp âm thanh, người dùng vẫn có thể kết nối với các thiết bị không có Bluetooth.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/Loa/Marshall/loa-bluetooth-marshall-emberton-ii-5.jpg" alt="Kết nối loa Marshall Emberton II"></strong></p>
<p><strong>Ngoài ra, loa Bluetooth Marshall Emberton II còn có thời lượng pin phát nhạc ấn tượng kéo dài cả ngày. Tuy vậy, loa chỉ mất khoảng 3 giờ để sạc đầy năng lượng thông qua cáp sạc Type C. Vì vậy, người dùng sẽ thoải mái dùng loa ở bất cứ đâu mà không cần quá lo về vấn đề bị gián đoạn pin.</strong></p>', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/a6485071-759f-42f6-b437-4ca61c4cab34_marshall_emberton_ii_3.webp')
INSERT [dbo].[product] ([product_id], [created_date], [description], [discount_percent], [original_price], [price], [product_name], [rating], [sold_count], [status], [brand_id], [category_id], [supplier_id], [description_details], [main_image]) VALUES (22, CAST(N'2025-01-25T10:00:00.0000000' AS DateTime2), N'Bàn phím gaming switch cơ học Razer Green', 10, CAST(4790000.00 AS Numeric(38, 2)), CAST(4311000.00 AS Numeric(38, 2)), N'Bàn phím cơ Razer BlackWidow V4', 4.7, 150, N'AVAILABLE', 10, 10, 6, N'<h2><strong>Đánh giá chi tiết bàn phím Razer BlackWidow V4</strong></h2>
<p>Razer BlackWidow V4 dòng <a href="https://gearvn.com/pages/ban-phim-may-tinh">bàn phím cơ</a> sở hữu thiết kế độc đáo, nổi bật với cụm phím Multimedia tăng thêm phần thuận tiện khi chơi game giải trí. Đặc biệt, nhờ vào hệ thống Led RGB sinh động mà người chơi có thể dễ dàng đồng bộ ánh sáng cùng các phụ kiện Gaming Gear khác tạo nên góc máy chơi game &amp; làm việc ấn tượng.</p>
<h3><strong>Thiết kế độc đáo cùng các chi tiết góc cạnh mạnh mẽ</strong></h3>
<p></p>
<p><img src="https://file.hstatic.net/200000722513/file/gearvn-ban-phim-razer-blackwidow-v4-green-switch_88b95d3f15cc4b4cba39d5faa9ad6686_1024x1024.jpg" alt="GEARVN-ban-phim-razer-blackwidow-v4-green-switch"></p>
<p></p>
<p>Sở hữu vẻ ngoài vô cùng độc đáo với tông màu đen chủ đạo được tạo điểm nhấn bằng các chi tiết góc cạnh được gia công tỉ mỉ tạo nên tổng thể đậm chất gaming nhưng vẫn không kém phần sang trọng. Nhờ vào thiết kế mang tính thẩm mỹ cao giúp người chơi dễ dàng chọn mua <a href="https://gearvn.com/pages/chuot-may-tinh">chuột máy tính</a>, tai nghe gaming cùng nhiều phụ kiện khác để kết hợp với Razer BlackWidow V4 tạo nên góc máy làm việc &amp; giải trí ấn tượng</p>
<h3><strong>Layout Fullsize cùng cụm phím Multimedia</strong></h3>
<p></p>
<p><img src="https://file.hstatic.net/200000722513/file/gearvn-ban-phim-razer-blackwidow-v4-green-switch-3_8f40ede496224a58b6dba131df7348d6_1024x1024.jpg" alt="GEARVN-ban-phim-razer-blackwidow-v4-green-switch"></p>
<p></p>
<p>Bên cạnh sở hữu thiết kế đầy mạnh mẽ, Razer BlackWidow V4 còn sở hữu Layout Fullsize cùng cụm phím số Numpad vô cùng thân thiện, rất thích hợp cho người dùng thường xuyên làm việc với số liệu, soạn thảo và sáng tạo nội dung.</p>
<p><img src="https://file.hstatic.net/200000722513/file/gearvn-ban-phim-razer-blackwidow-v4-green-switch-3_e73b7d793a314ca5a0858bf611930528_grande.png" alt="GEARVN-ban-phim-razer-blackwidow-v4-green-switch"></p>
<p>Đặc biệt, nhờ vào cụm phím Multimedia bao gồm 4 nút bấm và con lăn hỗ trợ người chơi điều chỉnh mọi thứ từ độ sáng đến âm lượng một cách tiện lợi mà không làm gián đoạn mạch cảm hứng sáng tạo và phấn khích khi đang giao tranh không các tựa game yêu thích.</p>
<h3><strong>Led Razer Chroma RGB độc đáo, sáng tạo</strong></h3>
<p></p>
<p><img src="https://file.hstatic.net/200000722513/file/gearvn-ban-phim-razer-blackwidow-v4-green-switch-1_c04bc4b6b3c34e45911746b493e592d5_1024x1024.jpg" alt="GEARVN-ban-phim-razer-blackwidow-v4-green-switch"></p>
<p></p>
<p>Để giúp vẻ ngoài thêm phần cuốn hút Razer BlackWidow V4 được tích hợp hệ thống led Razer Chroma RGB độc đáo, sáng tạo với nhiều hiệu ứng chiếu sáng khác nhau, tạo điều kiện cho người chơi dễ dàng đồng bộ màu sắc RGB cùng các phụ kiện chơi game như chuột gaming, <a href="https://gearvn.com/collections/tai-nghe-bluetooth-chinh-hang">tai nghe Bluetooth</a> hay bất kỳ phụ kiện khác trong hệ sinh thái Razer.</p>
<h3><strong>Công tắc cơ học độc quyền từ Razer</strong></h3>
<p></p>
<p><img src="https://file.hstatic.net/200000722513/file/gearvn-ban-phim-razer-blackwidow-v4-green-switch-1_b4fffbc991d5483289d16ae50e65d14c_grande.png" alt="GEARVN-ban-phim-razer-blackwidow-v4-green-switch"></p>
<p>Trang bị bộ công tắc cơ học độc quyền Razer Green Mechanical Switches cho cảm giác gõ tốt, âm thanh êm tai hoạt động ổn định khi sử dụng ở tần suất cao. Nếu bạn thường xuyên gõ văn bản thì đây chính là dòng <a href="https://gearvn.com/collections/ban-phim-may-tinh">bàn phím máy tính</a> bạn đang tìm kiếm đấy nhé!</p>
<p></p>
<h3><strong>Bộ Keycaps ABS Doubleshot</strong></h3>
<p></p>
<p><img src="https://file.hstatic.net/200000722513/file/gearvn-ban-phim-razer-blackwidow-v4-green-switch-2_669605e7640b481d8919217bbd86628b_1024x1024.jpg" alt="GEARVN-ban-phim-razer-blackwidow-v4-green-switch"></p>
<p></p>
<p>Bên cạnh việc sở hữu bộ công tắc cơ siêu mượt, <a href="https://gearvn.com/collections/ban-phim-choi-game-razer">bàn phím Razer</a> còn trang bị bộ Keycaps ABS cùng công nghệ in siêu bền đảm bảo các ký tự không bị phai mờ theo thời gian. Nhờ vào bộ Keycaps độ cao vừa phải, bề mặt có độ cong hợp lý vừa ôm lấy các ngón tay giúp gõ nhanh và chính xác mà không tốn quá nhiều sự tập trung.</p>
<h3><strong>Khả năng tương thích cao với nhiều thiết bị</strong></h3>
<p>Bạn có thể sử dụng tốt với các thiết bị <a href="https://gearvn.com/collections/laptop">Laptop</a> và cả <a href="https://gearvn.com/pages/pc-gvn">PC</a> vô cùng tiện lợi thông qua cáp USB Type-C theo tiêu chuẩn Châu Âu siêu bền đảm bảo quá trình sử dụng không bị gián đoạn.&nbsp;</p>', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/1d63739e-c047-4c97-bd73-39f7f8b2a20a_z4571453724049_38fee4cd392684bac3f5f2c9f131aaf8_7156f22659e0426cb913402d7ca960ed_master.jpg')
INSERT [dbo].[product] ([product_id], [created_date], [description], [discount_percent], [original_price], [price], [product_name], [rating], [sold_count], [status], [brand_id], [category_id], [supplier_id], [description_details], [main_image]) VALUES (23, CAST(N'2025-03-15T11:00:00.0000000' AS DateTime2), N'Chuột gaming không dây siêu nhẹ chuyên nghiệp', 7, CAST(3990000.00 AS Numeric(38, 2)), CAST(3710700.00 AS Numeric(38, 2)), N'Chuột Razer DeathAdder V3 Pro', 4.8, 220, N'AVAILABLE', 10, 10, 4, N'<h2>Chuột Razer DeathAdder V3 Pro Wireless</h2>
<p></p>
<p><img src="https://assets2.razerzone.com/images/og-image/razer-deathadder-v3-pro-og-image.png" alt="Image"></p>
<p></p>
<p><img src="https://www.tncstore.vn/media/product/250-8341-razer-deathadder-v3-pro-wireless.jpg" alt="Image"></p>
<p><strong>Razer DeathAdder V3 Pro Wireless</strong> là dòng chuột gaming cao cấp thuộc series <strong>DeathAdder</strong> nổi tiếng của <strong>Razer</strong>, được thiết kế dành cho game thủ chuyên nghiệp và esports. Với thiết kế công thái học tối ưu, cảm biến siêu chính xác và kết nối không dây tốc độ cao, chiếc chuột này mang lại hiệu năng vượt trội cho các tựa game FPS và competitive.</p>
<h3>🎮 Hiệu năng gaming đỉnh cao</h3>
<p>Chuột được trang bị cảm biến <strong>Razer Focus Pro 30K Optical Sensor</strong> cho độ chính xác cực cao với DPI lên đến <strong>30.000</strong>, tracking chuẩn xác trên nhiều bề mặt khác nhau. Điều này giúp game thủ kiểm soát từng chuyển động nhỏ nhất khi ngắm bắn hay thao tác nhanh trong game.</p>
<h3>⚡ Kết nối không dây siêu nhanh</h3>
<p>Sử dụng công nghệ <strong>Razer HyperSpeed Wireless</strong>, chuột mang lại độ trễ cực thấp, gần như tương đương chuột có dây. Khi kết hợp với <strong>HyperPolling Wireless Dongle</strong>, chuột có thể đạt <strong>polling rate lên tới 4000Hz</strong>, giúp phản hồi nhanh hơn nhiều so với chuẩn 1000Hz thông thường.</p>
<h3>🪶 Thiết kế siêu nhẹ – tối ưu cho esports</h3>
<p>Với trọng lượng chỉ khoảng <strong>63g</strong>, DeathAdder V3 Pro là một trong những chuột không dây nhẹ nhất trong phân khúc. Thiết kế công thái học đặc trưng của dòng DeathAdder giúp cầm nắm thoải mái trong thời gian dài, đặc biệt phù hợp với các kiểu grip như <strong>palm grip</strong> và <strong>claw grip</strong>.</p>
<h3>🔋 Thời lượng pin ấn tượng</h3>
<p>Chuột có thể hoạt động liên tục lên đến <strong>90 giờ</strong> chỉ với một lần sạc (ở chế độ 1000Hz). Điều này giúp game thủ yên tâm sử dụng trong nhiều ngày mà không cần sạc lại thường xuyên.</p>
<h3>🖱️ Switch quang học thế hệ mới</h3>
<p>Chuột sử dụng <strong>Optical Mouse Switch Gen-3</strong> cho tốc độ phản hồi cực nhanh, loại bỏ hiện tượng double-click và tăng độ bền lên đến <strong>90 triệu lần nhấn</strong>.</p>
<h3>📊 Thông số nổi bật</h3>
<p>Thông sốChi tiếtKết nốiHyperSpeed WirelessCảm biếnFocus Pro 30K Optical SensorDPI tối đa30.000 DPIPolling Rate1000Hz (4000Hz với HyperPolling Dongle)Trọng lượng~63gSwitchOptical Mouse Switch Gen-3Thời lượng pinLên đến 90 giờ</p>
<p>✅ <strong>Razer DeathAdder V3 Pro Wireless</strong> là lựa chọn lý tưởng cho game thủ muốn sở hữu một chiếc chuột không dây <strong>nhẹ – chính xác – phản hồi nhanh</strong>, phù hợp cho các tựa game FPS, MOBA và esports chuyên nghiệp.</p>', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/3af2dadc-57f9-4b73-a28f-6fca5ea8d2ac_g-day-razer-deathadder-v3-pro-black-5_dc9af905e20d41efb1952303ab3c444c_24a8518eb02c4a9aad9974f9859f8.jpg')
INSERT [dbo].[product] ([product_id], [created_date], [description], [discount_percent], [original_price], [price], [product_name], [rating], [sold_count], [status], [brand_id], [category_id], [supplier_id], [description_details], [main_image]) VALUES (24, CAST(N'2025-01-01T08:00:00.0000000' AS DateTime2), N'Ổ cứng SSD Samsung 990 Pro PCIe Gen 4.0 X4 NVMe 1TB cung cấp tốc độ đọc lên tới 7.450 MB/s và ghi đạt đến 6.900 MB/s, kèm theo dung lượng lưu trữ 1TB. Sản phẩm sở hữu tính linh hoạt nhờ thiết kế với giao diện NVMe 2.0. Ngoài ra, mẫu ổ cứng SSD Samsung này cho phép người dùng quản lý, tùy chỉnh qua phần mềm Samsung Magician.

', 12, CAST(3990000.00 AS Numeric(38, 2)), CAST(3511200.00 AS Numeric(38, 2)), N'Ổ cứng SSD Samsung 990 Pro 1TB', 4.9, 300, N'AVAILABLE', 7, 11, 6, N'<h2><strong>Ổ cứng SSD Samsung 990 Pro PCIe Gen 4.0 X4 NVMe 1TB - Mở rộng lưu trữ</strong></h2>
<p><strong>Ổ cứng SSD Samsung 990 Pro PCIe Gen 4.0 X4 NVMe 1TB là lựa chọn lý tưởng để trang bị cho máy tính. Qua các tính năng nổi trội, sản phẩm này có thể đáp ứng nhiều nhu cầu, nâng cấp hệ thống mạnh mẽ hơn.</strong></p>
<h3><strong>Ổ cứng SSD hiện đại, hoạt động bền bỉ</strong></h3>
<p><strong>Ổ cứng SSD Samsung 990 Pro PCIe Gen 4.0 X4 NVMe 1TB thuộc loại SSD giúp tối ưu hoá hiệu năng. Bên cạnh đó, dạng ổ thể rắn này cũng cho phép phân bổ dữ liệu đồng đều kèm theo khả năng duy trì nhiệt độ ổn định trong quá trình hoạt động lâu dài. Linh kiện được thiết kế gọn nhẹ, linh hoạt trong ứng dụng thực tế để phục vụ người dùng PC cấu hình cao.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/linh-kien-may-tinh/O-cung/SSD/Samsung/o-cung-ssd-samsung-990-pro-pcie-gen-4-0-x4-nvme-1tb-1.jpg" alt="Ổ cứng SSD hiện đại, hoạt động bền bỉ"></strong></p>
<p><strong>Ngoài ra, Samsung 990 Pro cũng mang lại sự an tâm cho người dùng nhờ cơ chế bảo mật dữ liệu tiên tiến. Không những vậy, phần mềm Samsung Magician đi kèm cho phép theo dõi tình trạng ổ cứng và điều chỉnh để đạt hiệu suất theo nhu cầu.</strong></p>
<h3><strong>Dung lượng lên đến 1TB</strong></h3>
<p><strong>Ổ cứng SSD Samsung 990 Pro PCIe Gen 4.0 X4 NVMe 1TB đem lại không gian lưu lý tưởng cho nhiều đối tượng người dùng khác nhau. Dung lượng 1TB cho phép lưu trữ trọn vẹn thư viện game và khởi chạy nhanh chóng nhờ tốc độ cao của SSD. Ổ cứng còn mang đến sự thoải mái khi người dùng cần bộ nhớ cho các dự án nặng.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/linh-kien-may-tinh/O-cung/SSD/Samsung/o-cung-ssd-samsung-990-pro-pcie-gen-4-0-x4-nvme-1tb-2.jpg" alt="Ổ cứng SSD Samsung 990 Pro PCIe Gen 4.0 X4 NVMe dung lượng  1TB"></strong></p>
<p><strong>Đối với người dùng phổ thông, 1TB là mức dung lượng cân bằng giữa khả năng lưu trữ và chi phí đầu tư. Khách hàng có thể thoải mái lưu tài liệu, hình ảnh, video cá nhân trong nhiều năm. Hãng cũng đã tối ưu bộ nhớ bằng cách sử dụng công nghệ NAND tiên tiến, đảm bảo vừa cung cấp không gian data rộng vừa duy trì độ bền và hiệu năng ổn định.</strong></p>
<h3><strong>Tốc độ đọc ghi ấn tượng</strong></h3>
<p><strong>Ổ cứng SSD Samsung 990 Pro PCIe Gen 4.0 X4 NVMe 1TB nổi bật với tốc độ đọc tuần tự lên tới 7.450 MB/s và ghi tuần tự đạt 6.900 MB/s. Vì vậy, hệ điều hành hay các ứng dụng cũng được tăng tốc khởi chạy, giảm thời gian chờ. Đồng thời, hệ thống còn xử lý mạnh mẽ các tệp tin dung lượng lớn và hạn chế độ trễ.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/linh-kien-may-tinh/O-cung/SSD/Samsung/o-cung-ssd-samsung-990-pro-pcie-gen-4-0-x4-nvme-1tb-3.jpg" alt="Tốc độ Ổ cứng SSD Samsung 990 Pro PCIe Gen 4.0 X4 NVMe 1TB"></strong></p>
<p><strong>Với các tác vụ văn phòng, việc truy xuất nhiều file cùng lúc cũng diễn ra nhanh chóng, giúp tối ưu năng suất công việc. Hơn thế nữa, công nghệ Dynamic Thermal Guard được tích hợp để kiểm soát nhiệt độ, ngăn chặn việc giảm hiệu suất khi ổ cứng hoạt động ở cường độ cao. Điều này giúp duy trì tốc độ ổn định khi khối lượng dữ liệu ghi liên tục thời gian dài.</strong></p>
<h3><strong>Giao diện NVMe 2.0, tương thích rộng</strong></h3>
<p><strong>Ổ cứng SSD Samsung 990 Pro PCIe Gen 4.0 X4 NVMe 1TB có giao diện NVMe 2.0 kết hợp chuẩn PCIe Gen 4.0 x4. Đây là chuẩn giao tiếp hiện đại mang lại băng thông lớn để khai thác tối đa tốc độ xử lý cho hệ thống.&nbsp;</strong></p>
<p><strong>Chuẩn NVMe 2.0 được thiết kế tối ưu cho bộ nhớ flash, giảm thiểu độ trễ và tăng khả năng xử lý nhiều hàng đợi dữ liệu cùng lúc. Giao diện này còn đảm bảo tính tương thích rộng rãi, hoạt động mượt mà trên các mẫu laptop gaming cao cấp, PC workstation.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/linh-kien-may-tinh/O-cung/SSD/Samsung/o-cung-ssd-samsung-990-pro-pcie-gen-4-0-x4-nvme-1tb-4.jpg" alt="Ổ cứng SSD Samsung 990 Pro PCIe Gen 4.0 X4 NVMe 1TB có giao diện NVMe 2.0"></strong></p>
<p><strong>Bên cạnh đó, ổ cứng SSD Samsung 990 Pro PCIe Gen 4.0 X4 NVMe 1TB còn có thể lắp ráp cho các hệ thống máy tính để bàn phổ thông có khe cắm M.2 PCIe 4.0. Điều này sẽ giúp mở rộng phạm vi ứng dụng của sản phẩm, không chỉ dành cho PC mà còn phục vụ nhu cầu đa nền tảng.</strong></p>
<h2><strong>Mua ổ cứng SSD Samsung 990 Pro PCIe Gen 4.0 X4 NVMe 1TB giá tốt tại CellphoneS</strong></h2>
<p><strong>Ổ cứng SSD Samsung 990 Pro PCIe Gen 4.0 X4 NVMe 1TB là linh kiện chất lượng với đa dạng đặc điểm tiên tiến. Từ bây giờ, khách hàng có thể đến CellphoneS để mua ngay sản phẩm chính hãng, giá phải chăng. Cửa hàng sẽ đem đến dịch vụ bảo hành uy tín với chính sách minh bạch, hỗ trợ chuyên nghiệp, tư vấn tận tìn</strong></p>', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/2d370709-a3d1-46ee-85ed-ba49ff71bed9_text_ng_n_8_8_24.webp')
INSERT [dbo].[product] ([product_id], [created_date], [description], [discount_percent], [original_price], [price], [product_name], [rating], [sold_count], [status], [brand_id], [category_id], [supplier_id], [description_details], [main_image]) VALUES (25, CAST(N'2024-12-01T08:00:00.0000000' AS DateTime2), N'USB lưu trữ tốc độ cao bền bỉ', 28, CAST(350000.00 AS Numeric(38, 2)), CAST(252000.00 AS Numeric(38, 2)), N'USB 3.2 Kingston DataTraveler', 4.5, 1200, N'AVAILABLE', 11, 11, 4, N'<h2><strong>USB Kingston DataTraveler Exodia DTX 64GB - Nhỏ gọn tiện lợi, truy cập dễ dàng</strong></h2>
<p><strong>USB Kingston DataTraveler Exodia DTX 64GB sở hữu kiểu dáng đơn giản nhưng vẫn truyền nhanh nhờ vào công nghệ USB 3.2 Gen 1. Hơn nữa, thiết bị </strong><a href="https://cellphones.com.vn/phu-kien/the-nho-usb-otg/usb.html" title="USB chính hãng"><strong>USB</strong></a><strong> này còn có mức dung lượng cao, đảm bảo để bạn lưu trữ một lượng lớn dữ liệu dễ dàng.</strong></p>
<h3><strong>Truyền tải nhanh chóng từ cổng USB 3.2 Gen 1</strong></h3>
<p><strong>USB Kingston DataTraveler Exodia DTX 64GB đạt chuẩn USB 3.2 Gen 1 với cổng USB Type-A được trang bị. Do đó, sản phẩm này sẽ cho phép bạn kết nối dễ dàng với nhiều thiết bị có sẵn USB Type-A như laptop, màn hình TV,... với tốc độ truyền tải nhanh ấn tượng và ổn định.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/phu-kien/USB/Kingston/usb-kingston-datatraveler-exodia-dtx-64gb-2.png" alt="USB Kingston DataTraveler Exodia DTX 64GB - Nhỏ gọn tiện lợi, truy cập dễ dàng"></strong></p>
<p><strong>Mặt khác, với chuẩn băng thông USB 3.2 Gen, USB Kingston DataTraveler Exodia DTX 64GB còn có thêm khả năng tương thích ngược. Từ đó, bạn có thể dùng được với USB 3.0, 2.0,... một cách tiện lợi.</strong></p>
<h3><strong>Thiết kế nhỏ gọn, có móc khóa treo dễ mang theo</strong></h3>
<p><strong>USB Kingston DataTraveler Exodia DTX 64GB được làm vô cùng nhỏ gọn với kiểu dáng đơn giản cho ra kích thước 67.3 x 21.04 x 10.14 mm. Vì vậy, thiết bị này đảm bảo tính di động cao và bạn có thể dễ dàng mang theo bên mình bằng cách bỏ túi.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/phu-kien/USB/Kingston/usb-kingston-datatraveler-exodia-dtx-64gb-1.png" alt="USB Kingston DataTraveler Exodia DTX 64GB - Nhỏ gọn tiện lợi, truy cập dễ dàng"></strong></p>
<p><strong>Bên cạnh đó, USB Kingston DataTraveler Exodia DTX 64GB còn có một móc treo khác màu và màu này sẽ tùy thuộc vào dung lượng bạn chọn. Hơn hết, móc treo này có thể gắn vào dây trang trí hoặc gắn chung với chìa khóa, hạn chế tình trạng USB bị rơi hay lạc mất.</strong></p>
<h2><strong>Mua ngay USB Kingston DataTraveler Exodia DTX 64GB giá tốt với CellphoneS</strong></h2>
<p><strong>USB Kingston DataTraveler Exodia DTX 64GB hiện đang được CellphoneS phân phối ở hầu hết các chi nhánh trên toàn quốc. Do đó, bạn có thể đến trực tiếp tại cửa hàng hoặc đặt mua USB này thông qua website để nhận về với giá siêu hời nhé!</strong></p>', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/caa3c134-1d5c-44e7-8a6b-4a08e504f1d8_usb-kingston-datatraveler-exodia-dtx-64gb_2.webp')
INSERT [dbo].[product] ([product_id], [created_date], [description], [discount_percent], [original_price], [price], [product_name], [rating], [sold_count], [status], [brand_id], [category_id], [supplier_id], [description_details], [main_image]) VALUES (26, CAST(N'2024-06-01T08:00:00.0000000' AS DateTime2), N'Ổ cứng lưu trữ dữ liệu truyền thống ổn định', 15, CAST(1890000.00 AS Numeric(38, 2)), CAST(1606500.00 AS Numeric(38, 2)), N'Ổ cứng HDD WD Blue 2TB', 4.4, 400, N'AVAILABLE', 12, 11, 4, N'<h2><strong>Ổ cứng HDD WD Blue 2TB 3.5” SATA (WD20EZBX) - Dung lượng lưu trữ nhiều hơn</strong></h2>
<p><strong>Ổ cứng HDD WD Blue 2TB 3.5” SATA (WD20EZBX) &nbsp;là một phần quan trọng không thể thiếu trong hệ thống lưu trữ của bạn, đặc biệt là khi bạn đang tìm kiếm sự ổn định và hiệu suất. Trong bài viết này, tìm hiểu ổ cứng này-một lựa chọn phổ biến cho cả văn phòng và trải nghiệm game.</strong></p>
<h3><strong>Loại ổ cứng HDD thông dụng</strong></h3>
<p><strong>Ổ cứng WD Blue 2TB thuộc dạng HDD (Hard Disk Drive) với kích thước 3.5 inch. Điều này làm cho nó trở thành sự lựa chọn lý tưởng cho các hệ thống máy tính đứng tại văn phòng hoặc trạm làm việc game.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/linh-kien-may-tinh/O-cung/HDD/o-cung-hdd-wd-blue-2tb-3-5-sata-wd20ezbx-1.png" alt="Ổ cứng HDD WD Blue 2TB 3.5&quot; SATA"></strong></p>
<p><strong>Với thương hiệu WD nổi tiếng, ổ cứng này mang đến độ tin cậy cao và khả năng bảo vệ dữ liệu. Công nghệ bảo vệ chống sốc giúp giảm nguy cơ mất dữ liệu do va đập.</strong></p>
<h3><strong>Dung lượng ổ cứng lên đến 2TB</strong></h3>
<p><strong>Ổ cứng HDD WD Blue 2TB 3.5” SATA (WD20EZBX) mang đến một khoảng không gian lưu trữ rộng lớn lên đến 2TB. Cho dù bạn là người sử dụng văn phòng hay một game thủ, dung lượng ổ cứng này đáp ứng mọi nhu cầu với tính linh hoạt cao.</strong></p>
<p><strong><img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:0:0/q:100/plain/https://cellphones.com.vn/media/wysiwyg/linh-kien-may-tinh/O-cung/HDD/o-cung-hdd-wd-blue-2tb-3-5-sata-wd20ezbx-2.jpg" alt="Ổ cứng HDD WD Blue 2TB 3.5&quot; SATA"></strong></p>
<p><strong>Với bộ nhớ cache mạnh mẽ lên đến 256MB, ổ cứng này không chỉ lưu trữ dữ liệu một cách hiệu quả mà còn tăng cường khả năng truy xuất.</strong></p>
<h2><strong>Mua ngay ổ cứng HDD WD Blue 2TB 3.5” SATA (WD20EZBX) chính hãng tại CellphoneS</strong></h2>
<p><strong>Hãy đến ngay với CellphoneS để có thể mua được ổ ổ cứng HDD WD Blue 2TB 3.5” SATA (WD20EZBX) chính hãng. Tại đây, bạn sẽ còn mua được sản phẩm với sự tư vấn nhiệt tình từ nhân viên cùng ưu đãi hấp dẫn.</strong></p>', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/2ba8ccef-5d8b-4fc2-b035-c6b004ae397a_o-cung-hdd-wd-blue-2tb-3-5-sata-wd20ezbx_3.webp')
SET IDENTITY_INSERT [dbo].[product] OFF
GO
SET IDENTITY_INSERT [dbo].[review] ON

INSERT [dbo].[review] ([review_id], [comment], [rating], [review_date], [product_id], [user_id]) VALUES (6, N'Ốp lưng rất đẹp, hít MagSafe cực chắc!', 5, CAST(N'2026-01-20T10:00:00.0000000' AS DateTime2), 16, 16)
INSERT [dbo].[review] ([review_id], [comment], [rating], [review_date], [product_id], [user_id]) VALUES (7, N'Sạc nhanh đúng chuẩn, dây cáp hơi ngắn một chút.', 4, CAST(N'2026-01-25T11:00:00.0000000' AS DateTime2), 17, 17)
INSERT [dbo].[review] ([review_id], [comment], [rating], [review_date], [product_id], [user_id]) VALUES (8, N'Chống ồn cực tốt, âm thanh tuyệt vời!', 5, CAST(N'2026-02-14T09:00:00.0000000' AS DateTime2), 20, 13)
INSERT [dbo].[review] ([review_id], [comment], [rating], [review_date], [product_id], [user_id]) VALUES (9, N'Chuột dùng cực sướng cho designer, nút cuộn rất thông minh.', 5, CAST(N'2026-02-22T14:00:00.0000000' AS DateTime2), 18, 14)
INSERT [dbo].[review] ([review_id], [comment], [rating], [review_date], [product_id], [user_id]) VALUES (10, N'Nhỏ gọn, gõ êm, kết nối đa thiết bị ổn định.', 5, CAST(N'2026-02-28T16:00:00.0000000' AS DateTime2), 19, 16)
SET IDENTITY_INSERT [dbo].[review] OFF
GO
SET IDENTITY_INSERT [dbo].[shopping_cart] ON

INSERT [dbo].[shopping_cart] ([cart_id], [created_date], [last_updated], [user_id]) VALUES (8, CAST(N'2026-01-10T09:00:00.0000000' AS DateTime2), CAST(N'2026-03-01T08:00:00.0000000' AS DateTime2), 16)
INSERT [dbo].[shopping_cart] ([cart_id], [created_date], [last_updated], [user_id]) VALUES (9, CAST(N'2026-01-18T11:00:00.0000000' AS DateTime2), CAST(N'2026-02-15T14:00:00.0000000' AS DateTime2), 17)
INSERT [dbo].[shopping_cart] ([cart_id], [created_date], [last_updated], [user_id]) VALUES (10, CAST(N'2026-02-01T08:00:00.0000000' AS DateTime2), CAST(N'2026-02-20T09:00:00.0000000' AS DateTime2), 11)
INSERT [dbo].[shopping_cart] ([cart_id], [created_date], [last_updated], [user_id]) VALUES (11, CAST(N'2026-02-08T10:00:00.0000000' AS DateTime2), CAST(N'2026-02-10T11:00:00.0000000' AS DateTime2), 13)
INSERT [dbo].[shopping_cart] ([cart_id], [created_date], [last_updated], [user_id]) VALUES (12, CAST(N'2026-02-18T15:00:00.0000000' AS DateTime2), CAST(N'2026-02-20T16:00:00.0000000' AS DateTime2), 14)
INSERT [dbo].[shopping_cart] ([cart_id], [created_date], [last_updated], [user_id]) VALUES (13, CAST(N'2026-03-09T23:10:10.9594781' AS DateTime2), NULL, 18)
SET IDENTITY_INSERT [dbo].[shopping_cart] OFF
GO
SET IDENTITY_INSERT [dbo].[wishlist] ON

INSERT [dbo].[wishlist] ([wishlist_id], [created_date], [user_id]) VALUES (6, CAST(N'2026-01-05T09:00:00.0000000' AS DateTime2), 16)
INSERT [dbo].[wishlist] ([wishlist_id], [created_date], [user_id]) VALUES (7, CAST(N'2026-01-15T10:00:00.0000000' AS DateTime2), 17)
INSERT [dbo].[wishlist] ([wishlist_id], [created_date], [user_id]) VALUES (8, CAST(N'2026-02-01T08:00:00.0000000' AS DateTime2), 13)
INSERT [dbo].[wishlist] ([wishlist_id], [created_date], [user_id]) VALUES (9, CAST(N'2026-03-09T23:10:10.9651337' AS DateTime2), 18)
SET IDENTITY_INSERT [dbo].[wishlist] OFF
GO
SET IDENTITY_INSERT [dbo].[wishlist_item] ON

INSERT [dbo].[wishlist_item] ([wishlist_item_id], [created_date], [product_id], [wishlist_id]) VALUES (6, CAST(N'2026-01-05T09:00:00.0000000' AS DateTime2), 22, 6)
INSERT [dbo].[wishlist_item] ([wishlist_item_id], [created_date], [product_id], [wishlist_id]) VALUES (7, CAST(N'2026-01-06T10:00:00.0000000' AS DateTime2), 25, 6)
INSERT [dbo].[wishlist_item] ([wishlist_item_id], [created_date], [product_id], [wishlist_id]) VALUES (8, CAST(N'2026-01-15T10:00:00.0000000' AS DateTime2), 16, 7)
INSERT [dbo].[wishlist_item] ([wishlist_item_id], [created_date], [product_id], [wishlist_id]) VALUES (9, CAST(N'2026-01-16T11:00:00.0000000' AS DateTime2), 20, 7)
INSERT [dbo].[wishlist_item] ([wishlist_item_id], [created_date], [product_id], [wishlist_id]) VALUES (10, CAST(N'2026-02-01T08:00:00.0000000' AS DateTime2), 24, 8)
SET IDENTITY_INSERT [dbo].[wishlist_item] OFF
GO
SET IDENTITY_INSERT [dbo].[store_branch] ON

INSERT [dbo].[store_branch] ([branch_id], [address], [branch_name], [contact_number], [location], [manager_name], [maps_url], [working_hours]) VALUES (5, N'25 Tràng Tiền, Hoàn Kiếm, Hà Nội', N'Chi nhánh Hà Nội', N'0243000001', N'Hoàn Kiếm, Hà Nội', N'Phạm Minh Đức', N'https://maps.app.goo.gl/hanoi1', N'08:00 - 22:00')
INSERT [dbo].[store_branch] ([branch_id], [address], [branch_name], [contact_number], [location], [manager_name], [maps_url], [working_hours]) VALUES (6, N'100 Lê Lợi, Q.1, TP.HCM', N'Chi nhánh TP.HCM', N'0283000002', N'Quận 1, TP.HCM', N'Nguyễn Thị Mai', N'https://maps.app.goo.gl/hcm1', N'08:00 - 22:00')
INSERT [dbo].[store_branch] ([branch_id], [address], [branch_name], [contact_number], [location], [manager_name], [maps_url], [working_hours]) VALUES (7, N'321 Nguyễn Văn Linh, Đà Nẵng', N'Chi nhánh Đà Nẵng', N'0236300003', N'Hải Châu, Đà Nẵng', N'Trần Thanh Hải', N'https://maps.app.goo.gl/danang1', N'08:00 - 21:00')
INSERT [dbo].[store_branch] ([branch_id], [address], [branch_name], [contact_number], [location], [manager_name], [maps_url], [working_hours]) VALUES (8, N'56 Hoà Bình, Ninh Kiều, Cần Thơ', N'Chi nhánh Cần Thơ', N'0292000004', N'Ninh Kiều, Cần Thơ', N'Lê Hoàng Anh', N'https://maps.app.goo.gl/cantho1', N'08:00 - 21:00')
SET IDENTITY_INSERT [dbo].[store_branch] OFF
GO
SET IDENTITY_INSERT [dbo].[order_detail] ON

INSERT [dbo].[order_detail] ([order_detail_id], [discount_amount], [quantity], [subtotal], [unit_price], [branch_id], [order_id], [product_id]) VALUES (8, CAST(0.00 AS Numeric(38, 2)), 1, CAST(1290000.00 AS Numeric(38, 2)), CAST(1290000.00 AS Numeric(38, 2)), 5, 7, 16)
INSERT [dbo].[order_detail] ([order_detail_id], [discount_amount], [quantity], [subtotal], [unit_price], [branch_id], [order_id], [product_id]) VALUES (9, CAST(0.00 AS Numeric(38, 2)), 1, CAST(8990000.00 AS Numeric(38, 2)), CAST(8990000.00 AS Numeric(38, 2)), 7, 8, 20)
INSERT [dbo].[order_detail] ([order_detail_id], [discount_amount], [quantity], [subtotal], [unit_price], [branch_id], [order_id], [product_id]) VALUES (10, CAST(0.00 AS Numeric(38, 2)), 1, CAST(2490000.00 AS Numeric(38, 2)), CAST(2490000.00 AS Numeric(38, 2)), 7, 8, 18)
INSERT [dbo].[order_detail] ([order_detail_id], [discount_amount], [quantity], [subtotal], [unit_price], [branch_id], [order_id], [product_id]) VALUES (11, CAST(0.00 AS Numeric(38, 2)), 10, CAST(42900000.00 AS Numeric(38, 2)), CAST(4290000.00 AS Numeric(38, 2)), 6, 9, 22)
INSERT [dbo].[order_detail] ([order_detail_id], [discount_amount], [quantity], [subtotal], [unit_price], [branch_id], [order_id], [product_id]) VALUES (12, CAST(0.00 AS Numeric(38, 2)), 1, CAST(3490000.00 AS Numeric(38, 2)), CAST(3490000.00 AS Numeric(38, 2)), NULL, 10, 24)
INSERT [dbo].[order_detail] ([order_detail_id], [discount_amount], [quantity], [subtotal], [unit_price], [branch_id], [order_id], [product_id]) VALUES (13, CAST(0.00 AS Numeric(38, 2)), 1, CAST(4490000.00 AS Numeric(38, 2)), CAST(4490000.00 AS Numeric(38, 2)), 6, 11, 21)
INSERT [dbo].[order_detail] ([order_detail_id], [discount_amount], [quantity], [subtotal], [unit_price], [branch_id], [order_id], [product_id]) VALUES (14, CAST(0.00 AS Numeric(38, 2)), 2, CAST(500000.00 AS Numeric(38, 2)), CAST(250000.00 AS Numeric(38, 2)), 5, 12, 25)
SET IDENTITY_INSERT [dbo].[order_detail] OFF
GO
SET IDENTITY_INSERT [dbo].[attribute] ON

INSERT [dbo].[attribute] ([attribute_id], [attribute_name]) VALUES (11, N'Chip xử lý')
INSERT [dbo].[attribute] ([attribute_id], [attribute_name]) VALUES (9, N'Dung lượng ROM')
INSERT [dbo].[attribute] ([attribute_id], [attribute_name]) VALUES (14, N'Hệ điều hành')
INSERT [dbo].[attribute] ([attribute_id], [attribute_name]) VALUES (12, N'Kích thước màn hình')
INSERT [dbo].[attribute] ([attribute_id], [attribute_name]) VALUES (10, N'Màu sắc')
INSERT [dbo].[attribute] ([attribute_id], [attribute_name]) VALUES (13, N'Pin')
INSERT [dbo].[attribute] ([attribute_id], [attribute_name]) VALUES (8, N'RAM')
SET IDENTITY_INSERT [dbo].[attribute] OFF
GO
SET IDENTITY_INSERT [dbo].[product_attribute] ON

INSERT [dbo].[product_attribute] ([product_attribute_id], [value], [attribute_id], [product_id]) VALUES (12, N'Graphite', 10, 18)
INSERT [dbo].[product_attribute] ([product_attribute_id], [value], [attribute_id], [product_id]) VALUES (13, N'1TB', 9, 24)
INSERT [dbo].[product_attribute] ([product_attribute_id], [value], [attribute_id], [product_id]) VALUES (14, N'30 gi?', 13, 20)
INSERT [dbo].[product_attribute] ([product_attribute_id], [value], [attribute_id], [product_id]) VALUES (15, N'Tím nhạt', 10, 16)
INSERT [dbo].[product_attribute] ([product_attribute_id], [value], [attribute_id], [product_id]) VALUES (16, N'Đen', 10, 17)
INSERT [dbo].[product_attribute] ([product_attribute_id], [value], [attribute_id], [product_id]) VALUES (17, N'Đen', 10, 21)
SET IDENTITY_INSERT [dbo].[product_attribute] OFF
GO
SET IDENTITY_INSERT [dbo].[bulk_order_details] ON

INSERT [dbo].[bulk_order_details] ([bulk_order_detail_id], [discount_snapshot], [quantity], [unit_price_snapshot], [bulk_order_id], [product_id]) VALUES (6, CAST(300000.00 AS Numeric(38, 2)), 20, CAST(4290000.00 AS Numeric(38, 2)), 4, 22)
INSERT [dbo].[bulk_order_details] ([bulk_order_detail_id], [discount_snapshot], [quantity], [unit_price_snapshot], [bulk_order_id], [product_id]) VALUES (7, CAST(200000.00 AS Numeric(38, 2)), 10, CAST(3690000.00 AS Numeric(38, 2)), 4, 23)
INSERT [dbo].[bulk_order_details] ([bulk_order_detail_id], [discount_snapshot], [quantity], [unit_price_snapshot], [bulk_order_id], [product_id]) VALUES (8, CAST(100000.00 AS Numeric(38, 2)), 5, CAST(3490000.00 AS Numeric(38, 2)), 5, 24)
INSERT [dbo].[bulk_order_details] ([bulk_order_detail_id], [discount_snapshot], [quantity], [unit_price_snapshot], [bulk_order_id], [product_id]) VALUES (9, CAST(100000.00 AS Numeric(38, 2)), 10, CAST(1290000.00 AS Numeric(38, 2)), 5, 16)
INSERT [dbo].[bulk_order_details] ([bulk_order_detail_id], [discount_snapshot], [quantity], [unit_price_snapshot], [bulk_order_id], [product_id]) VALUES (10, CAST(50000.00 AS Numeric(38, 2)), 5, CAST(890000.00 AS Numeric(38, 2)), 6, 19)
SET IDENTITY_INSERT [dbo].[bulk_order_details] OFF
GO
SET IDENTITY_INSERT [dbo].[bulk_price_tier] ON

INSERT [dbo].[bulk_price_tier] ([bulk_price_tier_id], [min_qty], [unit_price], [bulk_order_detail_id]) VALUES (8, 1, CAST(4290000.00 AS Numeric(38, 2)), 6)
INSERT [dbo].[bulk_price_tier] ([bulk_price_tier_id], [min_qty], [unit_price], [bulk_order_detail_id]) VALUES (9, 10, CAST(3990000.00 AS Numeric(38, 2)), 6)
INSERT [dbo].[bulk_price_tier] ([bulk_price_tier_id], [min_qty], [unit_price], [bulk_order_detail_id]) VALUES (10, 20, CAST(3700000.00 AS Numeric(38, 2)), 6)
INSERT [dbo].[bulk_price_tier] ([bulk_price_tier_id], [min_qty], [unit_price], [bulk_order_detail_id]) VALUES (11, 1, CAST(3690000.00 AS Numeric(38, 2)), 7)
INSERT [dbo].[bulk_price_tier] ([bulk_price_tier_id], [min_qty], [unit_price], [bulk_order_detail_id]) VALUES (12, 10, CAST(3400000.00 AS Numeric(38, 2)), 7)
INSERT [dbo].[bulk_price_tier] ([bulk_price_tier_id], [min_qty], [unit_price], [bulk_order_detail_id]) VALUES (13, 1, CAST(3490000.00 AS Numeric(38, 2)), 8)
INSERT [dbo].[bulk_price_tier] ([bulk_price_tier_id], [min_qty], [unit_price], [bulk_order_detail_id]) VALUES (14, 5, CAST(3200000.00 AS Numeric(38, 2)), 8)
SET IDENTITY_INSERT [dbo].[bulk_price_tier] OFF
GO
SET IDENTITY_INSERT [dbo].[order_customization] ON

INSERT [dbo].[order_customization] ([customization_id], [extra_fee], [note], [status], [type], [bulk_order_detail_id]) VALUES (5, CAST(500000.00 AS Numeric(38, 2)), N'Khắc tên công ty ABC vào vỏ phím', N'COMPLETED', N'ENGRAVING', 6)
INSERT [dbo].[order_customization] ([customization_id], [extra_fee], [note], [status], [type], [bulk_order_detail_id]) VALUES (6, CAST(200000.00 AS Numeric(38, 2)), N'Dán decal logo công ty XYZ', N'PENDING', N'STICKER', 7)
INSERT [dbo].[order_customization] ([customization_id], [extra_fee], [note], [status], [type], [bulk_order_detail_id]) VALUES (7, CAST(150000.00 AS Numeric(38, 2)), N'Đóng gói quà tặng cao cấp', N'PENDING', N'PACKAGING', 9)
SET IDENTITY_INSERT [dbo].[order_customization] OFF
GO
SET IDENTITY_INSERT [dbo].[branch_product_stock] ON

INSERT [dbo].[branch_product_stock] ([branch_product_stock_id], [last_updated], [quantity], [branch_id], [product_id]) VALUES (9, CAST(N'2025-01-10T08:00:00.0000000' AS DateTime2), 50, 5, 16)
INSERT [dbo].[branch_product_stock] ([branch_product_stock_id], [last_updated], [quantity], [branch_id], [product_id]) VALUES (10, CAST(N'2025-01-15T09:00:00.0000000' AS DateTime2), 100, 6, 17)
INSERT [dbo].[branch_product_stock] ([branch_product_stock_id], [last_updated], [quantity], [branch_id], [product_id]) VALUES (11, CAST(N'2025-01-20T08:00:00.0000000' AS DateTime2), 30, 5, 18)
INSERT [dbo].[branch_product_stock] ([branch_product_stock_id], [last_updated], [quantity], [branch_id], [product_id]) VALUES (12, CAST(N'2025-02-10T09:00:00.0000000' AS DateTime2), 45, 6, 19)
INSERT [dbo].[branch_product_stock] ([branch_product_stock_id], [last_updated], [quantity], [branch_id], [product_id]) VALUES (13, CAST(N'2025-01-05T08:00:00.0000000' AS DateTime2), 25, 7, 20)
INSERT [dbo].[branch_product_stock] ([branch_product_stock_id], [last_updated], [quantity], [branch_id], [product_id]) VALUES (14, CAST(N'2025-02-20T09:00:00.0000000' AS DateTime2), 15, 8, 21)
INSERT [dbo].[branch_product_stock] ([branch_product_stock_id], [last_updated], [quantity], [branch_id], [product_id]) VALUES (15, CAST(N'2025-01-25T10:00:00.0000000' AS DateTime2), 20, 5, 22)
INSERT [dbo].[branch_product_stock] ([branch_product_stock_id], [last_updated], [quantity], [branch_id], [product_id]) VALUES (16, CAST(N'2025-03-15T11:00:00.0000000' AS DateTime2), 35, 6, 23)
INSERT [dbo].[branch_product_stock] ([branch_product_stock_id], [last_updated], [quantity], [branch_id], [product_id]) VALUES (17, CAST(N'2025-01-01T08:00:00.0000000' AS DateTime2), 60, 5, 24)
INSERT [dbo].[branch_product_stock] ([branch_product_stock_id], [last_updated], [quantity], [branch_id], [product_id]) VALUES (18, CAST(N'2024-12-01T08:00:00.0000000' AS DateTime2), 200, 7, 25)
INSERT [dbo].[branch_product_stock] ([branch_product_stock_id], [last_updated], [quantity], [branch_id], [product_id]) VALUES (19, CAST(N'2024-06-01T08:00:00.0000000' AS DateTime2), 40, 8, 26)
SET IDENTITY_INSERT [dbo].[branch_product_stock] OFF
GO
SET IDENTITY_INSERT [dbo].[cart_item] ON

INSERT [dbo].[cart_item] ([cart_item_id], [added_date], [quantity], [cart_id], [product_id]) VALUES (8, CAST(N'2026-03-01T07:30:00.0000000' AS DateTime2), 1, 8, 25)
INSERT [dbo].[cart_item] ([cart_item_id], [added_date], [quantity], [cart_id], [product_id]) VALUES (9, CAST(N'2026-03-01T07:35:00.0000000' AS DateTime2), 2, 8, 17)
INSERT [dbo].[cart_item] ([cart_item_id], [added_date], [quantity], [cart_id], [product_id]) VALUES (10, CAST(N'2026-02-15T14:00:00.0000000' AS DateTime2), 1, 9, 24)
INSERT [dbo].[cart_item] ([cart_item_id], [added_date], [quantity], [cart_id], [product_id]) VALUES (11, CAST(N'2026-02-20T09:00:00.0000000' AS DateTime2), 2, 10, 23)
INSERT [dbo].[cart_item] ([cart_item_id], [added_date], [quantity], [cart_id], [product_id]) VALUES (12, CAST(N'2026-02-10T10:00:00.0000000' AS DateTime2), 1, 11, 21)
INSERT [dbo].[cart_item] ([cart_item_id], [added_date], [quantity], [cart_id], [product_id]) VALUES (13, CAST(N'2026-02-20T15:30:00.0000000' AS DateTime2), 1, 12, 20)
SET IDENTITY_INSERT [dbo].[cart_item] OFF
GO
SET IDENTITY_INSERT [dbo].[media] ON

INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (28, 1, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/b462a1f5-7185-4ba3-a440-89084de91e2d_op-lung-magsafe-iphone-15-pro-vai-tinh-det-apple-mt4h3-tim-2-750x500.jpg', 16)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (29, 0, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/b1bd381e-6d0c-4e38-88eb-50b0fa6781ba_op-lung-magsafe-iphone-15-pro-vai-tinh-det-apple-mt4h3-tim-3-750x500.jpg', 16)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (30, 2, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/fba2da05-6561-4d5f-88fb-ae317b41c599_op-lung-magsafe-iphone-15-pro-vai-tinh-det-apple-mt4h3-tim-1-750x500.jpg', 16)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (31, 0, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/3b4a2183-29b6-4a25-badc-f874f11539d2_5_1_1.webp', 17)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (32, 2, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/5773018f-e6fa-49db-a8c7-535199a40a71_frame_41.webp', 17)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (33, 1, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/e5118e73-65f5-460c-8241-ab2a8616726e_2_67.webp', 17)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (34, 0, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/ca6c422d-32f0-4ea7-a127-a322bf1e4029_5_235.webp', 18)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (35, 1, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/7a97dc42-6431-485c-a5a6-bad123140252_3_319.webp', 18)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (36, 2, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/be2e6a3d-6d65-4670-83ff-19313fcf7fec_1_346.jpg', 18)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (37, 0, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/c88fccd8-4bf7-4c09-9239-1eee8a79dad0_k380-multi-device-bluetooth-keyboard.webp', 19)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (38, 1, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/b4dcd5a1-7eed-42c0-b223-685031615db7_k380-multi-device-bluetooth-keyboard_2.webp', 19)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (39, 2, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/a991a368-f749-4909-87f0-9817b592bc94_ban-phim-bluetooth-logitech-k380.webp', 19)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (40, 1, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/b2a3fc36-803e-4056-9098-a40ba7f23b5d_03_1.webp', 20)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (41, 2, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/161f33b4-fba5-4f35-80e6-a6fe1745b270_02_4.webp', 20)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (42, 0, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/d1c861c0-1561-4124-8150-1c768deaf628_tai-nghe-chup-tai-sony-wh-1000xm5-4.webp', 20)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (43, 3, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/35ac3bb2-d8a5-4c29-99cb-cc2d4eae246b_01_2_1_1.webp', 20)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (44, 1, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/1c4df880-e5b9-4e86-a800-9ee145511633_marshall_emberton_ii_1.webp', 21)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (45, 0, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/a6485071-759f-42f6-b437-4ca61c4cab34_marshall_emberton_ii_3.webp', 21)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (46, 2, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/20374c14-abdc-483b-aa89-69dfe48fa4a1_loa-bluetooth-marshall-emberton-ii-ksp_1.png', 21)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (47, 0, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/1d63739e-c047-4c97-bd73-39f7f8b2a20a_z4571453724049_38fee4cd392684bac3f5f2c9f131aaf8_7156f22659e0426cb913402d7ca960ed_master.jpg', 22)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (48, 2, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/50f97db3-96de-4982-82bd-96600da34ea1_2_f69a2d419371436083b3a5521ebec66c_master.jpg', 22)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (49, 1, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/fae937be-3d0b-4c5f-9168-afd0483f4881_z4571450737160_2334de0074ebbfea277f8780519c46ac_568141b69a044b55bd1bf078f86a27a6_master.jpg', 22)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (50, 2, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/aada1619-4078-4237-aaee-52d8d81ad2ca_thumbchuot_acc1428f2df24917bbb963b7e16098ed_c67ceca043874fd69d778f62c28c63f4_master.png', 23)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (51, 0, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/3af2dadc-57f9-4b73-a28f-6fca5ea8d2ac_g-day-razer-deathadder-v3-pro-black-5_dc9af905e20d41efb1952303ab3c444c_24a8518eb02c4a9aad9974f9859f8.jpg', 23)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (52, 1, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/a65704c3-b52e-4277-abf2-aa46ea004c68_thumbchuot_acc1428f2df24917bbb963b7e16098ed_c67ceca043874fd69d778f62c28c63f4_master_1.png', 23)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (53, 2, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/06dfe789-cb78-424f-af4c-db21c02d8913_text_ng_n_3_8_29.webp', 24)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (54, 3, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/a221a0e0-7bda-442a-b3ba-4a4281f614f0_text_ng_n_2_10_38.webp', 24)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (55, 1, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/69482e45-917c-42a7-b08a-4dc39549387a_text_ng_n_6_5_30.webp', 24)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (56, 0, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/2d370709-a3d1-46ee-85ed-ba49ff71bed9_text_ng_n_8_8_24.webp', 24)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (57, 0, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/caa3c134-1d5c-44e7-8a6b-4a08e504f1d8_usb-kingston-datatraveler-exodia-dtx-64gb_2.webp', 25)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (58, 2, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/c892a975-4bda-4718-9b0f-1db70b45ddbe_usb-kingston-datatraveler-exodia-dtx-64gb-1.webp', 25)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (59, 1, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/556f1185-ffa7-4f99-b4c2-5e0e9feaf24c_usb-kingston-datatraveler-exodia-dtx-64gb_3.webp', 25)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (60, 1, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/1ea352e5-6d1f-45e1-88b9-a3c25abfe3c9_o-cung-hdd-wd-blue-2tb-3-5-sata-wd20ezbx_1.webp', 26)
INSERT [dbo].[media] ([media_id], [sort_order], [type], [url], [product_id]) VALUES (61, 0, N'IMAGE', N'https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev/elechoshop/others/2ba8ccef-5d8b-4fc2-b035-c6b004ae397a_o-cung-hdd-wd-blue-2tb-3-5-sata-wd20ezbx_3.webp', 26)
SET IDENTITY_INSERT [dbo].[media] OFF
GO
SET IDENTITY_INSERT [dbo].[warranty] ON

INSERT [dbo].[warranty] ([warranty_id], [end_date], [start_date], [warranty_period_months], [warranty_terms], [product_id]) VALUES (6, CAST(N'2026-01-10T00:00:00.0000000' AS DateTime2), CAST(N'2025-01-10T00:00:00.0000000' AS DateTime2), 12, N'Bảo hành chính hãng 12 tháng.', 16)
INSERT [dbo].[warranty] ([warranty_id], [end_date], [start_date], [warranty_period_months], [warranty_terms], [product_id]) VALUES (7, CAST(N'2026-01-15T00:00:00.0000000' AS DateTime2), CAST(N'2025-01-15T00:00:00.0000000' AS DateTime2), 12, N'Bảo hành 1 đổi 1 trong 12 tháng nếu có lỗi NSX.', 17)
INSERT [dbo].[warranty] ([warranty_id], [end_date], [start_date], [warranty_period_months], [warranty_terms], [product_id]) VALUES (8, CAST(N'2026-01-05T00:00:00.0000000' AS DateTime2), CAST(N'2025-01-05T00:00:00.0000000' AS DateTime2), 12, N'Bảo hành chính hãng Sony Việt Nam 12 tháng.', 20)
INSERT [dbo].[warranty] ([warranty_id], [end_date], [start_date], [warranty_period_months], [warranty_terms], [product_id]) VALUES (9, CAST(N'2027-01-25T00:00:00.0000000' AS DateTime2), CAST(N'2025-01-25T00:00:00.0000000' AS DateTime2), 24, N'Bàn hành chính hãng Razer 2 năm.', 22)
INSERT [dbo].[warranty] ([warranty_id], [end_date], [start_date], [warranty_period_months], [warranty_terms], [product_id]) VALUES (10, CAST(N'2030-01-01T00:00:00.0000000' AS DateTime2), CAST(N'2025-01-01T00:00:00.0000000' AS DateTime2), 60, N'Bảo hành 5 năm hoặc theo thông số TBW.', 24)
SET IDENTITY_INSERT [dbo].[warranty] OFF
GO
