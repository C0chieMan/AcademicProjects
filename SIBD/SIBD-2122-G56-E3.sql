--SIBD 2021/2022, Etapa 3, Grupo 56
--Henrique Catarino - 56278 - LEI4
--Francisco Oliveira - 56318 - LEI1
--Miguel Nunes - 56338 - LEI4

INSERT INTO atleta
    VALUES (1, 'Ana Cacho Paulo', 1969, 'F', 55, 'Portugal' );
INSERT INTO atleta
    VALUES (2, 'Filipe Lopes', 1996, 'M', 67, 'Brazil' );
INSERT INTO atleta
    VALUES (3, 'Sara Lopes', 1984, 'F', 73, 'Brazil' );
INSERT INTO atleta
    VALUES (4, 'Rafaela Lopes', 1990, 'F', 68, 'Portugal' );
INSERT INTO atleta
    VALUES (5, 'Johnathan Smith', 1980, 'M', 70, 'United Kingdom');
INSERT INTO atleta
    VALUES (6, 'Michael Hawk', 1990, 'M', 65, 'United Kingdom');
INSERT INTO atleta
    VALUES (7, 'Benjamin Dover', 2003, 'M', 69, 'United Kingdom');

INSERT INTO jogos
    VALUES (29, 2008, 'Japan', 'Hiroshima');
INSERT INTO jogos
    VALUES (30, 2012, 'United Kingdom', 'London');
INSERT INTO jogos
    VALUES (31, 2016, 'Brazil', 'Rio de Janeiro');
INSERT INTO jogos
    VALUES (32, 2020, 'Japan', 'Tokio');

INSERT INTO participa
    VALUES (1, 29, 'Athletics', 1);
INSERT INTO participa
    VALUES (1, 30, 'Athletics', 3);
INSERT INTO participa
    VALUES (1, 31, 'Athletics', 1);
INSERT INTO participa
    VALUES (1, 32, 'Athletics', 1);
INSERT INTO participa
    VALUES (2, 32, 'Swimming', 1);
INSERT INTO participa
    VALUES (3, 29, 'Swimming', 1);
INSERT INTO participa
    VALUES (3, 30, 'Swimming', 1);
INSERT INTO participa
    VALUES (3, 32, 'Swimming', 1);
INSERT INTO participa
    VALUES (4, 31, 'Athletics', 3);
INSERT INTO participa
    VALUES (4, 32, 'Athletics', 1);
INSERT INTO participa
    VALUES (5, 30, 'Swimming', 1);
INSERT INTO participa
    VALUES (5, 31, 'Swimming', 1);
INSERT INTO participa
    VALUES (6, 29, 'Swimming', 1);
INSERT INTO participa
    VALUES (6, 31, 'Swimming', 1);
INSERT INTO participa
    VALUES (6, 32, 'Swimming', 1);
INSERT INTO participa
    VALUES (6, 31, 'Athletics', 1);
INSERT INTO participa
    VALUES (6, 32, 'Athletics', 1);

SELECT A.numero, A.nome, 2021 - A.nascimento as idade, J.edicao, J.cidade
FROM atleta A, jogos J
    WHERE (A.nome LIKE '% Lopes')
    AND (upper(A.genero) = 'F')
    AND (J.pais = 'Japan')
    AND (0 < (SELECT COUNT(P.posicao)
                FROM participa P
                WHERE (P.posicao <= 3)
                AND (A.numero = P.atleta)
                AND (J.edicao = P.jogos)))
    ORDER BY J.edicao DESC, idade DESC, A.nome, A.numero;

SELECT A.numero, A.nome
FROM atleta A
    WHERE (upper(A.genero) = 'M')
    AND (0 = (SELECT COUNT(P.jogos)
              FROM participa P, jogos J
                WHERE (A.numero = P.atleta)
                AND (P.jogos = J.edicao)
                AND A.pais = J.pais))
    AND (3 > (SELECT COUNT(P.jogos)
              FROM participa P, jogos J
                WHERE (A.numero = P.atleta)
                AND (P.jogos = J.edicao)
                AND (P.modalidade = 'Swimming')
                AND (A.pais != J.pais)))
    ORDER BY A.nome, A.numero DESC;

SELECT A.numero, A.nome, A.nascimento, A.genero, A.peso, A.pais
FROM atleta A
    WHERE (A.peso < (SELECT AVG(A2.peso)
                     FROM atleta A2))
    AND ((SELECT COUNT(J.ano)
          FROM jogos J
            WHERE (J.ano >= 2016)) = (SELECT COUNT(P.jogos)
                                      FROM participa P, jogos J
                                        WHERE (P.atleta = A.numero)
                                        AND (P.jogos = J.edicao)
                                        AND (J.ano >= 2016)
                                        AND (P.modalidade = 'Athletics')))
    ORDER BY A.peso, A.numero DESC;

SELECT DISTINCT J.edicao, J.pais, A.pais as pais_atletas, A.genero, (SELECT COUNT(P2.posicao)
                                                     FROM participa P2, atleta A2
                                                        WHERE (A2.pais = A.pais)
                                                        AND (P2.jogos = J.edicao)
                                                        AND (A2.genero = A.genero)
                                                        AND (P2.atleta = A2.numero)
                                                        AND (P2.posicao = 1)) as ouros
FROM jogos J, atleta A, participa P
    WHERE (SELECT COUNT(P2.posicao)
           FROM participa P2, atleta A2
                WHERE (A2.pais = A.pais)
                AND (P2.jogos = J.edicao)
                AND (A2.genero = A.genero)
                AND (P2.atleta = A2.numero)
                AND (P2.posicao = 1)) = (SELECT MAX(COUNT(P3.posicao))
                                         FROM atleta A3, participa P3
                                            WHERE (A3.numero = P3.atleta)
                                            AND (P3.posicao = 1)
                                            AND (A3.genero = A.genero)
                                            AND (P3.jogos = J.edicao)
                                            GROUP BY A3.pais)
    ORDER BY J.edicao DESC, A.genero, A.pais;
