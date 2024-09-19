package no.nav.mulighetsrommet_altinn_acl.repository.dbo

import no.nav.mulighetsrommet_altinn_acl.domain.RolleType
import java.time.ZonedDateTime

data class RolleDbo(
	val id: Long,
	val personId: Long,
	val organisasjonsnummer: String,
	val rolleType: RolleType,
	val validFrom: ZonedDateTime,
	val validTo: ZonedDateTime?,
) {
	fun erGyldig(): Boolean = validTo == null
}
