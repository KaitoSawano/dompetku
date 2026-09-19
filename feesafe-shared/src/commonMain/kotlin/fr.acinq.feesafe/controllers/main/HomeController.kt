package fr.acinq.feesafe.controllers.main

import fr.acinq.lightning.logging.LoggerFactory
import fr.acinq.feesafe.FeeSafeBusiness
import fr.acinq.feesafe.controllers.AppController
import fr.acinq.feesafe.managers.BalanceManager
import kotlinx.coroutines.launch


class AppHomeController(
    loggerFactory: LoggerFactory,
    private val balanceManager: BalanceManager
) : AppController<Home.Model, Home.Intent>(
    loggerFactory = loggerFactory,
    firstModel = Home.emptyModel
) {
    constructor(business: FeeSafeBusiness): this(
        loggerFactory = business.loggerFactory,
        balanceManager = business.balanceManager
    )

    init {
        launch {
            balanceManager.balance.collect {
                model { copy(balance = it) }
            }
        }
    }

    override fun process(intent: Home.Intent) {}
}
