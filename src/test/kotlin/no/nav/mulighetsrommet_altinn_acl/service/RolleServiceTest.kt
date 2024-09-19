package no.nav.mulighetsrommet_altinn_acl.service

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import no.nav.mulighetsrommet_altinn_acl.domain.RolleType
import no.nav.mulighetsrommet_altinn_acl.domain.RolleType.KOORDINATOR
import no.nav.mulighetsrommet_altinn_acl.domain.RollerIOrganisasjon
import no.nav.mulighetsrommet_altinn_acl.repository.PersonRepository
import no.nav.mulighetsrommet_altinn_acl.repository.RolleRepository
import no.nav.mulighetsrommet_altinn_acl.test_util.IntegrationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class RolleServiceTest : IntegrationTest() {
	@Autowired
	lateinit var rolleService: RolleService

	@Autowired
	lateinit var personRepository: PersonRepository

	@Autowired
	lateinit var rolleRepository: RolleRepository

	@BeforeEach
	internal fun setUp() {
		mockMaskinportenHttpClient.enqueueTokenResponse()
		mockAltinnHttpClient.resetHttpServer()
	}

	@Test
	internal fun `getRollerForPerson - not exist - create person and get roller from altinn`() {
		val norskIdent = UUID.randomUUID().toString()
		val organisasjonsnummer = UUID.randomUUID().toString()

		mockAltinnHttpClient.addReporteeResponse(norskIdent, KOORDINATOR.serviceCode, listOf(organisasjonsnummer))

		val roller = rolleService.getRollerForPerson(norskIdent)

		mockAltinnHttpClient.requestCount() shouldBe 1

		roller.size shouldBe 1

		hasRolle(roller, organisasjonsnummer, KOORDINATOR) shouldBe true

		val databasePerson = personRepository.get(norskIdent)!!
		databasePerson.lastSynchronized.days() shouldBe ZonedDateTime.now().days()

		hasRolleInDatabase(databasePerson.id, organisasjonsnummer, KOORDINATOR) shouldBe true
	}

	@Test
	internal fun `getRollerForPerson - not exist and no roller in Altinn - don't save person`() {
		val norskIdent = UUID.randomUUID().toString()

		mockAltinnHttpClient.addReporteeResponse(norskIdent, KOORDINATOR.serviceCode, emptyList())

		val roller = rolleService.getRollerForPerson(norskIdent)

		mockAltinnHttpClient.requestCount() shouldBe 1
		roller.size shouldBe 0
		personRepository.get(norskIdent) shouldBe null
	}

	@Test
	internal fun `getRollerForPerson - exists - has rolle - return cached rolle if under cacheTime`() {
		val norskIdent = UUID.randomUUID().toString()
		val organisasjonsnummer = UUID.randomUUID().toString()

		val personDbo = personRepository.create(norskIdent)
		personRepository.setSynchronized(norskIdent)

		rolleRepository.createRolle(
			personId = personDbo.id,
			organisasjonsnummer = organisasjonsnummer,
			rolleType = KOORDINATOR,
		)

		rolleService.getRollerForPerson(norskIdent)
		mockAltinnHttpClient.requestCount() shouldBe 0
	}

	@Test
	internal fun `getRollerForPerson - exists - has no roller - should check altinn`() {
		val norskIdent = UUID.randomUUID().toString()
		val organisasjonsnummer = UUID.randomUUID().toString()

		personRepository.create(norskIdent)
		personRepository.setSynchronized(norskIdent)

		mockAltinnHttpClient.addReporteeResponse(norskIdent, KOORDINATOR.serviceCode, listOf(organisasjonsnummer))

		val roller = rolleService.getRollerForPerson(norskIdent)

		hasRolle(roller, organisasjonsnummer, KOORDINATOR) shouldBe true

		mockAltinnHttpClient.requestCount() shouldBe 1
	}

	@Test
	internal fun `getRollerForPerson - exists - has lost rolle in altinn`() {
		val norskIdent = UUID.randomUUID().toString()
		val organisasjonsnummer = UUID.randomUUID().toString()

		val personDbo = personRepository.create(norskIdent)
		rolleRepository.createRolle(personDbo.id, organisasjonsnummer, KOORDINATOR)

		mockAltinnHttpClient.addReporteeResponse(norskIdent, KOORDINATOR.serviceCode, listOf())

		val roller = rolleService.getRollerForPerson(norskIdent)

		hasRolle(roller, organisasjonsnummer, KOORDINATOR) shouldBe false

		val invalidKoordinator = rolleRepository.hentRollerForPerson(personDbo.id).find { it.rolleType == KOORDINATOR }!!

		invalidKoordinator.validTo shouldNotBe null
	}

	@Test
	internal fun `getRollerForPerson - exists - has gained rolle in altinn`() {
		val norskIdent = UUID.randomUUID().toString()
		val organisasjonsnummer = UUID.randomUUID().toString()

		val personDbo = personRepository.create(norskIdent)
		rolleRepository.createRolle(personDbo.id, organisasjonsnummer, KOORDINATOR)

		mockAltinnHttpClient.addReporteeResponse(norskIdent, KOORDINATOR.serviceCode, listOf(organisasjonsnummer))

		val roller = rolleService.getRollerForPerson(norskIdent)

		hasRolle(roller, organisasjonsnummer, KOORDINATOR) shouldBe true
	}

	@Test
	internal fun `getRollerForPerson - exists - has regained rolle in altinn`() {
		val norskIdent = UUID.randomUUID().toString()
		val organisasjonsnummer = UUID.randomUUID().toString()

		val personDbo = personRepository.create(norskIdent)
		rolleRepository.createRolle(personDbo.id, organisasjonsnummer, KOORDINATOR)

		mockAltinnHttpClient.addReporteeResponse(norskIdent, KOORDINATOR.serviceCode, listOf())

		val roller = rolleService.getRollerForPerson(norskIdent)

		roller.isEmpty() shouldBe true

		mockAltinnHttpClient.resetHttpServer()
		mockAltinnHttpClient.addReporteeResponse(norskIdent, KOORDINATOR.serviceCode, listOf(organisasjonsnummer))

		val updatedRoller = rolleService.getRollerForPerson(norskIdent)

		hasRolle(updatedRoller, organisasjonsnummer, KOORDINATOR) shouldBe true

		val databaseRoller =
			rolleRepository
				.hentRollerForPerson(personDbo.id)
				.filter { it.rolleType == KOORDINATOR }

		databaseRoller.size shouldBe 2
	}

	@Test
	internal fun `getRollerForPerson - Altinn down - returns cached roller`() {
		val norskIdent = UUID.randomUUID().toString()
		val organisasjonsnummer = UUID.randomUUID().toString()
		val personDbo = personRepository.create(norskIdent)

		rolleRepository.createRolle(personDbo.id, organisasjonsnummer, KOORDINATOR)

		mockAltinnHttpClient.addFailureResponse(norskIdent, KOORDINATOR.serviceCode, 500)

		val roller = rolleService.getRollerForPerson(norskIdent)

		mockAltinnHttpClient.requestCount() shouldBe 1
		hasRolle(roller, organisasjonsnummer, KOORDINATOR) shouldBe true

		val updatedPersonDbo = personRepository.get(norskIdent)

		updatedPersonDbo!!.lastSynchronized.days() shouldNotBe ZonedDateTime.now().days()
	}

	private fun hasRolleInDatabase(
		personId: Long,
		organisasjonsnummerNumber: String,
		rolle: RolleType,
	): Boolean =
		rolleRepository
			.hentRollerForPerson(personId)
			.filter { it.erGyldig() }
			.find { it.organisasjonsnummer == organisasjonsnummerNumber && it.rolleType == rolle } != null

	private fun hasRolle(
		list: List<RollerIOrganisasjon>,
		organisasjonsnummerNumber: String,
		rolle: RolleType,
	): Boolean =
		list
			.find { it.organisasjonsnummer == organisasjonsnummerNumber }
			?.roller
			?.find { it.rolleType == rolle } != null

	private fun ZonedDateTime.days(): ZonedDateTime = this.truncatedTo(ChronoUnit.DAYS)
}
