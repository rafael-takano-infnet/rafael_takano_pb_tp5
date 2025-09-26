public class Cliente {
    private int id;
    private String nome;
    private String codigo;

    public Cliente(int id, String nome, String codigo) {
        this.id = id;
        this.nome = nome;
        this.codigo = codigo;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return String.format("%d - %s (%s)", id, nome, codigo);
    }
}