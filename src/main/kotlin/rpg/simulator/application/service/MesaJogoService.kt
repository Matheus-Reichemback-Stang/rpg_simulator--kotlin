package rpg.simulator.application.service

import org.springframework.stereotype.Service
import rpg.simulator.application.dto.SalaBatalha
import java.util.concurrent.ConcurrentHashMap

/*Este serviço vai centralizar as escolhas na memória. Quando o segundo jogador escolhe,
* ele mesmo já puxa o BatalhaService, roda a pancadaria e devolve o resultado pronto.
*
* Este arquivo é um Gerenciador de Estado de Sessão. Ele funciona como um juiz de mesa de RPG,
* anotando em um papel quem chegou, quais são suas fichas de personagem e, quando os dois se posicionam,
* ele mesmo joga os dados e anuncia o vencedor.
* */

@Service
class MesaJogoService(
    // Injeção de Indepedência
    private val batalhaService: BatalhaService
) {
    /* Ao contrário de um HashMap comum ConcurrentHashMap é Thread-Safe (seguro para concorrência).
     * Como o WebSocket permite que vários jogadores enviem mensagens exatamente no mesmo milissegundo,
     * essa estrutura impede que as requisições de um jogador corrompam ou travem os dados do outro
     * na memória RAM.*/
    /*
    * Cria o nosso "banco de dados temporário" em memória RAM. A chave (String) é o nome
    * da sala (ex: "sala-da-taverna"), e o valor é o objeto SalaBatalha que guarda quem são
    * os dois jogadores atuais ali dentro.*/
    private val statusDasSalas = ConcurrentHashMap<String, SalaBatalha>()


    /*
    * registrarEscolha() - Declaração da função principal. Ela recebe três argumentos vindos do Controller: o
    * nome da sala, o nome do jogador que enviou a ação e o ID do personagem que ele escolheu. Ela promete
    * devolver o status atualizado da sala (SalaBatalha).
    * */
    fun registrarEscolha(sala: String, jogador: String, idPersonagem: Int): SalaBatalha {
        // Busca a sala ou cria uma nova se não existir no banco da memória RAM
        val salaAtual = statusDasSalas.getOrPut(sala) { SalaBatalha(nomeSala = sala) }

        // Encaixa o jogador na vaga 1 ou na vaga 2 e atualiza os personagens
        if (salaAtual.jogador1 == null || salaAtual.jogador1 == jogador) {
            salaAtual.jogador1 = jogador
            salaAtual.idPersonagem1 = idPersonagem
        } else if (salaAtual.jogador2 == null || salaAtual.jogador2 == jogador) {
            salaAtual.jogador2 = jogador
            salaAtual.idPersonagem2 = idPersonagem
        }

        // SE AMBOS ESCOLHERAM: A batalha roda AUTOMATICAMENTE aqui!
        if (salaAtual.idPersonagem1 != null && salaAtual.idPersonagem2 != null && salaAtual.resultado == null) {
            salaAtual.resultado = batalhaService.executarBatalha(
                // !! - Informam que os personagens não são nulos de certeza
                salaAtual.idPersonagem1!!,
                salaAtual.idPersonagem2!!
            )
        }

        return salaAtual
    }
}