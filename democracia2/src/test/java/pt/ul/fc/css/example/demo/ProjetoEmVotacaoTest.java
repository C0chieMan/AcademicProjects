package pt.ul.fc.css.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import pt.ul.fc.css.example.demo.entities.ProjetoEmVotacao;
import pt.ul.fc.css.example.demo.entities.PropostaLei;
import pt.ul.fc.css.example.demo.entities.Cidadao;
import pt.ul.fc.css.example.demo.entities.Delegado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

import pt.ul.fc.css.example.demo.entities.Tema;

@SpringBootTest
@Transactional
public class ProjetoEmVotacaoTest {
	
	@Autowired private ProjetoEmVotacaoController ProjetoEmVotacaoController;
	Cidadao d = new Delegado("Pedro","Seixo");
	Cidadao d2 = new Delegado("Phiona","Catarino");
	Cidadao c = new Cidadao("Sherek","Catarino");
	Cidadao c2 = new Cidadao("Eder","Militao");
	LocalDateTime validade = LocalDateTime.of(2024, Month.JULY, 29, 19, 30, 40);
	@Autowired private PropostaLeiController PropostaLeiController;
    Cidadao delegado = new Delegado("Paulo", "Seixo");
    File file = new File("pom.xml");
    Tema tema = new Tema("Saude", null);
    @Autowired private ProjetoEmVotacaoController pvc;
    @Autowired private PropostaLeiController plc;
	@Autowired private CidadaoController cc;
	@Autowired private TemaController tc;
	
	@Test
	@Transactional
	public void confirmVotacao() throws IOException{
		tc.putTema(tema);
		cc.putCidadao(c);
		cc.putCidadao(d);
		cc.putCidadao(c2);
		cc.putCidadao(delegado);
		PropostaLei pl = new PropostaLei("125 do costa", "Òbvio pelo titulo", tema, delegado, validade);
		ProjetoEmVotacao p1 = new ProjetoEmVotacao(pl.getId(), validade);
		plc.putPropostaLei(pl);
		pvc.putProjetoEmVotacao(p1);
		boolean test1 = p1.votarCidadaoContra(d);
		int tamanho_list = p1.getVotadoresC().size();
		
		assertEquals(tamanho_list, 0);
		assertEquals(test1, false);
		test1 = p1.votarCidadaoFavor(c);
		
		boolean test2 = p1.votarDelegadoContra(d);
		tamanho_list = p1.getVotadoresC().size();
		
		assertEquals(tamanho_list,2);
		assertEquals(test1, true);
		assertEquals(test1, test2);
		
	}
	@Test
	@Transactional
	public void testIncrementContra() throws IOException{
		tc.putTema(tema);
		cc.putCidadao(c);
		cc.putCidadao(d);
		cc.putCidadao(c2);
		cc.putCidadao(delegado);
		PropostaLei pl = new PropostaLei("125 do costa", "Òbvio pelo titulo", tema, delegado, validade);
		ProjetoEmVotacao p1 = new ProjetoEmVotacao(pl.getId(), validade);
		plc.putPropostaLei(pl);
		pvc.putProjetoEmVotacao(p1);
		p1.votarCidadaoContra(c);
		p1.votarCidadaoContra(c2);
		p1.votarDelegadoContra(d);
		assertEquals(p1.getVotosCon(),3);
		
	}
	@Test
	@Transactional
	public void testIncrementFav() throws IOException{
		tc.putTema(tema);
		cc.putCidadao(c);
		cc.putCidadao(d);
		cc.putCidadao(c2);
		cc.putCidadao(delegado);
		PropostaLei pl = new PropostaLei("125 do costa", "Òbvio pelo titulo", tema, delegado, validade);
		ProjetoEmVotacao p1 = new ProjetoEmVotacao(pl.getId(), validade);
		plc.putPropostaLei(pl);
		pvc.putProjetoEmVotacao(p1);
		p1.votarCidadaoFavor(c);
		p1.votarCidadaoFavor(c2);
		p1.votarDelegadoFavor(d);
		assertEquals(p1.getVotosFav(),3);
	}
	@Test
	@Transactional
	public void verificaDel() throws IOException{
		tc.putTema(tema);
		cc.putCidadao(c);
		cc.putCidadao(d);
		cc.putCidadao(c2);
		cc.putCidadao(delegado);
		PropostaLei pl = new PropostaLei("125 do costa", "Òbvio pelo titulo", tema, delegado, validade);
		ProjetoEmVotacao p1 = new ProjetoEmVotacao(pl.getId(), validade);
		plc.putPropostaLei(pl);
		pvc.putProjetoEmVotacao(p1);
		p1.votarDelegadoFavor(d);
		p1.votarDelegadoContra(d2);
		int tamanho_listF = p1.getVotadoresDFav().size();
		int tamanho_listC = p1.getVotadoresDFav().size();
		assertEquals(tamanho_listF, 1);
		assertEquals(tamanho_listF, tamanho_listC);
	}
	
	
	

}
