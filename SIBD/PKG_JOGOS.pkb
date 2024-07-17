/*
 SIBD 2021/2022, Etapa 4, Grupo 56
 Henrique Catarino - 56278 - LEI4
 Francisco Oliveira - 56318 - LEI1
 Miguel Nunes - 56338 - LEI4
 */

CREATE OR REPLACE PACKAGE BODY pkg_jogos IS

    FUNCTION regista_atleta(
        f_nome IN atleta.nome%TYPE,
        f_nascimento IN atleta.nascimento%TYPE,
        f_genero IN atleta.genero%TYPE,
        f_peso IN atleta.peso%TYPE,
        f_pais IN atleta.pais%TYPE)
        RETURN NUMBER
    IS
        new_primary atleta.numero%TYPE;
        CURSOR possible_keys IS
            SELECT numero + 1 AS numero
            FROM atleta
            MINUS
            SELECT numero
            FROM atleta;
        TYPE key_table IS TABLE OF possible_keys%ROWTYPE;
        keys key_table;
    BEGIN
        OPEN possible_keys;
        FETCH possible_keys BULK COLLECT INTO keys;
        CLOSE possible_keys;
        IF (keys.COUNT = 0) THEN
            new_primary := 1;
        ELSE
            new_primary := keys(keys.FIRST).numero;
        END IF;
        INSERT INTO atleta
            VALUES (new_primary, f_nome, f_nascimento, f_genero, f_peso, f_pais);
        RETURN new_primary;
    EXCEPTION
        WHEN OTHERS THEN
            IF (f_nome IS NULL) THEN
                RAISE_APPLICATION_ERROR(-20000, 'Athlete''s name must not be NULL');
            ELSIF (f_nascimento IS NULL) THEN
                RAISE_APPLICATION_ERROR(-20001, 'Athlete''s birth year must not be NULL');
            ELSIF (f_genero IS NULL) THEN
                RAISE_APPLICATION_ERROR(-20002, 'Athlete''s gender must not be NULL');
            ELSIF (f_peso IS NULL) THEN
                RAISE_APPLICATION_ERROR(-20003, 'Athlete''s weight must not be NULL');
            ELSIF (f_pais IS NULL) THEN
                RAISE_APPLICATION_ERROR(-20004, 'Athlete''s country must not be NULL');
            ELSIF (f_nascimento <= 0) THEN
                RAISE_APPLICATION_ERROR(-20005, 'Athlete''s birth year must be higher than 0');
            ELSIF (upper(f_genero) NOT IN ('M', 'F')) THEN
                RAISE_APPLICATION_ERROR(-20006, 'Athlete''s gender must be ''M'' or ''F''');
            ELSIF (f_peso <= 0) THEN
                RAISE_APPLICATION_ERROR(-20007, 'Athlete''s weight must be higher than 0');
            ELSE
                RAISE;
            END IF;
    END regista_atleta;

    PROCEDURE regista_edicao(
        p_numero IN jogos.edicao%TYPE,
        p_ano IN jogos.ano%TYPE,
        p_pais IN jogos.pais%TYPE,
        p_cidade IN jogos.cidade%TYPE)
    IS
        CURSOR previous_edition IS
            SELECT J.ano AS year
            FROM jogos J
            WHERE (J.edicao = p_numero - 1);
        TYPE edition_table IS TABLE OF previous_edition%ROWTYPE;
        previous edition_table;
        EDITION_SKIPPED EXCEPTION;
        PRAGMA EXCEPTION_INIT (EDITION_SKIPPED, -20008);
        EDITION_EARLY EXCEPTION;
        PRAGMA EXCEPTION_INIT (EDITION_EARLY, -20009);
        EDITION_INVALID EXCEPTION;
        PRAGMA EXCEPTION_INIT (EDITION_INVALID, -20010);
    BEGIN
        IF (p_numero = 1) THEN
            INSERT INTO jogos
                VALUES (p_numero, p_ano, p_pais, p_cidade);
        ELSIF (p_numero > 1) THEN
            OPEN previous_edition;
            FETCH previous_edition BULK COLLECT INTO previous;
            CLOSE previous_edition;
            IF (previous.COUNT = 1 AND p_ano >= (previous(previous.FIRST).year) + 4) THEN
                INSERT INTO jogos
                    VALUES (p_numero, p_ano, p_pais, p_cidade);
            ELSIF (previous.COUNT = 1) THEN
                RAISE EDITION_EARLY;
            ELSE
                RAISE EDITION_SKIPPED;
            END IF;
        ELSE
            RAISE EDITION_INVALID;
        END IF;
    EXCEPTION
        WHEN EDITION_SKIPPED THEN
            RAISE_APPLICATION_ERROR(-20008, 'Edition prior to this one does not exist');
        WHEN EDITION_EARLY THEN
            RAISE_APPLICATION_ERROR(-20009, '4 years have not passed since the last edition');
        WHEN EDITION_INVALID THEN
            RAISE_APPLICATION_ERROR(-20010, 'Edition number must be higher than 0');
        WHEN OTHERS THEN
            IF (SQLCODE = -1) THEN
                RAISE_APPLICATION_ERROR(-20011, 'Edition number already exists');
            ELSIF (p_ano < 1896) THEN
                RAISE_APPLICATION_ERROR(-20012, 'Edition year must not be lower than 1896');
            ELSE
                RAISE;
            END IF;
    END regista_edicao;

    PROCEDURE regista_participacao(
        p_atleta IN participa.atleta%TYPE,
        p_jogos IN participa.jogos%TYPE,
        p_modalidade IN participa.modalidade%TYPE,
        p_posicao IN participa.posicao%TYPE)
    IS
        CURSOR athlete IS
            SELECT numero
            FROM atleta
            WHERE (numero = p_atleta);
        TYPE athlete_table IS TABLE OF athlete%ROWTYPE;
        athlete_found athlete_table;
        CURSOR games IS
            SELECT edicao
            FROM jogos
            WHERE (edicao = p_jogos);
        TYPE games_table IS TABLE OF games%ROWTYPE;
        games_found games_table;
        ATHLETE_NOT_EXIST EXCEPTION;
        PRAGMA EXCEPTION_INIT (ATHLETE_NOT_EXIST, -20013);
        GAMES_NOT_EXIST EXCEPTION;
        PRAGMA EXCEPTION_INIT (GAMES_NOT_EXIST, -20014);
    BEGIN
        OPEN athlete;
        FETCH athlete BULK COLLECT INTO athlete_found;
        CLOSE athlete;
        OPEN games;
        FETCH games BULK COLLECT INTO games_found;
        CLOSE games;
        IF (athlete_found.COUNT = 0) THEN
            RAISE ATHLETE_NOT_EXIST;
        ELSIF (games_found.COUNT = 0) THEN
            RAISE GAMES_NOT_EXIST;
        ELSE
            INSERT INTO participa
                VALUES (p_atleta, p_jogos, p_modalidade, p_posicao);
        END IF;
    EXCEPTION
        WHEN ATHLETE_NOT_EXIST THEN
            RAISE_APPLICATION_ERROR(-20013, 'An athlete with this number does not exist');
        WHEN GAMES_NOT_EXIST THEN
            RAISE_APPLICATION_ERROR(-20014, 'An edition with this number does not exist');
        WHEN OTHERS THEN
            IF (SQLCODE = -1) THEN
                RAISE_APPLICATION_ERROR(-20015, 'This athlete has already participated in this modality ' ||
                                                'in these games');
            ELSIF (p_posicao IS NOT NULL AND p_posicao < 1) THEN
                RAISE_APPLICATION_ERROR(-20016, 'Position must not be lower than 1');
            ELSE
                RAISE;
            END IF;
    END regista_participacao;

    PROCEDURE remove_participacao(
        p_atleta IN participa.atleta%TYPE,
        p_jogos IN participa.jogos%TYPE,
        p_modalidade IN participa.modalidade%TYPE)
    IS
        PARTICIPATION_NOT_EXIST EXCEPTION;
        PRAGMA EXCEPTION_INIT (PARTICIPATION_NOT_EXIST, -20017);
    BEGIN
        DELETE FROM participa
            WHERE (atleta = p_atleta)
            AND (jogos = p_jogos)
            AND (modalidade = p_modalidade);
        IF (SQL%NOTFOUND) THEN
            RAISE PARTICIPATION_NOT_EXIST;
        END IF;
    EXCEPTION
        WHEN PARTICIPATION_NOT_EXIST THEN
            RAISE_APPLICATION_ERROR(-20017, 'This participation does not exist.');
        WHEN OTHERS THEN
            RAISE;
    END remove_participacao;

    PROCEDURE remove_edicao(
        p_numero IN jogos.edicao%TYPE)
    IS
        CURSOR participations IS
            SELECT atleta, jogos, modalidade
            FROM participa
            WHERE (jogos = p_numero);
        TYPE participations_table IS TABLE OF participations%ROWTYPE;
        participations_found participations_table;
        GAMES_NOT_EXIST EXCEPTION;
        PRAGMA EXCEPTION_INIT (GAMES_NOT_EXIST, -20014);
    BEGIN
        OPEN participations;
        FETCH participations BULK COLLECT INTO participations_found;
        CLOSE participations;
        IF (participations_found.COUNT > 0) THEN
            FOR position IN participations_found.FIRST .. participations_found.LAST LOOP
                remove_participacao(participations_found(position).atleta,
                    participations_found(position).jogos,
                    participations_found(position).modalidade);
            END LOOP;
        END IF;
        DELETE FROM jogos
            WHERE (edicao = p_numero);
        IF (SQL%NOTFOUND) THEN
            RAISE GAMES_NOT_EXIST;
        END IF;
    EXCEPTION
        WHEN GAMES_NOT_EXIST THEN
            RAISE_APPLICATION_ERROR(-20014, 'An edition with this number does not exist');
        WHEN OTHERS THEN
            RAISE;
    END remove_edicao;

    PROCEDURE remove_atleta(
        p_numero IN atleta.numero%TYPE)
    IS
        CURSOR participations IS
            SELECT atleta, jogos, modalidade
            FROM participa
            WHERE (atleta = p_numero);
        TYPE participations_table IS TABLE OF participations%ROWTYPE;
        participations_found participations_table;
        ATHLETE_NOT_EXIST EXCEPTION;
        PRAGMA EXCEPTION_INIT (ATHLETE_NOT_EXIST, -20013);
    BEGIN
        OPEN participations;
        FETCH participations BULK COLLECT INTO participations_found;
        CLOSE participations;
        IF (participations_found.COUNT > 0) THEN
            FOR position IN participations_found.FIRST .. participations_found.LAST LOOP
                remove_participacao(participations_found(position).atleta,
                    participations_found(position).jogos,
                    participations_found(position).modalidade);
            END LOOP;
        END IF;
        DELETE FROM atleta
            WHERE (numero = p_numero);
        IF (SQL%NOTFOUND) THEN
            RAISE ATHLETE_NOT_EXIST;
        END IF;
    EXCEPTION
        WHEN ATHLETE_NOT_EXIST THEN
            RAISE_APPLICATION_ERROR(-20013, 'An athlete with this number does not exist');
        WHEN OTHERS THEN
            RAISE;
    END remove_atleta;

    FUNCTION lista_medalhas(
        f_atleta IN atleta.numero%TYPE)
        RETURN SYS_REFCURSOR
    IS
        CURSOR athlete IS
            SELECT numero
            FROM atleta
            WHERE (numero = f_atleta);
        TYPE athlete_table IS TABLE OF athlete%ROWTYPE;
        athlete_found athlete_table;
        medals SYS_REFCURSOR;
        ATHLETE_NOT_EXIST EXCEPTION;
        PRAGMA EXCEPTION_INIT (ATHLETE_NOT_EXIST, -20013);
    BEGIN
        OPEN athlete;
        FETCH athlete BULK COLLECT INTO athlete_found;
        CLOSE athlete;
        IF (athlete_found.COUNT = 1) THEN
            OPEN medals FOR
                SELECT A.numero AS numero, A.nome AS nome, J.ano AS ano,
                    P.modalidade AS modalidade, P.posicao AS posicao
                FROM atleta A, jogos J, participa P
                    WHERE (A.numero = f_atleta)
                    AND (P.atleta = A.numero)
                    AND (P.jogos = J.edicao)
                    AND (P.posicao <= 3)
                    ORDER BY ano, posicao, modalidade;
            RETURN medals;
        ELSE
            RAISE ATHLETE_NOT_EXIST;
        END IF;
    EXCEPTION
        WHEN ATHLETE_NOT_EXIST THEN
            RAISE_APPLICATION_ERROR(-20013, 'An athlete with this number does not exist');
        WHEN OTHERS THEN
            RAISE;
    END lista_medalhas;

END pkg_jogos;
