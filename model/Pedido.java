import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int id;
    private LocalDate data;
    private String status;
    private Cliente cliente;
    private Usuario vendedor;
    private List<ItemPedido> itens;
    private static int proximoItemId = 1;

    public Pedido(int id, Cliente cliente, Usuario vendedor) {
        this.id = id;
        this.data = LocalDate.now();
        this.status = "PENDENTE";
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.itens = new ArrayList<>();
    }

    public void adicionarItem(Produto produto, int quantidade) {
        if (!produto.temEstoque(quantidade)) {
            throw new IllegalArgumentException("Estoque insuficiente para " + produto.getNome());
        }

        ItemPedido item = new ItemPedido(proximoItemId++, produto, quantidade);
        itens.add(item);
        produto.reduzirEstoque(quantidade);
    }

    public BigDecimal getValorTotal() {
        return itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters
    public int getId() {
        return id;
    }

    public LocalDate getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public List<ItemPedido> getItens() {
        return new ArrayList<>(itens);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PEDIDO #").append(id).append(" ===\n");
        sb.append("Data: ").append(data).append("\n");
        sb.append("Cliente: ").append(cliente.getNome()).append("\n");
        sb.append("Vendedor: ").append(vendedor.getLogin()).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("\nItens:\n");

        for (ItemPedido item : itens) {
            sb.append("- ").append(item).append("\n");
        }

        sb.append("\nTOTAL: R$ ").append(String.format("%.2f", getValorTotal()));
        return sb.toString();
    }
}