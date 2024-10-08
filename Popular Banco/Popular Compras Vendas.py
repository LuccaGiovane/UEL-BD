import pg8000
from faker import Faker
from datetime import datetime, timedelta
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

# Função para gerar uma data aleatória entre 2022 e 2024
def random_date(start, end):
    return start + timedelta(days=random.randint(0, (end - start).days))

# Buscar os IDs das mídias existentes
cursor.execute("SELECT id FROM midia WHERE ativo = TRUE")
midia_ids = [row[0] for row in cursor.fetchall()]

# Buscar os IDs dos usuários que foram gerados anteriormente
cursor.execute("SELECT id FROM usuario WHERE ativo = TRUE")
usuario_ids = [row[0] for row in cursor.fetchall()]

# Configurações
num_compras = 50  # Quantidade de compras a serem geradas
num_alugueis = 50  # Quantidade de aluguéis a serem gerados
start_date = datetime(2022, 1, 1)
end_date = datetime(2024, 12, 31)

# Função para criar uma nota fiscal
def criar_nota_fiscal(usuario_id, valor_total, dt_pagamento):
    insert_nota_fiscal_query = """
    INSERT INTO nota_fiscal (usuario_id, valor_total, dt_pagamento)
    VALUES (%s, %s, %s)
    """
    cursor.execute(insert_nota_fiscal_query, (usuario_id, valor_total, dt_pagamento))

# Função para verificar se o usuário já comprou a mídia
def usuario_ja_comprou(usuario_id, midia_id):
    cursor.execute("""
    SELECT 1 FROM compra WHERE usuario_id = %s AND midia_id = %s
    """, (usuario_id, midia_id))
    return cursor.fetchone() is not None

# Função para verificar se o usuário já alugou a mídia
def usuario_ja_alugou(usuario_id, midia_id):
    cursor.execute("""
    SELECT 1 FROM aluguel WHERE usuario_id = %s AND midia_id = %s AND dt_expira > NOW()
    """, (usuario_id, midia_id))
    return cursor.fetchone() is not None

# Gerar compras para os usuários
for _ in range(num_compras):
    usuario_id = random.choice(usuario_ids)  # Escolhe um usuário aleatório
    midia_id = random.choice(midia_ids)  # Escolhe uma mídia aleatória
    dt_compra = random_date(start_date, end_date)  # Gera uma data aleatória entre 2022 e 2024
    valor = round(random.uniform(5.0, 50.0), 2)  # Valor da compra aleatório entre 5 e 50 reais

    # Verifica se o usuário já comprou a mídia
    if not usuario_ja_comprou(usuario_id, midia_id):
        # Criar uma nota fiscal antes de inserir a compra
        criar_nota_fiscal(usuario_id, valor, dt_compra)

        # Inserir a compra
        insert_compra_query = """
        INSERT INTO compra (usuario_id, midia_id, dt_compra, valor)
        VALUES (%s, %s, %s, %s)
        """
        cursor.execute(insert_compra_query, (usuario_id, midia_id, dt_compra, valor))
    else:
        print(f"Usuário {usuario_id} já comprou a mídia {midia_id}, pulando essa compra.")

# Gerar aluguéis para os usuários
for _ in range(num_alugueis):
    usuario_id = random.choice(usuario_ids)  # Escolhe um usuário aleatório
    midia_id = random.choice(midia_ids)  # Escolhe uma mídia aleatória
    dt_inicio = random_date(start_date, end_date)  # Gera uma data de início aleatória
    dt_expira = dt_inicio + timedelta(days=30)  # Expiração 30 dias após o início
    valor = round(random.uniform(3.0, 30.0), 2)  # Valor do aluguel aleatório entre 3 e 30 reais

    # Verifica se o usuário já alugou a mídia e ainda está no prazo
    if not usuario_ja_alugou(usuario_id, midia_id):
        # Criar uma nota fiscal antes de inserir o aluguel
        criar_nota_fiscal(usuario_id, valor, dt_inicio)

        # Inserir o aluguel
        insert_aluguel_query = """
        INSERT INTO aluguel (usuario_id, midia_id, dt_inicio, dt_expira, valor)
        VALUES (%s, %s, %s, %s, %s)
        """
        cursor.execute(insert_aluguel_query, (usuario_id, midia_id, dt_inicio, dt_expira, valor))
    else:
        print(f"Usuário {usuario_id} já alugou a mídia {midia_id} e ainda está no prazo, pulando esse aluguel.")

# Confirma a transação
conn.commit()

# Fechar conexão
cursor.close()
conn.close()

print(f"{num_compras} compras e {num_alugueis} aluguéis foram inseridos com sucesso!")
