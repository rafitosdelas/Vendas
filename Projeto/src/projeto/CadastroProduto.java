package projeto;

import java.math.BigDecimal;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import projeto.controller.ProdutosJpaController;
import projeto.controller.exceptions.NonexistentEntityException;
import projeto.entities.Produtos;

public class CadastroProduto extends javax.swing.JFrame {

    private EntityManagerFactory factory;

    public CadastroProduto() {
        initComponents();

        try {
            factory = Persistence.createEntityManagerFactory("projetoPU");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar com o banco de dados: \n" + e.getMessage(), "ERRO DE CONEXÃO JPA", JOptionPane.ERROR_MESSAGE);
            btnBuscar.setEnabled(false);
            btnInserir.setEnabled(false);
            btnLimpar.setEnabled(false);
            return;
        }

        configurarModeloTabelaProdutos();
        atualizarTabelaProdutos();
        limparCamposProduto();
    }

    private void configurarModeloTabelaProdutos() {
        DefaultTableModel model = (DefaultTableModel) tblProdutos.getModel();
    }

    private void atualizarTabelaProdutos() {
        if (factory == null || !factory.isOpen()) {
            JOptionPane.showMessageDialog(this, "EntityManagerFactory não está disponível para atualizar tabela.", "Erro JPA", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ProdutosJpaController localController = new ProdutosJpaController(factory);
        DefaultTableModel model = (DefaultTableModel) tblProdutos.getModel();
        model.setRowCount(0);

        try {
            List<Produtos> produtos = localController.findProdutosEntities();
            for (Produtos produto : produtos) {
                model.addRow(new Object[]{
                    produto.getIdProduto(),
                    produto.getNome(),
                    produto.getPrecoVenda(),
                    produto.getQuantidadeEstoque()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar tabela de produtos: \n" + e.getMessage(), "ERRO TABELA", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void limparCamposProduto() {
        txtId.setText("");
        txtNome.setText("");
        txtPreco.setText("");
        txtEstoque.setText("");

        btnBuscar.setEnabled(true);
        btnInserir.setEnabled(true);
        btnLimpar.setEnabled(true);
        btnAlterar.setEnabled(false);
        btnRemover.setEnabled(false);

        tblProdutos.clearSelection();
        atualizarTabelaProdutos();
    }

    private void preencherCamposComLinhaSelecionadaProduto(int rowIndex) {
        DefaultTableModel model = (DefaultTableModel) tblProdutos.getModel();

        txtId.setText(model.getValueAt(rowIndex, 0).toString());
        txtNome.setText(model.getValueAt(rowIndex, 1).toString());
        txtPreco.setText(model.getValueAt(rowIndex, 2).toString());
        txtEstoque.setText(model.getValueAt(rowIndex, 3).toString());

        btnInserir.setEnabled(false);
        btnAlterar.setEnabled(true);
        btnRemover.setEnabled(true);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtNome = new javax.swing.JTextField();
        txtPreco = new javax.swing.JTextField();
        txtEstoque = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        btnInserir = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();
        btnRemover = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProdutos = new javax.swing.JTable();
        btnSair = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro de Produtos");
        setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("ID:");

        jLabel2.setText("Nome:");

        jLabel3.setText("Preço");

        jLabel4.setText("Qnt estoq.");

        txtId.setEditable(false);
        txtId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdActionPerformed(evt);
            }
        });

        txtPreco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecoActionPerformed(evt);
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
        btnLimpar.setMaximumSize(new java.awt.Dimension(70, 23));
        btnLimpar.setMinimumSize(new java.awt.Dimension(70, 23));
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        btnInserir.setBackground(new java.awt.Color(153, 204, 255));
        btnInserir.setText("Inserir");
        btnInserir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirActionPerformed(evt);
            }
        });

        btnAlterar.setBackground(new java.awt.Color(153, 204, 255));
        btnAlterar.setText("Alterar");
        btnAlterar.setEnabled(false);
        btnAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarActionPerformed(evt);
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

        tblProdutos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "Preço", "Quantidade"
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
        tblProdutos.getTableHeader().setReorderingAllowed(false);
        tblProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProdutosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProdutos);

