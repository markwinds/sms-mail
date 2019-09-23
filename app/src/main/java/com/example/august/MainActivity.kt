package com.example.august

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager
import androidx.annotation.NonNull
import android.view.ViewGroup
import com.example.august.adapter.MailAdapter
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter
import android.widget.AdapterView.OnItemLongClickListener
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.*
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter
import java.util.*
import android.widget.AdapterView
import android.widget.Toast
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener

import com.nhaarman.listviewanimations.ArrayAdapter











class MainActivity : AppCompatActivity() {

    val s1 = List(5) { MailItem() }
    val s2 = List(5) { MailItem("good",false) }

    val datas = s1+s2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mDynamicListView=findViewById<DynamicListView>(R.id.dynamiclistview)//找到listview
        var myAdapter=MailAdapter(this,datas,R.layout.itemrow_gripview)//创建自己的适配器

        var simpleSwipeUndoAdapter=SimpleSwipeUndoAdapter(myAdapter, this@MainActivity, MyOnDismissCallback(myAdapter))//为适配器添加删除操作
        val animAdapter = AlphaInAnimationAdapter(simpleSwipeUndoAdapter)//添加显示动画
        animAdapter.setAbsListView(mDynamicListView)//设置适配器
        assert(animAdapter.viewAnimator != null)
        animAdapter.viewAnimator!!.setInitialDelayMillis(1)//设置取消操作延迟时间
        mDynamicListView.setAdapter(animAdapter)//设置适配器

        mDynamicListView.enableDragAndDrop()//使能拖拽
        mDynamicListView.setDraggableManager(TouchViewDraggableManager(R.id.textView_receive_mail))//可以拖拽的位置
        mDynamicListView.setOnItemMovedListener(MyOnItemMovedListener(myAdapter as ArrayAdapter<MailItem>))//移动监听
        mDynamicListView.setOnItemLongClickListener(MyOnItemLongClickListener(mDynamicListView))//长按操作

        mDynamicListView.enableSimpleSwipeUndo()//使能撤销
        mDynamicListView.setOnItemClickListener(MyOnItemClickListener(mDynamicListView));

    }//onCreat

    //控制删除操作的类，OnDismissCallback是简单的删除处理，见官方教程
    private inner class MyOnDismissCallback internal constructor(private val mAdapter: com.nhaarman.listviewanimations.ArrayAdapter<MailItem>) :
        OnDismissCallback {
        private var mToast: Toast? = null
        //这个方法在删除操作进行时会被调用
        override fun onDismiss(listView: ViewGroup, reverseSortedPositions: IntArray) {
            for (position in reverseSortedPositions) {
                mAdapter.remove(position)//删除数据
            }

            if (mToast != null) {
                mToast!!.cancel()
            }
            mToast = Toast.makeText(
                this@MainActivity,
                "You have delete a mail address!",//被删除的时候显示这句提示
                Toast.LENGTH_LONG
            )
            mToast!!.show()
        }
    }

    //控制添加的类
    private inner class MyOnItemClickListener internal constructor(private val mListView: DynamicListView) :
        AdapterView.OnItemClickListener {

        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            //item点击之后的操作在这里进行，下面这个例子是在点击的位置添加一个item
            //mListView.insert(position, MailItem("new",true))
        }
    }

    //控制移动的类
    private inner class MyOnItemMovedListener internal constructor(private val mAdapter: ArrayAdapter<MailItem>) :
        OnItemMovedListener {

        private var mToast: Toast? = null

        override fun onItemMoved(originalPosition: Int, newPosition: Int) {
            if (mToast != null) {
                mToast!!.cancel()
            }
            //当item被移动的时候执行的代码，下面是移动后显示moved的例子

//            mToast = Toast.makeText(
//                this@MainActivity,
//                "moved",
//                Toast.LENGTH_SHORT
//            )
//            mToast!!.show()
        }
    }

    //操作长按
    private inner class MyOnItemLongClickListener internal constructor(val listview:DynamicListView?): AdapterView.OnItemLongClickListener{
        val mListView:DynamicListView?

        init {
            mListView=listview
        }

        override fun onItemLongClick(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ): Boolean {
            if (mListView != null) {
                //长按的操作，下面是长按拖动的例子
                //mListView.startDragging(position - mListView.getHeaderViewsCount());
            }
            return true
        }
    }



}
