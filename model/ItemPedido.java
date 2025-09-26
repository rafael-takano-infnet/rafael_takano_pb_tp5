import java.math.BigDecimal;

public class ItemPedido {
    private int id;
    private Produto produto;
    private int quantidade;
    private BigDecimal valorUnitario;

    public ItemPedido(int id, Produto produto, int quantidade) {
        this.id = id;
        this.produto = produto;
        this.quantidade = quantidade;
        this.valorUnitario = produto.getValorUnitario();
    }

    public int getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public BigDecimal getSubtotal() {
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    @Override
    public String toString() {
        return String.format("%s x%d = R$ %.2f",
                produto.getNome(), quantidade, getSubtotal());
    }
}
