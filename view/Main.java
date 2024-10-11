package view;
import java.util.Scanner;
import funcoes.Processador;
import model.Comando;

public class Main {
    public static int pc = 1;
    public static void main(String[] args) {
        Scanner t = new Scanner(System.in);
        System.out.println("Digite o caminho absoluto do arquivo binário: ");
        String binPath = t.nextLine();
        t.close();
        Processador.load_binary(binPath);
        Processador.printar_memory();
        Comando comando = new Comando();
        while(true){
                short instrucao = Processador.buscar_instrucao(pc);
                Processador.decodificar_instrucao(instrucao, comando);
                System.out.println("Linha " + pc);
                comando.printar_Comando();
                Processador.executar_instrucao(comando);
                System.out.println("--------------------");
                pc++;
        }   
    }
}
