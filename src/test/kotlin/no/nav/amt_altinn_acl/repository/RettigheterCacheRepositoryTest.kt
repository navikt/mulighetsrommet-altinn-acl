package no.nav.amt_altinn_acl.repository

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import no.nav.amt_altinn_acl.domain.Rettighet
import no.nav.amt_altinn_acl.service.RettigheterCacheService
import no.nav.amt_altinn_acl.test_util.DbTestDataUtils
import no.nav.amt_altinn_acl.test_util.DbTestDataUtils.shouldBeEqualTo
import no.nav.amt_altinn_acl.test_util.SingletonPostgresContainer
import no.nav.amt_altinn_acl.utils.JsonUtils.toJsonString
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.time.ZonedDateTime

class RettigheterCacheRepositoryTest {

	private val dataSource = SingletonPostgresContainer.getDataSource()

	private val repository = RettigheterCacheRepository(NamedParameterJdbcTemplate(dataSource))

	@AfterEach
	fun cleanup() {
		DbTestDataUtils.cleanDatabase(dataSource)
	}

	@Test
	fun `skal upserte og hente rettigheter`() {
		val norskIdent = "1243234"

		val data = RettigheterCacheService.CachetRettigheter(
			1,
			listOf(Rettighet("123", "4367842"))
		)

		val expiration = ZonedDateTime.now().plusHours(12)

		repository.upsertRettigheter(norskIdent, toJsonString(data), expiration)

		val cachetRettighet = repository.hentRettigheter(norskIdent)

		val expectedJson = """
			{"version":1,"rettigheter":[{"rettighetId":"123","organisasjonsnummmer":"4367842"}]}
		""".trimIndent()

		cachetRettighet shouldNotBe null
		cachetRettighet?.rettigheterJson shouldBe expectedJson
	}

	@Test
	fun `upsert skal oppdatere rettigheter og expiration`() {
		val norskIdent = "1243234"

		val oldData = RettigheterCacheService.CachetRettigheter(
			1,
			listOf(Rettighet("123", "4367842"))
		)

		val newData = RettigheterCacheService.CachetRettigheter(
			1,
			listOf(Rettighet("784932", "11111111"))
		)

		val oldExpiration = ZonedDateTime.now().plusHours(1)
		val newExpiration = ZonedDateTime.now().plusHours(12)

		repository.upsertRettigheter(norskIdent, toJsonString(oldData), oldExpiration)
		repository.upsertRettigheter(norskIdent, toJsonString(newData), newExpiration)

		val cachetRettighet = repository.hentRettigheter(norskIdent)

		val expectedJson = """
			{"version":1,"rettigheter":[{"rettighetId":"784932","organisasjonsnummmer":"11111111"}]}
		""".trimIndent()

		cachetRettighet shouldNotBe null
		cachetRettighet!!.rettigheterJson shouldBe expectedJson
		cachetRettighet.expiresAfter shouldBeEqualTo newExpiration
	}

	@Test
	fun `slettRettigheter - skal slette rettigheter`() {
		val norskIdent = "1243234"

		val data = RettigheterCacheService.CachetRettigheter(
			1,
			listOf(Rettighet("123", "4367842"))
		)

		val expiration = ZonedDateTime.now().plusHours(12)

		repository.upsertRettigheter(norskIdent, toJsonString(data), expiration)

		repository.slettRettigheter(norskIdent)

		val cachetRettighet = repository.hentRettigheter(norskIdent)

		cachetRettighet shouldBe null
	}

}
