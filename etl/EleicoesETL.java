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
import java.util.stream.Collectors;

@Command(name = "EleicoesETL", mixinStandardHelpOptions = true, version = "EleicoesETL 0.1", description = "EleicoesETL made with jbang")
class EleicoesETL implements Callable<Integer> {
	
	private static final String ELEICOES_PATH = "/home/pesilva/Documentos/Pessoal/eleicoes_2024/";
	private static final String CAND_INFO_FILE = ELEICOES_PATH + "consulta_cand_2024/consulta_cand_2024_SP.csv";
	
	private static final String INFO_TEXT = " %s, é o candidato(a) a %s pelo município de %s - %s na eleição %s de %s pelo %s (%s). "
										+ "%s é %s, nasceu em %s, se declara do gênero %s da cor/raça %s, sua ocupação é %s e seu grau de instrução é %s. "
										+ "Seu nome de urna é %s, e seu número de urna é %s";
	
	public record Candidato(String code, String cargo, String nome, String municipio, String uf, String abrangencia,
							String ano, String partido, String sigla, String estadoCivil, String dataNascimento, 
							String genero, String racaCor, String instrucao, String ocupacao, String nomeUrna, String numUrna) {}

    @Parameters(index = "0", description = "The greeting to print", defaultValue = "World!")
    private String greeting;

    public static void main(String... args) {
        int exitCode = new CommandLine(new EleicoesETL()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
    	List<Candidato> candidatos = getCandidatoData();
		Candidato c = candidatos.get(10);
		System.out.println(String.format(INFO_TEXT, c.nome, c.cargo, c.municipio, c.uf, c.abrangencia, c.ano,
				c.partido, c.sigla, c.nome, c.estadoCivil, c.dataNascimento, c.genero, c.racaCor, c.ocupacao,
				c.instrucao, c.nomeUrna, c.numUrna));
		
        return 0;
    }
    
    public List<Candidato> getCandidatoData() {
    	List<String> lines = readCSV(Paths.get(CAND_INFO_FILE));
    	return lines.stream().filter(s -> s.contains("SÃO JOSÉ DOS CAMPOS")).map(this::buildCandidato).collect(Collectors.toList());	
    }

    //TODO: clean text with \"\"
	private Candidato buildCandidato(String line) {
		String[] fields = line.split(";");
		return new Candidato(fields[15], fields[14], fields[17], fields[12], fields[10], fields[9], fields[2],
		fields[27], fields[26], fields[43], fields[36], fields[39], fields[45], fields[41],
		fields[47], fields[18], fields[25]);
	}
	
    
    private List<String> readCSV(Path path) {
    	try {
    		return Files.readAllLines(path, Charset.forName("ISO-8859-1"));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return null;
    }
}
