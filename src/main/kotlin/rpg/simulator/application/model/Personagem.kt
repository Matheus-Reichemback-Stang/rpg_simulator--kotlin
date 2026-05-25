package rpg.simulator.application.model

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table
import kotlin.random.Random

/*
* EXPLICAÇÃO JPA (Jakarta Persistence API)
* As anotações do JPA servem para fazer a Ponte entre o mundo do Kotlin (Orientado a Objetos)
* e o mundo do PostgreSQL (Banco de Dados Relacional).
*
* Esse processo se chama ORM (Mapeamento Objeto-Relacional). Basicamente, você usa as anotações
* para dizer ao Spring: "Esta classe Kotlin vai virar uma tabela no banco, e essas variáveis vão virar colunas".
* */


// @Entity - Diz ao Spring e ao banco de dados que essa classe Kotlin é uma Entidade (uma tabela).
@Entity
/* @Table - serve para customizar as propriedades dessa tabela no banco de dados. Ela é opcional
e fica logo abaixo ou acima do @Entity. */
@Table(name = "tb_personagem")
/* @Inheritance - Vai na classe Pai. Ela avisa ao JPA que essa classe tem filhos e que todos devem ser jogados dentro da mesma tabela.
* InheritanceType.SINGLE_TABLE - O banco de dados cria uma única tabela contendo todas as colunas comuns (id, nome,
* força, velocidade, vida) E junta todas as colunas específicas de cada classe filha*/
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
/* @DiscriminatorColumn - Vai na classe Pai. Ela cria e dá um nome para a coluna que o banco vai usar para diferenciar
qual é a classe do personagem salvo (Guerreiro, Mago, etc.).*/
@DiscriminatorColumn(name = "tipo_personagem", length = 255, discriminatorType = DiscriminatorType.STRING)
/* @DiscriminatorValue - Vai nas classes Filhas e na classe Pai. Ela define qual o texto exato que o JPA vai escrever na
coluna discriminadora quando salvar aquele tipo específico.*/
@DiscriminatorValue("PersonagemBase")
open class Personagem (
    // @Id - Define qual campo da sua classe será a Chave Primária (Primary Key) da tabela.
    @Id
    /* @GeneratedValue - Diz ao banco de dados para gerar o ID automaticamente (1, 2, 3, 4...).
     * GenerationType.IDENTITY - Delega a responsabilidade de gerar o valor do ID totalmente para o banco de dados.
     * GenerationType.SEQUENCE - O banco de dados utiliza uma estrutura separada chamada "Sequence" (uma tabela contadora) para gerar os números.
     * GenerationType.TABLE - Cria uma tabela normal no banco de dados apenas para guardar o último ID gerado.
     * GenerationType.AUTO - Pede para o Spring Boot aplicar o que ele achar melhor */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0, // Definir = 0 ajuda o Spring a saber que é um novo registro ao salvar
    /* @Column - É usada em cima das variáveis para customizar a coluna no banco de dados. Não é obrigatório sua aplicação, pois
    * o próprio Spring já transforma suas variáveis em colunas no banco de dados, contudo, caso você queira colocar regras mais
    * restritas na suas colunas da tabela, você vai ser obrigado a usar ela. Exemplos de Parâmetros (Regras):
    * name = "nome_coluna" - Altera o nome da coluna no banco.
    * nullable = false - Define se o campo aceita valores nulos (true) ou se é obrigatório (false).
    * unique = true - Garante que nenhum outro registro no banco tenha o mesmo valor.
    * length = 500 - Define o tamanho máximo de caracteres para campos de texto (String). O padrão do JPA é criar colunas varchar(255)
    * updatable = true - Se definido como false, o valor desse campo nunca mais poderá ser modificado após o primeiro salvamento.
    * */
    @Column (nullable = false, length = 100)
    var nome: String,
    @Column (nullable = false)
    var forca: Int,
    @Column (nullable = false)
    var velocidade: Int,
    @Column (nullable = false)
    var vida: Int
) {

    /*
    *  Faz um ATAQUE usando a força do personagem e tendo a chance
    * de adicionar um bônus de forca caso a variável numeroSortido
    * seja igual a variável numeroDoBonus que também é aleatória
    * */
    fun atacar(): Int {
        val numeroDoBonus: Int = Random.nextInt(1, 21)
        val numeroSortido: Int = Random.nextInt(1, 21)

        if(numeroSortido == numeroDoBonus) {
            val bonusForca: Int = Random.nextInt(5, 31)
            val forcaTotal = forca + bonusForca;
            return forcaTotal
        } else {
            return forca
        }

    }

    /*
    *   Faz uma DEFESA usando a velocidade do personagem e tendo a chance
    * de adicionar um bônus de velocidade caso a vairável numeroSortido
    * seja igual a variável numeroDoBonus que também é aleatória
    * */
    fun defender(): Int {
        val numeroDoBonus: Int = Random.nextInt(1, 21)
        val numeroSortido: Int = Random.nextInt(1, 21)

        if(numeroSortido == numeroDoBonus) {
            val bonusVelocidade: Int = Random.nextInt(5, 31)
            val velocidadeTotal: Int = velocidade + bonusVelocidade;
            return velocidadeTotal
        } else {
            return velocidade
        }
    }

    /*
    *  Faz o uso do poder do personagem, por padrão o poder é apenas sua
    * forca, contudo, nas subclasses (classes filhas), cada uma tem seu
    * poder único para ataques fortes.
    * */
    open fun usarPoder(): Int {
        return forca;
    }
}