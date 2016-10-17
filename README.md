# O Projeto
Para a proposta lançada foi desenvolvido um sistema para lançar pedidos, cada pedido é composto por um cliente e por um ou vários produtos.
Antes de lançar um pedido é necessário cadastrar um cliente e um produto no meu **cadastro**.

# Back-End 

Para a implementação do Back-end foi escolhido a *Opção 1*

* Aplicação JavaEE utilizando framework Google App Engine para Java
* Maven para controle de dependências
* JAX-RS para servir a API RESTful
* Biblioteca Jackson para fazer o parse dos objetos JSON
* Banco de dados Google Cloud Storage


## REST

Os dados estão disponiveis via REST de acordo com o recurso (Cliente, Produto, Pedido) sendo que para cada um deles é possível
listar, adicionar, editar e excluir. Cada requisição HTTP retorna um STATUS_CODE e um JSON qnd necessário.


# Front-End


Para o Front-End foi utilizado o angularjs 1.x, para a criação do layout foi utilizado o bootstrap.

As chamadas http e o controle visual do front estão separadas em cada controller.

Resolvi deixar os arquivos estaticos junto com a aplicação GAE, assim quando fazer o deploy para o cloud fica tudo ok.


# Acesso ao projeto

* Acessar a aplicação com pela url **https://contabilizei-pedidos.appspot.com**














