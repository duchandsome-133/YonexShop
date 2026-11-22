-- ========================================================
-- Migration: V2__seed_data.sql
-- Description: Seed initial data for development environment
-- Target DB: Microsoft SQL Server
-- ========================================================

-- 1. Insert Roles
INSERT INTO Roles (RoleName, Description) VALUES
('ROLE_ADMIN', N'Quản trị viên toàn hệ thống'),
('ROLE_STAFF', N'Nhân viên quản lý đơn hàng và kho'),
('ROLE_CUSTOMER', N'Khách hàng mua sắm');

-- 2. Insert Default Users (Password: Password@123 - BCrypt encoded)
-- $2a$10$Y5nvyZ.8k7H1D9qUvFzZCeUoO7aPZzZqD6P3YqZzZqD6P3YqZzZqD is placeholder; DataInitializer will ensure exact matching hash
INSERT INTO Users (RoleId, FullName, Email, Phone, PasswordHash, IsActive, CreatedAt) VALUES
(1, N'Quản Trị Viên Yonex', 'admin@yonexshop.vn', '0901234567', '$2a$10$7R9rR5tJj3VwQ3YvjV4re.bE7pIqN/TfZlqDlhQeA8FmUk1u81p1G', 1, SYSUTCDATETIME()),
(2, N'Nhân Viên Kho & Đơn', 'staff@yonexshop.vn', '0912345678', '$2a$10$7R9rR5tJj3VwQ3YvjV4re.bE7pIqN/TfZlqDlhQeA8FmUk1u81p1G', 1, SYSUTCDATETIME()),
(3, N'Nguyễn Văn Khách', 'customer@yonexshop.vn', '0987654321', '$2a$10$7R9rR5tJj3VwQ3YvjV4re.bE7pIqN/TfZlqDlhQeA8FmUk1u81p1G', 1, SYSUTCDATETIME());

-- 3. Insert Categories
INSERT INTO Categories (Name, Slug, Description, IsActive) VALUES
(N'Vợt Cầu Lông', 'vot-cau-long', N'Các dòng vợt Yonex chính hãng: Astrox, Nanoflare, Arcsaber', 1),
(N'Giày Cầu Lông', 'giay-cau-long', N'Giày cầu lông công nghệ Power Cushion êm ái, bám sân', 1),
(N'Balo & Túi Vợt', 'balo-tui-vot', N'Túi đựng vợt và balo thi đấu Yonex chuyên dụng', 1),
(N'Dây Cước Căng Vợt', 'day-cuoc-cang-vot', N'Dây cước BG65, BG65 Ti, Nanogy, Exbolt trợ lực và độ bền cao', 1),
(N'Quần Áo Thể Thao', 'quan-ao-the-thao', N'Áo thi đấu, quần thể thao thoáng khí công nghệ Very Cool', 1),
(N'Phụ Kiện Khác', 'phu-kien-khac', N'Cuốn cán Yonex, băng chặn mồ hôi, bao cán vợt', 1);

-- 4. Insert Products
INSERT INTO Products (CategoryId, SKU, Name, Description, Price, IsActive) VALUES
-- Vợt Cầu Lông (CategoryId = 1)
(1, 'YNX-AX100ZZ', N'Vợt Cầu Lông Yonex Astrox 100ZZ Kurenai', N'Cây vợt tấn công đỉnh cao được tay vợt Viktor Axelsen sử dụng. Thân vợt siêu cứng Hyper Slim Shaft, đũa Namd gia tăng tốc độ phục hồi.', 4250000.00, 1),
(1, 'YNX-NF800P', N'Vợt Cầu Lông Yonex Nanoflare 800 Pro', N'Dòng vợt tốc độ phản tạt nhanh nhẹn, khung hình Sonic Flare System tạo lực đẩy cầu bứt phá.', 3950000.00, 1),
(1, 'YNX-ARC11P', N'Vợt Cầu Lông Yonex Arcsaber 11 Pro', N'Vợt công thủ toàn diện với khả năng kiểm soát cầu chính xác từng milimet, công nghệ Control-Assist Bumper.', 4100000.00, 1),

-- Giày Cầu Lông (CategoryId = 2)
(2, 'YNX-SHB65Z3', N'Giày Cầu Lông Yonex Power Cushion 65 Z3 Trắng', N'Mẫu giày quốc dân đồng hành cùng các vận động viên hàng đầu. Đệm Power Cushion+ hấp thụ chấn động và chuyển hồi lực vượt trội.', 2850000.00, 1),
(2, 'YNX-ECLIPS', N'Giày Cầu Lông Yonex Power Cushion Eclipsion Z3 Men', N'Độ ổn định tối đa, ôm sát bàn chân, bảo vệ cổ chân chống lật tuyệt đối.', 3150000.00, 1),

