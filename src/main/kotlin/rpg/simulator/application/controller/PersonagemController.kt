package rpg.simulator.application.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import rpg.simulator.application.model.Personagem
import rpg.simulator.application.service.PersonagemService

/* Aqui é última camada da arquitetura clássica do Spring Boot: o Controller. É aqui que o seu RPG ganha vida na internet,
* permitindo que jogadores enviem comandos HTTP para criar personagens, atacar ou ver o status do jogo.
*
* O Controller é a porta de entrada da sua aplicação. O objetivo dele é expor as funcionalidades do seu jogo para a rede
* através do protocolo HTTP. Ele não faz cálculos de batalha e não mexe no banco de dados. O Controller apenas:
* 1 - Escuta o que o cliente (jogador/navegador) está pedindo na internet.
* 2 - Captura os dados enviados (como o nome do personagem).
* 3 - Passa esses dados para o Service processar.
* 4 - Pega o resultado do Service e devolve para o cliente com uma resposta HTTP apropriada (geralmente convertendo o
* resultado em JSON).*/

/* O Padrão de URLs: O Modelo REST:
* No padrão REST, a URL deve identificar apenas o recurso (o "substantivo"), e nunca a ação (o "verbo"). Quem diz o
* que vai ser feito é o Verbo HTTP, não o texto da URL.
*
* Por que a "Má Prática" é ruim?
* Colocar /salvar ou /listarTodos na URL polui o sistema. Se você tiver 50 tabelas no banco de dados, terá que criar
* centenas de URLs confusas (como /deletarMago, /editarLadino).
*
* Por que a "Boa Prática" é incrível?
* Usando a URL base limpa (/personagens), o seu sistema fica elegante e padronizado:
* POST /personagens -> O Spring sabe que é para criar.
* GET /personagens -> O Spring sabe que é para listar todos.
* GET /personagens/5 -> O Spring sabe que é para buscar o número 5.
* PUT /personagens/5 -> O Spring sabe que é para atualizar o número 5.
* DELETE /personagens/5 -> O Spring sabe que é para excluir o número 5.*/

/* As Partes de uma URL - https://api.rpg.com:8080/personagens/5?nivel=10
* 1 - Protocolo (https:// ou http://): Define as regras de comunicação (segura ou não).
* 2 - Host/Domínio (api.rpg.com): É o endereço do servidor na internet (o IP "traduzido" para texto legível).
* Localmente na sua máquina, você usará localhost.
* 3 - Porta (:8080): É o "canal" de entrada específico daquele servidor. O Spring Boot por padrão usa a porta 8080.
* 4 - Path/Caminho (/personagens/5): Identifica o recurso que você quer acessar dentro do servidor. O 5 aqui é o nosso
* parâmetro capturado pelo @PathVariable.
* 5 - Query Parameter (?nivel=10): Tudo o que vem depois do ponto de interrogação ? são parâmetros extras de filtro ou
* busca (não obrigatórios). No Spring, capturamos isso com outra anotação chamada @RequestParam.*/


