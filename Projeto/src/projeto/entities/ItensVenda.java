/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projeto.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author docar
 */
@Entity
@Table(name = "itens_venda")
@NamedQueries({
    @NamedQuery(name = "ItensVenda.findAll", query = "SELECT i FROM ItensVenda i"),
    @NamedQuery(name = "ItensVenda.findByIdItemVenda", query = "SELECT i FROM ItensVenda i WHERE i.idItemVenda = :idItemVenda"),
    @NamedQuery(name = "ItensVenda.findByIdVenda", query = "SELECT i FROM ItensVenda i WHERE i.idVenda = :idVenda"),
    @NamedQuery(name = "ItensVenda.findByIdProduto", query = "SELECT i FROM ItensVenda i WHERE i.idProduto = :idProduto"),
    @NamedQuery(name = "ItensVenda.findByQuantidade", query = "SELECT i FROM ItensVenda i WHERE i.quantidade = :quantidade"),
    @NamedQuery(name = "ItensVenda.findByPrecoUnitarioMomento", query = "SELECT i FROM ItensVenda i WHERE i.precoUnitarioMomento = :precoUnitarioMomento")})
public class ItensVenda implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_item_venda")
    private Integer idItemVenda;
    @Basic(optional = false)
    @Column(name = "id_venda")
    private int idVenda;
    @Basic(optional = false)
    @Column(name = "id_produto")
    private int idProduto;
    @Basic(optional = false)
    @Column(name = "quantidade")
    private int quantidade;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "preco_unitario_momento")
    private BigDecimal precoUnitarioMomento;

    public ItensVenda() {
    }

    public ItensVenda(Integer idItemVenda) {
        this.idItemVenda = idItemVenda;
    }

    public ItensVenda(Integer idItemVenda, int idVenda, int idProduto, int quantidade, BigDecimal precoUnitarioMomento) {
        this.idItemVenda = idItemVenda;
        this.idVenda = idVenda;
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.precoUnitarioMomento = precoUnitarioMomento;
    }

    public Integer getIdItemVenda() {
        return idItemVenda;
    }

    public void setIdItemVenda(Integer idItemVenda) {
        this.idItemVenda = idItemVenda;
    }

    public int getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(int idVenda) {
        this.idVenda = idVenda;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitarioMomento() {
        return precoUnitarioMomento;
    }

    public void setPrecoUnitarioMomento(BigDecimal precoUnitarioMomento) {
        this.precoUnitarioMomento = precoUnitarioMomento;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idItemVenda != null ? idItemVenda.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItensVenda)) {
            return false;
        }
        ItensVenda other = (ItensVenda) object;
        if ((this.idItemVenda == null && other.idItemVenda != null) || (this.idItemVenda != null && !this.idItemVenda.equals(other.idItemVenda))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "projeto.entities.ItensVenda[ idItemVenda=" + idItemVenda + " ]";
    }
    
}
