package no.nav.mulighetsrommet_altinn_acl.domain

data class RollerIOrganisasjon(
	val organisasjonsnummer: String,
	val roller: List<Rolle>,
)
