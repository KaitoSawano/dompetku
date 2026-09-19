package fr.acinq.feesafe.db

import fr.acinq.lightning.utils.UUID
import fr.acinq.feesafe.db.payments.CloudKitInterface
import fr.acinq.feesafe.db.sqldelight.AppDatabase
import fr.acinq.feesafe.db.sqldelight.PaymentsDatabase

actual fun didSaveWalletPayment(id: UUID, database: PaymentsDatabase) {}
actual fun didDeleteWalletPayment(id: UUID, database: PaymentsDatabase) {}
actual fun didUpdateWalletPaymentMetadata(id: UUID, database: PaymentsDatabase) {}

actual fun didSaveContact(contactId: UUID, database: PaymentsDatabase) {}
actual fun didDeleteContact(contactId: UUID, database: PaymentsDatabase) {}

actual fun makeCloudKitDb(appDb: SqliteAppDb, paymentsDb: SqlitePaymentsDb): CloudKitInterface? {
    return null
}