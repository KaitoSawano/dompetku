import Foundation

/// This file is **ONLY** for the main FeeSafe app (foreground process)
///
extension AppIdentifier {
	
	static var current: AppId {
		return .foreground
	}
}

