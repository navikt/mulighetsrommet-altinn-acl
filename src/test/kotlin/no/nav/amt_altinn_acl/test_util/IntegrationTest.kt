package no.nav.amt_altinn_acl.test_util

import no.nav.amt_altinn_acl.test_util.Constants.TEST_JWK
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ActiveProfiles("test")
@Import(TestConfig::class)
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {

	@LocalServerPort
	private var port: Int = 0

	private val client = OkHttpClient()

	companion object {
		val oAuthServer = MockOAuthServer()

		@JvmStatic
		@DynamicPropertySource
		fun registerProperties(registry: DynamicPropertyRegistry) {
			oAuthServer.start()
			registry.add("no.nav.security.jwt.issuer.azuread.discovery-url", oAuthServer::getDiscoveryUrl)
			registry.add("no.nav.security.jwt.issuer.azuread.accepted-audience") { "test-aud" }

			registry.add("altinn.url") { "" }
			registry.add("altinn.api-key") { "test-altinn-api-key" }

			registry.add("maskinporten.scopes") { "scope1 scope2" }
			registry.add("maskinporten.client-id") { "abc123" }
			registry.add("maskinporten.issuer") { "https://test-issuer" }
			registry.add("maskinporten.token-endpoint") { "TODO" }
			registry.add("maskinporten.client-jwk") { TEST_JWK }
		}
	}

	fun serverUrl() = "http://localhost:$port"

	fun sendRequest(
		method: String,
		path: String,
		body: RequestBody? = null,
		headers: Map<String, String> = emptyMap()
	): Response {
		val reqBuilder = Request.Builder()
			.url("${serverUrl()}$path")
			.method(method, body)

		headers.forEach {
			reqBuilder.addHeader(it.key, it.value)
		}

		return client.newCall(reqBuilder.build()).execute()
	}

}
