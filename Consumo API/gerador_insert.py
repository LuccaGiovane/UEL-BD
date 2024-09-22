import json

midia_id = 1

generos = ['Action', 'Adventure', 'Animation', 'Biography', 
    'Comedy', 'Crime', 'Documentary', 'Drama', 
    'Family', 'Fantasy', 'Film-Noir', 'History', 
    'Horror', 'Music', 'Musical', 'Mystery', 'Romance', 
    'Sci-Fi', 'Short', 'Sport', 'Thriller', 'War', 'Western']
idiomas = ['Arabic', 'Cantonese', 'Central Khmer', 
	'Chinese', 'Cornish', 'Dutch', 'English', 
	'French', 'Gaelic', 'German', 'Greek', 
	'Hebrew', 'Hungarian', 'Italian', 'Japanese', 
	'Korean', 'Mandarin', 'Polish', 'Portuguese', 
	'Romanian', 'Russian', 'Spanish', 'Swedish', 'Thai', 
	'Ukrainian', 'Vietnamese', 'Xhosa', 'Zulu']


def array_to_string(arr):
    l = len(arr)
    arr_str = '{'
    for i in range(l-1):
        arr_str+='\"'+arr[i]+'\",'
    if(l>0):
        arr_str+='\"'+arr[l-1]+'\"'
    arr_str+='}'
    return arr_str


def filme_insert(filme:dict):
    if(filme['duracao']==None): return
    global midia_id
    sql = (
            'INSERT INTO marketplace.midia '
            '(titulo,sinopse,avaliacao,poster,atores,dt_lancamento,valor,duracao) '
            'values (\'{}\',\'{}\',{},\'{}\',\'{}\',\'{}\',{},{});'
        )
    fidiomas = filme['idiomas']
    fgeneros = filme['generos']
    
    sql = sql.format(filme['titulo'],filme['sinopse'],filme['avaliacao'],filme['poster'],
               filme['atores'],filme['dt_lancamento'],filme['valor'],filme['duracao'])
    
    print(sql.replace('None','NULL'))
    
    
    for i in fidiomas:
        try: 
            index = idiomas.index(i)
            print(f'INSERT INTO marketplace.idiomas_da_midia values({midia_id},{index+1});')
        except ValueError:
            pass
        
    for g in fgeneros:
        try:
            index = generos.index(g)
            print(f'INSERT INTO marketplace.generos_da_midia values({midia_id},{index+1});')
        except ValueError:
            pass
    midia_id+=1     


def serie_insert(serie:dict):
    if(serie['temporadas']==None): return
    global midia_id
    sql = (
        'INSERT INTO marketplace.midia '
        '(titulo,sinopse,avaliacao,poster,atores,dt_lancamento,valor,temporadas) '
        'values (\'{}\',\'{}\',{},\'{}\',\'{}\',\'{}\',{},{});'
    )
    
    sidiomas = serie['idiomas']
    sgeneros = serie['generos']
    
    sql = sql.format(serie['titulo'],serie['sinopse'],serie['avaliacao'],serie['poster'],
               serie['atores'],serie['dt_lancamento'],serie['valor'],serie['temporadas'])
    
    print(sql.replace('None','NULL'))
    
    
    for i in sidiomas:
        try: 
            index = idiomas.index(i)
            print(f'INSERT INTO marketplace.idiomas_da_midia values({midia_id},{index+1});')
        except ValueError:
            pass
        
    for g in sgeneros:
        try:
            index = generos.index(g)
            print(f'INSERT INTO marketplace.generos_da_midia values({midia_id},{index+1});')
        except ValueError:
            pass
    midia_id+=1     

def genero_insert():
    for g in generos:
        print(f"INSERT INTO marketplace.genero (genero) values (\'{g}\');")

def idioma_insert():
    for i in idiomas:
        print(f"INSERT INTO marketplace.idioma (idioma) values (\'{i}\');")

    

if __name__ == '__main__':
    
    idioma_insert()
    genero_insert()

    with open('filmes.json','r') as f:
        filmes = json.loads(f.read())
        for filme in filmes:
            filme_insert(filme)
    with open('series.json','r') as f:
        series = json.loads(f.read())
        for serie in series:
            serie_insert(serie)
    