package pt.ul.fc.css.example.demo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import pt.ul.fc.css.example.demo.entities.Cidadao;
import pt.ul.fc.css.example.demo.entities.ProjetoEmVotacao;
import pt.ul.fc.css.example.demo.entities.PropostaLei;
import pt.ul.fc.css.example.demo.entities.Tema;
import pt.ul.fc.css.example.demo.repositories.CidadaoRepository;
import pt.ul.fc.css.example.demo.repositories.ProjetoEmVotacaoRepository;
import pt.ul.fc.css.example.demo.repositories.PropostaLeiRepository;
import pt.ul.fc.css.example.demo.repositories.TemaRepository;

@Controller
public class WebController {
	
	private final Democracia2 democracia2;
	
	public WebController(CidadaoRepository cidadaoRepository,
			PropostaLeiRepository propostaLeiRepository, 
			ProjetoEmVotacaoRepository projetoEmVotacaoRepository,
			TemaRepository temaRepository) {
		democracia2 = new Democracia2(cidadaoRepository, propostaLeiRepository, projetoEmVotacaoRepository, temaRepository);
    }

	@GetMapping("/")
    public String login(Model model) {
		return "login";
    }
	
	@PostMapping("/home")
	public String home(HttpSession session, Model model, @NonNull @RequestParam("id") long id) {
		Cidadao cidadao = democracia2.cidadaoController().getCidadao(id);
		session.setAttribute("cidadao", cidadao);
		if (cidadao.isDelegado()) {
			model.addAttribute("isDelegado", true);
		} else {
			model.addAttribute("isDelegado", false);
		}
		return "home";
	}
	
	@GetMapping("/votacoesEmCurso")
	public String votacoesEmCurso(Model model, HttpSession session) {
		Cidadao cidadao = (Cidadao) session.getAttribute("cidadao");
		if (cidadao == null) {
			return "error";
		}
		List<ProjetoEmVotacao> votacoes = democracia2.listarVotacoesEmCurso();
		ArrayList<String> listaStrings = new ArrayList<>();
		for (ProjetoEmVotacao projeto : votacoes) {
			if (!projeto.getFechada()) {
				PropostaLei proposta = democracia2.propostaController().getPropostaLei(projeto.getIdProposta());
				listaStrings.add("Votação " + projeto.getId() + " - Projeto " +
						proposta.getId() + " - \""  + proposta.getTitle() + "\"");
			}
		}
		model.addAttribute("votacoesEmCurso", listaStrings);
		return "votacoesEmCurso";
	}
	
	@GetMapping("/votarVotacao")
	public String voteOnVotacao(Model model, HttpSession session, @NonNull @RequestParam("votacaoId") long votacaoId) {
		ProjetoEmVotacao votacao = democracia2.votacaoController().getProjetoEmVotacao(votacaoId);
		model.addAttribute("votacao", votacao);
		PropostaLei projeto = democracia2.propostaController().getPropostaLei(votacao.getIdProposta());
		model.addAttribute("projeto", projeto);
		model.addAttribute("titulo", projeto.getTitle());
		model.addAttribute("votou", democracia2.hasVoted(((Cidadao)session.getAttribute("cidadao")).getId(), votacao.getId()));
		Boolean omissao =  democracia2.votoPorOmissao(((Cidadao)session.getAttribute("cidadao")).getId(), votacao.getId());
		model.addAttribute("omissaoBranco", omissao == null);
		model.addAttribute("omissao", omissao);
		return "votarVotacao";
	}
	
	@PostMapping("/votarVotacao")
	public String voteOnVotacao(Model model, HttpSession session, 
			@NonNull @RequestParam("votacaoId") long votacaoId, @NonNull @RequestParam("voto") String voto) {
		boolean vote;
		if (voto.equals("Favor")) {
			vote = true;
		}
		else if (voto.equals("Contra")) {
			vote = false;
		}
		else {
			return "error";
		}
		democracia2.votar(((Cidadao)session.getAttribute("cidadao")).getId(), votacaoId, vote);
		ProjetoEmVotacao votacao = democracia2.votacaoController().getProjetoEmVotacao(votacaoId);
		model.addAttribute("votacao", votacao);
		PropostaLei projeto = democracia2.propostaController().getPropostaLei(votacao.getIdProposta());
		model.addAttribute("projeto", projeto);
		model.addAttribute("titulo", projeto.getTitle());
		model.addAttribute("votou", democracia2.hasVoted(((Cidadao)session.getAttribute("cidadao")).getId(), votacao.getId()));
		Boolean omissao =  democracia2.votoPorOmissao(((Cidadao)session.getAttribute("cidadao")).getId(), votacao.getId());
		model.addAttribute("omissaoBranco", omissao == null);
		model.addAttribute("omissao", omissao);
		return "votarVotacao";
	}
	
	@GetMapping("/votosDelegados")
    public String votosDelegados(Model model) {
		model.addAttribute("msg", false);
		return "votosDelegados";
    }
	
	@GetMapping("/votosDelegadosSearch")
    public String votosDelegadosSearch(Model model, @NonNull @RequestParam("delegadoId") long delegadoId,
    		@NonNull @RequestParam("votacaoId") long votacaoId) {
		Boolean vote = democracia2.votoDelegado(delegadoId, votacaoId);
		String voteMessage;
		if (vote != null && vote) {
			voteMessage = "O delegado " + delegadoId + " votou a favor na votação " + votacaoId;
		}
		else if (vote != null && !vote) {
			voteMessage = "O delegado " + delegadoId + " votou contra na votação " + votacaoId;
		}
		else {
			voteMessage = "O delegado " + delegadoId + " não votou na votação " + votacaoId;
		}
		model.addAttribute("msg", true);
		model.addAttribute("vote", voteMessage);
		return "votosDelegados";
    }
	
