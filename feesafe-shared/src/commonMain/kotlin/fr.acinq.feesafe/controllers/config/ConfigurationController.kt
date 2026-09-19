package fr.acinq.feesafe.controllers.config

import co.touchlab.kermit.Logger
import fr.acinq.lightning.logging.LoggerFactory
import fr.acinq.feesafe.FeeSafeBusiness
import fr.acinq.feesafe.managers.WalletManager
import fr.acinq.feesafe.controllers.AppController
import kotlinx.coroutines.launch


class AppConfigurationController(
    loggerFactory: LoggerFactory,
    private val walletManager: WalletManager
) : AppController<Configuration.Model, Configuration.Intent>(
    loggerFactory = loggerFactory,
    firstModel = Configuration.Model.SimpleMode
) {
    constructor(business: FeeSafeBusiness): this(
        loggerFactory = business.loggerFactory,
        walletManager = business.walletManager
    )

    init {
        launch {
            model(
                if (!walletManager.isLoaded())
                    Configuration.Model.SimpleMode
                else
                    Configuration.Model.FullMode
            )
        }
    }

    override fun process(intent: Configuration.Intent) = error("Nothing to process")
}
