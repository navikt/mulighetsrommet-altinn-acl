package no.nav.amt_altinn_acl.controller

import io.kotest.matchers.shouldBe
import no.nav.amt_altinn_acl.test_util.IntegrationTest
import no.nav.amt_altinn_acl.utils.RestUtils.toJsonRequestBody
import org.junit.jupiter.api.Test

class RettigheterControllerIntegrationTest : IntegrationTest() {

	@Test
	fun `hentAlleRettigheter - should return 401 when not authenticated`() {
		val response = sendRequest(
			method = "POST",
			path = "/api/v1/rettighet/hent-alle",
			body = """{"norskIdent": "4273684"}"""".toJsonRequestBody()
		)

		response.code shouldBe 401
	}

	@Test
	fun `hentAlleRettigheter - should return 403 when not machine-to-machine request`() {
		val response = sendRequest(
			method = "POST",
			path = "/api/v1/rettighet/hent-alle",
			body = """{"norskIdent": "4273684"}"""".toJsonRequestBody(),
			headers = mapOf("Authorization" to "Bearer ${oAuthServer.issueAzureAdToken()}")
		)

		response.code shouldBe 403
	}

	@Test
	fun `hentAlleRettigheter - should return 200 with correct response`() {
		val response = sendRequest(
			method = "POST",
			path = "/api/v1/rettighet/hent-alle",
			body = """{"norskIdent": "4273684"}"""".toJsonRequestBody(),
			headers = mapOf("Authorization" to "Bearer ${oAuthServer.issueAzureAdM2MToken()}")
		)

		val expectedJson = """
			{"rettigheter":[]}
		""".trimIndent()

		response.body?.string() shouldBe expectedJson
		response.code shouldBe 200
	}

}
