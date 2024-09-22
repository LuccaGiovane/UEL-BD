select id, titulo, ativo from marketplace.midia where ativo=false;
update marketplace.midia set ativo=false where id = 2;


select id, genero from marketplace.genero;

select * from marketplace.generos_da_midia;
select * from marketplace.idiomas_da_midia;

insert into marketplace.usuario (nome,login,senha,nasc) 
	values ('allan','allan','senha','09-09-2003');
select id, nome,ativo from marketplace.usuario;
