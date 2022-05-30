package no.nav.amt_altinn_acl.client.maskinporten

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MaskinportenClientConfig {

	@Value("\${altinn.url}")
	lateinit var altinnUrl: String

	@Value("\${maskinporten.scopes}")
	lateinit var maskinportenScopes: String

	@Value("\${maskinporten.client-id}")
	lateinit var maskinportenClientId: String

	@Value("\${maskinporten.issuer}")
	lateinit var maskinportenIssuer: String

	@Value("\${maskinporten.token-endpoint}")
	lateinit var maskinportenTokenEndpoint: String

	@Value("\${maskinporten.client-jwk}")
	lateinit var maskinportenClientJwk: String

	@Bean
	fun maskinportenClient(): MaskinportenClient {
		val client = MaskinportenClientImpl(
			clientId = maskinportenClientId,
			issuer = maskinportenIssuer,
			altinnUrl = altinnUrl,
			scopes = maskinportenScopes.split(" "),
			tokenEndpointUrl = maskinportenTokenEndpoint,
			privateJwk = maskinportenClientJwk,
		)

		return CachedMaskinportenClient(client)
	}

}
