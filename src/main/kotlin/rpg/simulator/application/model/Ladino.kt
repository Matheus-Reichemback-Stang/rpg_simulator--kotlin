package rpg.simulator.application.model

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import kotlin.random.Random

@Entity
@DiscriminatorValue("Ladino")
class Ladino(
    id: Int = 0,
    nome: String,
    forca: Int,
    velocidade: Int,
    vida: Int,
    var sagacidade: Int = 0
):Personagem(id=id, nome=nome, forca=forca, velocidade=velocidade, vida=vida) {

    // Executa um ataque normal ou usa o poder do Ladino para causar mais dano
    override fun atacar(): Int {
        // Gera um número aleatório de 1 a 100
        val chance = Random.nextInt(1, 101)
        val ataqueBase = super.atacar()

        // Tem 20% de chance de usar seu poder ao invés do ataque normal
        if (chance <= 20) {
            return usarPoder()
        }
        return ataqueBase
    }

    /*
    *   O usarPoder do Ladino faz com que ele use sua sagacidade no lugar
    * da sua força em um ataque, portanto ele causa dano através de sua sagacidade
    * e aplica um bônus variável de sagacidade
    * */
    override fun usarPoder(): Int {
        val bonusSagacidade: Int = Random.nextInt(1, 21)
        val sagacidadeTotal: Int = sagacidade + bonusSagacidade
        return sagacidadeTotal
    }
}