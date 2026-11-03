CREATE TABLE documents(
id BIGINT NOT NULL AUTO_INCREMENT,
trip_id BIGINT NOT NULL,
uploaded_by_id BIGINT NOT NULL,
original_filename VARCHAR(255) NOT NULL,
storage_key VARCHAR(100) NOT NULL,
content_type VARCHAR(150) NOT NULL,
file_size BIGINT NOT NULL,
created_at DATETIME(6) NOT NULL,
updated_at DATETIME(6) NOT NULL,
CONSTRAINT pk_documents PRIMARY KEY(id),
CONSTRAINT uk_documents_storage_key UNIQUE(storage_key),
CONSTRAINT fk_documents_trip FOREIGN KEY(trip_id) REFERENCES trips(id),
CONSTRAINT fk_documents_uploaded_by FOREIGN KEY(uploaded_by_id) REFERENCES users(id),
CONSTRAINT chk_documents_file_size CHECK(file_size>0)
);

CREATE INDEX
idx_documents_trip_created
ON documents(trip_id,created_at);

CREATE INDEX
idx_documents_uploaded_by
ON documents(uploaded_by_id);