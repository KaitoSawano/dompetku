package fr.acinq.feesafe.controllers.init

import fr.acinq.feesafe.controllers.MVI
import fr.acinq.feesafe.utils.MnemonicLanguage


object Initialization {

    sealed class Model : MVI.Model() {
        object Ready : Model()
        data class GeneratedWallet(
            val mnemonics: List<String>,
            val language: MnemonicLanguage,
            val seed: ByteArray
        ) : Model() {
            override fun toString() = "GeneratedWallet"
        }
    }

    sealed class Intent : MVI.Intent() {
        data class GenerateWallet(
            val entropy: ByteArray,
            val language: MnemonicLanguage
        ) : Intent() {
            override fun toString() = "GenerateWallet"
        }
    }

}
