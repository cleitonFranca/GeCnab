package br.com.gecnab;

import static org.jrimum.utilix.text.DateFormat.DDMMYY_B;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;

import br.com.gecnab.retorno.ArquivoRetorno;
import br.com.gecnab.retorno.Protocolo;
import br.com.gecnab.retorno.Sumario;
import br.com.gecnab.retorno.TransacaoDeTitulo;



public class ExemploRetorno {

	public static void main(String[] args) throws IOException {
		
		File fileExemplo = new File("resource/arquivoRetorno/CBR6436462012201712213.RET");
		List<String> lines = FileUtils.readLines(fileExemplo, "UTF-8");
		ArquivoRetorno ff = ArquivoRetorno.newInstance(lines);
		
		//Header do arquivo
		Protocolo p = ff.getProtocolo();
				
		// Transa��o do T�tulo 
		List<TransacaoDeTitulo> tt = ff.getTransacoes();
		
		// Trailler do arquivo
		Sumario s = ff.getSumario();
		
		System.out.println("Arquivo de " + p.getIdentificacaoTipoOperacao()+" \nData de Grava��o: "+DDMMYY_B.format(p.getDataGravacao()));
		System.out.println("CEDENTE: " + p.getNomeCedente());
		
		int count = 0;
		for (TransacaoDeTitulo TransacaoDeTitulo : tt) {
			
			System.out.println(" -----------------------------------------------------------------: ");
			System.out.println("1- Nosso Numero: " + TransacaoDeTitulo.getNossoNumero());
			System.out.println("2- Agencia: " + TransacaoDeTitulo.getPrefixoAgencia() + " - " + TransacaoDeTitulo.getDigitoVerificadorAgencia());
			System.out.println("3- Conta Corrente Cedente: " + TransacaoDeTitulo.getNumeroContaCorrenteCedente() + " - " + TransacaoDeTitulo.getDigitoVerificadorContaCorrente());
			System.out.println("4- Conv�nio de Cobran�a do Cedente: " + TransacaoDeTitulo.getNumeroConvenioCobrancaCedente());
			System.out.println("5- N� Controle do participante: " + TransacaoDeTitulo.getNumeroControleParticipante());
			System.out.println("6- Dias para C�lculo: " + TransacaoDeTitulo.getDiasParaCalculo());
			System.out.println("7- Natureza Recebemineto: " + TransacaoDeTitulo.getNaturezaRecebimento());
			System.out.println("8- Prefixo do T�tulo: " + TransacaoDeTitulo.getPrefixoTitulo());
			System.out.println("9- Carteira - Varia��o: " + TransacaoDeTitulo.getCarteiraCobranca() + " - "+ TransacaoDeTitulo.getVariacaoCarteira());
			System.out.println("10- Data de liquida��o: " + DDMMYY_B.format(TransacaoDeTitulo.getDataLiquidacao()));
			System.out.println("11- Carteira - Varia��o: " + TransacaoDeTitulo.getCarteiraCobranca() + " - "+ TransacaoDeTitulo.getVariacaoCarteira());
			System.out.println("12- Data de vencimento " + DDMMYY_B.format(TransacaoDeTitulo.getDataVencimento()));
			System.out.println("13- Data de liquida��o :"  +DDMMYY_B.format(TransacaoDeTitulo.getDataLiquidacao())); 
			System.out.println("13- Data Cr�dito :"  +DDMMYY_B.format(TransacaoDeTitulo.getDataCredito())); 
			System.out.println("14- Valor do t�tulo " + TransacaoDeTitulo.getValorTitulo());
			System.out.println("15- Valor do lan�amento " + TransacaoDeTitulo.getValorLancamento());
			System.out.println("16- Valor Recebido: "+ TransacaoDeTitulo.getValorRecebido()); 
			System.out.println("17- Juros de mora: "+ TransacaoDeTitulo.getJurosDeMora()); 
			System.out.println("18- Valor Recebido: "+ TransacaoDeTitulo.getValorRecebido()); 
			System.out.println("19- Sequ�ncial do registro: "+ TransacaoDeTitulo.getSequencialRegistro()); 
			count += 1;
			 
		}
		
		System.out.println("###################################################################");
//		System.out.println("\t\t Cobran�a Simples - quantidade de T�tulo: " + s.getCobrancaSimplesQuantidadeTitulos());
//		System.out.println("\t\t Cobran�a Simples - valor total: " + s.getCobrancaSimplesValorTotal());
		System.out.println("\t\t Total de linhas lidas: "+ s.getCobrancaSimplesValorTotal());
		System.out.println("\t\t Total de T�tulo neste arquivo: "+ count);
		System.out.println("##################################################################");
		
					
	}
}
