package no.nav.mulighetsrommet_altinn_acl.client.altinn

import com.fasterxml.jackson.annotation.JsonAlias
import no.nav.common.rest.client.RestClient
import no.nav.mulighetsrommet_altinn_acl.domain.RolleType
import no.nav.mulighetsrommet_altinn_acl.utils.JsonUtils.fromJsonString
import no.nav.mulighetsrommet_altinn_acl.utils.SecureLog.secureLog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory

const val pagineringSize = 500

class AltinnClientImpl(
	private val baseUrl: String,
	private val altinnApiKey: String,
	private val maskinportenTokenProvider: () -> String,
	private val client: OkHttpClient = RestClient.baseClient(),
) : AltinnClient {
	private val log = LoggerFactory.getLogger(javaClass)

	override fun hentAlleOrganisasjoner(norskIdent: String): List<String> {
		val organisasjoner = HashSet<String>()
		var ferdig = false
		while (!ferdig) {
			log.info("Henter organisasjoner fra Altinn")
			val hentedeOrganisasjoner = hentAlleOrganisasjonerFraAltinnForBruker(norskIdent)
			organisasjoner.addAll(hentedeOrganisasjoner)
			ferdig = hentedeOrganisasjoner.size < pagineringSize
		}
		return organisasjoner.toList()
	}

	private fun hentAlleOrganisasjonerFraAltinnForBruker(norskIdent: String): List<String> {
		val request =
			Request
				.Builder()
				.url(
					"$baseUrl/accessmanagement/api/v1/resourceowner/authorizedparties?includeAltinn2=true",
				).addHeader("Ocp-Apim-Subscription-Key", altinnApiKey)
				.addHeader("Authorization", "Bearer ${maskinportenTokenProvider.invoke()}")
				.addHeader("Content-Type", "application/json")
				.addHeader("accept", "application/json")
				.post(
					"""
					{
						"type": "urn:altinn:person:identifier-no",
						"value": "$norskIdent"
					}
					""".trimIndent()
						.toRequestBody(contentType = "application/json".toMediaTypeOrNull()),
				).build()

		client.newCall(request).execute().use { response ->
			if (!response.isSuccessful) {
				secureLog.error(
					"Klarte ikke å hente organisasjoner for norskIdent=$norskIdent message=${response.message}, code=${response.code}, body=${response.body?.string()}",
				)
				log.error("Klarte ikke hente organisasjoner for Altinn. response: ${response.code}")
				throw RuntimeException("Klarte ikke å hente organisasjoner code=${response.code}")
			}

			val body =
				response.body?.string()
					?: throw RuntimeException("Body is missing")

			if (!response.headers["X-Warning-LimitReached"].isNullOrEmpty()) {
				secureLog.warn("Bruker med norskIdent=$norskIdent har for mange tilganger. Kunne ikke hente alle tilgangene for bruker.")
			}

			val authorizedParties = fromJsonString<List<AuthorizedParty>>(body)
			println("authorizedParties: $authorizedParties") // TODO Fjern meg
			return getAllOrganizationNumbers(authorizedParties)
		}
	}

	data class AuthorizedParty(
		@JsonAlias("organizationNumber")
		val organisasjonsnummer: String? = null,
		val type: String,
		val authorizedResources: List<String>, // TODO Kan vi type denne til vår ressurs-id?
		val subunits: List<AuthorizedParty>,
	)

	private fun getAllOrganizationNumbers(parties: List<AuthorizedParty>): List<String> {
		val organizationNumbers = mutableListOf<String>()
		for (party in parties) {
			if (party.authorizedResources.contains(RolleType.TILTAK_ARRANGOR_REFUSJON.ressursId)) {
				party.organisasjonsnummer?.let { organizationNumbers.add(it) }
			}
			organizationNumbers.addAll(getAllOrganizationNumbers(party.subunits))
		}
		log.info("Hentet ${organizationNumbers.size} organisasjonsnummer fra Altinn: ${organizationNumbers.joinToString(", ")}") // TODO Fjern meg
		return organizationNumbers
	}
}
