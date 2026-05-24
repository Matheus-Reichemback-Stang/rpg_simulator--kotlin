package rpg.simulator.application.model

class Guerreiro(
    id: Int,
    nome: String,
    forca: Int,
    velocidade: Int,
    vida: Int,
    var pontosDeDefesa: Int
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