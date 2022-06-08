package no.nav.amt_altinn_acl.service

import no.nav.amt_altinn_acl.domain.Rettighet
import no.nav.amt_altinn_acl.repository.RettigheterCacheRepository
import no.nav.amt_altinn_acl.utils.JsonUtils
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class RettigheterCacheService(
	private val rettigheterCacheRepository: RettigheterCacheRepository
) {

	fun cacheRettigheter(norskIdent: String, rettigheter: List<Rettighet>) {
		val json = JsonUtils.toJsonString(CachetRettigheter(rettigheter = rettigheter))
		val expiration = ZonedDateTime.now().plusHours(1)

		rettigheterCacheRepository.upsertRettigheter(norskIdent, json, expiration)
	}

	fun hentAlleCachedeRettigheter(norskIdent: String): List<Rettighet>? {
		val cachetRettigheterDbo = rettigheterCacheRepository.hentRettigheter(norskIdent) ?: return null

		val hasExpired = cachetRettigheterDbo.expiresAfter.isAfter(ZonedDateTime.now())

		if (hasExpired) {
			rettigheterCacheRepository.slettRettigheter(cachetRettigheterDbo.id)
			return null
		}

		val cachetRettigheter = JsonUtils.fromJsonString<CachetRettigheter>(cachetRettigheterDbo.rettigheterJson)

		return cachetRettigheter.rettigheter
	}

	data class CachetRettigheter(
		val version: Int = 1,
		val rettigheter: List<Rettighet>
	)

}
