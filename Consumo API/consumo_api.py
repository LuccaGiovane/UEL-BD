import os
import requests
import json

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
        'nome': dados_midia.get('Title'),
        'sinopse': dados_midia.get('Plot'),
        'avaliacao': dados_midia.get('imdbRating'),
        'poster': dados_midia.get('Poster'),
        'genero': dados_midia.get('Genre'),
        'diretor': dados_midia.get('Director'),
        'dt_lancamento': dados_midia.get('Released'),
    }

    if tipo_midia == 'filmes':
        dados_filtrados['duracao'] = dados_midia.get('Runtime')

    elif tipo_midia == 'series':
        dados_filtrados['temporadas'] = dados_midia.get('totalSeasons')

    return dados_filtrados

def ler_titulos_arquivo(nome_arquivo):
    
    try:
        with open(nome_arquivo, 'r') as arquivo:
            return [linha.strip() for linha in arquivo.readlines()]

    except FileNotFoundError:
        return []

def escrever_json_arquivo(nome_arquivo, dados_midias):
    
    with open(nome_arquivo, 'w') as arquivo:
        for midia in dados_midias:
            arquivo.write(json.dumps(midia, indent=4))
            arquivo.write('\n')

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
            arquivo_saida = f"{tipo_midia}_json.txt"

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