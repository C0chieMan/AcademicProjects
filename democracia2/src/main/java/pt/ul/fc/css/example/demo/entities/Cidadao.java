package pt.ul.fc.css.example.demo.entities;

import java.util.HashMap;
import java.util.Map;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;

import java.util.Objects;

import org.springframework.lang.NonNull;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Cidadao {
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @NonNull
    @Column(name = "NAME")
    private String name;
    
    @Column(name = "SURNAME")
    private String surname;
    
    @ElementCollection
    @CollectionTable(name = "CIDADAO_DELEGADOS_MAPPING", 
      joinColumns = {@JoinColumn(name = "CIDADAO_ID", referencedColumnName = "ID")})
    @MapKeyColumn(name = "TEMAS")
    @Column(name = "DELEGADOS")
    private Map<Long, Long> delegados;
    //<idTema, idDelegado>

    public Cidadao(@NonNull String name, String surname) {
        this.name = name;
        this.surname = surname;
        delegados = new HashMap<>();
    }
    
    protected Cidadao() {
        this.name = "James";
        this.surname = "Bond";
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
    
    public String getSurname() {
        return this.surname;
    }
    
    public boolean isDelegado() {
    	return this instanceof Delegado;
    }

    public void chooseDelegado(long temaID, long delegadoID) {
        this.delegados.put(temaID, delegadoID);
    }

    public Long getDelegado(long temaID) {
        return delegados.get(temaID);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (Cidadao) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.surname, that.surname) &&
                Objects.equals(this.delegados, that.delegados);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, delegados);
    }
    
    @Override
    public String toString() {
    	return "Cidadao[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "surname=" + surname + "]";
    }

}