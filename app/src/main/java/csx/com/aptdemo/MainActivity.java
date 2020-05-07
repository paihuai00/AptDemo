package csx.com.aptdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import csx.com.annotation_lib.AptTestAnnotation;

/**
 * Apt方法 {@link MainActivity#printTest(String)}
 *
 * ButterKnife
 * https://github.com/JakeWharton/butterknife/blob/master/butterknife-compiler/src/main/java/butterknife/compiler/ButterKnifeProcessor.java
 *
 * apt目录：compiler_lib/MyProcessor
 *
 * 注意：
 * 1，@AutoService(Processor.class)， 后面是 带“or”的类
 * 2，不能使用中文，（需要解决）
 * 3，生成的文件目录为：AptDemo/app/build/generated/source/apt/debug/csx.com.aptdemo
 */
public class MainActivity extends AppCompatActivity {

  @BindView(R.id.tv_content)
  TextView mTvContent;
  @BindView(R.id.btn_test)
  Button mBtnTest;
  private Unbinder unbinder;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    unbinder = ButterKnife.bind(this);

  }

  /**
   * 方法注解： apt 生成的文件在：AptDemo/app/build/generated/source/apt/debug/csx.com.aptdemo
   * 目录下
   */
  @AptTestAnnotation(getName = "测试Apt")
  public static void printTest(String name) {

  }


  @Override
  protected void onDestroy() {
    super.onDestroy();

    unbinder.unbind();
  }

  @OnClick({R.id.tv_content, R.id.btn_test})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.tv_content:
        break;
      case R.id.btn_test:
        break;
    }
  }
}
