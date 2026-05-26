package rpg.simulator.application.controller

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import rpg.simulator.application.dto.MensagemChat
import rpg.simulator.application.service.ChatService

/*
 * ChatController usa DOIS tipos de comunicação ao mesmo tempo:
 *
 * 1. WebSocket/STOMP (via @MessageMapping + @SendTo):
 *    Para o envio de mensagens em tempo real. O cliente se conecta
 *    uma vez e fica "ouvindo" o tópico — as mensagens chegam
 *    automaticamente sem precisar fazer nova requisição.
 *
 * 2. HTTP REST (via @GetMapping):
 *    Para buscar o histórico de mensagens de uma sala.
 *    Funciona igual aos outros Controllers do projeto.
 */

/*
* @RestController e @RequestMapping("/chat"): Configura a base HTTP da classe. Significa
* que para acessar os métodos REST tradicionais, a URL base começará com
* http://localhost:8080/chat.
* */
@RestController
@RequestMapping("/chat")
class ChatController(
    // Injeção de Dependência e Inversão de Controle
    private val chatService: ChatService
) {

    /*
     * @MessageMapping("/chat/{sala}") — Escuta mensagens WebSocket enviadas
     * para o destino "/app/chat/{sala}" (o prefixo "/app" foi definido no WebSocketConfig).
     * Ela diz ao Spring para escutar qualquer mensagem enviada pelos jogadores que chegue pelo
     * túnel do WebSocket com o destino /app/chat/sala-1
     *
     * @SendTo("/topic/chat/{sala}") — Após processar, distribui (broadcast) a mensagem
     * para TODOS os clientes inscritos no tópico "/topic/chat/{sala}".
     * É aqui que a mágica do tempo real acontece. Logo após a função terminar de rodar
     * e o Service salvar a mensagem na memória, o @SendTo pega o objeto retornado (MensagemChat)
     * e faz um Broadcast (transmissão em massa). Ele joga essa mensagem no canal /topic/chat/sala-1.
     * Todos os jogadores que estiverem conectados e "ouvindo" esse tópico específico vão receber o
     * JSON na tela na mesma hora, de forma automática.
     *
     * Como usar (via cliente WebSocket/STOMP, ex: extensão "Simple WebSocket Client"):
     * 1. Conectar em: http://localhost:8080/ws-chat
     * 2. Inscrever no tópico: /topic/chat/sala-1
     * 3. Enviar mensagem para: /app/chat/sala-1
     *    Com o corpo JSON: {"remetente": "Gandalf", "conteudo": "Que a batalha comece!", "sala": "sala-1"}
     */
    @MessageMapping("/chat/{sala}")
    @SendTo("/topic/chat/{sala}")
    fun enviarMensagem(mensagem: MensagemChat): MensagemChat {
        return chatService.processarMensagem(mensagem)
    }

    /*
     * GET /chat/historico/{sala}
     *
     * Por que esse metodo existe? O WebSocket só entrega mensagens que são enviadas enquanto você está conectado.
     * Se um jogador entrar no jogo agora, a tela dele estará em branco. Esse metodo serve para o frontend disparar
     * um GET logo na entrada, buscar o histórico que o ChatService guardou e carregar o chat com o passado da conversa.
     *
     * Retorna todas as mensagens já enviadas em uma sala específica.
     * Útil para quando um jogador entra na sala depois e quer ver
     * o que foi dito antes da sua chegada.
     *
     * Exemplo: GET http://localhost:8080/chat/historico/sala-1
     */
    @GetMapping("/historico/{sala}")
    fun verHistorico(@PathVariable sala: String): List<MensagemChat> {
        return chatService.buscarHistorico(sala)
    }

    /*
     * GET /chat/salas
     *
     * Por que esse metodo existe? Ele devolve um JSON contendo uma lista simples de Strings com os nomes das salas.
     * É perfeito para criar um painel no seu jogo do tipo: "Salas ativas no momento: [sala-guerreiros, taverna, calabouco]".
     *
     * Lista todas as salas de chat que já receberam mensagens.
     * Exemplo: GET http://localhost:8080/chat/salas
     */
    @GetMapping("/salas")
    fun listarSalas(): List<String> {
        return chatService.listarSalas()
    }
}