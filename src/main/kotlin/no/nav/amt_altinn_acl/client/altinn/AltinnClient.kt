package no.nav.amt_altinn_acl.client.altinn

interface AltinnClient {

	fun hentTilknyttedeEnheter(norskIdent: String): List<Enhet>

	fun hentRettigheter(norskIdent: String, organisasjonsnummer: String): List<AltinnRettighet>

}

data class Enhet(
	val organisasjonsnummer: String,
	val type: Type
) {
	enum class Type {
		OVERORDNET_ENHET,
		UNDERENHET
	}
}

data class AltinnRettighet(
	val rettighetId: Long
)
