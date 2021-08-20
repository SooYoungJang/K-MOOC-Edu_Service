package com.programmers.kmooc.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.repositories.KmoocRepository

class KmoocListViewModel(private val repository: KmoocRepository) : ViewModel() {

    var progress = MutableLiveData<Boolean>()

    private val _lectureList: MutableLiveData<LectureList> by lazy {
        MutableLiveData<LectureList>().apply {
            postValue(LectureList.EMPTY)
        }
    }
    val lectureList: LiveData<LectureList>
        get() = _lectureList

    fun list() {
        progress.postValue(true)
         repository.list { lectureList ->
             _lectureList.postValue(lectureList)
             progress.postValue(false)
        }
    }

     fun next() {
        progress.postValue(true)
        val currentLectureList = this._lectureList.value ?: return
        repository.next(currentLectureList) { lectureList ->
            val currentLecture = currentLectureList.lectures
            val mergedLecture = currentLecture.toMutableList().apply {
                addAll(lectureList.lectures)
            }
            lectureList.lectures = mergedLecture
            _lectureList.postValue(lectureList)
            progress.postValue(false)
        }
    }
}

class KmoocListViewModelFactory(private val repository: KmoocRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KmoocListViewModel::class.java)) {
            return KmoocListViewModel(repository) as T
        }
        throw IllegalAccessException("Unkown Viewmodel Class")
    }
}