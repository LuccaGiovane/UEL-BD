import os
import requests
import json
import collections.abc
import random as rdm
rdm.seed(1)

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

def buscar_midia(nome_midia, chave_api):
    
    url = f"http://www.omdbapi.com/?apikey={chave_api}&t={nome_midia}"
    
    resposta = requests.get(url)
    
    if resposta.status_code == 200:
        
        dados_midia = resposta.json()
        
        if dados_midia['Response'] == 'True':
            return dados_midia
        
        else:
            return f"Mídia não encontrada: {dados_midia['Error']}"
    
    else:
        return f"Erro na requisição: {resposta.status_code}"

def filtrar_dados(dados_midia, tipo_midia):
    
    dados_filtrados = {
        'titulo': dados_midia.get('Title').replace('\'','\'\''),
        'sinopse': dados_midia.get('Plot').replace('\'','\'\''),
        'poster': dados_midia.get('Poster'),
        'generos': (dados_midia.get('Genre')).split(', '),
        'atores': dados_midia.get('Actors').replace('\'','\'\''),
        'dt_lancamento': dados_midia.get('Released'),
        'idiomas': dados_midia.get('Language').split(', ')
    }
    # verifica se ha idiomas que nao serão usados
    for i in dados_filtrados['idiomas']:
        if idiomas.count(i)==0:
            dados_filtrados['idiomas'].remove(i)
            print(f"dado removido {i}")
    
    # verifica se ha generos que nao serão usados
    for i in dados_filtrados['generos']:
        if generos.count(i)==0:
            dados_filtrados['generos'].remove(i)
            print(f"dado removido {i}")
    
    try: # tenta converter a nota
        dados_filtrados['avaliacao'] = float(dados_midia.get('imdbRating'))
        dados_filtrados['valor'] = round(dados_filtrados['avaliacao']*10,2)
    except (ValueError, TypeError): # se não conseguir põe null
        dados_filtrados['avaliacao'] = None
        dados_filtrados['valor'] = round(rdm.uniform(10.0,100.0),2)
    
    for d in dados_filtrados: # verifica se tem algum vetor com N/A colocador
        if isinstance(d,collections.abc.Sequence):
            if(d[0]=='N/A'): # se tiver fica vazio
                d = []

    # verifica tipo
    if tipo_midia == 'filmes':
        try: # tenta converter para minutos para int
            dados_filtrados['duracao'] = int(dados_midia.get('Runtime')[0:-4])
        except (ValueError, TypeError): # se não conseguir põe null
            dados_filtrados['duracao'] = None
    elif tipo_midia == 'series':
        try: # tenta converter o numero de sessoes para int
            dados_filtrados['temporadas'] = int(dados_midia.get('totalSeasons'))
        except (ValueError, TypeError): # se não conseguir põe null
            dados_filtrados['temporadas'] = None

    return dados_filtrados


def ler_titulos_arquivo(nome_arquivo):
    try:
        with open(nome_arquivo, 'r') as arquivo:
            return [linha.strip() for linha in arquivo.readlines()]

    except FileNotFoundError:
        return []

def escrever_json_arquivo(nome_arquivo, dados_midias):
    
    with open(nome_arquivo, 'w') as arquivo:
        arquivo.write('[')
        for midia in dados_midias:
            arquivo.write(json.dumps(midia)+',')
        arquivo.write(']')

if __name__ == "__main__":
    
    # Pega a chave da API pela variavel de ambiente
    chave_api = os.getenv('API_KEY_OMDB')

    if not chave_api:
        print("Erro: A chave da API não foi encontrada. Certifique-se de definir a variável de ambiente 'API_KEY_OMDB'.")
    
    else:
        tipo_midia = input("Deseja consumir [filmes] ou [series]?").strip().lower()

        if tipo_midia not in ['filmes', 'series']:
            print("Opção inválida. Escolha entre 'filmes' ou 'series'.")

        else:
            arquivo_entrada = f"{tipo_midia}.txt"
            arquivo_saida = f"{tipo_midia}.json"

            titulos_midias = ler_titulos_arquivo(arquivo_entrada)
            dados_midias = []

            for titulo in titulos_midias:
                dados_midia = buscar_midia(titulo, chave_api)
                
                if isinstance(dados_midia, dict):  # So pra garantir que e um json valido
                    dados_filtrados = filtrar_dados(dados_midia, tipo_midia)
                    dados_midias.append(dados_filtrados)
                
                else:
                    print(dados_midia)

            escrever_json_arquivo(arquivo_saida, dados_midias)
            print(f"Dados das mídias foram escritos em '{arquivo_saida}'.")