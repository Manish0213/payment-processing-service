-- =========================================================
-- DATABASE RESET
-- =========================================================

DROP DATABASE IF EXISTS my_payments;

CREATE DATABASE my_payments;

-- =========================================================
-- USER RESET
-- =========================================================

DROP USER IF EXISTS 'payments'@'%';

CREATE USER 'payments'@'%' IDENTIFIED BY 'payments';

-- =========================================================
-- GRANT PERMISSIONS
-- =========================================================

GRANT ALL PRIVILEGES ON my_payments.* TO 'payments'@'%';

FLUSH PRIVILEGES;

-- =========================================================
-- USE DATABASE
-- =========================================================

USE my_payments;

-- =========================================================
-- Transaction_Status
-- =========================================================

CREATE TABLE Transaction_Status (
    id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    creationDate TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),

    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================================================
-- Provider
-- =========================================================

CREATE TABLE Provider (
    id INT NOT NULL,
    providerName VARCHAR(100) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    creationDate TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),

    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================================================
-- Payment_Type
-- =========================================================

CREATE TABLE Payment_Type (
    id INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    creationDate TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),

    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================================================
-- Payment_Method
-- =========================================================

CREATE TABLE Payment_Method (
    id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    creationDate TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),

    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================================================
-- Transaction
-- =========================================================

CREATE TABLE Transaction (
    id INT NOT NULL AUTO_INCREMENT,
    userId INT NOT NULL,

    paymentTypeId INT NOT NULL,
    paymentMethodId INT NOT NULL,
    providerId INT NOT NULL,

    txnStatusId INT NOT NULL,

    amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) NOT NULL,

    merchantTxnReference VARCHAR(100) NOT NULL,
    txnReference VARCHAR(100) NOT NULL,
    providerReference VARCHAR(100) DEFAULT NULL,

    errorCode VARCHAR(100) DEFAULT NULL,
    errorDescription VARCHAR(500) DEFAULT NULL,

    retry INT NOT NULL DEFAULT 0,

    creationDate TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),

    PRIMARY KEY (id),

    UNIQUE KEY uk_transaction_txn_reference (txnReference),

    KEY idx_transaction_payment_method_id (paymentMethodId),
    KEY idx_transaction_payment_type_id (paymentTypeId),
    KEY idx_transaction_provider_id (providerId),
    KEY idx_transaction_txn_status_id (txnStatusId),

    CONSTRAINT fk_transaction_payment_method
        FOREIGN KEY (paymentMethodId)
        REFERENCES Payment_Method(id),

    CONSTRAINT fk_transaction_payment_type
        FOREIGN KEY (paymentTypeId)
        REFERENCES Payment_Type(id),

    CONSTRAINT fk_transaction_provider
        FOREIGN KEY (providerId)
        REFERENCES Provider(id),

    CONSTRAINT fk_transaction_status
        FOREIGN KEY (txnStatusId)
        REFERENCES Transaction_Status(id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;