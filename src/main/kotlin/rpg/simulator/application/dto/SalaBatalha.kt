package rpg.simulator.application.dto

import rpg.simulator.application.service.ResultadoBatalha

/*
* Este arquivo vai guardar na memória RAM quem está em qual sala e qual personagem escolheu.
* Se o jogador mandar uma mensagem normal, é chat. Se ele mandar o ID de um personagem, o
* sistema registra.
* */

// Representa o estado atual de uma sala de jogo
data class SalaBatalha(
    val nomeSala: String,
    var jogador1: String? = null,
    var idPersonagem1: Int? = null,
    var jogador2: String? = null,
    var idPersonagem2: Int? = null,
    var resultado: ResultadoBatalha? = null
)