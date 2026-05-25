package rpg.simulator.application.model

/* Essa classe representa UMA mensagem de chat enviada por um jogador.
 * Não usa @Entity pois as mensagens não precisam ser salvas no banco
 * de dados. As mensagens existem apenas na memória enquanto o servidor
 * está rodando */
data class MensagemChat (
    // remetente - Quem enviou a mensagem (Ex: "Jogador1", "Gandalf")
    val remetente: String,

    // conteudo - O conteúdo da mensagem (Ex: "Prepare-se para a batalha!")
    val conteudo: String,

    // sala - Define o local que o conteúdo da mensagem vai ser enviado (Ex: "sala-1", "geral")
    // Isso permite ter múltiplas conversas separadas ao mesmo tempo
    val sala: String
) {
}