-- -------------------- --
-- Cadastro de usuarios --
-- -------------------- --

-- cadastrando usuarios
insert into marketplace.usuario (nome,login,senha,nasc)
values ('Jacques','Jacques D. B.','senha123','9 Sep 2009');

insert into marketplace.usuario (nome,login,senha,nasc)
values ('Wesley A.','CEO de Compilers','senha666','06 Jun 1666');

-- vendo se os usuarios foram cadastrados
select nome, login from marketplace.usuario; 


-- -------------------------- --
-- aluguel de filmes e series --
-- -------------------------- --

-- mostrando o catalogo de filmes para nossos usuarios comprarem/alugarem
select * from marketplace.midia;

-- jacques decidiu alugar um filme 
insert into marketplace.alugou values (1,1,'01-09-2024','01-10-2024');
select * from marketplace.alugou;

-- wesley ficou sabendo que o filme e bom e resolveu alugar o mesmo filme
insert into marketplace.alugou values (2,1,'01-09-2024','01-10-2024');
select * from marketplace.alugou;

-- ele gostou mesmo da loja e resolveu alugar the office tambem
insert into marketplace.alugou values (2,157,'02-09-2024','02-10-2024');
select * from marketplace.alugou;

-- apos 1 mes jacques decidiu alugar novamente matrix
insert into marketplace.alugou values (1,1,'01-10-2024','01-11-2024');
select * from marketplace.alugou;


-- -------------------------- --
-- compra de filmes e series --
-- -------------------------- --
select * from marketplace.comprou;

-- o wesley decidiu comprar the office após testar o aluguel pois gostou muito 
insert into marketplace.comprou values (2, 154, '02-09-2024');

-- jacques aproveitou o preço camarada e comprou la la land
insert into marketplace.comprou values (2, 80, '02-09-2024');

select * from marketplace.comprou;

-- ------------------------------- --
-- alterando e atualizando valores --
-- ------------------------------- --
SELECT unnest(enum_range(NULL::marketplace.idioma));

-- a pedidos de Chewbacca (aahh uuhhhh ahhhh) adicionamos seu idioma no 
-- site para ele assim finalmente poder assistir Star Wars em sua lingua materna
ALTER TYPE marketplace.idioma ADD VALUE 'Shyriiwook';

-- sabendo disso moradores da antiga Valiria tambem exigiram ver Game of Thrones
-- dublado
ALTER TYPE marketplace.idioma ADD VALUE 'Alto Valiriano';

-- assim como foi pedido, os idiomas se encontram disponiveis em nossa plataforma
SELECT unnest(enum_range(NULL::marketplace.idioma));

-- mostrando o catalogo de filmes antes da modificação
SELECT id, titulo, idiomas FROM marketplace.midia WHERE id=12 OR id=101;

-- atualizando Star Wars para o Chewe conseguir assistir sem legendas
UPDATE marketplace.midia
	SET idiomas = array_append(midia.idiomas, 'Shyriiwook')
WHERE id = 12;

-- assim como os moradores de Valiria finalmente verem sua serie favorita
UPDATE marketplace.midia
	SET idiomas = array_append(midia.idiomas, 'Alto Valiriano')
WHERE id = 101;

-- finalmente nossos amigos poderao ver suas midias \o/
SELECT id, titulo, idiomas FROM marketplace.midia WHERE id=12 OR id=101;