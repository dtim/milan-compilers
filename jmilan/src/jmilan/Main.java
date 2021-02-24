package jmilan;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import com.adarshr.args.ArgsEngine;

public class Main {  
    public static void printHelp() {
        System.out.println("JMilan: pure java version of Milan compiler");
        System.out.println("Usage: java -jar JMilan.jar [-f] [-o <output_file>] <input_file>");
        System.out.println("  -h               print this help message and quit");
        System.out.println("  -o <output_file> print output to file <output_file> (stdout by default)");
    }

    public static void main(String[] args) {
        try {
            ArgsEngine engine = new ArgsEngine();
            engine.add("-o", "--output", true);
            engine.add("-h", "--help");

            engine.parse(args);

            if(engine.getBoolean("-h")) {
                printHelp();
            }
            else {
                PrintStream output = System.out;

                if(engine.getBoolean("-o")) {
                    output = new PrintStream(engine.getString("-o"));
                }
                
                String[] inputFiles = engine.getNonOptions();
                if(inputFiles.length == 1) {
                    Scanner scanner = new Scanner(inputFiles[0]);
                    Parser parser = new Parser(scanner, output);
                    parser.parse();
                    scanner.close();
                }
                else if(inputFiles.length > 1) {
                   System.out.println("Too many input files");
                }
                else {
                    System.out.println("Input file must be specified");
                }
            }
        }
        catch(FileNotFoundException e) {
            System.err.printf("File not found: %s\n", e.getMessage());
        }
        catch(IOException e) {
            System.err.printf("Error processing file: %s\n", e.getMessage());
        }
        catch(RuntimeException e) {
            System.err.printf("Error: %s\n", e.getMessage());
        }
    }
}
