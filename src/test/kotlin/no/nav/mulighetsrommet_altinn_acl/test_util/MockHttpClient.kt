package no.nav.mulighetsrommet_altinn_acl.test_util

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.slf4j.LoggerFactory

open class MockHttpClient {
	private val server = MockWebServer()

	private val log = LoggerFactory.getLogger(javaClass)

	private var lastRequestCount = 0

	fun start() {
		try {
			server.start()
		} catch (e: IllegalArgumentException) {
			log.info("${javaClass.simpleName} is already started")
		}
	}

	fun resetRequestCount() {
		lastRequestCount = server.requestCount
	}

	fun serverUrl(): String = server.url("").toString().removeSuffix("/")

	fun enqueue(response: MockResponse) {
		server.enqueue(response)
	}

	fun enqueue(
		responseCode: Int = 200,
		headers: Map<String, String> = emptyMap(),
		body: String,
	) {
		val response =
			MockResponse()
				.setBody(body)
				.setResponseCode(responseCode)

		headers.forEach {
			response.addHeader(it.key, it.value)
		}

		server.enqueue(response)
	}

	fun dispatch(
		responseCode: Int = 200,
		headers: Map<String, String> = emptyMap(),
		body: String,
	) {
		server.dispatcher =
			object : Dispatcher() {
				override fun dispatch(request: RecordedRequest): MockResponse {
					val response =
						MockResponse()
							.setBody(body)
							.setResponseCode(responseCode)

					headers.forEach {
						response.addHeader(it.key, it.value)
					}
					return response
				}
			}
	}

	fun latestRequest(): RecordedRequest = server.takeRequest()

	fun requestCount(): Int = server.requestCount - lastRequestCount

	fun shutdown() {
		server.shutdown()
	}
}
