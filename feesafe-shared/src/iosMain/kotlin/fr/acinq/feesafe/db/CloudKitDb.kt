package fr.acinq.feesafe.db

import fr.acinq.feesafe.db.payments.*
import kotlinx.coroutines.*

class CloudKitDb(
    appDb: SqliteAppDb,
    paymentsDb: SqlitePaymentsDb
): CloudKitInterface, CoroutineScope by MainScope() {

    val contacts = CloudKitContactsDb(paymentsDb)
    val payments = CloudKitPaymentsDb(paymentsDb)
}
