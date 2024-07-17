package pt.ul.fc.css.example.demo;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import pt.ul.fc.css.example.demo.entities.Cidadao;
import pt.ul.fc.css.example.demo.entities.Delegado;
import pt.ul.fc.css.example.demo.entities.ProjetoEmVotacao;
import pt.ul.fc.css.example.demo.entities.PropostaLei;
import pt.ul.fc.css.example.demo.entities.Tema;
import pt.ul.fc.css.example.demo.repositories.CidadaoRepository;
import pt.ul.fc.css.example.demo.repositories.ProjetoEmVotacaoRepository;
import pt.ul.fc.css.example.demo.repositories.PropostaLeiRepository;
import pt.ul.fc.css.example.demo.repositories.TemaRepository;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(CidadaoRepository cidadaoRepository, 
    		PropostaLeiRepository propostaLeiRepository,
    		ProjetoEmVotacaoRepository projetoEmVotacaoRepository,
    		TemaRepository temaRepository) {
        return (args) -> {
        	//initialize demonstration data
        	Delegado d1 = cidadaoRepository.save(new Delegado("Jack", "Bauer"));
        	Delegado d2 = cidadaoRepository.save(new Delegado("Chloe", "O'Brian"));
        	cidadaoRepository.save(new Delegado("Kim", "Bauer"));
        	cidadaoRepository.save(new Delegado("David", "Palmer"));
        	Delegado d5 = cidadaoRepository.save(new Delegado("Michelle", "Dessler"));
        	
        	Tema temaSaude = temaRepository.save(new Tema("Saúde", null));
        	Tema temaHosp = temaRepository.save(new Tema("Hospitais", temaSaude.getId()));
        	temaRepository.save(new Tema("Educação", null));
        	Tema temaInfra = temaRepository.save(new Tema("Infraestruturas", null));
        	Tema temaAero = temaRepository.save(new Tema("Aeroportos", temaInfra.getId()));
        	temaRepository.save(new Tema("Ferrovias", temaInfra.getId()));
        	Tema temaEcon = temaRepository.save(new Tema("Economia", null));
        	Tema temaBolsa = temaRepository.save(new Tema("Bolsa de Valores", temaEcon.getId()));
        	Tema temaCripto = temaRepository.save(new Tema("Cripto", temaBolsa.getId()));
        	
        	propostaLeiRepository.save(new PropostaLei("Aeroporto do Montijo",
        			"Construir um aeroporto no Montijo", temaAero, d1, LocalDateTime.now().plusMonths(9)));
        	PropostaLei proposta2 = propostaLeiRepository.save(new PropostaLei("Hospital de Sintra",
        			"Construir um hospital em Sintra", temaHosp, d2, LocalDateTime.now().plusMonths(6)));
        	PropostaLei proposta3 = propostaLeiRepository.save(new PropostaLei("Taxar Criptomoedas",
        			"Introduzir uma taxa à venda de criptomoedas", temaCripto, d5,
        			LocalDateTime.now().plusMonths(5).plusDays(13)));
        	
        	//adding support from citizens that don't exist because creating and saving 10000 real citizens
        	//takes too much time
        	for (int i = 6; i < 10006; i++) {
        		proposta2.addSupport(i);
        		if (i < 373) {
        			proposta3.addSupport(i);
        		}
        	}
        	
        	for (int i = 1; i <= 50; i++) {
        		Cidadao cidadao = cidadaoRepository.save(new Cidadao("Cidadao", "" + i));
        		if (i < 23) {
        			cidadao.chooseDelegado(temaHosp.getId(), d2.getId());
        		}
        		else if (i < 41) {
        			cidadao.chooseDelegado(temaHosp.getId(), d5.getId());
        		}
    			cidadaoRepository.save(cidadao);
        	}

        	proposta2.close();
        	ProjetoEmVotacao projeto2 = projetoEmVotacaoRepository.save(
					new ProjetoEmVotacao(proposta2.getId(), LocalDateTime.now().plusMonths(2)));
			projeto2.votarDelegadoFavor(d2);
			projeto2.votarDelegadoContra(d5);
			
			for (Cidadao cidadao : cidadaoRepository.findAll().subList(25, 37)) {
				projeto2.votarCidadaoFavor(cidadao);
			}
			
			for (Cidadao cidadao : cidadaoRepository.findAll().subList(41, 48)) {
				projeto2.votarCidadaoContra(cidadao);
			}
			
			propostaLeiRepository.save(proposta2);
			propostaLeiRepository.save(proposta3);
			projetoEmVotacaoRepository.save(projeto2);
        };
    }

}