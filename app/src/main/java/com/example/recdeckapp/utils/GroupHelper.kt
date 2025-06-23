//package com.example.recdeckapp.utils
//
//import android.content.Context
//import android.util.Log
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.lifecycle.lifecycleScope
//import com.bumptech.glide.Glide
//import com.example.recdeckapp.data.roomDatabase.AppDatabase.AppDatabase
//import com.example.recdeckapp.data.roomDatabase.entities.GroupCreation.GroupEntity
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//
//object GroupHelper {
//
//    fun showUserGroupDetails(
//        context: Context,
//        scope: CoroutineScope, // from Fragment or Activity
//        userId: Int,
//        onSuccess: (GroupEntity?) -> Unit
//    ) {
//        val groupDao = AppDatabase.getDatabase(context).groupDao()
//
//        scope.launch {
//            try {
//                val groupList = withContext(Dispatchers.IO) {
//                    groupDao.getAllGroupsByUser(userId)
//                }
//                val firstGroup = groupList.firstOrNull()
//                onSuccess(firstGroup)
//            } catch (e: Exception) {
//                Log.e("GroupHelper", "Failed to load group: ${e.localizedMessage}")
//                onSuccess(null)
//            }
//        }
//    }
//
//    // Optional helper to bind data to views
//    fun bindGroupDataToViews(
//        group: GroupEntity,
//        tvGroupName: TextView,
//        tvDescription: TextView,
//        tvRules: TextView,
//        ivGroupImage: ImageView,
//        context: Context
//    ) {
//        tvGroupName.text = group.groupName
//        tvDescription.text = group.description
//        tvRules.text = group.rules
//
//        Glide.with(context)
//            .load(group.imageUrl)
//            .into(ivGroupImage)
//    }
//}
