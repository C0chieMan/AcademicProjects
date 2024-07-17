package pt.ul.fc.css.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import pt.ul.fc.css.example.demo.entities.Cidadao;
import pt.ul.fc.css.example.demo.entities.Delegado;

@SpringBootTest
@Transactional
public class CidadaoTest {

    @Autowired
    private CidadaoController cidadaoController;

    @Test
    @Transactional
    public void cidadaoNotNull() {
        Cidadao c = new Cidadao("Paulo", "Carvalhal");
        cidadaoController.putCidadao(c);
        assertNotNull(c);
    }

    @Test
    @Transactional
    public void cidadaoIdsDifferent() {
        Cidadao c = new Cidadao("Paulo", "Carvalhal");
        Cidadao d2 = new Cidadao("Paulo", "Carvalhal");
        cidadaoController.putCidadao(c);
        cidadaoController.putCidadao(d2);
        assertNotEquals(c.getId(), d2.getId());
    }

    @Test
    @Transactional
    public void saveCidadao() {
        Cidadao c = new Cidadao("Paulo", "Seixo");
        Cidadao d2 = new Delegado("Paulo", "Seixo");
        cidadaoController.putCidadao(c);
        cidadaoController.putCidadao(d2);
        assertTrue(cidadaoController.getCidadaos("Seixo").size() >= 2);
    }

    @Test
    @Transactional
    public void getByName() {
        Cidadao c = new Cidadao("Paulo", "Carvalhal");
        cidadaoController.putCidadao(c);
        List <Cidadao> names = cidadaoController.getCidadaos("Paulo");
        assertTrue(names.size() > 0);
    }

    @Test
    @Transactional
    public void getByID() {
        Cidadao c = new Cidadao("Afonso", "Henriques");
        cidadaoController.putCidadao(c);
        assertEquals(c.getId(), cidadaoController.getCidadao(c.getId()).getId());
    }
    
}