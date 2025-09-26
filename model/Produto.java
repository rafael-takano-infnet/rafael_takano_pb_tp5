import java.math.BigDecimal;

public class Produto {
    private int id;
    private String nome;
    private BigDecimal valorUnitario;
    private int estoque;

    public Produto(int id, String nome, BigDecimal valorUnitario, int estoque) {
        this.id = id;
        this.nome = nome;
        this.valorUnitario = valorUnitario;
        this.estoque = estoque;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public int getEstoque() {
        return estoque;
    }

    public boolean temEstoque(int quantidade) {
        return estoque >= quantidade;
    }

    public void reduzirEstoque(int quantidade) {
        if (temEstoque(quantidade)) {
            this.estoque -= quantidade;
        } else {
            throw new IllegalArgumentException("Estoque insuficiente");
        }
    }

    @Override
    public String toString() {
        return String.format("%d - %s (R$ %.2f) - Estoque: %d",
                id, nome, valorUnitario, estoque);
    }
}