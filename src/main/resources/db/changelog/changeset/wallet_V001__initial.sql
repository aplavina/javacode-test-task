CREATE TABLE wallet (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    balance bigint NOT NULL CHECK (balance >= 0)
);