-- ========================================================
-- Migration: V3__seed_all_categories_products.sql
-- Description: Seed products for all empty/sparse categories
-- Target DB: Microsoft SQL Server (Idempotent by SKU)
-- ========================================================

-- Resolve Category IDs
DECLARE @CatBaloId BIGINT = (SELECT TOP 1 CategoryId FROM Categories WHERE Slug = 'balo-tui-vot');
DECLARE @CatCuocId BIGINT = (SELECT TOP 1 CategoryId FROM Categories WHERE Slug = 'day-cuoc-cang-vot');
DECLARE @CatQuanAoId BIGINT = (SELECT TOP 1 CategoryId FROM Categories WHERE Slug = 'quan-ao-the-thao');
DECLARE @CatPhuKienId BIGINT = (SELECT TOP 1 CategoryId FROM Categories WHERE Slug = 'phu-kien-khac');

-- ========================================================
-- 1. DANH MỤC: BALO & TÚI VỢT (balo-tui-vot)
-- ========================================================
IF @CatBaloId IS NOT NULL
BEGIN
    -- Product 1: Balo Yonex Pro Backpack BA92212MEX
    IF NOT EXISTS (SELECT 1 FROM Products WHERE SKU = 'YNX-BA92212M')
    BEGIN
        INSERT INTO Products (CategoryId, SKU, Name, Description, Price, IsActive) VALUES
        (@CatBaloId, 'YNX-BA92212M', N'Balo Cầu Lông Yonex Pro Backpack BA92212MEX', 
         N'Balo thi đấu cao cấp Yonex Pro Series, tích hợp ngăn chứa vợt đệm mút chống sốc, ngăn để giày riêng biệt có lỗ thông khí và quai đeo công thái học trợ lực lưng.', 
         1850000.00, 1);
    END

    -- Product 2: Túi Vợt Yonex Pro Tournament Bag BA92231WEX
    IF NOT EXISTS (SELECT 1 FROM Products WHERE SKU = 'YNX-BA92231W')
    BEGIN
        INSERT INTO Products (CategoryId, SKU, Name, Description, Price, IsActive) VALUES
        (@CatBaloId, 'YNX-BA92231W', N'Túi Vợt Cầu Lông Yonex Pro Tournament Bag BA92231WEX', 
         N'Túi xách chữ nhật Pro Tournament chuyên nghiệp dành cho tuyển thủ. Thiết kế 2 ngăn lớn chứa tối đa 6 cây vợt, trang bị lớp bạc Thermo Guard chống nóng bảo vệ cước và khung vợt.', 
         2450000.00, 1);
    END

    -- Product 3: Bao Vợt Yonex Active 6 Chiếc BAG82226
    IF NOT EXISTS (SELECT 1 FROM Products WHERE SKU = 'YNX-BAG82226')
    BEGIN
        INSERT INTO Products (CategoryId, SKU, Name, Description, Price, IsActive) VALUES
        (@CatBaloId, 'YNX-BAG82226', N'Bao Vợt Cầu Lông Yonex Active 6 Chiếc BAG82226', 
         N'Túi vợt Yonex dòng Active thể thao, trẻ trung với khả năng chứa 6 vợt cùng phụ kiện, dây đeo vai kép êm ái có thể đeo dạng balo hoặc xách tay.', 
         1350000.00, 1);
    END
END

-- ========================================================
-- 2. DANH MỤC: DÂY CƯỚC CĂNG VỢT (day-cuoc-cang-vot)
-- ========================================================
IF @CatCuocId IS NOT NULL
BEGIN
    -- Product: Cước Yonex BG66 Ultimax
    IF NOT EXISTS (SELECT 1 FROM Products WHERE SKU = 'YNX-BG66UM')
    BEGIN
        INSERT INTO Products (CategoryId, SKU, Name, Description, Price, IsActive) VALUES
        (@CatCuocId, 'YNX-BG66UM', N'Dây Cước Căng Vợt Yonex BG66 Ultimax', 
         N'Dây cước cầu lông trợ lực số 1 của Yonex. Đường kính siêu mảnh 0.65mm kết hợp lớp phủ High-Intensity Nylon mang lại âm thanh nổ cầu cực vang và lực nảy cầu kinh ngạc.', 
         180000.00, 1);
    END
END

