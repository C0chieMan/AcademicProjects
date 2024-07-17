package pt.ul.fc.css.example.demo.entities;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Tema {
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Column(name = "TEMA_PAI")
    private Long temaPai;

    @NonNull
    @Column(name = "TITULO")
    private String titulo;

    public Tema(@NonNull String titulo, Long temaPai) {
        this.titulo = titulo;
        this.temaPai = temaPai;
    }
    
    protected Tema() {
    	this.titulo = "Apocalypse";
    	this.temaPai = null;
    }

    public long getId(){
        return this.id;
    }

    public String getTitulo(){
        return this.titulo;
    }

    public Long getTemaPai(){
        return this.temaPai;
    }
}