/*@RestController - Avisa ao Spring que esta classe vai responder a requisições HTTP da internet. É a união de @Controller
* com @ResponseBody. Ela diz ao Spring que as funções dessa classe não vão abrir uma página HTML física (View tradicional),
* mas sim devolver os dados brutos direto para o corpo da resposta web (em formato texto JSON).*/
@RestController
/* @RequestMapping - Define a URL base para acessar as funções de personagens (ex: http://localhost:8080/personagens).
* Define a rota de internet. Significa que para usar qualquer função/rotina/metodo desse controller, você precisará acessar
* o endereço do seu servidor seguido de /personagens.*/
@RequestMapping("/personagens")
class PersonagemController (
    // Injeção de Dependência: O Spring injeta o PersonagemService automaticamente.
    private val personagemService: PersonagemService
) {

    // 1. SALVAR (POST) -> Envia dados no corpo da requisição para criar um personagem
    /* @PostMapping - Usado para criar/inserir algo novo. Ao contrário do GET, ele transporta os dados de forma oculta no corpo
    * da requisição (@RequestBody). Não é idempotente: se você disparar o mesmo POST 3 vezes, criará 3 personagens idênticos no
    * banco.*/
    @PostMapping
    /* @ReponseStatus - Permite customizar os códigos de status HTTP do servidor. Por padrão, o Spring responde 200 OK para
    * tudo que dá certo. Com essa anotação, mudamos para 201 Created no cadastro e 204 No Content na exclusão, seguindo as
    * melhores práticas do mercado.*/
    @ResponseStatus(HttpStatus.CREATED) // Retorna o código HTTP 211 (Created) se der certo
    /* @ResquestBody - Diz ao Spring para abrir o "pacote" da requisição HTTP, ler o texto bruto em formato JSON que o cliente enviou
     * e usar uma biblioteca interna (chamada Jackson) para converter aquele texto em um Objeto Kotlin real.*/
    fun criar(@RequestBody personagem: Personagem): Personagem {
        return personagemService.salvarPersonagem(personagem)
    }

    // 2. BUSCAR TODOS (GET) -> Retorna a lista de todos os personagens
    /* @GetMapping - Usado para buscar/consultar dados. Uma peculiaridade importante: requisições GET nunca devem ter um corpo (RequestBody).
    * Todos os parâmetros que você envia em um GET devem ir direto na URL (como caminhos ou parâmetros de busca). Ele deve ser "idempotente",
    * ou seja, fazer a mesma busca 100 vezes não deve alterar o estado do seu RPG.*/
    @GetMapping
    fun listarTodos(): List<Personagem> {
        return personagemService.buscarTodos()
    }

    // 3. BUSCAR POR ID (GET específico) -> Ex: /personagens/5
    @GetMapping("/{id}")
    /* @PathVariable - Captura valores que vêm direto na URL da rota. Por exemplo, na rota /personagens/5, a anotação
    * @PathVariable id: Int percebe que o valor é 5 e joga esse número dentro da variável id. O @PathVariable é capaz de interpretar qual é o
    * atributo que ele está resgatando da rota, para isso o nome do parâmetro deve ser idêntico ao da rota, contudo se você quiser alterar o
    * nome do parâmetro da função, você precisa especificar no @PathVariable qual é o atributo que ele está pegando. Esses casos também são
    * válidos para caso haja mais de um atributo resgatável dentro da rota. Ex: @PathVariable("id") codigoPersonagem: Int*/
    fun buscarPorId(@PathVariable id: Int): Personagem {
        return personagemService.buscarPorId(id)
    }
    // 4. EDITAR (PUT) -> Substitui os dados de um personagem existente
    /* @PutMapping - Usado para atualizar/substituir um registro existente. O PUT assume que você está enviando o objeto completo atualizado
    para substituir o antigo.*/
    @PutMapping("/{id}")
    fun atualizar(@PathVariable("id") codigoPersonagem: Int, @RequestBody personagemAtualizado: Personagem): Personagem {
        // Buscamos o personagem existente para garantir que ele existe
        val personagemDoBanco = personagemService.buscarPorId(codigoPersonagem)

        // Atualizamos os dados dele com os novos dados recebidos
        personagemDoBanco.nome = personagemAtualizado.nome
        personagemDoBanco.forca = personagemAtualizado.forca
        personagemDoBanco.velocidade = personagemAtualizado.velocidade
        personagemDoBanco.vida = personagemAtualizado.vida

        // Salvamos de volta (o .save() do repository vai entender como UPDATE por causa do ID)
        return personagemService.salvarPersonagem(personagemDoBanco)
    }

    // 5. DELETAR (DELETE) -> Remove o personagem do banco pelo ID
    /* @DeleteMapping - Usado para remover dados. Geralmente não precisa de corpo, apenas do identificador (ID) na URL.*/
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Retorna o código HTTP 204 (No Content) indicando sucesso sem corpo de resposta
    fun deletar(@PathVariable id: Int) {
        personagemService.deletarPersonagem(id)
    }


}