# BackgroundWork

## 1️⃣ 为什么需要背景任务？

### ✅ 因为在移动设备上：
	•	用户操作App的时候，不希望一切都卡在主线程上（比如下载、同步、备份）
	•	有些工作需要在后台默默进行（比如推送更新、定时提醒、自动同步数据）

简单说就是：「让设备在不打扰用户的情况下，悄悄做一些事情。」

比如：
	•	你在用相册 App，但照片自动在后台上传到云端
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




