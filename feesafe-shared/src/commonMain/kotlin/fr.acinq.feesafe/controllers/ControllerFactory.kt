package fr.acinq.feesafe.controllers

import fr.acinq.feesafe.controllers.config.*
import fr.acinq.feesafe.controllers.init.Initialization
import fr.acinq.feesafe.controllers.init.RestoreWallet
import fr.acinq.feesafe.controllers.main.Content
import fr.acinq.feesafe.controllers.main.Home
import fr.acinq.feesafe.controllers.payments.Receive

typealias ContentController = MVI.Controller<Content.Model, Content.Intent>
typealias HomeController = MVI.Controller<Home.Model, Home.Intent>
typealias InitializationController = MVI.Controller<Initialization.Model, Initialization.Intent>
typealias ReceiveController = MVI.Controller<Receive.Model, Receive.Intent>
typealias RestoreWalletController = MVI.Controller<RestoreWallet.Model, RestoreWallet.Intent>

typealias CloseChannelsConfigurationController = MVI.Controller<CloseChannelsConfiguration.Model, CloseChannelsConfiguration.Intent>
typealias ConfigurationController = MVI.Controller<Configuration.Model, Configuration.Intent>
typealias ElectrumConfigurationController = MVI.Controller<ElectrumConfiguration.Model, ElectrumConfiguration.Intent>

/** Lets us define different implementation for the controllers, which is useful for mocks. */
interface ControllerFactory {
    fun content(): ContentController
    fun initialization(): InitializationController
    fun home(): HomeController
    fun receive(): ReceiveController
    fun restoreWallet(): RestoreWalletController
    fun configuration(): ConfigurationController
    fun electrumConfiguration(): ElectrumConfigurationController
    fun closeChannelsConfiguration(): CloseChannelsConfigurationController
    fun forceCloseChannelsConfiguration(): CloseChannelsConfigurationController
}
