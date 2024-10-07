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
	values (1,(select valor from marketplace.midia where id=10 limit 1));
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
	values (1,11,
		(select dt_pagamento from marketplace.nota_fiscal where usuario_id=1 limit 1),
		(select valor from marketplace.midia where id=11 limit 1));
select * from marketplace.compra;




select m.id,m.titulo from marketplace.aluguel c join marketplace.midia m on c.midia_id=m.id;

-- pode comprar
select midia.id as id, midia.valor as valor from marketplace.midia midia where not ( midia.id in (
	select m.id from marketplace.compra c join marketplace.midia m on c.midia_id=m.id 
		where usuario_id = 1 and midia_id in (25,2,1,12,13)))
	and midia.id in (25,2,1,12,13);

-- pode alugar
-- qualquer midia que não esteja em compra


-- total nota fiscal
select SUM(midia.valor) as total from marketplace.midia midia where not ( midia.id in (
	select m.id from marketplace.compra c 
	join marketplace.midia m on c.midia_id=m.id
		where usuario_id = 1 and midia_id in (25,2,1,1,12,13)))
	and midia.id in (25,2,1,1,12,13);


SELECT * FROM marketplace.aluguel;
SELECT * FROM marketplace.compra;

-- Relatorios
select 
	count(case when date('2024-01-01') <= dt_pagamento and dt_pagamento < (date('2024-01-01')+interval '1 month') then 1
		else null end) as janeiro,
	count(case when date('2024-02-01') <= dt_pagamento and dt_pagamento < (date('2024-02-01')+interval '1 month') then 1
		else null end) as fevereiro,
	count(case when date('2024-03-01') <= dt_pagamento and dt_pagamento < (date('2024-03-01')+interval '1 month') then 1
		else null end) as marco,
	count(case when date('2024-04-01') <= dt_pagamento and dt_pagamento < (date('2024-04-01')+interval '1 month') then 1
		else null end) as abril,
	count(case when date('2024-05-01') <= dt_pagamento and dt_pagamento < (date('2024-05-01')+interval '1 month') then 1
		else null end) as maio,
	count(case when date('2024-06-01') <= dt_pagamento and dt_pagamento < (date('2024-06-01')+interval '1 month') then 1
		else null end) as junho,
	count(case when date('2024-07-01') <= dt_pagamento and dt_pagamento < (date('2024-07-01')+interval '1 month') then 1
		else null end) as julho,
	count(case when date('2024-08-01') <= dt_pagamento and dt_pagamento < (date('2024-08-01')+interval '1 month') then 1
		else null end) as agosto,
	count(case when date('2024-09-01') <= dt_pagamento and dt_pagamento < (date('2024-09-01')+interval '1 month') then 1
		else null end) as setembro,
	count(case when date('2024-10-01') <= dt_pagamento and dt_pagamento < (date('2024-10-01')+interval '1 month') then 1
		else null end) as outubro,
	count(case when date('2024-11-01') <= dt_pagamento and dt_pagamento < (date('2024-11-01')+interval '1 month') then 1
		else null end) as novembro,
	count(case when date('2024-12-01') <= dt_pagamento and dt_pagamento < (date('2024-11-01')+interval '1 month') then 1
		else null end) as dezembro
		
from (marketplace.nota_fiscal nf join marketplace.compra c on c.usuario_id=nf.usuario_id and c.dt_compra=dt_pagamento);


select date('2024-12-01') + interval '1 month';

