package br.com.gecnab.remessa.bb.cbr641;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jrimum.bopepo.pdf.Files;
import org.jrimum.domkee.comum.pessoa.endereco.CEP;
import org.jrimum.domkee.comum.pessoa.endereco.Endereco;
import org.jrimum.domkee.comum.pessoa.endereco.UnidadeFederativa;
import org.jrimum.domkee.comum.pessoa.id.cprf.AbstractCPRF;
import org.jrimum.domkee.comum.pessoa.id.cprf.CPRF;
import org.jrimum.domkee.financeiro.banco.febraban.Agencia;
import org.jrimum.domkee.financeiro.banco.febraban.Banco;
import org.jrimum.domkee.financeiro.banco.febraban.Carteira;
import org.jrimum.domkee.financeiro.banco.febraban.Cedente;
import org.jrimum.domkee.financeiro.banco.febraban.CodigoDeCompensacaoBACEN;
import org.jrimum.domkee.financeiro.banco.febraban.ContaBancaria;
import org.jrimum.domkee.financeiro.banco.febraban.NumeroDaConta;
import org.jrimum.domkee.financeiro.banco.febraban.Sacado;
import org.jrimum.domkee.financeiro.banco.febraban.TipoDeCobranca;
import org.jrimum.domkee.financeiro.banco.febraban.Titulo;
import org.jrimum.domkee.financeiro.banco.febraban.Titulo.Aceite;
import org.jrimum.texgit.FlatFile;
import org.jrimum.texgit.Record;
import org.jrimum.texgit.Texgit;
import org.jrimum.utilix.ClassLoaders;
import org.jrimum.utilix.text.DateFormat;

public class CBR641 {
	
private static int convenio = 2866935 ;
	
	private static int sequencial = new Random(945L).nextInt(); // Vari�vel usada para seta a sequencia dos arquivos
	private static String nDocumento = "000000088";	
	/**
	 * Classe geradora de arquivos de REMESSA para BANCO DO BRASIL - CNAB 400 
	 * @author Rayan
	 */
	
	/*
	 * NOTA : as linhas comentadas s�o setadas por padr�o no layout do arquivo
	 */
	public static void main(String[] args) throws IOException {

		//manTest();
		//manTest2();
		mainTest3();
	}

	private static void mainTest3() {
		
		CodigoDeCompensacaoBACEN codigoDeCompensacaoBACEN = new CodigoDeCompensacaoBACEN(17);
		Banco banco = new Banco(codigoDeCompensacaoBACEN, "Nome Cedente") ;
		ContaBancaria contaBancaria = new ContaBancaria(banco);
		Agencia agencia = new Agencia(1623, "2");
		contaBancaria.setAgencia(agencia);
		NumeroDaConta numeroDaConta = new NumeroDaConta(101916);
		numeroDaConta.setDigitoDaConta("0");
		contaBancaria.setNumeroDaConta(numeroDaConta);
		Carteira carteira= new Carteira(17, TipoDeCobranca.COM_REGISTRO, "COBRANCA");
		contaBancaria.setCarteira(carteira);
		
		Sacado sacado  = new Sacado("Antonio josé", AbstractCPRF.create("01010208489"));
		
		Endereco endereco = new Endereco();
		endereco.setBairro("BEIRA MAR");
		CEP cep = new CEP();
		cep.setCep("66075110");
		cep.setPrefixo(66075);
		cep.setSufixo(110);
		
		endereco.setCep("59145630");
		endereco.setLocalidade(UnidadeFederativa.RN.getCapital());
		endereco.setLogradouro("VALE DO APODI");
		endereco.setNumero("1456");
		endereco.setUF(UnidadeFederativa.RN);
		
		List<Endereco> enderecos = new ArrayList<>();
		enderecos.add(endereco);
		
		sacado.setEnderecos(enderecos);
		
		Cedente cedente = new Cedente("Wander Cleiton", AbstractCPRF.create("01010208489"));
		
		Titulo titulo = new Titulo(contaBancaria, sacado, cedente);
		titulo.setDataDoVencimento(DateUtils.addDays(new Date(), 3));
		titulo.setAceite(Aceite.N);
		titulo.setValor(new BigDecimal(100));
		titulo.setValorCobrado(new BigDecimal(130.00));
		titulo.setDataDoDocumento(new Date());
		
		List<Titulo> titulos = new ArrayList<Titulo>();
		titulos.add(titulo);
		
		
		mostreBoletoNaTela(createCBR641(titulos));
		
	}

