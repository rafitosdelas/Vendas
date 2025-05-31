package projeto;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import projeto.controller.ClientesJpaController;
import projeto.controller.exceptions.NonexistentEntityException; 
import projeto.entities.Clientes;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class CadastroCliente extends javax.swing.JFrame {

    private EntityManagerFactory factory;

    public CadastroCliente() {
        initComponents();

        try {
            factory = Persistence.createEntityManagerFactory("projetoPU");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao criar EntityManagerFactory: \n" + e.getMessage(), "ERRO JPA", JOptionPane.ERROR_MESSAGE);
            btnBuscar.setEnabled(false);
            btnInserir.setEnabled(false);
            return;
        }

        configurarModeloTabelaClientes();
        atualizarTabelaClientes();
        limparCamposCliente();
    }

    private void configurarModeloTabelaClientes() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "Nome", "CPF/CNPJ", "Email", "Telefone"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblClientes.setModel(model);
    }

    private void atualizarTabelaClientes() {
        if (factory == null || !factory.isOpen()) {
            JOptionPane.showMessageDialog(this, "EntityManagerFactory não está disponível.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ClientesJpaController localController = new ClientesJpaController(factory);
        DefaultTableModel model = (DefaultTableModel) tblClientes.getModel();

        model.setRowCount(0); // Limpa linhas existentes

        try {
            List<Clientes> clientes = localController.findClientesEntities();

            for (Clientes cliente : clientes) {
                Object id = cliente.getIdCliente();
                String nome = cliente.getNome();
                String cpfCnpj = cliente.getCpfCnpj() != null ? cliente.getCpfCnpj() : "";
                String email = cliente.getEmail() != null ? cliente.getEmail() : "";
                String telefone = cliente.getTelefone() != null ? cliente.getTelefone() : "";

                model.addRow(new Object[]{id, nome, cpfCnpj, email, telefone});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao atualizar tabela de clientes: \n" + e.getMessage(),
                "ERRO TABELA",
                JOptionPane.ERROR_MESSAGE);
            // e.printStackTrace(); // REMOVIDO
        }
    }

    private void limparCamposCliente() {
        txtId.setText(""); // ID
        txtNome.setText(""); // Nome
        txtCPF.setText(""); // CPF/CNPJ
        txtEmail.setText(""); // Email
        txtTel.setText(""); // Telefone

        btnBuscar.setEnabled(true); // Buscar
        btnInserir.setEnabled(true); // Inserir
        btnAlterar.setEnabled(false); // Alterar
        btnRemover.setEnabled(false); // Remover

        tblClientes.clearSelection();

        atualizarTabelaClientes();

        txtNome.requestFocus();

    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtNome = new javax.swing.JTextField();
        txtCPF = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtTel = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        btnInserir = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();
        btnRemover = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro de Clientes");
        setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("ID:");

        jLabel2.setText("Nome:");

        jLabel3.setText("CPF/CNPJ:");

        jLabel4.setText("Email");

        jLabel5.setText("Telefone");

        txtId.setEditable(false);
        txtId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdActionPerformed(evt);
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

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "CPF/CNPJ", "Email", "Telefone"
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
        tblClientes.setSurrendersFocusOnKeystroke(true);
        tblClientes.getTableHeader().setResizingAllowed(false);
        tblClientes.getTableHeader().setReorderingAllowed(false);
        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblClientes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(130, 130, 130)
                                        .addComponent(btnBuscar))
                                    .addComponent(txtCPF, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnInserir)
                                    .addComponent(btnAlterar)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(txtTel, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(130, 130, 130)
                                .addComponent(btnLimpar)
                                .addGap(20, 20, 20)
                                .addComponent(btnRemover))
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(31, Short.MAX_VALUE))
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
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel3))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCPF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnInserir)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBuscar)
                            .addComponent(btnAlterar)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jLabel4)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLimpar)
                            .addComponent(btnRemover)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jLabel5)))
                .addGap(30, 30, 30)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
    limparCamposCliente();
    }//GEN-LAST:event_btnLimparActionPerformed

    private void btnInserirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirActionPerformed
     if (factory == null || !factory.isOpen()) {
        JOptionPane.showMessageDialog(this, "EntityManagerFactory não está disponível.", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
    }
    ClientesJpaController localController = new ClientesJpaController(factory);
    
    String nome = txtNome.getText().trim();
    String cpfCnpj = txtCPF.getText().trim();
    String email = txtEmail.getText().trim();
    String telefone = txtTel.getText().trim();

    try {
        if (nome.isEmpty()) { 
            throw new Exception("O campo Nome é obrigatório!"); 
        }
        Clientes cliente = new Clientes();
        cliente.setNome(nome); 
        cliente.setCpfCnpj(cpfCnpj.isEmpty() ? null : cpfCnpj);
        cliente.setEmail(email.isEmpty() ? null : email);
        cliente.setTelefone(telefone.isEmpty() ? null : telefone);
        
        localController.create(cliente); 
        JOptionPane.showMessageDialog(this, "Cliente inserido com sucesso!", "SUCESSO", JOptionPane.INFORMATION_MESSAGE);
        limparCamposCliente(); 
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Erro ao inserir cliente: \n" + e.getMessage(),
                "ERRO INSERÇÃO",
                JOptionPane.ERROR_MESSAGE);
        e.printStackTrace(); // debug
    }
    }//GEN-LAST:event_btnInserirActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        String nomeBusca = txtNome.getText().trim().toLowerCase(); 

        if (nomeBusca.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um nome para buscar na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            atualizarTabelaClientes(); 
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblClientes.getModel();
        boolean encontrou = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            String nomeTabela = model.getValueAt(i, 1).toString().toLowerCase();
            if (nomeTabela.contains(nomeBusca)) {
                tblClientes.setRowSelectionInterval(i, i); 
                tblClientes.scrollRectToVisible(tblClientes.getCellRect(i, 0, true)); 
                
                preencherCamposComLinhaSelecionada(i);
                encontrou = true;
                break; 
            }
        }

        if (!encontrou) {
            JOptionPane.showMessageDialog(this, "Nenhum cliente encontrado na tabela com o nome: " + nomeBusca, "Não Encontrado", JOptionPane.INFORMATION_MESSAGE);
            tblClientes.clearSelection();
            btnAlterar.setEnabled(false);
            btnRemover.setEnabled(false);
            btnInserir.setEnabled(true);
        }
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClientesMouseClicked
      if (evt.getClickCount() == 2) { 
            int selectedRow = tblClientes.getSelectedRow();
            if (selectedRow != -1) { 
                preencherCamposComLinhaSelecionada(selectedRow);
            }
        }
    }//GEN-LAST:event_tblClientesMouseClicked
    private void preencherCamposComLinhaSelecionada(int rowIndex) {
        DefaultTableModel model = (DefaultTableModel) tblClientes.getModel();
        txtId.setText(model.getValueAt(rowIndex, 0).toString()); // ID
        txtNome.setText(model.getValueAt(rowIndex, 1).toString()); // Nome
        txtCPF.setText(model.getValueAt(rowIndex, 2) != null ? model.getValueAt(rowIndex, 2).toString() : ""); // CPF/CNPJ
        txtEmail.setText(model.getValueAt(rowIndex, 3) != null ? model.getValueAt(rowIndex, 3).toString() : ""); // Email
        txtTel.setText(model.getValueAt(rowIndex, 4) != null ? model.getValueAt(rowIndex, 4).toString() : ""); // Telefone

        btnAlterar.setEnabled(true); 
        btnRemover.setEnabled(true); 
        btnInserir.setEnabled(false); // Desabilita Inserir
        txtId.setEditable(false);
    }
    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
    if (factory == null || !factory.isOpen()) {
        JOptionPane.showMessageDialog(this, "EntityManagerFactory não está disponível.", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
    }
    ClientesJpaController localController = new ClientesJpaController(factory); 
    
    String idStr = txtId.getText().trim(); 
    String nome = txtNome.getText().trim();
    String cpfCnpj = txtCPF.getText().trim();
    String email = txtEmail.getText().trim();
    String telefone = txtTel.getText().trim();

    try {
        if (idStr.isEmpty()) {
            throw new Exception("Nenhum cliente selecionado para alterar (ID vazio)!");
        }
        if (nome.isEmpty()) {
            throw new Exception("O campo Nome é obrigatório!");
        }
        
        Integer idCliente = Integer.parseInt(idStr);
        Clientes clienteParaAlterar = localController.findClientes(idCliente);

        if (clienteParaAlterar == null) {
             throw new Exception("Cliente com ID " + idStr + " não encontrado no banco de dados para alteração.");
        }

        clienteParaAlterar.setNome(nome);
        clienteParaAlterar.setCpfCnpj(cpfCnpj.isEmpty() ? null : cpfCnpj);
        clienteParaAlterar.setEmail(email.isEmpty() ? null : email);
        clienteParaAlterar.setTelefone(telefone.isEmpty() ? null : telefone);
        
        localController.edit(clienteParaAlterar);
        JOptionPane.showMessageDialog(this,
                "Cliente ID " + idStr + " alterado com sucesso!",
                "SUCESSO",
                JOptionPane.INFORMATION_MESSAGE);
        limparCamposCliente();
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
                "O ID do cliente deve ser um número válido.",
                "ERRO DE FORMATO",
                JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Erro ao alterar cliente: \n" + e.getMessage(),
                "ERRO",
                JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void btnRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed
    if (factory == null || !factory.isOpen()) { // Verifica a factory primeiro
        JOptionPane.showMessageDialog(this, "EntityManagerFactory não está disponível.", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
    }
    ClientesJpaController localController = new ClientesJpaController(factory);

    String idStr = txtId.getText().trim();
    String nomeClienteParaMsg = txtNome.getText();

    try {
        if (idStr.isEmpty()) {
             throw new Exception("Nenhum cliente selecionado para remover (ID vazio)!");
        }
        Integer idCliente = Integer.parseInt(idStr);

        int confirmacao = JOptionPane.showConfirmDialog(this, 
                "Remover o cliente: " + nomeClienteParaMsg + " (ID: " + idStr + ")?", 
                "CONFIRMAÇÃO", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            localController.destroy(idCliente); 
            JOptionPane.showMessageDialog(this,
                    "Cliente ID " + idStr + " removido com sucesso!",
                    "SUCESSO",
                    JOptionPane.INFORMATION_MESSAGE);
            
            limparCamposCliente();
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
                "O ID do cliente deve ser um número válido.",
                "ERRO DE FORMATO",
                JOptionPane.ERROR_MESSAGE);
    } catch (projeto.controller.exceptions.NonexistentEntityException nee) {
         JOptionPane.showMessageDialog(this,
                "Erro ao remover: Cliente com ID " + idStr + " não encontrado no banco.",
                "ERRO",
                JOptionPane.ERROR_MESSAGE);
         limparCamposCliente();
    }
    catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Erro ao remover cliente: \n" + e.getMessage(),
                "ERRO",
                JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    }//GEN-LAST:event_btnRemoverActionPerformed

    private void txtIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdActionPerformed

    }//GEN-LAST:event_txtIdActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnInserir;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnRemover;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtCPF;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtTel;
    // End of variables declaration//GEN-END:variables
}