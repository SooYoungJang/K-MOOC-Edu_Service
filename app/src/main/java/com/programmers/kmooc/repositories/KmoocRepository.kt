package com.programmers.kmooc.repositories

import android.util.Log
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.network.HttpClient
import com.programmers.kmooc.utils.DateUtil
import org.json.JSONObject

class KmoocRepository {

    /**
     * 국가평생교육진흥원_K-MOOC_강좌정보API
     * https://www.data.go.kr/data/15042355/openapi.do
     */

    private val httpClient = HttpClient("http://apis.data.go.kr/B552881/kmooc")
    private val serviceKey =
        "LwG%2BoHC0C5JRfLyvNtKkR94KYuT2QYNXOT5ONKk65iVxzMXLHF7SMWcuDqKMnT%2BfSMP61nqqh6Nj7cloXRQXLA%3D%3D"

    fun list(completed: (LectureList) -> Unit) {
        httpClient.getJson(
            "/courseList",
            mapOf("serviceKey" to serviceKey, "Mobile" to 1)
        ) { result ->
            result.onSuccess {
                completed(parseLectureList(JSONObject(it)))
            }
        }
    }

    fun next(currentPage: LectureList, completed: (LectureList) -> Unit) {
        val nextPageUrl = currentPage.next
        httpClient.getJson(nextPageUrl, emptyMap()) { result ->
            result.onSuccess {
                completed(parseLectureList(JSONObject(it)))
            }
        }
    }

    fun detail(courseId: String, completed: (Lecture) -> Unit) {
        httpClient.getJson(
            "/courseDetail",
            mapOf("CourseId" to courseId, "serviceKey" to serviceKey)
        ) { result ->
            result.onSuccess {
                completed(parseLecture(JSONObject(it)))
            }
        }
    }

    private fun parseLectureList(jsonObject: JSONObject): LectureList {
        //TODO: JSONObject -> LectureList 를 구현하세요
        val count : Int = jsonObject.getJSONObject("pagination").getInt("count")
        val numPages : Int = jsonObject.getJSONObject("pagination").getInt("num_pages")
        val previous : String = jsonObject.getJSONObject("pagination").getString("previous")
        val next : String = jsonObject.getJSONObject("pagination").getString("next")
        val results = jsonObject.getJSONArray("results")
        val listLecture = mutableListOf<Lecture>()
        for (i in 0..results.length() -1) {
            val id = results.getJSONObject(i).getString("id")
            val number = results.getJSONObject(i).getString("number")
            val name = results.getJSONObject(i).getString("name")
            val classfyName = results.getJSONObject(i).getString("classfy")
            val middleClassfyName = results.getJSONObject(i).getString("middle_classfy")
            var courseImage  = results.getJSONObject(i).getJSONObject("media").getJSONObject("image").getString("raw")
            val courseImageLarge = results.getJSONObject(i).getJSONObject("media").getJSONObject("image").getString("large")
            val shortDescription = results.getJSONObject(i).getString("short_description")
            val orgName = results.getJSONObject(i).getString("org_name")
            val start = results.getJSONObject(i).getString("start")
            val startToDate = DateUtil.parseDate(start)
            val end = results.getJSONObject(i).getString("end")
            val endToDate = DateUtil.parseDate(end)
            val teachers = results.getJSONObject(i).getString("teachers")
            val previewVideo = results.getJSONObject(i).getString("preview_video")
            listLecture.add(Lecture(id,number,name,classfyName,middleClassfyName,courseImage,courseImageLarge,shortDescription,orgName,startToDate,endToDate,teachers,previewVideo))
        }

        return LectureList(count,numPages,previous,next,listLecture)
    }

    private fun parseLecture(jsonObject: JSONObject): Lecture {
        Log.d("SDFf" ," acvvvvv ${jsonObject.toString()}")
        val name = jsonObject.getString("name")
        val courseImage = jsonObject.getJSONObject("media").getJSONObject("image").getString("large")
        val number = jsonObject.getString("number")
        val classfyName = jsonObject.getString("classfy_name")
        val orgName = jsonObject.getString("org_name")
        val teachers = jsonObject.getString("teachers")
        val start = jsonObject.getString("start")
        val startToDate = DateUtil.parseDate(start)
        val end = jsonObject.getString("end")
        val endToDate = DateUtil.parseDate(end)
        val previewVideo = jsonObject.getString("overview")
        Log.d("SDFSF ","AAAAAA $previewVideo")

        return Lecture(null,number,name,classfyName,null,null,courseImage,null,orgName,startToDate,endToDate,teachers,previewVideo)
    }
}