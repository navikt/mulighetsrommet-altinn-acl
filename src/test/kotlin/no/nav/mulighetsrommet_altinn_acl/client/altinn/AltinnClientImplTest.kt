package no.nav.mulighetsrommet_altinn_acl.client.altinn

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import no.nav.mulighetsrommet_altinn_acl.domain.RolleType
import no.nav.mulighetsrommet_altinn_acl.utils.JsonUtils
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AltinnClientImplTest {
	private lateinit var mockServer: MockWebServer

	@BeforeEach
	fun start() {
		mockServer = MockWebServer()
		mockServer.start()
	}

	@AfterEach
	fun shutdown() {
		mockServer.shutdown()
	}

	private fun mockServerUrl(): String = mockServer.url("").toString().removeSuffix("/")

	@Test
	fun `hentAlleOrganisasjoner - 1 tilgang - kun et kall til Altinn`() {
		val altinnClient =
			AltinnClientImpl(
				baseUrl = mockServerUrl(),
				altinnApiKey = "api-key",
				maskinportenTokenProvider = { "TOKEN" },
			)

		val jsonResponse =
			"""
			[
				{
					"name": "LAGSPORT PLUTSELIG",
					"organizationNumber": null,
					"type": "Person",
					"authorizedResources": [],
					"subunits": []
				},
				{
					"name": "NONFIGURATIV KOMFORTABEL HUND DA",
					"type": "Organization",
					"organizationNumber": "999987004",
					"authorizedResources": [],
					"subunits": [
						{
							"name": "UEMOSJONELL KREATIV TIGER AS",
							"type": "Organization",
							"organizationNumber": "211267232",
							"authorizedResources": ["tiltak-arrangor-refusjon"],
							"subunits": []
						}
					]
				},
				{
					"name": "FRYKTLØS OPPSTEMT STRUTS LTD",
					"type": "Organization",
					"organizationNumber": "312899485",
					"authorizedResources": ["tiltak-arrangor-refusjon"],
					"subunits": []
				}
			]
			""".trimIndent()

		mockServer.enqueue(
			MockResponse()
				.setBody(jsonResponse)
				.setHeader("Content-Type", "application/json"),
		)

		val norskIdent = "123456"

		val organisasjoner = altinnClient.hentAlleOrganisasjoner(norskIdent)

		val request = mockServer.takeRequest()

		request.method shouldBe "POST"
		request.path shouldBe
			"/accessmanagement/api/v1/resourceowner/authorizedparties?includeAltinn2=true"
		request.headers["Ocp-Apim-Subscription-Key"] shouldBe "api-key"
		request.headers["Authorization"] shouldBe "Bearer TOKEN"

		organisasjoner shouldHaveSize 2
	}

	@Test
	fun `hentAlleOrganisasjoner - 505 tilganger - to kall til Altinn`() {
		val altinnClient =
			AltinnClientImpl(
				baseUrl = mockServerUrl(),
				altinnApiKey = "api-key",
				maskinportenTokenProvider = { "TOKEN" },
			)

		val jsonResponse =
			"""
			[
				{
					"name": "LAGSPORT PLUTSELIG",
					"organizationNumber": null,
					"type": "Person",
					"authorizedResources": [],
					"subunits": []
				},
				{
					"name": "NONFIGURATIV KOMFORTABEL HUND DA",
					"type": "Organization",
					"organizationNumber": "999987004",
					"authorizedResources": [],
					"subunits": [
						{
							"name": "UEMOSJONELL KREATIV TIGER AS",
							"type": "Organization",
							"organizationNumber": "211267232",
							"authorizedResources": ["tiltak-arrangor-refusjon"],
							"subunits": []
						}
					]
				},
				{
					"name": "FRYKTLØS OPPSTEMT STRUTS LTD",
					"type": "Organization",
					"organizationNumber": "312899485",
					"authorizedResources": ["tiltak-arrangor-refusjon"],
					"subunits": []
				}
			]
			""".trimIndent()

		mockServer.enqueue(
			MockResponse()
				.setBody(JsonUtils.objectMapper.writeValueAsString(getAltinnResponse()))
				.setHeader("Content-Type", "application/json"),
		)
		mockServer.enqueue(
			MockResponse()
				.setBody(jsonResponse)
				.setHeader("Content-Type", "application/json"),
		)

		val norskIdent = "123456"

		val organisasjoner = altinnClient.hentAlleOrganisasjoner(norskIdent)

		mockServer.requestCount shouldBe 2
		organisasjoner shouldHaveSize 503
	}
}

private fun getAltinnResponse(size: Int = 501): List<AltinnClientImpl.AuthorizedParty> {
	val response = mutableListOf<AltinnClientImpl.AuthorizedParty>()
	var i = 0
	while (response.size < size) {
		response.add(
			AltinnClientImpl.AuthorizedParty(
				type = "Type + $i",
				organisasjonsnummer = i.toString(),
				authorizedResources = listOf(RolleType.TILTAK_ARRANGOR_REFUSJON.ressursId),
				subunits = emptyList(),
			),
		)
		i++
	}
	return response
}
