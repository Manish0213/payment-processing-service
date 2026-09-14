-- =========================================================
-- Payment_Method
-- =========================================================

INSERT INTO payments.Payment_Method
    (id, name, status, creationDate)
VALUES
    (1, 'APM', 1, CURRENT_TIMESTAMP(2));


-- =========================================================
-- Payment_Type
-- =========================================================

INSERT INTO payments.Payment_Type
    (id, type, status, creationDate)
VALUES
    (1, 'SALE', 1, CURRENT_TIMESTAMP(2));


-- =========================================================
-- Provider
-- =========================================================

INSERT INTO payments.Provider
    (id, providerName, status, creationDate)
VALUES
    (1, 'PAYPAL', 1, CURRENT_TIMESTAMP(2));


-- =========================================================
-- Transaction_Status
-- =========================================================

INSERT INTO payments.Transaction_Status
    (id, name, status, creationDate)
VALUES
    (1, 'CREATED',   1, CURRENT_TIMESTAMP(2)),
    (2, 'INITIATED', 1, CURRENT_TIMESTAMP(2)),
    (3, 'PENDING',   1, CURRENT_TIMESTAMP(2)),
    (4, 'APPROVED',  1, CURRENT_TIMESTAMP(2)),
    (5, 'SUCCESS',   1, CURRENT_TIMESTAMP(2)),
    (6, 'FAILED',    1, CURRENT_TIMESTAMP(2));