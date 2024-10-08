select id, titulo, valor, ativo from marketplace.midia where ativo=true;

update marketplace.midia set ativo=false where id = 2;


select id, genero from marketplace.genero;

select * from marketplace.generos_da_midia;
select * from marketplace.idiomas_da_midia;

-- cadastrando usuario
insert into marketplace.usuario (nome,login,senha,nasc) 
	values ('eduardo','eduardo','saenha','09-09-2003');
select * from marketplace.usuario;

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


SELECT * FROM marketplace.aluguel order by midia_id;
SELECT * FROM marketplace.compra order by midia_id;
SELECT * FROM marketplace.nota_fiscal;

-- Relatorios 1 e 2
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
	count(case when date('2024-12-01') <= dt_pagamento and dt_pagamento < (date('2024-12-01')+interval '1 month') then 1
		else null end) as dezembro
from (marketplace.nota_fiscal nf join marketplace.compra c on c.usuario_id=nf.usuario_id and c.dt_compra=dt_pagamento);

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
from (marketplace.nota_fiscal nf join marketplace.aluguel a on a.usuario_id=nf.usuario_id and a.dt_inicio=dt_pagamento);


-- Relatorio 3 e 4
select count(1) as total, m.titulo, m.id from marketplace.nota_fiscal nf 
	join marketplace.aluguel a on a.usuario_id=nf.usuario_id and a.dt_inicio=dt_pagamento
	join marketplace.midia m on m.id=a.midia_id
	group by (m.id) order by total desc limit 10;

select count(1) as total,  m.titulo, m.id from marketplace.nota_fiscal nf 
	join marketplace.compra c on c.usuario_id=nf.usuario_id and c.dt_compra=dt_pagamento
	join marketplace.midia m on m.id=c.midia_id
	group by (m.id) order by total desc limit 10;


-- Relatorio 5
select 
	sum(case when date('2024-01-01') <= dt_pagamento and dt_pagamento < (date('2024-01-01')+interval '1 month') then nf.valor_total
		else 0 end) as janeiro,
	sum(case when date('2024-02-01') <= dt_pagamento and dt_pagamento < (date('2024-02-01')+interval '1 month') then nf.valor_total
		else 0 end) as fevereiro,
	sum(case when date('2024-03-01') <= dt_pagamento and dt_pagamento < (date('2024-03-01')+interval '1 month') then nf.valor_total
		else 0 end) as marco,
	sum(case when date('2024-04-01') <= dt_pagamento and dt_pagamento < (date('2024-04-01')+interval '1 month') then nf.valor_total
		else 0 end) as abril,
	sum(case when date('2024-05-01') <= dt_pagamento and dt_pagamento < (date('2024-05-01')+interval '1 month') then nf.valor_total
		else 0 end) as maio,
	sum(case when date('2024-06-01') <= dt_pagamento and dt_pagamento < (date('2024-06-01')+interval '1 month') then nf.valor_total
		else 0 end) as junho,
	sum(case when date('2024-07-01') <= dt_pagamento and dt_pagamento < (date('2024-07-01')+interval '1 month') then nf.valor_total
		else 0 end) as julho,
	sum(case when date('2024-08-01') <= dt_pagamento and dt_pagamento < (date('2024-08-01')+interval '1 month') then nf.valor_total
		else 0 end) as agosto,
	sum(case when date('2024-09-01') <= dt_pagamento and dt_pagamento < (date('2024-09-01')+interval '1 month') then nf.valor_total
		else 0 end) as setembro,
	sum(case when date('2024-10-01') <= dt_pagamento and dt_pagamento < (date('2024-10-01')+interval '1 month') then nf.valor_total
		else 0 end) as outubro,
	sum(case when date('2024-11-01') <= dt_pagamento and dt_pagamento < (date('2024-11-01')+interval '1 month') then nf.valor_total
		else 0 end) as novembro,
	sum(case when date('2024-12-01') <= dt_pagamento and dt_pagamento < (date('2024-12-01')+interval '1 month') then nf.valor_total
		else 0 end) as dezembro
from marketplace.nota_fiscal nf;



