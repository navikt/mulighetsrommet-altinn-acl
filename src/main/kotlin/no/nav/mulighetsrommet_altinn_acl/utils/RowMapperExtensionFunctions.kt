package no.nav.mulighetsrommet_altinn_acl.utils

import java.sql.ResultSet
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun ResultSet.getNullableZonedDateTime(columnLabel: String): ZonedDateTime? {
	val timestamp = this.getTimestamp(columnLabel) ?: return null
	return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp.time), ZoneOffset.systemDefault())
}

fun ResultSet.getZonedDateTime(columnLabel: String): ZonedDateTime =
	getNullableZonedDateTime(columnLabel) ?: throw IllegalStateException("Expected $columnLabel not to be null")
