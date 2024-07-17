package pt.ul.fc.css.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;

import pt.ul.fc.css.example.demo.entities.Cidadao;
import pt.ul.fc.css.example.demo.entities.Delegado;
import pt.ul.fc.css.example.demo.entities.PropostaLei;
import pt.ul.fc.css.example.demo.entities.Tema;

@SpringBootTest
@Transactional
public class PropostaLeiTest  {

    @Autowired private PropostaLeiController propostaLeiController;

    LocalDateTime validade = LocalDateTime.of(2024, Month.JULY, 29, 19, 30, 40);

    Cidadao delegado = new Delegado("Paulo", "Seixo");
    Tema tema = new Tema("Saude", null);

    @Test
    @Transactional
    public void PropostaLeiNotNull() throws IOException {
        PropostaLei c = new PropostaLei("125 do costa", "Òbvio pelo titulo", tema, delegado, validade);
        assertNotNull(c);
    }

    @Test
    @Transactional
    public void PropostaLeiIdsDifferent() throws IOException{
        PropostaLei c = new PropostaLei("125 do costa", "Òbvio pelo titulo", tema, delegado, validade);
        PropostaLei d2 = new PropostaLei("125 do costa", "Òbvio pelo titulo", tema, delegado, validade);
        propostaLeiController.putPropostaLei(c);
        propostaLeiController.putPropostaLei(c);
        assertNotEquals(c.getId(), d2.getId());
    }

    @Test
    @Transactional
	public void confirmApoio() throws IOException{
		PropostaLei p1 = new PropostaLei("125 do costa", "Òbvio pelo titulo", tema, delegado, validade);
		p1.addSupport(delegado.getId());
		int tamanho_list = p1.getListaApoiantes().size();
		
		assertEquals(tamanho_list, 1);
		assertEquals(p1.getSupporters(), 1);
	}

    @Test
    @Transactional
    public void getByID() throws IOException{
        PropostaLei c = new PropostaLei("175 do Sócrates", "Òbvio pelo titulo", tema, delegado, validade);
        propostaLeiController.putPropostaLei(c);
        assertEquals(c.getId(), propostaLeiController.getPropostaLei(c.getId()).getId());
    }

    @Test
    @Transactional
    public void getsClosed() throws IOException{
        LocalDateTime validade2 = LocalDateTime.now().plusSeconds(2);
        PropostaLei c = new PropostaLei("175 do Sócrates", "Òbvio pelo titulo", tema, delegado, validade2);
        assertFalse(c.close());
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(c.close());
    }

    
}
