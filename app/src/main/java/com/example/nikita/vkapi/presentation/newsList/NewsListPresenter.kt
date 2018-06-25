package com.example.nikita.vkapi.presentation.newsList

import android.os.Bundle
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.nikita.vkapi.Application
import com.example.nikita.vkapi.data.dataBase.DBReposetory
import com.example.nikita.vkapi.data.models.NewsModel
import com.example.nikita.vkapi.interactors.PostGroup
import com.example.nikita.vkapi.other.FragmentType
import com.example.nikita.vkapi.other.NewsEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@InjectViewState
class NewsListPresenter : MvpPresenter<NewsListView>() {

    private val interactorPostGroup = PostGroup()
    lateinit var adapter: CardAdapter


    init {
        initPresenter()
    }

    private fun initPresenter() {

        EventBus.getDefault().register(this)

        interactorPostGroup.groupResponse()

        adapter = CardAdapter(DBReposetory.getNewsList())
        adapter.setOnItemClickListener(object : CardAdapter.OnItemClick {
            override fun onItemClick(newsModel: NewsModel) {
                val bundle = Bundle()
                bundle.putSerializable(NewsModel::class.java.name, newsModel)
                Application.SampleApplication.INSTANCE.getRouter().navigateTo(FragmentType.CARD_INFO, bundle)
            }
        })
    }

    override fun onDestroy() {
        interactorPostGroup.unsubscribe()

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setNewsList(newsEvent: NewsEvent) {
        if (newsEvent.errorMessage.isNotEmpty())
            viewState.makeErrorToast(newsEvent.errorMessage)
        adapter.updateNews(DBReposetory.getNewsList())

    }


}