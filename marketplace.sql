drop schema marketplace cascade;

CREATE SCHEMA marketplace;

CREATE TABLE marketplace.usuario (
    id SERIAL,
    nome VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    nasc DATE NOT NULL,
	ativo BOOLEAN DEFAULT TRUE,
	saldo DECIMAL(10,2),

    CONSTRAINT pk_usuario PRIMARY KEY(id),
    CONSTRAINT uk_usuario_login UNIQUE(login),
	
	CONSTRAINT ck_saldo CHECK (saldo>=0.00)
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
    valor DECIMAL(10, 2) NOT NULL,
	duracao INT, -- Exclusivo de filme
	temporadas INT, -- Exclusivo de serie
	ativo BOOLEAN DEFAULT TRUE,

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

CREATE TABLE marketplace.nota_fiscal(
	usuario_id INT,
	valor_total DECIMAL(10, 2) NOT NULL,
	dt_pagamento TIMESTAMP DEFAULT(NOW()),

	CONSTRAINT pk_nota_fiscal PRIMARY KEY (usuario_id, dt_pagamento),
	CONSTRAINT fk_usuario_possui FOREIGN KEY(usuario_id)
		REFERENCES marketplace.usuario(id)
);

CREATE TABLE marketplace.aluguel (
    usuario_id INT,
    midia_id INT,
	dt_inicio TIMESTAMP,
    dt_expira TIMESTAMP NOT NULL, --DEFAULT(dt_inicio + INTERVAL '30 days') NOT NULL, 
	valor DECIMAL(10, 2) NOT NULL,
    
    CONSTRAINT pk_aluguel PRIMARY KEY (usuario_id, midia_id, dt_inicio),
    CONSTRAINT fk_usuario_alugou FOREIGN KEY (usuario_id, dt_inicio)
		REFERENCES marketplace.nota_fiscal(usuario_id, dt_pagamento),
    CONSTRAINT fk_midia_foi_alugada FOREIGN KEY (midia_id)
		REFERENCES marketplace.midia(id),
    CONSTRAINT ck_dt_aluguel CHECK(dt_inicio < dt_expira),
	CONSTRAINT ck_valor_compra_positivo CHECK (valor>0.00)
);

CREATE TABLE marketplace.compra (
    usuario_id INT,
    midia_id INT,
	dt_compra TIMESTAMP,
	valor DECIMAL(10, 2) NOT NULL,

    CONSTRAINT pk_comprou PRIMARY KEY (usuario_id, midia_id),
    CONSTRAINT fk_usuario_comprou FOREIGN KEY (usuario_id, dt_compra)
		REFERENCES marketplace.nota_fiscal(usuario_id,dt_pagamento),
    CONSTRAINT fk_midia_foi_comprada FOREIGN KEY (midia_id) 
		REFERENCES marketplace.midia(id),
	CONSTRAINT ck_valor_compra_positivo CHECK (valor>0.00)
);
