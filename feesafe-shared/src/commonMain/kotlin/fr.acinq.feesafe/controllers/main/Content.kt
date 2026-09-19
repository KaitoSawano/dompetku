package fr.acinq.feesafe.controllers.main

import fr.acinq.feesafe.controllers.MVI

object Content {

    sealed class Model : MVI.Model() {
        object Waiting : Model()
        object IsInitialized : Model()
        object NeedInitialization : Model()
    }

    sealed class Intent : MVI.Intent()

}
