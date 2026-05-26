package rpg.simulator.application.model

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import kotlin.random.Random

@Entity
@DiscriminatorValue("Mago")
class Mago (
    id: Int = 0,
    nome: String,
    forca: Int,
    velocidade: Int,
    vida: Int,
    var pontosDeMagia: Int = 0
): Personagem(id, nome, forca, velocidade, vida) {

    // Executa um ataque normal ou usa o poder do Mago para causar mais dano
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
    *   O usarPoder do Mago faz com que ele usa seus pontosDeMagia
    * para um ataque, contudo esse ataque é feito usando o poderTotalDeMagia
    * que ele possui, no caso é somando os pontosDeMagia, vida e força
    * */
    override fun usarPoder(): Int {
        val poderTotalDeMagia = pontosDeMagia + vida + forca
        return poderTotalDeMagia
    }
}