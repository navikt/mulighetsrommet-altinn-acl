package no.nav.mulighetsrommet_altinn_acl.client.altinn

interface AltinnClient {
	fun hentAlleOrganisasjoner(norskIdent: String): List<String>
}
