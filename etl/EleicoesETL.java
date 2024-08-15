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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Command(name = "EleicoesETL", mixinStandardHelpOptions = true, version = "EleicoesETL 0.1", description = "EleicoesETL made with jbang")
class EleicoesETL implements Callable<Integer> {
	
	private static final String SEPARATOR = ";";
	private static final String MUNICIPIO = "SÃO JOSÉ DOS CAMPOS";
	private static final String CHARSET = "ISO-8859-1";
	
	private static final String DATA_MD_PATH = "../data/";
	private static final String ELEICOES_PATH = "/home/pesilva/Documentos/Pessoal/eleicoes_2024/";
	private static final String CAND_INFO_FILE = ELEICOES_PATH + "consulta_cand_2024/consulta_cand_2024_SP.csv";
	private static final String BENS_CAND_FILE = ELEICOES_PATH + "bem_candidato_2024/bem_candidato_2024_SP.csv";
	private static final String REDE_SOCIAL_CAND_FILE = ELEICOES_PATH + "rede_social_candidato_2024/rede_social_candidato_2024_SP.csv";
	
	private static final String INFO_TEXT = "# %s\n"
										+ "## %s - %s (%s)\n"
										+ "---\n"
										+ "**%s**, é candidato a **%s** pelo município de **%s - %s** na eleição **%s** de **%s** pelo **%s (%s)**.\n"
										+ "**%s** é **%s**, nasceu em **%s**, se declara do gênero **%s** da cor/raça **%s**, sua ocupação é **%s**, e seu grau de instrução é "
										+ "**%s**.\n"
										+ "Seu nome de urna é **%s**, e seu número de urna é **%s**.";
	
	private static final String BENS_TEXT = "\n- %s (%s) no valor de R$ %s";
	
	public record Redes(String candidatoCode, String url) {}
	public record Bens(String candidatoCode, String tipoBen, String descBen, String valorBen) {}
	public record Candidato(String code, String cargo, String nome, String municipio, String uf, String abrangencia,
							String ano, String partido, String sigla, String estadoCivil, String dataNascimento, 
							String genero, String racaCor, String instrucao, String ocupacao, String nomeUrna, String numUrna) {}

    public static void main(String... args) {
        int exitCode = new CommandLine(new EleicoesETL()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
    	
    	Map<String, List<Bens>> bens = getBensCandidato();
    	Map<String, List<Redes>> redes = getRedesSociais();
    	List<Candidato> candidatos = getCandidatoData();
    	
    	StringBuilder prefeitos = new StringBuilder("Lista de todos os candidatos a PREFEITO em " + MUNICIPIO + " para 2024:\n");
    	StringBuilder vereadores = new StringBuilder("Lista de todos os candidatos a VEREADOR em " + MUNICIPIO + " para 2024:\n");
    	
		candidatos.forEach(c -> {
			if ("PREFEITO".equalsIgnoreCase(c.cargo.replaceAll("\"", "").strip())) {
				prefeitos.append(String.format("- %s - %s (%s)", c.nome, c.partido, c.sigla));
				prefeitos.append("\n");
			} else if ("VEREADOR".equalsIgnoreCase(c.cargo.replaceAll("\"", "").strip())) {
				vereadores.append(String.format("- %s - %s (%s)", c.nome, c.partido, c.sigla));
				vereadores.append("\n");
			}
			
			String txt = buildCandidatoDataText(c).concat(buildBensText(bens.get(c.code), c.nome))
					.concat(buildRedesText(redes.get(c.code), c.nome)).replaceAll("\"", "").strip();
			try {
				Path path = Paths.get(DATA_MD_PATH + c.code + ".md");
				Files.write(path, txt.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		try {
			Files.write(Paths.get(DATA_MD_PATH + "prefeitos.md"), prefeitos.toString().replaceAll("\"", "").strip().getBytes());
			Files.write(Paths.get(DATA_MD_PATH + "vereadores.md"), vereadores.toString().replaceAll("\"", "").strip().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    	return 0;
    }
    
    private String buildBensText(List<Bens> bens, String nome) {
    	if(Objects.isNull(bens)) {
    		return "\n\nO Candidato(a) " + nome + " não declarou bens.\n";
    	}
    	
    	var base = "\n\nOs bens declarados do candidato(a) " + nome + " são: ";
    	return  base + bens.stream().map(b -> String.format(BENS_TEXT, b.tipoBen, b.descBen, b.valorBen))
    								.collect(Collectors.joining(";"));
    }
    
    private String buildRedesText(List<Redes> redes, String nome) {
    	
    	if(Objects.isNull(redes)) {
    		return "\n\nO Candidato(a) " + nome + " não forneceu dados das redes sociais.\n";
    	}
    	
    	String base = "\n\nAs redes sociais de " + nome + " são:\n";
    	return base + redes.stream().map(r -> "- " + r.url + ";\n").collect(Collectors.joining());
    }
    
	private String buildCandidatoDataText(Candidato c) {
		return String.format(INFO_TEXT, c.cargo, c.nome, c.partido, c.sigla, c.nome, c.cargo, c.municipio, c.uf, c.abrangencia, c.ano, c.partido, c.sigla,
				c.nome, c.estadoCivil, c.dataNascimento, c.genero, c.racaCor, c.ocupacao, c.instrucao, c.nomeUrna,
				c.numUrna);
	}
    
    private List<Candidato> getCandidatoData() {
    	List<String> lines = readCSV(Paths.get(CAND_INFO_FILE), MUNICIPIO);
    	return lines.stream().map(this::buildCandidato).collect(Collectors.toList());	
    }
    
	private Candidato buildCandidato(String line) {
		String[] fields = line.split(SEPARATOR);
		return new Candidato(fields[15], fields[14], fields[17], fields[12], fields[10], fields[9], fields[2],
		fields[27], fields[26], fields[43], fields[36], fields[39], fields[45], fields[41],
		fields[47], fields[18], fields[25]);
	}
	
	private Map<String, List<Bens>> getBensCandidato() {
		List<String> lines = readCSV(Paths.get(BENS_CAND_FILE), MUNICIPIO);
		return lines.stream().map(this :: buildBens).collect(Collectors.groupingBy(Bens :: candidatoCode));
	}
	
	private Bens buildBens(String line) {
		String[] fields = line.split(SEPARATOR);
		return new Bens(fields[11], fields[14], fields[15], fields[16]);
	}
	
	private Map<String, List<Redes>> getRedesSociais() {
		List<String> lines = readCSV(Paths.get(REDE_SOCIAL_CAND_FILE));
		return lines.stream().map(this :: buildRedes).collect(Collectors.groupingBy(Redes :: candidatoCode));
	}
	
	private Redes buildRedes(String line) {
		String[] fields = line.split(SEPARATOR);
		return new Redes(fields[8], fields[10]);
	}
    
	private List<String> readCSV(Path path) {
		return readCSV(path, null);
	}
	
    private List<String> readCSV(Path path, String municipio) {
    	
    	try {
    		Stream<String> allLines = Files.readAllLines(path, Charset.forName(CHARSET)).stream();
    		if(Objects.isNull(municipio)) {
    			return allLines.collect(Collectors.toList());
    		}
    		return allLines.filter(s -> s.contains(municipio)).collect(Collectors.toList());
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return null;
    }
}
