/*
 * Copyright 2024 ACINQ SAS
 * Copyright 2026 ZenderBin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.acinq.feesafe.utils.logger

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.OSLogWriter
import fr.acinq.feesafe.utils.PassthruLogWriter
import fr.acinq.feesafe.utils.PlatformContext

actual fun feesafeLogWriters(ctx: PlatformContext): List<LogWriter> {
    return if (ctx.logger != null) {
        listOf(PassthruLogWriter(ctx))
    } else {
        // OSLogWriter is disabled for now, as the current version of OSLogStore is buggy
        // listOf(OSLogWriter())
        throw IllegalArgumentException("A valid logger must be provided. OSLogWriter is disabled.")
    }
}