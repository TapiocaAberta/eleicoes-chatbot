///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.6.3


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "EleicoesETL", mixinStandardHelpOptions = true, version = "EleicoesETL 0.1", description = "EleicoesETL made with jbang")
class EleicoesETL implements Callable<Integer> {
	
	private static String ELEICOES_PATH = "/home/pesilva/Documentos/Pessoal/eleicoes_2024/";
	private static final String CAND_INFO_FILE = ELEICOES_PATH + "consulta_cand_2024/consulta_cand_2024_SP.csv";
	
	public record Candidato(String nome) {}

    @Parameters(index = "0", description = "The greeting to print", defaultValue = "World!")
    private String greeting;

    public static void main(String... args) {
        int exitCode = new CommandLine(new EleicoesETL()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception { // your business logic goes here...
    	getCandidatoData();
        return 0;
    }
    
    public void getCandidatoData() {
    	List<String> lines = readCSV(Paths.get(CAND_INFO_FILE));
    	
    	lines.stream().filter(s -> s.contains("SÃO JOSÉ DOS CAMPOS")).forEach(s -> { 
    		String[] fields = s.split(";");
    		Candidato c = new Candidato(fields[18]);
    		System.out.println(c.nome);
    	});	
    }
    
    public List<String> readCSV(Path path) {
    	try {
    		return Files.readAllLines(path, Charset.forName("ISO-8859-1"));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return null;
    }
}
