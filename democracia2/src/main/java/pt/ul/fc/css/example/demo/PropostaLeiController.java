package pt.ul.fc.css.example.demo;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;

import pt.ul.fc.css.example.demo.entities.PropostaLei;
import pt.ul.fc.css.example.demo.repositories.PropostaLeiRepository;

@Controller
public class PropostaLeiController {
	
	private final PropostaLeiRepository propostaLeiRepository;
	
	private ReentrantLock mutex;
	
	public PropostaLeiController(PropostaLeiRepository propostaLeiRepository) {
        this.propostaLeiRepository = propostaLeiRepository;
        this.mutex = new ReentrantLock();
    }
	
	public PropostaLei getPropostaLei(@NonNull Long id) {
		return propostaLeiRepository.getReferenceById(id);
	}
	
	public List<PropostaLei> getPropostas() {
        return propostaLeiRepository.findAll();
    }
	
	public PropostaLei putPropostaLei(@NonNull PropostaLei propostaLei) {
        return propostaLeiRepository.save(propostaLei);
    }
	
	public void lock() {
		mutex.lock();
	}
	
	public void unlock() {
		mutex.unlock();
	}

}