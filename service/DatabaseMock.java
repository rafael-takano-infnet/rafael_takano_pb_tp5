import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class DatabaseMock {
    private static List<Usuario> usuarios;
    private static List<Cliente> clientes;
    private static List<Produto> produtos;

    static {
        // Inicializar dados mock
        usuarios = Arrays.asList(
                new Usuario(1, "vendedor1", "123456", "VENDEDOR"),
                new Usuario(2, "gerente1", "admin123", "GERENTE"));

        clientes = Arrays.asList(
                new Cliente(1, "Empresa ABC Ltda", "ABC001"),
                new Cliente(2, "Distribuidora XYZ", "XYZ002"),
                new Cliente(3, "Comercial 123", "COM123"));

        produtos = Arrays.asList(
                new Produto(1, "Notebook Dell", new BigDecimal("2500.00"), 10),
                new Produto(2, "Mouse Logitech", new BigDecimal("45.90"), 50),
                new Produto(3, "Teclado Mecânico", new BigDecimal("189.90"), 25),
                new Produto(4, "Monitor 24pol", new BigDecimal("899.99"), 8));
    }

    public static List<Usuario> getUsuarios() {
        return usuarios;
    }

    public static List<Cliente> getClientes() {
        return clientes;
    }

    public static List<Produto> getProdutos() {
        return produtos;
    }

    public static Usuario autenticar(String login, String senha) {
        return usuarios.stream()
                .filter(u -> u.getLogin().equals(login) && u.getSenha().equals(senha))
                .findFirst()
                .orElse(null);
    }

    public static Cliente buscarClientePorId(int id) {
        return clientes.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public static Produto buscarProdutoPorId(int id) {
        return produtos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }
}