	private static void manTest() throws FileNotFoundException, IOException {
		// Identificando Layout
		File layoutTemporario = new File("LayoutBbCBR641Convenio7Remessa.txg.xml");
		File layout = Files.bytesToFile(layoutTemporario, IOUtils.toByteArray(ClassLoaders.getResourceAsStream("/layouts/LayoutBbCBR641Convenio7Remessa.txg.xml")));

		// Criando o arquivo de remessa
		FlatFile<Record> ff = Texgit.createFlatFile(layout);
		
		// Registrando Header
		ff.addRecord(createHeader(ff));
		
		/* La�o para representar mais de 1 boleto registrado no arquivo
		*  Registrando T�tulos(boletos)
		**/
		for( sequencial = 1; sequencial <= 5; sequencial++){ 
			ff.addRecord(createTransacaoTitulos(ff, sequencial));
		}
		
		// Registrando Trailer
		ff.addRecord(createTrailer(ff, sequencial));

		// Salvando arquivo de remessa.
		File arquivoDeRemessa = new File("resource/arquivoRemessa/REMESSABB"+new SimpleDateFormat("ddMMyyyy").format(new Date())+".REM");
		FileUtils.writeLines(arquivoDeRemessa, ff.write(), "\r\n");
		System.out.println("Arquivo gerado com sucesso em: "+ arquivoDeRemessa);

		if (layoutTemporario != null) {
			layoutTemporario.delete();
		}
	}
	
