package projeto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import projeto.controller.ClientesJpaController;
import projeto.controller.ItensVendaJpaController;
import projeto.controller.ProdutosJpaController;
import projeto.controller.VendasJpaController;
import projeto.entities.Clientes;
import projeto.entities.ItensVenda;
import projeto.entities.Produtos;
import projeto.entities.Vendas;

public class Consulta extends javax.swing.JFrame {

    private EntityManagerFactory emf;
    private VendasJpaController vendaController;
    private ClientesJpaController clienteController;
    private ItensVendaJpaController itemVendaController;
    private ProdutosJpaController produtoController;
    private DefaultTableModel tableModelVendas;
    private DefaultTableModel tableModelItensVenda;

    public Consulta() {
        initComponents(); 
        
        try {
            emf = Persistence.createEntityManagerFactory("projetoPU");
            vendaController = new VendasJpaController(emf);
            clienteController = new ClientesJpaController(emf);
            itemVendaController = new ItensVendaJpaController(emf);
            produtoController = new ProdutosJpaController(emf);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro crítico ao inicializar componentes JPA: \n" + e.getMessage(), "Erro de Inicialização", JOptionPane.ERROR_MESSAGE);
            btnBuscar.setEnabled(false);
            btnLimpar.setEnabled(false);
            return;
        }

        tableModelVendas = (DefaultTableModel) tblVendas.getModel();
        configurarTabelaItensVenda();

        carregarClientesComboBoxFiltro();
        buscarVendas();

        adicionarListenerTabelaVendas(); 
        
        this.setLocationRelativeTo(null);
    }

