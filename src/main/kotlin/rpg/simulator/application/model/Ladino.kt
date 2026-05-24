package rpg.simulator.application.model

import kotlin.random.Random

class Ladino(
    id: Int,
    nome: String,
    forca: Int,
    velocidade: Int,
    vida: Int,
    var sagacidade: Int
):Personagem(id=id, nome=nome, forca=forca, velocidade=velocidade, vida=vida) {

    /*
    *   O usarPoder do Ladino faz com que ele use sua sagacidade no lugar
    * da sua força em um ataque, portanto ele causa dano através de sua sagacidade
    * e aplica um bônus variável de sagacidade
    * */
    override fun usarPoder(): Int {
        val bonusSagacidade: Int = Random.nextInt(3, 21)
        val sagacidadeTotal: Int = sagacidade + bonusSagacidade
        return sagacidadeTotal
    }
}