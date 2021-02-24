package jmilan;

import java.io.PrintStream;
import java.util.Vector;

public class CodeEmitter {
    private class Command {
        public Command(Opcode op) {
            this.opcode = op;
            this.arg = 0;
        }

        public Command(Opcode op, int arg) {
            this.opcode = op;
            this.arg = arg;
        }

        public void print(PrintStream ps, int address) {
            if(opcode.needArgument()) {
                ps.format("%d:\t%s\t\t%d\n", address, opcode, arg);
            }
            else {
                ps.format("%d:\t%s\n", address, opcode);
            }
        }

        private Opcode opcode;
        private int arg;
    }

    public enum Opcode {
        NOP,
        STOP,
        LOAD {
            @Override
            public boolean needArgument() {
                return true;
            }
        },
        STORE {
            @Override
            public boolean needArgument() {
                return true;
            }
        },
        BLOAD {
            @Override
            public boolean needArgument() {
                return true;
            }
        },
        BSTORE {
            @Override
            public boolean needArgument() {
                return true;
            }
        },
        PUSH {
            @Override
            public boolean needArgument() {
                return true;
            }
        },
        POP,
        DUP,
        ADD,
        SUB,
        MULT,
        DIV,
        INVERT,
        COMPARE {
            @Override
            public boolean needArgument() {
                return true;
            }
        },
        JUMP {
            @Override
            public boolean needArgument() {
                return true;
            }
        },
        JUMP_YES {
            @Override
            public boolean needArgument() {
                return true;
            }
        },
        JUMP_NO {
            @Override
            public boolean needArgument() {
                return true;
            }
        },
        INPUT,
        PRINT;

        public boolean needArgument() {
            return false;
        }
    }

    public CodeEmitter(PrintStream ps) {
        this.stream = ps;
        this.address = 0;
        this.buffer = new Vector<Command>();
        this.nopCommand = new Command(Opcode.NOP);
    }

    public CodeEmitter() {
        this(System.out);
    }

    public void emit(Opcode opcode) {
        address++;
        buffer.add(new Command(opcode));
    }

    public void emit(Opcode opcode, int arg) {
        address++;
        buffer.add(new Command(opcode, arg));
    }

    public void emitAt(int addr, Opcode opcode) {
        buffer.set(addr, new Command(opcode));
    }

    public void emitAt(int addr, Opcode opcode, int arg) {
        buffer.set(addr, new Command(opcode, arg));
    }

    public void flush()
    {
        int addr;
        for(addr = 0; addr < buffer.size(); ++addr) {
            buffer.get(addr).print(stream, addr);
        }
        
        stream.flush();
    }

    public int makeHole()
    {
        buffer.add(address, nopCommand);
        return address++;
    }

    public int relationCode(Cmp r) {
        switch(r) {
            case EQ: return 0;
            case NE: return 1;
            case LT: return 2;
            case GT: return 3;
            case LE: return 4;
            case GE: return 5;
        }

        return 0;
    }

    public int getCurrentAddress() {
        return address;
    }

    private int address;
    private PrintStream stream;
    private Vector<Command> buffer;
    Command nopCommand;
}
