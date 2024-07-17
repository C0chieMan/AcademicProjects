package pt.ul.fc.css.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.ul.fc.css.example.demo.entities.Cidadao;

public interface CidadaoRepository extends JpaRepository<Cidadao, Long> {
	
	@Query("SELECT c FROM Cidadao c WHERE c.name LIKE %:q% OR c.surname LIKE %:q% ")
    List<Cidadao> findByName(@Param("q") String q);
	
}
