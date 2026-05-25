package rpg.simulator.application.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import rpg.simulator.application.service.BatalhaService
import rpg.simulator.application.service.ResultadoBatalha

@RestController
// URL base: http://localhost:8080/batalha
@RequestMapping("/batalha")
class BatalhaController (
    // Injeção de Dependência do BatalhaService e Inversão de Controle do Spring
    private val batalhaService: BatalhaService
) {
    /*
    * POST /batalha/{id1}/vs/{id2}
    *
    * Inicia e executa uma batalha completa entre dois personagens.
    * Usamos POST aqui pois estamos "criando" um evento de batalha —
    * uma ação que gera um resultado novo a cada chamada (não é idempotente).
    *
    * Exemplo de uso no Postman:
    * POST http://localhost:8080/batalha/1/vs/2
    * (sem corpo os IDs vão direto na URL como @PathVariable)
    *
    * Retorna o histórico completo de rodadas e o nome do vencedor em JSON.
    */
    @PostMapping("/{id1}/vs/{id2}")
    // @PathVariable captura os IDs diretamente da URL
    fun iniciarBatalha(@PathVariable id1: Int, @PathVariable id2: Int): ResultadoBatalha {
        return batalhaService.executarBatalha(id1, id2)
    }

    /*
     * GET /batalha/{id1}/vs/{id2}
     *
     * Funciona igual ao POST acima, porém via GET.
     * Esse endpoint é útil para testar diretamente no navegador ou via link, mas seu uso é errado.
     * Pois o GET deve sempre retornar os mesmo valor, mas a batalha sempre gera variações, então o
     * correto é um POST
     *
     * Exemplo de uso no navegador:
     * http://localhost:8080/batalha/1/vs/2
     */
    @GetMapping("/{id1}/vs/{id2}")
    fun verBatalha(@PathVariable id1: Int, @PathVariable id2: Int): ResultadoBatalha {
        return batalhaService.executarBatalha(id1, id2)
    }
}