        btnSair.setBackground(new java.awt.Color(153, 204, 255));
        btnSair.setText("Sair");
        btnSair.setMaximumSize(new java.awt.Dimension(70, 23));
        btnSair.setMinimumSize(new java.awt.Dimension(70, 23));
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtId, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(txtNome)
                                        .addGap(212, 212, 212)
                                        .addComponent(btnInserir))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtPreco, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                            .addComponent(txtEstoque))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnBuscar))
                                        .addGap(20, 20, 20)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(btnRemover)
                                            .addComponent(btnAlterar))))))))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnInserir))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel3))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnBuscar)
                        .addComponent(btnAlterar)))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnRemover))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
    limparCamposProduto();
    }//GEN-LAST:event_btnLimparActionPerformed
    
    private void btnInserirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirActionPerformed
    if (factory == null || !factory.isOpen()) {
            JOptionPane.showMessageDialog(this, "EntityManagerFactory não está disponível.", "Erro JPA", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ProdutosJpaController localController = new ProdutosJpaController(factory);

        String nome = txtNome.getText().trim();
        String precoStr = txtPreco.getText().trim();
        String qtdStr = txtEstoque.getText().trim();

        try {
            if (nome.isEmpty()) {
                throw new Exception("O campo Nome é obrigatório!");
            }
            if (precoStr.isEmpty()) {
                throw new Exception("O campo Preço é obrigatório!");
            }
            if (qtdStr.isEmpty()) {
                throw new Exception("O campo Quantidade em Estoque é obrigatório!");
            }

            BigDecimal precoVenda;
            try {
                precoVenda = new BigDecimal(precoStr.replace(",", ".")); // Vírgula como separador decimal
                if (precoVenda.compareTo(BigDecimal.ZERO) < 0) {
                    throw new Exception("O preço não pode ser negativo.");
                }
            } catch (NumberFormatException e) {
                throw new Exception("Formato de preço inválido. Use números (ex: 10.99).");
            }

            int quantidadeEstoque;
            try {
                quantidadeEstoque = Integer.parseInt(qtdStr);
                if (quantidadeEstoque < 0) {
                    throw new Exception("A quantidade em estoque não pode ser negativa.");
                }
            } catch (NumberFormatException e) {
                throw new Exception("Formato de quantidade inválido. Use números inteiros.");
            }

            Produtos produto = new Produtos();
            produto.setNome(nome);
            produto.setPrecoVenda(precoVenda);
            produto.setQuantidadeEstoque(quantidadeEstoque);

            localController.create(produto);
            JOptionPane.showMessageDialog(this, "Produto inserido com sucesso!", "SUCESSO", JOptionPane.INFORMATION_MESSAGE);
            limparCamposProduto();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao inserir produto: \n" + e.getMessage(), "ERRO INSERÇÃO", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnInserirActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        String nomeBusca = txtNome.getText().trim().toLowerCase();

        if (nomeBusca.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um nome para buscar na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            atualizarTabelaProdutos(); // Mostra todos se a busca for vazia
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblProdutos.getModel();
        boolean encontrou = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            String nomeTabela = model.getValueAt(i, 1).toString().toLowerCase();
            if (nomeTabela.contains(nomeBusca)) {
                tblProdutos.setRowSelectionInterval(i, i);
                tblProdutos.scrollRectToVisible(tblProdutos.getCellRect(i, 0, true));
                preencherCamposComLinhaSelecionadaProduto(i);
                encontrou = true;
                break;
            }
        }

        if (!encontrou) {
            JOptionPane.showMessageDialog(this, "Nenhum produto encontrado na tabela com o nome: " + nomeBusca, "Não Encontrado", JOptionPane.INFORMATION_MESSAGE);
            tblProdutos.clearSelection();
            btnAlterar.setEnabled(false);
            btnRemover.setEnabled(false);
            btnInserir.setEnabled(true);
        }
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void tblProdutosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProdutosMouseClicked
     if (evt.getClickCount() == 2) {
            int selectedRow = tblProdutos.getSelectedRow();
            if (selectedRow != -1) {
                preencherCamposComLinhaSelecionadaProduto(selectedRow);
            }
        }
    }//GEN-LAST:event_tblProdutosMouseClicked

    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
    if (factory == null || !factory.isOpen()) {
            JOptionPane.showMessageDialog(this, "EntityManagerFactory não está disponível.", "Erro JPA", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ProdutosJpaController localController = new ProdutosJpaController(factory);

        String idStr = txtId.getText().trim();
        String nome = txtNome.getText().trim();
        String precoStr = txtPreco.getText().trim();
        String qtdStr =txtEstoque.getText().trim();

        try {
            if (idStr.isEmpty()) {
                throw new Exception("Nenhum produto selecionado para alterar (ID vazio)!");
            }
            if (nome.isEmpty()) {
                throw new Exception("O campo Nome é obrigatório!");
            }
             if (precoStr.isEmpty()) {
                throw new Exception("O campo Preço é obrigatório!");
            }
            if (qtdStr.isEmpty()) {
                throw new Exception("O campo Quantidade em Estoque é obrigatório!");
            }

            Integer idProduto = Integer.parseInt(idStr);

            BigDecimal precoVenda;
            try {
                precoVenda = new BigDecimal(precoStr.replace(",", "."));
                 if (precoVenda.compareTo(BigDecimal.ZERO) < 0) {
                    throw new Exception("O preço não pode ser negativo.");
                }
            } catch (NumberFormatException e) {
                throw new Exception("Formato de preço inválido.");
            }

            int quantidadeEstoque;
            try {
                quantidadeEstoque = Integer.parseInt(qtdStr);
                if (quantidadeEstoque < 0) {
                    throw new Exception("A quantidade em estoque não pode ser negativa.");
                }
            } catch (NumberFormatException e) {
                throw new Exception("Formato de quantidade inválido.");
            }

            Produtos produtoParaAlterar = localController.findProdutos(idProduto);

            if (produtoParaAlterar == null) {
                throw new Exception("Produto com ID " + idStr + " não encontrado no banco para alteração.");
            }

            produtoParaAlterar.setNome(nome);
            produtoParaAlterar.setPrecoVenda(precoVenda);
            produtoParaAlterar.setQuantidadeEstoque(quantidadeEstoque);

            localController.edit(produtoParaAlterar);
            JOptionPane.showMessageDialog(this, "Produto ID " + idStr + " alterado com sucesso!", "SUCESSO", JOptionPane.INFORMATION_MESSAGE);
            limparCamposProduto();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "O ID, Preço ou Quantidade do produto devem ser números válidos.", "ERRO DE FORMATO", JOptionPane.ERROR_MESSAGE);
        } catch (NonexistentEntityException nee) {
             JOptionPane.showMessageDialog(this, "Erro ao alterar: Produto com ID " + idStr + " não existe mais no banco.", "ERRO", JOptionPane.ERROR_MESSAGE);
             limparCamposProduto();
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao alterar produto: \n" + e.getMessage(), "ERRO ALTERAÇÃO", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void btnRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed
        if (factory == null || !factory.isOpen()) {
            JOptionPane.showMessageDialog(this, "EntityManagerFactory não está disponível.", "Erro JPA", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ProdutosJpaController localController = new ProdutosJpaController(factory);

        String idStr = txtId.getText().trim();
        String nomeProdutoParaMsg = txtNome.getText();

        try {
            if (idStr.isEmpty()) {
                throw new Exception("Nenhum produto selecionado para remover (ID vazio)!");
            }
            Integer idProduto = Integer.parseInt(idStr);

            int confirmacao = JOptionPane.showConfirmDialog(this,
                    "Remover o produto: " + nomeProdutoParaMsg + " (ID: " + idStr + ")?",
                    "CONFIRMAÇÃO DE REMOÇÃO",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirmacao == JOptionPane.YES_OPTION) {
                localController.destroy(idProduto);
                JOptionPane.showMessageDialog(this, "Produto ID " + idStr + " removido com sucesso!", "SUCESSO", JOptionPane.INFORMATION_MESSAGE);
                limparCamposProduto();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "O ID do produto deve ser um número válido.", "ERRO DE FORMATO", JOptionPane.ERROR_MESSAGE);
        } catch (NonexistentEntityException nee) {
            JOptionPane.showMessageDialog(this, "Erro ao remover: Produto com ID " + idStr + " não encontrado no banco.", "ERRO", JOptionPane.ERROR_MESSAGE);
            limparCamposProduto();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao remover produto: \n" + e.getMessage(), "ERRO REMOÇÃO", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnRemoverActionPerformed

    private void txtIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdActionPerformed

    }//GEN-LAST:event_txtIdActionPerformed

    private void txtPrecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecoActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnInserir;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnRemover;
    private javax.swing.JButton btnSair;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblProdutos;
    private javax.swing.JTextField txtEstoque;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtPreco;
    // End of variables declaration//GEN-END:variables
}