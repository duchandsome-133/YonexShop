-- ========================================================
-- Migration: V1__init_schema.sql
-- Description: Create initial schema for Yonex Sports Shop
-- Target DB: Microsoft SQL Server
-- ========================================================

-- 1. Roles table
CREATE TABLE Roles (
    RoleId BIGINT IDENTITY(1,1) PRIMARY KEY,
    RoleName NVARCHAR(50) NOT NULL UNIQUE,
    Description NVARCHAR(255) NULL
);

-- 2. Users table
CREATE TABLE Users (
    UserId BIGINT IDENTITY(1,1) PRIMARY KEY,
    RoleId BIGINT NOT NULL,
    FullName NVARCHAR(150) NOT NULL,
    Email NVARCHAR(150) NOT NULL UNIQUE,
    Phone NVARCHAR(20) NULL,
    PasswordHash NVARCHAR(255) NOT NULL,
    IsActive BIT NOT NULL DEFAULT 1,
    CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    UpdatedAt DATETIME2 NULL,
    CONSTRAINT FK_Users_Roles FOREIGN KEY (RoleId) REFERENCES Roles(RoleId)
);

-- 3. Addresses table
CREATE TABLE Addresses (
    AddressId BIGINT IDENTITY(1,1) PRIMARY KEY,
    UserId BIGINT NOT NULL,
    RecipientName NVARCHAR(150) NOT NULL,
    Phone NVARCHAR(20) NOT NULL,
    AddressLine NVARCHAR(255) NOT NULL,
    Ward NVARCHAR(100) NULL,
    District NVARCHAR(100) NULL,
    Province NVARCHAR(100) NOT NULL,
    IsDefault BIT NOT NULL DEFAULT 0,
    CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_Addresses_Users FOREIGN KEY (UserId) REFERENCES Users(UserId) ON DELETE CASCADE
);

