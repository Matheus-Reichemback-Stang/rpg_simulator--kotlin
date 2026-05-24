package rpg.simulator.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/*
@SpringBootApplication - Diz onde o Spring Boot deve inicializar e é formada de outras trê anotações:

@Configuration: Diz ao Spring que esta classe pode definir configurações e registrar componentes (beans)
no sistema.

@EnableAutoConfiguration: Ativa a "mágica" do Spring Boot. Ele olha para o seu arquivo pom.xml (Maven),
vê que você adicionou o driver do PostgreSQL e o Spring Data JPA, e automaticamente configura a conexão
com o banco de dados para você.

@ComponentScan: Diz ao Spring para escanear a pasta onde esse arquivo está e todas as subpastas abaixo dela.
O Spring precisa disso para encontrar as outras classes que você vai criar no futuro (como os Controllers e Repositories).
*/
@SpringBootApplication
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
