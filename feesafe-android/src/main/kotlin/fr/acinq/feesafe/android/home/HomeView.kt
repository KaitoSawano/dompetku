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

package fr.acinq.feesafe.android.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId
import fr.acinq.lightning.blockchain.electrum.balance
import fr.acinq.lightning.utils.UUID
import fr.acinq.lightning.utils.sat
import fr.acinq.feesafe.FeeSafeBusiness
import fr.acinq.feesafe.android.LocalUserPrefs
import fr.acinq.feesafe.android.NoticesViewModel
import fr.acinq.feesafe.android.PaymentsViewModel
import fr.acinq.feesafe.android.R
import fr.acinq.feesafe.android.WalletId
import fr.acinq.feesafe.android.application
import fr.acinq.feesafe.android.components.PrimarySeparator
import fr.acinq.feesafe.android.components.buttons.FilledButton
import fr.acinq.feesafe.android.components.buttons.MutedFilledButton
import fr.acinq.feesafe.android.components.buttons.TransparentFilledButton
import fr.acinq.feesafe.android.components.buttons.openLink
import fr.acinq.feesafe.android.components.dialogs.ModalBottomSheet
import fr.acinq.feesafe.android.home.releasenotes.ReleaseNoteDialog
import fr.acinq.feesafe.android.navController
import fr.acinq.feesafe.android.navigation.Screen
import fr.acinq.feesafe.android.utils.FCMHelper
import fr.acinq.feesafe.android.utils.datastore.getHomeAmountDisplayMode
import fr.acinq.feesafe.android.utils.extensions.findActivity
import fr.acinq.feesafe.data.canRequestLiquidity
import fr.acinq.feesafe.data.inFlightPaymentsCount

