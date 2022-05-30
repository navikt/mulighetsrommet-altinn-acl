package no.nav.amt_altinn_acl.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.env")
data class EnvironmentProperties (
	var altinnUrl: String = "",
	var altinnApiKey: String = "",
	var maskinportenScopes: String = "",
	var maskinportenClientId: String = "",
	var maskinportenIssuer: String = "",
	var maskinportenTokenEndpoint: String = "",
	var maskinportenClientJwk: String = "",
)
