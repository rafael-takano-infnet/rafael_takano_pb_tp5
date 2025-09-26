import java.util.ArrayList;
import java.util.List;

public class PedidoService {
    private static List<Pedido> pedidos = new ArrayList<>();
    private static int proximoPedidoId = 1;

    public Pedido criarPedido(Usuario vendedor, Cliente cliente) {
        if (!vendedor.isVendedor()) {
            throw new IllegalArgumentException("Apenas vendedores podem criar pedidos");
        }

        Pedido pedido = new Pedido(proximoPedidoId++, cliente, vendedor);
        pedidos.add(pedido);
        return pedido;
    }

    public List<Pedido> listarPedidos() {
        return new ArrayList<>(pedidos);
    }

    public Pedido buscarPedidoPorId(int id) {
        return pedidos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }
}