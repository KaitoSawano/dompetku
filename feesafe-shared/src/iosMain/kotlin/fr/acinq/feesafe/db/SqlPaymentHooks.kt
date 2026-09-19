package fr.acinq.feesafe.db

import fr.acinq.lightning.utils.UUID
import fr.acinq.lightning.utils.currentTimestampMillis
import fr.acinq.feesafe.db.payments.CloudKitInterface
import fr.acinq.feesafe.db.sqldelight.AppDatabase
import fr.acinq.feesafe.db.sqldelight.PaymentsDatabase


actual fun didSaveWalletPayment(id: UUID, database: PaymentsDatabase) {
    database.cloudKitPaymentsQueries.addToQueue(id = id, date_added = currentTimestampMillis())
}

actual fun didDeleteWalletPayment(id: UUID, database: PaymentsDatabase) {
    database.cloudKitPaymentsQueries.addToQueue(id = id, date_added = currentTimestampMillis())
}

actual fun didUpdateWalletPaymentMetadata(id: UUID, database: PaymentsDatabase) {
    database.cloudKitPaymentsQueries.addToQueue(id = id, date_added = currentTimestampMillis())
}

actual fun didSaveContact(contactId: UUID, database: PaymentsDatabase) {
    database.cloudKitContactsQueries.addToQueue(
        id = contactId.toString(),
        date_added = currentTimestampMillis()
    )
}

actual fun didDeleteContact(contactId: UUID, database: PaymentsDatabase) {
    database.cloudKitContactsQueries.addToQueue(
        id = contactId.toString(),
        date_added = currentTimestampMillis()
    )
}

actual fun makeCloudKitDb(appDb: SqliteAppDb, paymentsDb: SqlitePaymentsDb): CloudKitInterface? {
    return CloudKitDb(appDb, paymentsDb)
}