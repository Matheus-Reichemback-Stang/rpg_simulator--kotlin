package rpg.simulator.application.model

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

/*Importante: no banco, como a tabela é única, as colunas específicas das outras classes (Mago e Ladino)
* serão nulas para o Guerreiro. Por isso, no banco ela aceita nulo, mas no Kotlin podemos inicializar com 0.*/


/* Nas classes filhas, o @Entity não serve para criar uma nova tabela, mas sim para dizer ao JPA:

"Ei, o ciclo de vida desta classe também é gerenciado pelo banco de dados, e ela faz parte da
estrutura de herança da tabela pai."

Na herança com SINGLE_TABLE, o @Entity na classe Pai define a criação da tabela física. O @Entity nas
classes Filhas serve apenas para registrar essas subclasses no motor do JPA, permitindo que o Spring
reconheça os atributos específicos de cada uma e consiga transformá-las em registros dentro daquela
tabela única.*/
@Entity
@DiscriminatorValue("Guerreiro")
class Guerreiro(
    id: Int = 0,
    nome: String,
    forca: Int,
    velocidade: Int,
    vida: Int,
    var pontosDeDefesa: Int = 0
): Personagem(id, nome, forca, velocidade, vida) {

    /*
    *   O usaPoder do Guerreiro faz com que ele gaste seus pontos
    * de defesa para repelir o ataque, removendo pontos de defesa
    * de acordo com a força do ataque e até que os pontos cheguem
    * a zero. Após isso é inútil usar esta habilidade.
    * */
    override fun usarPoder(): Int {
        return pontosDeDefesa
    }
}