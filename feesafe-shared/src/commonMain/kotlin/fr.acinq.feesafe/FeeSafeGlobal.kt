/*
 * Copyright 2025 ACINQ SAS
 * Copyright 2026 ZenderBin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.acinq.feesafe

import fr.acinq.lightning.logging.LoggerFactory
import fr.acinq.lightning.logging.info
import fr.acinq.feesafe.db.SqliteAppDb
import fr.acinq.feesafe.db.createAppDbDriver
import fr.acinq.feesafe.managers.AppConnectionsDaemon
import fr.acinq.feesafe.managers.global.CurrencyManager
import fr.acinq.feesafe.managers.global.FeerateManager
import fr.acinq.feesafe.managers.global.NetworkMonitor
import fr.acinq.feesafe.managers.global.WalletContextManager
import fr.acinq.feesafe.utils.PlatformContext
import fr.acinq.feesafe.utils.logger.FeeSafeLoggerConfig


class FeeSafeGlobal(val ctx: PlatformContext) {

    // this logger factory will be used throughout the project (including dependencies like lightning-kmp) to
    // create new [Logger] instances, and output logs to platform dependent writers.
    val loggerFactory = LoggerFactory(FeeSafeLoggerConfig(ctx))
    private val logger = loggerFactory.newLogger(this::class)

    val appDb by lazy { SqliteAppDb(createAppDbDriver(ctx)) }
    val networkMonitor = NetworkMonitor(loggerFactory, ctx)
    val currencyManager by lazy { CurrencyManager(loggerFactory, appDb) }
    val feerateManager by lazy { FeerateManager(loggerFactory) }
    val walletContextManager = WalletContextManager(loggerFactory)

    init {
        logger.info { "init FeeSafeGlobal..." }
        feerateManager.startMonitoringFeerate()
        walletContextManager.startJobs()
    }

    /** Called by [AppConnectionsDaemon] when internet is available. */
    internal fun enableNetworkAccess() {
        feerateManager.startMonitoringFeerate()
        walletContextManager.startJobs()
        currencyManager.enableNetworkAccess()
    }

    /** Called by [AppConnectionsDaemon] when no connection is available. */
    internal fun disableNetworkAccess() {
        feerateManager.stopMonitoringFeerate()
        walletContextManager.stopJobs()
        currencyManager.disableNetworkAccess()
    }

}