import java.util.ArrayList;
import java.util.Scanner;

enum StatusOrdem { ABERTA, EM_EXECUCAO, FINALIZADA }

class Mecanico {
    private String nome, cpf, especialidade, telefone;

    public Mecanico(String nome, String cpf, String especialidade, String telefone) {
        this.nome = nome; this.cpf = cpf;
        this.especialidade = especialidade; this.telefone = telefone;
    }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }

    public String toString() {
        return nome + " | CPF " + cpf + " | " + especialidade + " | " + telefone;
    }
}

class Servico {
    private String nome, categoria;
    private int tempoEstimado;
    private double valor;

    public Servico(String nome, int tempoEstimado, double valor, String categoria) {
        this.nome = nome; this.tempoEstimado = tempoEstimado;
        this.valor = valor; this.categoria = categoria;
    }
    public String getCategoria() { return categoria; }

    public String toString() {
        return nome + " | " + categoria + " | " + tempoEstimado + " min | R$ " + valor;
    }
}

class Box {
    private int numero, capacidadeMaxima;
    private String tipoServico, localizacao;
    private Mecanico mecanico;

    public Box(int numero, String tipoServico, int capacidadeMaxima, String localizacao) {
        this.numero = numero; this.tipoServico = tipoServico;
        this.capacidadeMaxima = capacidadeMaxima; this.localizacao = localizacao;
    }
    public int getNumero() { return numero; }
    public String getTipoServico() { return tipoServico; }
    public int getCapacidadeMaxima() { return capacidadeMaxima; }
    public Mecanico getMecanico() { return mecanico; }
    public void setMecanico(Mecanico mecanico) { this.mecanico = mecanico; }

    public String toString() {
        return "Box " + numero + " | " + tipoServico + " | cap. " + capacidadeMaxima
             + " | " + localizacao + " | Mecânico: " + (mecanico == null ? "nenhum" : mecanico.getNome());
    }
}

class OrdemServico {
    private int codigo;
    private String cliente, modelo, placa, data;
    private double valorEstimado;
    private StatusOrdem status = StatusOrdem.ABERTA;
    private Servico servico;
    private Box box; // null enquanto aberta

    public OrdemServico(int codigo, String cliente, String modelo, String placa,
                        String data, double valorEstimado, Servico servico) {
        this.codigo = codigo; this.cliente = cliente; this.modelo = modelo;
        this.placa = placa; this.data = data;
        this.valorEstimado = valorEstimado; this.servico = servico;
    }
    public int getCodigo() { return codigo; }
    public StatusOrdem getStatus() { return status; }
    public void setStatus(StatusOrdem status) { this.status = status; }
    public Servico getServico() { return servico; }
    public Box getBox() { return box; }
    public void setBox(Box box) { this.box = box; }

    public String toString() {
        return "OS " + codigo + " | " + cliente + " | " + modelo + " (" + placa + ") | " + data
             + " | " + status + " | R$ " + valorEstimado
             + "\n  Serviço: " + servico
             + "\n  " + (box == null ? "Box: não atribuído" : box
             + "\n  Mecânico: " + (box.getMecanico() == null ? "nenhum" : box.getMecanico()));
    }
}

public class Main {
    static Scanner sc = new Scanner(System.in);
    static ArrayList<Mecanico> mecanicos = new ArrayList<>();
    static ArrayList<Box> boxes = new ArrayList<>();
    static ArrayList<OrdemServico> ordens = new ArrayList<>();

    static void criarDadosIniciais() {
        mecanicos.add(new Mecanico("Carlos", "111", "Mecânica", "9111"));
        mecanicos.add(new Mecanico("Ana", "222", "Elétrica", "9222"));
        mecanicos.add(new Mecanico("Bruno", "333", "Funilaria", "9333"));
        boxes.add(new Box(1, "Mecânica", 2, "Setor A"));
        boxes.add(new Box(2, "Elétrica", 2, "Setor B"));
        boxes.add(new Box(3, "Funilaria", 3, "Setor C"));
    }

    static String ler(String msg) { System.out.print(msg); return sc.nextLine(); }
    static int lerInt(String msg) {
        while (true) {
            try { return Integer.parseInt(ler(msg).trim()); }
            catch (NumberFormatException e) { System.out.println("Número inválido."); }
        }
    }
    static double lerDouble(String msg) {
        while (true) {
            try { return Double.parseDouble(ler(msg).trim().replace(',', '.')); }
            catch (NumberFormatException e) { System.out.println("Valor inválido."); }
        }
    }

    static Box buscarBox(int n) {
        for (Box b : boxes) if (b.getNumero() == n) return b;
        return null;
    }
    static OrdemServico buscarOrdem(int c) {
        for (OrdemServico o : ordens) if (o.getCodigo() == c) return o;
        return null;
    }

