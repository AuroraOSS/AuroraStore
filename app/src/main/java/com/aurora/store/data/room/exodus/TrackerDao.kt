/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.data.room.exodus

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface TrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trackers: List<TrackerEntity>)

    @Query("DELETE FROM exodus_tracker")
    suspend fun deleteAll()

    @Query("SELECT * FROM exodus_tracker WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Int>): List<TrackerEntity>

    @Query("SELECT COUNT(*) FROM exodus_tracker")
    suspend fun count(): Int

    @Transaction
    suspend fun purgeAndInsert(trackers: List<TrackerEntity>) {
        deleteAll()
        insertAll(trackers)
    }
}
