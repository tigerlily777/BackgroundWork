# BackgroundWork

## 1️⃣ 为什么需要背景任务？

### ✅ 因为在移动设备上：
	•	用户操作App的时候，不希望一切都卡在主线程上（比如下载、同步、备份）
	•	有些工作需要在后台默默进行（比如推送更新、定时提醒、自动同步数据）

简单说就是：Make the device do some work quietly without bothering the users.

比如：
	•	when using photo App，the photoes are updated to cloud automatically at background
	•	你在用笔记 App，笔记后台悄悄保存草稿
	•	你下了个银行 App，它定时后台同步你的账户余额

### 🌟 总结，背景任务:
- 提升体验 用户不需要等下载完成，界面不卡住
- 节省电量 后台批量处理，避免频繁唤醒设备
- 符合系统限制 Android 限制了后台过度唤醒，必须合理使用

## 2️⃣ 背景任务的不同类型

### ✅ Android官方把背景任务大致分成两种：

| 类型 | 特点 | 示例 |
|:---|:---|:---|
| 短时间任务（短任务） | 快速完成，一般几秒内搞定 | 上传一张图片、保存一条记录 |
| 长时间任务（长任务） | 比较慢，可能几分钟、几十分钟才完成 | 大文件下载、定时备份数据 |

📚 简单区分一下
| 项目 | 短任务 | 长任务 |
|:---|:---|:---|
| 是否需要持续运行 | ❌ 不需要 | ✅ 需要 |
| 是否涉及设备休眠/重启 | ❌ 不需要太担心 | ✅ 要考虑（可能设备休眠或者关机） |
| 是否要求可靠性 | ❌ 一般即可 | ✅ 需要高可靠，比如断网后自动重试 |
| 典型例子 | 发送 API 请求 | 备份所有照片 |

### 🌟 总结成一句话：

短任务适合「快速搞定一次性的小事」，长任务适合「后台默默进行的大工程」。

## 3️⃣ Android 官方提供了哪些做背景任务的方法？
| 方法               | 关键词                            |
|:-------------------|:----------------------------------|
| Handler            | 主线程以外执行的小事              |
| WorkManager        | 后台可靠、自动恢复、最推荐         |
| JobScheduler       | 系统调度，大任务（有条件限制）     |
| Foreground Service | 必须让用户看到的后台活动           |

# Service
## Service 学习路线 - Android service - 见 [Service 复习笔记](https://github.com/tigerlily777/android-service-cheatbook/wiki/Home-%E2%80%90-Service-%E5%A4%8D%E4%B9%A0%E7%AC%94%E8%AE%B0)
## 1️⃣ 什么是 Service？
### ✅ Service 是 Android 四大组件之一。
（Activity / Service / BroadcastReceiver / ContentProvider）
一句话总结：

Service 是在后台长时间执行任务的 Android 组件，没有界面。

### 为什么需要 Service？

比如这些情况：
	• 播放音乐时，用户切到别的 App，音乐还要继续放 🎵
	• 使用导航 App，屏幕锁定后仍能持续定位 🧭
	• 云端同步，比如备份照片 ☁️

这些任务如果放在 Activity 里，Activity一关掉，任务就没了。
所以需要用 Service —— 让任务独立于界面，在后台继续运行。

⸻
## 2️⃣ Service 分类

| 类型               | 描述                                       | 示例                         |
| ------------------ | ------------------------------------------ | ---------------------------- |
| Foreground Service | 用户知道它在运行（通知栏有通知）            | 导航 App、音乐播放器         |
| Background Service | 用户不可见的后台执行                        | 自动同步                     |
| Bound Service      | 允许其他应用或组件绑定到这个 Service 上交互 | 音乐播放管理器（比如控制播放暂停） |

## 3️⃣ Service 的生命周期（基础版）

你需要知道，Service 也是有生命周期方法的，常用这三个：
| 方法           | 作用                                             |
| --------------- | ------------------------------------------------ |
| `onCreate()`    | 第一次创建时调用（只调用一次）                   |
| `onStartCommand()` | 每次通过 `startService()` 调用时执行主要逻辑     |
| `onDestroy()`   | `Service` 被销毁时调用（比如手动停止或者系统回收） |

