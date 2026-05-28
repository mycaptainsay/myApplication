# Android 双列瀑布流图片浏览 App 实现计划

## 一、项目研究结论

当前项目是一个基础的 Android Studio 项目：
- **包名**: com.example.myapplication
- **minSdk**: 24 (符合要求)
- **targetSdk**: 36
- **语言**: Kotlin
- **现有依赖**: androidx.core-ktx, material, appcompat, constraintlayout 等

需要基于此项目进行完整改造，实现一个 Pexels/Pinterest 风格的图片浏览应用。

## 二、需要编辑/创建的文件和模块

### 2.1 Gradle 配置文件
| 文件 | 操作 | 内容 |
|------|------|------|
| `gradle/libs.versions.toml` | 修改 | 添加 Retrofit, OkHttp, Glide, ViewModel, Lifecycle, Coroutines, SwipeRefreshLayout 等依赖版本 |
| `app/build.gradle.kts` | 修改 | 添加 kotlin-kapt 插件，添加新依赖，开启 ViewBinding |

### 2.2 Manifest 配置
| 文件 | 操作 | 内容 |
|------|------|------|
| `app/src/main/AndroidManifest.xml` | 修改 | 添加 INTERNET, ACCESS_NETWORK_STATE 权限，配置 Application，注册 DetailActivity |

### 2.3 常量配置
| 文件 | 操作 | 内容 |
|------|------|------|
| `app/src/main/java/com/example/myapplication/constants/ApiConstants.kt` | 创建 | Pexels API Key, Base URL, 缓存配置等常量 |
| `app/src/main/java/com/example/myapplication/constants/AppConstants.kt` | 创建 | 应用通用常量（分页数量等） |

### 2.4 数据模型
| 文件 | 操作 | 内容 |
|------|------|------|
| `app/src/main/java/com/example/myapplication/model/Src.kt` | 创建 | Src 数据类 (original, large, medium, small, portrait) |
| `app/src/main/java/com/example/myapplication/model/Photo.kt` | 创建 | Photo 数据类 (id, width, height, photographer, src 等) |
| `app/src/main/java/com/example/myapplication/model/PexelsResponse.kt` | 创建 | PexelsResponse 包装类 (page, per_page, photos, total_results) |
| `app/src/main/java/com/example/myapplication/model/Result.kt` | 创建 | BaseResult 封装类 (Loading, Success, Error) |

### 2.5 Retrofit/OkHttp 网络层
| 文件 | 操作 | 内容 |
|------|------|------|
| `app/src/main/java/com/example/myapplication/network/ApiService.kt` | 创建 | Retrofit 接口定义 (curated, search) |
| `app/src/main/java/com/example/myapplication/network/RetrofitClient.kt` | 创建 | OkHttpClient 配置（Cache, 超时, Header），Retrofit 实例创建 |

### 2.6 Repository 层
| 文件 | 操作 | 内容 |
|------|------|------|
| `app/src/main/java/com/example/myapplication/repository/PhotoRepository.kt` | 创建 | 图片数据仓库，封装网络请求，处理结果 |

### 2.7 ViewModel 层
| 文件 | 操作 | 内容 |
|------|------|------|
| `app/src/main/java/com/example/myapplication/viewmodel/PhotoViewModel.kt` | 创建 | 首页 ViewModel，管理分页状态，搜索状态，图片列表数据 |
| `app/src/main/java/com/example/myapplication/viewmodel/PhotoViewModelFactory.kt` | 创建 | ViewModelFactory，注入 Repository |

### 2.8 Adapter 层
| 文件 | 操作 | 内容 |
|------|------|------|
| `app/src/main/java/com/example/myapplication/adapter/PhotoAdapter.kt` | 创建 | RecyclerView Adapter，瀑布流 Item 绑定，点击事件，DiffUtil，加载更多 Footer |

### 2.9 Activity/UI 层
| 文件 | 操作 | 内容 |
|------|------|------|
| `app/src/main/java/com/example/myapplication/ui/home/HomeActivity.kt` | 修改 | 原 MainActivity 重命名/改造为首页，实现搜索、下拉刷新、上拉分页、点击跳转 |
| `app/src/main/java/com/example/myapplication/ui/detail/DetailActivity.kt` | 创建 | 详情页，展示大图和图片信息 |

