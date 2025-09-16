import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

enum PerfilUsuario {
    VENDEDOR("Vendedor"),
    GERENTE("Gerente"),
    OPERADOR_LOGISTICO("Operador Logístico");

    private final String nomeExibicao;

    PerfilUsuario(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    public static Optional<PerfilUsuario> daNome(String nome) {
        if (nome == null)
            return Optional.empty();

        return switch (nome.trim().toUpperCase()) {
            case "VENDEDOR" -> Optional.of(VENDEDOR);
            case "GERENTE" -> Optional.of(GERENTE);
            case "OPERADOR_LOGISTICO", "OPERADOR LOGISTICO" -> Optional.of(OPERADOR_LOGISTICO);
            default -> Optional.empty();
        };
    }
}

record Usuario(String login, String senha, PerfilUsuario perfil) {
}

record ResultadoAutenticacao(boolean sucesso, String mensagem, PerfilUsuario perfil) {
    public static ResultadoAutenticacao sucesso(PerfilUsuario perfil) {
        return new ResultadoAutenticacao(true, "Autenticação realizada com sucesso!", perfil);
    }

    public static ResultadoAutenticacao falha(String mensagem) {
        return new ResultadoAutenticacao(false, mensagem, null);
    }
}

interface RepositorioUsuario {
    Optional<Usuario> buscarPorLogin(String login);

    void carregarUsuarios() throws IOException;
}

class RepositorioUsuarioCSV implements RepositorioUsuario {
    private final Map<String, Usuario> usuarios = new HashMap<>();
    private final String caminhoArquivo;

    public RepositorioUsuarioCSV(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    @Override
    public void carregarUsuarios() throws IOException {
        Path caminho = Paths.get(caminhoArquivo);

        if (!Files.exists(caminho)) {
            criarArquivoExemplo(caminho);
            System.out.println("Arquivo CSV criado: " + caminhoArquivo);
            System.out.println("Você pode editá-lo e executar novamente.\n");
        }

        usuarios.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            int numeroLinha = 0;

            while ((linha = reader.readLine()) != null) {
                numeroLinha++;

                if (numeroLinha == 1 && linha.toLowerCase().contains("login")) {
                    continue;
                }

                if (linha.trim().isEmpty()) {
                    continue;
                }

                processarLinhaCSV(linha, numeroLinha);
            }
        }

        System.out.printf("Carregados %d usuários do arquivo CSV\n\n", usuarios.size());
    }

    private void processarLinhaCSV(String linha, int numeroLinha) {
        try {
            String[] campos = linha.split(",");

            if (campos.length != 3) {
                System.err.printf("Linha %d ignorada: formato inválido (esperado: login,senha,perfil)\n", numeroLinha);
                return;
            }

            String login = campos[0].trim();
            String senha = campos[1].trim();
            String perfilStr = campos[2].trim();

            Optional<PerfilUsuario> perfilOpt = PerfilUsuario.daNome(perfilStr);

            if (perfilOpt.isEmpty()) {
                System.err.printf("⚠️  Linha %d ignorada: perfil inválido '%s'\n", numeroLinha, perfilStr);
                return;
            }

            if (login.isEmpty() || senha.isEmpty()) {
                System.err.printf("⚠️  Linha %d ignorada: login ou senha vazios\n", numeroLinha);
                return;
            }

            Usuario usuario = new Usuario(login, senha, perfilOpt.get());
            usuarios.put(login, usuario);

        } catch (Exception e) {
            System.err.printf("❌ Erro ao processar linha %d: %s\n", numeroLinha, e.getMessage());
        }
    }

    private void criarArquivoExemplo(Path caminho) throws IOException {
        String conteudoExemplo = """
                login,senha,perfil
                vendedor1,123456,VENDEDOR
                gerente1,admin123,GERENTE
                operador1,log123,OPERADOR_LOGISTICO
                joao.silva,senha123,VENDEDOR
                maria.santos,gerente456,GERENTE
                """;

        Files.writeString(caminho, conteudoExemplo);
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        return Optional.ofNullable(usuarios.get(login));
    }
}

class ServicoAutenticacao {
    private final RepositorioUsuario repositorioUsuario;

    public ServicoAutenticacao(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    public ResultadoAutenticacao autenticar(String login, String senha) {
        if (login == null || login.trim().isEmpty()) {
            return ResultadoAutenticacao.falha("Login não pode estar vazio");
        }

        if (senha == null || senha.trim().isEmpty()) {
            return ResultadoAutenticacao.falha("Senha não pode estar vazia");
        }

        Optional<Usuario> usuarioOpt = repositorioUsuario.buscarPorLogin(login.trim());

        if (usuarioOpt.isEmpty()) {
            return ResultadoAutenticacao.falha("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.senha().equals(senha)) {
            return ResultadoAutenticacao.falha("Senha incorreta");
        }

        return ResultadoAutenticacao.sucesso(usuario.perfil());
    }
}

class InterfaceConsole {
    private final Scanner scanner;
    private final ServicoAutenticacao servicoAuth;

    public InterfaceConsole(ServicoAutenticacao servicoAuth) {
        this.scanner = new Scanner(System.in);
        this.servicoAuth = servicoAuth;
    }

    public void executar() {
        mostrarCabecalho();

        boolean continuar = true;
        while (continuar) {
            try {
                continuar = processarInteracao();
            } catch (Exception e) {
                System.err.println("Erro inesperado: " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Aplicação encerrada!");
    }

    private void mostrarCabecalho() {
        System.out.println("========================================");
        System.out.println("           SISTEMA DE LOGIN             ");
        System.out.println("      Autenticação de Usuários          ");
        System.out.println("========================================\n");
    }

    private boolean processarInteracao() {
        System.out.print("Login (ou 'sair' para encerrar): ");
        String login = scanner.nextLine().trim();

        if (login.equalsIgnoreCase("sair") || login.equalsIgnoreCase("exit")) {
            return false;
        }

        if (login.isEmpty()) {
            System.out.println("Login não pode estar vazio!\n");
            return true;
        }

        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        ResultadoAutenticacao resultado = servicoAuth.autenticar(login, senha);
        mostrarResultado(resultado);

        System.out.println("─".repeat(50));
        return true;
    }

    private void mostrarResultado(ResultadoAutenticacao resultado) {
        if (resultado.sucesso()) {
            System.out.printf("%s\n", resultado.mensagem());
            System.out.printf("Bem-vindo(a)! Perfil: %s\n\n", resultado.perfil().getNomeExibicao());
        } else {
            System.out.printf("%s\n\n", resultado.mensagem());
        }
    }
}

public class SistemaAutenticacao {
    private static final String ARQUIVO_CSV = "usuarios.csv";

    public static void main(String[] args) {
        try {

            RepositorioUsuario repositorio = new RepositorioUsuarioCSV(ARQUIVO_CSV);
            repositorio.carregarUsuarios();

            ServicoAutenticacao servicoAuth = new ServicoAutenticacao(repositorio);
            InterfaceConsole console = new InterfaceConsole(servicoAuth);

            console.executar();

        } catch (IOException e) {
            System.err.printf("Erro ao carregar arquivo CSV: %s\n", e.getMessage());
            System.err.println("Verifique se o arquivo existe e tem permissões de leitura.");
        } catch (Exception e) {
            System.err.printf("Erro inesperado: %s\n", e.getMessage());
            e.printStackTrace();
        }
    }
}