-- Dây cước (CategoryId = 4)
(4, 'YNX-BG65TI', N'Dây Cước Căng Vợt Yonex BG65 Titanium', N'Dây cước phổ biến nhất thế giới, bền bỉ với lớp phủ Titanium hydride, âm thanh nổ cầu đanh.', 140000.00, 1),
(4, 'YNX-EXBOLT65', N'Dây Cước Căng Vợt Yonex Exbolt 65', N'Cước thế hệ mới sợi Forged Fiber siêu bền và lực nảy cầu cực mạnh.', 210000.00, 1),

-- Phụ kiện khác (CategoryId = 6)
(6, 'YNX-AC102EX', N'Cuốn Cán Vợt Yonex Super Grap AC102EX (Vỉ 3 chiếc)', N'Cuốn cán êm, thấm hút mồ hôi số 1 của Yonex, chống trơn trượt hiệu quả.', 125000.00, 1);

-- 5. Insert Product Images
INSERT INTO ProductImages (ProductId, ImageUrl, IsPrimary, DisplayOrder) VALUES
(1, '/images/products/astrox-100zz.jpg', 1, 1),
(2, '/images/products/nanoflare-800-pro.jpg', 1, 1),
(3, '/images/products/arcsaber-11-pro.jpg', 1, 1),
(4, '/images/products/shb65z3.jpg', 1, 1),
(5, '/images/products/eclipsion-z3.jpg', 1, 1),
(6, '/images/products/bg65ti.jpg', 1, 1),
(7, '/images/products/exbolt65.jpg', 1, 1),
(8, '/images/products/ac102ex.jpg', 1, 1);

-- 6. Insert Product Variants
INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive) VALUES
-- Astrox 100ZZ (3U và 4U)
(1, 'YNX-AX100ZZ-4U5', N'Yonex Astrox 100ZZ 4UG5', N'Đỏ Kurenai', '4UG5', 0.00, 1),
(1, 'YNX-AX100ZZ-3U5', N'Yonex Astrox 100ZZ 3UG5', N'Đỏ Kurenai', '3UG5', 0.00, 1),

-- Nanoflare 800 Pro
(2, 'YNX-NF800P-4U5', N'Yonex Nanoflare 800 Pro 4UG5', N'Xanh Deep Green', '4UG5', 0.00, 1),

-- Arcsaber 11 Pro
(3, 'YNX-ARC11P-4U5', N'Yonex Arcsaber 11 Pro 4UG5', N'Xám Đỏ Grayish Pearl', '4UG5', 0.00, 1),

-- Giày 65 Z3 (Sizes)
(4, 'YNX-SHB65Z3-40', N'Yonex 65 Z3 Trắng Size 40', N'Trắng', '40', 0.00, 1),
(4, 'YNX-SHB65Z3-41', N'Yonex 65 Z3 Trắng Size 41', N'Trắng', '41', 0.00, 1),
(4, 'YNX-SHB65Z3-42', N'Yonex 65 Z3 Trắng Size 42', N'Trắng', '42', 0.00, 1),

-- Giày Eclipsion Z3
(5, 'YNX-ECLIPS-41', N'Yonex Eclipsion Z3 Size 41', N'Xanh Navy', '41', 0.00, 1),
(5, 'YNX-ECLIPS-42', N'Yonex Eclipsion Z3 Size 42', N'Xanh Navy', '42', 0.00, 1),

-- Cước BG65 Ti
(6, 'YNX-BG65TI-W', N'Yonex BG65 Ti Trắng', N'Trắng', N'0.70mm', 0.00, 1),

-- Cước Exbolt 65
(7, 'YNX-EXBOLT65-Y', N'Yonex Exbolt 65 Vàng', N'Vàng Chuối', N'0.65mm', 0.00, 1),

-- Cuốn cán AC102EX
(8, 'YNX-AC102EX-BLK', N'Cuốn cán Yonex AC102EX Đen', N'Đen', N'Vỉ 3', 0.00, 1),
(8, 'YNX-AC102EX-WHT', N'Cuốn cán Yonex AC102EX Trắng', N'Trắng', N'Vỉ 3', 0.00, 1);

-- 7. Insert Inventory for each Variant
INSERT INTO Inventory (VariantId, Quantity, UpdatedAt) VALUES
(1, 25, SYSUTCDATETIME()),
(2, 15, SYSUTCDATETIME()),
(3, 30, SYSUTCDATETIME()),
(4, 20, SYSUTCDATETIME()),
(5, 12, SYSUTCDATETIME()),
(6, 18, SYSUTCDATETIME()),
(7, 10, SYSUTCDATETIME()),
(8, 14, SYSUTCDATETIME()),
(9, 8, SYSUTCDATETIME()),
(10, 100, SYSUTCDATETIME()),
(11, 80, SYSUTCDATETIME()),
(12, 150, SYSUTCDATETIME()),
(13, 120, SYSUTCDATETIME());

-- 8. Customer Default Address
INSERT INTO Addresses (UserId, RecipientName, Phone, AddressLine, Ward, District, Province, IsDefault, CreatedAt) VALUES
(3, N'Nguyễn Văn Khách', '0987654321', N'Số 123 Đường Cầu Giấy', N'Quan Hoa', N'Cầu Giấy', N'Hà Nội', 1, SYSUTCDATETIME());
