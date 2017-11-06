namespace java Grafo

typedef i32 int

struct Vertice
{
	1: int nome,
	2: int cor,
	3: string desc,
	4: double peso,
	5: optional bool bloqueado = false
}

struct Aresta
{
	1: int vertice1,
	2: int vertice2,
	3: double peso,
	4: bool direc,
	5: string desc,
	6: optional bool bloqueado = false
}

struct ArestaId
{
	1: int nome1,
	2: int nome2
}

struct Grafo
{
	1: map<int,Vertice> vertices = {},
	2: map<ArestaId,Aresta> arestas = {}
}

exception NullException
{
	1: string mensagem
}

service Handler
{
	bool createVertice(1:Vertice v),
	bool createAresta(1:Aresta a),
	Vertice readVertice(1:int nome) throws (1:NullException ne),
	Aresta readAresta(1:int nome1, 2:int nome2) throws (1:NullException ne),
	bool updateVertice(1:Vertice v),
	bool updateAresta(1:Aresta a),
	bool deleteVertice(1:int nome),
	bool deleteAresta(1:int nome1, 2:int nome2),
	list<Vertice> listVerticesDoGrafo(),
	list<Aresta> listArestasDoGrafo(),
	list<Aresta> listArestasDoVertice(1:int nome) throws (1:NullException ne),
	list<Vertice> listVizinhosDoVertice(1:int nome) throws (1:NullException ne),
	list<Vertice> listMenorCaminho(1:int nome1, 2:int nome2) throws (1:NullException ne),
	int getServerId(),
	oneway void setFt(),
	bool bloqueiaVertice(1:int nome),
	oneway void desbloqueiaVertice(1:int nome),
	oneway void deleteArestasDoVertice(1:int nome, 2:int endId),
	list<Vertice> listVerticesDoAnel(1:int endId),
	list<Aresta> listArestasDoAnel(1:int endId)
}
	