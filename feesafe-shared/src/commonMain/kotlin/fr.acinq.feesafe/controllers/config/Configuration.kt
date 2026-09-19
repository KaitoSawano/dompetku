package fr.acinq.feesafe.controllers.config

import fr.acinq.feesafe.controllers.MVI

object Configuration {

    sealed class Model : MVI.Model() {
        object SimpleMode : Model()
        object FullMode : Model()
    }

    sealed class Intent : MVI.Intent()
}