-- 4. Categories table
CREATE TABLE Categories (
    CategoryId BIGINT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(150) NOT NULL,
    Slug NVARCHAR(150) NOT NULL UNIQUE,
    Description NVARCHAR(500) NULL,
    IsActive BIT NOT NULL DEFAULT 1,
    CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

-- 5. Products table
CREATE TABLE Products (
    ProductId BIGINT IDENTITY(1,1) PRIMARY KEY,
    CategoryId BIGINT NOT NULL,
    SKU NVARCHAR(50) NOT NULL UNIQUE,
    Name NVARCHAR(255) NOT NULL,
    Description NVARCHAR(MAX) NULL,
    Price DECIMAL(18,2) NOT NULL CHECK (Price >= 0),
    IsActive BIT NOT NULL DEFAULT 1,
    CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    UpdatedAt DATETIME2 NULL,
    CONSTRAINT FK_Products_Categories FOREIGN KEY (CategoryId) REFERENCES Categories(CategoryId)
);

-- 6. ProductImages table
CREATE TABLE ProductImages (
    ImageId BIGINT IDENTITY(1,1) PRIMARY KEY,
    ProductId BIGINT NOT NULL,
    ImageUrl NVARCHAR(500) NOT NULL,
    IsPrimary BIT NOT NULL DEFAULT 0,
    DisplayOrder INT NOT NULL DEFAULT 0,
    CONSTRAINT FK_ProductImages_Products FOREIGN KEY (ProductId) REFERENCES Products(ProductId) ON DELETE CASCADE
);

-- 7. ProductVariants table
CREATE TABLE ProductVariants (
    VariantId BIGINT IDENTITY(1,1) PRIMARY KEY,
    ProductId BIGINT NOT NULL,
    SKU NVARCHAR(50) NOT NULL UNIQUE,
    VariantName NVARCHAR(150) NOT NULL,
    Color NVARCHAR(50) NULL,
    Size NVARCHAR(50) NULL,
    PriceAdjustment DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    IsActive BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_ProductVariants_Products FOREIGN KEY (ProductId) REFERENCES Products(ProductId) ON DELETE CASCADE
);

-- 8. Inventory table (1-1 with ProductVariants)
CREATE TABLE Inventory (
    InventoryId BIGINT IDENTITY(1,1) PRIMARY KEY,
    VariantId BIGINT NOT NULL UNIQUE,
    Quantity INT NOT NULL DEFAULT 0 CHECK (Quantity >= 0),
    UpdatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_Inventory_ProductVariants FOREIGN KEY (VariantId) REFERENCES ProductVariants(VariantId) ON DELETE CASCADE
);

-- 9. InventoryTransactions table
CREATE TABLE InventoryTransactions (
    TransactionId BIGINT IDENTITY(1,1) PRIMARY KEY,
    VariantId BIGINT NOT NULL,
    ChangeQuantity INT NOT NULL,
    Reason NVARCHAR(255) NOT NULL,
    ReferenceId NVARCHAR(100) NULL,
    CreatedBy NVARCHAR(150) NULL,
    CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_InventoryTransactions_ProductVariants FOREIGN KEY (VariantId) REFERENCES ProductVariants(VariantId)
);

-- 10. Carts table (1 active cart per user)
CREATE TABLE Carts (
    CartId BIGINT IDENTITY(1,1) PRIMARY KEY,
    UserId BIGINT NOT NULL UNIQUE,
    CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    UpdatedAt DATETIME2 NULL,
    CONSTRAINT FK_Carts_Users FOREIGN KEY (UserId) REFERENCES Users(UserId) ON DELETE CASCADE
);

-- 11. CartItems table
CREATE TABLE CartItems (
    CartItemId BIGINT IDENTITY(1,1) PRIMARY KEY,
    CartId BIGINT NOT NULL,
    VariantId BIGINT NOT NULL,
    Quantity INT NOT NULL CHECK (Quantity > 0),
    CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_CartItems_Carts FOREIGN KEY (CartId) REFERENCES Carts(CartId) ON DELETE CASCADE,
    CONSTRAINT FK_CartItems_ProductVariants FOREIGN KEY (VariantId) REFERENCES ProductVariants(VariantId),
    CONSTRAINT UQ_Cart_Variant UNIQUE (CartId, VariantId)
);

-- 12. Orders table
CREATE TABLE Orders (
    OrderId BIGINT IDENTITY(1,1) PRIMARY KEY,
    OrderCode NVARCHAR(50) NOT NULL UNIQUE,
    UserId BIGINT NOT NULL,
    RecipientName NVARCHAR(150) NOT NULL,
    RecipientPhone NVARCHAR(20) NOT NULL,
    ShippingAddress NVARCHAR(500) NOT NULL,
    Subtotal DECIMAL(18,2) NOT NULL CHECK (Subtotal >= 0),
    ShippingFee DECIMAL(18,2) NOT NULL DEFAULT 0.00 CHECK (ShippingFee >= 0),
    DiscountAmount DECIMAL(18,2) NOT NULL DEFAULT 0.00 CHECK (DiscountAmount >= 0),
    TotalAmount DECIMAL(18,2) NOT NULL CHECK (TotalAmount >= 0),
    PaymentMethod NVARCHAR(20) NOT NULL DEFAULT 'COD' CHECK (PaymentMethod = 'COD'),
    PaymentStatus NVARCHAR(30) NOT NULL DEFAULT 'UNPAID',
    OrderStatus NVARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (OrderStatus IN ('PENDING', 'CONFIRMED', 'SHIPPING', 'DELIVERED', 'CANCELLED')),
    Note NVARCHAR(500) NULL,
    CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    UpdatedAt DATETIME2 NULL,
    CONSTRAINT FK_Orders_Users FOREIGN KEY (UserId) REFERENCES Users(UserId)
);

-- 13. OrderItems table (Captures price & product snapshot)
CREATE TABLE OrderItems (
    OrderItemId BIGINT IDENTITY(1,1) PRIMARY KEY,
    OrderId BIGINT NOT NULL,
    ProductId BIGINT NULL,
    VariantId BIGINT NULL,
    ProductNameSnapshot NVARCHAR(255) NOT NULL,
    SKUSnapshot NVARCHAR(50) NOT NULL,
    UnitPrice DECIMAL(18,2) NOT NULL CHECK (UnitPrice >= 0),
    Quantity INT NOT NULL CHECK (Quantity > 0),
    LineTotal DECIMAL(18,2) NOT NULL CHECK (LineTotal >= 0),
    CONSTRAINT FK_OrderItems_Orders FOREIGN KEY (OrderId) REFERENCES Orders(OrderId) ON DELETE CASCADE,
    CONSTRAINT FK_OrderItems_Products FOREIGN KEY (ProductId) REFERENCES Products(ProductId) ON DELETE SET NULL,
    CONSTRAINT FK_OrderItems_ProductVariants FOREIGN KEY (VariantId) REFERENCES ProductVariants(VariantId) ON DELETE SET NULL
);

-- 14. Reviews table
CREATE TABLE Reviews (
    ReviewId BIGINT IDENTITY(1,1) PRIMARY KEY,
    UserId BIGINT NOT NULL,
    ProductId BIGINT NOT NULL,
    OrderId BIGINT NOT NULL,
    Rating INT NOT NULL CHECK (Rating BETWEEN 1 AND 5),
    Comment NVARCHAR(1000) NULL,
    IsVisible BIT NOT NULL DEFAULT 1,
    CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_Reviews_Users FOREIGN KEY (UserId) REFERENCES Users(UserId),
    CONSTRAINT FK_Reviews_Products FOREIGN KEY (ProductId) REFERENCES Products(ProductId),
    CONSTRAINT FK_Reviews_Orders FOREIGN KEY (OrderId) REFERENCES Orders(OrderId),
    CONSTRAINT UQ_User_Product_Order UNIQUE (UserId, ProductId, OrderId)
);

-- Indexes for performance
CREATE INDEX IX_Users_Email ON Users(Email);
CREATE INDEX IX_Products_CategoryId ON Products(CategoryId);
CREATE INDEX IX_Products_IsActive ON Products(IsActive);
CREATE INDEX IX_ProductVariants_ProductId ON ProductVariants(ProductId);
CREATE INDEX IX_Orders_UserId ON Orders(UserId);
CREATE INDEX IX_Orders_OrderCode ON Orders(OrderCode);
CREATE INDEX IX_Orders_OrderStatus ON Orders(OrderStatus);
CREATE INDEX IX_Reviews_ProductId ON Reviews(ProductId);
