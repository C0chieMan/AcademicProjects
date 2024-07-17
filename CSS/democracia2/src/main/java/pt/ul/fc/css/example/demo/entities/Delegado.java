package pt.ul.fc.css.example.demo.entities;

import org.springframework.lang.NonNull;

import jakarta.persistence.Entity;

@Entity
public class Delegado extends Cidadao {
	
	public Delegado(@NonNull String name, String surname) {
        super(name, surname);
    }
    
    protected Delegado() {
        super();
    }
	
}
