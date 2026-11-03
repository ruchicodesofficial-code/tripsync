ALTER TABLE invitations
ADD COLUMN token_hash
VARCHAR(62)NULL;

CREATE UNIQUE INDEX
uk_invitations_token_hash
ON invitations(token_hash);