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
	idioma VARCHAR(24),

	CONSTRAINT pk_idioma PRIMARY KEY(id),
	CONSTRAINT uk_idioma UNIQUE(idioma)
);

CREATE TABLE marketplace.genero(
	id SERIAL,
	genero VARCHAR(24),

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
    dt_lancamento DATE,
    valor DECIMAL(10, 2),
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

CREATE TABLE marketplace.SPRING_SESSION (
  PRIMARY_ID CHAR(36) NOT NULL,
  SESSION_ID CHAR(36) NOT NULL,
  CREATION_TIME BIGINT NOT NULL,
  LAST_ACCESS_TIME BIGINT NOT NULL,
  MAX_INACTIVE_INTERVAL INT NOT NULL,
  EXPIRY_TIME BIGINT NOT NULL,
  PRINCIPAL_NAME VARCHAR(100),
  CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)
);

CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON marketplace.SPRING_SESSION (SESSION_ID);
CREATE INDEX SPRING_SESSION_IX2 ON marketplace.SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX3 ON marketplace.SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE marketplace.SPRING_SESSION_ATTRIBUTES (
  SESSION_PRIMARY_ID CHAR(36) NOT NULL,
  ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
  ATTRIBUTE_BYTES BYTEA NOT NULL,
  CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
  CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) 
  	REFERENCES marketplace.SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE
);
