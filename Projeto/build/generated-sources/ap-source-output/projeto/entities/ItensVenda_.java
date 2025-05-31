package projeto.entities;

import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-05-30T19:04:53", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(ItensVenda.class)
public class ItensVenda_ { 

    public static volatile SingularAttribute<ItensVenda, Integer> idProduto;
    public static volatile SingularAttribute<ItensVenda, Integer> idItemVenda;
    public static volatile SingularAttribute<ItensVenda, Integer> quantidade;
    public static volatile SingularAttribute<ItensVenda, BigDecimal> precoUnitarioMomento;
    public static volatile SingularAttribute<ItensVenda, Integer> idVenda;

}