package fr.acinq.feesafe.managers

import fr.acinq.lightning.blockchain.electrum.ElectrumClient
import fr.acinq.lightning.logging.LoggerFactory
import fr.acinq.lightning.utils.Connection
import fr.acinq.feesafe.FeeSafeBusiness
import fr.acinq.feesafe.managers.global.NetworkMonitor
import fr.acinq.feesafe.managers.global.NetworkState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import fr.acinq.feesafe.utils.extensions.plus

data class Connections(
    val internet: Connection = Connection.CLOSED(reason = null),
    val peer: Connection = Connection.CLOSED(reason = null),
    val electrum: Connection = Connection.CLOSED(reason = null),
) {
    val global : Connection get() = internet + peer + electrum
}

@OptIn(ExperimentalCoroutinesApi::class)
class ConnectionsManager(
    loggerFactory: LoggerFactory,
    peerManager: PeerManager,
    electrumClient: ElectrumClient,
    networkMonitor: NetworkMonitor,
): CoroutineScope {

    constructor(business: FeeSafeBusiness): this(
        loggerFactory = business.loggerFactory,
        peerManager = business.peerManager,
        electrumClient = business.electrumClient,
        networkMonitor = business.feesafeGlobal.networkMonitor
    )

    val log = loggerFactory.newLogger(this::class)
    private val job = Job()
    override val coroutineContext = MainScope().coroutineContext + job

    @OptIn(ExperimentalCoroutinesApi::class)
    val connections = peerManager.peerState.filterNotNull().flatMapLatest { peer ->
        combine(
            peer.connectionState,
            electrumClient.connectionStatus,
            networkMonitor.networkState
        ) { peerState, electrumStatus, internetState ->
            Connections(
                peer = peerState,
                electrum = electrumStatus.toConnectionState(),
                internet = when (internetState) {
                    NetworkState.Available -> Connection.ESTABLISHED
                    NetworkState.NotAvailable -> Connection.CLOSED(reason = null)
                },
            )
        }
    }.stateIn(
        scope = this,
        started = SharingStarted.Eagerly,
        initialValue = Connections(), // default value is everything = closed
    )
}