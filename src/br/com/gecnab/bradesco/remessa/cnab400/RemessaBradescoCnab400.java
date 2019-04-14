package br.com.gecnab.bradesco.remessa.cnab400;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jrimum.bopepo.pdf.Files;
import org.jrimum.texgit.FlatFile;
import org.jrimum.texgit.Record;
import org.jrimum.texgit.Texgit;
import org.jrimum.utilix.ClassLoaders;

public class RemessaBradescoCnab400 {
	
private static int convenio = 2866935 ;
	
	private static int sequencial = new Random(659L).nextInt(); // Vari�vel usada para seta a sequencia dos arquivos
	private static String nDocumento = "000000000";	
	/**
	 * Classe geradora de arquivos de REMESSA para BANCO DO BRASIL - CNAB 400 
	 * @author Rayan
	 */
	
	/*
	 * NOTA : as linhas comentadas s�o setadas por padr�o no layout do arquivo
	 */
	public static void main(String[] args) throws IOException {

		// Identificando Layout
		File layoutTemporario = new File("LayoutBradescoCNAB400Remessa.txg.xml");
		File layout = Files.bytesToFile(layoutTemporario, IOUtils.toByteArray(ClassLoaders.getResourceAsStream("/layouts/LayoutBradescoCNAB400Remessa.txg.xml")));

		// Criando o arquivo de remessa
		FlatFile<Record> ff = Texgit.createFlatFile(layout);
		
		// Registrando Header
		ff.addRecord(createHeader(ff));
		
		/* La�o para representar mais de 1 boleto registrado no arquivo
		*  Registrando T�tulos(boletos)
		**/
		//for( sequencial = 1; sequencial <= 5; sequencial++){ 
			ff.addRecord(createTransacaoTitulos(ff, 1));
		//}
		
		// Registrando Trailer
		ff.addRecord(createTrailer(ff, 1));

		// Salvando arquivo de remessa.
		File arquivoDeRemessa = new File("resource/arquivoRemessa/REMESSABRADESCO"+new SimpleDateFormat("ddMMyyyy").format(new Date())+".REM");
		FileUtils.writeLines(arquivoDeRemessa, ff.write(), "\r\n");
		System.out.println("Arquivo gerado com sucesso em: "+ arquivoDeRemessa);

		mostreBoletoNaTela(arquivoDeRemessa);
		
		if (layoutTemporario != null) {
			layoutTemporario.delete();
		}
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
		//header.setValue("PrefixoAgencia", 1623);
		header.setValue("CodigoEmpresa", 00000000000000000000);
		header.setValue("NomeEmpresa", "TESTES S.A");
		header.setValue("DataGravacaoArquivo", new SimpleDateFormat("ddMMyy").format(new Date()));
		header.setValue("NroSequencialArquivo", StringUtils.leftPad(StringUtils.right(sequencial+"", 5), 7, "0"));
		//header.setValue("BB", "001BANCO DO BRASIL");
		header.setValue("DataGravacao", 170118);
		header.setValue("SequencialRemessa", "0000000");
		header.setValue("NumeroConvenioLider", convenio);
		header.setValue("NroSequencialRegistroH", StringUtils.leftPad(StringUtils.left(sequencial+"", 5), 6, "0"));

		return header;
	}

	private static Record createTransacaoTitulos(FlatFile<Record> ff, int sequencial) {
			
		/*
		 * NOTA : as linhas comentadas s�o setadas por padr�o no layout do arquivo
		 */
		Record transacaoTitulos = ff.createRecord("TransacaoTitulo");

//		/*
//		transacaoTitulos.setValue("IdentiFicacaoRegistroDetalhe", 7);
//		transacaoTitulos.setValue("TipoInscricaoCedente", 02);
//		*/
//		transacaoTitulos.setValue("NumeroCpfCnpjCedente", "06313870000441");
//		transacaoTitulos.setValue("PrefixoAgencia", 1623);
//		transacaoTitulos.setValue("DigitoVerificadorAgencia", 2);
//		transacaoTitulos.setValue("NumeroContaCorrenteCedente", 101916);
//		transacaoTitulos.setValue("DigitoVerificadorConta", 0);
//		transacaoTitulos.setValue("NumeroCovenioCobrancaCedente", convenio);
//		//transacaoTitulos.setValue("CodigoControleEmpresa", "");
//		transacaoTitulos.setValue("NossoNumero", convenio+nDocumento+sequencial);
//		/*
//		transacaoTitulos.setValue("NumeroPrestacao", 00);
//		transacaoTitulos.setValue("GrupoValor", 00);
//		transacaoTitulos.setValue("IndicativoMensagemSacador", "");
//		transacaoTitulos.setValue("PrefixoTitulo", "   ");
//		*/
//		transacaoTitulos.setValue("VariacaoCarteira", "019");
//		/*
//		transacaoTitulos.setValue("ContaCaucao", 0);
//		transacaoTitulos.setValue("NumeroBordero", 000000);
//		transacaoTitulos.setValue("TipoCobranca", "     ");
//		*/
//		transacaoTitulos.setValue("CarteiraCobranca", 17);
//		transacaoTitulos.setValue("Comando", 01);
//		//transacaoTitulos.setValue("NumeroTituloAtribuidoCedente", "          ");
//		transacaoTitulos.setValue("DataVencimento", "190118");
//		transacaoTitulos.setValue("ValorTitulo", 1);
//		/*transacaoTitulos.setValue("NumeroBanco", 001);
//		transacaoTitulos.setValue("PrefixoAgenciaCobradora", 0000);
//		transacaoTitulos.setValue("DigitoVerificadorPrefixoAgenciaCobradora", " ");
//		*/
//		transacaoTitulos.setValue("EspecieTitulo", 12);
//		transacaoTitulos.setValue("AceiteTitulo", "N");
//		transacaoTitulos.setValue("DataEmissao", 170118);
//		/*
//		transacaoTitulos.setValue("InstrucaoCodificada", "  ");
//		transacaoTitulos.setValue("InstrucaoCodificada", "  ");
//		*/
//		transacaoTitulos.setValue("JurosMoraDiaAtraso", "00000000003");
//		/*transacaoTitulos.setValue("DataLimite", "      ");
// 		transacaoTitulos.setValue("ValorDesconto", "           "); 
//		transacaoTitulos.setValue("ValorIof", "           ");
//		transacaoTitulos.setValue("ValorAbatimento", "           ");
//		*/
//		transacaoTitulos.setValue("TipoInscricaoSacado", 01);
//		transacaoTitulos.setValue("NumeroCnpjCpfSacado", "12345678900");
//		transacaoTitulos.setValue("NomeSacado", "FULADO DA SILVA SAURO");
//		//transacaoTitulos.setValue("ComplementoRegistro", "");
//		transacaoTitulos.setValue("EnderecoSacado", "Rua Augusto Corr�a, LABES");
//		transacaoTitulos.setValue("BairroSacado", "Guam�");
//		transacaoTitulos.setValue("CepEnderecoSacado", "66075110");
//		transacaoTitulos.setValue("CidadeSacado", "BEL�M");
//		transacaoTitulos.setValue("UfCidadeSacado", "PA");
//		/*
//		transacaoTitulos.setValue("Observacao", "                                        ");
//		transacaoTitulos.setValue("NumeroDiasProtesto", "  ");
//		transacaoTitulos.setValue("ComplementoRegistro", " ");
//		*/
//		transacaoTitulos.setValue("SequencialRegistro", sequencial);
		
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
