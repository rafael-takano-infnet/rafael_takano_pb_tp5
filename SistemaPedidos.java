import java.util.List;
import java.util.Scanner;

public class SistemaPedidos {
    private static Scanner scanner = new Scanner(System.in);
    private static PedidoService pedidoService = new PedidoService();
    private static Usuario usuarioLogado = null;

    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE GESTÃO DE PEDIDOS ===");

        // Login
        if (!realizarLogin()) {
            System.out.println("Falha na autenticação. Sistema encerrado.");
            return;
        }

        // Menu principal
        boolean continuar = true;
        while (continuar) {
            continuar = exibirMenuPrincipal();
        }

        System.out.println("Sistema encerrado.");
        scanner.close();
    }

    private static boolean realizarLogin() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Login: ");
        String login = scanner.nextLine();

        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        usuarioLogado = DatabaseMock.autenticar(login, senha);

        if (usuarioLogado != null) {
            System.out.println("Login realizado com sucesso!");
            System.out.println("Bem-vindo, " + usuarioLogado.getLogin() + " (" + usuarioLogado.getPerfil() + ")");
            return true;
        } else {
            System.out.println("Credenciais inválidas!");
            return false;
        }
    }

    private static boolean exibirMenuPrincipal() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Criar Pedido");
        System.out.println("2. Listar Pedidos");
        System.out.println("3. Sair");
        System.out.print("Escolha uma opção: ");

        String opcao = scanner.nextLine();

        switch (opcao) {
            case "1":
                if (usuarioLogado.isVendedor()) {
                    criarPedido();
                } else {
                    System.out.println("Apenas vendedores podem criar pedidos!");
                }
                break;
            case "2":
                listarPedidos();
                break;
            case "3":
                return false;
            default:
                System.out.println("Opção inválida!");
        }

        return true;
    }

    private static void criarPedido() {
        System.out.println("\n--- CRIAR PEDIDO ---");

        // Selecionar cliente
        Cliente cliente = selecionarCliente();
        if (cliente == null)
            return;

        // Criar pedido
        Pedido pedido = pedidoService.criarPedido(usuarioLogado, cliente);
        System.out.println("Pedido #" + pedido.getId() + " criado para " + cliente.getNome());

        // Adicionar produtos
        boolean continuarAdicionando = true;
        while (continuarAdicionando) {
            System.out.println("\n--- ADICIONAR PRODUTOS ---");

            Produto produto = selecionarProduto();
            if (produto == null)
                break;

            int quantidade = solicitarQuantidade(produto);
            if (quantidade <= 0)
                continue;

            try {
                pedido.adicionarItem(produto, quantidade);
                System.out.println("Produto adicionado: " + produto.getNome() + " x" + quantidade);
                System.out.println("Subtotal atual: R$ " + String.format("%.2f", pedido.getValorTotal()));
            } catch (IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
            }

            System.out.print("\nDeseja adicionar outro produto? (s/n): ");
            String resposta = scanner.nextLine().toLowerCase();
            continuarAdicionando = resposta.startsWith("s");
        }

        // Finalizar pedido
        if (!pedido.getItens().isEmpty()) {
            System.out.println("\n" + pedido);
            System.out.println("\nPedido criado com sucesso!");
        } else {
            System.out.println("Pedido cancelado (nenhum item adicionado).");
        }
    }

    private static Cliente selecionarCliente() {
        System.out.println("\n--- SELECIONAR CLIENTE ---");
        List<Cliente> clientes = DatabaseMock.getClientes();

        for (Cliente cliente : clientes) {
            System.out.println(cliente);
        }

        System.out.print("Digite o ID do cliente: ");
        try {
            int clienteId = Integer.parseInt(scanner.nextLine());
            Cliente cliente = DatabaseMock.buscarClientePorId(clienteId);
            if (cliente == null) {
                System.out.println("Cliente não encontrado!");
            }
            return cliente;
        } catch (NumberFormatException e) {
            System.out.println("ID inválido!");
            return null;
        }
    }

    private static Produto selecionarProduto() {
        System.out.println("\n--- SELECIONAR PRODUTO ---");
        List<Produto> produtos = DatabaseMock.getProdutos();

        for (Produto produto : produtos) {
            System.out.println(produto);
        }

        System.out.print("Digite o ID do produto (0 para finalizar): ");
        try {
            int produtoId = Integer.parseInt(scanner.nextLine());
            if (produtoId == 0)
                return null;

            Produto produto = DatabaseMock.buscarProdutoPorId(produtoId);
            if (produto == null) {
                System.out.println("Produto não encontrado!");
            }
            return produto;
        } catch (NumberFormatException e) {
            System.out.println("ID inválido!");
            return null;
        }
    }

    private static int solicitarQuantidade(Produto produto) {
        System.out.print("Digite a quantidade para " + produto.getNome() +
                " (estoque: " + produto.getEstoque() + "): ");
        try {
            int quantidade = Integer.parseInt(scanner.nextLine());
            if (quantidade <= 0) {
                System.out.println("Quantidade deve ser maior que zero!");
                return 0;
            }
            return quantidade;
        } catch (NumberFormatException e) {
            System.out.println("Quantidade inválida!");
            return 0;
        }
    }

    private static void listarPedidos() {
        System.out.println("\n--- PEDIDOS CADASTRADOS ---");
        List<Pedido> pedidos = pedidoService.listarPedidos();

        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido encontrado.");
        } else {
            for (Pedido pedido : pedidos) {
                System.out.println("\n" + pedido);
                System.out.println("─".repeat(50));
            }
        }
    }
}