    static void cadastrarOrdem() {
        int codigo = lerInt("Código: ");
        if (buscarOrdem(codigo) != null) { System.out.println("Código já existe."); return; }
        String cliente = ler("Cliente: ");
        String modelo = ler("Modelo: ");
        String placa = ler("Placa: ");
        String data = ler("Data: ");
        double valor = lerDouble("Valor estimado: ");
        Servico s = new Servico(ler("Nome do serviço: "), lerInt("Tempo estimado (min): "),
                                lerDouble("Valor do serviço: "), ler("Categoria: "));
        ordens.add(new OrdemServico(codigo, cliente, modelo, placa, data, valor, s));
        System.out.println("Ordem cadastrada como ABERTA.");
    }

    static void associarMecanico() {
        Box box = buscarBox(lerInt("Número do box: "));
        if (box == null) { System.out.println("Box não encontrado."); return; }
        String cpf = ler("CPF do mecânico: ");
        Mecanico mec = null;
        for (Mecanico m : mecanicos) if (m.getCpf().equals(cpf)) mec = m;
        if (mec == null) { System.out.println("Mecânico não encontrado."); return; }
        for (Box b : boxes) {
            if (b.getMecanico() == mec) {
                System.out.println("Mecânico já é responsável pelo box " + b.getNumero());
                return;
            }
        }
        box.setMecanico(mec);
        System.out.println("Associado.");
    }

    static void atribuirOrdem() {
        OrdemServico os = buscarOrdem(lerInt("Código da ordem: "));
        Box box = buscarBox(lerInt("Número do box: "));
        if (os == null || box == null) { System.out.println("Ordem ou box não encontrado."); return; }
        if (os.getStatus() != StatusOrdem.ABERTA) { System.out.println("Só ordens abertas."); return; }
        if (!box.getTipoServico().equalsIgnoreCase(os.getServico().getCategoria())) {
            System.out.println("Tipo de serviço incompatível com o box."); return;
        }
        int ocupadas = 0;
        for (OrdemServico o : ordens)
            if (o.getBox() == box && o.getStatus() == StatusOrdem.EM_EXECUCAO) ocupadas++;
        if (ocupadas >= box.getCapacidadeMaxima()) { System.out.println("Box lotado."); return; }
        os.setBox(box);
        os.setStatus(StatusOrdem.EM_EXECUCAO);
        System.out.println("Ordem atribuída.");
    }

    static void finalizarOrdem() {
        OrdemServico os = buscarOrdem(lerInt("Código da ordem: "));
        if (os == null || os.getStatus() != StatusOrdem.EM_EXECUCAO) {
            System.out.println("Ordem inexistente ou não está em execução."); return;
        }
        os.setStatus(StatusOrdem.FINALIZADA);
        System.out.println("Ordem finalizada.");
    }

    static void ordensDoBox() {
        Box box = buscarBox(lerInt("Número do box: "));
        if (box == null) { System.out.println("Box não encontrado."); return; }
        int total = 0;
        for (OrdemServico o : ordens) if (o.getBox() == box) { System.out.println(o); total++; }
        System.out.println("Total de ordens: " + total);
    }

    static void finalizadasPorBox() {
        for (Box b : boxes) {
            int c = 0;
            for (OrdemServico o : ordens)
                if (o.getBox() == b && o.getStatus() == StatusOrdem.FINALIZADA) c++;
            System.out.println("Box " + b.getNumero() + ": " + c + " finalizada(s)");
        }
    }

    static void buscarPorStatus() {
        int op = lerInt("1-Aberta 2-Em execução 3-Finalizada: ");
        if (op < 1 || op > 3) { System.out.println("Opção inválida."); return; }
        StatusOrdem st = StatusOrdem.values()[op - 1];
        for (OrdemServico o : ordens) if (o.getStatus() == st) System.out.println(o);
    }

    static void detalhes() {
        OrdemServico os = buscarOrdem(lerInt("Código da ordem: "));
        System.out.println(os == null ? "Ordem não encontrada." : os);
    }

    public static void main(String[] args) {
        criarDadosIniciais();
        int op;
        do {
            System.out.println("\n1-Cadastrar ordem  2-Associar mecânico  3-Atribuir ordem a box"
                + "\n4-Ordens de um box  5-Finalizadas por box  6-Buscar por status"
                + "\n7-Detalhes da ordem  8-Finalizar ordem  0-Sair");
            op = lerInt("Opção: ");
            switch (op) {
                case 1: cadastrarOrdem(); break;
                case 2: associarMecanico(); break;
                case 3: atribuirOrdem(); break;
                case 4: ordensDoBox(); break;
                case 5: finalizadasPorBox(); break;
                case 6: buscarPorStatus(); break;
                case 7: detalhes(); break;
                case 8: finalizarOrdem(); break;
                case 0: System.out.println("Encerrando."); break;
                default: System.out.println("Opção inválida.");
            }
        } while (op != 0);
    }
}