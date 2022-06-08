package no.nav.amt_altinn_acl.repository.dbo

import java.time.ZonedDateTime

data class RettigheterCacheDbo(
	val id: Long,
	val norskIdent: String,
	val rettigheterJson: String,
	val expiresAfter: ZonedDateTime,
	val createdAt: ZonedDateTime
)
