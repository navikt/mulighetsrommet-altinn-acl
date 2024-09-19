package no.nav.amt_altinn_acl.client.maskinporten

import no.nav.common.token_client.cache.CaffeineTokenCache

class CachedMaskinportenClient(
	private val maskinportenClient: MaskinportenClient
) : MaskinportenClient {

	private val key = "altinn"

	private val cache = CaffeineTokenCache()

	override fun hentAltinnToken(): String {
		return cache.getFromCacheOrTryProvider(key, maskinportenClient::hentAltinnToken)
	}

}
