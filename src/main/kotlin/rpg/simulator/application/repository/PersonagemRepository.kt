package rpg.simulator.application.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import rpg.simulator.application.model.Personagem

/* @Repository - Avisa ao Spring que esta interface gerencia o acesso aos dados
* É uma anotação de estereótipo do Spring. Ela serve para o @ComponentScan encontrar
* essa interface e transformá-la em um componente gerenciado pelo Spring (um Bean).
* Além disso, ela ativa mecanismos automáticos de tradução de erros de banco de dados
* para exceções legíveis do Spring.
* */
@Repository
/*
interface PersonagemRepository - Definimos como interface porque nós não queremos programar
as funções de salvar, deletar ou buscar. Nós queremos que o Spring faça isso por nós.

No Spring Data JPA, o Repository não é uma classe, mas sim uma Interface.
Você cria uma interface Kotlin e faz ela herdar (estender) uma interface pronta do Spring
chamada JpaRepository. Quando você faz isso, o Spring Boot cria automaticamente toda a
implementação pesada nos bastidores em tempo de execução.

Dentro do operador diamante ("<>") do JpaRepository, dois parametros:
Personagem - Diz ao Spring qual é a classe @Entity que este repositório
vai gerenciar (a nossa tabela).

Int - Diz ao Spring qual é o tipo de dado da Chave Primária (@Id) daquela
entidade. Como a classe Personagem o id é um Int, passamos Int aqui.
* */
interface PersonagemRepository : JpaRepository<Personagem, Int> {}

/*
* Com a herança do JpaRepository você terá métodos prontos para um CRUD:
*
* save(personagem) -> Executa o POST (salvar) e o PUT (editar).
* findById(id) -> Executa o GET para buscar um personagem específico pelo ID.
* findAll() -> Executa o GET para buscar todos os personagens do jogo.
* deleteById(id) -> Executa o DELETE para excluir do banco.
* */