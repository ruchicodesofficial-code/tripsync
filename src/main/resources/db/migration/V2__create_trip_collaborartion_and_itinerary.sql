CREATE TABLE trip_members(
id BIGINT NOT NULL AUTO_INCREMENT,
trip_id BIGINT NOT NULL,
user_id BIGINT NOT NULL,
role VARCHAR(20) NOT NULL,
joined_at DATETIME(6) NOT NULL,
created_at DATETIME(6) NOT NULL,
updated_at DATETIME(6) NOT NULL,
CONSTRAINT pk_trip_members PRIMARY KEY(id),
CONSTRAINT uk_trip_members_trip_user UNIQUE(trip_id,user_id),
CONSTRAINT fk_trip_members_trip FOREIGN KEY(trip_id) REFERENCES trips(id),
CONSTRAINT fk_trip_members_user FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE invitations(
id BIGINT NOT NULL AUTO_INCREMENT,
trip_id BIGINT NOT NULL,
invited_by_id BIGINT NOT NULL,
invitee_email VARCHAR(150) NOT NULL,
proposed_role VARCHAR(20) NOT NULL,
status VARCHAR(20) NOT NULL,
expires_at DATETIME(6) NOT NULL,
created_at DATETIME(6) NOT NULL,
updated_at DATETIME(6) NOT NULL,
CONSTRAINT pk_trip_invitations PRIMARY KEY(id),
CONSTRAINT fk_trip_invitations_trip FOREIGN KEY(trip_id) REFERENCES trips(id),
CONSTRAINT fk_trip_invitations_invited_by FOREIGN KEY(invited_by_id) REFERENCES users(id)
);

CREATE TABLE itineraries(
id BIGINT NOT NULL AUTO_INCREMENT,
trip_id BIGINT NOT NULL,
itinerary_date DATE NOT NULL,
notes VARCHAR(1000),
created_at DATETIME(6) NOT NULL,
updated_at DATETIME(6) NOT NULL,
CONSTRAINT pk_itineraries PRIMARY KEY(id),
CONSTRAINT uk_itineraries_trip_date UNIQUE(trip_id,itinerary_date),
CONSTRAINT fk_itineraries_trip FOREIGN KEY(trip_id) REFERENCES trips(id)
);

CREATE TABLE activities(
id BIGINT NOT NULL AUTO_INCREMENT,
itinerary_id BIGINT NOT NULL,
title VARCHAR(150) NOT NULL,
description VARCHAR(100),
location VARCHAR(250),
start_time TIME,
end_time TIME,
estimated_cost DECIMAL(12,2),
display_order INT NOT NULL,
created_at DATETIME(6) NOT NULL,
updated_at DATETIME(6) NOT NULL,
CONSTRAINT pk_activities PRIMARY KEY(id),
CONSTRAINT fk_activities_itinerary FOREIGN KEY(itinerary_id) REFERENCES itineraries(id)
);

CREATE INDEX
idx_trip_members_user_id
ON trip_members(user_id);

CREATE INDEX
idx_trip_invitations_email_status
ON invitations(invitee_email, status);

CREATE INDEX idx_itineraries_trip_date
ON itineraries(trip_id,itinerary_date);

CREATE INDEX
idx_activities_itinerary_order
ON activities(itinerary_id,display_order);


