public class Usuario {
    private int id;
    private String login;
    private String senha;
    private String perfil;

    public Usuario(int id, String login, String senha, String perfil) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    public String getPerfil() {
        return perfil;
    }

    public boolean isVendedor() {
        return "VENDEDOR".equals(perfil);
    }
}