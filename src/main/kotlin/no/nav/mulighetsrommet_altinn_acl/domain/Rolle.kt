package no.nav.mulighetsrommet_altinn_acl.domain

import java.time.ZonedDateTime

data class Rolle(
	val id: Long,
	val rolleType: RolleType,
	val validFrom: ZonedDateTime,
	val validTo: ZonedDateTime?,
)