	public static File createCBR641(List<Titulo> titulos) {

		File arquivoDeRemessa = new File("resource/arquivoRemessa/REMESSABB"+new SimpleDateFormat("ddMMyyyy").format(new Date())+RandomUtils.nextInt()+".REM");
		try {
			// Identificando Layout
			File layoutTemporario = new File("LayoutBbCBR641Convenio7Remessa.txg.xml");
			File layout = Files.bytesToFile(layoutTemporario, IOUtils.toByteArray(ClassLoaders.getResourceAsStream("/layouts/LayoutBbCBR641Convenio7Remessa.txg.xml")));
			
			// Criando o arquivo de remessa
			FlatFile<Record> ff = Texgit.createFlatFile(layout);
			
			// Registrando Header
			ff.addRecord(myCreateHeader(ff, titulos.get(0)));
			//Registrando T�tulos(boletos)
			ff.addRecord(myCreateTransacaoTitulos(ff, titulos));
			
			// Registrando Trailer
			ff.addRecord(createTrailer(ff, (titulos.size() + 1) ));
			
			FileUtils.writeLines(arquivoDeRemessa, ff.write(), "\r\n");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arquivoDeRemessa;
		
		
		
	}
	private static void manTest2() throws FileNotFoundException, IOException {
		// Identificando Layout
		File layoutTemporario = new File("LayoutBbCBR641Convenio7Remessa.txg.xml");
		File layout = Files.bytesToFile(layoutTemporario, IOUtils.toByteArray(ClassLoaders.getResourceAsStream("/layouts/LayoutBbCBR641Convenio7Remessa.txg.xml")));

		// Criando o arquivo de remessa
		FlatFile<Record> ff = Texgit.createFlatFile(layout);
		
		CodigoDeCompensacaoBACEN codigoDeCompensacaoBACEN = new CodigoDeCompensacaoBACEN(17);
		Banco banco = new Banco(codigoDeCompensacaoBACEN, "Nome Cedente") ;
		ContaBancaria contaBancaria = new ContaBancaria(banco);
		Agencia agencia = new Agencia(1623, "2");
		contaBancaria.setAgencia(agencia);
		NumeroDaConta numeroDaConta = new NumeroDaConta(101916);
		numeroDaConta.setDigitoDaConta("0");
		contaBancaria.setNumeroDaConta(numeroDaConta);
		Carteira carteira= new Carteira(17, TipoDeCobranca.COM_REGISTRO, "COBRANCA");
		contaBancaria.setCarteira(carteira);
		
		Sacado sacado  = new Sacado("Antonio josé", AbstractCPRF.create("01010208489"));
		
		Endereco endereco = new Endereco();
		endereco.setBairro("BEIRA MAR");
		CEP cep = new CEP();
		cep.setCep("66075110");
		cep.setPrefixo(66075);
		cep.setSufixo(110);
		
		endereco.setCep("59145630");
		endereco.setLocalidade(UnidadeFederativa.RN.getCapital());
		endereco.setLogradouro("VALE DO APODI");
		endereco.setNumero("1456");
		endereco.setUF(UnidadeFederativa.RN);
		
		List<Endereco> enderecos = new ArrayList<>();
		enderecos.add(endereco);
		
		sacado.setEnderecos(enderecos);
		
		Cedente cedente = new Cedente("Wander Cleiton", AbstractCPRF.create("01010208489"));
		
		Titulo titulo = new Titulo(contaBancaria, sacado, cedente);
		titulo.setDataDoVencimento(DateUtils.addDays(new Date(), 3));
		titulo.setAceite(Aceite.N);
		titulo.setValor(new BigDecimal(100));
		titulo.setValorCobrado(new BigDecimal(130.00));
		titulo.setDataDoDocumento(new Date());
		
		List<Titulo> titulos = new ArrayList<Titulo>();
		titulos.add(titulo);
		
		// Registrando Header
		ff.addRecord(myCreateHeader(ff, titulo));
		//Registrando T�tulos(boletos)
		ff.addRecord(myCreateTransacaoTitulos(ff, titulos));
		// Registrando Trailer
		ff.addRecord(createTrailer(ff, 2));
		
		
		
		
		/* La�o para representar mais de 1 boleto registrado no arquivo
		*  Registrando T�tulos(boletos)
		*
		for( sequencial = 1; sequencial <= 5; sequencial++){ 
			ff.addRecord(createTransacaoTitulos(ff, sequencial));
		}
		
		*/
		
		// Salvando arquivo de remessa.
		File arquivoDeRemessa = new File("resource/arquivoRemessa/REMESSABB"+new SimpleDateFormat("ddMMyyyy").format(new Date())+RandomUtils.nextInt()+".REM");
		FileUtils.writeLines(arquivoDeRemessa, ff.write(), "\r\n");
		mostreBoletoNaTela(arquivoDeRemessa);
		System.out.println("Arquivo gerado com sucesso em: "+ arquivoDeRemessa);

		if (layoutTemporario != null) {
			layoutTemporario.delete();
		}
	}
	
	private static Record myCreateHeader(FlatFile<Record> ff, Titulo titulo) {
		
		/*
		 * TODO NOTA : as linhas comentadas s�o setadas por padr�o no layout do arquivo
			header.setValue("IdentificacaoRegistro", 0);
			header.setValue("TipoOperacao", 1);
			header.setValue("IdentificacaoExtensoTipoOperacao", "REMESSA");
			header.setValue("IdentificacaoTipoServico", 1);
			header.setValue("IdentificacaoExtensoTipoServico", "COBRANCA");
			header.setValue("BB", "001BANCO DO BRASIL");
		 */
		Record header = ff.createRecord("Header");
		
		header.setValue("SequencialRemessa", StringUtils.leftPad(StringUtils.right(sequencial+"", 5), 7, "0")); // "0000000"
		header.setValue("PrefixoAgencia",titulo.getContaBancaria().getAgencia().getCodigo());
		header.setValue("DigitoVerificadorAgencia", titulo.getContaBancaria().getAgencia().getDigitoVerificador());
		header.setValue("NumeroContaCorrente", titulo.getContaBancaria().getNumeroDaConta().getCodigoDaConta());
		header.setValue("DigitoVerificadorContaCorrente", titulo.getContaBancaria().getNumeroDaConta().getDigitoDaConta());
		
	
		

		return header;
		
	}
	
	private static Record createHeader(FlatFile<Record> ff) {
				
		/*
		 * NOTA : as linhas comentadas s�o setadas por padr�o no layout do arquivo
		 */
		Record header = ff.createRecord("Header");

		/* 
		header.setValue("IdentificacaoRegistro", 0);
		header.setValue("TipoOperacao", 1);
		header.setValue("IdentificacaoExtensoTipoOperacao", "REMESSA");
		header.setValue("IdentificacaoTipoServico", 1);
		header.setValue("IdentificacaoExtensoTipoServico", "COBRANCA");
		*/
		header.setValue("PrefixoAgencia", 1623);
		header.setValue("DigitoVerificadorAgencia", 2);
		header.setValue("NumeroContaCorrente", 101916);
		header.setValue("DigitoVerificadorContaCorrente", 0);
		header.setValue("NomeCedente", "FADESP");
		//header.setValue("BB", "001BANCO DO BRASIL");
		header.setValue("DataGravacao", 170118);
		header.setValue("SequencialRemessa", "0000000");
		header.setValue("NumeroConvenioLider", convenio);
		header.setValue("SequencialRegistro", 1);

		return header;
	}
	
	private static Record myCreateTransacaoTitulos(FlatFile<Record> ff, List<Titulo> titulo) {
		
		/*
		 * NOTA : as linhas comentadas s�o setadas por padr�o no layout do arquivo
		 */
		Record transacaoTitulos = ff.createRecord("TransacaoTitulo");
		
		titulo.forEach(t -> {
			int sequencial = 1;
			
			/*
			transacaoTitulos.setValue("IdentiFicacaoRegistroDetalhe", 7);
			transacaoTitulos.setValue("TipoInscricaoCedente", 02);
			*/
			transacaoTitulos.setValue("NumeroCpfCnpjCedente", t.getCedente().getCPRF().getCodigoComZeros());
			transacaoTitulos.setValue("PrefixoAgencia", t.getContaBancaria().getAgencia().getCodigo());
			transacaoTitulos.setValue("DigitoVerificadorAgencia", t.getContaBancaria().getAgencia().getDigitoVerificador());
			transacaoTitulos.setValue("NumeroContaCorrenteCedente", t.getContaBancaria().getNumeroDaConta().getCodigoDaConta());
			transacaoTitulos.setValue("DigitoVerificadorConta", t.getContaBancaria().getNumeroDaConta().getDigitoDaConta());
			transacaoTitulos.setValue("NumeroCovenioCobrancaCedente", convenio);
			//transacaoTitulos.setValue("CodigoControleEmpresa", "");
			transacaoTitulos.setValue("NossoNumero", convenio+nDocumento+sequencial);
			/*
			transacaoTitulos.setValue("NumeroPrestacao", 00);
			transacaoTitulos.setValue("GrupoValor", 00);
			transacaoTitulos.setValue("IndicativoMensagemSacador", "");
			transacaoTitulos.setValue("PrefixoTitulo", "   ");
			*/
			transacaoTitulos.setValue("VariacaoCarteira", "019");
			/*
			transacaoTitulos.setValue("ContaCaucao", 0);
			transacaoTitulos.setValue("NumeroBordero", 000000);
			transacaoTitulos.setValue("TipoCobranca", "     ");
			*/
			transacaoTitulos.setValue("CarteiraCobranca", t.getContaBancaria().getCarteira().getCodigo());
			transacaoTitulos.setValue("Comando", 01);
			//transacaoTitulos.setValue("NumeroTituloAtribuidoCedente", "          ");
			transacaoTitulos.setValue("DataVencimento", t.getDataDoVencimento());
			transacaoTitulos.setValue("ValorTitulo", 1);
			/*transacaoTitulos.setValue("NumeroBanco", 001);
			transacaoTitulos.setValue("PrefixoAgenciaCobradora", 0000);
			transacaoTitulos.setValue("DigitoVerificadorPrefixoAgenciaCobradora", " ");
			*/
			transacaoTitulos.setValue("EspecieTitulo", 12);
			transacaoTitulos.setValue("AceiteTitulo", "N");
			transacaoTitulos.setValue("DataEmissao", 170119);
			/*
			transacaoTitulos.setValue("InstrucaoCodificada", "  ");
			transacaoTitulos.setValue("InstrucaoCodificada", "  ");
			*/
			transacaoTitulos.setValue("JurosMoraDiaAtraso", "00000000003");
			/*transacaoTitulos.setValue("DataLimite", "      ");
	 		transacaoTitulos.setValue("ValorDesconto", "           "); 
			transacaoTitulos.setValue("ValorIof", "           ");
			transacaoTitulos.setValue("ValorAbatimento", "           ");
			*/
			transacaoTitulos.setValue("TipoInscricaoSacado", 01);
			transacaoTitulos.setValue("NumeroCnpjCpfSacado", t.getSacado().getCPRF().getCodigoComZeros());
			transacaoTitulos.setValue("NomeSacado", t.getSacado().getNome());
			//transacaoTitulos.setValue("ComplementoRegistro", "");
			transacaoTitulos.setValue("EnderecoSacado", t.getSacado().getNextEndereco().getLogradouro());
			transacaoTitulos.setValue("BairroSacado", t.getSacado().getNextEndereco().getBairro());
			transacaoTitulos.setValue("CepEnderecoSacado", t.getSacado().getNextEndereco().getCEP().getCep());
			transacaoTitulos.setValue("CidadeSacado", t.getSacado().getNextEndereco().getLocalidade());
			transacaoTitulos.setValue("UfCidadeSacado", t.getSacado().getNextEndereco().getUF());
			/*
			transacaoTitulos.setValue("Observacao", "                                        ");
			transacaoTitulos.setValue("NumeroDiasProtesto", "  ");
			transacaoTitulos.setValue("ComplementoRegistro", " ");
			*/
			transacaoTitulos.setValue("SequencialRegistro", sequencial);
			
			 sequencial++;
		});

		
		
		return transacaoTitulos;
		
	}
	private static Record createTransacaoTitulos(FlatFile<Record> ff, int sequencial) {
			
		/*
		 * NOTA : as linhas comentadas s�o setadas por padr�o no layout do arquivo
		 */
		Record transacaoTitulos = ff.createRecord("TransacaoTitulo");

		/*
		transacaoTitulos.setValue("IdentiFicacaoRegistroDetalhe", 7);
		transacaoTitulos.setValue("TipoInscricaoCedente", 02);
		*/
		transacaoTitulos.setValue("NumeroCpfCnpjCedente", "06313870000441");
		transacaoTitulos.setValue("PrefixoAgencia", 1623);
		transacaoTitulos.setValue("DigitoVerificadorAgencia", 2);
		transacaoTitulos.setValue("NumeroContaCorrenteCedente", 101916);
		transacaoTitulos.setValue("DigitoVerificadorConta", 0);
		transacaoTitulos.setValue("NumeroCovenioCobrancaCedente", convenio);
		//transacaoTitulos.setValue("CodigoControleEmpresa", "");
		transacaoTitulos.setValue("NossoNumero", convenio+nDocumento+sequencial);
		/*
		transacaoTitulos.setValue("NumeroPrestacao", 00);
		transacaoTitulos.setValue("GrupoValor", 00);
		transacaoTitulos.setValue("IndicativoMensagemSacador", "");
		transacaoTitulos.setValue("PrefixoTitulo", "   ");
		*/
		transacaoTitulos.setValue("VariacaoCarteira", "019");
		/*
		transacaoTitulos.setValue("ContaCaucao", 0);
		transacaoTitulos.setValue("NumeroBordero", 000000);
		transacaoTitulos.setValue("TipoCobranca", "     ");
		*/
		transacaoTitulos.setValue("CarteiraCobranca", 17);
		transacaoTitulos.setValue("Comando", 01);
		//transacaoTitulos.setValue("NumeroTituloAtribuidoCedente", "          ");
		transacaoTitulos.setValue("DataVencimento", "190118");
		transacaoTitulos.setValue("ValorTitulo", 1);
		/*transacaoTitulos.setValue("NumeroBanco", 001);
		transacaoTitulos.setValue("PrefixoAgenciaCobradora", 0000);
		transacaoTitulos.setValue("DigitoVerificadorPrefixoAgenciaCobradora", " ");
		*/
		transacaoTitulos.setValue("EspecieTitulo", 12);
		transacaoTitulos.setValue("AceiteTitulo", "N");
		transacaoTitulos.setValue("DataEmissao", 170118);
		/*
		transacaoTitulos.setValue("InstrucaoCodificada", "  ");
		transacaoTitulos.setValue("InstrucaoCodificada", "  ");
		*/
		transacaoTitulos.setValue("JurosMoraDiaAtraso", "00000000003");
		/*transacaoTitulos.setValue("DataLimite", "      ");
 		transacaoTitulos.setValue("ValorDesconto", "           "); 
		transacaoTitulos.setValue("ValorIof", "           ");
		transacaoTitulos.setValue("ValorAbatimento", "           ");
		*/
		transacaoTitulos.setValue("TipoInscricaoSacado", 01);
		transacaoTitulos.setValue("NumeroCnpjCpfSacado", "12345678900");
		transacaoTitulos.setValue("NomeSacado", "FULADO DA SILVA SAURO");
		//transacaoTitulos.setValue("ComplementoRegistro", "");
		transacaoTitulos.setValue("EnderecoSacado", "Rua Augusto Corr�a, LABES");
		transacaoTitulos.setValue("BairroSacado", "Guam�");
		transacaoTitulos.setValue("CepEnderecoSacado", "66075110");
		transacaoTitulos.setValue("CidadeSacado", "BEL�M");
		transacaoTitulos.setValue("UfCidadeSacado", "PA");
		/*
		transacaoTitulos.setValue("Observacao", "                                        ");
		transacaoTitulos.setValue("NumeroDiasProtesto", "  ");
		transacaoTitulos.setValue("ComplementoRegistro", " ");
		*/
		transacaoTitulos.setValue("SequencialRegistro", sequencial);
		
		return transacaoTitulos;
	}

	private static Record createTrailer(FlatFile<Record> ff, int sequencial) {
		
		Record trailer = ff.createRecord("Trailler");
		
		trailer.setValue("NumeroSequencialRegistro", sequencial);
		
		return trailer;
	}
	
	private static void mostreBoletoNaTela(File arquivoBoleto) {

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        
        try {
                desktop.open(arquivoBoleto);
        } catch (IOException e) {
                e.printStackTrace();
        }
}

}
