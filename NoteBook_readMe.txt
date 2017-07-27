Notepad--第二次任务笔记

1. 为什么传递adapter对象的时候Event也要继承Seria...?

2. 空指针异常：
	1. 绑定布局出错，使findViewById的时候找不到
	2. 整型没有转换为字符串，使其在找资源的时候，将整型当做资源ID去找
	3. finish的调用位置不当，常在有Intent进行数据绑定是出现，finish应该在Intent绑定完数据之后调用

3. 不同界面上的组件指定onClick的时候还可以跳转到其他活动的响应函数中去么？？

4. Android中传递对象：
	解决此异常，一般大家知道方法就是，既然报没有序列化的错误,那我们就把我们的对象序列化了就是了,
	实现起来也很简单,就是把要放入对象序列化即可public class YourClassName implements java.io.Serializable{}
    于是修改此类实现Serializable，可是运行发现仍然报这个错误。然后又调试半天，
	终于发现问题并解决了。原因是此类中还调用了其他的类对象，调用的类对象必须一并实现Serializable。
	注：如果父类实现了序列化接口，那么子类无需实现序列化接口父类也可以成功序列化！
	
5. 还存在一个问题是每次返回在重新打开以后顺序就又变为默认的了

6. 统计完成情况时，绘制图形时，不应该出现的一些确定的数字，比如说间隔，这样就没有普适性

7. Android中View组件的加载通过在绘图时设置默认项，导致程序崩溃，可知View组件的大小在
	Activity的onCreate，onStart，onResume三个生命周期中其实是不能得到其大小的，此时得到的
	组件大小还是0；一个活动启动后什么时候才能获得其中View组件的尺寸？
	（这篇博客有讲解：http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2014/0802/1641.html）

8. 下面这篇博客讲解RecyclerView的点击和长按事件还不错
http://blog.csdn.net/lmj623565791/article/details/45059587#t6	
	
	
	
	
	