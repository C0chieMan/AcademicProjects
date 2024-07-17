/*
 SIBD 2021/2022, Etapa 4, Grupo 56
 Henrique Catarino - 56278 - LEI4
 Francisco Oliveira - 56318 - LEI1
 Miguel Nunes - 56338 - LEI4
 */

CREATE OR REPLACE PACKAGE pkg_jogos IS

    /*
     Exceptions that can be thrown by these functions and procedures:
     -20000 - Athlete's name must not be NULL
     -20001 - Athlete's birth year must not be NULL
     -20002 - Athlete's gender must not be NULL
     -20003 - Athlete's weight must not be NULL
     -20004 - Athlete's country must not be NULL
     -20005 - Athlete's birth year must be higher than 0
     -20006 - Athlete's gender must be 'M' or 'F'
     -20007 - Athlete's weight must be higher than 0
     -20008 - Edition prior to this one does not exist
     -20009 - 4 years have not passed since the last edition
     -20010 - Edition number must be higher than 0
     -20011 - Edition number already exists
     -20012 - Edition year must not be lower than 1896
     -20013 - An athlete with this number does not exist
     -20014 - An edition with this number does not exist
     -20015 - This athlete has already participated in this modality in these games
     -20016 - Position must not be lower than 1
     -20017 - This participation does not exist.
     */

    --Registers an athlete and returns the olympic number assigned to them.
    FUNCTION regista_atleta(
        f_nome IN atleta.nome%TYPE,
        f_nascimento IN atleta.nascimento%TYPE,
        f_genero IN atleta.genero%TYPE,
        f_peso IN atleta.peso%TYPE,
        f_pais IN atleta.pais%TYPE)
        RETURN NUMBER;

    --Registers an edition of the olympic games.
    PROCEDURE regista_edicao(
        p_numero IN jogos.edicao%TYPE,
        p_ano IN jogos.ano%TYPE,
        p_pais IN jogos.pais%TYPE,
        p_cidade IN jogos.cidade%TYPE);

    /*Registers the participation of an athlete in a modality in a certain edition of the games
      and the position they achieved.*/
    PROCEDURE regista_participacao(
        p_atleta IN participa.atleta%TYPE,
        p_jogos IN participa.jogos%TYPE,
        p_modalidade IN participa.modalidade%TYPE,
        p_posicao IN participa.posicao%TYPE);

    --Removes the participation of an athlete in a modality in a certain edition of the games.
    PROCEDURE remove_participacao(
        p_atleta IN participa.atleta%TYPE,
        p_jogos IN participa.jogos%TYPE,
        p_modalidade IN participa.modalidade%TYPE);

    --Removes an edition of the games and all athlete participations in that edition.
    PROCEDURE remove_edicao(
        p_numero IN jogos.edicao%TYPE);

    --Removes an athlete and all of their participations in editions of the games.
    PROCEDURE remove_atleta(
        p_numero IN atleta.numero%TYPE);

    --Returns a Cursor with all of an athlete's participations in the games where they received a medal.
    FUNCTION lista_medalhas(
        f_atleta IN atleta.numero%TYPE)
        RETURN SYS_REFCURSOR;

END pkg_jogos;
