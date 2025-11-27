-- Flyway baseline migration to create trades table
CREATE TABLE IF NOT EXISTS trades (
  trade_id varchar NOT NULL,
  version integer NOT NULL,
  counterpartyid varchar,
  bookid varchar,
  maturitydate date NOT NULL,
  createddate timestamp NOT NULL,
  expired boolean NOT NULL default false,
  PRIMARY KEY (trade_id, version)
);
CREATE INDEX IF NOT EXISTS idx_trades_tradeid_version_desc ON trades (trade_id, version DESC);

CREATE TABLE IF NOT EXISTS idempotency_keys (
	id varchar PRIMARY KEY,
	status_code integer,
	response_body text,
	created_at timestamp
);