-- ========================================================
-- 3. DANH MỤC: QUẦN ÁO THỂ THAO (quan-ao-the-thao)
-- ========================================================
IF @CatQuanAoId IS NOT NULL
BEGIN
    -- Product 1: Áo Yonex Tournament Very Cool 10505
    IF NOT EXISTS (SELECT 1 FROM Products WHERE SKU = 'YNX-APP10505')
    BEGIN
        INSERT INTO Products (CategoryId, SKU, Name, Description, Price, IsActive) VALUES
        (@CatQuanAoId, 'YNX-APP10505', N'Áo Cầu Lông Yonex Tournament Very Cool 10505', 
         N'Áo thi đấu chính hãng công nghệ Very Cool làm mát cơ thể tức thì, giảm 3°C nhiệt độ bề mặt da. Vải sợi thun Poly cao cấp co giãn 4 chiều kháng tĩnh điện.', 
         750000.00, 1);
    END

    -- Product 2: Áo Yonex Game Shirt National Team 10488
    IF NOT EXISTS (SELECT 1 FROM Products WHERE SKU = 'YNX-APP10488')
    BEGIN
        INSERT INTO Products (CategoryId, SKU, Name, Description, Price, IsActive) VALUES
        (@CatQuanAoId, 'YNX-APP10488', N'Áo Cầu Lông Yonex Game Shirt National Team 10488', 
         N'Áo thể thao Yonex Game Shirt mẫu tuyển quốc gia, phong cách cắt may Precision Cut cho chuyển động vung vợt mượt mà, công nghệ chống tia UV50+.', 
         620000.00, 1);
    END

    -- Product 3: Quần Short Yonex Pro 15124
    IF NOT EXISTS (SELECT 1 FROM Products WHERE SKU = 'YNX-SHO15124')
    BEGIN
        INSERT INTO Products (CategoryId, SKU, Name, Description, Price, IsActive) VALUES
        (@CatQuanAoId, 'YNX-SHO15124', N'Quần Short Cầu Lông Yonex Pro 15124', 
         N'Quần short thể thao Yonex Pro vải dệt thoi mỏng nhẹ, cạp chun có dây rút chắc chắn, túi 2 bên hông sâu tiện lợi đựng cầu lông và đồ cá nhân.', 
         490000.00, 1);
    END
END

-- ========================================================
-- 4. DANH MỤC: PHỤ KIỆN KHÁC (phu-kien-khac)
-- ========================================================
IF @CatPhuKienId IS NOT NULL
BEGIN
    -- Product 1: Ống Cầu Lông Yonex Aerosensa AS-50
    IF NOT EXISTS (SELECT 1 FROM Products WHERE SKU = 'YNX-AS50')
    BEGIN
        INSERT INTO Products (CategoryId, SKU, Name, Description, Price, IsActive) VALUES
        (@CatPhuKienId, 'YNX-AS50', N'Ống Cầu Lông Yonex Aerosensa AS-50 (Hộp 12 quả)', 
         N'Quả cầu lông thi đấu hàng đầu thế giới được chứng nhận bởi Liên đoàn Cầu lông Thế giới (BWF). Lông ngỗng chọn lọc kỹ càng, đế bần 100% tự nhiên tạo quỹ đạo bay chuẩn xác và độ bền vượt bậc.', 
         790000.00, 1);
    END

    -- Product 2: Băng Chặn Mồ Hôi Cổ Tay Yonex AC489
    IF NOT EXISTS (SELECT 1 FROM Products WHERE SKU = 'YNX-AC489')
    BEGIN
        INSERT INTO Products (CategoryId, SKU, Name, Description, Price, IsActive) VALUES
        (@CatPhuKienId, 'YNX-AC489', N'Băng Chặn Mồ Hôi Cổ Tay Yonex AC489 (Bộ 2 chiếc)', 
         N'Băng cổ tay chính hãng Yonex dệt từ sợi cotton và spandex thấm hút mồ hôi tối đa, ngăn mồ hôi chảy xuống bàn tay gây trơn cán vợt.', 
         160000.00, 1);
    END

    -- Product 3: Tất Thể Thao Yonex 3D Ergo 19183
    IF NOT EXISTS (SELECT 1 FROM Products WHERE SKU = 'YNX-SOX19183')
    BEGIN
        INSERT INTO Products (CategoryId, SKU, Name, Description, Price, IsActive) VALUES
        (@CatPhuKienId, 'YNX-SOX19183', N'Tất Thể Thao Cầu Lông Yonex 3D Ergo 19183 (Set 3 Đôi)', 
         N'Vớ thể thao thiết kế 3D công thái học chuẩn cho bàn chân phải và trái riêng biệt, đệm gót và mũi dày êm chống chai chân và giảm lực phản hồi khi tiếp đất.', 
         220000.00, 1);
    END
