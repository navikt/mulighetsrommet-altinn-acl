package no.nav.mulighetsrommet_altinn_acl

import org.springframework.boot.SpringApplication

fun main(args: Array<String>) {
	val application = SpringApplication(Application::class.java)
	application.setAdditionalProfiles("local")
	application.run(*args)
}
