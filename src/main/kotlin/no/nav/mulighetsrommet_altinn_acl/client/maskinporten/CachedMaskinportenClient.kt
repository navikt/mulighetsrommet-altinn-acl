package no.nav.mulighetsrommet_altinn_acl.client.maskinporten

import no.nav.common.token_client.cache.CaffeineTokenCache

class CachedMaskinportenClient(
	private val maskinportenClient: MaskinportenClient,
) : MaskinportenClient {
	private val key = "altinn"

	private val cache = CaffeineTokenCache()

	override fun hentAltinnToken(): String = cache.getFromCacheOrTryProvider(key, maskinportenClient::hentAltinnToken)
}
