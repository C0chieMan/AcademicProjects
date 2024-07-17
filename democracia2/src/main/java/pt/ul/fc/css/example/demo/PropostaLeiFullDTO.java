package pt.ul.fc.css.example.demo;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PropostaLeiFullDTO {
	
	private long id;
	private String titulo;
	private String descricao;
	private String tema;
	private long delegado;
	private LocalDateTime validade;
	private int apoiantes;
	private ArrayList<Long> listaApoiantes;
	private boolean fechada;

    public PropostaLeiFullDTO (@JsonProperty("id") long id, @JsonProperty("titulo") String titulo,
    		@JsonProperty("descricao") String descricao, @JsonProperty("tema") String tema,
    		@JsonProperty("delegado") long delegado, @JsonProperty("date") LocalDateTime validade,
    		@JsonProperty("apoiantes") int apoiantes, @JsonProperty("listaApoiantes") ArrayList<Long> listaApoiantes,
    		@JsonProperty("fechada") boolean fechada) {
    	this.id = id;
    	this.titulo = titulo;
        this.descricao = descricao;
        this.tema = tema;
        this.delegado = delegado;
        this.validade = validade;
        this.apoiantes = apoiantes;
        this.listaApoiantes = listaApoiantes;
        this.fechada = fechada;
    }

    public long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getTema() {
        return tema;
    }

    public long getDelegado() {
        return delegado;
    }

    public LocalDateTime getDate() {
        return validade;
    }

    public int getApoiantes() {
        return apoiantes;
    }

    public ArrayList<Long> getListaApoiantes() {
        return listaApoiantes;
    }
    
    public boolean getFechada() {
    	return fechada;
    }

}
