--SIBD 2021/2022, Etapa 2, Grupo 56
--Henrique Catarino - 56278 - LEI4
--Francisco Oliveira - 56318 - LEI1
--Miguel Nunes - 56338 - LEI4

DROP TABLE competitions;
DROP TABLE disputed;
DROP TABLE entourages;
DROP TABLE categories;
DROP TABLE modalities;
DROP TABLE athletes;
DROP TABLE countries;
DROP TABLE regions;
DROP TABLE games;

CREATE TABLE games (
    edition_number INTEGER,
    edition_year INTEGER CONSTRAINT NN_games_edition_year NOT NULL,
    opening_date DATE,
    closing_date DATE,
    CONSTRAINT PK_games
        PRIMARY KEY (edition_number),
    CONSTRAINT UN_games_edition_year
        UNIQUE (edition_year),
    CONSTRAINT CK_games_dates
        CHECK (opening_date < closing_date) --RIA 4
);

INSERT INTO games
VALUES (30, 2012, '27-JUL-2012', '12-AUG-2012');
INSERT INTO games
VALUES (31, 2016, '5-AUG-2016', '21-AUG-2016');

CREATE TABLE regions (
    region_id INTEGER,
    region_name VARCHAR(1000),
    region_population INTEGER,
    region_pib NUMBER(38, 3),
    CONSTRAINT PK_regions
        PRIMARY KEY (region_id)
);

INSERT INTO regions
VALUES (1, 'Russia', 143759445, 4328000000000);
INSERT INTO regions
VALUES (2, 'United States', 331449281, 22940000000000);
INSERT INTO regions
VALUES (3, 'China', 1411778724, 16640000000000);

CREATE TABLE countries (
    country_id,
    country_code CHAR(3) CONSTRAINT NN_countries_country_code NOT NULL,
    CONSTRAINT PK_countries
        PRIMARY KEY (country_id),
    CONSTRAINT UN_countries_country_code
        UNIQUE (country_code), --RIA 11
    CONSTRAINT FK_countries_country_id
        FOREIGN KEY (country_id)
        REFERENCES regions
        ON DELETE CASCADE
);

INSERT INTO countries
VALUES (1, 'RUS');
INSERT INTO countries
VALUES (2, 'USA');
INSERT INTO countries
VALUES (3, 'CHN');

CREATE TABLE athletes (
    athlete_id INTEGER,
    athlete_name VARCHAR(1000),
    athlete_birth_year INTEGER,
    athlete_gender CHAR(1),
    athlete_weight NUMBER(5, 2),
    athlete_height NUMBER(5, 2),
    CONSTRAINT PK_athletes
        PRIMARY KEY (athlete_id),
    CONSTRAINT CK_athletes_athlete_gender
        CHECK (athlete_gender = 'M' OR athlete_gender = 'F') --RIA 12
);

INSERT INTO athletes
VALUES (1, 'John Doe', 1995, 'M', 75, 180);
INSERT INTO athletes
VALUES (2, 'Jane Doe', 2000, 'F', 55, 165);
INSERT INTO athletes
VALUES (3, 'Vladimir', 1990, 'M', 90, 190);
INSERT INTO athletes
VALUES (4, 'Wong', 1993, 'M', 80, 185);

CREATE TABLE modalities (
    modality_name VARCHAR(1000),
    CONSTRAINT PK_modalities
        PRIMARY KEY (modality_name)
);

INSERT INTO modalities
VALUES ('Athletics');
INSERT INTO modalities
VALUES ('Swimming');

CREATE TABLE categories (
    modality_name,
    category_name VARCHAR(1000),
    category_gender CHAR(1),
    CONSTRAINT PK_categories
        PRIMARY KEY (modality_name, category_name),
    CONSTRAINT FK_categories_modality_name
        FOREIGN KEY (modality_name)
        REFERENCES modalities
        ON DELETE CASCADE
);

