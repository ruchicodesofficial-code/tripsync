CREATE TABLE expenses(
id BIGINT NOT NULL AUTO_INCREMENT,
trip_id BIGINT NOT NULL,
paid_by_id BIGINT NOT NULL,
title VARCHAR(150) NOT NULL,
description VARCHAR(1000),
category VARCHAR(30) NOT NULL,
amount DECIMAL(12,2) NOT NULL,
expense_date DATE NOT NULL,
created_at DATETIME(6) NOT NULL,
updated_at DATETIME(6) NOT NULL,
CONSTRAINT pk_expenses PRIMARY KEY(id),
CONSTRAINT fk_expenses_trip FOREIGN KEY(trip_id) REFERENCES trips(id),
CONSTRAINT fk_expenses_paid_by FOREIGN KEY(paid_by_id) REFERENCES users(id),
CONSTRAINT chk_expenses_amount CHECK(amount>0)
);

CREATE TABLE expense_splits(
id BIGINT NOT NULL AUTO_INCREMENT,
expense_id BIGINT NOT NULL,
user_id BIGINT NOT NULL,
share_amount DECIMAL(12,2) NOT NULL,
created_at DATETIME(6) NOT NULL,
updated_at DATETIME(6) NOT NULL,
CONSTRAINT pk_expense_splits PRIMARY KEY(id),
CONSTRAINT uk_expense_splits_expense_user UNIQUE(expense_id,user_id),
CONSTRAINT fk_expense_splits_expense FOREIGN KEY(expense_id) REFERENCES expenses(id),
CONSTRAINT fk_expense_splits_user FOREIGN KEY(user_id) REFERENCES users(id),
CONSTRAINT chk_expense_splits_amount CHECK(share_amount>=0)
);

CREATE TABLE settlements(
id BIGINT NOT NULL AUTO_INCREMENT,
trip_id BIGINT NOT NULL,
payer_id BIGINT NOT NULL,
payee_id BIGINT NOT NULL,
amount DECIMAL(12,2) NOT NULL,
settled_at DATETIME(6) NOT NULL,
note VARCHAR(500),
created_at DATETIME(6) NOT NULL,
updated_at DATETIME(6) NOT NULL,
CONSTRAINT pk_settlements PRIMARY KEY(id),
CONSTRAINT fk_settlements_trip FOREIGN KEY(trip_id) REFERENCES trips(id),
CONSTRAINT fk_settlements_payer FOREIGN KEY(payer_id) REFERENCES users(id),
CONSTRAINT fk_settlements_payee FOREIGN KEY(payee_id) REFERENCES users(id),
CONSTRAINT chk_settlements_amount CHECK(amount>0),
CONSTRAINT chk_settlements_different_users CHECK(payer_id<>payee_id)
);

CREATE INDEX idx_expenses_trip_date
ON expenses(trip_id,expense_date);

CREATE INDEX idx_expenses_paid_by
ON expenses(paid_by_id);

CREATE INDEX idx_expenses_splits_user
ON expense_splits(user_id);

CREATE INDEX idx_settlements_trip_date
ON settlements(trip_id,settled_at);