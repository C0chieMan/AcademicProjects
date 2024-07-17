package pt.ul.fc.css.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.transaction.annotation.Transactional;

import pt.ul.fc.css.example.demo.entities.ProjetoEmVotacao;
import pt.ul.fc.css.example.demo.entities.PropostaLei;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RestAPITest {
	
	@Value(value="${local.server.port}")
	private int port;
	
	@Autowired
	private ProjetoEmVotacaoController votacaoController;
	
	@Autowired
	private PropostaLeiController propostaController;
	
	@Autowired
	private TemaController temaController;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	@Transactional
    public void listarVotacoesEmCurso() throws IOException {
		String json = this.restTemplate.getForObject(
				"http://localhost:" + port + "/api/listarVotacoesEmCurso",String.class);
		List<ProjetoEmVotacao> votacoes = votacaoController.getProjetos();
		for (int i = 0; i < votacoes.size(); i++) {
			if (votacoes.get(i).getFechada()) {
				votacoes.remove(i);
				i--;
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		List<VotacaoDTO> jsonList = mapper.readValue(json,
				TypeFactory.defaultInstance().constructCollectionType(List.class, VotacaoDTO.class));
		assertEquals(votacoes.size(), jsonList.size());
		for (int i = 0; i < votacoes.size(); i++) {
			assertEquals(votacoes.get(i).getId(), jsonList.get(i).getId());
			assertEquals(votacoes.get(i).getIdProposta(), jsonList.get(i).getIdProposta());
			assertEquals(votacoes.get(i).getValidade(), jsonList.get(i).getValidade());
			assertEquals(votacoes.get(i).getVotadoresDFav().size(), jsonList.get(i).getVotadoresDFav().size());
			for (int f = 0; f < votacoes.get(i).getVotadoresDFav().size(); f++) {
				assertEquals(votacoes.get(i).getVotadoresDFav().get(f), jsonList.get(i).getVotadoresDFav().get(f));
			}
			assertEquals(votacoes.get(i).getVotadoresDCon().size(), jsonList.get(i).getVotadoresDCon().size());
			for (int f = 0; f < votacoes.get(i).getVotadoresDCon().size(); f++) {
				assertEquals(votacoes.get(i).getVotadoresDCon().get(f), jsonList.get(i).getVotadoresDCon().get(f));
			}
		}
	}
	
	@Test
	@Transactional
    public void listarProjetosLei() throws IOException {
		String json = this.restTemplate.getForObject(
				"http://localhost:" + port + "/api/listarProjetosLei",String.class);
		List<PropostaLei> propostas = propostaController.getPropostas();
		for (int i = 0; i < propostas.size(); i++) {
			if (propostas.get(i).close()) {
				propostas.remove(i);
				i--;
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		List<PropostaLeiDTO> jsonList = mapper.readValue(json,
				TypeFactory.defaultInstance().constructCollectionType(List.class, PropostaLeiDTO.class));
		assertEquals(propostas.size(), jsonList.size());
		for (int i = 0; i < propostas.size(); i++) {
			assertEquals(propostas.get(i).getId(), jsonList.get(i).getId());
			assertEquals(propostas.get(i).getTitle(), jsonList.get(i).getTitle());
			assertEquals(propostas.get(i).getDate(), jsonList.get(i).getDate());
		}
	}
	
	@Test
	@Transactional
    public void consultarProjetoLei() throws IOException {
		String json = this.restTemplate.getForObject(
				"http://localhost:" + port + "/api/consultarProjetoLei/3",String.class);
		PropostaLei proposta = propostaController.getPropostaLei((long)3);
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		PropostaLeiFullDTO jsonObj = mapper.readValue(json, PropostaLeiFullDTO.class);
		assertEquals(proposta.getId(), jsonObj.getId());
		assertEquals(proposta.getTitle(), jsonObj.getTitulo());
		assertEquals(proposta.getDescription(), jsonObj.getDescricao());
		assertEquals(proposta.getDate(), jsonObj.getDate());
		assertEquals(temaController.getTema(proposta.getTema()).getTitulo(), jsonObj.getTema());
		assertEquals(proposta.getDelegado(), jsonObj.getDelegado());
		assertEquals(proposta.getDate(), jsonObj.getDate());
		assertEquals(proposta.getSupporters(), jsonObj.getApoiantes());
		assertEquals(proposta.getListaApoiantes().size(), jsonObj.getListaApoiantes().size());
		for (int f = 0; f < proposta.getListaApoiantes().size(); f++) {
			assertEquals(proposta.getListaApoiantes().get(f), jsonObj.getListaApoiantes().get(f));
		}
		assertEquals(proposta.close(), jsonObj.getFechada());
	}
	
	@Test
	@Transactional
    public void votoPorOmissao() throws IOException {
		String json = this.restTemplate.getForObject(
				"http://localhost:" + port + "/api/votoPorOmissao?cidadaoId=10&votacaoId=1", String.class);
		ObjectMapper mapper = new ObjectMapper();
		Boolean jsonObj = mapper.readValue(json, Boolean.class);
		assertEquals(jsonObj, Boolean.valueOf(true));
		json = this.restTemplate.getForObject(
				"http://localhost:" + port + "/api/votoPorOmissao?cidadaoId=41&votacaoId=1", String.class);
		jsonObj = mapper.readValue(json, Boolean.class);
		assertEquals(jsonObj, Boolean.valueOf(false));
	}
	
	@Test
	@Transactional
    public void votoDelegado() throws IOException {
		String json = this.restTemplate.getForObject(
				"http://localhost:" + port + "/api/votoDelegado?delegadoId=2&votacaoId=1", String.class);
		ObjectMapper mapper = new ObjectMapper();
		Boolean jsonObj = mapper.readValue(json, Boolean.class);
		assertEquals(jsonObj, Boolean.valueOf(true));
		json = this.restTemplate.getForObject(
				"http://localhost:" + port + "/api/votoDelegado?delegadoId=5&votacaoId=1", String.class);
		jsonObj = mapper.readValue(json, Boolean.class);
		assertEquals(jsonObj, Boolean.valueOf(false));
	}

}
