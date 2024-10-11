package model;
public class Comando {
    public short format;
    public short opcode;
    public short reg_dst;
    public short reg_1;
    public short reg_2;
    public short imediato;
    
    public Comando (){
    }

    public void printar_Comando(){
        System.out.printf("Format: %d ", (int)this.format);
        switch((int)this.format){
            case 0:
                System.out.printf("Opcode: %d, Reg_dst: %d, reg1: %d, reg2: %d\n", (int)this.opcode, (int)this.reg_dst, (int)this.reg_1, (int)this.reg_2);
                break;
            
            case 1:
                System.out.printf("Opcode: %d, reg_dst: %d, imediato: %d\n", (int)this.opcode, (int)this.reg_dst, (int)this.imediato);
                break;
        }
    }
}
