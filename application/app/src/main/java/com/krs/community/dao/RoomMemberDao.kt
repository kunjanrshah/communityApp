package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.RoomMember

@Dao
interface RoomMemberDao {

   @Query("SELECT * FROM RoomMember WHERE id==:id")
   fun getRoomMember(id:Int) : LiveData<RoomMember>

    @Query("SELECT * FROM RoomMember")
    fun getRoomMembers() : List<RoomMember>

   @Query("DELETE FROM RoomMember WHERE id==:id")
   fun deleteRoomMember(id:Int)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   fun saveRoomMember(roomMember: RoomMember)
}