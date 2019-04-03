package br.com.gecnab.retorno.bb.cbr643;

import static org.jrimum.utilix.Collections.checkNotEmpty;
import static org.jrimum.utilix.Collections.hasElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jrimum.texgit.Record;

import br.com.gecnab.utils.AbstractFlatFile;


public class ProccessCBR643 extends AbstractFlatFile{

	
	private Protocolo protocolo;
	
	private List<TransacaoDeTitulo> transacoes;

	private Sumario sumario;
	
	private static final String LAYOUT = "/layouts/LayoutBbCBR643Convenio7Retorno.txg.xml"; // lê da raiz da pasta resource
	
	private ProccessCBR643(){
		super(LAYOUT);
	}
	
	private ProccessCBR643(String cfgFile) {
		super(cfgFile);
	}
	
	public static ProccessCBR643 newInstance(List<String> lines) {
		
		checkNotEmpty(lines, "Linhas ausentes!");

		ProccessCBR643 ff = new ProccessCBR643();
		
		ff.read(lines);
		
		ff.loadInfo();
		
		return ff;

	}

	public Protocolo getProtocolo() {
		return protocolo;
	}

	public Sumario getSumario() {

		return sumario;
	}

	public List<TransacaoDeTitulo> getTransacoes() {

		return transacoes;
	}

	private void loadInfo() {

		this.protocolo = new Protocolo(getFlatFile().getRecord("Header"));
		
		this.sumario = new Sumario(getFlatFile().getRecord("Trailler"));
		
		Collection<Record> registrosDeTransacoes = getFlatFile().getRecords("TransacaoTitulo");

		if (hasElement(registrosDeTransacoes)) {
			ArrayList<TransacaoDeTitulo> transacoes = new ArrayList<TransacaoDeTitulo>(registrosDeTransacoes.size());
			for (Record registro : registrosDeTransacoes) {
				try {
					transacoes.add(new TransacaoDeTitulo(registro));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			this.transacoes = transacoes;
		}
	}

}
