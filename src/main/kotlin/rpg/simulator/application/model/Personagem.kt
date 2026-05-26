package rpg.simulator.application.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
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

/* Essas duas anotações pertencem à biblioteca Jackson, que é o motor interno do Spring Boot responsável por transformar JSON (texto)
 * em Objetos Kotlin (e vice-versa). O objetivo geral delas é ensinar o Spring a lidar com o Polimorfismo no JSON. Elas dizem ao Jackson:
 * "Não crie apenas a classe mãe genérica; olhe para o texto do JSON, descubra qual é o tipo do herói e instancie a classe filha correta".
 *
 * Como o endpoint do Controller recebe a classe abstrata/mãe Personagem, o Jackson por padrão não sabe qual filho instanciar. Usamos o
 * @JsonTypeInfo para definir que a propriedade "tipo_personagem" do JSON guiará essa escolha, e o @JsonSubTypes para mapear quais strings
 * correspondem a quais subclasses Kotlin. Isso garante que os atributos específicos de cada herói sejam processados corretamente
 *
 * @JsonTypeInfo - Esta anotação configura como o Jackson vai identificar o tipo da classe dentro do JSON.
 * 1 - use = JsonTypeInfo.Id.NAME -> Diz que vamos usar um nome lógico em formato de texto (uma String) para identificar a classe filha, em
 * vez de usar o caminho completo do arquivo físico do Kotlin.
 * 2 - include = JsonTypeInfo.As.PROPERTY -> Define que esse nome identificador vai vir como se fosse uma propriedade comum (uma chave) dentro
 * do objeto JSON.
 * 3 - property = "tipo_personagem" ->  É o nome exato da chave que você vai escrever no Postman. Quando o Jackson ler "tipo_personagem": "Guerreiro",
 * ele saberá que essa linha define o tipo do objeto.
 * 4 - defaultImpl = Personagem::class -> É a rota de fuga. Se o jogador enviar um JSON sem a propriedade tipo_personagem, ou se escrever um tipo que não
 * existe, o Jackson vai criar uma instância da classe mãe genérica (Personagem) para o sistema não quebrar.
 * */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "tipo_personagem", // Nome da propriedade que você vai enviar no JSON
    defaultImpl = Personagem::class
)
/* @JsonSubTypes - Enquanto a primeira anotação diz onde procurar o tipo, esta aqui serve para mapear os nomes textuais para as classes Kotlin reais.
É um dicionário de tradução para o Spring.

Cada JsonSubTypes.Type faz uma ligação direta:
value = Guerreiro::class, name = "Guerreiro" -> Se o texto na propriedade for "Guerreiro", instancie a classe Guerreiro.kt e capture os pontosDeDefesa.
*/
@JsonSubTypes(
    JsonSubTypes.Type(value = Guerreiro::class, name = "Guerreiro"),
    JsonSubTypes.Type(value = Mago::class, name = "Mago"),
    JsonSubTypes.Type(value = Ladino::class, name = "Ladino")
)
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
    open fun atacar(): Int {
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
    open fun defender(): Int {
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