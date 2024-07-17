package pt.ul.fc.css.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import pt.ul.fc.css.example.demo.entities.Tema;

@SpringBootTest
@Transactional
public class TemaTest {

    @Autowired private TemaController TemaController;

    @Test
    @Transactional
    public void TemaNotNull(){
        Tema c = new Tema("saude", null);
        assertNotNull(c);
    }

    @Test
    @Transactional
    public void TemaIdsDifferent(){
        Tema c = new Tema("saude", null);
        Tema d2 = new Tema("saude", null);
        TemaController.putTema(c);
        TemaController.putTema(d2);
        assertNotEquals(c.getId(), d2.getId());
    }
    

    @Test
    @Transactional
    public void saveTema(){
        Tema c = new Tema("saude", null);
        Tema d2 = new Tema("Paulo", (long) 23);
        TemaController.putTema(c);
        TemaController.putTema(d2);
        assertTrue(TemaController.getTemas().size() >= 2);
    }
    
    @Test
    @Transactional
    public void isFather(){
        Tema c = new Tema("saude", null);
        Tema d2 = new Tema("Paulo", c.getId());
        assertEquals(c.getId(), d2.getTemaPai());
    }

    @Test
    @Transactional
    public void getByID(){
        Tema c = new Tema("saude", null);
        TemaController.putTema(c);
        assertEquals(c.getId(), TemaController.getTema(c.getId()).getId());
    }

}
