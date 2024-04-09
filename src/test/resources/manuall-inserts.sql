-- Inserting mock data into the User table
INSERT INTO "user" (user_id, username)
VALUES
    (101, 'user101'), -- User ID 101, Username 'user101'
    (102, 'user102'), -- User ID 102, Username 'user102'
    (103, 'user103'); -- User ID 103, Username 'user103'


    -- Inserting mock data into the BankAccount table
INSERT INTO bank_account (account_id, user_id, balance)
VALUES
    (1, 101, 5000.00), -- Account ID 1, User ID 101, Balance 5000.00
    (2, 102, 3000.00), -- Account ID 2, User ID 102, Balance 3000.00
    (3, 103, 7000.00); -- Account ID 3, User ID 103, Balance 7000.00

    -- Inserting mock data into the Card table
INSERT INTO card (card_id, card_number, account_id)
VALUES
    (1, '1234567890123456', 1), -- Card ID 1, Card Number '1234567890123456', Account ID 1
    (2, '2345678901234567', 1), -- Card ID 2, Card Number '2345678901234567', Account ID 1
    (3, '3456789012345678', 2); -- Card ID 3, Card Number '3456789012345678', Account ID 2