    private void configurarTabelaItensVenda() {
        String[] colunasItens = {"Produto", "Quantidade", "Preço Unit. (Venda)", "Subtotal Item"};
        tableModelItensVenda = new DefaultTableModel(new Object[][]{}, colunasItens) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return String.class;     // Produto
                    case 1: return Integer.class;    // Quantidade
                    case 2: return BigDecimal.class; // Preço Unit.
                    case 3: return BigDecimal.class; // Subtotal
                    default: return Object.class;
                }
            }
        };
        tblDetalhesVendas.setModel(tableModelItensVenda); // itens
    }

    private void carregarClientesComboBoxFiltro() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("Todos os Clientes");
        try {
            List<Clientes> clientes = clienteController.findClientesEntities();
            for (Clientes cliente : clientes) {
                model.addElement(cliente.getIdCliente() + " - " + cliente.getNome());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes para o filtro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        cmbCliente.setModel(model);
    }

    private void buscarVendas() {
        tableModelVendas.setRowCount(0);
        if (tableModelItensVenda != null) {
            tableModelItensVenda.setRowCount(0);
        }

        Date dataInicial = null;
        Date dataFinal = null;
        Integer idClienteFiltro = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        try {
            if (!txtDataInicial.getText().trim().isEmpty()) {
                dataInicial = sdf.parse(txtDataInicial.getText().trim());
            }
            if (!txtDataFinal.getText().trim().isEmpty()) {
                dataFinal = sdf.parse(txtDataFinal.getText().trim());
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/mm/aaaa.", "Erro de Data", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cmbCliente.getSelectedIndex() > 0) {
            String clienteSelecionadoTexto = (String) cmbCliente.getSelectedItem();
            try {
                idClienteFiltro = Integer.parseInt(clienteSelecionadoTexto.split(" - ")[0]);
            } catch (Exception e) {
                 JOptionPane.showMessageDialog(this, "Erro ao obter ID do cliente para filtro.", "Erro Filtro", JOptionPane.ERROR_MESSAGE);
            }
        }

        try {
            List<Vendas> listaDeVendas = vendaController.findVendasComFiltros(dataInicial, dataFinal, idClienteFiltro);

            if (listaDeVendas != null && !listaDeVendas.isEmpty()) {
                for (Vendas venda : listaDeVendas) {
                    String nomeCliente;
                    int clienteIdDaVenda = venda.getIdCliente();
                    final int ID_CONSUMIDOR_FINAL_EXEMPLO = 1; 

                    Clientes clienteDaVenda = clienteController.findClientes(clienteIdDaVenda);
                    if (clienteDaVenda != null) {
                        nomeCliente = clienteDaVenda.getNome();
                    } else {
                        if (clienteIdDaVenda == ID_CONSUMIDOR_FINAL_EXEMPLO) { 
                            nomeCliente = "Consumidor Final";
                        } else {
                            nomeCliente = "Cliente ID: " + clienteIdDaVenda + " (Nome não disponível)";
                        }
                    }
                    
                    String dataFormatada = (venda.getDataVenda() != null) ? sdf.format(venda.getDataVenda()) : "N/A";
                    
                    tableModelVendas.addRow(new Object[]{
                        venda.getIdVenda(),
                        dataFormatada,
                        nomeCliente,
                        venda.getValorTotal(),
                        venda.getFormaPagamento()
                    });
                }
            } else {
                 JOptionPane.showMessageDialog(this, "Nenhuma venda encontrada para os filtros aplicados.", "Sem Resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar vendas: \n" + e.getMessage(), "Erro na Consulta", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); 
        }
    }
    
    private void adicionarListenerTabelaVendas() {
        tblVendas.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && tblVendas.getSelectedRow() != -1) {
                    int linhaSelecionada = tblVendas.getSelectedRow();
                    Object idVendaObjeto = tblVendas.getValueAt(linhaSelecionada, 0);

                    if (idVendaObjeto != null) {
                        try {
                            int idVenda = Integer.parseInt(idVendaObjeto.toString());
                            carregarItensDaVendaSelecionada(idVenda);
                        } catch (NumberFormatException ex) {
                            tableModelItensVenda.setRowCount(0); 
                        }
                    }
                } else if (tblVendas.getSelectedRow() == -1) {
                     tableModelItensVenda.setRowCount(0);
                }
            }
        });
    }

    private void carregarItensDaVendaSelecionada(int idVenda) {
        tableModelItensVenda.setRowCount(0); 

        try {
            List<ItensVenda> itensDaVenda = itemVendaController.findItensVendaByIdVenda(idVenda);

            if (itensDaVenda != null) {
                for (ItensVenda item : itensDaVenda) {
                    int produtoId = item.getIdProduto();
                    Produtos produto = produtoController.findProdutos(produtoId);

                    String nomeProduto = "Produto Desconhecido";
                    if (produto != null) {
                        nomeProduto = produto.getNome();
                    }

                    int quantidade = item.getQuantidade();
                    BigDecimal precoUnitario = item.getPrecoUnitarioMomento();
                    BigDecimal subtotalItem = precoUnitario.multiply(new BigDecimal(quantidade)).setScale(2, RoundingMode.HALF_UP);

                    tableModelItensVenda.addRow(new Object[]{
                        nomeProduto,
                        quantidade,
                        precoUnitario,
                        subtotalItem
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar os itens da venda selecionada:\n" + e.getMessage(), "Erro Detalhes Venda", JOptionPane.ERROR_MESSAGE);   
        }
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblDataInicial = new javax.swing.JLabel();
        lblDataFinal = new javax.swing.JLabel();
        lblCliente = new javax.swing.JLabel();
        lblDataExemplo = new javax.swing.JLabel();
        txtDataInicial = new javax.swing.JTextField();
        txtDataFinal = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        cmbCliente = new javax.swing.JComboBox<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblVendas = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblDetalhesVendas = new javax.swing.JTable();
        btnSair = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Consultar Venda");
        setBackground(new java.awt.Color(255, 255, 255));

        lblDataInicial.setText("Data inicial");

        lblDataFinal.setText("Data final");

        lblCliente.setText("Cliente");

        lblDataExemplo.setText("dd/MM/aaaa");

        txtDataInicial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDataInicialActionPerformed(evt);
            }
        });

        btnBuscar.setBackground(new java.awt.Color(153, 204, 255));
        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        btnLimpar.setBackground(new java.awt.Color(153, 204, 255));
        btnLimpar.setText("Limpar");
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        cmbCliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbClienteActionPerformed(evt);
            }
        });

        tblVendas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID venda", "Data", "Nome Clietne", "Valor Total", "Forma Pgto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
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
        tblVendas.setMaximumSize(new java.awt.Dimension(400, 260));
        tblVendas.setPreferredSize(new java.awt.Dimension(400, 260));
        tblVendas.getTableHeader().setReorderingAllowed(false);
        tblVendas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVendasMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblVendas);

        tblDetalhesVendas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Produto", "Quantidade", "Preço Unt", "Subtotal do item"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetalhesVendas.setMaximumSize(new java.awt.Dimension(400, 260));
        tblDetalhesVendas.setPreferredSize(new java.awt.Dimension(400, 260));
        tblDetalhesVendas.getTableHeader().setReorderingAllowed(false);
        tblDetalhesVendas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDetalhesVendasMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tblDetalhesVendas);

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
                    .addComponent(btnSair)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane4)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lblCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblDataInicial, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblDataFinal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(30, 30, 30)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtDataInicial, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                                .addComponent(txtDataFinal, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cmbCliente, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(lblDataExemplo))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(250, 250, 250)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btnLimpar)
                                        .addComponent(btnBuscar)))))
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING)))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDataInicial)
                    .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDataExemplo))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDataFinal)
                    .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCliente)
                    .addComponent(cmbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpar))
                .addGap(20, 20, 20)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnSair)
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
        txtDataInicial.setText("");
        txtDataFinal.setText("");
        if (cmbCliente.getItemCount() > 0) {
            cmbCliente.setSelectedIndex(0);
        }
        buscarVendas();
    }//GEN-LAST:event_btnLimparActionPerformed
    
    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        buscarVendas();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void txtDataInicialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDataInicialActionPerformed

    }//GEN-LAST:event_txtDataInicialActionPerformed

    private void tblDetalhesVendasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDetalhesVendasMouseClicked

    }//GEN-LAST:event_tblDetalhesVendasMouseClicked

    private void tblVendasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVendasMouseClicked
     
    }//GEN-LAST:event_tblVendasMouseClicked

    private void cmbClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbClienteActionPerformed
    
    }//GEN-LAST:event_cmbClienteActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnSair;
    private javax.swing.JComboBox<String> cmbCliente;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblDataExemplo;
    private javax.swing.JLabel lblDataFinal;
    private javax.swing.JLabel lblDataInicial;
    private javax.swing.JTable tblDetalhesVendas;
    private javax.swing.JTable tblVendas;
    private javax.swing.JTextField txtDataFinal;
    private javax.swing.JTextField txtDataInicial;
    // End of variables declaration//GEN-END:variables
}