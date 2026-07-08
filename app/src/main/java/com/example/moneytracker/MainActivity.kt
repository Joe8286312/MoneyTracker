// 路径: app/src/main/java/com/example/moneytracker/MainActivity.kt
package com.example.moneytracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.moneytracker.ui.home.HomeScreen
import com.example.moneytracker.ui.home.HomeViewModel
import com.example.moneytracker.ui.home.HomeViewModelFactory
import com.example.moneytracker.ui.theme.MoneyTrackerTheme

class MainActivity : ComponentActivity() {

    // 💡 Java类比：使用 Activity 依赖扩展提供的 viewModels 委托属性。
    // 传入自定义的工厂，这就等同于通过 Spring 容器拿到带参构造函数的依赖 Bean。
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((application as MoneyTrackerApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 采用项目默认生成的 Material 3 主题包裹 UI
            MoneyTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 调用刚才写好的首页 Compose 组件
                    HomeScreen(
                        viewModel = homeViewModel,
                        onAddTransactionClick = {
                            // 暂时用 Toast 轻量弹窗做阻断，确保第三阶段编译通过，第四阶段我们将在这里做跳转
                            Toast.makeText(this@MainActivity, "点击了添加账单，第四阶段将解锁手动记账与键盘页面", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}