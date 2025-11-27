-- optional seed data
INSERT INTO trades (trade_id, version, counterpartyid, bookid, maturitydate, createddate, expired)
VALUES ('T1', 1, 'CP-1', 'B1', DATE '2099-01-01', TIMESTAMP '2025-01-01 00:00:00', false);