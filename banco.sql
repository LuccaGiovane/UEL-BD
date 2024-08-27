CREATE SCHEMA marketplace;

CREATE TABLE marketplace.usuario (
    id INT PRIMARY KEY,
    nome VARCHAR(255),
    login VARCHAR(255),
    senha VARCHAR(255),
    nasc DATE
);

CREATE TABLE marketplace.midia (
    id INT PRIMARY KEY,
    nome VARCHAR(255),
    sinopse TEXT,
    avaliacao DECIMAL(3, 2),
    poster VARCHAR(255),
    diretores VARCHAR(255),
    dt_lancamento DATE
);

CREATE TABLE marketplace.filme (
    id INT PRIMARY KEY,
    duracao INT,
    FOREIGN KEY (id) REFERENCES marketplace.midia(id)
);

CREATE TABLE marketplace.serie (
    id INT PRIMARY KEY,
    temporadas INT,
    FOREIGN KEY (id) REFERENCES marketplace.midia(id)
);

CREATE TABLE marketplace.generos (
    midia_id INT,
    genero VARCHAR(255),
    PRIMARY KEY (midia_id, genero),
    FOREIGN KEY (midia_id) REFERENCES marketplace.midia(id)
);

CREATE TABLE marketplace.idiomas (
    midia_id INT,
    idioma VARCHAR(255),
    PRIMARY KEY (midia_id, idioma),
    FOREIGN KEY (midia_id) REFERENCES marketplace.midia(id)
);


CREATE TABLE marketplace.atores (
    midia_id INT,
    ator VARCHAR(255),
    PRIMARY KEY (midia_id, ator),
    FOREIGN KEY (midia_id) REFERENCES marketplace.midia(id)
);


CREATE TABLE marketplace.alugou (
    usuario_id INT,
    midia_id INT,
    dt_inicio DATE,
    dt_expira DATE,
    valor DECIMAL(10, 2),
    PRIMARY KEY (usuario_id, midia_id),
    FOREIGN KEY (usuario_id) REFERENCES marketplace.usuario(id),
    FOREIGN KEY (midia_id) REFERENCES marketplace.midia(id)
);


CREATE TABLE marketplace.comprou (
    usuario_id INT,
    midia_id INT,
    dt_compra DATE,
    valor DECIMAL(10, 2),
    PRIMARY KEY (usuario_id, midia_id),
    FOREIGN KEY (usuario_id) REFERENCES marketplace.usuario(id),
    FOREIGN KEY (midia_id) REFERENCES marketplace.midia(id)
);