@Composable
fun HomeView(
    walletId: WalletId,
    business: FeeSafeBusiness,
    paymentsViewModel: PaymentsViewModel,
    noticesViewModel: NoticesViewModel,
    onPaymentClick: (UUID) -> Unit,
    onSettingsClick: () -> Unit,
    onReceiveClick: () -> Unit,
    onSendClick: () -> Unit,
    onPaymentsHistoryClick: () -> Unit,
    onTorClick: () -> Unit,
    onElectrumClick: () -> Unit,
    onNavigateToSwapInWallet: () -> Unit,
    onNavigateToFinalWallet: () -> Unit,
    onShowNotifications: () -> Unit,
    onRequestLiquidityClick: () -> Unit,
) {
    val context = LocalContext.current

    val isPowerSaverModeOn = noticesViewModel.isPowerSaverModeOn
    val fcmTokenFlow = application.globalPrefs.getFcmToken.collectAsState(initial = "")
    val isFCMAvailable = remember { FCMHelper.isFCMAvailable(context) }
    val balanceDisplayMode by LocalUserPrefs.current.getHomeAmountDisplayMode()

    val connections by business.connectionsManager.connections.collectAsState()
    val channels by business.peerManager.channelsFlow.collectAsState()
    val inFlightPaymentsCount = remember(channels) { channels.inFlightPaymentsCount() }

    var showConnectionsDialog by remember { mutableStateOf(false) }
    if (showConnectionsDialog) {
        ConnectionDialog(
            connections = connections,
            onClose = { showConnectionsDialog = false },
            onTorClick = onTorClick,
            onElectrumClick = onElectrumClick
        )
    }

    var showTorDisconnectedDialog by remember { mutableStateOf(false) }
    if (showTorDisconnectedDialog) {
        TorDisconnectedDialog(onDismiss = { showTorDisconnectedDialog = false })
    }

    val payments by paymentsViewModel.homePaymentsFlow.collectAsState()
    val swapInBalance = business.balanceManager.swapInWalletBalance.collectAsState()
    val swapInNextTimeout = business.peerManager.swapInNextTimeout.collectAsState(null)
    val finalWallet = business.peerManager.finalWallet.collectAsState()

    BackHandler {
        context.findActivity().moveTaskToBack(false)
    }

    val defaultHeight = 240.dp
    val collapsedHeight = 0.dp

    val motionScene = MotionScene {
        val collapsibleRef = createRefFor("collapsible")
        val topBarRef = createRefFor("topBar")
        val balanceRef = createRefFor("balance")
        val actionsRef = createRefFor("actions")
        val separatorRef = createRefFor("separator")
        val noticesRef = createRefFor("notices")

        val startConstraint = constraintSet {
            constrain(collapsibleRef) {
                top.linkTo(parent.top)
                height = Dimension.value(defaultHeight)
            }
            constrain(topBarRef) {
                top.linkTo(parent.top, margin = 8.dp)
                alpha = 1f
            }
            constrain(balanceRef) {
                top.linkTo(topBarRef.bottom, margin = 16.dp)
                centerHorizontallyTo(collapsibleRef)
                alpha = 1f
            }
            constrain(actionsRef) {
                top.linkTo(balanceRef.bottom, margin = 16.dp)
                centerHorizontallyTo(collapsibleRef)
                alpha = 1f
            }
            constrain(separatorRef) {
                bottom.linkTo(collapsibleRef.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                alpha = 1f
            }
            constrain(noticesRef) {
                top.linkTo(separatorRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, margin = 8.dp)
            }
        }
        val endConstraint = constraintSet {
            constrain(collapsibleRef) {
                top.linkTo(parent.top)
                height = Dimension.value(collapsedHeight)
            }
            constrain(topBarRef) {
                bottom.linkTo(parent.top)
                alpha = 0f
            }
            constrain(balanceRef) {
                bottom.linkTo(parent.top)
                centerHorizontallyTo(parent)
                alpha = 0f
            }
            constrain(actionsRef) {
                bottom.linkTo(parent.top)
                alpha = 0f
            }
            constrain(separatorRef) {
                bottom.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                alpha = 0f
            }
            constrain(noticesRef) {
                top.linkTo(separatorRef.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
        }
        transition(startConstraint, endConstraint, "default") {}
    }

    val maxPx = with(LocalDensity.current) { defaultHeight.roundToPx().toFloat() }
    val minPx = with(LocalDensity.current) { collapsedHeight.roundToPx().toFloat() }
    val collapsibleHeight = remember { mutableStateOf(maxPx) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            val max = maxPx
            val min = minPx
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val height = collapsibleHeight.value

                if (height + available.y > max) {
                    collapsibleHeight.value = max
                    return Offset(0f, max - height)
                }

                if (height + available.y < min) {
                    collapsibleHeight.value = min
                    return Offset(0f, min - height)
                }

                collapsibleHeight.value += available.y
                return Offset(0f, available.y)
            }
        }
    }

    val progress = 1 - (collapsibleHeight.value - minPx) / (maxPx - minPx)

    val balance by business.balanceManager.balance.collectAsState()
    val notices = noticesViewModel.notices
    val notifications by business.notificationsManager.notifications.collectAsState(emptyList())
    val walletContext by application.feesafeGlobal.walletContextManager.walletContext.collectAsState()

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        MotionLayout(
            motionScene = motionScene,
            progress = progress
        ) {
            Box(
                modifier = Modifier
                    .layoutId("collapsible")
                    .fillMaxWidth()
            ) {}
            TopBar(
                modifier = Modifier.layoutId("topBar"),
                onConnectionsStateButtonClick = { showConnectionsDialog = true },
                connections = connections,
                inFlightPaymentsCount = inFlightPaymentsCount,
                onTorClick = onTorClick,
                isFCMUnavailable = fcmTokenFlow.value == null || !isFCMAvailable,
                isPowerSaverMode = isPowerSaverModeOn,
                showRequestLiquidity = walletContext?.isManualLiquidityEnabled == true && channels.canRequestLiquidity(),
                onRequestLiquidityClick = onRequestLiquidityClick,
                onSettingsClick = onSettingsClick,
            )
            HomeBalance(
                modifier = Modifier.layoutId("balance"),
                channels = channels,
                balance = balance,
                balanceDisplayMode = balanceDisplayMode,
                swapInBalance = swapInBalance.value,
                swapInNextTimeout = swapInNextTimeout.value,
                finalWalletBalance = finalWallet.value?.all?.balance ?: 0.sat,
                onNavigateToSwapInWallet = onNavigateToSwapInWallet,
                onNavigateToFinalWallet = onNavigateToFinalWallet,
            )
            
            HomeActions(
                modifier = Modifier.layoutId("actions"),
                onSendClick = onSendClick,
                onReceiveClick = onReceiveClick,
            )

            PrimarySeparator(modifier = Modifier.layoutId("separator"))
            HomeNotices(
                modifier = Modifier.layoutId("notices"),
                notices = notices.toList(),
                notifications = notifications,
                onNavigateToSwapInWallet = onNavigateToSwapInWallet,
                onNavigateToNotificationsList = onShowNotifications,
                onShowTorDisconnectedClick = { showTorDisconnectedDialog = true }
            )
        }

        LatestPaymentsList(
            modifier = Modifier.nestedScroll(nestedScrollConnection),
            balanceDisplayMode = balanceDisplayMode,
            onPaymentClick = onPaymentClick,
            onPaymentsHistoryClick = onPaymentsHistoryClick,
            payments = payments,
        )
    }

    val releaseNoteCode = application.globalPrefs.showReleaseNoteSinceCode.collectAsState(initial = null).value
    releaseNoteCode?.let { ReleaseNoteDialog(sinceCode = it) }
}

@Composable
fun HomeActions(
    modifier: Modifier = Modifier,
    onSendClick: () -> Unit,
    onReceiveClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledButton(
            text = "Send",
            icon = R.drawable.ic_send,
            onClick = onSendClick,
            modifier = Modifier.weight(1f)
        )
        FilledButton(
            text = "Receive",
            icon = R.drawable.ic_receive,
            onClick = onReceiveClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TorDisconnectedDialog(
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismiss = onDismiss) {
        val navController = navController
        val context = LocalContext.current

        Text(text = stringResource(R.string.tor_disconnected_dialog_title), style = MaterialTheme.typography.h4)
        Spacer(Modifier.height(8.dp))
        Text(text = stringResource(R.string.tor_disconnected_dialog_details_1))
        Spacer(Modifier.height(8.dp))
        Text(text = stringResource(R.string.tor_disconnected_dialog_details_2))
        Spacer(Modifier.height(8.dp))
        Text(text = stringResource(R.string.tor_disconnected_dialog_details_3))
        Spacer(Modifier.height(24.dp))
        MutedFilledButton(
            text = stringResource(R.string.tor_disconnected_dialog_open_settings),
            icon = R.drawable.ic_settings,
            onClick = { navController.navigate(Screen.BusinessNavGraph.TorConfig.route) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )
        Spacer(Modifier.height(8.dp))
        TransparentFilledButton(
            text = stringResource(R.string.tor_disconnected_dialog_open_orbot_page),
            icon = R.drawable.ic_external_link,
            onClick = { openLink(context, "https://orbot.app") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )
    }
}
