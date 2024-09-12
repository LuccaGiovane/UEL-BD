insert into marketplace.midia (titulo,temporadas)
	values ('One Piece',40);

insert into marketplace.genero (genero) values ('Fantasy');
insert into marketplace.genero (genero) values ('Action');
insert into marketplace.genero (genero) values ('Short');

select id, titulo, temporadas from marketplace.midia;
select id, genero from marketplace.genero;



insert into marketplace.generos_da_midia 
	(midia_id,genero_id) values (1,2);
insert into marketplace.generos_da_midia 
	(midia_id,genero_id) values (1,1);

select * from marketplace.generos_da_midia;

SELECT * FROM marketplace.midia WHERE (temporadas!=NULL);