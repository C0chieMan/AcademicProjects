package pt.ul.fc.css.example.demo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;

import pt.ul.fc.css.example.demo.entities.Cidadao;
import pt.ul.fc.css.example.demo.entities.ProjetoEmVotacao;
import pt.ul.fc.css.example.demo.entities.PropostaLei;
import pt.ul.fc.css.example.demo.entities.Tema;
import pt.ul.fc.css.example.demo.repositories.CidadaoRepository;
import pt.ul.fc.css.example.demo.repositories.ProjetoEmVotacaoRepository;
import pt.ul.fc.css.example.demo.repositories.PropostaLeiRepository;
import pt.ul.fc.css.example.demo.repositories.TemaRepository;

public class Democracia2 {
	
	private final CidadaoController cidadaoController;
	private final PropostaLeiController propostaLeiController;
	private final ProjetoEmVotacaoController projetoEmVotacaoController;
	private final TemaController temaController;
	
	public Democracia2(CidadaoRepository cidadaoRepository,
			PropostaLeiRepository propostaLeiRepository, 
			ProjetoEmVotacaoRepository projetoEmVotacaoRepository,
			TemaRepository temaRepository) {
        this.cidadaoController = new CidadaoController(cidadaoRepository);
        this.propostaLeiController = new PropostaLeiController(propostaLeiRepository);
        this.projetoEmVotacaoController = new ProjetoEmVotacaoController(projetoEmVotacaoRepository);
        this.temaController = new TemaController(temaRepository);
    }
	
	public List<ProjetoEmVotacao> listarVotacoesEmCurso() {
		return projetoEmVotacaoController.getProjetos();
	}
	
	public long apresentarProjetoLei(@NonNull String titulo, @NonNull String descricao,
    		@NonNull Tema tema, @NonNull Cidadao delegado, @NonNull LocalDateTime validade) throws IOException {
		if (!delegado.isDelegado() || validade.isAfter(LocalDateTime.now().plusYears(1))) {
			return -1;
		}
		return propostaLeiController.putPropostaLei(new PropostaLei(titulo, descricao, tema,
				delegado, validade)).getId();
	}
	
	public boolean apoiarProjetoLei(long cidadaoID, long propostaLeiID) {
		propostaLeiController.lock();
		PropostaLei proposta = propostaLeiController.getPropostaLei(propostaLeiID);
		if (proposta.close() || proposta.getListaApoiantes().contains(cidadaoID) ||
				!proposta.addSupport(cidadaoID)) {
			propostaLeiController.putPropostaLei(proposta);
			propostaLeiController.unlock();
			return false;
		}
		if (proposta.getSupporters() >= 10000) {
			LocalDateTime validade = proposta.getDate();
			if (validade.isBefore(LocalDateTime.now().plusDays(15))) {
				validade = LocalDateTime.now().plusDays(15);
			}
			else if (validade.isAfter(LocalDateTime.now().plusMonths(2))) {
				validade = LocalDateTime.now().plusMonths(2);
			}
			projetoEmVotacaoController.lock();
			ProjetoEmVotacao projeto = projetoEmVotacaoController.putProjetoEmVotacao(
					new ProjetoEmVotacao(proposta.getId(), validade));
			projeto.votarDelegadoFavor(cidadaoController.getCidadao(proposta.getDelegado()));
			projetoEmVotacaoController.putProjetoEmVotacao(projeto);
			projetoEmVotacaoController.unlock();
		}
		propostaLeiController.putPropostaLei(proposta);
		propostaLeiController.unlock();
		return true;
	}
	
	public boolean hasVoted(long cidadaoID, long projetoEmVotacaoID) {
		ProjetoEmVotacao votacao = projetoEmVotacaoController.getProjetoEmVotacao(projetoEmVotacaoID);
		ArrayList<Long> votadoresC = votacao.getVotadoresC();
		return votadoresC.contains(cidadaoID);
	}
	
	public List<PropostaLei> listarProjetosLei() {
		return propostaLeiController.getPropostas();
	}
	
	public PropostaLei consultarProjetoLei(long idProposta) {
		return propostaLeiController.getPropostaLei(idProposta);
	}
	
	public boolean escolherDelegado(long cidadaoID, long delegadoID, long temaID) {
		cidadaoController.lock();
		Cidadao delegado = cidadaoController.getCidadao(delegadoID);
		if (!delegado.isDelegado() || cidadaoID == delegadoID) {
			cidadaoController.unlock();
			return false;
		}
		Cidadao cidadao = cidadaoController.getCidadao(cidadaoID);
		cidadao.chooseDelegado(temaID, delegadoID);
		cidadaoController.putCidadao(cidadao);
		cidadaoController.unlock();
		return true;
	}
	
