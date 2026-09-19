/*
 * Copyright 2025 ACINQ SAS
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

package fr.acinq.feesafe.android.components.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.acinq.feesafe.android.R
import fr.acinq.feesafe.android.components.buttons.Clickable
import fr.acinq.feesafe.android.components.dialogs.ModalBottomSheet
import fr.acinq.feesafe.android.utils.mutedBgColor

object WalletAvatars {
    val list = listOf(
        "😃", "🙃", "🤑", "😎", "🥳", "🧐", "🤠", "🤖",
        "👑", "🚀", "✈️", "🚣", "⛵", "🚗", "🏍️",
        "🧘", "⛹️", "🤾", "🚴", "🧗", "🏋️", "🤼", "🏌️", "🏇", "🤺", "⛷️", "🏂", "🏄", "🏊", "🥷", "💂", "🤵", "🤵‍♀️", "🧑‍🚀", "👷", "👮", "🧑‍🔬", "🧑‍🔧", "🧑‍🚒", "🧑‍🌾", "🧑‍🎓", "🧑‍⚖️", "👶", "🧒", "🧑", "🧓", "👧", "👩", "👵",
        "🐶", "🐱", "🐴", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🐔", "🐧", "🐤", "🦄", "🐝", "🐙", "🐬", "🐋", "🦜", "🐟", "🐛",
        "🍏", "🍎", "🍌", "🍇", "🍓", "🍺", "🍿", "🥖", "🧀", "🍔",
        "🌹", "🌻", "☘️", "❄️", "⛰️", "🌴", "🌳", "🌲", "⚡", "🌧️", "🌩️", "🌦️", "☀️",
        "🎂", "🎁", "🎈", "🎉", "🎃", "🎄", "🎀",
        "🔥", "💫", "⭐", "✨", "💰", "💸", "📈", "🎯", "♥️"
    )
}

@Composable
fun WalletAvatar(avatar: String, fontSize: TextUnit = 28.sp, borderColor: Color = Color.Transparent, backgroundColor: Color = MaterialTheme.colors.surface, internalPadding: PaddingValues = PaddingValues(10.dp)) {
    Box(modifier = Modifier.clip(CircleShape).background(backgroundColor).border(1.dp, color = borderColor, shape = CircleShape).padding(internalPadding), contentAlignment = Alignment.Center) {
        Text(
            text = avatar,
            fontSize = fontSize,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ColumnScope.AvatarPicker(
    avatar: String,
    onAvatarChange: (String) -> Unit,
) {
    var showPickerDialog by remember { mutableStateOf(false) }

    Clickable(onClick = { showPickerDialog = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
        WalletAvatar(avatar, fontSize = 48.sp, internalPadding = PaddingValues(16.dp))
    }

    if (showPickerDialog) {
        ModalBottomSheet(
            onDismiss = { showPickerDialog = false },
            horizontalAlignment = Alignment.CenterHorizontally,
            internalPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
            isContentScrollable = false,
        ) {
            Text(stringResource(R.string.wallet_edit_pick_avatar), style = MaterialTheme.typography.body2)
            Spacer(modifier = Modifier.height(24.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(WalletAvatars.list) { emoji ->
                    Clickable(onClick = {
                        showPickerDialog = false
                        onAvatarChange(emoji)
                    }) {
                        WalletAvatar(emoji, backgroundColor = if (emoji == avatar) mutedBgColor else Color.Transparent)
                    }
                }
            }
        }
    }
}