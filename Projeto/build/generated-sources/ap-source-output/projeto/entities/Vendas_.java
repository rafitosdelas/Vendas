package projeto.entities;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-05-30T19:04:53", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Vendas.class)
public class Vendas_ { 

    public static volatile SingularAttribute<Vendas, String> formaPagamento;
    public static volatile SingularAttribute<Vendas, Date> dataVenda;
    public static volatile SingularAttribute<Vendas, Integer> idCliente;
    public static volatile SingularAttribute<Vendas, BigDecimal> valorTotal;
    public static volatile SingularAttribute<Vendas, Integer> idVenda;

}