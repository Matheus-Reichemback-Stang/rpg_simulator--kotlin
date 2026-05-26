package rpg.simulator.application.service

import org.springframework.stereotype.Service
import rpg.simulator.application.dto.MensagemChat

/*
 * ChatService gerencia o histórico de mensagens em memória.
 *
 * HashMap<String, MutableList<MensagemChat>>:
 * - A chave (String) é o nome da sala (ex: "sala-1")
 * - O valor (MutableList) é a lista de mensagens daquela sala
 */
@Service
class ChatService {

    // Armazena as mensagens separadas por sala
    /* LinkedHashMap É um mapa (dicionário) baseado em chave e valor. A Chave é o nome da sala (uma String, ex: "sala-guerreiros").
     * O Valor é uma lista mutável (MutableList) contendo todos os objetos MensagemChat enviados naquela sala. O diferencial do LinkedHashMap
     * é que ele mantém a ordem exata em que as salas foram criadas, impedindo que a lista de salas mude de posição aleatoriamente na tela. */
    private val mensagensPorSala = LinkedHashMap<String, MutableList<MensagemChat>>()

    /*
     * Processa uma mensagem recebida: salva no histórico da sala
     * e a retorna para ser distribuída pelo WebSocket para todos
     * os inscritos naquele tópico.
     */
    fun processarMensagem(mensagem: MensagemChat): MensagemChat {
        // getOrPut: busca a lista da sala; se não existir, cria uma lista vazia primeiro
        mensagensPorSala.getOrPut(mensagem.sala) { mutableListOf() }
            .add(mensagem)
        return mensagem
    }

    /*
     * Retorna o histórico de mensagens de uma sala específica.
     * Usado no endpoint GET para quem entra na sala depois e quer
     * ver as mensagens anteriores.
     */
    fun buscarHistorico(sala: String): List<MensagemChat> {
        // Elvis operator (?:): se a sala não existir, retorna lista vazia em vez de null
        // CURIOSIDADE - O operador ?: recebeu esse nome porque, se você virar a cabeça para a esquerda, ele se parece com o topete e os olhos do cantor Elvis Presley.

        /* Em termos técnicos, ele é o Operador de Coalescência Nula. Ele serve para tratar valores que podem ser nulos (null) de forma extremamente curta, substituindo
         * aquele monte de blocos if (variavel == null) { ... } else { ... }.
         *
         * Como ele funciona na prática:
         * O operador olha para o que está do seu lado esquerdo.
         * Se o lado esquerdo NÃO for nulo, ele usa o valor do lado esquerdo.
         * Se o lado esquerdo FOR nulo, ele ignora o lado esquerdo e usa o que estiver do lado direito.*/
        return mensagensPorSala[sala] ?: emptyList()
    }

    /*
     * Retorna os nomes de todas as salas que já receberam ao menos uma mensagem.
     */
    fun listarSalas(): List<String> {
        return mensagensPorSala.keys.toList()
    }
}