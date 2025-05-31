package projeto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import projeto.entities.Clientes;
import projeto.controller.ClientesJpaController;
import projeto.entities.Produtos;
import projeto.controller.ProdutosJpaController;
import projeto.entities.Vendas;
import projeto.controller.VendasJpaController; 
import projeto.entities.ItensVenda;
import projeto.controller.ItensVendaJpaController;

public class CadastroVendas extends javax.swing.JFrame {

    private EntityManagerFactory emf;
    private ClientesJpaController clienteController;
    private ProdutosJpaController produtoController;
    private VendasJpaController vendaController;
    private ItensVendaJpaController itemVendaController;

    private List<ItemVendaCarrinho> carrinhoDeCompras;
    private DefaultTableModel tableModelItensVenda;

    private static class ItemVendaCarrinho {
        Produtos produto;
        int quantidade;
        BigDecimal precoUnitarioNoMomento;
        BigDecimal subtotal;

        public ItemVendaCarrinho(Produtos produto, int quantidade, BigDecimal precoUnitarioNoMomento) {
            this.produto = produto;
            this.quantidade = quantidade;
            this.precoUnitarioNoMomento = precoUnitarioNoMomento;
            this.subtotal = precoUnitarioNoMomento.multiply(new BigDecimal(quantidade)).setScale(2, RoundingMode.HALF_UP);
        }
    }


