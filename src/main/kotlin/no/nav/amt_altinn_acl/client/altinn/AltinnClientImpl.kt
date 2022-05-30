package no.nav.amt_altinn_acl.client.altinn

import no.nav.common.rest.client.RestClient
import okhttp3.OkHttpClient

class AltinnClientImpl(
	private val baseUrl: String,
	private val altinnApiKey: String,
	private val maskinportenTokenProvider: () -> String,
	private val client: OkHttpClient = RestClient.baseClient(),
) : AltinnClient {



}
