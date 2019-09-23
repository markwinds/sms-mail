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
        //mDynamicListView.setOnItemMovedListener(MyOnItemMovedListener(myAdapter))
        //mDynamicListView.setOnItemLongClickListener(new MyOnItemLongClickListener(mDynamicListView))

        mDynamicListView.enableSimpleSwipeUndo()

    }//onCreat

    //控制删除操作的类，OnDismissCallback是简单的删除处理，见官方教程
    private class MyOnDismissCallback internal constructor(private val mAdapter: com.nhaarman.listviewanimations.ArrayAdapter<MailItem>) :
        OnDismissCallback {
        private var mToast: Toast? = null
        //这个方法在删除操作进行时会被调用
        override fun onDismiss(listView: ViewGroup, reverseSortedPositions: IntArray) {
            for (position in reverseSortedPositions) {
                mAdapter.remove(position)//删除数据
            }

//            if (mToast != null) {
//                mToast!!.cancel()
//            }
//            mToast = Toast.makeText(
//                this@MainActivity,
//                "You have delete a mail address! You can undo it now.",
//                Toast.LENGTH_LONG
//            )
//            mToast!!.show()
        }
    }


}
