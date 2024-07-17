package pt.ul.fc.css.example.demo.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.lang.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public final class ProjetoEmVotacao {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private long id;
	
	@NonNull
	@Column(name = "ID_PROPOSTA") 
	private long idProposta;
	
	@NonNull
	@Column(name = "VALIDADE") 
	@Temporal(value = TemporalType.TIMESTAMP)
    private LocalDateTime validade;
	
	@NonNull
	@Column(name = "FAVOR") 
	private long votos_fav;

	@NonNull
	@Column(name = "CONTRA") 
	private long votos_con;
	
	@NonNull
    @Column(name = "VOTADORES_CID")
    private ArrayList<Long> votadoresC;
	
	@NonNull
    @Column(name = "VOTADORES_D_FAV")
    private ArrayList<Long> votadoresDFav;
	
	@NonNull
    @Column(name = "VOTADORES_D_CON")
    private ArrayList<Long> votadoresDCon;
	
	@NonNull
	@Column(name = "FECHADA")
	private boolean fechada;
	
	@Column(name = "APROVADA")
	private Boolean aprovada;
	
	public ProjetoEmVotacao(@NonNull long idProposta, @NonNull LocalDateTime validade) {
		this.idProposta = idProposta;
		this.validade = validade;
		this.votos_fav = 0;
		this.votos_con = 0;
        this.votadoresC = new ArrayList<>();
        this.votadoresDFav = new ArrayList<>();
        this.votadoresDCon = new ArrayList<>();
        this.fechada = false;
        this.aprovada = null;
	}
	
	protected ProjetoEmVotacao() {
		this.idProposta = -1;
		this.validade = LocalDateTime.now();
		this.votos_fav = 0;
		this.votos_con = 0;
        this.votadoresC = new ArrayList<>();
        this.votadoresDFav = new ArrayList<>();
        this.votadoresDCon = new ArrayList<>();
        this.fechada = true;
        this.aprovada = null;
	}
	
	public long getId() {
		return id;
	}
	
	public long getIdProposta() {
		return idProposta;
	}
	
    public boolean votarCidadaoFavor(Cidadao cidadao) {
    	if (!votadoresC.contains(cidadao.getId()) && !cidadao.isDelegado()) {
        	votos_fav++;
            votadoresC.add(cidadao.getId());
            return true;
    	}
    	return false;
    }
    
    public boolean votarCidadaoContra(Cidadao cidadao) {
    	if (!votadoresC.contains(cidadao.getId()) && !cidadao.isDelegado()) {
    		votos_con++;
    		votadoresC.add(cidadao.getId());
    		return true;
    	}
    	return false;
    }

    public boolean votarDelegadoFavor(Cidadao delegado) {
    	if (!votadoresC.contains(delegado.getId()) && delegado.isDelegado()) {
    		votos_fav++;
    		votadoresC.add(delegado.getId());
    		votadoresDFav.add(delegado.getId());
    		return true;
    	}
    	return false;
    }
    
    public boolean votarDelegadoContra(Cidadao delegado) {
    	if (!votadoresC.contains(delegado.getId()) && delegado.isDelegado()) {
    		votos_con++;
    		votadoresC.add(delegado.getId());
    		votadoresDCon.add(delegado.getId());
    		return true;
    	}
    	return false;
    }
	
	public long getVotosFav() {
		return this.votos_fav ;
	}
	
	public long getVotosCon() {
		return this.votos_con;
	}
	
	public LocalDateTime getValidade() {
		return this.validade;
	}
	
	public ArrayList<Long> getVotadoresC(){
        return this.votadoresC;
    }

    public ArrayList<Long> getVotadoresDFav(){
        return this.votadoresDFav;
    }
    
    public ArrayList<Long> getVotadoresDCon(){
        return this.votadoresDCon;
    }
    
    public boolean fechar() {
    	if (!validade.isAfter(LocalDateTime.now())) {
    		fechada = true;
    	}
    	return fechada;
    }
    
    public boolean getFechada() {
    	return fechada;
    }
    
    public void setAprovada(boolean aprovada) {
    	this.aprovada = aprovada;
    }
    
    public Boolean getAprovada() {
    	return aprovada;
    }
	
}	
