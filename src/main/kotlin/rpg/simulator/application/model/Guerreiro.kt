package rpg.simulator.application.model

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import kotlin.random.Random

/*Importante: no banco, como a tabela é única, as colunas específicas das outras classes (Mago e Ladino)
* serão nulas para o Guerreiro. Por isso, no banco ela aceita nulo, mas no Kotlin podemos inicializar com 0.*/


/* Nas classes filhas, o @Entity não serve para criar uma nova tabela, mas sim para dizer ao JPA:
 *
 * "Ei, o ciclo de vida desta classe também é gerenciado pelo banco de dados, e ela faz parte da
 * estrutura de herança da tabela pai."
 * Na herança com SINGLE_TABLE, o @Entity na classe Pai define a criação da tabela física. O @Entity nas
 * classes Filhas serve apenas para registrar essas subclasses no motor do JPA, permitindo que o Spring
 * reconheça os atributos específicos de cada uma e consiga transformá-las em registros dentro daquela
 * tabela única.*/
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


    // Executa uma defesa normal ou usa o poder do Gerreiro
    override fun defender(): Int {
        // Gera um número aleatório de 1 a 100
        val chance = Random.nextInt(1, 101)
        val defesaBase = super.defender()

        // Tem 20% de chance de usar seus pontosDeDefesa como sua defesa
        if (chance <= 20) {
            return usarPoder()
        }
        return defesaBase
    }

    /*
    * O usaPoder do Guerreiro faz com que ele use seu pontosDeDefesa que por sua vez,
    * são capazes de repelir ataques com muito mais força, assim podendo causar sua sobrevivência
    * por mais tempo
    * */
    override fun usarPoder(): Int {
        return pontosDeDefesa
    }
}