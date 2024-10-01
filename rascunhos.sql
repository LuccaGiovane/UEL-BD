select id, titulo, valor, ativo from marketplace.midia where ativo=true;

update marketplace.midia set ativo=false where id = 2;


select id, genero from marketplace.genero;

select * from marketplace.generos_da_midia;
select * from marketplace.idiomas_da_midia;

-- cadastrando usuario
insert into marketplace.usuario (nome,login,senha,nasc) 
	values ('allan','allan','senha','09-09-2003');
select id, nome,ativo from marketplace.usuario;

-- criou a nota fiscal para os itens abaixo
-- o certo seria servir só pra um.
-- nota inclui somente Fight Club 
insert into marketplace.nota_fiscal (usuario_id, valor_total)
	values (1,(select valor from marketplace.midia where id=13 limit 1));
select * from marketplace.nota_fiscal;

-- alugando Fight Club
insert into marketplace.aluguel (usuario_id, midia_id, dt_inicio, dt_expira, valor)
	values (1,13,
		(select dt_pagamento from marketplace.nota_fiscal where usuario_id=1 limit 1),
		(select dt_pagamento + INTERVAL '30 days' from marketplace.nota_fiscal where usuario_id=1 limit 1), -- adicionando 30 dias no aluguel
		(select valor from marketplace.midia where id=13 limit 1));
select * from marketplace.aluguel;

-- comprando Fight Club
insert into marketplace.compra (usuario_id, midia_id, dt_compra, valor)
	values (1,13,
		(select dt_pagamento from marketplace.nota_fiscal where usuario_id=1 limit 1),
		(select valor from marketplace.midia where id=13 limit 1));
select * from marketplace.compra;



	