package no.nav.amt_altinn_acl.config

import no.nav.common.rest.filter.LogRequestFilter
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableJwtTokenValidation
class ApplicationConfig {

	@Bean
	fun logFilterRegistrationBean(): FilterRegistrationBean<LogRequestFilter> {
		val registration = FilterRegistrationBean<LogRequestFilter>()
		registration.filter = LogRequestFilter("amt-altinn-acl", false)
		registration.order = 1
		registration.addUrlPatterns("/*")
		return registration
	}

}
