/*
 * Copyright 2020 ACINQ SAS
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

package fr.acinq.feesafe.android

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import fr.acinq.feesafe.FeeSafeBusiness
import fr.acinq.feesafe.android.utils.UserTheme
import fr.acinq.feesafe.android.utils.datastore.GlobalPrefs
import fr.acinq.feesafe.android.utils.datastore.InternalPrefs
import fr.acinq.feesafe.android.utils.datastore.PreferredBitcoinUnits
import fr.acinq.feesafe.android.utils.datastore.UserPrefs
import fr.acinq.feesafe.controllers.ControllerFactory
import fr.acinq.feesafe.data.*
import fr.acinq.feesafe.managers.AppConfigurationManager


typealias CF = ControllerFactory

val LocalTheme = staticCompositionLocalOf { UserTheme.SYSTEM }
val LocalWalletId = compositionLocalOf<WalletId?> { null }
val LocalBusiness = compositionLocalOf<FeeSafeBusiness?> { null }
val LocalUserPrefs = staticCompositionLocalOf<UserPrefs?> { null }
val LocalInternalPrefs = staticCompositionLocalOf<InternalPrefs?> { null }
val LocalControllerFactory = staticCompositionLocalOf<ControllerFactory?> { null }
val LocalNavController = staticCompositionLocalOf<NavController?> { null }
val LocalBitcoinUnits = compositionLocalOf { PreferredBitcoinUnits(primary = BitcoinUnit.Sat) }
val LocalFiatCurrencies = compositionLocalOf { PreferredFiatCurrencies(primary = FiatCurrency.USD, others = emptyList()) }
val LocalExchangeRatesMap = compositionLocalOf<Map<FiatCurrency, ExchangeRate.BitcoinPriceRate>> { emptyMap() }
val LocalShowInFiat = compositionLocalOf { false }
val isDarkTheme: Boolean
    @Composable
    get() = LocalTheme.current.let { it == UserTheme.DARK || (it == UserTheme.SYSTEM && isSystemInDarkTheme()) }

val navController: NavController
    @Composable
    get() = LocalNavController.current ?: error("navigation controller is not available")

val preferredAmountUnit: CurrencyUnit
    @Composable
    get() = if (LocalShowInFiat.current) LocalFiatCurrencies.current.primary else LocalBitcoinUnits.current.primary

val primaryFiatRate: ExchangeRate.BitcoinPriceRate?
    @Composable
    get() = LocalFiatCurrencies.current.primary.let { prefFiat -> LocalExchangeRatesMap.current[prefFiat] }

val globalPrefs: GlobalPrefs
    @Composable
    get() = application.globalPrefs

val application: FeeSafeApplication
    @Composable
    get() = LocalContext.current.applicationContext as? FeeSafeApplication ?: error("Application is not of type FeeSafeApplication. Are you using appView in preview?")
