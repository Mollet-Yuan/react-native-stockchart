package com.mollet.stockchart

import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext

import com.mollet.stockchart.chart.DataHelper
import com.mollet.stockchart.chart.KLineChartAdapter
import com.mollet.stockchart.chart.KLineChartView
import com.mollet.stockchart.chart.KLineEntity
import com.mollet.stockchart.chart.draw.Status
import com.mollet.stockchart.chart.formatter.DateFormatter
import org.jetbrains.anko.doAsync

class StockChartManager : SimpleViewManager<KLineChartView>() {
    private lateinit var ticks: List<KLineEntity>

    private val adapter by lazy { KLineChartAdapter() }

    // 主图指标下标
    private var mainIndex = 0
    // 副图指标下标
    private var subIndex = -1

    override fun getName(): String {
        return REACT_CLASS
    }

    override fun createViewInstance(reactContext: ThemedReactContext): KLineChartView {
        val kLineChartView= KLineChartView(reactContext)
        kLineChartView.adapter = adapter
        kLineChartView.dateTimeFormatter = DateFormatter()
        kLineChartView.setGridRows(4)
        kLineChartView.setGridColumns(4)
        //  kLineChartView.changeMainDrawType(Status.MA)
        //  kLineChartView.setChildDraw(subIndex)
        //  kLineChartView.setMainDrawLine(true)
        //  kLineChartView.hideSelectData()
        initData(reactContext,kLineChartView)
        return KLineChartView(reactContext)
    }
    private fun initData(reactContext:ThemedReactContext,kLineChartView:KLineChartView) {
        kLineChartView.justShowLoading()
        doAsync {
            ticks = DataRequest.getALL(reactContext).subList(0, 500)
            DataHelper.calculate(ticks)
            runOnUiThread {
                adapter.addFooterData(ticks)
                adapter.notifyDataSetChanged()
                kLineChartView.startAnimation()
                kLineChartView.refreshEnd()
            }
        }
    }
    companion object {
        const val REACT_CLASS = "StockChart"
    }
}