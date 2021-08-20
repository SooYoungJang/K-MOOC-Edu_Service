package com.programmers.kmooc.activities.detail

import android.os.Bundle
import android.util.Log
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.programmers.kmooc.KmoocApplication
import com.programmers.kmooc.databinding.ActivityKmookDetailBinding
import com.programmers.kmooc.network.ImageLoader
import com.programmers.kmooc.utils.DateUtil
import com.programmers.kmooc.viewmodels.KmoocDetailViewModel
import com.programmers.kmooc.viewmodels.KmoocDetailViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KmoocDetailActivity : AppCompatActivity() {

    companion object {
        const val TAG = "KmoocDetailActivity"
        const val INTENT_PARAM_COURSE_ID = "param_course_id"
    }

    private lateinit var binding: ActivityKmookDetailBinding
    private lateinit var viewModel: KmoocDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kmoocRepository = (application as KmoocApplication).kmoocRepository
        viewModel = ViewModelProvider(this, KmoocDetailViewModelFactory(kmoocRepository)).get(
            KmoocDetailViewModel::class.java
        )

        binding = ActivityKmookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(INTENT_PARAM_COURSE_ID)) {
            val id = intent.getStringExtra(INTENT_PARAM_COURSE_ID)
            viewModel.detail(id)
        }
        else {
            Log.e(TAG, "no data")
        }

        binding.webView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
        }



        viewModel.lecture.observe(this, Observer {lecture ->
            Log.d("FSFSDFS ", "SSS ${lecture.toString()}  ")
            CoroutineScope(Dispatchers.Main).launch {
                val bitmap = withContext(Dispatchers.IO) {
                    ImageLoader.loadImage(lecture.courseImageLarge!!)
                }
//                Log.d("FSFSDFS ", "SSS ${}  ")
                binding.webView.loadData(lecture.previewVideo ?: "","text/html","UTF-8")
                binding.lectureImage.setImageBitmap(bitmap)
                binding.lectureNumber.setDescription("강좌번호 : ",lecture.number!!)
                val duration : String? = DateUtil.formatDate(lecture.start!!).plus(" ~ ").plus(
                    DateUtil.formatDate(lecture.end!!))
                binding.lectureDue.setDescription("운영기간 : ",duration!!)
                binding.lectureOrg.setDescription("운영기관 : ",lecture.orgName!!)
                binding.lectureTeachers.setDescription("교수정보 : ",lecture.teachers!!)
                binding.lectureType.setDescription("강좌분류 : ",lecture.classfyName!!)
            }
        })
    }

}


