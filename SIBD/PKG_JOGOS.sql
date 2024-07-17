/*
 SIBD 2021/2022, Etapa 4, Grupo 56
 Henrique Catarino - 56278 - LEI4
 Francisco Oliveira - 56318 - LEI1
 Miguel Nunes - 56338 - LEI4
 */

DECLARE
    result atleta.numero%TYPE;
    medal_cursor SYS_REFCURSOR;
    athlete atleta.numero%TYPE;
    name atleta.nome%TYPE;
    year jogos.ano%TYPE;
    modality participa.modalidade%TYPE;
    position participa.posicao%TYPE;
BEGIN
    result := PKG_JOGOS.REGISTA_ATLETA('Ana Cacho Paulo', 1969, 'F', 55, 'Portugal');
    result := PKG_JOGOS.REGISTA_ATLETA('Filipe Lopes', 1996, 'M', 67, 'Brazil');
    result := PKG_JOGOS.REGISTA_ATLETA('Sara Lopes', 1984, 'F', 73, 'Brazil');
    result := PKG_JOGOS.REGISTA_ATLETA('Rafaela Lopes', 1990, 'F', 68, 'Portugal');
    result := PKG_JOGOS.REGISTA_ATLETA('Johnathan Smith', 1980, 'M', 70, 'United Kingdom');
    result := PKG_JOGOS.REGISTA_ATLETA('Michael Hawk', 1990, 'M', 65, 'United Kingdom');
    result := PKG_JOGOS.REGISTA_ATLETA('Benjamin Dover', 2003, 'M', 69, 'United Kingdom');
    PKG_JOGOS.REGISTA_EDICAO(1, 2008, 'Japan', 'Hiroshima');
    PKG_JOGOS.REGISTA_EDICAO(2, 2012, 'United Kingdom', 'London');
    PKG_JOGOS.REGISTA_EDICAO(3, 2016, 'Brazil', 'Rio de Janeiro');
    PKG_JOGOS.REGISTA_EDICAO(4, 2020, 'Japan', 'Tokio');
    PKG_JOGOS.REGISTA_PARTICIPACAO(1, 1, 'Athletics', 1);
    PKG_JOGOS.REGISTA_PARTICIPACAO(1, 2, 'Athletics', 3);
    PKG_JOGOS.REGISTA_PARTICIPACAO(1, 3, 'Athletics', 1);
    PKG_JOGOS.REGISTA_PARTICIPACAO(1, 4, 'Athletics', 1);
    PKG_JOGOS.REGISTA_PARTICIPACAO(2, 4, 'Swimming', 1);
    PKG_JOGOS.REGISTA_PARTICIPACAO(3, 1, 'Swimming', 1);
    PKG_JOGOS.REGISTA_PARTICIPACAO(3, 2, 'Swimming', 1);
    PKG_JOGOS.REGISTA_PARTICIPACAO(3, 4, 'Swimming', 1);
    PKG_JOGOS.REGISTA_PARTICIPACAO(4, 3, 'Athletics', 3);
    PKG_JOGOS.REGISTA_PARTICIPACAO(4, 4, 'Athletics', 1);
    PKG_JOGOS.REGISTA_PARTICIPACAO(5, 2, 'Swimming', 1);
    PKG_JOGOS.REGISTA_PARTICIPACAO(5, 3, 'Swimming', 1);
    PKG_JOGOS.REGISTA_PARTICIPACAO(6, 1, 'Swimming', 1);
    PKG_JOGOS.REGISTA_PARTICIPACAO(6, 3, 'Swimming', 1);
    PKG_JOGOS.REGISTA_PARTICIPACAO(6, 4, 'Swimming', 1);
    PKG_JOGOS.REGISTA_PARTICIPACAO(6, 3, 'Athletics', 1);
    PKG_JOGOS.REGISTA_PARTICIPACAO(6, 4, 'Athletics', 1);
    PKG_JOGOS.REMOVE_PARTICIPACAO(1, 1, 'Athletics');
    PKG_JOGOS.REMOVE_EDICAO(2);
    PKG_JOGOS.REMOVE_ATLETA(1);
    medal_cursor := PKG_JOGOS.LISTA_MEDALHAS(6);
    LOOP
        FETCH medal_cursor INTO athlete, name, year, modality, position;
        EXIT WHEN medal_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE(athlete || ' | ' || name || ' | ' || year || ' | ' ||
                             modality || ' | ' || position);
    END LOOP;
END;
