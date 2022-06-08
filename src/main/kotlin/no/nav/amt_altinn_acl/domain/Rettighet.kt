package no.nav.amt_altinn_acl.domain

data class Rettighet(
	val rettighetId: String, // Er representert som et heltall i Altinn, men behandles som en String her for mer fleksibilitet
	val organisasjonsnummmer: String
)
