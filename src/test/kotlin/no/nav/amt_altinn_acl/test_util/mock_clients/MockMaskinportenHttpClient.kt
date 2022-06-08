package no.nav.amt_altinn_acl.test_util.mock_clients

import no.nav.amt_altinn_acl.test_util.MockHttpClient
import no.nav.amt_altinn_acl.test_util.TokenCreator

class MockMaskinportenHttpClient : MockHttpClient() {

	fun enqueueTokenResponse() {
		val token = TokenCreator.instance().createToken()

		enqueue(
			headers = mapOf("Content-Type" to "application/json"),
			body = """{ "token_type": "Bearer", "access_token": "$token", "expires": 3600 }"""
		)
	}

}
