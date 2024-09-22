-- drop schema marketplace cascade;

CREATE SCHEMA marketplace;

CREATE TABLE marketplace.usuario (
    id SERIAL,
    nome VARCHAR(255),
    login VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    nasc DATE,

    CONSTRAINT pk_usuario PRIMARY KEY(id),
    CONSTRAINT uk_usuario_login UNIQUE(login)
);

CREATE TABLE marketplace.idioma(
	id SERIAL,
	idioma VARCHAR(24) NOT NULL,

	CONSTRAINT pk_idioma PRIMARY KEY(id),
	CONSTRAINT uk_idioma UNIQUE(idioma)
);

CREATE TABLE marketplace.genero(
	id SERIAL,
	genero VARCHAR(24) NOT NULL,

	CONSTRAINT pk_genero PRIMARY KEY(id),
	CONSTRAINT uk_genero UNIQUE(genero)
);

CREATE TABLE marketplace.midia (
    id SERIAL,
    titulo VARCHAR(255) NOT NULL,
    sinopse TEXT,
    avaliacao DECIMAL(3, 2),
    poster VARCHAR(255),
    atores VARCHAR(255),
    dt_lancamento DATE NOT NULL,
<<<<<<< Updated upstream
    valor DECIMAL(10, 2),
=======
    valor DECIMAL(10, 2) NOT NULL,
>>>>>>> Stashed changes
	duracao INT, -- Exclusivo de filme
	temporadas INT, -- Exclusivo de serie

    CONSTRAINT pk_midia PRIMARY KEY(id),
	CONSTRAINT ck_filme_ou_serie CHECK(
		((duracao IS NULL) AND (temporadas IS NOT NULL)) OR
		((duracao IS NOT NULL) AND (temporadas IS NULL))
	)
);

CREATE TABLE marketplace.idiomas_da_midia(
	midia_id INT,
	idioma_id INT,

	CONSTRAINT pk_idiomas_da_midia PRIMARY KEY(midia_id,idioma_id),
	CONSTRAINT fk_midia_tem_idioma FOREIGN KEY(midia_id)
		REFERENCES marketplace.midia(id),
	CONSTRAINT fk_idioma_da_midia FOREIGN KEY(idioma_id)
		REFERENCES 	marketplace.idioma(id)
);

CREATE TABLE marketplace.generos_da_midia(
	midia_id INT,
	genero_id INT,

	CONSTRAINT pk_generos_da_midia PRIMARY KEY(midia_id,genero_id),
	CONSTRAINT fk_midia_tem_genero FOREIGN KEY(midia_id)
		REFERENCES marketplace.midia(id),
	CONSTRAINT fk_genero_da_midia FOREIGN KEY(genero_id)
		REFERENCES 	marketplace.genero(id)
);

CREATE TABLE marketplace.alugou (
    usuario_id INT,
    midia_id INT,
    dt_inicio DATE NOT NULL,
    dt_expira DATE NOT NULL,
    
    CONSTRAINT pk_alugou PRIMARY KEY (usuario_id, midia_id, dt_inicio),
    CONSTRAINT fk_usuario_alugou FOREIGN KEY (usuario_id) 
		REFERENCES marketplace.usuario(id),
    CONSTRAINT fk_midia_foi_alugada FOREIGN KEY (midia_id) 
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

