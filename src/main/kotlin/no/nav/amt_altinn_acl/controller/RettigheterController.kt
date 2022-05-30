package no.nav.amt_altinn_acl.controller

import no.nav.amt_altinn_acl.service.AuthService
import no.nav.amt_altinn_acl.utils.Issuer
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/rettighet")
class RettigheterController(
	private val authService: AuthService
) {

	@PostMapping("/hent-alle")
	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	fun hentAlleRettigheter(@RequestBody request: HentRettigheter.Request): HentRettigheter.Response {
		authService.verifyRequestIsMachineToMachine()

		return HentRettigheter.Response(emptyList())
	}

	@PostMapping("/har-rettighet")
	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	fun harRettighet(@RequestBody request: HarRettighet.Request): HarRettighet.Response {
		authService.verifyRequestIsMachineToMachine()

		return HarRettighet.Response(false)
	}

	object HentRettigheter {
		data class Request(
			val norskIdent: String
		)

		data class Response(
			val rettigheter: List<Rettighet>
		) {
			data class Rettighet(
				val id: String,
				val navn: String,
				val organisasjonsnummer: String,
			)
		}
	}

	object HarRettighet {
		data class Request(
			val norskIdent: String,
			val rettighetId: String
		)

		data class Response(
			val harRettighet: Boolean
		)
	}

}