### 2.10 XML 布局文件
| 文件 | 操作 | 内容 |
|------|------|------|
| `app/src/main/res/layout/activity_home.xml` | 修改 | 首页布局：搜索框 + SwipeRefreshLayout + RecyclerView |
| `app/src/main/res/layout/activity_detail.xml` | 创建 | 详情页布局 |
| `app/src/main/res/layout/item_photo.xml` | 创建 | 瀑布流图片 Item 布局 (Material CardView) |
| `app/src/main/res/layout/item_loading.xml` | 创建 | 加载更多 Footer 布局 |
| `app/src/main/res/layout/empty_state.xml` | 创建 | 空状态布局 |
| `app/src/main/res/values/strings.xml` | 修改 | 添加字符串资源 |
| `app/src/main/res/values/colors.xml` | 修改 | 添加颜色资源 |
| `app/src/main/res/values/themes.xml` | 修改 | 优化主题配置 |

### 2.11 工具类
| 文件 | 操作 | 内容 |
|------|------|------|
| `app/src/main/java/com/example/myapplication/utils/GlideAppModule.kt` | 创建 | Glide 配置（内存缓存、磁盘缓存大小） |
| `app/src/main/java/com/example/myapplication/utils/NetworkUtils.kt` | 创建 | 网络状态检查工具类 |
| `app/src/main/java/com/example/myapplication/utils/SpacingItemDecoration.kt` | 创建 | RecyclerView 间距装饰类 |
| `app/src/main/java/com/example/myapplication/utils/EndlessScrollListener.kt` | 创建 | 滚动监听，实现上拉加载更多 |

## 三、修改步骤

### 阶段 1: Gradle 配置
1. 更新 `libs.versions.toml`，添加所有新依赖
2. 更新 `app/build.gradle.kts`，应用新配置

### 阶段 2: 基础配置
3. 添加网络权限到 `AndroidManifest.xml`
4. 创建常量配置类
5. 创建数据模型类

### 阶段 3: 网络层
6. 创建 OkHttp 配置（Cache, 超时, Header 拦截器）
7. 创建 Retrofit 接口和客户端

### 阶段 4: 数据层
8. 创建 Repository 层
9. 创建 ViewModel 层

### 阶段 5: 工具类
10. 创建 Glide 配置模块
11. 创建网络检查、间距装饰、滚动监听等工具类

### 阶段 6: Adapter 层
12. 创建 PhotoAdapter（含 DiffUtil, 加载状态, 点击事件）

### 阶段 7: UI 层 - 首页
13. 创建首页 XML 布局
14. 实现 HomeActivity（搜索逻辑, 下拉刷新, 分页加载）

### 阶段 8: UI 层 - 详情页
15. 创建详情页 XML 布局
16. 实现 DetailActivity（大图展示, 图片信息展示）

### 阶段 9: 缓存机制验证
17. 验证三级缓存：OkHttp Cache + Glide Disk Cache + Glide Memory Cache

## 四、潜在依赖项和注意事项

### 4.1 必须的依赖库
- **Retrofit2**: 网络请求
- **OkHttp3**: HTTP 客户端（含 Cache）
- **Gson/Converter**: JSON 解析
- **Glide**: 图片加载（含 Disk Cache, Memory Cache）
- **Kotlin Coroutines**: 异步处理
- **AndroidX Lifecycle/ViewModel**: MVVM 架构
- **AndroidX SwipeRefreshLayout**: 下拉刷新
- **Material Components**: CardView 等 UI 组件

### 4.2 Pexels API Key
需要用户在 `ApiConstants.kt` 中填入自己的 Pexels API Key
可从 https://www.pexels.com/api/ 免费获取

### 4.3 缓存说明
**三级缓存机制避免重复下载：**

1. **Glide Memory Cache** (第一级):
   - 内存缓存，最快
   - 已解码的 Bitmap 复用
   - 相同 URL 不重复解码

2. **Glide Disk Cache** (第二级):
   - 磁盘缓存
   - RESOURCE 缓存转换后的图片
   - DATA 缓存原始下载流
   - 相同 URL 不重复下载

3. **OkHttp Cache** (第三级):
   - HTTP 层缓存
   - 支持 Cache-Control
   - 原始 Response 缓存
   - 作为 Glide 的后备缓存

## 五、风险处理

| 风险 | 影响 | 处理方案 |
|------|------|----------|
| Pexels API Key 缺失 | 网络请求失败 | 在代码中明确注释提示位置，Toast 提示用户配置 |
| minSdk < 24 | 编译失败 | 项目当前 minSdk=24 已符合要求 |
| 依赖版本冲突 | 编译失败 | 在 libs.versions.toml 统一管理版本 |
| 图片 OOM | App 崩溃 | Glide 自动处理，合理配置缓存大小 |
| 分页重复加载 | 数据重复 | 在 ViewModel 添加 isLoading 状态锁防止并发 |
| 瀑布流 Item 跳动 | 体验差 | 给 ImageView 设置 aspectRatio，固定宽高比 |
