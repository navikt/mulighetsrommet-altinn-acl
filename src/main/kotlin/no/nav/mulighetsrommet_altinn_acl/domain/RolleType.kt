package no.nav.mulighetsrommet_altinn_acl.domain

// TODO Erstatt med riktig ressursId istedenfor serviceCode
enum class RolleType(
	val ressursId: String,
) {
	TILTAK_ARRANGOR_REFUSJON("tiltak-arrangor-refusjon"),
}
