package fr.acinq.feesafe.utils.extensions

import fr.acinq.bitcoin.Chain

/**
 * Value used by FeeSafe for naming files relative to the [Chain].
 * Specifically, testnet3 name must be "testnet", for historical reasons.
 */
val Chain.feesafeName: String
    get() = when (this) {
        Chain.Regtest -> "regtest"
        Chain.Signet -> "signet"
        Chain.Testnet3 -> "testnet"
        Chain.Testnet4 -> "testnet4"
        Chain.Mainnet -> "mainnet"
    }
