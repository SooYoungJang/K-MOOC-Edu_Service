package com.programmers.kmooc.activities.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.programmers.kmooc.KmoocApplication
import com.programmers.kmooc.activities.detail.KmoocDetailActivity
import com.programmers.kmooc.databinding.ActivityKmookListBinding
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.utils.toVisibility
import com.programmers.kmooc.viewmodels.KmoocListViewModel
import com.programmers.kmooc.viewmodels.KmoocListViewModelFactory

class KmoocListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKmookListBinding
    private lateinit var viewModel: KmoocListViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var adapter: LecturesAdapter

    private var totalCount = 0 // 전체 아이템 개수
    private var isNext = false // 다음 페이지 유무
    private var page = 0       // 현재 페이지
    private var limit = 10     // 한 번에 가져올 아이템 수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kmoocRepository = (application as KmoocApplication).kmoocRepository
        viewModel = ViewModelProvider(this, KmoocListViewModelFactory(kmoocRepository)).get(
            KmoocListViewModel::class.java
        )
        binding = ActivityKmookListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = LecturesAdapter()
            .apply { onClick = this@KmoocListActivity::startDetailActivity }

        binding.lectureList.adapter = adapter
        viewModel.list()
        viewModel.lectureList.observe(this, Observer {
            adapter.updateLectures(it.lectures)
        })
        binding.pullToRefresh.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            binding.pullToRefresh.isRefreshing = false
        }

        binding.lectureList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = binding.lectureList.layoutManager

                // hasNextPage() -> 다음 페이지가 있는 경우
                if(viewModel.progress.value != true) {
                    val lastItem = (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                    if(layoutManager.itemCount <= lastItem +5) {
                        viewModel.next()
                    }
                }
            }
        })
    }

    private fun startDetailActivity(lecture: Lecture) {
        Log.d("SDFSDf","RRRR ${lecture.id}")
        startActivity(
            Intent(this, KmoocDetailActivity::class.java)
                .apply { putExtra(KmoocDetailActivity.INTENT_PARAM_COURSE_ID, lecture.id) }
        )
    }

    // 리사이클러뷰에 더 보여줄 데이터를 로드하는 경우
    private fun loadMorePosts() {


    }





    private fun getPage(): Int {
        page++
        return page
    }

    private fun hasNextPage(): Boolean {
        return isNext
    }

    private fun setHasNextPage(b: Boolean) {
        isNext = b
    }
}
