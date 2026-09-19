import Foundation
import FeeSafeShared

struct MinerFeeInfo {
	let pubKeyScript: Bitcoin_kmpByteVector? // For targets: .spliceOut
	let transaction: Bitcoin_kmpTransaction? // For targets: .expiredSwapIn, .finalWallet
	let feerate: Lightning_kmpFeeratePerKw
	let minerFee: Bitcoin_kmpSatoshi
}
