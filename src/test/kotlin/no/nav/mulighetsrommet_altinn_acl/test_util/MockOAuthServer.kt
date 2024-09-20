package no.nav.mulighetsrommet_altinn_acl.test_util

import no.nav.security.mock.oauth2.MockOAuth2Server
import org.slf4j.LoggerFactory

open class MockOAuthServer {
	private val tokenXIssuer = "tokenx"

	private val log = LoggerFactory.getLogger(javaClass)

	companion object {
		private val server = MockOAuth2Server()
	}

	fun start() {
		try {
			server.start()
		} catch (e: IllegalArgumentException) {
			log.info("${javaClass.simpleName} is already started")
		}
	}

	fun getDiscoveryUrl(issuer: String = tokenXIssuer): String = server.wellKnownUrl(issuer).toString()

	fun shutdown() {
		server.shutdown()
	}

	fun issueTokenXToken(
		subject: String = "test",
		audience: String = "test-aud",
		claims: Map<String, Any> = emptyMap(),
	): String = server.issueToken(tokenXIssuer, subject, audience, claims).serialize()
}
