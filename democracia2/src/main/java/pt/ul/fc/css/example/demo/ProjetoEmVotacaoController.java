package pt.ul.fc.css.example.demo;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;

import pt.ul.fc.css.example.demo.entities.ProjetoEmVotacao;
import pt.ul.fc.css.example.demo.repositories.ProjetoEmVotacaoRepository;

@Controller
public class ProjetoEmVotacaoController {

	private final ProjetoEmVotacaoRepository projetoEmVotacaoRepository;
	
	private ReentrantLock mutex;
	
	public ProjetoEmVotacaoController(ProjetoEmVotacaoRepository projetoEmVotacaoRepository) {
        this.projetoEmVotacaoRepository = projetoEmVotacaoRepository;
        this.mutex = new ReentrantLock();
    }
	
	public ProjetoEmVotacao getProjetoEmVotacao(@NonNull Long id) {
		return projetoEmVotacaoRepository.getReferenceById(id);
	}
	
	public List<ProjetoEmVotacao> getProjetos() {
        return projetoEmVotacaoRepository.findAll();
    }
	
	public ProjetoEmVotacao putProjetoEmVotacao(@NonNull ProjetoEmVotacao projetoEmVotacao) {
		return projetoEmVotacaoRepository.save(projetoEmVotacao);
    }
	
	public void lock() {
		mutex.lock();
	}
	
	public void unlock() {
		mutex.unlock();
	}
	
}
