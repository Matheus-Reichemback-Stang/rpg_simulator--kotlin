package rpg.simulator.application.service

import org.springframework.stereotype.Service
import rpg.simulator.application.model.Personagem

// Classe que carrega o resultado de uma rodada de batalha.
// Usamos 'data class' pois ela serve apenas para transportar dados — sem lógica.
// Será convertida automaticamente para JSON pelo Spring ao retornar no Controller.
data class ResultadoRodada(
    val rodada: Int,               // Número da rodada atual
    val nomeAtacante: String,      // Nome de quem atacou
    val nomeDefensor: String,      // Nome de quem defendeu
    val danoAtaque: Int,           // Valor do ataque (pode ser normal ou com bônus)
    val defesa: Int,               // Valor da defesa (pode ser normal ou com bônus)
    val danoCausado: Int,          // Dano real = ataque - defesa (mínimo 0)
    val vidaRestanteDefensor: Int, // Quanto de vida sobrou no defensor após o golpe
    val vencedor: String?          // Nome do vencedor (null se a batalha ainda não acabou)
)

// Classe que representa o resultado COMPLETO de toda a batalha
data class ResultadoBatalha(
    val rodadas: List<ResultadoRodada>, // Histórico de todas as rodadas
    val vencedor: String                // Nome do vencedor final
)

@Service
class BatalhaService(
    // Injeção do PersonagemService para buscar os personagens do banco pelo ID
    private val personagemService: PersonagemService
) {

    /*
     * Método principal que executa a batalha completa entre dois personagens.
     * Recebe os IDs dos dois personagens, busca eles no banco de dados,
     * e simula o combate até que um deles chegue a 0 (ou menos) de vida.
     *
     * O combate funciona em rodadas:
     * - O personagem mais rápido (maior velocidade) sempre ataca primeiro
     * - O ataque usa o método atacar() da classe Personagem (herdado por todos)
     * - A defesa usa o método defender() da classe Personagem
     * - O dano real = valor do ataque - valor da defesa (mínimo 1, para sempre causar algum dano)
     * - A batalha para quando algum personagem chega a vida <= 0
     */
    fun executarBatalha(idPersonagem1: Int, idPersonagem2: Int): ResultadoBatalha {
        // Buscamos os dois personagens no banco de dados usando o PersonagemService
        // Se qualquer ID não existir, o .orElseThrow do buscarPorId já lança o erro automaticamente
        val personagem1 = personagemService.buscarPorId(idPersonagem1)
        val personagem2 = personagemService.buscarPorId(idPersonagem2)

        // Fazemos cópias das vidas para não alterar os dados reais no banco durante a batalha
        // (a vida no banco só muda se você quiser salvar o resultado — aqui apenas simulamos)
        var vidaAtual1 = personagem1.vida
        var vidaAtual2 = personagem2.vida

        // Lista que vai acumulando o resultado de cada rodada
        val historicoRodadas = mutableListOf<ResultadoRodada>()

        var numeroRodada = 1

        // Loop da batalha: continua enquanto os dois personagens ainda estiverem vivos
        while (vidaAtual1 > 0 && vidaAtual2 > 0) {

            // Determinamos quem ataca primeiro baseado na velocidade
            // O mais rápido tem vantagem de atacar antes nesta rodada
            val (atacante, defensor, vidaAtacante, vidaDefensor) =
                if (personagem1.velocidade >= personagem2.velocidade) {
                    // Personagem 1 é mais rápido ou empatou — ele ataca primeiro
                    arrayOf(personagem1, personagem2, vidaAtual1, vidaAtual2)
                } else {
                    // Personagem 2 é mais rápido — ele ataca primeiro
                    arrayOf(personagem2, personagem1, vidaAtual2, vidaAtual1)
                }

            // Usamos cast (as Personagem) para garantir que o compilador veja como Personagem
            val atacanteCast = atacante as Personagem
            val defensorCast = defensor as Personagem

            // Chamamos os métodos de ataque e defesa definidos na classe Personagem
            val valorAtaque = atacanteCast.atacar()   // Pode ter bônus aleatório de força
            val valorDefesa = defensorCast.defender() // Pode ter bônus aleatório de velocidade

            // Calculamos o dano real causado. O 'coerceAtLeast(1)' garante dano mínimo de 1
            // (para que a batalha não fique presa caso o defensor seja muito forte)
            val danoCausado = (valorAtaque - valorDefesa).coerceAtLeast(1)

            // Subtraímos o dano da vida atual do defensor
            val novaVidaDefensor = (vidaDefensor as Int) - danoCausado

            // Atualizamos as vidas locais (não salva no banco)
            if (defensorCast == personagem1) {
                vidaAtual1 = novaVidaDefensor
            } else {
                vidaAtual2 = novaVidaDefensor
            }

            // Verificamos se a batalha acabou nessa rodada
            val vencedorDaRodada = if (novaVidaDefensor <= 0) atacanteCast.nome else null

            // Registramos o que aconteceu nessa rodada
            historicoRodadas.add(
                ResultadoRodada(
                    rodada = numeroRodada,
                    nomeAtacante = atacanteCast.nome,
                    nomeDefensor = defensorCast.nome,
                    danoAtaque = valorAtaque,
                    defesa = valorDefesa,
                    danoCausado = danoCausado,
                    vidaRestanteDefensor = novaVidaDefensor.coerceAtLeast(0),
                    vencedor = vencedorDaRodada
                )
            )

            // Se alguém morreu, saímos do loop
            if (novaVidaDefensor <= 0) break

            numeroRodada++
        }

        // Determinamos o vencedor final: quem ainda tem vida positiva
        val nomeVencedor = if (vidaAtual1 > 0) personagem1.nome else personagem2.nome

        return ResultadoBatalha(
            rodadas = historicoRodadas,
            vencedor = nomeVencedor
        )
    }
}