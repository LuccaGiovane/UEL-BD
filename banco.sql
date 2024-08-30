CREATE SCHEMA marketplace;

CREATE TABLE marketplace.usuario (
    id INT,
    nome VARCHAR(255),
    login VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    nasc DATE,

    CONSTRAINT pk_usuario PRIMARY KEY(id),
    CONSTRAINT uk_usuario_login UNIQUE(login)
);

-- novo tipo 'genero' para os generos dos filmes/series
CREATE TYPE GENERO AS ENUM (
	'Action', 'Adventure', 'Animation', 'Biography', 
	'Comedy', 'Crime', 'Documentary', 'Drama', 
	'Family', 'Fantasy', 'Film-Noir', 'History', 
	'Horror', 'Music', 'Musical', 'Mystery', 'Romance', 
	'Sci-Fi', 'Short', 'Sport', 'Thriller', 'War', 'Western');

-- novo tipo 'idioma' para os idiomas dos filmes/series
CREATE TYPE IDIOMA AS ENUM (
	'Arabic', 'Cantonese', 'Central Khmer', 
	'Chinese', 'Cornish', 'Dutch', 'English', 
	'French', 'Gaelic', 'German', 'Greek', 
	'Hebrew', 'Hungarian', 'Italian', 'Japanese', 
	'Korean', 'Mandarin', 'Polish', 'Portuguese', 
	'Romanian', 'Russian', 'Spanish', 'Swedish', 'Thai', 
	'Ukrainian', 'Vietnamese', 'Xhosa', 'Zulu');

CREATE TABLE marketplace.midia (
    id INT,
    titulo VARCHAR(255) NOT NULL,
    sinopse TEXT,
	idiomas IDIOMA[],
	generos GENERO[],
    avaliacao DECIMAL(3, 2),
    poster VARCHAR(255),
    atores VARCHAR(255),
    dt_lancamento DATE,
    valor DECIMAL(10, 2),

    CONSTRAINT pk_midia PRIMARY KEY(id)
);

CREATE TABLE marketplace.filme (
    id INT,
    duracao INT, -- Duração em minutos inteiros.

    CONSTRAINT pk_filme PRIMARY KEY(id),
    CONSTRAINT fk_filme_eh_midia FOREIGN KEY(id) 
		REFERENCES marketplace.midia(id) ON DELETE CASCADE
);

CREATE TABLE marketplace.serie (
    id INT,
    temporadas INT,
    
    CONSTRAINT pk_serie PRIMARY KEY(id),
    CONSTRAINT fk_serie_eh_midia FOREIGN KEY(id) 
		REFERENCES marketplace.midia(id) ON DELETE CASCADE
);

CREATE TABLE marketplace.alugou (
    usuario_id INT,
    midia_id INT,
    dt_inicio DATE NOT NULL,
    dt_expira DATE NOT NULL,
    
    CONSTRAINT pk_alugou PRIMARY KEY (usuario_id, midia_id),
    CONSTRAINT pk_usuario_alugou FOREIGN KEY (usuario_id) 
		REFERENCES marketplace.usuario(id),
    CONSTRAINT pk_midia_foi_alugada FOREIGN KEY (midia_id) 
		REFERENCES marketplace.midia(id),
    CONSTRAINT ck_dt_aluguel CHECK(dt_inicio < dt_expira)
);

CREATE TABLE marketplace.comprou (
    usuario_id INT,
    midia_id INT,
    dt_compra DATE NOT NULL,

    CONSTRAINT pk_comprou PRIMARY KEY (usuario_id, midia_id),
    CONSTRAINT fk_usuario_comprou FOREIGN KEY (usuario_id) 
		REFERENCES marketplace.usuario(id),
    CONSTRAINT fk_midia_foi_comprada FOREIGN KEY (midia_id) 
		REFERENCES marketplace.midia(id)
);
