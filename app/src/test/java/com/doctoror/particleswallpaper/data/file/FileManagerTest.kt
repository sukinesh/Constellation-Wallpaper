/*
 * Copyright (C) 2018 Yaroslav Mytkalyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doctoror.particleswallpaper.data.file

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.io.File

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class FileManagerTest {

    private val filesDir = shadowOf(RuntimeEnvironment.application).applicationContext.filesDir
    private val uri = Uri.parse("content://file")
    private val file = File(filesDir, "fileName")
    private val fileContents = byteArrayOf(0, 1, 2)

    @Before
    fun setup() {
        file.writeBytes(fileContents)
    }

    @After
    fun tearDown() {
        file.delete()
    }

    @Test
    fun savesToPrivateFile() {
        // Given
        val context = mockContextForTargetFile()

        // When
        FileManager(context).saveToPrivateFile(uri, file)

        // Then
        val readFileContents = file.readBytes()
        assertTrue(readFileContents.contentEquals(fileContents))
    }

    private fun mockContextForTargetFile(): Context {
        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

        val contentResolver: ContentResolver = mock {
            on(it.openFileDescriptor(uri, "r")).doReturn(fileDescriptor)
        }

        return mock {
            on(it.contentResolver).doReturn(contentResolver)
            on(it.filesDir).doReturn(filesDir)
        }
    }
}
