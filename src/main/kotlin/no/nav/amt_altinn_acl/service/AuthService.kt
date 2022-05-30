package no.nav.amt_altinn_acl.service

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
	private val tokenValidationContextHolder: TokenValidationContextHolder
) {

	companion object {
		const val ACCESS_AS_APPLICATION_ROLE = "access_as_application"
	}

	fun verifyRequestIsMachineToMachine() {
		if (!isRequestFromMachine()) {
			throw ResponseStatusException(HttpStatus.FORBIDDEN, "Request is not machine-to-machine")
		}
	}

	private fun isRequestFromMachine(): Boolean {
		val roles = tokenValidationContextHolder
			.tokenValidationContext
			.anyValidClaims
			.map { claims -> claims.getAsList("roles") ?: emptyList() }
			.orElse(emptyList())

		return roles.contains(ACCESS_AS_APPLICATION_ROLE)
	}

}