	@GetMapping("/projetosLei")
	public String projetosLei(Model model) {
		List<PropostaLei> projetos = democracia2.listarProjetosLei();
		ArrayList<String> listaStrings = new ArrayList<>();
		for (PropostaLei proposta : projetos) {
			if (!proposta.close()) {
				listaStrings.add("Projeto " + proposta.getId() + " - \""  + proposta.getTitle() + "\"");
			}
		}
		model.addAttribute("projetosLei", listaStrings);
		return "projetosLei";
	}
	
	@GetMapping("/propostaLei")
	public String propostaLei(HttpSession session, Model model, @NonNull @RequestParam("propostaId") long id) {
		model.addAttribute("id", id);
		PropostaLei proposta = democracia2.consultarProjetoLei(id);
		System.out.println(proposta.getListaApoiantes().toString());
		boolean apoiado = proposta.getListaApoiantes().contains(((Cidadao)session.getAttribute("cidadao")).getId());
		model.addAttribute("apoiado", apoiado);
		ArrayList<String> strings = new ArrayList<>();
		strings.add(proposta.getId() + " - \""  + proposta.getTitle() + "\"");
		Tema tema = democracia2.temaController().getTema(proposta.getTema());
		strings.add("Tema: " + tema.getId() + " - " + tema.getTitulo());
		strings.add("Descrição: " + proposta.getDescription());
		strings.add("ID do Delegado: " + proposta.getDelegado());
		strings.add("Validade: " + proposta.getDate().toString());
		model.addAttribute("detalhesProposta", strings);
		return "propostaLei";
	}
	
	@PostMapping("/apoiarProjeto")
	public String apoiarProjeto(HttpSession session, Model model, @NonNull @RequestParam("propostaId") long id) {
		Cidadao cidadao = (Cidadao)session.getAttribute("cidadao");
		democracia2.apoiarProjetoLei(cidadao.getId(), id);
		model.addAttribute("id", id);
		PropostaLei proposta = democracia2.consultarProjetoLei(id);
		boolean apoiado = proposta.getListaApoiantes().contains(cidadao.getId());
		model.addAttribute("apoiado", apoiado);
		ArrayList<String> strings = new ArrayList<>();
		strings.add(proposta.getId() + " - \""  + proposta.getTitle() + "\"");
		Tema tema = democracia2.temaController().getTema(proposta.getTema());
		strings.add("Tema: " + tema.getId() + " - " + tema.getTitulo());
		strings.add("Descrição: " + proposta.getDescription());
		strings.add("ID do Delegado: " + proposta.getDelegado());
		strings.add("Validade: " + proposta.getDate().toString());
		model.addAttribute("detalhesProposta", strings);
		return "propostaLei";
	}
	
	@GetMapping("/criarProjetoLei")
	public String createProjetoLei(Model model) {
		List<Tema> temasL = democracia2.temaController().getTemas();
		ArrayList<Tema> temas = new ArrayList<>();
		for (Tema x : temasL) {
			temas.add(x);
		}
		model.addAttribute("temas", temas);
		return "criarProjetoLei";
	}

	@PostMapping("/criarProjetoLei")
	public String createProjetoLei(Model model, HttpSession session, @RequestParam("titulo") String titulo,
			@RequestParam("descricao") String descricao, @RequestParam("tema") long temaid) throws IOException {
		Tema tema = null;
		List<Tema> temasL = democracia2.temaController().getTemas();
		ArrayList<Tema> temas = new ArrayList<>();
		for (Tema x : temasL) {
			if(x.getId() == temaid){
				tema = x;
			}
			temas.add(x);
		}
		model.addAttribute("temas", temas);
		Cidadao delegado = (Cidadao) session.getAttribute("cidadao");
		LocalDateTime validade = LocalDateTime.now().plusYears(1);
		long newProjeto = democracia2.apresentarProjetoLei(titulo, descricao, tema, delegado, validade);
		if (newProjeto != -1) {
			model.addAttribute("message", "ProjetoLei created with ID: " + newProjeto);
		} else {
			model.addAttribute("message", "Failed to create ProjetoLei");
		}
		return "criarProjetoLei";
	}
	
	@GetMapping("/escolherDelegado")
	public String escolherDelegado(Model model, HttpSession session) {
		List<Tema> temasL = democracia2.temaController().getTemas();
		ArrayList<Tema> temas = new ArrayList<>();
		for (Tema tema : temasL) {
			temas.add(tema);
		}
		model.addAttribute("temas", temas);
		return "escolherDelegado";
	}
	
	@PostMapping("/escolherDelegado")
	public String processEscolherDelegado(Model model, HttpSession session,
			@NonNull @RequestParam("temaID") long temaID, @NonNull @RequestParam("delegadoID") long delegadoID) {
		boolean delegadoEscolhido = democracia2.escolherDelegado(((Cidadao) session.getAttribute("cidadao")).getId(), delegadoID, temaID);
		model.addAttribute("escolherDelegado", delegadoEscolhido);
		List<Tema> temasL = democracia2.temaController().getTemas();
		ArrayList<Tema> temas = new ArrayList<>();
		for (Tema tema : temasL) {
			temas.add(tema);
		}
		model.addAttribute("temas", temas);
		return "escolherDelegado";
	}
}