startService() → onCreate() → onStartCommand() → ... → onDestroy()

另外对于 Bound Service，还有：
	•	onBind()
	•	onUnbind()

## 4️⃣ 官方强提醒：Background Service限制！

从 Android 8.0 (API 26) 开始，
✅ 系统对后台 Service 有了严格限制：

如果 App 在后台（没有任何可见的 Activity），
❗ 不能随便启动 Background Service了！

如果一定要后台长时间运行，必须用 Foreground Service（带通知栏通知）。

如果要使用 Foreground Service（比如后台播放音乐、后台下载），
✅ 必须调用 startForeground() 方法，并且显示一个通知(Notification)！

Client → startService() → onCreate() + onStartCommand()
      → Service在后台跑
Client → stopService() 或 Service自己stopSelf()
      → onDestroy()
      
如果是 Bound Service，则是：

Client → bindService() → onBind() → 客户端和 Service 可以通信
      
Client → unbindService() → onUnbind()

## 5️⃣ 怎么使用 Service？（非常基础的使用）

第一步：写一个类继承 Service

```kotlin
class MyService : Service() {
    override fun onCreate() {
        super.onCreate()
        // 初始化，比如准备播放器
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 主要工作，比如开始播放音乐
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // 收尾，比如停止播放器
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null  // 如果是普通 Service 返回 null
    }
}
```

第二步：在 AndroidManifest.xml 注册 Service
```xml
<service android:name=".MyService" />
```

第三步：从 Activity 启动 Service
```kotlin
val intent = Intent(this, MyService::class.java)
startService(intent)
```


# Q&A
## 1. Service 和 Thread 
1️⃣ 「Service 和 Thread 是不是一回事？」
不是 

| 对比项               | Service                                     | Thread                             |
|----------------------|---------------------------------------------|-------------------------------------|
| 是什么？             | Android 组件，生命周期由系统管理            | Java 基础线程类，用于执行代码块     |
| 是否自动开启子线程？ | ❌ 默认不会自动开启线程                      | ✅ 手动启动后会拥有自己的线程       |
| 适合干嘛？           | 管理任务的生命周期                           | 执行任务的逻辑处理                   |

✅ 重点：Service 本身默认运行在主线程！！！

所以你在 onStartCommand() 里写代码，其实是在主线程里执行的
👉 如果你直接在里面做耗时操作，会 卡 UI！

⸻

2️⃣ 那为什么在 Service 里用 Handler？

因为 ——
🔸 Service 默认运行在主线程，但它的任务通常是长时间后台操作（比如定时上传/下载）
🔸 为了不阻塞主线程，我们经常会在 Service 里：
	•	开一个子线程（比如 Thread、Executor、HandlerThread）
	•	或者用 Handler 来配合做定时、延迟、循环任务

所以：
Handler 出现在 Service 里，不是因为 Service 是线程，
而是因为你需要在线程间调度工作，而 Handler 是一种调度机制。
📌 举个常见的场景（文档 sample 里就是这种）：
```kotlin
val handler = Handler()
handler.postDelayed(runnable, 1000)
```
	• 你在 Service 里想每隔一秒做一件事，比如打一个 log
	• 虽然你不想开新线程，也不想用 Timer，那 Handler 就是最简便的方案
 🧠 小对照理解
 | 类别     | 本质                        | 常见用途                                 |
|----------|-----------------------------|------------------------------------------|
| Thread   | Java 层线程，执行任务        | 执行后台任务                              |
| Service  | Android 组件，系统管理生命周期 | 管理任务“何时开始”“是否继续”等逻辑       |
| Handler  | 消息调度器                    | 延迟执行、线程跳转、定时任务              |

🏆 小结一段话

Service 本身不等于线程，它运行在主线程，
但我们经常需要在里面处理后台逻辑，所以用 Handler 来调度任务、延迟执行或切换线程。

Handler 是调度工具，Service 是生命周期容器，Thread 是执行器。三者职责完全不同，但经常配合使用。

