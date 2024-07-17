package pt.ul.fc.css.example.demo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PropostaLeiDTO {
	
	private long id;
	private String titulo;
	private LocalDateTime validade;

    public PropostaLeiDTO (@JsonProperty("id") long id, @JsonProperty("title") String titulo, 
    		@JsonProperty("date") LocalDateTime validade) {
    	this.id = id;
    	this.titulo = titulo;
        this.validade = validade;
    }
    
    public long getId(){
        return this.id;
    }

    public String getTitle(){
        return this.titulo;
    }

    public LocalDateTime getDate(){
        return this.validade;
    }

}
