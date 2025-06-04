SET FOREIGN_KEY_CHECKS=0;

TRUNCATE TABLE `clientes`;
TRUNCATE TABLE `itens_venda`;
TRUNCATE TABLE `produtos`;
TRUNCATE TABLE `vendas`;

SET FOREIGN_KEY_CHECKS=1;

INSERT INTO clientes (id_cliente, nome, cpf_cnpj, email, telefone)
VALUES (1, 'Consumidor Final', '000.000.000-00', 'consumidor@final.com', '(00) 00000-0000');

SELECT * FROM clientes; -- Para verificar