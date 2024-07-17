package pt.ul.fc.css.example.demo;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;

import pt.ul.fc.css.example.demo.entities.Cidadao;
import pt.ul.fc.css.example.demo.repositories.CidadaoRepository;

@Controller
public class CidadaoController {
	
	private final CidadaoRepository cidadaoRepository;
	
	private ReentrantLock mutex;
	
	public CidadaoController(CidadaoRepository cidadaoRepository) {
        this.cidadaoRepository = cidadaoRepository;
        this.mutex = new ReentrantLock();
    }
	
	public Cidadao getCidadao(@NonNull Long id) {
		return cidadaoRepository.getReferenceById(id);
	}
	
	public List<Cidadao> getCidadaos(@NonNull String query) {
        return cidadaoRepository.findByName(query);
    }

	public List<Cidadao> getAllCidadaos() {
        return cidadaoRepository.findAll();
    }
	
	public Cidadao putCidadao(@NonNull Cidadao cidadao) {
		return cidadaoRepository.save(cidadao);
    }
	
	public void lock() {
		mutex.lock();
	}
	
	public void unlock() {
		mutex.unlock();
	}

}

