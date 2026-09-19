/*
 * Copyright 2023 ACINQ SAS
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

package fr.acinq.feesafe.android.settings.walletinfo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.acinq.lightning.MilliSatoshi
import fr.acinq.lightning.blockchain.electrum.WalletState
import fr.acinq.lightning.blockchain.electrum.balance
import fr.acinq.lightning.utils.msat
import fr.acinq.lightning.utils.sat
import fr.acinq.lightning.utils.toMilliSatoshi
import fr.acinq.feesafe.FeeSafeBusiness
import fr.acinq.feesafe.android.R
import fr.acinq.feesafe.android.components.layouts.Card
import fr.acinq.feesafe.android.components.layouts.CardHeader
import fr.acinq.feesafe.android.components.layouts.DefaultScreenHeader
import fr.acinq.feesafe.android.components.layouts.DefaultScreenLayout
import fr.acinq.feesafe.android.components.buttons.FilledButton
import fr.acinq.feesafe.utils.extensions.confirmed

@Composable
fun FinalWalletInfo(
    business: FeeSafeBusiness,
    onBackClick: () -> Unit,
    onSpendClick: () -> Unit,
) {
    val finalWallet by business.peerManager.finalWallet.collectAsState()

    DefaultScreenLayout(isScrollable = false) {
        DefaultScreenHeader(onBackClick = onBackClick, title = stringResource(id = R.string.walletinfo_onchain_final), helpMessage = stringResource(id = R.string.walletinfo_onchain_final_about))
        ConfirmedBalanceView(balance = finalWallet?.confirmed?.balance?.toMilliSatoshi(), onSpendClick = onSpendClick)
        UnconfirmedWalletView(utxos = finalWallet?.unconfirmed.orEmpty())
    }
}

@Composable
private fun ConfirmedBalanceView(
    balance: MilliSatoshi?,
    onSpendClick: () -> Unit,
) {
    CardHeader(text = stringResource(id = R.string.walletinfo_confirmed_title))
    Card(
        internalPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        BalanceRow(balance = balance)
        if (balance != null && balance > 0.msat) {
            Spacer(modifier = Modifier.height(12.dp))
            FilledButton(
                text = stringResource(R.string.walletinfo_onchain_final_spend_button),
                icon = R.drawable.ic_send,
                onClick = onSpendClick,
            )
        }
    }
}

@Composable
private fun UnconfirmedWalletView(
    utxos: List<WalletState.Utxo>
) {
    if (utxos.balance > 0.sat) {
        CardHeader(text = stringResource(id = R.string.walletinfo_unconfirmed_title))
        Card {
            utxos.forEach {
                UtxoRow(it, null)
            }
        }
    }
}
