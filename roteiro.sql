-- -------------------- --
-- Cadastro de usuarios --
-- -------------------- --
insert into marketplace.usuario (nome,login,senha,nasc)
values ('Allan','AllanSeidler','senha','9 Sep 2009');

insert into marketplace.usuario (nome,login,senha,nasc)
values ('ADM','Admin','admin','28 Feb 1802');


select nome, login from marketplace.usuario; 

-- -------------------------- --
-- aluguel de filmes e series --
-- -------------------------- --
select id, titulo from marketplace.midia; -- vendo se existe
select id, nome from marketplace.usuario; -- vendo se existe


-- onde ta dando o problema              v

insert into marketplace.midia values (500,'Testando');

select id, titulo from marketplace.midia where id=500;


insert into marketplace.alugou values (1,1,'01-09-2024','01-10-2024');
select * from marketplace.alugou;
SELECT * FROM ONLY marketplace.midia;


-- compra de filmes e series


select * from marketplace.alugou;