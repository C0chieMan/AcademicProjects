package pt.ul.fc.css.example.demo;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VotacaoDTO {
	
	private long id;
	private long idProposta;
    private LocalDateTime validade;
	private ArrayList<Long> votadoresDFav;
	private ArrayList<Long> votadoresDCon;
	
	public VotacaoDTO(@JsonProperty("id") long id, @JsonProperty("idProposta") long idProposta,
			@JsonProperty("validade") LocalDateTime validade, @JsonProperty("votadoresDFav") ArrayList<Long> votadoresDFav,
			@JsonProperty("votadoresDCon") ArrayList<Long> votadoresDCon) {
		this.id = id;
		this.idProposta = idProposta;
		this.validade = validade;
		this.votadoresDFav = votadoresDFav;
		this.votadoresDCon = votadoresDCon;
	}
	
	public long getId() {
		return id;
	}
	
	public long getIdProposta() {
		return idProposta;
	}

	public LocalDateTime getValidade() {
		return validade;
	}
    
	public ArrayList<Long> getVotadoresDFav(){
        return votadoresDFav;
    }
    
    public ArrayList<Long> getVotadoresDCon(){
        return votadoresDCon;
    }

}