	public Boolean votoPorOmissao(long cidadaoID, long projetoEmVotacaoID) {
		ProjetoEmVotacao votacao = projetoEmVotacaoController.getProjetoEmVotacao(projetoEmVotacaoID);
		Cidadao cidadao = cidadaoController.getCidadao(cidadaoID);
		PropostaLei proposta = propostaLeiController.getPropostaLei(votacao.getIdProposta());
		Long tema = proposta.getTema();
		Long delegado = cidadao.getDelegado(tema);
		while (delegado == null) {
			tema = temaController.getTema(tema).getTemaPai();
			if (tema != null) {
				delegado = cidadao.getDelegado(tema);
			}
			else {
				return null;
			}
		}
		if (votacao.getVotadoresDFav().contains(delegado)) {
			return true;
		}
		else if (votacao.getVotadoresDCon().contains(delegado)) {
			return false;
		}
		else {
			return null;
		}
	}
	
	public boolean votar(long cidadaoID, long projetoEmVotacaoID, boolean voto) {
		projetoEmVotacaoController.lock();
		ProjetoEmVotacao votacao = projetoEmVotacaoController.getProjetoEmVotacao(projetoEmVotacaoID);
		if (fecharVotacao(votacao)) {
			projetoEmVotacaoController.unlock();
			return false;
		}
		Cidadao cidadao = cidadaoController.getCidadao(cidadaoID);
		boolean success;
		if (cidadao.isDelegado() && voto) {
			success = votacao.votarDelegadoFavor(cidadao);
		}
		else if (cidadao.isDelegado() && !voto) {
			success = votacao.votarDelegadoContra(cidadao);
		}
		else if (voto) {
			success = votacao.votarCidadaoFavor(cidadao);
		}
		else {
			success = votacao.votarCidadaoContra(cidadao);
		}
		projetoEmVotacaoController.putProjetoEmVotacao(votacao);
		projetoEmVotacaoController.unlock();
		return success;
	}
	
	public Boolean votoDelegado(long delegadoID, long projetoEmVotacaoID) {
		ProjetoEmVotacao votacao = projetoEmVotacaoController.getProjetoEmVotacao(projetoEmVotacaoID);
		if (votacao.getVotadoresDFav().contains(delegadoID)) {
			return true;
		}
		else if (votacao.getVotadoresDCon().contains(delegadoID)) {
			return false;
		}
		else {
			return null;
		}
	}
	
	@Scheduled(fixedDelay = 1000)
	public void fecharTudo() {
		for (PropostaLei proposta : propostaLeiController.getPropostas()) {
			if (proposta.close()) {
				propostaLeiController.lock();
				proposta = propostaLeiController.getPropostaLei(proposta.getId());
				proposta.close();
				propostaLeiController.putPropostaLei(proposta);
				propostaLeiController.unlock();
			}
		}
		for (ProjetoEmVotacao votacao : projetoEmVotacaoController.getProjetos()) {
			fecharVotacao(votacao);
		}
	}
	
	public boolean fecharVotacao(@NonNull ProjetoEmVotacao votacao) {
		if (votacao.getFechada() || !votacao.fechar()) {
			return false;
		}
		projetoEmVotacaoController.lock();
		votacao = projetoEmVotacaoController.getProjetoEmVotacao(votacao.getId());
		votacao.fechar();
		if (votacao.getVotadoresDFav().isEmpty() && votacao.getVotadoresDCon().isEmpty()) {
			votacao.setAprovada(votacao.getVotosFav() > votacao.getVotosCon());
			projetoEmVotacaoController.putProjetoEmVotacao(votacao);
			projetoEmVotacaoController.unlock();
			return true;
		}
		cidadaoController.lock();
		for (Cidadao cidadao : cidadaoController.getAllCidadaos()) {
			if (!votacao.getVotadoresC().contains(cidadao.getId())) {
				Boolean votoPorOmissao = votoPorOmissao(cidadao.getId(), votacao.getId());
				if (votoPorOmissao != null) {
					votar(cidadao.getId(), votacao.getId(), votoPorOmissao);
				}
			}
		}
		cidadaoController.unlock();
		votacao.setAprovada(votacao.getVotosFav() > votacao.getVotosCon());
		projetoEmVotacaoController.putProjetoEmVotacao(votacao);
		projetoEmVotacaoController.unlock();
		return true;
	}
	
	protected CidadaoController cidadaoController() {
		return cidadaoController;
	}
	
	protected ProjetoEmVotacaoController votacaoController() {
		return projetoEmVotacaoController;
	}
	
	protected PropostaLeiController propostaController() {
		return propostaLeiController;
	}
	
	protected TemaController temaController() {
		return temaController;
	}
	
}