END

-- ========================================================
-- 5. THÊM PRODUCT IMAGES
-- ========================================================
-- Helper procedure to add primary image by Product SKU
DECLARE @pId BIGINT;

-- BA92212M
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-BA92212M');
IF @pId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ProductImages WHERE ProductId = @pId)
    INSERT INTO ProductImages (ProductId, ImageUrl, IsPrimary, DisplayOrder) VALUES (@pId, '/images/products/ba92212m.jpg', 1, 1);

-- BA92231W
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-BA92231W');
IF @pId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ProductImages WHERE ProductId = @pId)
    INSERT INTO ProductImages (ProductId, ImageUrl, IsPrimary, DisplayOrder) VALUES (@pId, '/images/products/ba92231w.jpg', 1, 1);

-- BAG82226
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-BAG82226');
IF @pId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ProductImages WHERE ProductId = @pId)
    INSERT INTO ProductImages (ProductId, ImageUrl, IsPrimary, DisplayOrder) VALUES (@pId, '/images/products/bag82226.jpg', 1, 1);

-- BG66UM
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-BG66UM');
IF @pId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ProductImages WHERE ProductId = @pId)
    INSERT INTO ProductImages (ProductId, ImageUrl, IsPrimary, DisplayOrder) VALUES (@pId, '/images/products/bg66um.jpg', 1, 1);

-- APP10505
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-APP10505');
IF @pId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ProductImages WHERE ProductId = @pId)
    INSERT INTO ProductImages (ProductId, ImageUrl, IsPrimary, DisplayOrder) VALUES (@pId, '/images/products/app10505.jpg', 1, 1);

-- APP10488
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-APP10488');
IF @pId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ProductImages WHERE ProductId = @pId)
    INSERT INTO ProductImages (ProductId, ImageUrl, IsPrimary, DisplayOrder) VALUES (@pId, '/images/products/app10488.jpg', 1, 1);

-- SHO15124
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-SHO15124');
IF @pId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ProductImages WHERE ProductId = @pId)
    INSERT INTO ProductImages (ProductId, ImageUrl, IsPrimary, DisplayOrder) VALUES (@pId, '/images/products/sho15124.jpg', 1, 1);

-- AS50
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-AS50');
IF @pId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ProductImages WHERE ProductId = @pId)
    INSERT INTO ProductImages (ProductId, ImageUrl, IsPrimary, DisplayOrder) VALUES (@pId, '/images/products/as50.jpg', 1, 1);

-- AC489
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-AC489');
IF @pId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ProductImages WHERE ProductId = @pId)
    INSERT INTO ProductImages (ProductId, ImageUrl, IsPrimary, DisplayOrder) VALUES (@pId, '/images/products/ac489.jpg', 1, 1);

-- SOX19183
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-SOX19183');
IF @pId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ProductImages WHERE ProductId = @pId)
    INSERT INTO ProductImages (ProductId, ImageUrl, IsPrimary, DisplayOrder) VALUES (@pId, '/images/products/sox19183.jpg', 1, 1);


-- ========================================================
-- 6. THÊM PRODUCT VARIANTS & INVENTORY
-- ========================================================

-- Helper procedure to add variant and inventory
-- BA92212M
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-BA92212M');
IF @pId IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-BA92212M-BLU')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-BA92212M-BLU', N'Balo Yonex Pro BA92212MEX Xanh Navy', N'Xanh Navy', N'Tiêu chuẩn', 0.00, 1);
    
    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-BA92212M-BLK')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-BA92212M-BLK', N'Balo Yonex Pro BA92212MEX Đen Xám', N'Đen Xám', N'Tiêu chuẩn', 0.00, 1);
END

-- BA92231W
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-BA92231W');
IF @pId IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-BA92231W-RED')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-BA92231W-RED', N'Túi Yonex Pro BA92231WEX Đỏ Kurenai', N'Đỏ Kurenai', N'6 Cây Vợt', 0.00, 1);

    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-BA92231W-BLU')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-BA92231W-BLU', N'Túi Yonex Pro BA92231WEX Xanh Sapphire', N'Xanh Sapphire', N'6 Cây Vợt', 0.00, 1);
END

-- BAG82226
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-BAG82226');
IF @pId IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-BAG82226-YEL')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-BAG82226-YEL', N'Bao Vợt Yonex Active BAG82226 Đen Vàng', N'Đen Vàng', N'6 Cây Vợt', 0.00, 1);

    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-BAG82226-TEAL')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-BAG82226-TEAL', N'Bao Vợt Yonex Active BAG82226 Xanh Ngọc', N'Xanh Ngọc', N'6 Cây Vợt', 0.00, 1);
