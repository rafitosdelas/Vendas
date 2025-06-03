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
        txtId.setText(""); // ID Venda (gerado pelo banco)
        txtValorTotal.setText("0.00"); // Valor total
        txtValorTotal.setEditable(false);

        cmbCliente.setSelectedIndex(-1); // Cliente
        cmbPgto.setSelectedIndex(0); // Forma de Pagamento
        cmbProduto.setSelectedIndex(-1); // Produto
        spnQnt.setValue(1);       // Quantidade

        carrinhoDeCompras.clear();
        atualizarTabelaCarrinho();
        atualizarValorTotalVenda();

        btnRemover.setEnabled(false); // Botão Remover Item
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
        cmbCliente.setModel(model);
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
        cmbProduto.setModel(model);
    }

    private void carregarFormasPagamentoComboBox() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("Dinheiro");
        model.addElement("Cartão de Crédito");
        model.addElement("Cartão de Débito");
        model.addElement("PIX");
        cmbPgto.setModel(model);
    }

    // Dentro da sua classe CadastroVendas.java

    private void adicionarItemAoCarrinho() {
        if (cmbProduto.getSelectedIndex() <= 0) { 
            JOptionPane.showMessageDialog(this, "Selecione um produto!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int quantidadeParaAdicionar;
        try {
            quantidadeParaAdicionar = (Integer) spnQnt.getValue();
            if (quantidadeParaAdicionar <= 0) {
                JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero!", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String produtoSelecionadoTexto = (String) cmbProduto.getSelectedItem();
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
        cmbProduto.setSelectedIndex(0); // Produto
        spnQnt.setValue(1);       // Quantidade
        cmbProduto.requestFocus();
    }

    private void removerItemDoCarrinho() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < carrinhoDeCompras.size()) {
            carrinhoDeCompras.remove(selectedRow);
            atualizarTabelaCarrinho();
            atualizarValorTotalVenda();
            btnRemover.setEnabled(false); 
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
        txtValorTotal.setText(total.setScale(2, RoundingMode.HALF_UP).toString()); 
    }

    private void finalizarVenda() {
        Integer idClienteParaVenda; 
        Clientes clienteSelecionado = null; 

        if (carrinhoDeCompras.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há itens no carrinho para finalizar a venda!", "Validação da Venda", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cmbPgto.getSelectedItem() == null || cmbPgto.getSelectedIndex() < 0) { 
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma forma de pagamento!", "Validação da Venda", JOptionPane.WARNING_MESSAGE);
            cmbPgto.requestFocus();
            return;
        }

        // Lógica para pegar o cliente:
        if (cmbCliente.getSelectedIndex() <= 0) {
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
            String clienteSelecionadoTexto = (String) cmbCliente.getSelectedItem();
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
            novaVenda.setFormaPagamento((String) cmbPgto.getSelectedItem());
            novaVenda.setValorTotal(new BigDecimal(txtValorTotal.getText().replace(",", ".")));

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
            txtId.setText(String.valueOf(idDaVendaGerado)); 

            btnInserir.setEnabled(false); // Botão Adicionar Item
            btnRemover.setEnabled(false); // Botão Remover Item
            btnFinalizar.setEnabled(false); // Botão Finalizar Venda

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Erro ao converter ID do cliente.\nDetalhe: " + nfe.getMessage(), "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao finalizar venda: \n" + e.getMessage(), "Erro Crítico na Venda", JOptionPane.ERROR_MESSAGE);
        }
    }                       
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblId = new javax.swing.JLabel();
        lblCliente = new javax.swing.JLabel();
        lblFormaPgto = new javax.swing.JLabel();
        lblProduto = new javax.swing.JLabel();
        lblValorTotal = new javax.swing.JLabel();
        lblQnt = new javax.swing.JLabel();
        txtValorTotal = new javax.swing.JTextField();
        txtId = new javax.swing.JTextField();
        cmbCliente = new javax.swing.JComboBox<>();
        cmbPgto = new javax.swing.JComboBox<>();
        cmbProduto = new javax.swing.JComboBox<>();
        spnQnt = new javax.swing.JSpinner();
        btnInserir = new javax.swing.JButton();
        btnRemover = new javax.swing.JButton();
        btnFinalizar = new javax.swing.JButton();
        btnNovaVenda = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVendas = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnSair = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro de Vendas");
        setBackground(new java.awt.Color(255, 255, 255));
        setSize(new java.awt.Dimension(0, 0));

        lblId.setText("ID:");

        lblCliente.setText("Cliente");

        lblFormaPgto.setText("Forma pgto");

        lblProduto.setText("Produto");

        lblValorTotal.setText("VALOR TOTAL: R$");

        lblQnt.setText("Qnt");

        txtValorTotal.setEditable(false);
        txtValorTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtValorTotalActionPerformed(evt);
            }
        });

        txtId.setEditable(false);
        txtId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdActionPerformed(evt);
            }
        });

        cmbCliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbClienteActionPerformed(evt);
            }
        });

        cmbPgto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbPgto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPgtoActionPerformed(evt);
            }
        });

        cmbProduto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnInserir.setBackground(new java.awt.Color(153, 204, 255));
        btnInserir.setText("Inserir");
        btnInserir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirActionPerformed(evt);
            }
        });

        btnRemover.setBackground(new java.awt.Color(153, 204, 255));
        btnRemover.setText("Remover");
        btnRemover.setEnabled(false);
        btnRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverActionPerformed(evt);
            }
        });

        btnFinalizar.setBackground(new java.awt.Color(153, 204, 255));
        btnFinalizar.setText("Finalizar");
        btnFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarActionPerformed(evt);
            }
        });

        btnNovaVenda.setBackground(new java.awt.Color(153, 204, 255));
        btnNovaVenda.setText("Nova Venda");
        btnNovaVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovaVendaActionPerformed(evt);
            }
        });

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
        tblVendas.setViewportView(jTable1);

        jScrollPane2.setViewportView(tblVendas);

        btnSair.setBackground(new java.awt.Color(153, 204, 255));
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblFormaPgto, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
                                .addGap(30, 30, 30)
                                .addComponent(cmbProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(lblQnt)
                                .addGap(10, 10, 10)
                                .addComponent(spnQnt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(btnInserir))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblId, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                                    .addComponent(lblCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cmbPgto, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cmbCliente, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtId))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnRemover)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblValorTotal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(240, 240, 240)
                                .addComponent(btnSair)
                                .addGap(15, 15, 15)
                                .addComponent(btnNovaVenda)
                                .addGap(15, 15, 15)
                                .addComponent(btnFinalizar)))))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblId)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCliente)
                    .addComponent(cmbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFormaPgto)
                    .addComponent(cmbPgto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProduto)
                    .addComponent(cmbProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQnt)
                    .addComponent(spnQnt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnInserir)
                    .addComponent(btnRemover))
                .addGap(20, 20, 20)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblValorTotal)
                    .addComponent(txtValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFinalizar)
                    .addComponent(btnNovaVenda)
                    .addComponent(btnSair))
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
        
    private void btnInserirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirActionPerformed
        adicionarItemAoCarrinho();
    }//GEN-LAST:event_btnInserirActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
                if (jTable1.getSelectedRow() != -1) {
            btnRemover.setEnabled(true);
        } else {
            btnRemover.setEnabled(false);
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void txtIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdActionPerformed
        // ID da Venda
    }//GEN-LAST:event_txtIdActionPerformed

    private void cmbClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbClienteActionPerformed
       
    }//GEN-LAST:event_cmbClienteActionPerformed

    private void cmbPgtoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPgtoActionPerformed
   
    }//GEN-LAST:event_cmbPgtoActionPerformed

    private void btnRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed
        removerItemDoCarrinho();
    }//GEN-LAST:event_btnRemoverActionPerformed

    private void txtValorTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValorTotalActionPerformed

    }//GEN-LAST:event_txtValorTotalActionPerformed

    private void btnFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarActionPerformed
        finalizarVenda();
    }//GEN-LAST:event_btnFinalizarActionPerformed

    private void btnNovaVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovaVendaActionPerformed
        iniciarNovaVenda();
        btnInserir.setEnabled(true);
        btnFinalizar.setEnabled(true); 
    }//GEN-LAST:event_btnNovaVendaActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFinalizar;
    private javax.swing.JButton btnInserir;
    private javax.swing.JButton btnNovaVenda;
    private javax.swing.JButton btnRemover;
    private javax.swing.JButton btnSair;
    private javax.swing.JComboBox<String> cmbCliente;
    private javax.swing.JComboBox<String> cmbPgto;
    private javax.swing.JComboBox<String> cmbProduto;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblFormaPgto;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblProduto;
    private javax.swing.JLabel lblQnt;
    private javax.swing.JLabel lblValorTotal;
    private javax.swing.JSpinner spnQnt;
    private javax.swing.JScrollPane tblVendas;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtValorTotal;
    // End of variables declaration//GEN-END:variables
}