        public CadastroVendas() {
        initComponents();
        this.setTitle("Registro de Vendas"); 

        try {
            emf = Persistence.createEntityManagerFactory("projetoPU");
            clienteController = new ClientesJpaController(emf);
            produtoController = new ProdutosJpaController(emf);
            vendaController = new VendasJpaController(emf);      
            itemVendaController = new ItensVendaJpaController(emf);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar com o banco: " + e.getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        carrinhoDeCompras = new ArrayList<>();
        configurarTabelaItensVenda();
        carregarClientesComboBox();
        carregarProdutosComboBox();
        carregarFormasPagamentoComboBox();

        iniciarNovaVenda();
        setLocationRelativeTo(null);
    }

    private void configurarTabelaItensVenda() {
        tableModelItensVenda = (DefaultTableModel) jTable1.getModel();
    }


    private void iniciarNovaVenda() {
        jTextField1.setText(""); // ID Venda (gerado pelo banco)
        jTextField3.setText(getCurrentFormattedDate()); // Data atual
        jTextField3.setEditable(false);
        jTextField2.setText("0.00"); // Valor total
        jTextField2.setEditable(false);

        jComboBox1.setSelectedIndex(-1); // Cliente
        jComboBox2.setSelectedIndex(0); // Forma de Pagamento
        jComboBox3.setSelectedIndex(-1); // Produto
        jSpinner1.setValue(1);       // Quantidade

        carrinhoDeCompras.clear();
        atualizarTabelaCarrinho();
        atualizarValorTotalVenda();

        jButton4.setEnabled(false); // Botão Remover Item
    }

    private String getCurrentFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    private void carregarClientesComboBox() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("-- Selecione um Cliente --");
        try {
            List<Clientes> clientes = clienteController.findClientesEntities();
            for (Clientes cliente : clientes) {
                model.addElement(cliente.getIdCliente() + " - " + cliente.getNome());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        jComboBox1.setModel(model);
    }

    private void carregarProdutosComboBox() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("-- Selecione um Produto --");
        try {
            List<Produtos> produtos = produtoController.findProdutosEntities();
            for (Produtos produto : produtos) {
                model.addElement(produto.getIdProduto() + " - " + produto.getNome() + " (R$ " + produto.getPrecoVenda() + ")");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        jComboBox3.setModel(model);
    }

    private void carregarFormasPagamentoComboBox() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("Dinheiro");
        model.addElement("Cartão de Crédito");
        model.addElement("Cartão de Débito");
        model.addElement("PIX");
        jComboBox2.setModel(model);
    }

    // Dentro da sua classe CadastroVendas.java

    private void adicionarItemAoCarrinho() {
        if (jComboBox3.getSelectedIndex() <= 0) { 
            JOptionPane.showMessageDialog(this, "Selecione um produto!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int quantidadeParaAdicionar;
        try {
            quantidadeParaAdicionar = (Integer) jSpinner1.getValue();
            if (quantidadeParaAdicionar <= 0) {
                JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero!", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String produtoSelecionadoTexto = (String) jComboBox3.getSelectedItem();
            int idProduto = Integer.parseInt(produtoSelecionadoTexto.split(" - ")[0]);
            Produtos produtoDoBanco = produtoController.findProdutos(idProduto);

            if (produtoDoBanco == null) {
                JOptionPane.showMessageDialog(this, "Produto não encontrado no banco!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int estoqueAtualDoProduto = produtoDoBanco.getQuantidadeEstoque();
            int quantidadeJaNoCarrinhoParaEsteProduto = 0;
            ItemVendaCarrinho itemExistenteNoCarrinho = null;

            for (ItemVendaCarrinho item : carrinhoDeCompras) {
                if (item.produto.getIdProduto().equals(produtoDoBanco.getIdProduto())) {
                    quantidadeJaNoCarrinhoParaEsteProduto = item.quantidade;
                    itemExistenteNoCarrinho = item;
                    break;
                }
            }

            int quantidadeTotalDesejadaNoCarrinho = quantidadeJaNoCarrinhoParaEsteProduto + quantidadeParaAdicionar;

            // --- VERIFICAÇÃO DE ESTOQUE ---
            if (quantidadeTotalDesejadaNoCarrinho > estoqueAtualDoProduto) {
                String mensagem = "Estoque insuficiente para o produto: " + produtoDoBanco.getNome() + ".\n" +
                                  "Estoque atual: " + estoqueAtualDoProduto + " unidade(s).\n" +
                                  "Você já tem: " + quantidadeJaNoCarrinhoParaEsteProduto + " no carrinho e está tentando adicionar mais " + quantidadeParaAdicionar + ".\n" +
                                  "Total desejado: " + quantidadeTotalDesejadaNoCarrinho + " unidade(s).";
                if (quantidadeJaNoCarrinhoParaEsteProduto == 0) { // Se for a primeira vez adicionando
                     mensagem = "Estoque insuficiente para o produto: " + produtoDoBanco.getNome() + ".\n" +
                                "Estoque atual: " + estoqueAtualDoProduto + " unidade(s).\n" +
                                "Você está tentando adicionar: " + quantidadeParaAdicionar + " unidade(s).";
                }
                JOptionPane.showMessageDialog(this, mensagem, "Estoque Insuficiente", JOptionPane.WARNING_MESSAGE);
                return; // Impede a adição
            }
            // --- FIM DA VERIFICAÇÃO DE ESTOQUE ---

            if (itemExistenteNoCarrinho != null) {
                // Atualiza a quantidade do item existente
                itemExistenteNoCarrinho.quantidade = quantidadeTotalDesejadaNoCarrinho; 
                itemExistenteNoCarrinho.subtotal = itemExistenteNoCarrinho.precoUnitarioNoMomento.multiply(new BigDecimal(itemExistenteNoCarrinho.quantidade)).setScale(2, RoundingMode.HALF_UP);
            } else {
                ItemVendaCarrinho novoItem = new ItemVendaCarrinho(produtoDoBanco, quantidadeParaAdicionar, produtoDoBanco.getPrecoVenda());
                carrinhoDeCompras.add(novoItem);
            }

            atualizarTabelaCarrinho();
            atualizarValorTotalVenda();
            limparCamposAdicionarProduto();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Erro ao processar ID do produto. Verifique a seleção.", "Erro", JOptionPane.ERROR_MESSAGE);
        } 
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar item ao carrinho: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void limparCamposAdicionarProduto(){
        jComboBox3.setSelectedIndex(0); // Produto
        jSpinner1.setValue(1);       // Quantidade
        jComboBox3.requestFocus();
    }

    private void removerItemDoCarrinho() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < carrinhoDeCompras.size()) {
            carrinhoDeCompras.remove(selectedRow);
            atualizarTabelaCarrinho();
            atualizarValorTotalVenda();
            jButton4.setEnabled(false); 
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um item da lista para remover.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void atualizarTabelaCarrinho() {
        tableModelItensVenda.setRowCount(0);
        for (ItemVendaCarrinho item : carrinhoDeCompras) {
            tableModelItensVenda.addRow(new Object[]{
                item.produto.getIdProduto(),
                item.produto.getNome(),
                item.quantidade,
                item.precoUnitarioNoMomento,
                item.subtotal
            });
        }
    }

    private void atualizarValorTotalVenda() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemVendaCarrinho item : carrinhoDeCompras) {
            total = total.add(item.subtotal);
        }
        jTextField2.setText(total.setScale(2, RoundingMode.HALF_UP).toString()); 
    }

    private void finalizarVenda() {
        Integer idClienteParaVenda; 
        Clientes clienteSelecionado = null; 

        if (carrinhoDeCompras.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há itens no carrinho para finalizar a venda!", "Validação da Venda", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (jComboBox2.getSelectedItem() == null || jComboBox2.getSelectedIndex() < 0) { 
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma forma de pagamento!", "Validação da Venda", JOptionPane.WARNING_MESSAGE);
            jComboBox2.requestFocus();
            return;
        }

        // Lógica para pegar o cliente:
        if (jComboBox1.getSelectedIndex() <= 0) {
            final int ID_CONSUMIDOR_FINAL = 1; // "CONSUMIDOR FINAL"
            clienteSelecionado = clienteController.findClientes(ID_CONSUMIDOR_FINAL);
            if (clienteSelecionado == null) {
                JOptionPane.showMessageDialog(this, 
                    "Cliente padrão 'Consumidor Final' (ID: " + ID_CONSUMIDOR_FINAL + ") não encontrado no banco!\n" +
                    "Cadastre-o ou verifique o ID.", 
                    "Erro Cliente Padrão", JOptionPane.ERROR_MESSAGE);
                return;
            }
            idClienteParaVenda = clienteSelecionado.getIdCliente();
        } else {
            // Usuário selecionou um cliente específico
            String clienteSelecionadoTexto = (String) jComboBox1.getSelectedItem();
            int idClienteExtraido = Integer.parseInt(clienteSelecionadoTexto.split(" - ")[0]);
            clienteSelecionado = clienteController.findClientes(idClienteExtraido);

            if (clienteSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Cliente selecionado não foi encontrado no banco de dados!", "Erro Cliente", JOptionPane.ERROR_MESSAGE);
                return;
            }
            idClienteParaVenda = clienteSelecionado.getIdCliente();
        }

        try {
            Vendas novaVenda = new Vendas();
            novaVenda.setIdCliente(idClienteParaVenda); 
            novaVenda.setDataVenda(java.sql.Timestamp.valueOf(LocalDateTime.now()));
            novaVenda.setFormaPagamento((String) jComboBox2.getSelectedItem());
            novaVenda.setValorTotal(new BigDecimal(jTextField2.getText().replace(",", ".")));

            vendaController.create(novaVenda);
            if (novaVenda.getIdVenda() == null) {
                JOptionPane.showMessageDialog(this, 
                    "FALHA CRÍTICA: Não foi possível obter o ID da Venda após salvá-la.\n" +
                    "Os itens da venda não poderão ser registrados corretamente.",
                    "Erro Crítico de Persistência", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Integer idDaVendaGerado = novaVenda.getIdVenda();

            for (ItemVendaCarrinho itemCarrinho : carrinhoDeCompras) {
                ItensVenda itemVendaDB = new ItensVenda();
                itemVendaDB.setIdVenda(idDaVendaGerado);
                itemVendaDB.setIdProduto(itemCarrinho.produto.getIdProduto());
                itemVendaDB.setQuantidade(itemCarrinho.quantidade);
                itemVendaDB.setPrecoUnitarioMomento(itemCarrinho.precoUnitarioNoMomento);
                itemVendaController.create(itemVendaDB);

                try {
                    Produtos produtoParaAtualizarEstoque = itemCarrinho.produto;
                    int estoqueAtual = produtoParaAtualizarEstoque.getQuantidadeEstoque();
                    int quantidadeVendida = itemCarrinho.quantidade;
                    int novoEstoque = estoqueAtual - quantidadeVendida;
                    produtoParaAtualizarEstoque.setQuantidadeEstoque(novoEstoque);
                    produtoController.edit(produtoParaAtualizarEstoque);
                } catch (Exception exEstoque) {
                    JOptionPane.showMessageDialog(this,
                        "Venda registrada (ID: " + idDaVendaGerado + "), MAS FALHA ao atualizar estoque para o produto: " + itemCarrinho.produto.getNome() +
                        "\nErro: " + exEstoque.getMessage() +
                        "\nPor favor, ajuste o estoque ID " + itemCarrinho.produto.getIdProduto() + " manualmente para: " + (itemCarrinho.produto.getQuantidadeEstoque() - itemCarrinho.quantidade),
                        "Erro Crítico de Estoque", JOptionPane.ERROR_MESSAGE);
                }
            }

            JOptionPane.showMessageDialog(this, "Venda finalizada com sucesso! ID da Venda: " + idDaVendaGerado, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            jTextField1.setText(String.valueOf(idDaVendaGerado)); 

            jButton2.setEnabled(false); // Botão Adicionar Item
            jButton4.setEnabled(false); // Botão Remover Item
            jButton5.setEnabled(false); // Botão Finalizar Venda

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Erro ao converter ID do cliente.\nDetalhe: " + nfe.getMessage(), "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao finalizar venda: \n" + e.getMessage(), "Erro Crítico na Venda", JOptionPane.ERROR_MESSAGE);
        }
    }                       
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro de Vendas");
        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(800, 500));
        setSize(new java.awt.Dimension(0, 0));

        jLabel1.setText("ID:");

        jTextField1.setEditable(false);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Cliente");

        jLabel3.setText("Data");

        jLabel4.setText("Forma pgto");

        jButton2.setBackground(new java.awt.Color(153, 204, 255));
        jButton2.setText("Inserir");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel6.setText("Produto");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel7.setText("Qnt");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cod Prod", "Nome Prod", "Qtd", "Preço Unit", "Sub Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jScrollPane2.setViewportView(jScrollPane1);

        jButton4.setBackground(new java.awt.Color(153, 204, 255));
        jButton4.setText("Remover");
        jButton4.setEnabled(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel5.setText("VALOR TOTAL: R$");

        jTextField2.setEditable(false);
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(153, 204, 255));
        jButton5.setText("Finalizar");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(153, 204, 255));
        jButton6.setText("Nova Venda");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 324, Short.MAX_VALUE)
                        .addComponent(jButton6)
                        .addGap(26, 26, 26)
                        .addComponent(jButton5))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
                        .addGap(30, 30, 30)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jLabel7)
                        .addGap(10, 10, 10)
                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jButton2))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton4))
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                        .addGap(348, 348, 348)))
                .addGap(30, 30, 30))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)
                    .addComponent(jButton4))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5)
                    .addComponent(jButton6))
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
        
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        adicionarItemAoCarrinho();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
                if (jTable1.getSelectedRow() != -1) {
            jButton4.setEnabled(true);
        } else {
            jButton4.setEnabled(false);
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // ID da Venda
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
       
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
   
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        removerItemDoCarrinho();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed

    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        finalizarVenda();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        iniciarNovaVenda();
        jButton2.setEnabled(true);
        jButton5.setEnabled(true); 
    }//GEN-LAST:event_jButton6ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}