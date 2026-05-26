package rpg.simulator.application.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

/* No HTTP, a comunicação é de via única e baseada em "Requisição e Resposta". O cliente (Postman/Celular)
 * faz uma pergunta, o servidor responde e a conexão morre imediatamente. O servidor nunca pode falar com
 * o cliente se não for solicitado.
 *
 * O problema disso no Chat: Se você estivesse usando HTTP, para saber se recebeu uma mensagem nova, o seu
 * aplicativo teria que ficar fazendo requisições GET para o servidor a cada 2 segundos (técnica chamada Polling).
 * Isso sobrecarrega o servidor e gasta muita internet.
 *
 * Por que usar WebSocket (protocolo de comunicação em tempo real)?
 * O WebSocket resolve isso mudando a natureza da internet. Em vez de abrir e fechar a conexão, ele abre um túnel
 * de comunicação bidirecional e persistente. A conexão fica "viva" por horas. Tanto o cliente quanto o servidor podem
 * empurrar dados por esse túnel a qualquer milissegundo. É graças a isso que o WhatsApp mostra as mensagens chegando
 * na mesma hora, sem você precisar ficar atualizando a tela.
 *
 * "O WebSocket envia dados crus, então nós usamos o STOMP para estruturar estes dados"
 *
 * O que é STOMP (Simple Text Oriented Messaging Protocol)?
 * O WebSocket puro é muito primitivo: ele só envia textos puros, sem saber quem enviou ou para onde vai. Por isso, usamos
 * o STOMP (um protocolo que roda em cima do WebSocket). Ele funciona como o "serviço de correios" do chat, introduzindo os
 * conceitos de Tópicos (Salas) e Mensagens Estruturadas.*/


/* @Configuration — Diz ao Spring que esta classe define configurações da aplicação. É lida durante a inicialização, antes
 * de qualquer requisição chegar. Avisa ao Spring que esta classe não é um Controller nem um Service comum. Ela é uma classe
 * de infraestrutura. O Spring Boot vai ler esse arquivo assim que você der "Play" no IntelliJ, moldando o comportamento do
 * servidor antes mesmo de aceitar qualquer conexão.
 *
 * @EnableWebSocketMessageBroker — Ativa o sistema de WebSocket com suporte ao protocolo STOMP. STOMP (Simple Text Oriented Messaging Protocol)
 * é uma camada acima do WebSocket puro que organiza as mensagens em "tópicos" e "filas", permitindo que múltiplos jogadores se inscrevam
 * em uma sala de chat e recebam as mensagens automaticamente. Ativa os superpoderes de mensagens em tempo real no Spring. "Message Broker"
 * significa "Corretor de Mensagens" — um sistema inteligente encarregado de receber mensagens de uma pessoa e distribuir para as outras.*/
@Configuration
@EnableWebSocketMessageBroker
// Aqui estamos estendendo a interface do Spring para customizar as regras do chat do nosso jeito.
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    /*
     * configureMessageBroker — Define como as mensagens são roteadas dentro do servidor.
     *
     * enableSimpleBroker("/topic") — Ativa um roteador de mensagens na memória RAM do servidor.
     * Toda vez que uma URL começar com "/topic", o Spring entende que aquilo é um canal público de
     * transmissão (Broadcast). Se 5 jogadores estiverem "escutando" a URL /topic/chat/sala-1, qualquer
     * mensagem jogada ali será replicada instantaneamente para os 5.
     *
     * setApplicationDestinationPrefixes("/app") — Define o prefixo que identifica mensagens
     * destinadas ao seu código Kotlin (aos @MessageMapping dos Controllers).
     * Quando um cliente envia para "/app/chat/sala-1", o Spring sabe que deve processar
     * com o metodo anotado com @MessageMapping("/chat/sala-1"). Define o gatilho para o seu código Kotlin.
     * Se um jogador quiser enviar uma mensagem para ser processada por uma lógica sua, o endereço de envio
     * deve começar com /app.
     */
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic")
        registry.setApplicationDestinationPrefixes("/app")
    }

    /*
     * registerStompEndpoints — Registra o endereço WebSocket onde os clientes se conectam. Este metodo configura a
     * "porta física" onde os jogadores vão bater para pedir conexão.
     *
     * addEndpoint("/ws-chat") — Cria o endereço inicial de conexão. Para o chat começar, o cliente não faz um GET
     * nem um POST. Ele faz uma requisição especial chamada Handshake (aperto de mão) na URL ws://localhost:8080/ws-chat
     * para transformar a conexão HTTP normal em uma conexão estável de WebSocket.
     *
     * setAllowedOriginPatterns("*") — Permite conexões de qualquer origem (qualquer URL).
     * Em produção você deve restringir isso para o domínio do seu frontend.
     *
     * withSockJS() — É o cinto de segurança do sistema. Se o jogador estiver tentando usar o chat através de um navegador
     * muito antigo ou de uma rede corporativa com redes bloqueadas que não aceitam WebSockets, o SockJS intercepta e simula
     * o tempo real usando truques de repetição de requisições HTTP normais de forma invisível. O usuário nem percebe a diferença.
     *
     * LONG-POLLING
     */
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws-chat")
            .setAllowedOriginPatterns("*")
            .withSockJS()
    }
}