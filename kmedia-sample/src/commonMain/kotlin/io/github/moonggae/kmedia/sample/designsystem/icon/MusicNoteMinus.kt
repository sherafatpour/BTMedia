package io.github.moonggae.kmedia.sample.designsystem.icon

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

private var _musicMinus: ImageVector? = null

public val NcsIcons.MusicMinus: ImageVector
    get() {
        if (_musicMinus != null) {
            return _musicMinus!!
        }
        _musicMinus = materialIcon(name = "Outlined.MusicMinus") {
            materialPath {
                // Music note part
                moveTo(12f, 20f)
                curveTo(10.35f, 20f, 9.175f, 18.825f, 9.175f, 17.175f)
                curveTo(9.175f, 15.525f, 10.35f, 14.35f, 12f, 14.35f)
                curveTo(12.575f, 14.35f, 13.0625f, 14.4875f, 14f, 14.95f)
                verticalLineTo(5.45f)
                horizontalLineTo(20f)
                verticalLineTo(9.45f)
                horizontalLineTo(16f)
                verticalLineTo(17.175f)
                curveTo(16f, 18.825f, 14.825f, 20f, 13.175f, 20f)
                close()

//                // Minus part
//                moveTo(4f, 8f)
//                verticalLineTo(6f)
//                horizontalLineTo(12f)
//                verticalLineTo(8f)
//                horizontalLineTo(4f)
//                close()

                // Minus part
                moveTo(4f, 10f)
                verticalLineTo(8f)
                horizontalLineTo(10f)
                verticalLineTo(10f)
                horizontalLineTo(4f)
                close()
            }
        }
        return _musicMinus!!
    }
