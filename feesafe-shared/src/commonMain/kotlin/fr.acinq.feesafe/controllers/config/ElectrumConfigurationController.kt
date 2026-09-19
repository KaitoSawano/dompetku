package fr.acinq.feesafe.controllers.config

import co.touchlab.kermit.Logger
import fr.acinq.lightning.blockchain.electrum.ElectrumClient
import fr.acinq.feesafe.FeeSafeBusiness
import fr.acinq.feesafe.managers.AppConfigurationManager
import fr.acinq.feesafe.managers.AppConnectionsDaemon
import fr.acinq.feesafe.controllers.AppController
import fr.acinq.lightning.logging.LoggerFactory
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


class AppElectrumConfigurationController(
    loggerFactory: LoggerFactory,
    private val configurationManager: AppConfigurationManager,
    private val electrumClient: ElectrumClient,
    private val appConnectionsDaemon: AppConnectionsDaemon?
) : AppController<ElectrumConfiguration.Model, ElectrumConfiguration.Intent>(
    loggerFactory = loggerFactory,
    firstModel = ElectrumConfiguration.Model()
) {
    constructor(business: FeeSafeBusiness): this(
        loggerFactory = business.loggerFactory,
        configurationManager = business.appConfigurationManager,
        electrumClient = business.electrumClient,
        appConnectionsDaemon = business.appConnectionsDaemon
    )

    init {
        launch {
            if (appConnectionsDaemon != null) {
                combine(
                    configurationManager.electrumConfig,
                    appConnectionsDaemon.lastElectrumServerAddress,
                    electrumClient.connectionStatus,
                    configurationManager.electrumMessages,
                    transform = { configState, currentServer, connectionStatus, message ->
                        ElectrumConfiguration.Model(
                            configuration = configState,
                            currentServer = currentServer,
                            connection = connectionStatus.toConnectionState(),
                            blockHeight = message?.blockHeight ?: 0,
                            tipTimestamp = message?.header?.time ?: 0,
                        )
                    }
                ).collect {
                    model(it)
                }
            }
        }
    }

    override fun process(intent: ElectrumConfiguration.Intent) {
        when (intent) {
            is ElectrumConfiguration.Intent.UpdateElectrumServer -> {
                configurationManager.updateElectrumConfig(intent.config)
            }
        }
    }
}