INSERT INTO categories
VALUES ('Athletics', 'Male High Jump', 'M');
INSERT INTO categories
VALUES ('Swimming', 'Male Backstroke', 'M');
INSERT INTO categories
VALUES ('Swimming', 'Female Backstroke', 'F');

CREATE TABLE entourages (
    country_id,
    edition_number,
    registration_date DATE,
    CONSTRAINT PK_entourages
        PRIMARY KEY (country_id, edition_number),
    CONSTRAINT FK_entourages_country_id
        FOREIGN KEY (country_id)
        REFERENCES countries,
    CONSTRAINT FK_entourages_edition_number
        FOREIGN KEY (edition_number)
        REFERENCES games
);

INSERT INTO entourages
VALUES (1, 30, '17-OCT-2011');
INSERT INTO entourages
VALUES (3, 30, '21-DEC-2011');
INSERT INTO entourages
VALUES (2, 30, '1-JAN-2012');
INSERT INTO entourages
VALUES (3, 31, '23-FEB-2016');
INSERT INTO entourages
VALUES (2, 31, '8-JUN-2016');

CREATE TABLE disputed (
    modality_name,
    category_name,
    edition_number,
    CONSTRAINT PK_disputed
        PRIMARY KEY (modality_name, category_name, edition_number),
    CONSTRAINT FK_disputed_modality_category
        FOREIGN KEY (modality_name, category_name)
        REFERENCES categories,
    CONSTRAINT FK_disputed_edition_number
        FOREIGN KEY (edition_number)
        REFERENCES games
);

INSERT INTO disputed
VALUES ('Swimming', 'Male Backstroke', 30);
INSERT INTO disputed
VALUES ('Swimming', 'Female Backstroke', 30);
INSERT INTO disputed
VALUES ('Athletics', 'Male High Jump', 30);
INSERT INTO disputed
VALUES ('Swimming', 'Female Backstroke', 31);
INSERT INTO disputed
VALUES ('Athletics', 'Male High Jump', 31);

CREATE TABLE competitions (
    athlete_id,
    country_id,
    entourage_edition_number,
    modality_name,
    category_name,
    category_edition_number,
    position INTEGER,
    CONSTRAINT PK_competitions
        PRIMARY KEY (athlete_id, modality_name, category_name, category_edition_number),
    CONSTRAINT FK_competitions_athlete_id
        FOREIGN KEY (athlete_id)
        REFERENCES athletes,
    CONSTRAINT FK_competitions_entourages
        FOREIGN KEY (country_id, entourage_edition_number)
        REFERENCES entourages, --RIA 13
    CONSTRAINT FK_competitions_disputed
        FOREIGN KEY (modality_name, category_name, category_edition_number)
        REFERENCES disputed,
    CONSTRAINT CK_competitions_edition_number
        CHECK (entourage_edition_number = category_edition_number) --RIA 1
);

INSERT INTO competitions
VALUES (1, 2, 30, 'Athletics', 'Male High Jump', 30, 1);
INSERT INTO competitions
VALUES (4, 3, 30, 'Athletics', 'Male High Jump', 30, 2);
INSERT INTO competitions
VALUES (3, 1, 30, 'Athletics', 'Male High Jump', 30, 3);
INSERT INTO competitions
VALUES (3, 1, 30, 'Swimming', 'Male Backstroke', 30, 1);
INSERT INTO competitions
VALUES (2, 2, 30, 'Swimming', 'Female Backstroke', 30, 1);
INSERT INTO competitions
VALUES (2, 3, 31, 'Swimming', 'Female Backstroke', 31, 1);
INSERT INTO competitions
VALUES (4, 3, 31, 'Athletics', 'Male High Jump', 31, 1);
INSERT INTO competitions
VALUES (1, 2, 31, 'Athletics', 'Male High Jump', 31, 2);

--Restrictions not supported: RIA 2, RIA 3, RIA 5, RIA 6, RIA 7, RIA 8, RIA 9, RIA 10, RIA 14, RIA 15
