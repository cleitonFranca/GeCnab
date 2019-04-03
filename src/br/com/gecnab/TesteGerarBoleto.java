package br.com.gecnab;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import org.jrimum.bopepo.BancosSuportados;
import org.jrimum.bopepo.Boleto;
import org.jrimum.bopepo.view.BoletoViewer;
import org.jrimum.domkee.comum.pessoa.endereco.CEP;
import org.jrimum.domkee.comum.pessoa.endereco.Endereco;
import org.jrimum.domkee.comum.pessoa.endereco.UnidadeFederativa;
import org.jrimum.domkee.financeiro.banco.febraban.Agencia;
import org.jrimum.domkee.financeiro.banco.febraban.Carteira;
import org.jrimum.domkee.financeiro.banco.febraban.Cedente;
import org.jrimum.domkee.financeiro.banco.febraban.ContaBancaria;
import org.jrimum.domkee.financeiro.banco.febraban.NumeroDaConta;
import org.jrimum.domkee.financeiro.banco.febraban.Sacado;
import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;
import org.jrimum.domkee.financeiro.banco.febraban.Titulo;
import org.jrimum.domkee.financeiro.banco.febraban.Titulo.Aceite;

public class TesteGerarBoleto {
	private static int convenio = 2866935 ;
	private static String nDocumento = "0000000001";

	public static void main(String[] args) {
		// Cedente
		Cedente cedente = new Cedente("TESTE PPP - Pronto Para Pagar", "52586928000101");
		
		// Sacado
		Sacado sacado = new Sacado("FALANO DA SILVA SAURO", "58206359494");
		
		// Endere�o do sacado
		Endereco endereco = new Endereco();
		endereco.setUF(UnidadeFederativa.PA);
		endereco.setLocalidade("Bel�m");
		endereco.setCep(new CEP("66075-110"));
		endereco.setBairro("Guam�");
		endereco.setLogradouro("Rua Augusto Corr�a, Lab. de Engenharia de Software(LABES)");
		endereco.setNumero("1");
		
		sacado.addEndereco(endereco);
		
		// Criando o t�tulo
		ContaBancaria contaBancaria = new ContaBancaria(BancosSuportados.BANCO_DO_BRASIL.create());
		contaBancaria.setAgencia(new Agencia(1623, "2"));
		contaBancaria.setNumeroDaConta(new NumeroDaConta(convenio)); // usado para conv�ncio > 1.000.000 - sen�o basta colocar o N� da conta
		//contaBancaria.setNumeroDaConta(new NumeroDaConta(333006, "0"));
		//contaBancaria.setCarteira(new Carteira(17, TipoDeCobranca.COM_REGISTRO));
		contaBancaria.setCarteira(new Carteira(17));
		
		Titulo titulo = new Titulo(contaBancaria, sacado, cedente);
		titulo.setNumeroDoDocumento(nDocumento);
		titulo.setNossoNumero(convenio+nDocumento); 
		//titulo.setDigitoDoNossoNumero("7");
		titulo.setValor(BigDecimal.valueOf(1.00));
		titulo.setDataDoDocumento(new Date());
		Calendar calendar = Calendar.getInstance();
		calendar.set(2017, 12, 19);
		titulo.setDataDoVencimento(calendar.getTime());
		titulo.setTipoDeDocumento(TipoDeTitulo.DS_DUPLICATA_DE_SERVICO);
		titulo.setAceite(Aceite.N);
		
		// Dados do boleto
		Boleto boleto = new Boleto(titulo);
		
		/* cria uma informa��o fake para o usu�rio, pois  foi necess�io o n� conv�nio em "contaBancaria.setNumeroDaConta" para 
		*  poder mostrar agencia e conta para o usu�rio
		*/
		boleto.addTextosExtras("txtFcAgenciaCodigoCedente", "1123-4/123.444-0"); 
		boleto.addTextosExtras("txtRsAgenciaCodigoCedente", "1123-4/123.444-0"); 
		boleto.setLocalPagamento("Pagar preferencialmente no Banco do Brasil");
		boleto.setInstrucaoAoSacado("Evite multas, pague em dias suas contas.");
		boleto.setInstrucao1("N�O ACEITAR PAGAMENTO EM CHEQUE");
		boleto.setInstrucao3("EM CASO DE ATRASO COBRAR MULTA DE 2%, MAIS JUROS DE 1% AO M�S");
		
		 
		BoletoViewer boletoViewer = new BoletoViewer(boleto);
		
		File arquivoPdf = boletoViewer.getPdfAsFile("resource/boletoBB.pdf");
		 mostreBoletoNaTela(arquivoPdf);
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
