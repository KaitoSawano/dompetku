package fr.acinq.feesafe.managers

import fr.acinq.bitcoin.Chain
import fr.acinq.bitcoin.PublicKey
import fr.acinq.bitcoin.byteVector
import fr.acinq.lightning.db.Databases
import fr.acinq.lightning.logging.LoggerFactory
import fr.acinq.lightning.logging.debug
import fr.acinq.feesafe.FeeSafeBusiness
import fr.acinq.feesafe.db.SqliteAppDb
import fr.acinq.feesafe.db.SqliteChannelsDb
import fr.acinq.feesafe.db.SqlitePaymentsDb
import fr.acinq.feesafe.db.contacts.SqliteContactsDb
import fr.acinq.feesafe.db.createChannelsDbDriver
import fr.acinq.feesafe.db.createPaymentsDbDriver
import fr.acinq.feesafe.db.createSqliteChannelsDb
import fr.acinq.feesafe.db.createSqlitePaymentsDb
import fr.acinq.feesafe.db.makeCloudKitDb
import fr.acinq.feesafe.db.payments.CloudKitInterface
import fr.acinq.feesafe.defaultScope
import fr.acinq.feesafe.managers.global.CurrencyManager
import fr.acinq.feesafe.utils.PlatformContext
import fr.acinq.feesafe.utils.extensions.feesafeName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

class DatabaseManager(
    loggerFactory: LoggerFactory,
    private val ctx: PlatformContext,
    private val chain: Chain,
    private val appDb: SqliteAppDb,
    private val nodeParamsManager: NodeParamsManager,
    appConfigurationManager: AppConfigurationManager,
    currencyManager: CurrencyManager,
) : CoroutineScope by defaultScope() {

    constructor(business: FeeSafeBusiness): this(
        loggerFactory = business.loggerFactory,
        ctx = business.feesafeGlobal.ctx,
        chain = business.chain,
        appDb = business.feesafeGlobal.appDb,
        nodeParamsManager = business.nodeParamsManager,
        appConfigurationManager = business.appConfigurationManager,
        currencyManager = business.feesafeGlobal.currencyManager,
    )

    private val log = loggerFactory.newLogger(this::class)

    private val _databases = MutableStateFlow<FeeSafeDatabases?>(null)
    val databases: StateFlow<FeeSafeDatabases?> = _databases.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val contactsList = _databases.filterNotNull().flatMapLatest { it.payments.contacts.contactsList }

    @OptIn(ExperimentalCoroutinesApi::class)
    val contactsDb = _databases.filterNotNull().mapLatest { it.payments.contacts }

    val paymentMetadataQueue = PaymentMetadataQueue(currencyManager = currencyManager, appConfigurationManager = appConfigurationManager)

    init {
        launch {
            nodeParamsManager.nodeParams.collect { nodeParams ->
                if (nodeParams == null) return@collect
                log.debug { "nodeParams available: building databases..." }

                val channelsDbDriver = createChannelsDbDriver(ctx, channelsDbName(chain, nodeParams.nodeId))
                val channelsDb = createSqliteChannelsDb(channelsDbDriver)
                val paymentsDbDriver = createPaymentsDbDriver(ctx, paymentsDbName(chain, nodeParams.nodeId)) { log.e { "payments-db migration error: $it" } }
                val paymentsDb = createSqlitePaymentsDb(paymentsDbDriver, paymentMetadataQueue, loggerFactory)
                val cloudKitDb = makeCloudKitDb(appDb, paymentsDb)
                log.debug { "databases object created" }
                _databases.value = FeeSafeDatabases(
                    channels = channelsDb,
                    payments = paymentsDb,
                    cloudKit = cloudKitDb,
                )
            }
        }
        launch {
            paymentsDb().contacts.migrateContactsIfNeeded(appDb)
        }
    }

    fun close() {
        val db = databases.value
        if (db != null) {
            db.channels.close()
            db.payments.close()
        }
    }

    suspend fun paymentsDb(): SqlitePaymentsDb {
        val db = databases.filterNotNull().first()
        return db.payments
    }

    suspend fun contactsDb(): SqliteContactsDb {
        return paymentsDb().contacts
    }

    suspend fun cloudKitDb(): CloudKitInterface? {
        val db = databases.filterNotNull().first()
        return db.cloudKit
    }

    companion object {
        fun channelsDbName(chain: Chain, nodeId: PublicKey): String {
            return "channels-${chain.feesafeName.lowercase()}-${nodeId.hash160().byteVector().toHex()}.sqlite"
        }

        fun paymentsDbName(chain: Chain, nodeId: PublicKey): String {
            return "payments-${chain.feesafeName.lowercase()}-${nodeId.hash160().byteVector().toHex()}.sqlite"
        }
    }
}

data class FeeSafeDatabases(
    override val channels: SqliteChannelsDb,
    override val payments: SqlitePaymentsDb,
    val cloudKit: CloudKitInterface?,
): Databases
