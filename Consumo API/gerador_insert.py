import json

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
    sql = (
            'INSERT INTO marketplace.filme '
            '(titulo,sinopse,idiomas,generos,avaliacao,poster,atores,dt_lancamento,valor,duracao) '
            'values (\'{}\',\'{}\',\'{}\',\'{}\',{},\'{}\',\'{}\',\'{}\',{},{});'
        )
    idiomas = array_to_string(filme['idiomas'])
    generos = array_to_string(filme['generos'])
    
    sql = sql.format(filme['titulo'],filme['sinopse'],idiomas,generos,filme['avaliacao'],filme['poster'],
               filme['atores'],filme['dt_lancamento'],filme['valor'],filme['duracao'])
    
    print(sql.replace('None','NULL'))

def serie_insert(serie:dict):
    sql = (
        'INSERT INTO marketplace.serie '
        '(titulo,sinopse,idiomas,generos,avaliacao,poster,atores,dt_lancamento,valor,temporadas) '
        'values (\'{}\',\'{}\',\'{}\',\'{}\',{},\'{}\',\'{}\',\'{}\',{},{});'
    )
    idiomas = array_to_string(serie['idiomas'])
    generos = array_to_string(serie['generos'])
    
    sql = sql.format(serie['titulo'],serie['sinopse'],idiomas,generos,serie['avaliacao'],serie['poster'],
               serie['atores'],serie['dt_lancamento'],serie['valor'],serie['temporadas'])
    print(sql.replace('None','NULL'))


if __name__ == '__main__':
    with open('filmes.json','r') as f:
        filmes = json.loads(f.read())
        for filme in filmes:
            filme_insert(filme)
    with open('series.json','r') as f:
        series = json.loads(f.read())
        for serie in series:
            serie_insert(serie)
    