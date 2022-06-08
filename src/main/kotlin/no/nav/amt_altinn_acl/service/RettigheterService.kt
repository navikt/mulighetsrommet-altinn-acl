package no.nav.amt_altinn_acl.service

import no.nav.amt_altinn_acl.client.altinn.AltinnClient
import no.nav.amt_altinn_acl.domain.Rettighet
import org.springframework.stereotype.Service

@Service
class RettigheterService(
	private val altinnClient: AltinnClient,
	private val cacheService: RettigheterCacheService
) {

	fun hentAlleRettigheter(norskIdent: String): List<Rettighet> {
		val cachetRettigheter = cacheService.hentAlleCachedeRettigheter(norskIdent)

		if (cachetRettigheter != null) {
			return cachetRettigheter
		}

		val rettigheter = hentAlleRettigheterFraAltinn(norskIdent)

		cacheService.cacheRettigheter(norskIdent, rettigheter)

		return rettigheter
	}

	private fun hentAlleRettigheterFraAltinn(norskIdent: String): List<Rettighet> {
		val virksomheter = altinnClient.hentTilknyttedeOrganisasjoner(norskIdent)

		return virksomheter.parallelStream()
			.flatMap {
				altinnClient.hentRettigheter(norskIdent, it.organisasjonsnummer)
					.map { r -> Rettighet(r.rettighetId.toString(), it.organisasjonsnummer) }
					.stream()
			}
			.toList()
	}

}
