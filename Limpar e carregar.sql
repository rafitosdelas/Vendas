
-- # ATENÇÃO: ESTE SCRIPT VAI LIMPAR TODAS AS TABELAS DO BANCO DE DADOS ATUAL

SET FOREIGN_KEY_CHECKS=0; 

TRUNCATE TABLE `clientes`;
TRUNCATE TABLE `itens_venda`;
TRUNCATE TABLE `produtos`;
TRUNCATE TABLE `vendas`;

SET FOREIGN_KEY_CHECKS=1;

-- Consumidor Final cliente ID 1
INSERT INTO clientes (id_cliente, nome, cpf_cnpj, email, telefone)
VALUES (1, 'Consumidor Final', '000.000.000-00', 'consumidor@final.com', '(00) 00000-0000');

-- Inserindo mais 7 Clientes de Exemplo
INSERT INTO clientes (nome, cpf_cnpj, email, telefone) VALUES
('Maria Silva', '111.111.111-11', 'maria.silva@email.com.br', '(11) 91111-1111'),
('João Santos', '222.222.222-22', 'joao.santos@email.com.br', '(21) 92222-2222'),
('Ana Oliveira', '333.333.333-33', 'ana.oliveira@email.com.br', '(31) 93333-3333'),
('Pedro Souza', '444.444.444-44', 'pedro.souza@email.com.br', '(41) 94444-4444'),
('Beatriz Costa', '555.555.555-55', 'beatriz.costa@email.com.br', '(51) 95555-5555'),
('Carlos Pereira', '666.666.666-66', 'carlos.pereira@email.com.br', '(61) 96666-6666'),
('Sofia Almeida', '777.777.777-77', 'sofia.almeida@email.com.br', '(71) 97777-7777');

-- Inserindo 7 Produtos de Exemplo
INSERT INTO produtos (nome, preco_venda, quantidade_estoque) VALUES
('Pão de Queijo Congelado (Pacote 1kg)', 22.50, 100),
('Café Especial em Grãos (500g)', 45.75, 50),
('Doce de Leite Artesanal (Pote 600g)', 32.00, 70),
('Cachaça Ouro Envelhecida (700ml)', 75.20, 30),
('Goiabada Cascão Cremosa (Lata 500g)', 18.50, 60),
('Tapioca Granulada Pronta (500g)', 9.90, 120),
('Açaí Puro Congelado (Polpa 1L)', 28.00, 80);

-- Inserindo 7 Vendas de Exemplo
INSERT INTO vendas (id_cliente, data_venda, forma_pagamento, valor_total) VALUES
(1, '2025-06-01 10:30:00', 'Pix', 68.25),                            -- Venda ID 1 (Consumidor Final compra Pão de Queijo e Café)
(2, '2025-06-01 14:15:00', 'Cartão de Débito', 32.00),             -- Venda ID 2 (Maria Silva compra Doce de Leite)
(3, '2025-06-02 09:00:00', 'Cartão de Crédito', 112.20),           -- Venda ID 3 (João Santos compra Cachaça e Goiabada)
(4, '2025-06-02 16:45:00', 'Pix', 29.70),                            -- Venda ID 4 (Ana Oliveira compra 3 Tapiocas)
(5, '2025-06-03 11:20:00', 'Cartão de Crédito', 84.50),           -- Venda ID 5 (Pedro Souza compra Açaí e Pão de Queijo)
(6, '2025-06-03 17:05:00', 'Boleto Bancário', 91.50),                -- Venda ID 6 (Beatriz Costa compra 2 Cafés)
(8, '2025-06-03 18:30:00', 'Pix', 58.40);                            -- Venda ID 7 (Sofia Almeida compra Doce de Leite, Goiabada e Tapioca)

-- Inserindo Itens para cada Venda
-- Itens da Venda 1 (Valor Total: 68.25)
INSERT INTO itens_venda (id_venda, id_produto, quantidade, preco_unitario_momento) VALUES
(1, 1, 1, 22.50), -- Pão de Queijo
(1, 2, 1, 45.75); -- Café

-- Itens da Venda 2 (Valor Total: 32.00)
INSERT INTO itens_venda (id_venda, id_produto, quantidade, preco_unitario_momento) VALUES
(2, 3, 1, 32.00); -- Doce de Leite

-- Itens da Venda 3 (Valor Total: 112.20)
INSERT INTO itens_venda (id_venda, id_produto, quantidade, preco_unitario_momento) VALUES
(3, 4, 1, 75.20), -- Cachaça
(3, 5, 2, 18.50); -- Goiabada (2 unidades)

-- Itens da Venda 4 (Valor Total: 29.70)
INSERT INTO itens_venda (id_venda, id_produto, quantidade, preco_unitario_momento) VALUES
(4, 6, 3, 9.90);  -- Tapioca (3 unidades)

-- Itens da Venda 5 (Valor Total: 84.50)
INSERT INTO itens_venda (id_venda, id_produto, quantidade, preco_unitario_momento) VALUES
(5, 7, 2, 28.00), -- Açaí (2 unidades)
(5, 1, 1, 22.50); -- Pão de Queijo

-- Itens da Venda 6 (Valor Total: 91.50)
INSERT INTO itens_venda (id_venda, id_produto, quantidade, preco_unitario_momento) VALUES
(6, 2, 2, 45.75); -- Café (2 unidades)

-- Itens da Venda 7 (Valor Total: 58.40)
INSERT INTO itens_venda (id_venda, id_produto, quantidade, preco_unitario_momento) VALUES
(7, 3, 1, 32.00), -- Doce de Leite
(7, 5, 1, 18.50), -- Goiabada
(7, 6, 1, 9.90);  -- Tapioca

-- Verificação final (opcional, mas bom para conferir)
SELECT 'Clientes após inserts:' AS Tabela;
SELECT * FROM clientes LIMIT 10;
SELECT 'Produtos após inserts:' AS Tabela;
SELECT * FROM produtos LIMIT 10;
SELECT 'Vendas após inserts:' AS Tabela;
SELECT * FROM vendas LIMIT 10;
SELECT 'Itens de Venda após inserts:' AS Tabela;
SELECT * FROM itens_venda LIMIT 15;