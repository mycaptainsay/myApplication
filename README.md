# Image Gallery - 双列图片流 App

字节技术训练营课后作业：参考抖音「经验栏」风格，实现双列图片流列表与图文详情页。

## 功能

- 双列瀑布流图片列表（Pexels API）
- 图文详情页（大图预览 + 元信息展示）
- 关键词搜索、下拉刷新、无限分页
- 三级缓存：OkHttp 磁盘缓存 + Glide 内存/磁盘缓存

## 技术栈

- Kotlin / MVVM / ViewBinding
- Retrofit + OkHttp + Gson
- Glide / Coroutines / LiveData

## 运行方式

1. 克隆仓库
2. 复制 `local.properties.example` 为 `local.properties`
3. 在 [Pexels API](https://www.pexels.com/api/) 申请 Key，填入 `pexels.api.key`
4. 配置 `sdk.dir` 为你的 Android SDK 路径
5. Android Studio 打开项目 → Sync Gradle → Run

## 项目结构

```
app/src/main/java/com/example/myapplication/
├── ui/home/HomeActivity.kt       # 双列列表首页
├── ui/detail/DetailActivity.kt   # 图文详情页
├── viewmodel/PhotoViewModel.kt   # 状态管理
├── repository/PhotoRepository.kt # 数据仓库
├── network/RetrofitClient.kt     # OkHttp 配置
├── adapter/PhotoAdapter.kt       # 列表适配器
└── utils/GlideAppModule.kt       # 图片缓存配置
```

## 缓存验证

1. 首次打开 App，浏览若干图片
2. 完全关闭 App（杀进程）
3. 重新打开，再次查看相同图片
4. 图片从 Glide 磁盘缓存加载，无需重新下载