## 2. service sample code解析
```kotlin
class HelloService : Service() {

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1)
        }
	/** ✅ 这个类继承自 Handler，构造时传入一个后台线程的 Looper。

	它的作用是：
	• 等会儿我们通过这个 Handler 把任务发给子线程执行
	• 每一个任务到了子线程后，就会被 handleMessage() 执行

	这里 handleMessage() 中的逻辑是：
	• 简单模拟“干点事情”，比如睡5秒
	• 然后调用 stopSelf(startId) 表示这个任务结束了，可以安全地关闭 Service（不会误关其他未完成的任务）
	**/
    }

	/** ✅ 这里是Service生命周期第一次启动时执行的初始化工作。

	流程是：
	1. 创建了一个 HandlerThread —— 它是 Android 提供的线程 + Looper 一体的工具
	2. 启动线程 start()
	3. 获取这个线程的 Looper（它有自己的消息队列）
	4. 用这个 Looper 创建了 ServiceHandler，作为 Handler 的任务调度入口
	以后所有的任务就通过这个 Handler 安排到子线程去了。
	**/
    override fun onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
	
        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()

            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    /**
	✅ 每次你调用 startService()，都会走进这个方法。

	它做的事：
	1. 弹个 Toast，表示 service 开始了
	2. 给我们的后台线程 Handler 发个消息，让它去处理任务（通过 handleMessage()）
	3. 返回 START_STICKY —— 表示系统杀掉 Service 后还会尝试重启它

	🔄 这就是把工作从主线程“安全地”安排到后台线程的过程。
    **/
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }
}
```

整体流程：
App 调用 startService() →
→ Service.onCreate() → 创建 HandlerThread & Handler
→ Service.onStartCommand()
    → handler.sendMessage()
    → Handler(子线程).handleMessage()
        → 做任务（睡 5 秒）
        → stopSelf() → Service 结束

 Main Thread (UI线程)
  └── HelloService（运行在主线程上）
       └── 创建 HandlerThread (后台线程)
            └── 绑定自己的 Looper
                 └── 用 ServiceHandler 派发任务
                      └── 后台线程安全地执行耗时任务
		      
🧠 这段代码的真正结构是：
| 角色           | 对应对象                  | 说明                                               |
|----------------|----------------------------|----------------------------------------------------|
| 执行环境（线程） | HandlerThread               | 单独开启一个后台线程                                |
| 循环器（任务循环） | Looper（通常叫 serviceLooper） | 管理任务队列，循环执行                              |
| 任务调度器       | Handler（通常叫 ServiceHandler） | 负责派发、处理任务                                  |
| 任务执行         | handleMessage()             | 真正进行后台工作（比如 sleep 5秒，模拟下载等）       |

### 🎯 为什么 Service 要这样写？

因为：

✅ Service 默认运行在 主线程 ❗
✅ 如果你直接在 onStartCommand() 里做耗时任务，主线程就会卡死！

所以，为了「后台做任务又不阻塞主线程」，就要：
	1.	创建独立的后台线程（HandlerThread）
	2.	给这个线程配一个 Looper（自动）
	3.	用 Handler 向它发任务（post/sendMessage）
	4.	让后台线程慢慢处理这些任务

🌟 这样，Service 保持轻快，
🌟 主线程不会被堵塞，
🌟 耗时任务也能安安稳稳地完成！

🧠 最最重要的理解：

Service 本质上只是「生命周期容器」，真正干活的是自己开的子线程（HandlerThread）+ Handler！

⸻

## [Service notes entry](https://github.com/tigerlily777/android-service-cheatbook/wiki) 
## Handler Learning path - lightweighted 2 day pass!（notes + Q&A）- See [About Handler ‐ Quick pass](https://github.com/tigerlily777/android-service-cheatbook/wiki/About-Handler-%E2%80%90-%E9%80%9F%E8%AF%BB)
## Handler quick Q&A! [Handler quiz!](https://github.com/tigerlily777/android-service-cheatbook/wiki/Handler-%E2%80%90-pop-quiz!)
## [Bound service](https://github.com/tigerlily777/android-service-cheatbook/wiki/Bound-service!)
## [Foreground service](https://github.com/tigerlily777/android-service-cheatbook/wiki/Foreground-services-overview)
