-- ============================================================
-- Customer Warranty Table
-- Migration reference (Flyway disabled, Hibernate auto-creates)
-- ============================================================

CREATE TABLE CUSTOMER_WARRANTY (
    id              INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    product_id      INT NOT NULL,
    order_id        INT NULL,
    bulk_order_id   INT NULL,
    user_id         INT NOT NULL,
    quantity        INT NOT NULL,
    warranty_months INT NOT NULL,
    start_date      DATETIME2 NOT NULL,
    end_date        DATETIME2 NOT NULL,
    status          NVARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    notes           NVARCHAR(1000) NULL,
    created_at      DATETIME2 NULL,
    updated_at      DATETIME2 NULL,

    CONSTRAINT FK_CW_product      FOREIGN KEY (product_id)    REFERENCES PRODUCT(product_id),
    CONSTRAINT FK_CW_order        FOREIGN KEY (order_id)      REFERENCES [ORDER](order_id),
    CONSTRAINT FK_CW_bulk_order   FOREIGN KEY (bulk_order_id) REFERENCES BULK_ORDER(bulk_order_id),
    CONSTRAINT FK_CW_user         FOREIGN KEY (user_id)       REFERENCES USERS(user_id),

    -- Ràng buộc: không được có cả order_id VÀ bulk_order_id cùng lúc
    CONSTRAINT CHK_CW_single_source CHECK (
        NOT (order_id IS NOT NULL AND bulk_order_id IS NOT NULL)
    )
);

-- Indexes
CREATE INDEX idx_cw_user_id      ON CUSTOMER_WARRANTY (user_id);
CREATE INDEX idx_cw_product_id   ON CUSTOMER_WARRANTY (product_id);
CREATE INDEX idx_cw_order_id     ON CUSTOMER_WARRANTY (order_id);
CREATE INDEX idx_cw_bulk_order_id ON CUSTOMER_WARRANTY (bulk_order_id);
