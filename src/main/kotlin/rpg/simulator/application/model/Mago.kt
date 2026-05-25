package rpg.simulator.application.model

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

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