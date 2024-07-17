package pt.ul.fc.css.example.demo;

import java.util.List;
import java.util.ArrayList;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.ul.fc.css.example.demo.entities.ProjetoEmVotacao;
import pt.ul.fc.css.example.demo.entities.PropostaLei;
import pt.ul.fc.css.example.demo.repositories.CidadaoRepository;
import pt.ul.fc.css.example.demo.repositories.ProjetoEmVotacaoRepository;
import pt.ul.fc.css.example.demo.repositories.PropostaLeiRepository;
import pt.ul.fc.css.example.demo.repositories.TemaRepository;

@RestController
@RequestMapping("/api")
public class RestAPIController {

	private final Democracia2 democracia2;
	
	public RestAPIController(CidadaoRepository cidadaoRepository,
			PropostaLeiRepository propostaLeiRepository, 
			ProjetoEmVotacaoRepository projetoEmVotacaoRepository,
			TemaRepository temaRepository) {
		democracia2 = new Democracia2(cidadaoRepository, propostaLeiRepository, projetoEmVotacaoRepository, temaRepository);
    }
	
	@GetMapping("/listarVotacoesEmCurso")
    public List<VotacaoDTO> listarVotacoesEmCurso() {
        List<ProjetoEmVotacao> votacoes = democracia2.listarVotacoesEmCurso();
        List<VotacaoDTO> listaVotacoes = new ArrayList<>();
        for (ProjetoEmVotacao votacao : votacoes) {
        	if (!votacao.getFechada()) {
                listaVotacoes.add(new VotacaoDTO(votacao.getId(), votacao.getIdProposta(), votacao.getValidade(),
                		votacao.getVotadoresDFav(), votacao.getVotadoresDCon()));
            }
        }
        return listaVotacoes;
    }
	
	@GetMapping("/listarProjetosLei")
	public List<PropostaLeiDTO> listarProjetosLei() {
        List<PropostaLei> propostas = democracia2.listarProjetosLei();
        List<PropostaLeiDTO> listaPropostas = new ArrayList<>();
        for (PropostaLei proposta : propostas) {
        	if (!proposta.close()) {
        		listaPropostas.add(new PropostaLeiDTO(proposta.getId(), proposta.getTitle(), proposta.getDate()));
            }
        }
        return listaPropostas;
    }
	
	@GetMapping("/consultarProjetoLei/{id}")
	public PropostaLeiFullDTO consultarProjetoLei(@PathVariable long id) {
        PropostaLei proposta = democracia2.consultarProjetoLei(id);
        String tema = democracia2.temaController().getTema(proposta.getTema()).getTitulo();
        return new PropostaLeiFullDTO(proposta.getId(), proposta.getTitle(), proposta.getDescription(), tema,
        		proposta.getDelegado(), proposta.getDate(), proposta.getSupporters(), 
        		proposta.getListaApoiantes(), proposta.close());
    }
	
	@PostMapping("/apoiarProjetoLei")
	public Boolean apoiarProjetoLei(@NonNull @RequestParam("cidadaoId") long cidadaoId,
			@NonNull @RequestParam("projetoId") long projetoId) {
		return democracia2.apoiarProjetoLei(cidadaoId, projetoId);
	}
	
	@GetMapping("/votoPorOmissao")
	public Boolean votoPorOmissao(@NonNull @RequestParam("cidadaoId") long cidadaoId,
			@NonNull @RequestParam("votacaoId") long votacaoId) {
		return democracia2.votoPorOmissao(cidadaoId, votacaoId);
    }
	
	@PostMapping("/votar")
	public Boolean votar(@NonNull @RequestParam("cidadaoId") long cidadaoId,
			@NonNull @RequestParam("votacaoId") long votacaoId, @NonNull @RequestParam("voto") boolean voto) {
		return democracia2.votar(cidadaoId, votacaoId, voto);
	}
	
	@GetMapping("/votoDelegado")
	public Boolean votoDelegado(@NonNull @RequestParam("delegadoId") long delegadoId,
			@NonNull @RequestParam("votacaoId") long votacaoId) {
		return democracia2.votoDelegado(delegadoId, votacaoId);
    }
	
}
