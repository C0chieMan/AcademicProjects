package pt.ul.fc.css.example.demo;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;

import pt.ul.fc.css.example.demo.entities.Tema;
import pt.ul.fc.css.example.demo.repositories.TemaRepository;

@Controller
public class TemaController {
	
	private final TemaRepository temaRepository;
	
	private ReentrantLock mutex;
	
	public TemaController(TemaRepository temaRepository) {
        this.temaRepository = temaRepository;
        this.mutex = new ReentrantLock();
    }
	
	public Tema getTema(@NonNull Long id) {
		return temaRepository.getReferenceById(id);
	}

	public List<Tema> getTemas() {
        return temaRepository.findAll();
    }
	
	public Tema putTema(@NonNull Tema tema) {
        return temaRepository.save(tema);
    }
	
	public void lock() {
		mutex.lock();
	}
	
	public void unlock() {
		mutex.unlock();
	}

}
