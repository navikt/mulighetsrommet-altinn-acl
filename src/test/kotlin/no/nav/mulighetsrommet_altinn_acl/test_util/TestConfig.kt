package no.nav.mulighetsrommet_altinn_acl.test_util

import no.nav.common.token_client.client.MachineToMachineTokenClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestConfig {
	@Bean
	fun machineToMachineTokenClient(): MachineToMachineTokenClient = MachineToMachineTokenClient { "TOKEN" }
}
