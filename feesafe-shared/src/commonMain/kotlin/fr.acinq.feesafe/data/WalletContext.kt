package fr.acinq.feesafe.data

/** Contains contextual information for the wallet, fetched from https://acinq.co/feesafe/walletcontext.json. */
data class WalletContext(
    val isMempoolFull: Boolean,
    val androidLatestVersion: Int,
    val androidLatestCriticalVersion: Int,
    val isManualLiquidityEnabled: Boolean,
)
