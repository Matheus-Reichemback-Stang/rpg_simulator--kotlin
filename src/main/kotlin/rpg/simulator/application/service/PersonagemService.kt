package rpg.simulator.application.service

import org.springframework.stereotype.Service
import rpg.simulator.application.model.Personagem
import rpg.simulator.application.repository.PersonagemRepository

/*
* A Inversão de Controle (IoC - Inversion of Control)
* É o conceito filosófico do Spring. Significa que você, desenvolvedor, deixa de ter o controle de criar e gerenciar
* as instâncias das classes do seu sistema, e passa esse controle para o Spring Boot.O Spring cria uma espécie de "caixa"
* (chamada de Application Context ou Container IoC) onde ele guarda todas as classes que possuem anotações como @Service,
* @Repository e @RestController. Essas classes guardadas pelo Spring são chamadas de Beans.
*
* A Injeção de Dependência (DI - Dependency Injection)
* É a forma prática de como a Inversão de Controle acontece no código.Quando a classe PersonagemService diz no construtor
* que precisa do PersonagemRepository, ela não tenta criar um repositório. Ela simplesmente avisa: "Eu dependo disso para
* funcionar". O Spring olha dentro do seu container, acha o Bean do repositório que ele mesmo criou, e injeta (entrega) essa
* instância pronta dentro do seu Service.
*
* "Inversão de Controle é delegar para o Spring a responsabilidade de gerenciar o ciclo de vida dos objetos. Injeção de
* Dependência é o ato do Spring entregar esses objetos prontos para as classes que precisam deles, eliminando a necessidade
* de usarmos o operador de criação manual."
* */


/*O objetivo do Service é isolar as Regras de Negócio do seu jogo da infraestrutura do banco de dados (Repository)
e da comunicação com a internet (Controller). Se deixarmos toda a lógica de um combate de RPG dentro do Controller,
o código fica gigante e difícil de manter. O Controller deve apenas "receber a requisição e entregar a resposta".
Quem calcula se o personagem tem pontos de magia suficientes, quem executa as funções atacar() e defender(), e quem
decide o vencedor é a camada de Serviço.*/

/* @Service - Avisa ao Spring que esta classe contém as regras de negócio do RPG
* Ela diz ao Component Scan: "Registre esta classe como um componente de serviço nos bastidores".*/
@Service
class PersonagemService (
    // Injeção de Dependência: O Spring automaticamente coloca o Repository aqui para poder usar
    private val personagemRepository: PersonagemRepository
){
    // Regra para Salvar (Atende ao POST)
    fun salvarPeronsagem(personagem: Personagem): Personagem {
        /*.save() - Faz papel duplo no banco de dados e é extremamente inteligente:
        * Se o ID do personagem for zero ou nulo: O Spring entende que é um personagem novo.
        * Ele gera um comando SQL INSERT INTO tb_personagem (...) e o PostgreSQL cria o registro
        * gerando um novo ID automático.
        *
        * Se o ID já existir no banco: O Spring entende que você quer atualizar aquele personagem.
        * Ele gera um comando SQL UPDATE tb_personagem SET ... WHERE id = X. Ele altera os valores
        * daquela linha específica sem criar um registro novo.*/
        return personagemRepository.save(personagem)
    }

    // Regra para Buscar por ID (Atende ao GET específico)
    fun buscarPorId(id: Int): Personagem {
        /*.findById() - Busca apenas uma única linha do banco baseado na Chave Primária. O SQL gerado
        * é SELECT * FROM tb_personagem WHERE id = ?;. Como o ID que você buscou pode não existir no
        banco (por exemplo, buscar o ID 99 em um jogo que só tem 3 personagens), o Spring não te devolve
        * o personagem direto. Ele devolve um objeto chamado Optional. Se o personagem existir, ele está
        * dentro desse Optional; se não existir, o Optional vem vazio (o que te permite usar o .orElseThrow
        * para tratar o erro elegantemente).*/
        return personagemRepository.findById(id)
            .orElseThrow { RuntimeException("Personagem com o ID=$id não foi encontrado!") }
    }

    // Regra para Buscar Todos (Atende ao GET geral)
    fun buscarTodos(personagem: Personagem): List<Personagem> {
        /*.findAll() - Busca todos os registros da tabela. Nos bastidores, o Spring envia o comando
        * SELECT * FROM tb_personagem;. Ele pega todas as linhas que retornaram do PostgreSQL, converte
        * cada linha em um objeto Kotlin correspondente (Guerreiro, Mago ou Ladino) e te entrega tudo
        * organizado dentro de uma List.*/
        return personagemRepository.findAll();
    }

    // Regra para Deletar (Atende ao DELETE)
    fun deletarPersonagem(id: Int) {
        /*.delete() ou .deleteById() - Responsável por remover o registro do banco. Ele gera o comando
         * SQL DELETE FROM tb_personagem WHERE id = ?;. A linha correspondente àquele personagem deixa
         * de existir no seu PostgreSQL instantaneamente.*/
        val personagem = personagemRepository.findById(id)
        personagemRepository.deleteById(id)
    }



}