package no.nav.mulighetsrommet_altinn_acl.repository.dbo

import java.time.ZonedDateTime

data class PersonDbo(
	val id: Long,
	val norskIdent: String,
	val created: ZonedDateTime,
	val lastSynchronized: ZonedDateTime,
)