END

-- BG66UM
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-BG66UM');
IF @pId IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-BG66UM-W')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-BG66UM-W', N'Yonex BG66 Ultimax Trắng', N'Trắng', N'0.65mm', 0.00, 1);

    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-BG66UM-Y')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-BG66UM-Y', N'Yonex BG66 Ultimax Vàng Neon', N'Vàng Neon', N'0.65mm', 0.00, 1);
END

-- APP10505
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-APP10505');
IF @pId IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-APP10505-M-BLU')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-APP10505-M-BLU', N'Áo Yonex 10505 Xanh Dương Size M', N'Xanh Dương', N'M', 0.00, 1);

    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-APP10505-L-BLU')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-APP10505-L-BLU', N'Áo Yonex 10505 Xanh Dương Size L', N'Xanh Dương', N'L', 0.00, 1);

    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-APP10505-XL-BLU')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-APP10505-XL-BLU', N'Áo Yonex 10505 Xanh Dương Size XL', N'Xanh Dương', N'XL', 0.00, 1);
END

-- APP10488
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-APP10488');
IF @pId IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-APP10488-M-BLK')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-APP10488-M-BLK', N'Áo Yonex 10488 Đen Size M', N'Đen Cam', N'M', 0.00, 1);

    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-APP10488-L-BLK')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-APP10488-L-BLK', N'Áo Yonex 10488 Đen Size L', N'Đen Cam', N'L', 0.00, 1);

    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-APP10488-XL-BLK')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-APP10488-XL-BLK', N'Áo Yonex 10488 Đen Size XL', N'Đen Cam', N'XL', 0.00, 1);
END

-- SHO15124
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-SHO15124');
IF @pId IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-SHO15124-M-BLK')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-SHO15124-M-BLK', N'Quần Yonex 15124 Đen Size M', N'Đen', N'M', 0.00, 1);

    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-SHO15124-L-BLK')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-SHO15124-L-BLK', N'Quần Yonex 15124 Đen Size L', N'Đen', N'L', 0.00, 1);

    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-SHO15124-XL-BLK')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-SHO15124-XL-BLK', N'Quần Yonex 15124 Đen Size XL', N'Đen', N'XL', 0.00, 1);
END

-- AS50
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-AS50');
IF @pId IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-AS50-SP77')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-AS50-SP77', N'Cầu Lông Yonex AS-50 Tốc Độ 77', N'Trắng', N'Speed 77', 0.00, 1);

    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-AS50-SP76')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-AS50-SP76', N'Cầu Lông Yonex AS-50 Tốc Độ 76', N'Trắng', N'Speed 76', 0.00, 1);
END

-- AC489
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-AC489');
IF @pId IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-AC489-BLK')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-AC489-BLK', N'Băng Cổ Tay Yonex AC489 Đen', N'Đen', N'Bộ 2 chiếc', 0.00, 1);

    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-AC489-WHT')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-AC489-WHT', N'Băng Cổ Tay Yonex AC489 Trắng', N'Trắng', N'Bộ 2 chiếc', 0.00, 1);
END

-- SOX19183
SET @pId = (SELECT ProductId FROM Products WHERE SKU = 'YNX-SOX19183');
IF @pId IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-SOX19183-WHT')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-SOX19183-WHT', N'Tất Yonex 19183 Trắng Cổ Trung', N'Trắng', N'25-28cm', 0.00, 1);

    IF NOT EXISTS (SELECT 1 FROM ProductVariants WHERE SKU = 'YNX-SOX19183-BLK')
        INSERT INTO ProductVariants (ProductId, SKU, VariantName, Color, Size, PriceAdjustment, IsActive)
        VALUES (@pId, 'YNX-SOX19183-BLK', N'Tất Yonex 19183 Đen Cổ Trung', N'Đen', N'25-28cm', 0.00, 1);
END


-- ========================================================
-- 7. THÊM TỒN KHO CHO CÁC BIẾN THỂ MỚI (INVENTORY)
-- ========================================================
INSERT INTO Inventory (VariantId, Quantity, UpdatedAt)
SELECT v.VariantId, 30, SYSUTCDATETIME()
FROM ProductVariants v
LEFT JOIN Inventory i ON v.VariantId = i.VariantId
WHERE i.InventoryId IS NULL;
