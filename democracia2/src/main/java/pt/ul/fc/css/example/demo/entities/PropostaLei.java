package pt.ul.fc.css.example.demo.entities;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.springframework.lang.NonNull;

@Entity
public final class PropostaLei {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Column(name = "TITULO")
    @NonNull
    private String titulo;

    @Column(name = "DESCRICAO")
    @NonNull
    private String descricao;

    @Column(name = "TEMA")
    @NonNull
    private long tema;

    @Column(name = "DELEGADO")
    @NonNull
    private long delegado;

    @Column(name = "VALIDADE")
    @NonNull
    @Temporal(value = TemporalType.TIMESTAMP)
    private LocalDateTime validade;
    
    @Column(name = "LISTA_APOIOS")
    @NonNull
    private ArrayList<Long> listaApoiantes;

    @Column(name = "N_APOIOS")
    @NonNull
    private int apoiantes;
    
    @Column(name = "CLOSED")
    @NonNull
    private boolean closed;

    //no main fazer get data e verificar que validade mázima é menor que 1 ano
    public PropostaLei (@NonNull String titulo, @NonNull String descricao, @NonNull Tema tema,
    		@NonNull Cidadao delegado, @NonNull LocalDateTime validade) throws IOException {
    	this.titulo = titulo;
        this.descricao = descricao;
        this.tema = tema.getId();
        this.delegado = delegado.getId();
        this.validade = validade;
        this.listaApoiantes = new ArrayList<>();
        this.apoiantes = 0;
        this.closed = false;
    }
    
    protected PropostaLei () {
    	this.titulo = "Project Doomsday";
        this.descricao = "The end is nigh!";
        this.tema = 0;
        this.delegado = 0;
        this.validade = LocalDateTime.now();
        this.listaApoiantes = new ArrayList<>();
        this.apoiantes = 0;
        this.closed = true;
    }

    public long getId(){
        return this.id;
    }

    public String getTitle(){
        return this.titulo;
    }

    public String getDescription(){
        return this.descricao;
    }

    public long getTema(){
        return this.tema;
    }

    public long getDelegado(){
        return this.delegado;
    }

    public LocalDateTime getDate(){
        return this.validade;
    }

    public int getSupporters(){
        return this.apoiantes;
    }

    public ArrayList<Long> getListaApoiantes(){
        return this.listaApoiantes;
    }

    public boolean addSupport(long cidadaoID){
    	if (!closed) {
        	listaApoiantes.add(cidadaoID);
            this.apoiantes++;
            if (apoiantes >= 10000) {
            	closed = true;
            }
            return true;
    	}
    	return false;
    }
    
    public boolean close() {
    	if (!validade.isAfter(LocalDateTime.now()) || apoiantes >= 10000) {
    		closed = true;
    	}
    	return closed;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (PropostaLei) obj;
        return Objects.equals(this.titulo, that.titulo) &&
                Objects.equals(this.tema, that.tema) &&
                Objects.equals(this.descricao, that.descricao) &&
                Objects.equals(this.delegado, that.delegado) &&
                Objects.equals(this.validade, that.validade) &&
                Objects.equals(this.apoiantes, that.apoiantes);
    }
    
}
