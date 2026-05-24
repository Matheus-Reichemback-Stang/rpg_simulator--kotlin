package rpg.simulator.application.model


import kotlin.random.Random


open class Personagem (
    val id: Int,
    var nome: String,
    var forca: Int,
    var velocidade: Int,
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