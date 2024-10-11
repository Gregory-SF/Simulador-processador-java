package funcoes;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import model.*;
import view.*;

public class Processador {
	final static int memorySize = 1024;
	final static int NUM_REGISTERS = 8;
	static short memory [] = new short [memorySize];  
	static short registers [] = new short [NUM_REGISTERS];

	public static short extract_bits (short value, int bstart, int blength) {
		short mask = (short)((1 << blength) - 1);
		return (short)((value >> bstart) & mask);
	}

	public static void memory_write (short addr, short value) { 
		memory[addr] = value;
	}

	public static void load_binary (String binary_name) {
		try {
			FileInputStream fileInputStream = new FileInputStream(binary_name);
			DataInputStream dataInputStream = new DataInputStream(fileInputStream);

			long tamanhoArquivo = fileInputStream.getChannel().size();

			int numShorts = (int) (tamanhoArquivo / 2);

			System.out.println("Sla antes do for");

			for (int i = 0; i < numShorts; i++) {
				int low = dataInputStream.readByte() & 0x000000FF;
				int high = dataInputStream.readByte() & 0x000000FF;
				int value = (low | (high << 8)) & 0x0000FFFF;

				memory_write((short)i, (short)value);
			}

			System.out.println("Sla depois do for");

			dataInputStream.close();
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static short buscar_instrucao (int pc) {
		return memory[pc];
	}

	public static int shorToInt(short value) {
		return (int) value & 0x0000FFFF;
	} 

	public static void printar_registradores(){
		for(int i = 0; i < NUM_REGISTERS; i++){
			System.out.printf("Registrador [%d]: %d\n", i, shorToInt(registers[i]));	
		}
		System.out.println();
	}

    public static void printar_memory(){
        for(int index = 0; index <= 100; index++){
            System.out.printf("memory[%d]: %d\n",index, shorToInt(memory[index]));
        }
    }

	public static void decodificar_instrucao(short instrucao, Comando comando) {
        comando.format = extract_bits(instrucao, 15,1);
        switch (comando.format) {
            case 0:
                comando.opcode = extract_bits(instrucao, 9, 6);
                comando.reg_dst = extract_bits(instrucao, 6, 3);
                comando.reg_1 = extract_bits(instrucao, 3, 3);
                comando.reg_2 = extract_bits(instrucao, 0, 3);
                break;
            case 1:
                comando.opcode = extract_bits(instrucao, 13, 2);
                comando.reg_dst = extract_bits(instrucao, 10, 3);
                comando.imediato = extract_bits(instrucao, 0, 10);
                break;
        }
	}

	public static void add(Comando comando) {
		registers[comando.reg_dst] = (short) (registers[comando.reg_1] + registers[comando.reg_2]);
		System.out.printf("Valor em register[%d]: %d, registers[%d]: %d, registers[%d]: %d\n", shorToInt(comando.reg_1), shorToInt(registers[comando.reg_1]), shorToInt(comando.reg_2),  shorToInt(registers[comando.reg_2]), shorToInt(comando.reg_dst),  shorToInt(registers[comando.reg_dst]));
	}

	public static void sub(Comando comando) {
		registers[comando.reg_dst] = (short) (registers[comando.reg_1] - registers[comando.reg_2]);
		System.out.printf("Valor em register[%d]: %d, registers[%d]: %d, registers[%d]: %d\n", shorToInt(comando.reg_1), shorToInt(registers[comando.reg_1]), shorToInt(comando.reg_2), shorToInt(registers[comando.reg_2]), shorToInt(comando.reg_dst), shorToInt(registers[comando.reg_dst]));
	}

	public static void mul(Comando comando) {
		registers[comando.reg_dst] = (short) (registers[comando.reg_1] * registers[comando.reg_2]);
		System.out.printf("Valor em register[%d]: %d, registers[%d]: %d, registers[%d]: %d\n", shorToInt(comando.reg_1), shorToInt(registers[comando.reg_1]), shorToInt(comando.reg_2), shorToInt(registers[comando.reg_2]), shorToInt(comando.reg_dst), shorToInt(registers[comando.reg_dst]));
	}	

	public static void divi(Comando comando) {
		if (registers[comando.reg_2] != 0) {
			registers[comando.reg_dst] = (short) (registers[comando.reg_1] / registers[comando.reg_2]);
			System.out.printf("Valor em register[%d]: %d, registers[%d]: %d, registers[%d]: %d\n", shorToInt(comando.reg_1), shorToInt(registers[comando.reg_1]), shorToInt(comando.reg_2), shorToInt(registers[comando.reg_2]), shorToInt(comando.reg_dst), shorToInt(registers[comando.reg_dst]));
		} else {
			System.out.println("Erro: Divisão por zero.");
		}
		
	}

	public static void cmp_equal(Comando comando) {
		registers[comando.reg_dst] = (short) ((registers[comando.reg_1] == registers[comando.reg_2]) ? 1 : 0);
		System.out.printf("Valor em registers[%d]: %d, valor em registers[%d]: %d\n", shorToInt(comando.reg_1), shorToInt(registers[comando.reg_1]), shorToInt(comando.reg_2), shorToInt(registers[comando.reg_2]));
	}

	public static void cmp_neq(Comando comando) {
		registers[comando.reg_dst] = (short)((registers[comando.reg_1] != registers[comando.reg_2]) ? 1 : 0);
		System.out.printf("Valor em registers[%d]: %d, valor em registers[%d]: %d\n", shorToInt(comando.reg_1), shorToInt(registers[comando.reg_1]), shorToInt(comando.reg_2), shorToInt(registers[comando.reg_2]));
	}

	public static void syscall() {
		switch (registers[0]){
			case 0:
				printar_registradores();
				printar_memory();
				System.out.println("Encerrando o programa.");
				System.exit(0); 
				break;
			case 1:
				int i = registers[1];
				while(memory[i] != 0){
					System.out.printf("%c", (char)memory[i]);
					i++;
				}
				System.out.println();;
				break;
			case 2:
				System.out.println();;
				break;
			case 3:
				System.out.printf("Valor em no registers[1]: %d\n",(int)registers[1]);
				break;
			default:
				System.out.printf("Serviço do sistema não suportado ou não implementado: %d\n", shorToInt(registers[0]));
				break;
			}
	}

	public static void load(Comando comando) {
		registers[comando.reg_dst] = memory[registers[comando.reg_1]];
		System.out.printf("Valor em registers[%d]: %d, valor em registers[%d]: %d, valor na memória: %d\n", shorToInt(comando.reg_1), shorToInt(registers[comando.reg_1]), shorToInt(comando.reg_dst), shorToInt(registers[comando.reg_dst]), shorToInt(memory[registers[comando.reg_1]]));
	}

	public static void store(Comando comando) {
		memory[registers[comando.reg_1]] = registers[comando.reg_2];
		System.out.printf("Valor em regiters[%d]: %d, valor em registers[%d]: %d, valor na memória: %d\n", shorToInt(comando.reg_1), shorToInt(registers[comando.reg_1]), shorToInt(comando.reg_2), shorToInt(registers[comando.reg_2]), shorToInt(memory[registers[comando.reg_1]]));
	}

	public static void jump(Comando comando) {
		System.out.println("pc: "+Main.pc);
		Main.pc = comando.imediato-1;
		System.out.println("pc: "+Main.pc);
	}

	public static void jump_cond(Comando comando) {
		if (registers[comando.reg_dst] == 1) {
			System.out.println("pc: "+Main.pc);
			Main.pc = comando.imediato-1;
			System.out.println("pc: "+Main.pc);
		}
	}

	public static void mov(Comando comando) {
		registers[comando.reg_dst] = comando.imediato;
		System.out.printf("Valor em registers[%d]: %d\n", shorToInt(comando.reg_dst), shorToInt(registers[comando.reg_dst]));
	}

	public static void executar_instrucao(Comando comando){
		switch (comando.format) {
			case 0: // Instrução do tipo R
				switch (comando.opcode) {
					case 0:
						add(comando);
						break;
					case 1:
						sub(comando);
						break;
					case 2: 
						mul(comando);
						break;
					case 3:
						divi(comando);
						break;
					case 4:
						cmp_equal(comando);
						break;
					case 5:
						cmp_neq(comando);
						break;
					case 15:
						load(comando);
						break;
					case 16:
						store(comando); 
						break;
					case 63:
						syscall();
						break;
					default:
						System.out.println("Erro: Opcode desconhecido.");
						break;
					}
				break;
			case 1: // Instrução do tipo I
				switch (comando.opcode){
					case 0:
						jump(comando);
						break;
					case 1:
						jump_cond(comando);
						break;
					case 3:
						mov(comando);
						break;
					default:
						System.out.println("Erro: Opcode desconhecido.");
						break;
				}
				break;
		}
	}

}
