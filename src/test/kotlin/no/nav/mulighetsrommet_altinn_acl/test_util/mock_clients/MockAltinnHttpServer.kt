package no.nav.mulighetsrommet_altinn_acl.test_util.mock_clients

import okhttp3.mockwebserver.MockResponse

class MockAltinnHttpServer : MockHttpServer(name = "Altinn Mock Server") {
	fun addReporteeResponse(
		personIdent: String,
		organisasjonnummer: List<String>,
	) {
		addResponseHandler(
			path = "/accessmanagement/api/v1/resourceowner/authorizedparties?includeAltinn2=true",
			generateReporteeResponse(personIdent, organisasjonnummer),
		)
	}

	fun addFailureResponse(responseCode: Int) {
		addResponseHandler(
			path = "/accessmanagement/api/v1/resourceowner/authorizedparties?includeAltinn2=true",
			response = MockResponse().setResponseCode(responseCode),
		)
	}

	private fun generateReporteeResponse(
		personId: String,
		organisasjonnummer: List<String>,
	): MockResponse {
		val body =
			if (organisasjonnummer.isEmpty()) {
				"""
				[
					${generatePersonJson(personId)}
				]
				""".trimIndent()
			} else {
				StringBuilder()
					.append("[")
					.append(generatePersonJson(personId))
					.append(",")
					.append(
						organisasjonnummer.joinToString(",") {
							"""
							{
								"name": "UEMOSJONELL KREATIV TIGER AS",
								"type": "Organization",
								"organizationNumber": "$it",
								"authorizedResources": ["tiltak-arrangor-refusjon"],
								"subunits": []
							}
							""".trimIndent()
						},
					).append("]")
					.toString()
			}

		return MockResponse()
			.setResponseCode(200)
			.setBody(body)
	}

	private fun generatePersonJson(personIdent: Any): String =
		"""
		{
			"name": "LAGSPORT PLUTSELIG ",
			"type": "Person",
			"personId": "$personIdent",
			"authorizedResources": [],
			"subunits": []
		}
		""".trimIndent()
}
