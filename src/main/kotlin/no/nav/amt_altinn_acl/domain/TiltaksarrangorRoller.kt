package no.nav.amt_altinn_acl.domain

data class TiltaksarrangorRoller(
	val organisasjonsnummer: String,
	val roller: List<TiltaksarrangorRolleType>,
)
