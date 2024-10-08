import pg8000
from faker import Faker
import random

# Conecte-se ao banco de dados principal 'postgres', onde está o schema 'marketplace'
conn = pg8000.connect(
    user="postgres",
    password="147532",
    host="localhost",
    port=5432,
    database="postgres"  # Conectando ao banco de dados principal 'postgres'
)

# Criação do cursor para operações no banco de dados
cursor = conn.cursor()

# Define o schema a ser usado explicitamente
cursor.execute("SET search_path TO marketplace")

# Instância do Faker para gerar dados em português (Brasil)
fake = Faker('pt_BR')

# Quantidade de usuários a serem gerados
num_usuarios = 100

# Gerar dados de usuários fictícios e inserir no banco de dados
for _ in range(num_usuarios):
    nome = fake.name()
    login = fake.unique.user_name()
    senha = fake.password(length=10)
    nasc = fake.date_of_birth(minimum_age=18, maximum_age=80)
    ativo = random.choice([True, False])

    # Comando SQL para inserir os dados gerados na tabela de usuários
    insert_usuario_query = """
    INSERT INTO usuario (nome, login, senha, nasc, ativo)
    VALUES (%s, %s, %s, %s, %s)
    """

    cursor.execute(insert_usuario_query, (nome, login, senha, nasc, ativo))

# Confirma a transação
conn.commit()

# Fechar conexão
cursor.close()
conn.close()

print(f"{num_usuarios} usuários foram inseridos com sucesso!")
